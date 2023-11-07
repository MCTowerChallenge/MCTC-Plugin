package io.github.mystievous.towerchallenge.team;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mystigui.GuiHeldItem;
import io.github.mystievous.mystigui.element.Representable;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.portal.EndPortal;
import io.github.mystievous.towerchallenge.quest.Quest;
import io.github.mystievous.towerchallenge.quest.QuestChangeEvent;
import io.github.mystievous.towerchallenge.quest.QuestCompleteEvent;
import io.github.mystievous.towerchallenge.quest.QuestManager;
import io.github.mystievous.towerchallenge.spawncompass.SpawnCompass;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.bossbar.BossBar;
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
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

/**
 * A team for the tower challenge event.
 */
public abstract class TowerTeam implements Audience, Listener, Representable {

    /**
     * Main scoreboard, used to create teams
     * if they are not already.
     */
    public static final Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

    /**
     * Name of the shulker boxes given
     * to teams by Gods.
     */
    public static final String SHULKER_NAME = "Starting Shulker Box";

    private final int databaseId;
    private final Team team;
    protected final TeamManager teamManager;
    private final TowerChallenge plugin;
    private final Color color;
    private final String dye;

    private final Map<String, Quest> quests;
    private String currentQuest;
    /**
     * Whether the team is currently
     * listening to dialogue.
     */
    private boolean inDialogue;
    /**
     * If true, the next dialogue
     * playing for this team
     * will not trigger.
     */
    private boolean stopDialogue;

    /**
     * When initialized, creates a server team
     * with the given values if one does not exist.
     *
     * @param plugin      The current plugin instance.
     * @param teamManager The current team manager instance.
     * @param databaseId  The ID of this team in the database.
     * @param displayName The name to display in game for this team.
     * @param color       The color to make this team.
     * @param dye         The name of the in-game dye to use for this team. i.e. {@code MAGENTA}
     */
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

    /**
     * Unregister events related to the team.
     */
    public void unregisterEvents() {
        HandlerList.unregisterAll(this);
    }

    /**
     * Sets the quest instances for this team based on the given quests.
     *
     * @param quests The quests to copy to this team.
     */
    public void setQuests(Map<String, Quest> quests, Collection<String> completedQuests) {
        for (Map.Entry<String, Quest> questEntry : quests.entrySet()) {
            Quest quest = questEntry.getValue().copy();
            if (completedQuests != null && completedQuests.contains(questEntry.getKey())) {
                quest.setCompleted(true);
            }
            this.quests.put(questEntry.getKey(), quest);
        }
    }

    /**
     * Sets the current quest for this team.
     * <p></p>
     * Team members will automatically be
     * shown the new quest when they reopen
     * the quest book.
     *
     * @param currentQuest The new quest to set.
     */
    public void setCurrentQuestTag(String currentQuest) {
        this.currentQuest = currentQuest;
    }

    public Map<String, Quest> getQuests() {
        return quests;
    }

