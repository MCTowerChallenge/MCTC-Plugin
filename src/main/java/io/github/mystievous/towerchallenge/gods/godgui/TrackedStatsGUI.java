package io.github.mystievous.towerchallenge.gods.godgui;

import io.github.mystievous.towerchallenge.TextUtil;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.decoration.waterspouts.SpoutManager;
import io.github.mystievous.towerchallenge.gods.GodTeam;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.ListGui;
import io.github.mystievous.towerchallenge.gui.page.PlayerGui;
import io.github.mystievous.towerchallenge.misc.fasttravel.FastTravelListener;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.stream.Collectors;

public class TrackedStatsGUI extends ListGui {

    public static final EnumSet<Statistic> distanceStats = EnumSet.of(
            Statistic.WALK_ONE_CM,
            Statistic.WALK_ON_WATER_ONE_CM,
            Statistic.FALL_ONE_CM,
            Statistic.CLIMB_ONE_CM,
            Statistic.FLY_ONE_CM,
            Statistic.WALK_UNDER_WATER_ONE_CM,
            Statistic.MINECART_ONE_CM,
            Statistic.BOAT_ONE_CM,
            Statistic.PIG_ONE_CM,
            Statistic.HORSE_ONE_CM,
            Statistic.SPRINT_ONE_CM,
            Statistic.CROUCH_ONE_CM,
            Statistic.AVIATE_ONE_CM,
            Statistic.SWIM_ONE_CM,
            Statistic.STRIDER_ONE_CM
    );

    public TrackedStatsGUI(Element lastElement) {
        super(Component.text("Pick stat to view"), lastElement);

        ItemStack jumpsItem = new ItemStack(Material.RABBIT_FOOT);
        ItemMeta jumpsMeta = jumpsItem.getItemMeta();
        jumpsMeta.displayName(TextUtil.noItalic("Jumps"));
        jumpsItem.setItemMeta(jumpsMeta);
        ButtonElement jumpsElement = new ButtonElement(jumpsItem, player -> {
            PlayerGui gui = new PlayerGui(Component.text("Number of Jumps:"), new ItemStack(Material.PLAYER_HEAD), player1 -> {
                return TextUtil.formatTexts(Component.text("Jumps: ").append(Component.text(player1.getStatistic(Statistic.JUMP))));
            }, Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> {
                return !(TowerChallenge.me.getChallengeManager().getPlayerTeam(offlinePlayer) instanceof GodTeam);
            }).sorted(Comparator.comparingInt(value -> ((OfflinePlayer) value).getStatistic(Statistic.JUMP)).reversed()).collect(Collectors.toList()),
                    (player1, offlinePlayer) -> {}, new ButtonElement(ButtonElement.backItem(), this::openInventory));
            gui.openInventory(player);
        });
        addElement(jumpsElement);

