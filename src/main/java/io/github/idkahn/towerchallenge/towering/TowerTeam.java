package io.github.idkahn.towerchallenge.towering;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.halloween.candy.Candy;
import io.github.idkahn.towerchallenge.halloween.candy.CandyUtils;
import io.github.idkahn.towerchallenge.hats.HatGUI;
import io.github.idkahn.towerchallenge.spawncompass.SpawnCompass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class TowerTeam {

    // Server's scoreboard
    public static Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
    public static String BUNDLE_NAME = "Candy Basket";
    public static String SHULKER_NAME = "Starting Shulker Box";
    private static final SecureRandom RANDOM = new SecureRandom();


    private final Team team;
    private final JavaPlugin plugin;
    private final EventManager manager;
    private final String displayName;
    private final String color;
    private final String dye;
    private final HatGUI hatGUI;

    public static YamlConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(TowerChallenge.regionConfigFile);
    }

    public TowerTeam(EventManager manager, String displayName, String color, String dye) {
        this.manager = manager;
        this.plugin = manager.getPlugin();
        this.displayName = displayName;
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
        this.team.prefix(Component.text("[").append(Component.text(displayName, TextColor.fromHexString(color))).append(Component.text("] ")));
        this.hatGUI = new HatGUI(manager, Color.fromRGB(Integer.parseInt(this.color.replaceAll("#", ""), 16)));
    }

    public Team getTeam() {
        return team;
    }

    public String getName() {
        return team.getName();
    }

    public EventManager getManager() {
        return manager;
    }

    public String getColor() {
        return color;
    }

    public TextColor getTextColor() {
        return TextColor.fromHexString(color);
    }
    public Color getBukkitColor() {
        return Color.fromRGB(Integer.parseInt(this.color.replaceAll("#", ""), 16));
    }

    public String getDye() {
        return dye;
    }

    public Audience getAudience() {
        return Audience.audience(getOnlinePlayers());
    }

    public Set<Player> getOnlinePlayers() {
        Set<Player> players = new HashSet<>();
        for (String name : team.getEntries()) {
            Player player = Bukkit.getPlayer(name);
            if (player != null && player.isOnline()) {
                players.add(player);
            }
        }
        return players;
    }

    public Component getDisplayName() {
        return team.displayName();
    }

    public void openHatGUI(Player player) {
        hatGUI.openInventory(player);
    }

    public HatGUI getHatGUI() {
        return hatGUI;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }

    public void destroyTeam() {
        team.unregister();
    }

    public void addPlayer(OfflinePlayer player) {
        try {
            getTeam().addPlayer(player);
        } catch (IllegalArgumentException e) {
            getPlugin().getLogger().warning(player.getUniqueId() + "; Player has not joined the server, unable to add to team.");
        }
    }

    public abstract void addPlayerConfig(OfflinePlayer player);

    public void removePlayer(OfflinePlayer player) {
        getTeam().removePlayer(player);
    }

    public Boolean hasPlayer(OfflinePlayer player) {
        return getTeam().hasPlayer(player);
    }

    public Set<String> getEntries() {
        return getTeam().getEntries();
    }

    public void clear() {
        getTeam().removeEntries(getTeam().getEntries());
    }

    public void giveBundle(Player player, int candies, int customModelID) {
        ItemStack bundle = CandyUtils.setBundle(CandyUtils.setTeam(new ItemStack(Material.BUNDLE), this));
        BundleMeta bundleMeta = (BundleMeta) bundle.getItemMeta();
        bundleMeta.displayName(Component.text(BUNDLE_NAME).decoration(TextDecoration.ITALIC, false));
        bundleMeta.setCustomModelData(customModelID);
        ItemStack[] candyItems = new ItemStack[candies];
        Arrays.fill(candyItems, Candy.randomCandy());
        bundleMeta.setItems(Arrays.stream(candyItems).toList());
        bundle.setItemMeta(bundleMeta);
        player.getInventory().addItem(bundle);
    }

    public void giveBundle(Player player, int candies) {
        giveBundle(player, candies, RANDOM.nextInt(3));
    }

    public void giveBundle(Player player) {
        giveBundle(player, 0, RANDOM.nextInt(3));
    }

    public void giveShulker(Player player, int number) {
        ItemStack shulker = new ItemStack(Material.valueOf(getDye().toUpperCase()+"_SHULKER_BOX"));
        ItemMeta shulkerMeta = shulker.getItemMeta();
        shulkerMeta.displayName(Component.text(SHULKER_NAME).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.AQUA));
        shulker.setItemMeta(shulkerMeta);
        for (int i = 0; i < number; i++) {
            player.getInventory().addItem(shulker);
        }
    }

    public void dealItems(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack hat = inventory.getHelmet();
        if (hat == null || hat.getType().isAir()) {
            hat = new ItemStack(Material.DIAMOND_HELMET);
            ItemMeta hatMeta = hat.getItemMeta();
            hatMeta.setUnbreakable(true);
            hat.setItemMeta(hatMeta);
        }
        inventory.clear();

        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemMeta chestplateMeta = chestplate.getItemMeta();
        chestplateMeta.setUnbreakable(true);
        chestplate.setItemMeta(chestplateMeta);

        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemMeta leggingsMeta = leggings.getItemMeta();
        leggingsMeta.setUnbreakable(true);
        leggings.setItemMeta(leggingsMeta);

        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        ItemMeta bootsMeta = boots.getItemMeta();
        bootsMeta.setUnbreakable(true);
        bootsMeta.addEnchant(Enchantment.DEPTH_STRIDER, 3, false);
        boots.setItemMeta(bootsMeta);

        ItemStack pickaxe = new ItemStack(Material.NETHERITE_PICKAXE);
        if (player.getName().equals("ScaredArti")) {
            pickaxe.lore(new ArrayList<>() {{
                add(Component.text("Look what you made me do...").color(TowerChallenge.SECONDARY_COLOR));
            }});
        }

        ItemMeta pickaxeMeta = pickaxe.getItemMeta();
        pickaxeMeta.setUnbreakable(true);

        pickaxeMeta.addEnchant(Enchantment.DIG_SPEED, 3, false);
        pickaxeMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
        pickaxe.setItemMeta(pickaxeMeta);

        ItemStack axe = new ItemStack(Material.NETHERITE_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        axeMeta.setUnbreakable(true);
        axeMeta.addEnchant(Enchantment.DIG_SPEED, 3, false);
        axe.setItemMeta(axeMeta);

        ItemStack shovel = new ItemStack(Material.NETHERITE_SHOVEL);
        ItemMeta shovelMeta = shovel.getItemMeta();
        shovelMeta.setUnbreakable(true);
        shovelMeta.addEnchant(Enchantment.DIG_SPEED, 3, false);
        shovel.setItemMeta(shovelMeta);

        ItemStack steak = new ItemStack(Material.COOKED_BEEF, 64);

        ItemStack torches = new ItemStack(Material.TORCH, 64);

        inventory.setHelmet(hat);
        inventory.setChestplate(chestplate);
        inventory.setLeggings(leggings);
        inventory.setBoots(boots);

        inventory.setItem(0, axe);
        inventory.setItem(1, pickaxe);
        inventory.setItem(2, shovel);
        inventory.setItem(3, getManager().getQuestManager().getBook());
        giveBundle(player);
        inventory.setItem(5, steak);
        inventory.setItem(6, torches);
        SpawnCompass.giveCompass(player);
        giveShulker(player, 3);

        inventory.setItem(32, steak);
        inventory.setItem(33, torches);

    }

}
