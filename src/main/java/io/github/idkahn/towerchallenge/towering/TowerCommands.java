package io.github.idkahn.towerchallenge.towering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.YAMLConfiguration;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import de.tr7zw.nbtapi.NBTList;
import de.tr7zw.nbtapi.NBTListCompound;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.Wands.WandGUI;
import io.github.idkahn.towerchallenge.Wands.WandUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.NBTComponent;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
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

public class TowerCommands implements CommandExecutor {

    public static final TextComponent PERMISSION_WARN = Component.text("You do not have permission to use this command!").color(NamedTextColor.DARK_RED);
    private TowerListener towerListener;
    private JavaPlugin plugin;


    public TowerCommands(JavaPlugin plugin, TowerListener towerListener) {
        this.plugin = plugin;
        this.towerListener = towerListener;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("tower")) {
            if (args.length > 0) {
                switch(args[0].toLowerCase()) {
                    case ("wand"):
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(Component.text("You must be a player to run this command!").color(NamedTextColor.DARK_RED));
                            break;
                        }
                        if (args[1] == null) {
                            sender.sendMessage(Component.text("Please enter a wand ID!").color(NamedTextColor.DARK_RED));
                            break;
                        }
                        try {
                            int id = Integer.parseInt(args[1]);
                            WandGUI.getWand((Player) sender, id);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(Component.text("Please enter a valid ID!").color(NamedTextColor.DARK_RED));
                        }
                        break;
                    case ("reloadconfig"):
                        sender.sendMessage(Component.text("Reloading Config"));
                        towerListener.loadConfig();
                        break;
                    case("initregionconfig"):
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.regionConfigFile);
                        towerListener.getTeams().forEach((String string, TowerTeam team) -> {
                            List<HashMap<String, String>> list = new ArrayList<>();
                            HashMap<String, String> spawn = new HashMap<>();
                            spawn.put("world", "world_nether");
                            spawn.put("name", PlainTextComponentSerializer.plainText().serialize(team.getDisplayName()).replaceAll(" ", "_").toLowerCase()+"_base");
                            list.add(spawn);
                            HashMap<String, String> tower = new HashMap<>();
                            tower.put("world", "world");
                            tower.put("name", PlainTextComponentSerializer.plainText().serialize(team.getDisplayName()).replaceAll(" ", "_").toLowerCase()+"_tower");
                            list.add(tower);
                            config.set(PlainTextComponentSerializer.plainText().serialize(team.getDisplayName()), list);
                        });
                        try {
                            config.save(TowerChallenge.regionConfigFile);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        break;
//                    case ("gui"):
//                        if (sender instanceof Player) {
//                            Player player = (Player) sender;
//                            Inventory inv = Bukkit.createInventory(player, InventoryType.CHEST, Component.text("\uF808\uE001", NamedTextColor.WHITE));
//                            ItemStack[] stacks = new ItemStack[27];
//                            Arrays.fill(stacks, new ItemStack(Material.AIR));
//                            stacks[11] = new ItemStack(Material.ENDER_EYE);
//                            stacks[15] = new ItemStack(Material.TROPICAL_FISH_BUCKET);
//                            inv.setContents(stacks);
//                            player.openInventory(inv);
//                        }
//                        break;
                    case ("color"):
                        if (!(sender instanceof Player)) {
                            sender.sendMessage(Component.text("You must be a player to use this command!"));
                            break;
                        } else {
                            TowerTeam team = towerListener.getPlayerTeam((Player) sender);
                            if (PlainTextComponentSerializer.plainText().serialize(team.getDisplayName()).equals("God")) {
                                String color = null;
                                try {
                                    color = args[1];
                                    sender.sendMessage("Setting hat color to " + color + "...");
                                } catch (ArrayIndexOutOfBoundsException e) {
                                    sender.sendMessage("No color given, setting default...");
                                }
                                Bukkit.getLogger().info("Setting player color to " + color);
                                ((GodTeam) team).setPlayerHatColor((Player) sender, color);
                            }
                        }
                        break;
                    case ("hat"):
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            TowerTeam team = towerListener.getPlayerTeam(player);
                            if (team != null) {
                                team.openHatGUI(player);
                            } else {
                                player.sendMessage(Component.text("You are not assigned a team! Giving default hats.").color(NamedTextColor.DARK_RED));
                                TowerListener.defaultHats.openInventory(player);
                            }
                        }
                        break;
                    default:
                        sender.sendMessage(Component.text("Invalid command usage.").color(NamedTextColor.RED));
                }
            }
        }

        return true;
    }
}
