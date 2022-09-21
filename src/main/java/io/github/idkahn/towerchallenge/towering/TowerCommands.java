package io.github.idkahn.towerchallenge.towering;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.github.idkahn.towerchallenge.gui.HatGUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
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

    private TowerListener towerListener;
    private JavaPlugin plugin;
    private HatGUI hatGUI;

    public TowerCommands(JavaPlugin plugin, TowerListener towerListener, HatGUI hatGUI) {
        this.plugin = plugin;
        this.towerListener = towerListener;
        this.hatGUI = hatGUI;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("tower")) {
            if (args.length > 0) {
                switch(args[0].toLowerCase()) {
                    case ("book"):
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
                            BookMeta bookMeta = (BookMeta) book.getItemMeta();
                            bookMeta.author(Component.text("Mystievous"));
                            bookMeta.title(Component.text("Commands"));
                            List<Component> pages = new ArrayList<>(){{
                                add(Component.text(String.format("%-5s%5s", "[M]", "[M]")));
                                add(Component.text("F"));
                            }};
                            bookMeta.pages(pages);
                            book.setItemMeta(bookMeta);
                            player.getInventory().addItem(book);
                        }
                        break;
                    case ("enable"):
                        sender.sendMessage(Component.text("Enabling Tower Phase"));
                        towerListener.enableTower();
                        break;
                    case ("disable"):
                        sender.sendMessage(Component.text("Disabling Tower Phase"));
                        towerListener.disableTower();
                        break;
                    case ("remove"):
                        sender.sendMessage(Component.text("Removing Tower Blocks"));
                        towerListener.removeBlocks();
                        break;
                    case ("reloadteams"):
                        sender.sendMessage(Component.text("Reloading Teams from Config"));
                        towerListener.loadTeams();
                        break;
                    case ("reloadhats"):
                        sender.sendMessage(Component.text("Reloading Hats from Config"));
                        hatGUI.reloadHats();
                        break;
                    case ("reloadconfig"):
                        sender.sendMessage(Component.text("Reloading Hats from Config"));
                        towerListener.loadTeams();
                        hatGUI.reloadHats();
                        break;
                    case ("gui"):
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            Inventory inv = Bukkit.createInventory(player, InventoryType.CHEST, Component.text("\uF808\uE001", NamedTextColor.WHITE));
                            ItemStack[] stacks = new ItemStack[27];
                            Arrays.fill(stacks, new ItemStack(Material.AIR));
                            stacks[11] = new ItemStack(Material.ENDER_EYE);
                            stacks[15] = new ItemStack(Material.TROPICAL_FISH_BUCKET);
                            inv.setContents(stacks);
                            player.openInventory(inv);
                        }
                        break;
                    case ("team"):
                        sender.sendMessage(Bukkit.getScoreboardManager().getMainScoreboard().getPlayerTeam((Player) sender).displayName());
                        break;
                    case ("hat"):
                        hatGUI.openInventory((Player) sender);
                        break;
                    case ("config"):

//                        TowerTeam towerTeam = new TowerTeam(plugin, "Red", "#B02E26");

                        List maps = plugin.getConfig().getMapList("Teams");
                        for (Object o : maps) {
                            HashMap map = (HashMap) o;

                            ArrayList<String> players = (ArrayList<String>) map.get("players");

                            if (players != null) {
                                sender.sendMessage(Component.text("Name: ").append(Component.text((String) map.get("name"))));
                                sender.sendMessage(Component.text("Color: ").append(Component.text((String) map.get("color"))));
                                sender.sendMessage(Component.text("Players: "));
                                for (String uuid : players) {
                                    OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
//                                    sender.sendMessage(Component.text(player.hasPlayedBefore()));
                                    if (player.hasPlayedBefore()) {
//                                        towerTeam.addPlayer(player);
                                        sender.sendMessage(Component.text(player.getName()));
                                    } else {
                                        try {
                                            HttpRequest request = HttpRequest.newBuilder()
                                                    .uri(new URI("https://api.mojang.com/user/profile/" + uuid))
                                                    .GET()
                                                    .build();
                                            HttpClient client = HttpClient.newHttpClient();
                                            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                                            JSONParser parser = new JSONParser();
                                            Object json = parser.parse(response.body());
                                            String playerName = (String) ((JSONObject) json).get("name");

                                            sender.sendMessage(playerName);

                                        } catch (URISyntaxException e) {
                                            throw new RuntimeException(e);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }

                                    }
                                }
                            }
                        }
                        break;
                    case ("getregion"):
                        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                        RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getWorld("world")));

                        ApplicableRegionSet set = regions.getApplicableRegions(BukkitAdapter.adapt((Player) sender).getLocation().toVector().toBlockPoint());

                        if (set.size() < 1) {
                            sender.sendMessage(Component.text("Not in a region!", NamedTextColor.DARK_RED));
                        } else {
                            for (ProtectedRegion r : set) {
                                sender.sendMessage(r.getId());
                            }
                        }
                        break;
                    case ("events"):
                        if (args.length >= 2) {
                            if (args[1].equalsIgnoreCase("enable")) {
                                sender.sendMessage(Component.text("Enabling Block Events"));
                                towerListener.enableEvents();
                            } else {
                                sender.sendMessage(Component.text("Disabling Block Events"));
                                towerListener.disableEvents();
                            }
                        }
                        break;
                    default:
                        sender.sendMessage(Component.text("Invalid Command").color(NamedTextColor.RED));
                }
            }
        }

        return true;
    }
}