        ItemStack distanceItem = new ItemStack(Material.LEATHER_BOOTS);
        ItemMeta distanceMeta = distanceItem.getItemMeta();
        distanceMeta.displayName(TextUtil.noItalic("Distance Travelled"));
        distanceItem.setItemMeta(distanceMeta);
        ButtonElement distanceElement = new ButtonElement(distanceItem, player -> {
            PlayerGui gui = new PlayerGui(Component.text("Distance Travelled:"), new ItemStack(Material.PLAYER_HEAD), player1 -> {
                int distance = 0;
                for (Statistic statistic : distanceStats) {
                    distance += player1.getStatistic(statistic);
                }
                return TextUtil.formatTexts(Component.text("Distance: ").append(Component.text(String.format("%.2fm", ((double) distance)/100))));
            }, Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> {
                return !(TowerChallenge.me.getChallengeManager().getPlayerTeam(offlinePlayer) instanceof GodTeam);
            }).sorted(Comparator.comparingInt(value -> {
                int distance = 0;
                for (Statistic statistic : distanceStats) {
                    distance += ((OfflinePlayer) value).getStatistic(statistic);
                }
                return distance;
            }).reversed()).collect(Collectors.toList()),
                    (player1, offlinePlayer) -> {}, new ButtonElement(ButtonElement.backItem(), this::openInventory));
            gui.openInventory(player);
        });
        addElement(distanceElement);

        ItemStack bellsItem = new ItemStack(Material.BELL);
        ItemMeta bellsMeta = bellsItem.getItemMeta();
        bellsMeta.displayName(TextUtil.noItalic("Bells Rung"));
        bellsItem.setItemMeta(bellsMeta);
        ButtonElement bellsElement = new ButtonElement(bellsItem, player -> {
            PlayerGui gui = new PlayerGui(Component.text("Bells Rung:"), new ItemStack(Material.PLAYER_HEAD), player1 -> {
                return TextUtil.formatTexts(Component.text("Bells Rung: ").append(Component.text(player1.getStatistic(Statistic.BELL_RING))));
            }, Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> {
                return !(TowerChallenge.me.getChallengeManager().getPlayerTeam(offlinePlayer) instanceof GodTeam);
            }).sorted(Comparator.comparingInt(value -> ((OfflinePlayer) value).getStatistic(Statistic.BELL_RING)).reversed()).collect(Collectors.toList()),
                    (player1, offlinePlayer) -> {}, new ButtonElement(ButtonElement.backItem(), this::openInventory));
            gui.openInventory(player);
        });
        addElement(bellsElement);

        ItemStack snowballItem = new ItemStack(Material.SNOWBALL);
        ItemMeta snowballMeta = snowballItem.getItemMeta();
        snowballMeta.displayName(TextUtil.noItalic("Snowballs Picked Up"));
        snowballItem.setItemMeta(snowballMeta);
        ButtonElement snowballElement = new ButtonElement(snowballItem, player -> {
            PlayerGui gui = new PlayerGui(Component.text("Snowballs Picked Up:"), new ItemStack(Material.PLAYER_HEAD), player1 -> {
                return TextUtil.formatTexts(Component.text("Snowballs: ").append(Component.text(player1.getStatistic(Statistic.PICKUP, Material.SNOWBALL))));
            }, Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> {
                return !(TowerChallenge.me.getChallengeManager().getPlayerTeam(offlinePlayer) instanceof GodTeam);
            }).sorted(Comparator.comparingInt(value -> ((OfflinePlayer) value).getStatistic(Statistic.PICKUP, Material.SNOWBALL)).reversed()).collect(Collectors.toList()),
                    (player1, offlinePlayer) -> {}, new ButtonElement(ButtonElement.backItem(), this::openInventory));
            gui.openInventory(player);
        });
        addElement(snowballElement);

        ItemStack waterItem = new ItemStack(Material.WATER_BUCKET);
        ItemMeta waterMeta = waterItem.getItemMeta();
        waterMeta.displayName(TextUtil.noItalic("Time Spent in Splash Zone"));
        waterItem.setItemMeta(waterMeta);
        ButtonElement waterElement = new ButtonElement(waterItem, player -> {
            PlayerGui gui = new PlayerGui(Component.text("Splash Time:"), new ItemStack(Material.PLAYER_HEAD), player1 -> {
                return TextUtil.formatTexts(Component.text("Time: ").append(Component.text(SpoutManager.getRegionTime(player1).toSeconds()).append(Component.text(" seconds"))));
            }, Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> {
                return !(TowerChallenge.me.getChallengeManager().getPlayerTeam(offlinePlayer) instanceof GodTeam);
            }).sorted(Comparator.comparingLong(value -> (SpoutManager.getRegionTime(((OfflinePlayer) value)).toSeconds())).reversed()).collect(Collectors.toList()),
                    (player1, offlinePlayer) -> {}, new ButtonElement(ButtonElement.backItem(), this::openInventory));
            gui.openInventory(player);
        });
        addElement(waterElement);

        ItemStack cookieItem = new ItemStack(Material.COOKIE);
        ItemMeta cookieMeta = cookieItem.getItemMeta();
        cookieMeta.displayName(TextUtil.noItalic("Portal Cookies Eaten"));
        cookieItem.setItemMeta(cookieMeta);
        ButtonElement cookieElement = new ButtonElement(cookieItem, player -> {
            PlayerGui gui = new PlayerGui(Component.text("Cookies Eaten:"), new ItemStack(Material.PLAYER_HEAD), player1 -> {
                return TextUtil.formatTexts(Component.text("Cookies: ").append(Component.text(FastTravelListener.getTeleportCount(player1))));
            }, Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> {
                return !(TowerChallenge.me.getChallengeManager().getPlayerTeam(offlinePlayer) instanceof GodTeam);
            }).sorted(Comparator.comparingInt(value -> FastTravelListener.getTeleportCount(((OfflinePlayer) value))).reversed()).collect(Collectors.toList()),
                    (player1, offlinePlayer) -> {}, new ButtonElement(ButtonElement.backItem(), this::openInventory));
            gui.openInventory(player);
        });
        addElement(cookieElement);


    }



}
