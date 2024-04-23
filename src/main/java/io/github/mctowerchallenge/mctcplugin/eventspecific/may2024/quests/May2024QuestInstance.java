package io.github.mctowerchallenge.mctcplugin.eventspecific.may2024.quests;

import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.quest.instance.QuestInstance;
import io.github.mctowerchallenge.mctcplugin.quest.instance.QuestInteractRegion;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mctowerchallenge.mctcplugin.utility.MVPortalUtils;
import io.github.mctowerchallenge.mctcplugin.utility.TeamUtils;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.mystigui.page.PresetGui;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class May2024QuestInstance extends QuestInstance implements Openable {

    public static final String TEMPLATE_EXIT_NAME = "May2024_fromQuest";
    public static final Location[] TEMPLATE_EXIT_PORTAL = new Location[]{
            new Location(Worlds.May2024_quest(), 10, 65, -19),
            new Location(Worlds.May2024_quest(), 10, 67, -16)
    };

    private final Plugin plugin;

    public May2024QuestInstance(Plugin plugin, TeamManager teamManager, int teamId, Location instanceLocation) {
        super(teamManager, teamId, May2024QuestManager.TemplateBounds[0], instanceLocation);
        this.plugin = plugin;

        TowerTeam team = getTeam();

        if (team != null) {
            MVPortalUtils.initPortal(TeamUtils.toTeamTag(team, TEMPLATE_EXIT_NAME), new Location[]{
                    offsetLocation(TEMPLATE_EXIT_PORTAL[0]),
                    offsetLocation(TEMPLATE_EXIT_PORTAL[1])
            }, May2024QuestManager.EXIT_LOCATION);
        }

        QuestInteractRegion questRegion = new QuestInteractRegion(plugin, this, May2024QuestManager.TemplateBounds, "oct2023_quest");
    }

    /**
     * Teleports the given entity to the instance's entrance.
     *
     * @param entity The entity to teleport.
     */
    public void enterTeleport(Entity entity) {
        entity.teleport(offsetLocation(May2024QuestManager.TEMPLATE_ENTER_LOCATION));
    }

    @Override
    public Gui getGui(Player player) {
        PresetGui gui = new PresetGui(plugin, Component.text("Instance Utilities"), 3);

        gui.placeElement(2, 1, new ButtonElement(GuiUtil.formatItem("Teleport", Material.COMPASS, 0), this::enterTeleport));

        return gui;
    }
}
