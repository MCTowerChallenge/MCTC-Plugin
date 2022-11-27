package io.github.idkahn.towerchallenge.towering;

import io.github.idkahn.towerchallenge.BlockSets;
import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.hats.HatGUI;
import io.github.idkahn.towerchallenge.hats.HatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TowerListener implements Listener {

    private GodTeam godTeam;

    private HashMap<String, ParticipantTeam> teams;
    BlockSets blockSets;

    private final EventManager manager;
    private final JavaPlugin plugin;

    public static HatGUI defaultHats;

    public TowerListener(EventManager manager) {
        this.manager = manager;
        this.plugin = manager.getPlugin();
        this.blockSets = new BlockSets();
        this.teams = manager.getTeams();
        loadTeams();
        defaultHats = new HatGUI(manager, Color.RED);
    }

    public ParticipantTeam getTeam(String name) {
        return this.teams.get(name);
    }

    public HashMap<String, ParticipantTeam> getTeams() {
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
        Bukkit.getLogger().info("Loading Team Config...");
//        plugin.reloadConfig();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.teamConfigFile);
        godTeam = new GodTeam(manager);
        List<String> godPlayers = config.getStringList("Gods");
        for (String uuid : godPlayers) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            plugin.getLogger().info(String.format("Adding %s to team %s...", player.getName(), godTeam.getTeam().getName()));
            godTeam.addPlayer(player);
        }
        // Get team configs
        List<String> teamNames = config.getStringList("TeamNames");
        HashMap<String, ParticipantTeam> newTeams = new HashMap<>();
        for (String name : teamNames) {
            // retrieve all config values
            String teamPath = "Teams."+name;
            Bukkit.getLogger().info("Checking Team: " + name);
            boolean disabled = config.getBoolean(teamPath+".disabled");
            if (disabled) {
                Bukkit.getLogger().info(String.format("Team %s is disabled, skipping.", name));
                continue;
            }
            String color = config.getString(teamPath+".color");
            String configDye = config.getString(teamPath+".dye");
            String dye = (configDye != null) ? configDye : "white";
            List<String> players = config.getStringList(teamPath+".players");

            // Create new Team
            if (teams.get(name) != null) {
                this.teams.get(name).loadPortal();
                newTeams.put(name, this.teams.get(name));
            } else {
                newTeams.put(name, new ParticipantTeam(manager, name, color, dye));
            }

            if (!players.isEmpty()) {
                for (String uuid : players) {
                    // add each player to team, if able
                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    plugin.getLogger().info(String.format("Adding %s to team %s...", player.getName(), name));
                    newTeams.get(name).addPlayer(player);
                }
            }
        }

        this.teams = newTeams;
        Bukkit.getLogger().info("Team Config Loaded!");
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
                ComponentBuilder<TextComponent, TextComponent.Builder> message = Component.text().decoration(TextDecoration.ITALIC, true).color(TowerChallenge.PRIMARY_COLOR);
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
                    Component displayName = blockStateMeta.displayName();
                    if (displayName != null && PlainTextComponentSerializer.plainText().serialize(displayName).equals(ParticipantTeam.SHULKER_NAME)) {
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
//    public void onEntityPortal(final EntityPortalEvent event) {
//        if (event.isCancelled())
//            return;
//        if (event.getTo().getWorld().equals(Bukkit.getWorld("December MCTC_nether"))) {
//            event.setTo(EventManager.NETHER_PORTAL_LOCATION);
//        } else if (event.getTo().getWorld().equals(Bukkit.getWorld("December MCTC"))) {
//            event.setTo(EventManager.OVERWORLD_PORTAL_LOCATION);
//        }
//    }

//    @EventHandler
//    public void onPlayerPortal(final PlayerPortalEvent event) {
//        if (event.isCancelled())
//            return;
//        if (event.getTo().getWorld().equals(Bukkit.getWorld("December MCTC_nether"))) {
//            event.setTo(EventManager.NETHER_PORTAL_LOCATION);
//        } else if (event.getTo().getWorld().equals(Bukkit.getWorld("December MCTC"))) {
//            event.setTo(EventManager.OVERWORLD_PORTAL_LOCATION);
//        }
//    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {

        Player player = event.getPlayer();
        loadTeams();

        if (!player.hasPlayedBefore()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                if (getPlayerTeam(player) instanceof ParticipantTeam team) {
                    player.teleport(team.getSpawnpoint());
                }
            }, 1);
        }
    }

}
