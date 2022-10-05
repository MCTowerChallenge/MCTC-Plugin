package io.github.idkahn.towerchallenge.penelope;

import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.towering.TowerCommands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public class PenelopeCommands implements CommandExecutor {

    private AbstractHorse penelope;

    public PenelopeCommands() {
        loadConfig();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("towerchallenge.penelope")) {
                if (args.length > 0) {

                    if (args[0].equalsIgnoreCase("create")){
                        try {
                            remove();
                            Bukkit.getLogger().info("Removed existing penelope.");
                        } catch (EntityDoesNotExistException e) {
                            Bukkit.getLogger().info("Penelope does not already exist, creating a new one...");
                        }
                        if (args.length > 1) {
                            try {
                                create(player.getLocation(), PenelopeTypes.valueOf(args[1].toUpperCase()));
                            } catch (IllegalArgumentException e) {
                                player.sendMessage(Component.text("Please enter a correct type!").color(NamedTextColor.DARK_RED));
                                player.sendMessage(Component.text("Types: alive, skeleton, zombie").color(NamedTextColor.DARK_RED));
                            }
                        } else {
                            create(player.getLocation(), PenelopeTypes.ALIVE);
                            player.sendMessage(Component.text("Created new Penelope"));
                        }

                    }

                    if (args[0].equalsIgnoreCase("remove")){

                        try {
                            remove();
                        } catch (EntityDoesNotExistException e) {
                            player.sendMessage(Component.text("There is not an existing Penelope.").color(NamedTextColor.DARK_RED));
                        }

                    }

                    if (args[0].equalsIgnoreCase("set")){
                        if (args.length > 1) {
                            try {
                                change(PenelopeTypes.valueOf(args[1].toUpperCase()));
                            } catch (IllegalArgumentException e) {
                                player.sendMessage(Component.text("Please enter a correct type!").color(NamedTextColor.DARK_RED));
                                player.sendMessage(Component.text("Types: alive, skeleton, zombie").color(NamedTextColor.DARK_RED));
                            } catch (EntityDoesNotExistException e) {
                                player.sendMessage(Component.text("There is not an existing Penelope!").color(NamedTextColor.DARK_RED));
                                player.sendMessage(Component.text("Please use ", NamedTextColor.DARK_RED).append(Component.text("/penelope create <Type>", NamedTextColor.GOLD)).append(Component.text(" to create one!", NamedTextColor.DARK_RED)));
                            }
                        } else {
                            try {
                                change(PenelopeTypes.ALIVE);
                            } catch (EntityDoesNotExistException e) {
                                player.sendMessage(Component.text("There is not an existing Penelope!").color(NamedTextColor.DARK_RED));
                                player.sendMessage(Component.text("Please use ", NamedTextColor.DARK_RED).append(Component.text("/penelope create <Type>", NamedTextColor.GOLD)).append(Component.text(" to create one!", NamedTextColor.DARK_RED)));
                            }
                        }
                    }

                    if (args[0].equalsIgnoreCase("reload")){
                        loadConfig();
                    }

                }
            } else {
                player.sendMessage(TowerCommands.PERMISSION_WARN);
            }
        }

        return true;
    }

    private void loadConfig() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.penelopeConfigFile);
        String uuid = config.getString("UUID");
        if (uuid != null) {
            Entity penelope = Bukkit.getEntity(UUID.fromString(config.getString("UUID")));
            if (penelope instanceof AbstractHorse) {
                this.penelope = (AbstractHorse) penelope;
            }
        }
    }

    private void saveConfig() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.penelopeConfigFile);
        config.set("UUID", penelope.getUniqueId().toString());
        try {
            config.save(TowerChallenge.penelopeConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private AbstractHorse create(Location location, PenelopeTypes type) {
        AbstractHorse newPenelope;
        switch (type) {
            case ZOMBIE:
                newPenelope = (AbstractHorse) location.getWorld().spawnEntity(location, EntityType.ZOMBIE_HORSE, false);
                break;
            case SKELETON:
                newPenelope = (AbstractHorse) location.getWorld().spawnEntity(location, EntityType.SKELETON_HORSE, false);
                break;
            default:
            case ALIVE:
                Horse penelope = (Horse) location.getWorld().spawnEntity(location, EntityType.HORSE, false);
                penelope.setStyle(Horse.Style.WHITE);
                penelope.setColor(Horse.Color.BLACK);
                newPenelope = penelope;
                break;
        }
        newPenelope.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        newPenelope.setTamed(true);
        newPenelope.customName(Component.text("Penelope"));
        this.penelope = newPenelope;
        saveConfig();
        return this.penelope;
    }
    private AbstractHorse create(Location location) {
        return create(location, PenelopeTypes.ALIVE);
    }

    private void remove() throws EntityDoesNotExistException {
        if (penelope == null) {
            throw new EntityDoesNotExistException();
        }
        penelope.remove();
    }

    private void change(PenelopeTypes type) throws EntityDoesNotExistException {
        if (penelope == null) {
            throw new EntityDoesNotExistException();
        }
        Location location = penelope.getLocation();

        Entity leashHolder = null;
        if (penelope.isLeashed()) {
             leashHolder = penelope.getLeashHolder();
        }
        remove();
        AbstractHorse newPenelope = create(location, type);
        if (leashHolder != null) {
            newPenelope.setLeashHolder(leashHolder);
        }
    }

}
