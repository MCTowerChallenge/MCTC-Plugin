package io.github.mystievous.towerchallenge.eventspecific.oct2023.quest;

import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.mystigui.page.PresetGui;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.quest.instance.QuestInstance;
import io.github.mystievous.towerchallenge.quest.instance.QuestRegion;
import io.github.mystievous.towerchallenge.team.TeamManager;
import io.github.mystievous.towerchallenge.team.TowerTeam;
import io.github.mystievous.towerchallenge.utility.MVPortalUtils;
import io.github.mystievous.towerchallenge.utility.TeamUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Represents an instance of the October 2023 quest.
 */
public class Oct2023QuestInstance extends QuestInstance implements Openable {

    public static final String TEMPLATE_EXIT_NAME = "Oct2023_fromQuest";
    public static final Location[] TEMPLATE_EXIT_PORTAL = new Location[]{
            new Location(Worlds.Oct2023_quest(), -17, 65, -20),
            new Location(Worlds.Oct2023_quest(), -16, 67, -20)
    };

    public static final String TEMPLATE_PARKOUR_NAME = "Oct2023_toParkour";
    public static final Location[] TEMPLATE_PARKOUR_PORTAL = new Location[]{
            new Location(Worlds.Oct2023_quest(), -24, 73, -6),
            new Location(Worlds.Oct2023_quest(), -24, 75, -5)
    };
    public static final Location TEMPLATE_PARKOUR_LEVER = new Location(Worlds.Oct2023_quest(), -17, 71, -45);

    public static final String TEMPLATE_TRIVIA_NAME = "Oct2023_toTrivia";
    public static final Location[] TEMPLATE_TRIVIA_PORTAL = new Location[]{
            new Location(Worlds.Oct2023_quest(), -9, 73, -6),
            new Location(Worlds.Oct2023_quest(), -9, 75, -5)
    };
    public static final Location[] TEMPLATE_TRIVIA_BOUNDS = new Location[]{
            new Location(Worlds.Oct2023_quest(), -34, 65, -12),
            new Location(Worlds.Oct2023_quest(), -27, 132, -2)
    };

    private final Plugin plugin;

    public Oct2023QuestInstance(Plugin plugin, TeamManager teamManager, int teamId, Location instanceLocation) {
        super(teamManager, teamId, Oct2023QuestManager.TemplateBounds[0], instanceLocation);
        this.plugin = plugin;

        TowerTeam team = getTeam();

        if (team != null) {
            MVPortalUtils.initPortal(TeamUtils.toTeamTag(team, TEMPLATE_EXIT_NAME), new Location[]{
                    offsetLocation(TEMPLATE_EXIT_PORTAL[0]),
                    offsetLocation(TEMPLATE_EXIT_PORTAL[1])
            }, Oct2023QuestManager.EXIT_LOCATION);

            MVPortalUtils.initPortal(TeamUtils.toTeamTag(team, TEMPLATE_PARKOUR_NAME), new Location[]{
                    offsetLocation(TEMPLATE_PARKOUR_PORTAL[0]),
                    offsetLocation(TEMPLATE_PARKOUR_PORTAL[1])
            }, offsetLocation(Parkour.TEMPLATE_ENTER_LOCATION));

            MVPortalUtils.initPortal(TeamUtils.toTeamTag(team, TEMPLATE_TRIVIA_NAME), new Location[]{
                    offsetLocation(TEMPLATE_TRIVIA_PORTAL[0]),
                    offsetLocation(TEMPLATE_TRIVIA_PORTAL[1])
            }, offsetLocation(Trivia.TEMPLATE_ENTER_LOCATION));
        }

        QuestRegion questRegion = new QuestRegion(plugin, this, Oct2023QuestManager.TemplateBounds, "oct2023_quest");
        QuestInteractRegion parkourLeverRegion = new QuestInteractRegion(plugin, this, new Location[]{TEMPLATE_PARKOUR_LEVER,TEMPLATE_PARKOUR_LEVER}, "oct2023_parkour_checkpoint");
        QuestInteractRegion triviaRegion = new QuestInteractRegion(plugin, this, TEMPLATE_TRIVIA_BOUNDS, "oct2023_trivia_region");


        Parkour parkour = new Parkour(this);
        Bukkit.getPluginManager().registerEvents(parkour, plugin);
        Trivia trivia = new Trivia(this, plugin);

    }

    /**
     * Teleports the given entity to the instance's entrance.
     *
     * @param entity The entity to teleport.
     */
    public void enterTeleport(Entity entity) {
        entity.teleport(offsetLocation(Oct2023QuestManager.TEMPLATE_ENTER_LOCATION));
    }

    @Override
    public Gui getGui(Player player) {
        PresetGui gui = new PresetGui(plugin, Component.text("Instance Utilities"), 3);

        gui.placeElement(2, 1, new ButtonElement(GuiUtil.formatItem("Teleport", Material.COMPASS, 0), this::enterTeleport));

        return gui;
    }
}