    public void completeQuest(String questTag) {
        Quest quest = getQuest(questTag);
        if (quest == null || quest.isCompleted()) {
            return;
        }
        QuestCompleteEvent event = new QuestCompleteEvent(this, quest);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;
        quest.setCompleted(true);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                teamManager.getDatabase().setCompletedQuest(this, quest);
            } catch (SQLException e) {
                Bukkit.getLogger().warning("Error setting database: " + e.getMessage());
            }
        });
    }

    /**
     * Sets a team's quest to the one with the given ID.
     *
     * @param questId The ID of the quest to change to.
     */
    public void setQuest(String questId) {
        QuestChangeEvent event = new QuestChangeEvent(this, getQuest(getCurrentQuestTag()), getQuest(questId));
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled())
            return;

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                teamManager.getDatabase().setTeamQuest(this, questId);
            } catch (SQLException e) {
                Bukkit.getLogger().warning("Error setting database: " + e.getMessage());
            }
        });
    }

    /**
     * Gets the tag of the current quest for this team.
     *
     * @return The quest tag.
     */
    public String getCurrentQuestTag() {
        if (currentQuest == null) {
            return QuestManager.NO_QUEST;
        }
        return currentQuest;
    }

    /**
     * Gets the current quest instance for this team.
     *
     * @return The current quest instance or null if none.
     */
    public @Nullable Quest getCurrentQuest() {
        return getQuest(getCurrentQuestTag());
    }

    /**
     * Gets a specific quest instance for this team.
     *
     * @param tag The quest tag to retrieve.
     * @return The instance of the Quest or null if not found.
     */
    public @Nullable Quest getQuest(String tag) {
        return quests.get(tag);
    }

    /**
     * Adds to a quest objective score for this team.
     *
     * @param tag   The quest tag.
     * @param name  The name of the objective.
     * @param value The value to add.
     */
    public void addObjectiveScore(String tag, String name, int value) {
        try {
            teamManager.getDatabase().addObjectiveScore(this, tag, name, value);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Error updating database: " + e.getMessage());
        }
    }

    /**
     * Gets the value of a quest objective for this team.
     *
     * @param tag  The quest tag.
     * @param name The name of the objective.
     * @return The value of the quest objective.
     */
    public int getObjective(String tag, String name) {
        try {
            return teamManager.getDatabase().getObjective(this, tag, name);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Error reading database: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Sets the team to be currently in dialogue or not.
     *
     * @param inDialogue The value to set.
     */
    public void setInDialogue(boolean inDialogue) {
        this.inDialogue = inDialogue;
    }

    /**
     * Checks if the team can start a new dialogue.
     *
     * @return True if the team is not already in a dialogue.
     */
    public boolean canStartDialogue() {
        return !inDialogue;
    }

    /**
     * Sets whether the team's current dialogue should not continue.
     *
     * @param shouldStop Whether the dialogue should stop.
     */
    public void setStopDialogue(boolean shouldStop) {
        stopDialogue = shouldStop;
    }

    /**
     * Checks if the team's next dialogue should not trigger.
     *
     * @return True if the next dialogue should not trigger.
     */
    public boolean shouldStopDialogue() {
        return stopDialogue;
    }

    /**
     * Gets the ID of this team in the database.
     *
     * @return The database ID of the team.
     */
    public int getDatabaseId() {
        return databaseId;
    }

    /**
     * Gets the Bukkit Team associated with this TowerTeam.
     *
     * @return The Bukkit Team instance.
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Serializes the team's display name to a plain string.
     *
     * @return The display name as a plain string.
     */
    public String getTextName() {
        return PlainTextComponentSerializer.plainText().serialize(team.displayName());
    }

    /**
     * Gets the server/technical name for this team.
     * <p></p>
     * i.e. if the team is {@code Light Blue}, this returns {@code LightBlue}.
     *
     * @return The server/technical name of the team.
     */
    public String getServerTeamName() {
        return team.getName();
    }

    /**
     * Gets the color associated with this team.
     *
     * @return The team's color.
     */
    public Color getColor() {
        return color;
    }

    @Override
    public abstract ItemStack getRepresentation();

    /**
     * Gets the dye color name for this team.
     * <p></p>
     * i.e. {@code MAGENTA}
     *
     * @return The dye color name.
     */
    public String getDye() {
        return dye;
    }

    public boolean hasPlayer(Player player) {
        return team.hasPlayer(player);
    }

    private Audience getAudience() {
        return Audience.audience(getOnlinePlayers());
    }

    /**
     * Gets all players on this team, whether online or not.
     *
     * @return The list of offline players.
     */
    public Collection<OfflinePlayer> getOfflinePlayers() {
        Collection<OfflinePlayer> offlinePlayers = new ArrayList<>();
        for (String entry : team.getEntries()) {
            OfflinePlayer offlinePlayer;
            try {
                offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(entry));
            } catch (IllegalArgumentException e) {
                offlinePlayer = Bukkit.getOfflinePlayer(entry);
            }
            offlinePlayers.add(offlinePlayer);
        }
        return offlinePlayers;
    }

    /**
     * Gets online players on this team.
     *
     * @return The list of online players.
     */
    public Collection<Player> getOnlinePlayers() {
        Collection<Player> players = new ArrayList<>();
        for (String entry : team.getEntries()) {
            Player player;
            try {
                player = Bukkit.getPlayer(UUID.fromString(entry));
            } catch (IllegalArgumentException e) {
                player = Bukkit.getPlayer(entry);
            }
            if (player != null && player.isOnline()) {
                players.add(player);
            }
        }
        return players;
    }

    /**
     * Gets the display name of the team with the proper color applied.
     *
     * @return The display name with color.
     */
    public Component getDisplayName() {
        return team.displayName().color(getColor().toTextColor());
    }

    /**
     * Gets the TowerChallenge plugin instance associated with this team.
     *
     * @return The plugin instance.
     */
    public TowerChallenge getPlugin() {
        return plugin;
    }

    /**
     * Adds a player to this team.
     * <p></p>
     * This method will not add the player to the database.
     * To properly add a player, the {@link TeamManager} should be used.
     *
     * @param player The player to add.
     * @see TeamManager#setPlayerTeam(OfflinePlayer, TowerTeam)
     */
    public void addTeamPlayer(OfflinePlayer player) {
        try {
            getTeam().addPlayer(player);
        } catch (IllegalArgumentException e) {
            getPlugin().getLogger().warning(player.getUniqueId() + "; Player has not joined the server, unable to add to team.");
        }
    }

    /**
     * Adds a list of players to this team.
     * <p></p>
     * This method will not add the players to the database.
     * To properly add players, the {@link TeamManager} should be used.
     *
     * @param players The players to add.
     * @see TeamManager#setPlayerTeam(OfflinePlayer, TowerTeam)
     */
    public void addAllPlayers(@NotNull List<OfflinePlayer> players) {
        for (OfflinePlayer player : players) {
            addTeamPlayer(player);
        }
    }

    /**
     * Clears all players from this team.
     */
    public void clearPlayers() {
        for (String name : team.getEntries()) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(name);
            if (player.hasPlayedBefore()) {
                team.removePlayer(player);
            }
        }
    }

    /**
     * Gets the starting shulker box for this team.
     *
     * @return The starting shulker box.
     */
    public ItemStack getShulker() {
        ItemStack shulker = new ItemStack(Material.valueOf(getDye().toUpperCase() + "_SHULKER_BOX"));
        ItemMeta shulkerMeta = shulker.getItemMeta();
        shulkerMeta.displayName(Component.text(SHULKER_NAME).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.AQUA));
        shulker.setItemMeta(shulkerMeta);
        return shulker;
    }

    /**
     * Gives a player the given number of shulker boxes from this team.
     *
     * @param player The player to give the shulker boxes to.
     * @param number The number of shulker boxes to give.
     */
    public void giveShulker(Player player, int number) {
        for (int i = 0; i < number; i++) {
            player.getInventory().addItem(getShulker());
        }
    }

    /**
     * Gets the starting armor for this team.
     * <p></p>
     * To give items, {@link #dealItems(Player)} should be used.
     *
     * @return A map containing the starting armor with their slots.
     * @see #dealItems(Player)
     * @see #dealItemsAllPlayers()
     */
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

    /**
     * Gets the starting items for this team.
     * <p></p>
     * To give items, {@link #dealItems(Player)} should be used.
     *
     * @return A map containing the starting items with their slots.
     * @see #dealItems(Player)
     * @see #dealItemsAllPlayers()
     */
    public Map<Integer, ItemStack> getStartingItems(@Nullable Player player) {
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

        ItemStack book = teamManager.getQuestBook().getItem();
        items.put(3, book);

        ItemStack steak = new ItemStack(Material.COOKED_BEEF, 64);
        items.put(4, steak);
        items.put(31, steak);

        ItemStack torches = new ItemStack(Material.TORCH, 64);
        items.put(5, torches);
        items.put(32, torches);

        ItemStack bow = new ItemStack(Material.BOW);
        items.put(6, bow);
        ItemStack arrows = new ItemStack(Material.ARROW, 64);
        items.put(33, arrows);

        items.put(7, SpawnCompass.getCompass(this, player));

        items.put(8, getShulker());
        items.put(26, getShulker());
        items.put(35, getShulker());

        return items;

    }

    /**
     * Deals starting items to all players on this team.
     *
     * @see #dealItems(Player)
     */
    public void dealItemsAllPlayers() {
        for (OfflinePlayer offlinePlayer : getOfflinePlayers()) {
            if (offlinePlayer instanceof Player player) {
                dealItems(player);
            }
        }
    }

    /**
     * Deals starting items for this team to the given player.
     *
     * @param player The player to deal items to.
     */
    public void dealItems(Player player) {
        PlayerInventory inventory = player.getInventory();
        Map<EquipmentSlot, ItemStack> equipment = getStartingEquipment();
        Map<Integer, ItemStack> items = getStartingItems(player);
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

    public void teleportToSpawn(Player player) {
        Location spawnpoint = EndPortal.spawnLocation();
        player.teleport(spawnpoint);
    }

    public void teleportAllSpawn() {
        for (Player player : getOnlinePlayers()) {
            teleportToSpawn(player);
        }
    }

    /*
        Inherited Audience methods
     */

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
    public void sendMessage(@NotNull Component message) {
        getAudience().sendMessage(message);
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
