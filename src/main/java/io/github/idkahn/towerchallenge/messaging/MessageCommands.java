package io.github.idkahn.towerchallenge.messaging;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.commands.CommandUtils;
import io.github.idkahn.towerchallenge.towering.GodTeam;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MessageCommands implements CommandExecutor {

    private final EventManager eventManager;

    public MessageCommands(EventManager eventManager) {
        this.eventManager = eventManager;
        eventManager.getPlugin().getCommand("msg").setExecutor(this);
        eventManager.getPlugin().getCommand("msg").setTabCompleter(new MessageTabComplete());
        eventManager.getPlugin().getCommand("godhelp").setExecutor(this);
        eventManager.getPlugin().getCommand("godhelp").setTabCompleter(new MessageTabComplete());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("msg")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
                return true;
            }

            if (args.length < 1) {
                sender.sendMessage(CommandUtils.errorMessage("Please enter a player to message."));
            }

            String[] body = Arrays.copyOfRange(args, 1, args.length);

            TowerTeam senderTeam = eventManager.getTowerListener().getPlayerTeam(player);
            Audience sendFrom;
            if (checkTeam(senderTeam)) {
                sendFrom = senderTeam.getAudience();
            } else {
                if (player.getName().equalsIgnoreCase("Mystievous") || player.getName().equalsIgnoreCase("apple270")) {
                    Player mysti = Bukkit.getPlayer("Mystievous");
                    Player apple = Bukkit.getPlayer("apple270");

                    Set<Player> mystiApple = new HashSet<>();

                    if (mysti != null) {
                        mystiApple.add(mysti);
                    }
                    if (apple != null) {
                        mystiApple.add(apple);
                    }

                    sendFrom = Audience.audience(mystiApple);

                } else {
                    sendFrom = player;
                }
            }

            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                if ((target.getName().equalsIgnoreCase("Mystievous") || target.getName().equalsIgnoreCase("apple270"))) {
                    Player mysti = Bukkit.getPlayer("Mystievous");
                    Player apple = Bukkit.getPlayer("apple270");
                    if (!(player.equals(mysti) || player.equals(apple))) {
                        if (mysti != null && mysti.isOnline()) {
                            send(mysti, formatFromMessage(player, body));
                        }
                        if (apple != null && apple.isOnline()) {
                            send(apple, formatFromMessage(player, body));
                        }
                        sendFrom.sendMessage(formatFromToMessage(player, new HashSet<>(){{
                            add(mysti);
                            add(apple);
                        }}, body));
                        return true;
                    }
                }
                TowerTeam targetTeam = eventManager.getTowerListener().getPlayerTeam(target);
                if (checkTeam(targetTeam)) {
                    send(targetTeam.getAudience(), formatFromMessage(player, body));
                    sendFrom.sendMessage(formatFromToMessage(player, targetTeam.getOnlinePlayers(), body));
                } else {
                    send(target, formatFromMessage(player, body));
                    sendFrom.sendMessage(formatFromToMessage(player, new HashSet<>(){{add(target);}}, body));
                }
            } else {
                sender.sendMessage(CommandUtils.PLAYER_DOES_NOT_EXIST);
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("godhelp")) {

            if (!(sender instanceof Player player)) {
                sender.sendMessage(CommandUtils.SENDER_NOT_PLAYER);
                return true;
            }

            TowerTeam senderTeam = eventManager.getTowerListener().getPlayerTeam(player);
            Audience sendFrom;
            if (checkTeam(senderTeam)) {
                sendFrom = senderTeam.getAudience();
            } else {
                if (senderTeam instanceof GodTeam) {
                    sendFrom = Audience.empty();
                } else {
                    sendFrom = player;
                }
            }

            TowerTeam targetTeam = eventManager.getTowerListener().getGodTeam();

            send(targetTeam.getAudience(), formatFromToGods(player, args));
            sendFrom.sendMessage(formatFromToGods(player, args));
            return true;

        }
        return true;
    }

    private void send(Audience audience, Component message) {
        audience.sendMessage(message);
        audience.playSound(Sound.sound(Key.key(Key.MINECRAFT_NAMESPACE, "block.note_block.chime"), Sound.Source.MASTER, 100, 1.5f));
    }

    private boolean checkTeam(TowerTeam team) {
        return team != null && !(team instanceof GodTeam);
    }

    private Component formatPlayers(Set<Player> players) {

        ComponentBuilder<TextComponent, TextComponent.Builder> output = Component.text();

        Player[] playersArr = new Player[0];
        playersArr = players.toArray(playersArr);

        for (int i = 0; i < playersArr.length; i++) {
            Player recipient = playersArr[i];
            TowerTeam team = eventManager.getTowerListener().getPlayerTeam(recipient);
            Component prefix;



            if (i > 0) {
                if (i > 1) {
                    output.append(Component.text(","));
                }
                if (i == playersArr.length-1) {
                    output.append(Component.text(" and"));
                }
                output.append(Component.space());
            }

            if (team != null) {
                prefix = team.getTeam().prefix();
                output.append(prefix);
            }

            output.append(recipient.name());
        }

        return output.build();

    }

    private Component formatPlayer(Player player) {
        return formatPlayers(new HashSet<>(){{add(player);}});
    }

    private Component formatFromToMessage(Player sender, Set<Player> recipients, String[] body) {
        ComponentBuilder<TextComponent, TextComponent.Builder> message = Component.text().color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true);

        message.append(formatPlayer(sender))
                .append(Component.text(" whispers to "))
                .append(formatPlayers(recipients))
                .append(Component.text(": "));

        for (String string : body) {
            message.append(Component.text(string).append(Component.space()));
        }

        return message.build();
    }

    private Component formatToMessage(Set<Player> recipients, String[] body) {
        ComponentBuilder<TextComponent, TextComponent.Builder> message = Component.text().color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true);

        message.append(Component.text("You whisper to "))
                .append(formatPlayers(recipients))
                .append(Component.text(": "));

        for (String string : body) {
            message.append(Component.text(string).append(Component.space()));
        }

        return message.build();
    }

    private Component formatFromMessage(Player sender, String[] body) {
        ComponentBuilder<TextComponent, TextComponent.Builder> message = Component.text().color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true);

        message.append(formatPlayer(sender))
                .append(Component.text(" whispers to your team: "));

        for (String string : body) {
            message.append(Component.text(string+" "));
        }

        return message.build();
    }

    private Component formatFromToGods(Player sender, String[] body) {
        ComponentBuilder<TextComponent, TextComponent.Builder> message = Component.text().color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true);

        message.append(formatPlayer(sender))
                .append(Component.text(" whispers to the Gods: "));

        for (String string : body) {
            message.append(Component.text(string+" "));
        }

        return message.build();
    }

}
