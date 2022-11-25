package io.github.idkahn.towerchallenge.halloween.steve;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.towering.ParticipantTeam;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SteveManager {

    private final EventManager eventManager;
    private YamlConfiguration config;
    private Dialogue firstDialogue;

    public SteveManager(EventManager eventManager) {
        this.eventManager = eventManager;
//        new SteveListener(this);
        SteveCommands steveCommands = new SteveCommands(this);
        eventManager.getPlugin().getCommand("steve").setExecutor(steveCommands);
        loadSteve();
    }

    public void loadSteve() {
        config = YamlConfiguration.loadConfiguration(TowerChallenge.steveConfigFile);

        Dialogue previousDialogue = null;
        List<Map<?, ?>> list = config.getMapList("Dialogue");
        for (Map<?, ?> map : list) {
            List<Map<?, ?>> configMessage = (List<Map<?, ?>>) map.get("message");
            int configDelay = (int) map.get("delay");

            ComponentBuilder<TextComponent, TextComponent.Builder> message = Component.text().color(NamedTextColor.GRAY);

            for (Map<?, ?> text : configMessage) {
                Object italics = text.get("italics");
                ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();
                builder.append(Component.text((String) text.get("text")));
                if (italics != null) {
                    builder.decoration(TextDecoration.ITALIC, (boolean) italics);
                }
                message.append(builder.build());
            }

            Dialogue thisDialogue = new Dialogue(this, message.build(), configDelay);
            if (previousDialogue == null) {
                firstDialogue = thisDialogue;
                previousDialogue = firstDialogue;
            } else {
                previousDialogue.setNext(thisDialogue);
                previousDialogue = thisDialogue;
            }

        }
        if (previousDialogue != null) {
            previousDialogue.setCallback((player) -> {
                TowerTeam team = getEventManager().getTowerListener().getPlayerTeam(player);
                if (team != null) {
                    setTeamStage(team, 2);
                }
            });
        }
    }

    public void playDialogue(Player player) {
//        audience.sendMessage(Component.text("Hello"));
        firstDialogue.play(player);
    }

    public int getTeamStage(TowerTeam team) {
        return config.getInt("HasFound."+team.getTeam().getName());
    }

    public ItemStack getRiddle() {
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bookMeta = (BookMeta) book.getItemMeta();
        bookMeta.title(Component.text("steve's riddle"));
        bookMeta.author(Component.text("steve skellington"));
        /*
        For the isle of a skull’s, the river parts
        and far beneath the green surface
        lays a treasure of healing hearts.
         */
        bookMeta.pages(Component.text("For the isle of a skull’s, the river parts")
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("and far beneath the green surface"))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("lays a treasure of healing hearts.")));
        book.setItemMeta(bookMeta);
        return book;
    }

    public void setTeamStage(TowerTeam team, int value) {
        config.set("HasFound."+team.getTeam().getName(), value);
        try {
            config.save(TowerChallenge.steveConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        loadSteve();
    }

    public EventManager getEventManager() {
        return eventManager;
    }
}
