package io.github.idkahn.towerchallenge.towering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.halloween.candy.Candy;
import io.github.idkahn.towerchallenge.halloween.candy.CandyUtils;
import io.github.idkahn.towerchallenge.hats.HatGUI;
import io.github.idkahn.towerchallenge.spawncompass.SpawnCompass;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.EndPortalFrame;
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

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;

public class ParticipantTeam {

    public static final RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

    // Server's scoreboard
    public static Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
    public static String SHULKER_NAME = "Starting Shulker Box";
    public static String BUNDLE_NAME = "Candy Basket";

    private static final SecureRandom RANDOM = new SecureRandom();

    private final Team team;
    private final JavaPlugin plugin;
    private final EventManager manager;
    private final String displayName;
    private final String color;
    private final String dye;
    private final HatGUI hatGUI;

    private int extraScore;
    private SpawnArea spawnArea;
    private TowerArea towerArea;

    private Location frameLocation;

    public ParticipantTeam(EventManager manager, String displayName, String color, String dye) {
        this.manager = manager;
        this.plugin = manager.getPlugin();
        this.displayName = displayName;
        String name = displayName.replaceAll("\\s", "");
        this.color = color;
        this.dye = dye.toUpperCase();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamScoreConfigFile);
        extraScore = config.getInt(displayName);
        Team team = scoreboard.getTeam(name);
        if (team != null) {
            this.team = team;
        } else {
            this.team = scoreboard.registerNewTeam(name);
            this.team.displayName(Component.text(displayName));
        }
        this.team.prefix(Component.text("[").append(Component.text(displayName, TextColor.fromHexString(color))).append(Component.text("] ")));
        this.hatGUI = new HatGUI(manager, Color.fromRGB(Integer.parseInt(this.color.replaceAll("#", ""), 16)));
        this.loadRegions();
        this.loadPortal();
    }

    public void loadRegions() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.regionConfigFile);
        List maps = config.getMapList(displayName);
        List<Map> regions = (List<Map>) maps;

        if (regions.size() >= 2) {
            HashMap<String, String> spawn = (HashMap<String, String>) regions.get(0);
            HashMap<String, String> tower = (HashMap<String, String>) regions.get(1);
            this.spawnArea = new SpawnArea(manager, container.get(BukkitAdapter.adapt(TowerChallenge.WORLD)).getRegion(spawn.get("name")));
            this.towerArea = new TowerArea(this, manager, container.get(BukkitAdapter.adapt(TowerChallenge.WORLD)).getRegion(tower.get("name")), displayName);
        }
    }

    public void loadPortal() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.endPortalConfigFile);
        if (config.isString(displayName+".world")) {
            this.frameLocation = new Location(Bukkit.getWorld(config.getString(displayName+".world")), config.getInt(displayName+".x"), config.getInt(displayName+".y"), config.getInt(displayName+".z"));
            Block block = this.frameLocation.getBlock();
            block.setType(Material.END_PORTAL_FRAME);
            EndPortalFrame blockData = (EndPortalFrame) block.getBlockData();
            if (config.isString(displayName+".facing")) {
                blockData.setFacing(BlockFace.valueOf((config.getString(displayName+".facing")).toUpperCase()));
            }
            if (config.isBoolean(displayName+".completed")) {
                blockData.setEye(config.getBoolean(displayName+".completed"));
                block.setBlockData(blockData);
            }
            Bukkit.getLogger().info("Loaded portal frame for " + displayName + " at location " + this.frameLocation.getX() +" "+ this.frameLocation.getY() +" "+ this.frameLocation.getZ());
        }
    }

    public int getScore() {
        int score = manager.getObjective().getScore(PlainTextComponentSerializer.plainText().serialize(getDisplayName())).getScore();
        return score+extraScore;
    }

    public int addExtraScore(int score) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamScoreConfigFile);
        extraScore += score;
        config.set(displayName, extraScore);
        try {
            config.save(TowerChallenge.teamScoreConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return extraScore;
    }

    public int removeExtraScore(int score) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamScoreConfigFile);
        extraScore -= score;
        config.set(displayName, extraScore);
        try {
            config.save(TowerChallenge.teamScoreConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return extraScore;
    }

    public int getExtraScore() {
        return extraScore;
    }

    public void setExtraScore(int extraScore) {
        this.extraScore = extraScore;
    }

    public String getColor() {
        return color;
    }

    public EventManager getManager() {
        return manager;
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

    public void destroyTeam() {
        team.unregister();
    }

    public void setArea(ProtectedRegion teamArea) {
    }

    public void addPlayer(OfflinePlayer player) {
        try {
            team.addPlayer(player);
            if (!displayName.equals("God")) {
                if (spawnArea != null) {
                    spawnArea.addPlayer(player);
                }
                if (towerArea != null) {
                    towerArea.addPlayer(player);
                }
            }
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning(player.getUniqueId() + "; Player has not joined the server, unable to add to team.");
//            return;
        }
    }

    public void addPlayerConfig(OfflinePlayer player) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamConfigFile);
        List<String> players = config.getStringList("Teams."+displayName+".players");
        players.add(player.getUniqueId().toString());
        try {
            config.save(TowerChallenge.teamConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Team getTeam() {
        return team;
    }

    public Location getSpawnpoint() {
        return spawnArea.getSpawnpoint();
    }

    public Location getFrameLocation() {
        return frameLocation;
    }

    public boolean hasEye() {
        return ((EndPortalFrame) frameLocation.getBlock().getBlockData()).hasEye();
    }

    public void placeEye() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.endPortalConfigFile);
        Block frame = frameLocation.getBlock();
        EndPortalFrame frameData = (EndPortalFrame) frame.getBlockData();
        frameData.setEye(true);
        config.set(displayName+".completed", true);
        try {
            config.save(TowerChallenge.endPortalConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frame.setBlockData(frameData);

        int remainingEyes = manager.getTowerListener().getTeams().size()-manager.getCompletedPortalFrames();

//        final Component mainTitle = getDisplayName().color(getTextColor());
////        final Component subtitle = Component.text("There are ", NamedTextColor.DARK_GRAY)
////                .append(Component.text(16-manager.getCompletedPortalFrames(), NamedTextColor.DARK_RED))
////                .append(Component.text(" remaining.", NamedTextColor.DARK_GRAY));
//        final Component subtitle = Component.text("has contributed to the End Portal!").color(NamedTextColor.WHITE);

        final Component chatMessage = getDisplayName().color(getTextColor())
                .append(Component.text(" has contributed to the End Portal! ").color(NamedTextColor.WHITE))
                .append(Component.text(remainingEyes+" remain... ").color(TowerChallenge.PRIMARY_COLOR));

        // Creates a simple title with the default values for fade-in, stay on screen and fade-out durations
//        final Title title = Title.title(mainTitle, subtitle);

        // Send the title to your audience
//        Bukkit.getServer().showTitle(title);
        Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "entity.player.levelup"), Sound.Source.MASTER, 100, 1));
        Bukkit.getServer().sendMessage(chatMessage);

        if (remainingEyes <= 0) {
            manager.getEndPortal().openPortal();
        }

    }

    public void resetFrame() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.endPortalConfigFile);
        Block frame = frameLocation.getBlock();
        EndPortalFrame frameData = (EndPortalFrame) frame.getBlockData();
        frameData.setEye(false);
        config.set(displayName+".completed", false);
        try {
            config.save(TowerChallenge.endPortalConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frame.setBlockData(frameData);
        Bukkit.getLogger().info("Reset frame for "+displayName);
    }

    public void removePlayer(OfflinePlayer player) {
        team.removePlayer(player);
    }

    public Boolean hasPlayer(OfflinePlayer player) {

        return team.hasPlayer(player);

    }

    public Set<String> getEntries() {
        return team.getEntries();
    }

    public void clear() {
        team.removeEntries(team.getEntries());
        if (towerArea != null) {
            towerArea.clearPlayers();
        }
        if (spawnArea != null) {
            spawnArea.clearPlayers();
        }
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

    public void giveShulker(Player player, int number) {

        ItemStack shulker = new ItemStack(Material.valueOf(dye.toUpperCase()+"_SHULKER_BOX"));
        ItemMeta shulkerMeta = shulker.getItemMeta();
        shulkerMeta.displayName(Component.text(SHULKER_NAME).decoration(TextDecoration.ITALIC, false).color(NamedTextColor.AQUA));
        shulker.setItemMeta(shulkerMeta);
        for (int i = 0; i < number; i++) {
            player.getInventory().addItem(shulker);
        }

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
        inventory.setItem(3, manager.getQuestManager().getBook());
        giveBundle(player);
        inventory.setItem(5, steak);
        inventory.setItem(6, torches);
        SpawnCompass.giveCompass(player);
        giveShulker(player, 3);

        inventory.setItem(32, steak);
        inventory.setItem(33, torches);

    }
}
