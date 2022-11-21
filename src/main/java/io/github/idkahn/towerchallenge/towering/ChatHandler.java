package io.github.idkahn.towerchallenge.towering;

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
        switch(splitBody[0]) {
            case("teehee"):
                dayBotMessage = Component.text("teehee");
                break;
            case("soup"):
                dayBotMessage = Component.text("good soup");
                break;
            case("so"):
                if (splitBody[1] != null && splitBody[1].equals("true")) {
                    dayBotMessage = Component.text("so true bestie");
                }
                break;
            case("smooch?"):
            case("!smooch"):
            case("smooch"):
                // $(eval if(decodeURIComponent("$(user)") == "cptkapn"){ "Yes 😘"}
                // else {e = Math.floor(Math.random() * 10);
                // if (e == 1){"Just this once, $(user) 😘"}
                // else {"$(user), no way. I'm Kapn's only"}})
                int e = (int) Math.floor(Math.random()*10);
                if (e == 1) {
                    dayBotMessage = Component.text("Just this once, " + event.getPlayer().getName() + " ;)");
                } else {
                    dayBotMessage = Component.text(event.getPlayer().getName() + ", no way. I'm Kapn's only");
                }
                break;
            case("rip"):
                ComponentBuilder<TextComponent, TextComponent.Builder> text = Component.text().append(Component.text("o7"));
                if (splitBody.length > 1) {
                    text.append(Component.text(stringBody.substring(stringBody.indexOf(' '))));
                }
                dayBotMessage = text.build();
                break;
            case("o7"):
                dayBotMessage = Component.text("o7");
                break;
            case("daybot"):
                dayBotMessage = Component.text("you talking about me?");
                break;
//            case("mothman"):
//                dayBotMessage = Component.text("https://clips.twitch.tv/SpinelessWonderfulCucumberDuDudu-wjwpYRPlgEY2Scpd").clickEvent(ClickEvent.openUrl("https://clips.twitch.tv/SpinelessWonderfulCucumberDuDudu-wjwpYRPlgEY2Scpd"));
//                break;
            case("l"):
                dayBotMessage = Component.text("that sucks man");
                break;
            case("imagine"):
                dayBotMessage = Component.text("imagine");
                break;
            case("chomp"):
                dayBotMessage = Component.text("nom nom nom");
                break;
            case("bruh"):
                dayBotMessage = Component.text("this is a certified bruh moment");
                break;
            case("^"):
                dayBotMessage = Component.text("^");
                break;
            case("!arson"):
            case("arson"):
                dayBotMessage = Component.text("haha yea");
                break;
            case("!barson"):
            case("barson"):
                dayBotMessage = Component.text("baha yea");
                break;
            case("!carson"):
            case("carson"):
                dayBotMessage = Component.text("committing haha yea");
                break;
            case("creeper"):
            case("!creeper"):
                dayBotMessage = Component.text("Awwwww man SSSsss");
                break;
            case("!bonk"):
            case("bonk"):
                if (splitBody[1] != null) {
                    dayBotMessage = Component.text(event.getPlayer().getName() + " has bonked " + splitBody[1]);
                } else {
                    dayBotMessage = Component.text("bonk");
                }
                break;
            case("!hug"):
                if (splitBody[1] != null) {
                    dayBotMessage = Component.text(event.getPlayer().getName() + " is hugging " + splitBody[1] + "! How sweet!");
                }
                break;
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
