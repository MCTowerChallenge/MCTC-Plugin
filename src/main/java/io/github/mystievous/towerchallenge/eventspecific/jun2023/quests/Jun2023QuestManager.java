package io.github.mystievous.towerchallenge.eventspecific.jun2023.quests;

import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.gui.page.TeamGui;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.quests.npcs.Sparkle;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jun2023QuestManager implements Listener, Openable {

    public static final CommandSender sender = Bukkit.createCommandSender(component -> {
    });
    public static final String REMOVE_TAG = "jun2023-remove";

    private static final Map<Integer, Location> teamLocations = new HashMap<>() {{
        put(1, Jun2023QuestInstance.baseLocation); // God
    }};

    private final Plugin plugin;
    private final TeamManager teamManager;
    private final Map<Integer, Jun2023QuestInstance> questInstances;


    public Jun2023QuestManager(Plugin plugin, QuestManager questManager, TeamManager teamManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;

        unloadAll();

        questInstances = new HashMap<>();
        for (Map.Entry<Integer, Location> entry : teamLocations.entrySet()) {
            questInstances.put(entry.getKey(), new Jun2023QuestInstance(plugin, questManager, teamManager, teamManager.getTeam(entry.getKey()), entry.getValue()));
        }

        new Sparkle(plugin, new Location(Worlds.Jun2023(), 276, 62.5, -2034.55), 0.3f, 0.45f, 0.01f);

    }

    public @Nullable Jun2023QuestInstance getQuestInstance(TowerTeam team) {
        return questInstances.get(team.getDatabaseId());
    }

    public void unloadAll() {
        List<Entity> entities = Bukkit.selectEntities(sender, String.format("@e[tag=%s]", REMOVE_TAG));
        for (Entity entity : entities) {
            entity.remove();
        }
    }


    @Override
    public Gui getGui(Player player) {
        return new TeamGui(plugin, Component.text("Which team?"), new ArrayList<>(), teamManager.getAllTeams(), (player1, team) -> {
            Jun2023QuestInstance instance = getQuestInstance(team);
            if (instance != null) {
                instance.getGui(player1).openInventory(player1);
            }
        }, Element.blank());
    }
}
