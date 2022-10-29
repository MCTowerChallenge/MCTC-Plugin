package io.github.idkahn.towerchallenge.towering;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.ibm.icu.impl.UtilityExtensions;
import com.ibm.icu.impl.locale.LocaleValidityChecker;
import io.github.idkahn.towerchallenge.BlockSets;
import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.Teams;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.hats.HatGUI;
import io.github.idkahn.towerchallenge.hats.HatUtil;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TowerListener implements Listener {

    private final EnumMap<Teams, ArrayList<BlockState>> towers = new EnumMap<Teams, ArrayList<BlockState>>(Teams.class);

    public static final Location STEVE_LECTERN = new Location(Bukkit.getWorld("world"), -708, 56, -1331);

    private GodTeam godTeam;

    private HashMap<String, TowerTeam> teams;
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
        this.teams = manager.getTeams();
        loadTeams();
        for (Teams team : Teams.values()) {
            towers.put(team, new ArrayList<>());
        }
        defaultHats = new HatGUI(manager, Color.RED);
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
                return getTeam(PlainTextComponentSerializer.plainText().serialize(team.displayName()));
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
            String configDye = (String) map.get("dye");
            String dye = (configDye != null) ? configDye : "white";
            ArrayList<String> players = (ArrayList<String>) map.get("players");


            // Create new Team
            if (teams.get(name) != null) {
                this.teams.get(name).loadPortal();
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

    @EventHandler
    public void onPortalCreate(final PortalCreateEvent event) {
        if (event.isCancelled())
            return;

        if (event.getReason().equals(PortalCreateEvent.CreateReason.NETHER_PAIR)) {
            event.setCancelled(true);
        }
        if (event.getReason().equals(PortalCreateEvent.CreateReason.FIRE)) {
            if (event.getEntity() == null
                    || !(event.getEntity() instanceof Player player)
                    || !(getPlayerTeam(player) instanceof GodTeam)) {
                event.setCancelled(true);
                Location location = event.getBlocks().get(0).getLocation();
                ComponentBuilder message = Component.text().decoration(TextDecoration.ITALIC, true).color(TowerChallenge.PRIMARY_COLOR);
                message.append(Component.text("A portal was attempted to be opened at ")
                        .append(Component.text("X: "+location.getBlockX()))
                        .append(Component.text(", Y: "+location.getBlockY()))
                        .append(Component.text(", Z: "+location.getBlockZ()))
                );
                if (event.getEntity() instanceof Player player) {
                    message.append(Component.text(" by ").append(player.name()));
                    message.append(Component.text(" [Teleport]").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
                            .hoverEvent(HoverEvent.showText(Component.text("Click to teleport!")))
                            .clickEvent(ClickEvent.runCommand("/tp "+player.getName()))
                    );
                } else {
                    message.append(Component.text(" [Teleport]").color(NamedTextColor.AQUA).decoration(TextDecoration.ITALIC, false)
                            .hoverEvent(HoverEvent.showText(Component.text("Click to teleport!")))
                            .clickEvent(ClickEvent.runCommand("/tp "+location.getBlockX()+" "+location.getBlockY()+" "+location.getBlockZ()))
                    );
                }
                godTeam.getAudience().sendMessage(message.build());
            }
        }
        if (event.getBlocks().get(0).getType().equals(Material.END_PORTAL_FRAME)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;

        if (event.getView().getTopInventory() instanceof AnvilInventory) {
            if(event.getCurrentItem().getItemMeta() instanceof BlockStateMeta blockStateMeta){
                if(blockStateMeta.getBlockState() instanceof ShulkerBox){
                    if (PlainTextComponentSerializer.plainText().serialize(blockStateMeta.displayName()).equals(TowerTeam.SHULKER_NAME)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }

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

//    @EventHandler
//    public void onPlayerInteract(final PlayerInteractEvent event) {
//        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock() != null) {
//            if (event.getClickedBlock().getType().equals(Material.LECTERN)) {
//                if (event.getClickedBlock().getLocation().equals(STEVE_LECTERN)) {
//                    event.setCancelled(true);
//                }
//            }
//        }
//    }

//    @EventHandler
//    public void onEntityPortal(final EntityPortalEvent event) {
//        if (event.isCancelled())
//            return;
//        if (event.getTo().getWorld().equals(Bukkit.getWorld("world_nether"))) {
//            event.setTo(EventManager.NETHER_PORTAL_LOCATION);
//        } else if (event.getTo().getWorld().equals(Bukkit.getWorld("world"))) {
//            event.setTo(EventManager.OVERWORLD_PORTAL_LOCATION);
//        }
//    }
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

    }

}
