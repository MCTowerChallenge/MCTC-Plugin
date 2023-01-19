package io.github.mystievous.towerchallenge.towering;

import com.onarandombox.MultiverseNetherPortals.MultiverseNetherPortals;
import com.onarandombox.MultiverseNetherPortals.utils.MVEventRecord;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.configs.Config;
import io.github.mystievous.towerchallenge.Palette;
import io.github.mystievous.towerchallenge.gods.GodTeam;
import io.github.mystievous.towerchallenge.hats.HatGUI;
import io.github.mystievous.towerchallenge.hats.HatUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TowerListener implements Listener {

    private GodTeam godTeam;

    private HashMap<String, ParticipantTeam> teams;

    private final ChallengeManager manager;
    private final JavaPlugin plugin;

    public static HatGUI defaultHats;

    public TowerListener(ChallengeManager manager) {
        this.manager = manager;
        this.plugin = manager.getPlugin();
        this.teams = manager.getTeams();
        loadTeams();
        defaultHats = new HatGUI(manager, Color.RED);
    }

    public @Nullable TowerTeam getTeam(@NotNull String name) {
        if (!name.equalsIgnoreCase("God")) {
            return this.teams.get(name);
        } else {
            return getGodTeam();
        }
    }

    public HashMap<String, ParticipantTeam> getTeams() {
        return teams;
    }

    public @Nullable TowerTeam getPlayerTeam(OfflinePlayer player) {
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
        YamlConfiguration config = YamlConfiguration.loadConfiguration(Config.teamConfigFile);
        godTeam = new GodTeam(manager);
        List<String> godPlayers = config.getStringList("Gods");
        for (String uuid : godPlayers) {
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            plugin.getLogger().info(String.format("Adding %s to team %s...", player.getName(), godTeam.getTeam().getName()));
            godTeam.addTeamPlayer(player);
        }
        // Get team configs
        HashMap<String, ParticipantTeam> newTeams = new HashMap<>();
        if (config.isConfigurationSection("Teams")) {
            for (String name : config.getConfigurationSection("Teams").getKeys(false)) {
                ConfigurationSection teamConfig = config.getConfigurationSection("Teams."+name);
                // retrieve all config values
//                String teamPath = "Teams."+name;
                Bukkit.getLogger().info("Checking Team: " + name);
                boolean disabled = teamConfig.getBoolean("disabled");
                if (disabled) {
                    Bukkit.getLogger().info(String.format("Team %s is disabled, skipping.", name));
                    continue;
                }
                String color = teamConfig.getString("color");
                String dye = teamConfig.isString("dye") ? teamConfig.getString("dye") : "white";
                List<String> players = teamConfig.getStringList("players");
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
                        newTeams.get(name).addTeamPlayer(player);
                    }
                }
            }
        } else {
            Bukkit.getLogger().warning("Team config is not valid!");
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
                    || !(getPlayerTeam(player) instanceof GodTeam || player.isOp())) {
                event.setCancelled(true);
                Location location = event.getBlocks().get(0).getLocation();
                ComponentBuilder<TextComponent, TextComponent.Builder> message = Component.text().decoration(TextDecoration.ITALIC, true).color(Palette.PRIMARY.getTextColor());
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

    private final Location overworldPortalLocation = new Location(Worlds.Feb2023(), 97.5, 66, -2114.5);
    private final Location netherPortalLocation = new Location(Worlds.Feb2023_nether(), -23.5, 41, -388.5);

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onEntityPortal(final EntityPortalEvent event) {
        if (event.isCancelled())
            return;
        if (event.getTo().getWorld().equals(Worlds.Feb2023_nether())) {
            event.setCancelled(true);
            event.getEntity().teleport(netherPortalLocation, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
//                event.setTo(netherPortalLocation);
        } else if (event.getTo().getWorld().equals(Worlds.Feb2023())) {
            event.setCancelled(true);
            event.setTo(overworldPortalLocation);
            event.getEntity().teleport(overworldPortalLocation, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
        }
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onPlayerPortal(final PlayerPortalEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        if (event.getCause().equals(PlayerTeleportEvent.TeleportCause.NETHER_PORTAL)) {
            if (event.getTo().getWorld().equals(Worlds.Feb2023_nether())) {
                event.setCancelled(true);
                player.teleport(netherPortalLocation, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
                Advancement enterNetherAdvancement = this.plugin.getServer().getAdvancement(NamespacedKey.minecraft("story/enter_the_nether"));
                if (enterNetherAdvancement != null) {
                    String enterNetherCriteria = "entered_nether";
                    AdvancementProgress advancementProgress = player.getAdvancementProgress(enterNetherAdvancement);
                    if (!advancementProgress.isDone()) {
                        advancementProgress.awardCriteria(enterNetherCriteria);
                    }
                }
            } else if (event.getTo().getWorld().equals(Worlds.Feb2023())) {
                event.setCancelled(true);
                event.setTo(overworldPortalLocation);
                player.teleport(overworldPortalLocation, PlayerTeleportEvent.TeleportCause.NETHER_PORTAL);
            }
        }
    }

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
