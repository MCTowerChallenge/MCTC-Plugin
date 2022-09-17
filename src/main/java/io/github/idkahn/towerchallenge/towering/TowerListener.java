package io.github.idkahn.towerchallenge.towering;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import io.github.idkahn.towerchallenge.BlockSets;
import io.github.idkahn.towerchallenge.Teams;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import it.unimi.dsi.fastutil.Hash;
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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class TowerListener implements Listener {

    private EnumMap<Teams, ArrayList<BlockState>> towers;

    private TowerTeam[] teams;

    BlockSets blockSets;

    // Whether server is currently in Towering State
    private boolean isTowering;
    private boolean cancelEvents;

    private JavaPlugin plugin;

    public TowerListener(JavaPlugin plugin) {
        this.plugin = plugin;
        this.blockSets = new BlockSets();
        this.isTowering = false;
        this.cancelEvents = false;

        initTeams();

        this.towers = new EnumMap<Teams, ArrayList<BlockState>>(Teams.class);
        for (Teams team : Teams.values()) {
            towers.put(team, new ArrayList<BlockState>());
        }
    }

    public void initTeams() {
        plugin.reloadConfig();
        // Get team configs
        List maps = plugin.getConfig().getMapList("Teams");
        this.teams = new TowerTeam[maps.size()];
        for (int i = 0; i < maps.size(); i++) {
            // for each team in the config
            HashMap map = (HashMap) maps.get(i);
            teams[i] = new TowerTeam(plugin, (String) map.get("name"), (String) map.get("color"));
            ArrayList<String> players = (ArrayList<String>) map.get("players");
            if (players != null) {
                for (String uuid : players) {
                    // add each player to team, if able
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    if (player.hasPlayedBefore()) {
                        teams[i].addPlayer(player, false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {

        initTeams();
//        Player player = event.getPlayer();
//        plugin.reloadConfig();
//        List maps = plugin.getConfig().getMapList("Teams");
//
//        for (Object o : maps) {
//            HashMap map = (HashMap) o;
//            ArrayList<String> players = (ArrayList<String>) map.get("players");
//            for (String uuid : players) {
//                if (player.getUniqueId().toString() == uuid) {
//
//                }
//            }
//        }
//
//        for (TowerTeam team : teams) {
//            if (team.hasPlayer(player)) {
//                for (Object o : maps) {
//                    HashMap map = (HashMap) o;
//                    if (map.get("name") == team.getDisplayName()) {
//                        if (map.get)
//                    }
//                }
//            }
//        }


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
