package io.github.mystievous.towerchallenge.misc;

import io.github.mystievous.towerchallenge.DefaultFontInfo;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BroadcastCommand implements CommandExecutor {

    private final ChallengeManager manager;

    public BroadcastCommand(ChallengeManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(CommandUtils.errorMessage("Please enter a message to broadcast!"));
            return true;
        }

        manager.getTowerListener().getGodTeam().getAudience().sendMessage(
                Component.text(sender.getName()).color(TowerChallenge.PRIMARY_COLOR).decoration(TextDecoration.ITALIC, true)
                        .append(Component.text(" has sent an announcement!")
                                .color(TowerChallenge.SECONDARY_COLOR)
                                .decoration(TextDecoration.ITALIC, false))
        );

        ComponentBuilder<TextComponent, TextComponent.Builder> message = Component.text();

        // \uF801 is the -1 character width from the resource pack
        message.append(Component.text("------ \uF801ANNOUNCEMENT\uF801 ------").color(TowerChallenge.PRIMARY_COLOR))
                .append(Component.text("\n\n"));

        int pixelCount = 0;
        for (String word : args) {
            int wordPixels = DefaultFontInfo.getStringLength(word + " ");
            pixelCount += wordPixels;
            if (pixelCount >= 245) {
                message.append(Component.newline());
                pixelCount = wordPixels;
            }
            message.append(Component.text(word)).append(Component.space());
        }

        message.append(Component.text("\n\n")).append(Component.text("-------------------------").color(TowerChallenge.PRIMARY_COLOR));

        Bukkit.getServer().sendMessage(message.build());
        Title title = Title.title(Component.text("ANNOUNCEMENT").color(TowerChallenge.PRIMARY_COLOR), Component.text("An event announcement has been posted in chat!").color(NamedTextColor.WHITE));
//        Title title = Title.title(Component.empty(), Component.text("An event announcement has been posted in chat!"));
        Bukkit.getServer().showTitle(title);
        Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.chime"), Sound.Source.MASTER, 100, 1f));
        Bukkit.getServer().playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.chime"), Sound.Source.MASTER, 100, 0.6f));

        return true;
    }

}
