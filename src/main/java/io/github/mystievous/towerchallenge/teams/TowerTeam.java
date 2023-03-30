package io.github.mystievous.towerchallenge.teams;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mystigui.GuiHeldItem;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.quests.Quest;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.spawncompass.SpawnCompass;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.sound.SoundStop;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class TowerTeam implements Audience, Listener {

    // Server's scoreboard
    public static final Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
    public static final String SHULKER_NAME = "Starting Shulker Box";

    private final int databaseId;
    private final Team team;
    protected final TeamManager teamManager;
    private final TowerChallenge plugin;
    private final Color color;
    private final String dye;

    private Map<String, Quest> quests;
    private String currentQuest;
    private boolean inDialogue;
    private boolean stopDialogue;

    public TowerTeam(TowerChallenge plugin, TeamManager teamManager, int databaseId, String displayName, Color color, String dye) {
        this.teamManager = teamManager;
        this.databaseId = databaseId;
        this.plugin = plugin;
        String name = displayName.replaceAll("\\s", "");
        this.color = color;
        this.dye = dye.toUpperCase();
        Team team = scoreboard.getTeam(name);
        if (team != null) {
            this.team = team;
        } else {
            this.team = scoreboard.registerNewTeam(name);
            this.team.displayName(Component.text(displayName));
        }
        this.team.prefix(Component.text("[").append(Component.text(displayName, color.toTextColor())).append(Component.text("] ")));
        this.quests = new HashMap<>();
        this.inDialogue = false;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public abstract void unregisterEvents();

    public boolean hasQuests() {
        return !quests.isEmpty();
    }

    public @Nullable Quest getQuest(String tag) {
        return quests.get(tag);
    }

    public void setQuests(Map<String, Quest> quests) {
        this.quests = quests;
    }

    public void setCurrentQuestId(String currentQuest) {
        this.currentQuest = currentQuest;
    }

    public String getCurrentQuestId() {
        return currentQuest;
    }

    public void addObjectiveScore(String tag, String name, int value) {
        try {
            teamManager.getDatabase().addObjectiveScore(this, tag, name, value);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Error updating database: " + e.getMessage());
        }
    }

    public int getObjective(String tag, String name) {
        try {
            return teamManager.getDatabase().getObjective(this, tag, name);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Error reading database: " + e.getMessage());
        }
        return 0;
    }

    public void setInDialogue(boolean inDialogue) {
        this.inDialogue = inDialogue;
    }

    public boolean isInDialogue() {
        return inDialogue;
    }

    public void setStopDialogue(boolean shouldStop) {
        stopDialogue = shouldStop;
    }
    public boolean shouldStopDialogue() {
        return stopDialogue;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public Team getTeam() {
        return team;
    }

    public String getTextName() {
        return PlainTextComponentSerializer.plainText().serialize(team.displayName());
    }

    public String getServerTeamName() {
        return team.getName();
    }

    public Color getColor() {
        return color;
    }

    public abstract ItemStack getItem();

    public String getDye() {
        return dye;
    }

    private Audience getAudience() {
        return Audience.audience(getOnlinePlayers());
    }

    public Collection<OfflinePlayer> getPlayers() {
        return team.getEntries().stream().map(Bukkit::getOfflinePlayer).toList();
    }

    public Collection<Player> getOnlinePlayers() {
        return team.getEntries().stream().map(Bukkit::getPlayer).filter(Objects::nonNull).filter(Player::isOnline).toList();
    }

    public Component getDisplayName() {
        return team.displayName().color(getColor().toTextColor());
    }

    public TowerChallenge getPlugin() {
        return plugin;
    }

    public void addTeamPlayer(OfflinePlayer player) {
        try {
            getTeam().addPlayer(player);
        } catch (IllegalArgumentException e) {
            getPlugin().getLogger().warning(player.getUniqueId() + "; Player has not joined the server, unable to add to team.");
        }
    }

    public void addAllPlayers(@NotNull List<OfflinePlayer> players) {
        for (OfflinePlayer player : players) {
            addTeamPlayer(player);
        }
    }

    public void clearPlayers() {
        for (String name : team.getEntries()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            if (player.hasPlayedBefore()) {
                team.removePlayer(player);
            }
        }
    }

    public ItemStack getShulker() {
        ItemStack shulker = new ItemStack(Material.valueOf(getDye().toUpperCase()+"_SHULKER_BOX"));
        ItemMeta shulkerMeta = shulker.getItemMeta();
        shulkerMeta.displayName(Component.text(SHULKER_NAME).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.AQUA));
        shulker.setItemMeta(shulkerMeta);
        return shulker;
    }

    public void giveShulker(Player player, int number) {
        for (int i = 0; i < number; i++) {
            player.getInventory().addItem(getShulker());
        }
    }

    public Map<EquipmentSlot, ItemStack> getStartingEquipment() {

        Map<EquipmentSlot, ItemStack> equipment = new HashMap<>();

        ItemStack hat = new ItemStack(Material.DIAMOND_HELMET);
        ItemMeta hatMeta = hat.getItemMeta();
        hatMeta.setUnbreakable(true);
        hat.setItemMeta(hatMeta);
        equipment.put(EquipmentSlot.HEAD, hat);

        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta chestplateMeta = chestplate.getItemMeta();
        chestplateMeta.setUnbreakable(true);
        chestplate.setItemMeta(chestplateMeta);
        equipment.put(EquipmentSlot.CHEST, chestplate);

        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemMeta leggingsMeta = leggings.getItemMeta();
        leggingsMeta.setUnbreakable(true);
        leggings.setItemMeta(leggingsMeta);
        equipment.put(EquipmentSlot.LEGS, leggings);

        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        ItemMeta bootsMeta = boots.getItemMeta();
        bootsMeta.setUnbreakable(true);
        boots.setItemMeta(bootsMeta);
        equipment.put(EquipmentSlot.FEET, boots);

        return equipment;
    }

    public Map<Integer, ItemStack> getStartingItems() {
        Map<Integer, ItemStack> items = new HashMap<>();

        ItemStack axe = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        axeMeta.setUnbreakable(true);
        axeMeta.addEnchant(Enchantment.DIG_SPEED, 3, false);
        axe.setItemMeta(axeMeta);
        items.put(0, axe);

        ItemStack pickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta pickaxeMeta = pickaxe.getItemMeta();
        pickaxeMeta.setUnbreakable(true);
        pickaxeMeta.addEnchant(Enchantment.DIG_SPEED, 3, false);
        pickaxeMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
        pickaxe.setItemMeta(pickaxeMeta);
        items.put(1, pickaxe);

        ItemStack shovel = new ItemStack(Material.NETHERITE_SHOVEL);
        ItemMeta shovelMeta = shovel.getItemMeta();
        shovelMeta.setUnbreakable(true);
        shovelMeta.addEnchant(Enchantment.DIG_SPEED, 3, false);
        shovel.setItemMeta(shovelMeta);
        items.put(2, shovel);

        ItemStack book = new ItemStack(Material.BOOK);
        NBTUtils.noStack(plugin, book);
        NBTUtils.setString(plugin, GuiHeldItem.GUI_ID, book, QuestManager.GUI_ID);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.displayName(Component.text("Quest Book").decoration(TextDecoration.ITALIC, false));
        bookMeta.setCustomModelData(2);
        bookMeta.lore(TextUtil.formatTexts(
                Component.text("Use ").append(Component.keybind("key.use")).append(Component.text(" with me in your hand")),
                Component.text("to open the quest menu!")
        ));
        book.setItemMeta(bookMeta);

        items.put(3, book);

        ItemStack steak = new ItemStack(Material.COOKED_BEEF, 64);
        items.put(4, steak);
        items.put(31, steak);

        ItemStack torches = new ItemStack(Material.TORCH, 64);
        items.put(5, torches);
        items.put(6, torches);

        items.put(7, SpawnCompass.getCompass());

        items.put(8, getShulker());
        items.put(26, getShulker());
        items.put(35, getShulker());

        return items;

    }

    public void dealItemsAllPlayers() {
        for (OfflinePlayer offlinePlayer : getPlayers()) {
            if (offlinePlayer instanceof Player player) {
                dealItems(player);
            }
        }
    }

    public void dealItems(Player player) {
        PlayerInventory inventory = player.getInventory();
        Map<EquipmentSlot, ItemStack> equipment = getStartingEquipment();
        Map<Integer, ItemStack> items = getStartingItems();
        ItemStack hat = inventory.getHelmet();
        if (hat == null || hat.getType().isAir()) {
            hat = equipment.get(EquipmentSlot.HEAD);
        }
        inventory.clear();

        inventory.setHelmet(hat);
        inventory.setChestplate(equipment.get(EquipmentSlot.CHEST));
        inventory.setLeggings(equipment.get(EquipmentSlot.LEGS));
        inventory.setBoots(equipment.get(EquipmentSlot.FEET));

        for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {

            int index = entry.getKey();
            ItemStack item = entry.getValue();

            player.getInventory().setItem(index, item);

        }

    }

    @Override
    public @NotNull Audience filterAudience(@NotNull Predicate<? super Audience> filter) {
        return getAudience().filterAudience(filter);
    }

    @Override
    public void forEachAudience(@NotNull Consumer<? super Audience> action) {
        getAudience().forEachAudience(action);
    }

    @Override
    public void sendMessage(@NotNull ComponentLike message) {
        getAudience().sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull Identified source, @NotNull ComponentLike message) {
        getAudience().sendMessage(source, message);
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull ComponentLike message) {
        getAudience().sendMessage(source, message);
    }

    @Override
    public void sendMessage(@NotNull Component message) {
        getAudience().sendMessage(message);
    }

    @Override
    public void sendMessage(@NotNull Identified source, @NotNull Component message) {
        getAudience().sendMessage(source, message);
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message) {
        getAudience().sendMessage(source, message);
    }

    @Override
    public void sendMessage(@NotNull ComponentLike message, @NotNull MessageType type) {
        getAudience().sendMessage(message, type);
    }

    @Override
    public void sendMessage(@NotNull Identified source, @NotNull ComponentLike message, @NotNull MessageType type) {
        getAudience().sendMessage(source, message, type);
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull ComponentLike message, @NotNull MessageType type) {
        getAudience().sendMessage(source, message, type);
    }

    @Override
    public void sendMessage(@NotNull Component message, @NotNull MessageType type) {
        getAudience().sendMessage(message, type);
    }

    @Override
    public void sendMessage(@NotNull Identified source, @NotNull Component message, @NotNull MessageType type) {
        getAudience().sendMessage(source, message, type);
    }

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        getAudience().sendMessage(source, message, type);
    }

    @Override
    public void sendActionBar(@NotNull ComponentLike message) {
        getAudience().sendActionBar(message);
    }

    @Override
    public void sendActionBar(@NotNull Component message) {
        getAudience().sendActionBar(message);
    }

    @Override
    public void sendPlayerListHeader(@NotNull ComponentLike header) {
        getAudience().sendPlayerListHeader(header);
    }

    @Override
    public void sendPlayerListHeader(@NotNull Component header) {
        getAudience().sendPlayerListHeader(header);
    }

    @Override
    public void sendPlayerListFooter(@NotNull ComponentLike footer) {
        getAudience().sendPlayerListFooter(footer);
    }

    @Override
    public void sendPlayerListFooter(@NotNull Component footer) {
        getAudience().sendPlayerListFooter(footer);
    }

    @Override
    public void sendPlayerListHeaderAndFooter(@NotNull ComponentLike header, @NotNull ComponentLike footer) {
        getAudience().sendPlayerListHeaderAndFooter(header, footer);
    }

    @Override
    public void sendPlayerListHeaderAndFooter(@NotNull Component header, @NotNull Component footer) {
        getAudience().sendPlayerListHeaderAndFooter(header, footer);
    }

    @Override
    public void showTitle(@NotNull Title title) {
        getAudience().showTitle(title);
    }

    @Override
    public <T> void sendTitlePart(@NotNull TitlePart<T> part, @NotNull T value) {
        getAudience().sendTitlePart(part, value);
    }

    @Override
    public void clearTitle() {
        getAudience().clearTitle();
    }

    @Override
    public void resetTitle() {
        getAudience().resetTitle();
    }

    @Override
    public void showBossBar(@NotNull BossBar bar) {
        getAudience().showBossBar(bar);
    }

    @Override
    public void hideBossBar(@NotNull BossBar bar) {
        getAudience().hideBossBar(bar);
    }

    @Override
    public void playSound(@NotNull Sound sound) {
        getAudience().playSound(sound);
    }

    @Override
    public void playSound(@NotNull Sound sound, double x, double y, double z) {
        getAudience().playSound(sound, x, y, z);
    }

    @Override
    public void playSound(@NotNull Sound sound, Sound.@NotNull Emitter emitter) {
        getAudience().playSound(sound, emitter);
    }

    @Override
    public void stopSound(@NotNull Sound sound) {
        getAudience().stopSound(sound);
    }

    @Override
    public void stopSound(@NotNull SoundStop stop) {
        getAudience().stopSound(stop);
    }

    @Override
    public void openBook(Book.@NotNull Builder book) {
        getAudience().openBook(book);
    }

    @Override
    public void openBook(@NotNull Book book) {
        getAudience().openBook(book);
    }
}
