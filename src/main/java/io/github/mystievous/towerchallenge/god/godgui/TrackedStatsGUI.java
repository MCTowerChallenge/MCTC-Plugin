package io.github.mystievous.towerchallenge.god.godgui;

import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.ListGui;
import io.github.mystievous.mystigui.page.PlayerGui;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gui.Icons;
import io.github.mystievous.towerchallenge.team.TeamManager;
import io.github.mystievous.towerchallenge.god.GodTeam;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * GUI Showing all preset statistics
 * that are tracked for the event
 */
public class TrackedStatsGUI extends ListGui {

    /**
     * All statistics that record distance
     */
    public static final AbstractStatistic[] distanceStats = new AbstractStatistic[]{
            new AbstractStatistic(Statistic.WALK_ONE_CM),
            new AbstractStatistic(Statistic.WALK_ON_WATER_ONE_CM),
            new AbstractStatistic(Statistic.FALL_ONE_CM),
            new AbstractStatistic(Statistic.CLIMB_ONE_CM),
            new AbstractStatistic(Statistic.FLY_ONE_CM),
            new AbstractStatistic(Statistic.WALK_UNDER_WATER_ONE_CM),
            new AbstractStatistic(Statistic.MINECART_ONE_CM),
            new AbstractStatistic(Statistic.BOAT_ONE_CM),
            new AbstractStatistic(Statistic.PIG_ONE_CM),
            new AbstractStatistic(Statistic.HORSE_ONE_CM),
            new AbstractStatistic(Statistic.SPRINT_ONE_CM),
            new AbstractStatistic(Statistic.CROUCH_ONE_CM),
            new AbstractStatistic(Statistic.AVIATE_ONE_CM),
            new AbstractStatistic(Statistic.SWIM_ONE_CM),
            new AbstractStatistic(Statistic.STRIDER_ONE_CM)
    };

    /**
     * GUI Showing all preset statistics
     * that are tracked for the event
     *
     * @param exitElement Element used to exit the gui
     */
    public TrackedStatsGUI(TowerChallenge plugin, TeamManager teamManager, Element exitElement) {
        super(plugin, Component.text("Pick stat to view"), exitElement);

        ItemStack jumpsItem = new ItemStack(Material.RABBIT_FOOT);
        ItemMeta jumpsMeta = jumpsItem.getItemMeta();
        jumpsMeta.displayName(TextUtil.noItalic("Jumps"));
        jumpsItem.setItemMeta(jumpsMeta);
        ButtonElement jumpsElement = new TrackedStatElement(plugin, teamManager, jumpsItem, new AbstractStatistic(Statistic.JUMP));
        addElement(jumpsElement);

        ItemStack distanceItem = new ItemStack(Material.LEATHER_BOOTS);
        ItemMeta distanceMeta = distanceItem.getItemMeta();
        distanceMeta.displayName(TextUtil.noItalic("Distance Travelled"));
        distanceItem.setItemMeta(distanceMeta);
        ButtonElement distanceElement = new TrackedStatElement(plugin, teamManager, distanceItem, distanceStats);
        addElement(distanceElement);

        ItemStack bellsItem = new ItemStack(Material.BELL);
        ItemMeta bellsMeta = bellsItem.getItemMeta();
        bellsMeta.displayName(TextUtil.noItalic("Bells Rung"));
        bellsItem.setItemMeta(bellsMeta);
        ButtonElement bellsElement = new TrackedStatElement(plugin, teamManager, bellsItem, new AbstractStatistic(Statistic.BELL_RING));
        addElement(bellsElement);

        ItemStack snowballItem = new ItemStack(Material.SNOWBALL);
        ItemMeta snowballMeta = snowballItem.getItemMeta();
        snowballMeta.displayName(TextUtil.noItalic("Snowballs Picked Up"));
        snowballItem.setItemMeta(snowballMeta);
        ButtonElement snowballElement = new TrackedStatElement(plugin, teamManager, snowballItem, new AbstractStatistic(Statistic.PICKUP, Material.SNOWBALL));
        addElement(snowballElement);

    }

    /**
     * ButtonElement representing a statistic,
     * brings the user to a PlayerGui showing
     * the stat for all players
     */
    public class TrackedStatElement extends ButtonElement {

        /**
         * ButtonElement representing a statistic,
         * brings the user to a PlayerGui showing
         * the stat for all players
         */
        public TrackedStatElement(TowerChallenge plugin, TeamManager teamManager, ItemStack icon, AbstractStatistic... statistics) {
            super(icon, playerInGui -> {
                List<OfflinePlayer> players = new ArrayList<>(Arrays.stream(Bukkit.getOfflinePlayers())
                        .filter(offlinePlayer -> !(teamManager.getPlayerTeam(offlinePlayer) instanceof GodTeam))
                        .toList());

                Map<UUID, Integer> playerStats = new HashMap<>();
                for (OfflinePlayer player : players) {
                    int total = 0;
                    for (AbstractStatistic statistic : statistics) {
                        total += statistic.getStatistic(player);
                    }
                    playerStats.put(player.getUniqueId(), total);
                }
                players.sort(Comparator.comparingInt(value -> playerStats.get(((OfflinePlayer) value).getUniqueId())).reversed());

                Component iconName = TextUtil.getItemName(icon);
                PlayerGui gui = new PlayerGui(plugin, iconName,
                        playerOfIcon -> TextUtil.formatTexts(iconName.append(Component.text(": ")).append(Component.text(playerStats.get(playerOfIcon.getUniqueId())))),
                        players,
                        (player1, offlinePlayer) -> {
                        }, new ButtonElement(Icons.backItem(), TrackedStatsGUI.this::openInventory));
                gui.openInventory(playerInGui);
            });
        }

    }

    /**
     * Statistic class that represents
     * any of the Minecraft statistics
     */
    public static class AbstractStatistic {

        private final Statistic statistic;
        private final Material material;
        private final EntityType entityType;

        private AbstractStatistic(Statistic statistic, Material material, EntityType entityType) {
            this.statistic = statistic;
            this.material = material;
            this.entityType = entityType;
        }

        public AbstractStatistic(Statistic statistic, Material material) {
            this(statistic, material, null);
        }

        public AbstractStatistic(Statistic statistic, EntityType entityType) {
            this(statistic, null, entityType);
        }

        public AbstractStatistic(Statistic statistic) {
            this(statistic, null, null);
        }

        /**
         * Gets this statistic for the specified player
         *
         * @param player The player to get the stat of
         * @return The int value of the statistic
         */
        public int getStatistic(@NotNull OfflinePlayer player) {
            if (material != null) {
                return player.getStatistic(statistic, material);
            } else if (entityType != null) {
                return player.getStatistic(statistic, entityType);
            } else {
                return player.getStatistic(statistic);
            }
        }
    }

}
