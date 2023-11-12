package io.github.mctowerchallenge.towerchallenge.quest.util;

import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.ListGui;
import io.github.mctowerchallenge.towerchallenge.TowerChallenge;
import io.github.mctowerchallenge.towerchallenge.utility.CommandUtils;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Utility class for managing player inventories with excess items.
 */
public class FullInventory implements CommandExecutor {

    private final static HashMap<UUID, Collection<ItemStack>> playerItems = new HashMap<>();

    /**
     * Gets all the currently stored items for the given player.
     *
     * @param player The player to check.
     * @return The items for the player.
     */
    private static Collection<ItemStack> getPlayerItems(Player player) {
        return playerItems.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    /**
     * Overrides and sets the current stored collection of items for the player.
     *
     * @param player The player to set.
     * @param items The new items.
     */
    private static void setPlayerItems(Player player, Collection<ItemStack> items) {
        playerItems.put(player.getUniqueId(), items);
    }

    /**
     * Appends the current list of items for the player with the items given.
     *
     * @param player The player to add items to.
     * @param items The items to add.
     */
    private static void addPlayerItems(Player player, Collection<ItemStack> items) {
        Collection<ItemStack> currentItems = getPlayerItems(player);
        currentItems.addAll(items);
        playerItems.put(player.getUniqueId(), currentItems);
    }

    /**
     * Will do the same as {@link #givePlayerItems(Player, ItemStack...)},
     * but with the stored items that were previously unable to be given,
     * attempting to give as many as possible.
     *
     * @param player The player to give items to.
     * @return Whether there are still leftover items.
     */
    public static boolean giveExcessItems(Player player) {
        Collection<ItemStack> currentItems = getPlayerItems(player);
        HashMap<Integer, ItemStack> leftOvers = player.getInventory().addItem(currentItems.toArray(ItemStack[]::new));
        setPlayerItems(player, leftOvers.values());
        return leftOvers.isEmpty();
    }

    /**
     * Attempts to give a player the items specified,
     * and stores them here if there is not enough space.
     * <p></p>
     * This should be used any time a player is being given
     * items, rather than directly adding them to their inventory.
     *
     * @param player The player to give items.
     * @param items  The items to give.
     */
    public static void givePlayerItems(Player player, ItemStack... items) {
        HashMap<Integer, ItemStack> hashMap = player.getInventory().addItem(items);

        if (!hashMap.isEmpty()) {
            FullInventory.addPlayerItems(player, hashMap.values());
            player.sendMessage(CommandUtils.errorMessage("Your inventory is full!"));
            player.sendMessage(CommandUtils.errorMessage("Excess items can be retrieved with ")
                    .append(Component.text("[/fullinv]").color(NamedTextColor.AQUA)
                            .clickEvent(ClickEvent.suggestCommand("/fullinv"))
                            .hoverEvent(Component.text("Click to run command."))));
        }
    }

    private final TowerChallenge plugin;

    /**
     * Initializes a new instance of the FullInventory class.
     *
     * @param plugin The main TowerChallenge plugin instance.
     */
    public FullInventory(TowerChallenge plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {

            if (args.length > 0 && player.hasPermission("towerchallenge.fullinv.other")) {
                Player targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer != null) {
                    ListGui viewInventory = new ListGui(plugin, Component.text(String.format("%s's Extra Items", targetPlayer.getName())), Element.blank());
                    for (ItemStack item : getPlayerItems(targetPlayer)) {
                        ButtonElement buttonElement = new ButtonElement(item, player1 -> {
                            player1.getInventory().addItem(item);
                            Collection<ItemStack> newItems = getPlayerItems(targetPlayer);
                            newItems.remove(item);
                            setPlayerItems(targetPlayer, newItems);
                        });
                        viewInventory.addElement(buttonElement);
                    }
                } else {
                    player.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
                }
            } else {
                if (giveExcessItems(player)) {
                    player.sendMessage(TextUtil.formatText("All remaining items given!"));
                } else {
                    player.sendMessage(CommandUtils.errorMessage("Could not give all items, some remain in storage."));
                }
            }
        } else {
            sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
        }
        return true;
    }
}
