package io.github.mystievous.towerchallenge.eventspecific.apr2023.quests;

import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Apr2023QuestManager implements Listener {

    public static final CommandSender sender = Bukkit.createCommandSender(component -> {});
    public static final String REMOVE_TAG = "apr2023-remove";
    public static final String INVITE_BOOK = "H4sIAAAAAAAA/z1RzU5aQRQ+1FKR6rbr09mYGE2qJiTeXSvUkKgloBhTjBnhSE8YZm5mzqUlBJesTVd9A96DR+FJOnDVWX7/OVMG2ICPVS26TT6wswA7WyV4xz34NGRLXa8fJfntWYTs/YNzgzJsiO5vQTHVfQolACgUqhMl9EdUohAX88re02Levf1xvdusYf2yXb+qVSP6tNexuOK/nJMEPCPBhpNYSb3PHduxi/lRK7MDsthKORYGvNIj8mvmOBprOgg2OULYZmNi+4o5Wc7+4nfPPT3ex6+pZ4NHh1G4nD2v62LfYSUdojiM6Z4DrbpyorL8N1vtOSVDD14L2/4L2rE51f2WiWBLtDFx6G7AG7avxjzlJSLCF9rTQVz1FpEL8hclzVa7kaxdi7kNQiPCMKAYbPvirJqWYbPHITV6XIL3l3pIUJmoeFWvVfJzojiO4K5KHrUJtP9677odsZCa3r0hagol+KAz+eU8lC/GQZhGLos/VRQWQ7Dd0F7GmDuhAMVTl1kpwH+io5i3CwIAAA==";

    private static final Map<Integer, Location> teamLocations = new HashMap<>() {{
        put(1, Apr2023QuestInstance.baseLocation); // God
        put(2, new Location(Worlds.Apr2023_quest(), 101, 65, 32));	// (Red)
        put(4, new Location(Worlds.Apr2023_quest(), 201, 64, 32));	// (Yellow)
        put(8, new Location(Worlds.Apr2023_quest(), 301, 64, 32));	// Light (Blue)
        put(9, new Location(Worlds.Apr2023_quest(), 401, 64, 32));	// (Blue)
        put(11, new Location(Worlds.Apr2023_quest(), 501, 64, 32));	// (Magenta)
        put(14, new Location(Worlds.Apr2023_quest(), 601, 64, 32));	// Light (Gray)
        put(16, new Location(Worlds.Apr2023_quest(), 701, 64, 32));	// (Black)
    }};

    private final TeamManager teamManager;
    private final Map<Integer, Apr2023QuestInstance> questInstances;


    public Apr2023QuestManager(Plugin plugin, QuestManager questManager, TeamManager teamManager) {
        this.teamManager = teamManager;

        unloadAll();

        questInstances = new HashMap<>();
        for (Map.Entry<Integer, Location> entry : teamLocations.entrySet()) {
            questInstances.put(entry.getKey(), new Apr2023QuestInstance(plugin, questManager, teamManager, teamManager.getTeam(entry.getKey()), entry.getValue()));
        }

    }

    public @Nullable Apr2023QuestInstance getQuestInstance(TowerTeam team) {
        return questInstances.get(team.getDatabaseId());
    }

    public void unloadAll() {
        List<Entity> entities = Bukkit.selectEntities(sender, String.format("@e[tag=%s]", REMOVE_TAG));
        for (Entity entity : entities) {
            entity.remove();
        }
    }

    public void removeSteve() {
        for (Apr2023QuestInstance instance : questInstances.values()) {
            instance.goodTavern.removeSteve();
        }
    }

    public static final String BASKET = "house-basket";

    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        TowerTeam team = teamManager.getPlayerTeam(player);

        Entity entity = event.getRightClicked();

        if (team != null) {

            if (entity.getScoreboardTags().contains(BASKET)) {

                Component text = Component.newline()
                        .append(Component.text("====================")).append(Component.newline())
                        .append(Component.text("An empty picnic basket.")).append(Component.newline())
                        .append(Component.text("Would you like to SAVE?")).append(Component.newline())
                        .append(Component.newline())
                        .append(Component.newline())
                        .append(Component.text("[YES] ☜")).append(Component.newline())
                        .append(Component.text("[NO]")).append(Component.newline())
                        .append(Component.text("===================="))
                        .append(Component.newline());

                player.sendMessage(text);

            }

        }

    }

}
