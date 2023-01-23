package io.github.mystievous.towerchallenge.towering;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Team;

public class ChatHandler implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        Team playerTeam = Bukkit.getServer().getScoreboardManager().getMainScoreboard().getPlayerTeam(event.getPlayer());

        if (event.isCancelled()) {
            return;
        }

        Component prefix = Component.empty();
        Component name = Component.text(String.format("<%s> ", event.getPlayer().getName()));
        Component body = event.message().replaceText(TextReplacementConfig.builder().match(":benbyyFire:").replacement("\uE100").build())
                .replaceText(TextReplacementConfig.builder().match(":benbyyPog:").replacement("\uE101").build())
                .replaceText(TextReplacementConfig.builder().match(":LoveFonda:").replacement("\uE102").build())
                .replaceText(TextReplacementConfig.builder().match(":LaFlameda:").replacement("\uE103").build())
                .replaceText(TextReplacementConfig.builder().match(":eyes:").replacement("\uE104").build());

        if (playerTeam != null) {
            prefix = playerTeam.prefix();
        }

        Component message = prefix.append(name).append(body);
        Component dayBotMessage = null;

        String stringBody = PlainTextComponentSerializer.plainText().serialize(body);
        String[] splitBody = stringBody.toLowerCase().split(" ");
        switch (splitBody[0]) {
            case ("teehee") -> dayBotMessage = Component.text("teehee");
            case ("soup") -> dayBotMessage = Component.text("good soup");
            case ("so") -> {
                if (splitBody[1] != null && splitBody[1].equals("true")) {
                    dayBotMessage = Component.text("so true bestie");
                }
            }
            case ("smooch?"), ("!smooch"), ("smooch") -> {
                // $(eval if(decodeURIComponent("$(user)") == "cptkapn"){ "Yes 😘"}
                // else {e = Math.floor(Math.random() * 10);
                // if (e == 1){"Just this once, $(user) 😘"}
                // else {"$(user), no way. I'm Kapn's only"}})
                int e = (int) Math.floor(Math.random() * 10);
                if (e == 1) {
                    dayBotMessage = Component.text("Just this once, " + event.getPlayer().getName() + " ;)");
                } else {
                    dayBotMessage = Component.text(event.getPlayer().getName() + ", no way. I'm Kapn's only");
                }
            }
            case ("rip") -> {
                ComponentBuilder<TextComponent, TextComponent.Builder> text = Component.text().append(Component.text("o7"));
                if (splitBody.length > 1) {
                    text.append(Component.text(stringBody.substring(stringBody.indexOf(' '))));
                }
                dayBotMessage = text.build();
            }
            case ("o7") -> dayBotMessage = Component.text("o7");
            case ("daybot") -> dayBotMessage = Component.text("you talking about me?");
            case ("l") -> dayBotMessage = Component.text("that sucks man");
            case ("imagine") -> dayBotMessage = Component.text("imagine");
            case ("chomp") -> dayBotMessage = Component.text("nom nom nom");
            case ("bruh") -> dayBotMessage = Component.text("this is a certified bruh moment");
            case ("^") -> dayBotMessage = Component.text("^");
            case ("!arson"), ("arson") -> dayBotMessage = Component.text("haha yea");
            case ("!barson"), ("barson") -> dayBotMessage = Component.text("baha yea");
            case ("!carson"), ("carson") -> dayBotMessage = Component.text("committing haha yea");
            case ("fr") -> dayBotMessage = Component.text("frfr");
            case ("frfr") -> dayBotMessage = Component.text("fr");
            case ("smh") -> dayBotMessage = Component.text("smh my head");
            case ("creeper"), ("!creeper") -> dayBotMessage = Component.text("Awwwww man SSSsss");
            case ("!bonk"), ("bonk") -> {
                if (splitBody[1] != null) {
                    dayBotMessage = Component.text(event.getPlayer().getName() + " has bonked " + splitBody[1]);
                } else {
                    dayBotMessage = Component.text("bonk");
                }
            }
            case ("!hug") -> {
                if (splitBody[1] != null) {
                    dayBotMessage = Component.text(event.getPlayer().getName() + " is hugging " + splitBody[1] + "! How sweet!");
                }
            }
        }

        for (Audience audience : event.viewers()) {
            audience.sendMessage(message);
            if (dayBotMessage != null) {
                Component finalDayBotMessage = dayBotMessage;
                Thread t=new Thread(() -> {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    audience.sendMessage(Component.text()
                            .append(Component.text("["))
                            .append(Component.text("God").color(TextColor.fromHexString("#F7E983")))
                            .append(Component.text("] "))
                            .append(Component.text("<Daybot> "))
                            .append(finalDayBotMessage));
                });
                t.start();
            }
        }

        event.setCancelled(true);

    }

}
