package io.github.mctowerchallenge.towerchallenge.towering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mctowerchallenge.towerchallenge.ChallengeManager;
import io.github.mctowerchallenge.towerchallenge.Database;
import io.github.mctowerchallenge.towerchallenge.eventspecific.oct2023.quest.Oct2023QuestManager;
import io.github.mctowerchallenge.towerchallenge.quest.Quest;
import io.github.mctowerchallenge.towerchallenge.quest.QuestManager;
import io.github.mctowerchallenge.towerchallenge.quest.QuestUtil;
import io.github.mctowerchallenge.towerchallenge.quest.util.BlockVoucher;
import io.github.mctowerchallenge.towerchallenge.quest.util.FullInventory;
import io.github.mctowerchallenge.towerchallenge.team.ParticipantTeam;
import io.github.mctowerchallenge.towerchallenge.team.TeamManager;
import io.github.mctowerchallenge.towerchallenge.team.TowerTeam;
import io.github.mctowerchallenge.towerchallenge.team.regions.SpawnRegion;
import io.github.mctowerchallenge.towerchallenge.waitingroom.WaitingRoom;
import io.github.mystievous.mysticore.Palette;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mctowerchallenge.towerchallenge.portal.EndPortal;
import io.github.mctowerchallenge.towerchallenge.portal.PortalControllers;
import io.github.mctowerchallenge.towerchallenge.utility.CommandUtils;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.*;

public class TowerCommands implements CommandExecutor {

    public static final TextComponent PERMISSION_WARN = Component.text("You do not have permission to use this command!").color(NamedTextColor.DARK_RED);

    private final ChallengeManager challengeManager;
    private final TeamManager teamManager;
    private final EndPortal endPortal;
    private final WaitingRoom waitingRoom;
    private final Oct2023QuestManager oct2023QuestManager;
    private final Database database;

