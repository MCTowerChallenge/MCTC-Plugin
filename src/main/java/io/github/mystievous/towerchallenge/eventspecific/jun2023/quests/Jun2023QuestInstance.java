package io.github.mystievous.towerchallenge.eventspecific.jun2023.quests;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.mystigui.page.PresetGui;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.eventspecific.apr2023.quests.*;
import io.github.mystievous.towerchallenge.gui.Icons;
import io.github.mystievous.towerchallenge.quests.QuestItems;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.quests.instances.QuestInstance;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class Jun2023QuestInstance extends QuestInstance implements Openable {

    public static final String ENTER_DOOR = "cave-enter-door";

    /**
     * Anchor block for the instance.
     * <p></p>
     * The bottom half of the spruce door entrance
     * to the first room.
     */
    public static final Location baseLocation = new Location(Worlds.Jun2023_quest(), -2, 65, -3);

    private final Plugin plugin;
    private final QuestManager questManager;
    private final TeamManager teamManager;

    private final Noteblocks noteblocks;
    private final SimonSays simonSays;

    public Jun2023QuestInstance(Plugin plugin, QuestManager questManager, TeamManager teamManager, TowerTeam team, Location instanceLocation) {
        super(team, baseLocation, instanceLocation);
        this.plugin = plugin;
        this.questManager = questManager;
        this.teamManager = teamManager;

        this.noteblocks = new Noteblocks(plugin, this);
        this.simonSays = new SimonSays(plugin, this);
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

    @Override
    public Gui getGui(Player player) {
        PresetGui gui = new PresetGui(plugin, Component.text("Instance Utilities"), 3);

        gui.placeElement(1, 1, new ButtonElement(GuiUtil.formatItem("Open Noteblock Door", Material.NOTE_BLOCK, 0), player1 -> noteblocks.openDoor()));

        gui.placeElement(3, 1, new ButtonElement(GuiUtil.formatItem("Close Noteblock Door", Material.BEDROCK, 0), player1 -> noteblocks.closeDoor()));

        gui.placeElement(1, 3, new ButtonElement(GuiUtil.formatItem("Complete Simon Says", Material.REDSTONE_BLOCK, 0), player1 -> simonSays.completeRoom()));
        gui.placeElement(3, 3, new ButtonElement(GuiUtil.formatItem("Reset Simon Says", Material.BEDROCK, 0), player1 -> simonSays.reset()));

        return gui;
    }
}
