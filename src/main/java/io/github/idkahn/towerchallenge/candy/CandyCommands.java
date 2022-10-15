package io.github.idkahn.towerchallenge.candy;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.commands.CommandUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class CandyCommands implements CommandExecutor {

    private EventManager eventManager;

    public CandyCommands(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reset")) {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.candyConfigFile);
                for (String key : config.getKeys(false)) {
                    config.set(key, null);
                }
                try {
                    config.save(TowerChallenge.candyConfigFile);
                    sender.sendMessage("Reset candy taken");
                } catch (IOException e) {
                    Bukkit.getLogger().info("failed to reset candy");
                }
            }
            if (args[0].equalsIgnoreCase("spawn")) {
                if (sender instanceof Player player) {
                    Location location = player.getLocation().add(0, -1.5, 0);
                    World world = location.getWorld();
                    ArmorStand armorStand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND, false);
                    armorStand.setItem(EquipmentSlot.HEAD, Candy.randomPile());
                    armorStand.setGravity(false);
                    armorStand.setInvisible(true);
                    armorStand.addDisabledSlots(EquipmentSlot.values());
                    armorStand.setInvulnerable(true);
                    player.sendMessage("Spawned new candy armorstand");
                }
            }
            if (args[0].equalsIgnoreCase("bundle")) {
                if (args.length < 2) {
                    sender.sendMessage(CommandUtils.errorMessage("Please enter a player to give a bundle"));
                } else {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player != null) {
                        if (args.length < 3) {
                            eventManager.getTowerListener().getPlayerTeam(player).giveBundle(player);
                        } else {
                            try {
                                int number = Integer.parseInt(args[2]);
                                eventManager.getTowerListener().getPlayerTeam(player).giveBundle(player, number);
                            } catch (NumberFormatException e) {
                                sender.sendMessage(CommandUtils.errorMessage("Please enter a valid number of candies."));
                            }
                        }
                    } else {
                        sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                    }
                }
            }
        } else {
            sender.sendMessage("you did the thing bad");
        }

        return true;
    }
}