    public TowerCommands(ChallengeManager challengeManager, TeamManager teamManager, PortalControllers portalControllers, WaitingRoom waitingRoom, Oct2023QuestManager oct2023QuestManager, Database database) {
        this.challengeManager = challengeManager;
        this.teamManager = teamManager;
        this.endPortal = portalControllers.getEndPortal();
        this.waitingRoom = waitingRoom;
        this.oct2023QuestManager = oct2023QuestManager;
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (command.getName().equalsIgnoreCase("tower")) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case ("pronouns") -> {
                        if (sender instanceof Player player) {
                            player.displayName(player.name().append(Component.space()).append(Component.text("(She/they)").font(Key.key(Key.MINECRAFT_NAMESPACE, "uniform"))));
                        }
                    }
                    case ("deserialize") -> { // Deserializes the item in the player's hand, for use in the plugin.
                        if (sender instanceof Player player) {
                            ItemStack item = player.getInventory().getItemInMainHand();
                            byte[] bytes = item.serializeAsBytes();
                            String output = Base64.getEncoder().encodeToString(bytes);
                            player.sendMessage(output);
                            Bukkit.getServer().getLogger().info(output);
                        } else {
                            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
                        }
                    }
                    case ("addplayer") -> { // Adds a player to a certain team.
                        OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                        StringBuilder teamNameBuilder = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            teamNameBuilder.append(args[i]).append(" ");
                        }
                        String teamName = teamNameBuilder.toString().strip();
                        TowerTeam team = teamManager.getTeam(teamName);
                        if (team != null) {
                            if (teamManager.setPlayerTeam(player, team)) {
                                sender.sendMessage(Component.text(player.getName()).append(Component.text(" set to team ")).append(team.getDisplayName()));
                                teamManager.loadPlayers();
                            } else {
                                sender.sendMessage(CommandUtils.errorMessage(Component.text("Could not set ")
                                        .append(Component.text(player.getName())).append(Component.text(" to team "))
                                        .append(team.getDisplayName())));
                            }
                        } else {
                            sender.sendMessage(CommandUtils.errorMessage(String.format("Team %s does not exist", teamName)));
                        }
                    }
                    case ("addfullblock") -> { // Adds the block in the player's hand to the full block list
                        if (args.length < 2) {
                            if (sender instanceof Player player) {
                                ItemStack item = player.getInventory().getItemInMainHand();
                                if (item.getType().isAir()) {
                                    player.sendMessage(Component.text("You must hold a block, or specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                    player.sendMessage(Component.text("ex. /tower addFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                                } else {
                                    try {
                                        challengeManager.addFullBlock(item.getType());
                                        player.sendMessage(Component.text(TextUtil.formatBlockType(item.getType())).append(Component.text("added as a full block.")));
                                    } catch (IllegalArgumentException e) {
                                        player.sendMessage(Component.text("Held Item is not a block!").color(NamedTextColor.DARK_RED));
                                        player.sendMessage(Component.text("You must hold a block, or specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                        player.sendMessage(Component.text("ex. /tower addFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                                    }
                                }
                            } else {
                                sender.sendMessage(Component.text("You must specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("ex. /tower addFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                            }
                        } else {
                            try {
                                Material type = Material.valueOf(args[1].toUpperCase());
                                challengeManager.addFullBlock(type);
                                sender.sendMessage(Component.text(TextUtil.formatBlockType(type)).append(Component.text("added as a full block.")));
                            } catch (IllegalArgumentException e) {
                                sender.sendMessage(Component.text("Selected material is not a block! Make sure you entered it correctly.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("You must specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("ex. /tower addFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                            }
                        }
                    }
                    case ("removefullblock") -> { // Removes the block in the player's hand to the full block list.
                        if (args.length < 2) {
                            if (sender instanceof Player player) {
                                ItemStack item = player.getInventory().getItemInMainHand();
                                if (item.getType().isAir()) {
                                    player.sendMessage(Component.text("You must hold a block, or specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                    player.sendMessage(Component.text("ex. /tower removeFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                                } else {
                                    try {
                                        challengeManager.removeFullBlock(item.getType());
                                        player.sendMessage(Component.text(TextUtil.formatBlockType(item.getType())).append(Component.text("is no longer a full block.")));
                                    } catch (IllegalArgumentException e) {
                                        player.sendMessage(Component.text("Held Item is not a block!").color(NamedTextColor.DARK_RED));
                                        player.sendMessage(Component.text("You must hold a block, or specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                        player.sendMessage(Component.text("ex. /tower removeFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                                    }
                                }
                            } else {
                                sender.sendMessage(Component.text("You must specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("ex. /tower removeFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                            }
                        } else {
                            try {
                                Material type = Material.valueOf(args[1].toUpperCase());
                                challengeManager.removeFullBlock(type);
                                sender.sendMessage(Component.text(TextUtil.formatBlockType(type)).append(Component.text("is no longer a full block.")));
                            } catch (IllegalArgumentException e) {
                                sender.sendMessage(Component.text("Selected material is not a block! Make sure you entered it correctly.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("You must specify a block type in the command.").color(NamedTextColor.DARK_RED));
                                sender.sendMessage(Component.text("ex. /tower removeFullBlock acacia_log").color(NamedTextColor.DARK_RED));
                            }
                        }
                    }
                    case ("reloadteams") -> { // Reloads the teams and players
                        sender.sendMessage(Component.text("Reloading Config"));
                        teamManager.loadTeams();
                        teamManager.loadPlayers();
                    }
                    case ("resetendportal") -> endPortal.resetPortal(); // resets the end portal to empty
                    case ("showtowerscores") ->
                            teamManager.showTowerScores(sender); // displays the tower scores to the command sender
                    case ("dealitems") -> { // deals items to all players, or the specified player.
                        if (args.length < 2) {
                            teamManager.dealAllItems();
                        } else {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player != null) {
                                teamManager.dealPlayerItems(player);
                            } else {
                                sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                            }
                        }
                    }
                    case ("resetteams") -> teamManager.resetTeams(); // resets teams and re-adds players.
                    case ("shulker") -> { // gives a certain player their team's shulker boxes.
                        if (args.length < 2) {
                            sender.sendMessage(CommandUtils.errorMessage("Please enter a player to give a shulker"));
                        } else {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player != null) {
                                if (args.length < 3) {
                                    teamManager.getPlayerTeam(player).giveShulker(player, 1);
                                } else {
                                    try {
                                        int number = Integer.parseInt(args[2]);
                                        teamManager.getPlayerTeam(player).giveShulker(player, number);
                                    } catch (NumberFormatException e) {
                                        sender.sendMessage(CommandUtils.errorMessage("Please enter a valid number of shulkers."));
                                    }
                                }
                            } else {
                                sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                            }
                        }
                    }
                    case ("pickwinner") -> { // Picks the winning team of the event; announces and gives them the crown.
                        if (sender instanceof Player player) {
                            challengeManager.getWinnersGUI().openInventory(player);
                        } else {
                            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
                        }
                    }
                    case ("voucher") -> { // Gives the sender the specified number of block vouchers.
                        if (sender instanceof Player player) {
                            int number = 1;
                            if (args.length > 1) {
                                try {
                                    number = Integer.parseInt(args[1]);
                                } catch (NumberFormatException e) {
                                    sender.sendMessage(CommandUtils.errorMessage("Invalid number for amount of vouchers."));
                                    return true;
                                }
                            }
                            player.getInventory().addItem(BlockVoucher.getVouchers(number));
                        } else {
                            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
                        }
                    }
                    case ("toggletower") -> { // Enables/disables the towering phase of the event so players can place blocks.
                        if (challengeManager.getChallengePhase().equals(ChallengeManager.ChallengePhase.TOWERING)) {
                            challengeManager.setChallengePhase(ChallengeManager.ChallengePhase.IN_PROGRESS);
                            sender.sendMessage(Component.text("Tower Phase Disabled").color(NamedTextColor.RED));
                        } else if (challengeManager.getChallengePhase().equals(ChallengeManager.ChallengePhase.IN_PROGRESS)) {
                            challengeManager.setChallengePhase(ChallengeManager.ChallengePhase.TOWERING);
                            sender.sendMessage(Component.text("Tower Phase Enabled").color(NamedTextColor.GREEN));
                        }
                    }
                    case ("whitelist") -> {
                        if (args.length < 2) {
                            sender.sendMessage(CommandUtils.errorMessage("Please specify whether to open or close the whitelist."));
                        }

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (args[1].equalsIgnoreCase("open")) {
                                    try {
                                        for (UUID uuid : database.getAllPlayers()) {
                                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    offlinePlayer.setWhitelisted(true);
                                                }
                                            }.runTask(teamManager.getPlugin());
                                        }
                                        sender.sendMessage(TextUtil.formatText("Done opening whitelist"));
                                    } catch (SQLException e) {
                                        throw new RuntimeException(e);
                                    }
                                } else {
                                    Collection<UUID> godUUIDs = teamManager.getGodTeam().getOfflinePlayers().stream().map(OfflinePlayer::getUniqueId).toList();
                                    OfflinePlayer[] allPlayers = Bukkit.getOfflinePlayers();

                                    for (OfflinePlayer player : allPlayers) {
                                        if (!godUUIDs.contains(player.getUniqueId())) {
                                            new BukkitRunnable() {
                                                @Override
                                                public void run() {
                                                    player.setWhitelisted(false);
                                                }
                                            }.runTask(teamManager.getPlugin());
                                        }
                                    }
                                    sender.sendMessage(TextUtil.formatText("Done closing whitelist"));
                                }
                            }
                        }.runTaskAsynchronously(teamManager.getPlugin());
                    }
                    case ("waitingroom") -> {
                        boolean isEnabled = waitingRoom.isEnabled();
                        if (isEnabled) {
                            waitingRoom.setEnabled(false);
                            sender.sendMessage(Component.text("Waiting Room Disabled").color(Palette.NEGATIVE_COLOR.toTextColor()));
                        } else {
                            waitingRoom.setEnabled(true);
                            sender.sendMessage(Component.text("Waiting Room Enabled").color(Palette.PRIMARY.toTextColor()));
                        }
                    }
                    case ("tpallspawn") -> {
                        teamManager.teleportAllSpawn();
                    }
                    case ("tptospawn") -> { // deals items to all players, or the specified player.
                        if (args.length >= 2) {
                            Player player = Bukkit.getPlayer(args[1]);
                            if (player != null) {
                                if (teamManager.getPlayerTeam(player) instanceof ParticipantTeam team) {
                                    team.teleportToSpawn(player);
                                }
                            } else {
                                sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                            }
                        }
                    }
                    case ("copyquestinstances") -> {
                        oct2023QuestManager.copyTemplateToTeams();
                    }
                    case ("deletequestinstances") -> {
                        oct2023QuestManager.deleteTeamInstances();
                    }
                    case ("collectblockvouchers") -> {
                        if (!(sender instanceof Player player)) {
                            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
                            return true;
                        }

                        Map<String, Integer> voucherAmounts = new HashMap<>();
                        for (Player serverPlayer : Bukkit.getOnlinePlayers()) {
                            TowerTeam team = teamManager.getPlayerTeam(serverPlayer);
                            if (team == null) {
                                continue;
                            }

                            PlayerInventory inventory = serverPlayer.getInventory();
                            for (ItemStack itemStack : inventory.getContents()) {
                                if (itemStack == null || !BlockVoucher.isVoucher(itemStack)) {
                                    continue;
                                }

                                int amount = itemStack.getAmount();
                                int voucherAmount = voucherAmounts.getOrDefault(team.getServerTeamName(), 0);
                                voucherAmount += amount;
                                voucherAmounts.put(team.getServerTeamName(), voucherAmount);
                                inventory.removeItem(itemStack);
                            }
                        }

                        for (ParticipantTeam team : teamManager.getParticipantTeams()) {
                            SpawnRegion spawnRegion = team.getSpawnRegion();
                            ProtectedRegion region = spawnRegion.getRegion();
                            BlockVector3 minPoint = region.getMinimumPoint();
                            BlockVector3 maxPoint = region.getMaximumPoint();
                            for (int x = minPoint.getBlockX(); x < maxPoint.getBlockX(); x++) {
                                for (int y = minPoint.getBlockY(); y < maxPoint.getBlockY(); y++) {
                                    for (int z = minPoint.getBlockZ(); z < maxPoint.getBlockZ(); z++) {
                                        Block block = new Location(spawnRegion.getSpawnpoint().getWorld(), x, y, z).getBlock();
                                        if (block.getState() instanceof Container container) {
                                            Inventory inventory = container.getInventory();
                                            if (container instanceof Chest chest) {
                                                inventory = chest.getBlockInventory();
                                            }

                                            for (ItemStack itemStack : inventory.getContents()) {
                                                if (itemStack == null || !BlockVoucher.isVoucher(itemStack)) {
                                                    continue;
                                                }

                                                int amount = itemStack.getAmount();
                                                int voucherAmount = voucherAmounts.getOrDefault(team.getServerTeamName(), 0);
                                                voucherAmount += amount;
                                                voucherAmounts.put(team.getServerTeamName(), voucherAmount);
                                                inventory.removeItem(itemStack);
                                            }
                                        }
                                    }
                                }
                            }
                        }


                        for (Map.Entry<String, Integer> teamVouchers : voucherAmounts.entrySet()) {
                            ItemStack bedrock = GuiUtil.formatItem(teamVouchers.getKey(), Material.BEDROCK, 0);
                            bedrock.setAmount(teamVouchers.getValue());
                            FullInventory.givePlayerItems(player, bedrock);
                            sender.sendMessage(TextUtil.formatText("Done collecting vouchers"));
                        }
                    }
                    case ("completetrivia") -> {
                        // tower completetrivia team_id Oct2023_quest x y z x' y' z' destx desty destz
                        TowerTeam team = teamManager.getTeam(Integer.parseInt(args[1]));
                        if (team != null) {
                            World world = Bukkit.getWorld(args[2]);
                            if (world == null) {
                                return true;
                            }

                            CuboidRegion cuboidRegion = new CuboidRegion(BukkitAdapter.adapt(world),
                                    BlockVector3.at(Double.parseDouble(args[3]), Double.parseDouble(args[4]), Double.parseDouble(args[5])),
                                    BlockVector3.at(Double.parseDouble(args[6]), Double.parseDouble(args[7]), Double.parseDouble(args[8])));
                            Location teleportLocation = new Location(world, Double.parseDouble(args[9]), Double.parseDouble(args[10]), Double.parseDouble(args[11]), Float.parseFloat(args[12]), Float.parseFloat(args[13]));
                            boolean rewardGiven = false;
                            Quest quest = team.getQuest(QuestManager.TRIVIA);
                            for (Player player : Bukkit.getOnlinePlayers()) {
                                if (cuboidRegion.contains(BukkitAdapter.asBlockVector(player.getLocation()))) {
                                    if (quest != null && !quest.isCompleted() && !rewardGiven && team.hasPlayer(player)) {
                                        team.completeQuest(QuestManager.TRIVIA);
                                        rewardGiven = true;
                                        ItemStack bundle = QuestUtil.randomBlockBundle(5);
                                        player.sendMessage(QuestManager.getRewards(bundle));
                                        FullInventory.givePlayerItems(player, bundle);
                                        Oct2023QuestManager.checkCompletedQuests(team, player);
                                    }
                                    player.teleport(teleportLocation);
                                }
                            }
                        }
                    }
                    default -> sender.sendMessage(Component.text("Invalid command usage.").color(NamedTextColor.RED));
                }
            }
        }

        return true;
    }
}
