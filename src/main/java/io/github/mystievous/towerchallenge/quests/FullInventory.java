package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.eventspecific.feb2023.ValentinesUtil;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.ListGui;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import io.github.mystievous.towerchallenge.utility.TextUtil;
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

public class FullInventory implements CommandExecutor {

    private final static HashMap<UUID, Collection<ItemStack>> playerItems = new HashMap<>();

    private static Collection<ItemStack> getPlayerItems(Player player) {
        return playerItems.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    public static void setPlayerItems(Player player, Collection<ItemStack> items) {
        playerItems.put(player.getUniqueId(), items);
    }

    public static void addPlayerItems(Player player, Collection<ItemStack> items) {
        Collection<ItemStack> currentItems = getPlayerItems(player);
        currentItems.addAll(items);
        playerItems.put(player.getUniqueId(), currentItems);
    }

    public static boolean giveExcessItems(Player player) {
        Collection<ItemStack> currentItems = getPlayerItems(player);
        HashMap<Integer, ItemStack> leftOvers = player.getInventory().addItem(currentItems.toArray(ItemStack[]::new));
        setPlayerItems(player, leftOvers.values());
        return leftOvers.isEmpty();
    }

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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {

            if (args.length > 0 && player.hasPermission("towerchallenge.fullinv.other")) {
                Player targetPlayer = Bukkit.getPlayer(args[0]);
                if (targetPlayer != null) {
                    ListGui viewInventory = new ListGui(Component.text(String.format("%s's Extra Items", targetPlayer.getName())), Element.empty());
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
