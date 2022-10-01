package io.github.idkahn.towerchallenge.towering;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import io.github.idkahn.towerchallenge.BlockSets;
import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.hats.HatGUI;
import io.github.idkahn.towerchallenge.hats.HatUtil;
import io.github.idkahn.towerchallenge.Teams;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TowerListener implements Listener {

    private final EnumMap<Teams, ArrayList<BlockState>> towers = new EnumMap<Teams, ArrayList<BlockState>>(Teams.class);

    private HashMap<String, TowerTeam> teams;
    private GodTeam godTeam;

    BlockSets blockSets;

    // Whether server is currently in Towering State
    private boolean isTowering;
    private boolean cancelEvents;

    private final EventManager manager;
    private final JavaPlugin plugin;

    public static HatGUI defaultHats;

    public TowerListener(EventManager manager) {
        this.manager = manager;
        this.plugin = manager.getPlugin();
        this.blockSets = new BlockSets();
        this.isTowering = false;
        this.cancelEvents = false;
        this.teams = new HashMap<>();
        loadTeams();
        for (Teams team : Teams.values()) {
            towers.put(team, new ArrayList<>());
        }
        defaultHats = new HatGUI(plugin, Color.RED);
    }

    public TowerTeam getTeam(String name) {
        return this.teams.get(name);
    }

    public HashMap<String, TowerTeam> getTeams() {
        return teams;
    }

    public TowerTeam getPlayerTeam(OfflinePlayer player) {
        Team team = Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam(player);
        if (team != null) {
            if (team.getName().equals("God")) {
                return godTeam;
            } else {
                return getTeam(team.getName());
            }
        } else {
            return null;
        }
    }

    public GodTeam getGodTeam() {
        return godTeam;
    }

    public void loadConfig() {
        loadTeams();
        loadHats();
    }

    public void loadTeams() {
        Bukkit.getLogger().info("[Tower Challenge] Loading Team Config...");
        plugin.reloadConfig();
        godTeam = new GodTeam(manager, "God", "#F7E983", "orange");
        List<String> godPlayers = plugin.getConfig().getStringList("Gods");
        for (String uuid : godPlayers) {
            godTeam.addPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
        }
        // Get team configs
        List maps = plugin.getConfig().getMapList("Teams");
        HashMap<String, TowerTeam> newTeams = new HashMap<>();
        for (int i = 0; i < maps.size(); i++) {
            // for each team in the config
            HashMap map = (HashMap) maps.get(i);
            // retrieve all config values
            String name = (String) map.get("name");
            String color = (String) map.get("color");
            String dye = (String) map.get("dye");
            ArrayList<String> players = (ArrayList<String>) map.get("players");


            // Create new Team
            if (teams.get(name) != null) {
                newTeams.put(name, this.teams.get(name));
            } else {
                newTeams.put(name, new TowerTeam(manager, name, color, dye));
            }

//            //TODO: Check teams for extra players
//            for (Team entry : TowerTeam.scoreboard.getTeams()) {
////            for (String entry : newTeams.get(name).getEntries()) {
//                Bukkit.getServer().sendMessage(entry.displayName());
//
//            }

            if (players != null) {
                for (String uuid : players) {
                    // add each player to team, if able
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    plugin.getLogger().info("Checking team for " + player.getName() + "...");
                    newTeams.get(name).addPlayer(player, false);
                }
            }
        }

//        // Set
//        Set<String> removedTeams = teams.keySet();
//        removedTeams.removeAll(newTeams.keySet());
//        for (String team : removedTeams) {
//            this.teams.get(team).destroyTeam();
//        }

        this.teams = newTeams;
        Bukkit.getLogger().info("[Tower Challenge] Team Config Loaded!");
    }

    public void loadHats() {
        Bukkit.getLogger().info("[Tower Challenge] Loading Hat Config...");
        teams.forEach((name, team) -> team.loadHats());
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;

        Player player = (Player) event.getWhoClicked();
        if (player.getGameMode().equals(GameMode.CREATIVE))
            return;

        ItemStack item = event.getCurrentItem();
        int slot = event.getSlot();

        if (HatUtil.isHat(item)) {
            if (event.getSlotType().equals(InventoryType.SlotType.ARMOR)) {
                event.setCancelled(true);
                Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.getInventory().clear(slot), 1);
            }
        }
    }

    @EventHandler
    public void onEntityPortal(final EntityPortalEvent event) {
        if (event.isCancelled())
            return;
        if (event.getTo().getWorld().equals(Bukkit.getWorld("world_nether"))) {
            event.setTo(EventManager.NETHER_PORTAL_LOCATION);
        } else if (event.getTo().getWorld().equals(Bukkit.getWorld("world"))) {
            event.setTo(EventManager.OVERWORLD_PORTAL_LOCATION);
        }
    }
    @EventHandler
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (event.isCancelled())
            return;
        if (event.getTo().getWorld().equals(Bukkit.getWorld("world_nether"))) {
            event.setTo(EventManager.NETHER_PORTAL_LOCATION);
        } else if (event.getTo().getWorld().equals(Bukkit.getWorld("world"))) {
            event.setTo(EventManager.OVERWORLD_PORTAL_LOCATION);
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {

        Player player = event.getPlayer();
        loadTeams();

        if (!player.hasPlayedBefore()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> player.teleport(getPlayerTeam(player).getSpawnpoint()), 1);
        }
//        event.joinMessage(Component.text(String.format("%s joined the game", event.getPlayer().getName())));

    }

    @EventHandler
    public void onPlaceBlock(final BlockPlaceEvent event) {

        Player player = event.getPlayer();
        BlockState placedBlockState = event.getBlockPlaced().getState(); // Snapshot of the block at the moment it is placed
        
        if (isTowering) {
            if (!blockSets.fullBlocks.contains(event.getBlockPlaced().getType())) {
                // Placed block is not registered as a full block
                TextComponent text = Component.text(placedBlockState.getType().name(), NamedTextColor.RED)
                        .append(Component.text(" is not a full block!", NamedTextColor.WHITE));
                player.sendActionBar(text);
                event.setCancelled(true);
                return;
            }

            if (placedBlockState instanceof ShulkerBox) {
                ShulkerBox shulkerBox = (ShulkerBox) placedBlockState;

                if (shulkerBox.customName() != null) {
                    String shulkerBoxName = PlainTextComponentSerializer.plainText().serialize(shulkerBox.customName());

                    if (shulkerBoxName.equals("Starting Shulker")) {
                        return;
                    }
                }
            }


            for (ArrayList<BlockState> array : towers.values()) {
                for (BlockState blockState : array) {
                    if (placedBlockState.getType() == blockState.getType()) {
                        event.setCancelled(true);

                        TextComponent text = Component.text("You have already placed ").
                                append(Component.text(placedBlockState.getType().name(), NamedTextColor.RED))
                                .append(Component.text("!"));

                        player.sendActionBar(text);
                        return;
                    }
                }
            }

            towers.get(Teams.RED).add(placedBlockState);
            player.sendMessage("Blocks Placed: " + towers.get(Teams.RED).size());
        }

        if (isTowering || cancelEvents && !event.isCancelled()) {
            if (event.getItemInHand().getType() == Material.WET_SPONGE) {

                BlockData wetSponge = Material.WET_SPONGE.createBlockData();

                if (event.getBlockPlaced().getWorld().getEnvironment() == World.Environment.NETHER) {
                    event.setCancelled(true);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            event.getBlock().setBlockData(wetSponge, false);
                        }
                    }.runTask(plugin);
                }
            }
        }

    }



    @EventHandler
    public void onBreakBlock(final BlockBreakEvent event) {

        Player player = event.getPlayer();
        BlockState brokenBlockState = event.getBlock().getState();

        if (isTowering) {
            for (ArrayList<BlockState> list : towers.values()) {
                Iterator<BlockState> listIterator = list.iterator();
                while (listIterator.hasNext()) {
                    BlockState blockState = listIterator.next();

                    Location blockLocation = blockState.getLocation();

                    if (blockLocation.getX() == brokenBlockState.getX() &&
                            blockLocation.getY() == brokenBlockState.getY() &&
                            blockLocation.getZ() == brokenBlockState.getZ()) {

    //                    event.getPlayer().sendMessage(event.getBlock().getType().name());
                        listIterator.remove();
                        player.sendMessage("Blocks Placed: " + towers.get(Teams.RED).size());
                        return;
                    }

                }
            }
        }

    }

    @EventHandler
    public void onBlockBurn(final BlockBurnEvent event) {
        if (isTowering || cancelEvents) event.setCancelled(true);
//        Bukkit.getLogger().info("Cancelled BlockBurn");
//        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(final BlockSpreadEvent event) {
        if (isTowering || cancelEvents) event.setCancelled(true);
//        Bukkit.getLogger().info("Cancelled BlockSpread");
//        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockExplode(final BlockExplodeEvent event) {
        if (isTowering || cancelEvents) event.setCancelled(true);
//        Bukkit.getLogger().info("Cancelled BlockExplode");
//        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (isTowering || cancelEvents) event.setCancelled(true);
//        Bukkit.getLogger().info("Cancelled EntityExplode");
//        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(final BlockFadeEvent event) {
        if (isTowering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled BlockFade"));
//        event.setCancelled(true);
    }

//    @EventHandler
//    public void onBlockIgnite(final BlockIgniteEvent event) {
//        if (isTowering) event.setCancelled(true);
////        Bukkit.broadcast(Component.text("Cancelled BlockIgnite"));
////        event.setCancelled(true);
//    }

    @EventHandler
    public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
        if (isTowering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled BlockPistonExtend"));
//        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
        if (isTowering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled BlockPistonRetract"));
//        event.setCancelled(true);
    }

    @EventHandler
    public void onSpongeAbsorb(final SpongeAbsorbEvent event) {
        if (isTowering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled SpongeAbsorb"));
//        event.setCancelled(true);
    }

//    @EventHandler
//    public void onBlockDestroy(final BlockDestroyEvent event) {
//        if (isTowering) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled BlockDestroy"));
//        event.setCancelled(true);
//    }

    @EventHandler
    public void onTNTPrime(final TNTPrimeEvent event) {
        if (isTowering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled TNTPrime"));
//        event.setCancelled(true);
    }

//    @EventHandler
//    public void onBlockPhysics(final BlockPhysicsEvent event) {
//        if (fallingBlocks.contains(event.getBlock().getRelative(BlockFace.UP).getType())) {
//            if (isTowering) event.setCancelled(true);
////            Bukkit.broadcast(Component.text("Cancelled BlockPhysics ").append(Component.text(event.getBlock().getType().name())));
////            event.setCancelled(true);
//        }
//    }

    @EventHandler
    public void onBlockGrow(final BlockGrowEvent event) {
        if (isTowering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled BlockGrow ").append(Component.text(event.getBlock().getType().name())));
//        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(final BlockFormEvent event) {
        if (isTowering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled BlockForm ").append(Component.text(event.getBlock().getType().name())));
//        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        if (isTowering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled EntityChangeBlock ").append(Component.text(event.getEntity().getType().name())));
//        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerShearBlock(final PlayerShearBlockEvent event) {
        if (isTowering || cancelEvents) event.setCancelled(true);
//        Bukkit.broadcast(Component.text("Cancelled EntityChangeBlock ").append(Component.text(event.getEntity().getType().name())));
//        event.setCancelled(true);
    }

    public void enableTower() {
        isTowering = true;
    }

    public void disableTower() {
        isTowering = false;
    }

    public void enableEvents() {
        cancelEvents = false;
    }

    public void disableEvents() {
        cancelEvents = true;
    }

    public void removeBlocks() {
        for (ArrayList<BlockState> list : towers.values()) {
            Iterator<BlockState> listIterator = list.iterator();
            while (listIterator.hasNext()) {
                BlockState blockState = listIterator.next();
                blockState.getBlock().setType(Material.AIR);
                listIterator.remove();
            }
        }
    }

}
