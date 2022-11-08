package io.github.idkahn.towerchallenge.halloween.steve;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class SteveManager {

    private EventManager eventManager;
    private SteveListener steveListener;
    private SteveCommands steveCommands;
    private YamlConfiguration config;
    private Dialogue firstDialogue;
    private MapView riddleMapView;

    public SteveManager(EventManager eventManager) {
        this.eventManager = eventManager;
        this.steveListener = new SteveListener(this);
        this.steveCommands = new SteveCommands(this);
        eventManager.getPlugin().getCommand("steve").setExecutor(steveCommands);
        loadSteve();
        World world = Bukkit.getServer().getWorld("December MCTC");

        File riddleFile = new File(eventManager.getPlugin().getDataFolder(), "riddle.png");
        BufferedImage riddle = null;
        try {
            riddle = ImageIO.read(riddleFile);
        } catch (IOException e) {
            Bukkit.getLogger().warning("Steve image failed to load!");
        }

        riddleMapView = Bukkit.getServer().createMap(world);
        riddleMapView.setScale(MapView.Scale.FARTHEST);

        riddleMapView.getRenderers().clear();
        BufferedImage finalRiddle = riddle;
        riddleMapView.addRenderer(new MapRenderer() {
            @Override
            public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
                //code goes here

                if (finalRiddle != null) {
                    canvas.drawImage(0, 0, finalRiddle);
                }

                // to draw text you do this
//                canvas.drawText(x, y, MinecraftFont.Font, "any text here or a string");
            }
        });
    }

    public void loadSteve() {
        config = YamlConfiguration.loadConfiguration(TowerChallenge.steveConfigFile);

        Dialogue previousDialogue = null;
        List<Map<?, ?>> list = config.getMapList("Dialogue");
        for (Map<?, ?> map : list) {
            List<Map<?, ?>> configMessage = (List<Map<?, ?>>) map.get("message");
            int configDelay = (int) map.get("delay");

            ComponentBuilder message = Component.text().color(NamedTextColor.GRAY);

            for (Map<?, ?> text : configMessage) {
                Object italics = text.get("italics");
                ComponentBuilder builder = Component.text();
                builder.append(Component.text((String) text.get("text")));
                if (italics != null) {
                    builder.decoration(TextDecoration.ITALIC, (boolean) italics);
                }
                message.append(builder.build());
            }

            //            Bukkit.getLogger().info(PlainTextComponentSerializer.plainText().serialize(message.build()));
//            Bukkit.getLogger().info(Long.toString(delay));

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

    public ItemStack getMap() {
        ItemStack map = new ItemStack(Material.MAP);
        MapMeta mapMeta = (MapMeta) map.getItemMeta();
        mapMeta.setMapView(riddleMapView);
        mapMeta.displayName(Component.text("steve's riddle").decoration(TextDecoration.ITALIC, false));
        map.setItemMeta(mapMeta);
        return map;
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
