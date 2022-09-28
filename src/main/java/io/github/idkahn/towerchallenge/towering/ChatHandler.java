package io.github.idkahn.towerchallenge.towering;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
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
                .replaceText(TextReplacementConfig.builder().match(":LaFlameda:").replacement("\uE103").build());

        if (playerTeam != null && playerTeam.prefix() != null) {
            prefix = playerTeam.prefix();
        }

        Component message = prefix.append(name).append(body);
        Component nightBotMessage = null;

        String stringBody = PlainTextComponentSerializer.plainText().serialize(body);
        String[] splitBody = stringBody.toLowerCase().split(" ");
//        for (String string : splitBody) {
//            Bukkit.getLogger().info(string);
//        }
        switch(splitBody[0]) {
            case("teehee"):
                nightBotMessage = Component.text("teehee");
                break;
            case("soup"):
                nightBotMessage = Component.text("good soup");
                break;
            case("so"):
                if (splitBody[1] != null && splitBody[1].equals("true")) {
                    nightBotMessage = Component.text("so true bestie");
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
                    nightBotMessage = Component.text("Just this once, " + event.getPlayer().getName() + " ;)");
                } else {
                    nightBotMessage = Component.text(event.getPlayer().getName() + ", no way. I'm Kapn's only");
                }
                break;
            case("rip"):
                nightBotMessage = Component.text("o7 ").append(Component.text(stringBody.substring(stringBody.indexOf(' '))));
                break;
            case("o7"):
                nightBotMessage = Component.text("o7");
                break;
            case("nightbot"):
                nightBotMessage = Component.text("you talking about me?");
                break;
            case("mothman"):
                nightBotMessage = Component.text("https://clips.twitch.tv/SpinelessWonderfulCucumberDuDudu-wjwpYRPlgEY2Scpd").clickEvent(ClickEvent.openUrl("https://clips.twitch.tv/SpinelessWonderfulCucumberDuDudu-wjwpYRPlgEY2Scpd"));
                break;
            case("l"):
                nightBotMessage = Component.text("that sucks man");
                break;
            case("imagine"):
                nightBotMessage = Component.text("imagine");
                break;
            case("chomp"):
                nightBotMessage = Component.text("nom nom nom");
                break;
            case("bruh"):
                nightBotMessage = Component.text("this is a certified bruh moment");
                break;
            case("^"):
                nightBotMessage = Component.text("^");
                break;
            case("!arson"):
            case("arson"):
                nightBotMessage = Component.text("haha yea");
                break;
            case("!barson"):
            case("barson"):
                nightBotMessage = Component.text("baha yea");
                break;
            case("!carson"):
            case("carson"):
                nightBotMessage = Component.text("committing haha yea");
                break;
            case("creeper"):
            case("!creeper"):
                nightBotMessage = Component.text("Awwwww man SSSsss");
                break;
            case("!bonk"):
            case("bonk"):
                if (splitBody[1] != null) {
                    nightBotMessage = Component.text(event.getPlayer().getName() + " has bonked " + splitBody[1]);
                } else {
                    nightBotMessage = Component.text("bonk");
                }
                break;
        }

        for (Audience audience : event.viewers()) {
            audience.sendMessage(message);
            if (nightBotMessage != null) {
                Component finalNightBotMessage = nightBotMessage;
                Thread t=new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        audience.sendMessage(Component.text("<Nightbot> ").append(finalNightBotMessage));
                    }
                });
                t.start();
            }
        }

        event.setCancelled(true);

    }

}
