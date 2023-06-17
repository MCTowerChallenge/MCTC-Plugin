package io.github.mystievous.towerchallenge.eventspecific.jun2023.quests;

import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.mystigui.page.PresetGui;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.eventspecific.jun2023.quests.minesweeper.MineHandler;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.quests.instances.QuestInstance;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class Jun2023QuestInstance extends QuestInstance implements Openable {

    public static final String ENTER_DOOR = "cave-door-enter";
    public static final String EXIT_DOOR = "cave-door-exit";

    /**
     * Anchor block for the instance.
     * <p></p>
     * The block under the spruce door entrance
     * to the first room.
     */
    public static final Location baseLocation = new Location(Worlds.Jun2023_quest(), -2, 64, -3);

    private final Plugin plugin;
    private final QuestManager questManager;
    private final TeamManager teamManager;

    private final Noteblocks noteblocks;
    private final SimonSays simonSays;
    private final MineHandler mineHandler;

    public Jun2023QuestInstance(Plugin plugin, QuestManager questManager, TeamManager teamManager, TowerTeam team, Location instanceLocation) {
        super(team, baseLocation, instanceLocation);
        this.plugin = plugin;
        this.questManager = questManager;
        this.teamManager = teamManager;

        this.noteblocks = new Noteblocks(plugin, this);
        this.simonSays = new SimonSays(plugin, this);
        this.mineHandler = new MineHandler(plugin, this, teamManager);
    }

    public Noteblocks getNoteblocks() {
        return noteblocks;
    }

    public boolean noteblocksCompleted() {
        return noteblocks.isCompleted();
    }

    public boolean simonSaysCompleted() {
        return simonSays.isCompleted();
    }

    public void resetMineSweeper() {
        mineHandler.load();
    }

    public void resetSimonSays() {
        simonSays.reset();
    }

    public void resetNoteBlocks() {
        noteblocks.closeDoor();
    }

    public static final Location BASE_ENTER_LOCATION = new Location(Worlds.Jun2023_quest(), -1.5, 65, -1.5, 0, 0);

    public void enterTeleport(Entity entity) {
        entity.teleport(offsetLocation(BASE_ENTER_LOCATION));
    }

    @Override
    public Gui getGui(Player player) {
        PresetGui gui = new PresetGui(plugin, Component.text("Instance Utilities"), 3);

        gui.placeElement(2, 1, new ButtonElement(GuiUtil.formatItem("Teleport", Material.COMPASS, 0), this::enterTeleport));

        gui.placeElement(1, 3, new ButtonElement(GuiUtil.formatItem("Open Noteblock Door", Material.NOTE_BLOCK, 0), player1 -> noteblocks.openDoor()));

        gui.placeElement(3, 3, new ButtonElement(GuiUtil.formatItem("Close Noteblock Door", Material.BEDROCK, 0), player1 -> noteblocks.closeDoor()));

        gui.placeElement(1, 5, new ButtonElement(GuiUtil.formatItem("Complete Simon Says", Material.REDSTONE_BLOCK, 0), player1 -> simonSays.completeRoom()));
        gui.placeElement(3, 5, new ButtonElement(GuiUtil.formatItem("Reset Simon Says", Material.BEDROCK, 0), player1 -> simonSays.reset()));

        gui.placeElement(1, 7, new ButtonElement(GuiUtil.formatItem("Complete Minesweeper", Material.TNT, 0), player1 -> mineHandler.completeRoom()));
        gui.placeElement(3, 7, new ButtonElement(GuiUtil.formatItem("Reset Minesweeper", Material.BEDROCK, 0), player1 -> mineHandler.load()));

        return gui;
    }
}
