package io.github.idkahn.towerchallenge.hats;

import io.github.idkahn.towerchallenge.commands.CommandUtils;
import io.github.idkahn.towerchallenge.towering.GodTeam;
import io.github.idkahn.towerchallenge.towering.TowerCommands;
import io.github.idkahn.towerchallenge.towering.TowerListener;
import io.github.idkahn.towerchallenge.towering.ParticipantTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class HatCommands implements CommandExecutor {

    private final TowerListener towerListener;

    public HatCommands(TowerListener towerListener) {
        this.towerListener = towerListener;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("You must be a player to use this command!"));
        }
        assert sender instanceof Player;
        Player player = (Player) sender;
        ParticipantTeam team = towerListener.getPlayerTeam(player);
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("getItem")) {
                if (player.hasPermission("towerchallenge.hat.getitem")) {
                    int customModelData = 0;
                    Color color = null;
                    if (args.length > 1) {
                        try {
                            customModelData = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            player.sendMessage(CommandUtils.errorMessage("Custom Model ID is invalid."));
                        }
                    }
                    if (args.length > 2) {
                        try {
                            color = Color.fromRGB(Integer.parseInt(args[2].replaceAll("#", ""), 16));
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(CommandUtils.errorMessage("Color is an invalid Hex string."));
                        }
                    }
                    ItemStack hat = HatUtil.setHat(new ItemStack(Material.LEATHER_HORSE_ARMOR));
                    LeatherArmorMeta hatMeta = (LeatherArmorMeta) hat.getItemMeta();

                    hatMeta.displayName(Component.text("hat").decoration(TextDecoration.ITALIC, false));
                    hatMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                    hatMeta.addItemFlags(ItemFlag.HIDE_DYE);
                    hatMeta.setCustomModelData(customModelData);
                    hatMeta.addAttributeModifier(
                            Attribute.GENERIC_ARMOR,
                            new AttributeModifier(
                                    UUID.randomUUID(),
                                    "armor",
                                    3.0,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.HEAD
                            )
                    );
                    hatMeta.addAttributeModifier(
                            Attribute.GENERIC_ARMOR_TOUGHNESS,
                            new AttributeModifier(
                                    UUID.randomUUID(),
                                    "armor",
                                    2.0,
                                    AttributeModifier.Operation.ADD_NUMBER,
                                    EquipmentSlot.HEAD
                            )
                    );
                    hatMeta.setColor(color);
                    hat.setItemMeta(hatMeta);
                    player.getInventory().addItem(hat);
                } else {
                    player.sendMessage(TowerCommands.PERMISSION_WARN);
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("hand")) {
                if (player.hasPermission("towerchallenge.hat.hand")) {
                    PlayerInventory inventory = player.getInventory();
                    inventory.setHelmet(inventory.getItemInMainHand());
                } else {
                    player.sendMessage(TowerCommands.PERMISSION_WARN);
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("color")) {
                if (player.hasPermission("towerchallenge.hat.color")) {
                    if (PlainTextComponentSerializer.plainText().serialize(team.getDisplayName()).equals("God")) {
                        String color = null;
                        try {
                            color = args[1];
                            sender.sendMessage("Setting hat color to " + color + "...");
                        } catch (ArrayIndexOutOfBoundsException e) {
                            sender.sendMessage("No color given, setting default...");
                        }
        //                Bukkit.getLogger().info("Setting player color to " + color);
                        ((GodTeam) team).setPlayerHatColor((Player) sender, color);
                    }
                } else {
                    player.sendMessage(TowerCommands.PERMISSION_WARN);
                    return true;
                }
            }
        }
        if (team != null) {
            team.openHatGUI(player);
        } else {
            player.sendMessage(Component.text("You are not assigned a team! Giving default hats.").color(NamedTextColor.DARK_RED));
            TowerListener.defaultHats.openInventory(player);
        }
        return true;
    }
}
