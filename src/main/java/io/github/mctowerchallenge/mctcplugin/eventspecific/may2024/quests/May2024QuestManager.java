package io.github.mctowerchallenge.mctcplugin.eventspecific.may2024.quests;

import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.eventspecific.jan2024.NewYearsBall;
import io.github.mctowerchallenge.mctcplugin.eventspecific.jan2024.quests.NPCMaps;
import io.github.mctowerchallenge.mctcplugin.gui.page.TeamGui;
import io.github.mctowerchallenge.mctcplugin.interaction.InteractableTagManager;
import io.github.mctowerchallenge.mctcplugin.interaction.InteractableTaggedEntity;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.character.Erie;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.character.SteveSkellington;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.element.Element;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.mystigui.page.PresetGui;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Manages the specific quest instance for the October 2023 event.
 */
public class May2024QuestManager implements Listener, Openable {

    public static final CommandSender sender = Bukkit.createCommandSender(component -> {
    });
    public static final String REMOVE_TAG = "may2024-remove";

    private final Random random;
    private final Plugin plugin;
    private final TeamManager teamManager;

    /**
     * Creates a May2024QuestManager instance.
     *
     * @param plugin      The plugin instance.
     * @param teamManager The TeamManager instance.
     */
    public May2024QuestManager(Plugin plugin, TeamManager teamManager) {
        this.random = new SecureRandom();
        this.plugin = plugin;
        this.teamManager = teamManager;

        unloadAll();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Unloads all tagged entities with the REMOVE_TAG.
     */
    public void unloadAll() {
        List<Entity> entities = Bukkit.selectEntities(sender, String.format("@e[tag=%s]", REMOVE_TAG));
        for (Entity entity : entities) {
            entity.remove();
        }
    }

    @Override
    public Gui getGui(Player player) {
        return new TeamGui(plugin, Component.text("Which team?"), new ArrayList<>(), teamManager.getAllTeams(), (player1, team) -> {
            PresetGui gui = new PresetGui(plugin, team.getDisplayName().append(Component.text(": Control Panel")), 3);
            gui.openInventory(player1);
        }, Element.blank());
    }

}
