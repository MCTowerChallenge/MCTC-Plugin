package io.github.mctowerchallenge.mctcplugin.eventspecific.jan2024.quests;

import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.Worlds;
import io.github.mctowerchallenge.mctcplugin.eventspecific.jan2024.NewYearsBall;
import io.github.mctowerchallenge.mctcplugin.gui.page.TeamGui;
import io.github.mctowerchallenge.mctcplugin.interaction.InteractableTagManager;
import io.github.mctowerchallenge.mctcplugin.interaction.InteractableTaggedEntity;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.character.*;
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
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.LookClose;
import net.kyori.adventure.key.Key;
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
import java.util.*;

/**
 * Manages the specific quest instance for the October 2023 event.
 */
public class Jan2024QuestManager implements Listener, Openable {

    public static final CommandSender sender = Bukkit.createCommandSender(component -> {
    });
    public static final String REMOVE_TAG = "jan2024-remove";

    public static final String southGearTag = "south-gear";
    public static final String northGearTag = "north-gear";
    public static final String leverTag = "lever";

    private final Random random;
    private final Plugin plugin;
    private final TeamManager teamManager;

    private final NewYearsBall newYearsBall;

    /**
     * Creates a Oct2023QuestManager instance.
     *
     * @param plugin      The plugin instance.
     * @param teamManager The TeamManager instance.
     */
    public Jan2024QuestManager(Plugin plugin, TeamManager teamManager) {
        this.random = new SecureRandom();
        this.plugin = plugin;
        this.teamManager = teamManager;

        unloadAll();

        newYearsBall = new NewYearsBall(plugin, teamManager);

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

    public void loadStartPositions() {
        Worlds.Jan2024().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
        Worlds.Jan2024().setGameRule(GameRule.DO_MOB_SPAWNING, true);
        Worlds.Jan2024().setGameRule(GameRule.DO_WEATHER_CYCLE, true);
        Worlds.Jan2024().setClearWeatherDuration(0);
        for (TowerTeam team : teamManager.getAllTeams()) {
            team.setQuest(QuestTags.NOT_STARTED);
        }
        newYearsBall.reloadBall();
        for (Map.Entry<Class<? extends Trait>, Integer> idEntry : NPCMaps.npcIds.entrySet()) {
            Class<? extends Trait> traitClass = idEntry.getKey();
            NPC npc = CitizensAPI.getNPCRegistry().getById(idEntry.getValue());
            npc.teleport(NPCMaps.preLocations.get(traitClass), PlayerTeleportEvent.TeleportCause.PLUGIN);
            LookClose lookClose = npc.getOrAddTrait(LookClose.class);
            float[] yawRange = NPCMaps.preLookYaws.get(traitClass);
            lookClose.setRandomLookYawRange(yawRange[0], yawRange[1]);

            float[] pitchRange = NPCMaps.preLookPitches.get(traitClass);
            lookClose.setRandomLookPitchRange(pitchRange[0], pitchRange[1]);

            if (NPCMaps.preItems.containsKey(traitClass)) {
                if (npc.getEntity() instanceof LivingEntity livingEntity) {
                    EntityEquipment equipment = livingEntity.getEquipment();
                    if (equipment != null) {
                        equipment.setItemInMainHand(null);
                        equipment.setItemInOffHand(null);
                    }
                }
            }
        }

        for (Entity entity : Bukkit.selectEntities(sender, "@e[tag=guitar-display]")) {
            if (entity instanceof ItemDisplay itemDisplay) {
                itemDisplay.setItemStack(NPCMaps.postItems.get(Erie.ErieTrait.class)[0]);
            }
        }
        for (Entity entity : Bukkit.selectEntities(sender, "@e[tag=trumpet-display]")) {
            if (entity instanceof ItemDisplay itemDisplay) {
                itemDisplay.setItemStack(NPCMaps.postItems.get(SteveSkellington.SteveTrait.class)[0]);
            }
        }
        for (Entity entity : Bukkit.selectEntities(sender, "@e[tag=drumsticks-display]")) {
            if (entity instanceof ItemDisplay itemDisplay) {
                itemDisplay.setItemStack(GuiUtil.formatItem("Drumsticks", Material.STICK, 5));
            }
        }
    }

    public void loadPerformPositions() {
        Worlds.Jan2024().setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        Worlds.Jan2024().setGameRule(GameRule.DO_MOB_SPAWNING, false);
        Worlds.Jan2024().setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        Worlds.Jan2024().setClearWeatherDuration(10000);
        Worlds.Jan2024().setTime(18000);
        for (TowerTeam team : teamManager.getAllTeams()) {
            team.setQuest(QuestTags.PERFORMANCE);
        }
        newYearsBall.reloadBall();
        for (Map.Entry<Class<? extends Trait>, Integer> idEntry : NPCMaps.npcIds.entrySet()) {
            Class<? extends Trait> traitClass = idEntry.getKey();
            NPC npc = CitizensAPI.getNPCRegistry().getById(idEntry.getValue());
            npc.teleport(NPCMaps.postLocations.get(traitClass), PlayerTeleportEvent.TeleportCause.PLUGIN);
            LookClose lookClose = npc.getOrAddTrait(LookClose.class);
            float[] yawRange = NPCMaps.postLookYaws.get(traitClass);
            lookClose.setRandomLookYawRange(yawRange[0], yawRange[1]);

            float[] pitchRange = NPCMaps.postLookPitches.get(traitClass);
            lookClose.setRandomLookPitchRange(pitchRange[0], pitchRange[1]);

            if (NPCMaps.postItems.containsKey(traitClass)) {
                if (npc.getEntity() instanceof LivingEntity livingEntity) {
                    EntityEquipment equipment = livingEntity.getEquipment();
                    if (equipment != null) {
                        ItemStack[] items = NPCMaps.postItems.get(traitClass);
                        equipment.setItemInMainHand(items[0]);
                        equipment.setItemInOffHand(items[1]);
                    }
                }
            }

            for (Entity entity : Bukkit.selectEntities(sender, "@e[tag=guitar-display]")) {
                if (entity instanceof ItemDisplay itemDisplay) {
                    itemDisplay.setItemStack(null);
                }
            }
            for (Entity entity : Bukkit.selectEntities(sender, "@e[tag=trumpet-display]")) {
                if (entity instanceof ItemDisplay itemDisplay) {
                    itemDisplay.setItemStack(null);
                }
            }
            for (Entity entity : Bukkit.selectEntities(sender, "@e[tag=drumsticks-display]")) {
                if (entity instanceof ItemDisplay itemDisplay) {
                    itemDisplay.setItemStack(null);
                }
            }

        }
    }

    public NewYearsBall getNewYearsBall() {
        return newYearsBall;
    }

    @Override
    public Gui getGui(Player player) {
        return new TeamGui(plugin, Component.text("Which team?"), new ArrayList<>(), teamManager.getAllTeams(), (player1, team) -> {
            PresetGui gui = new PresetGui(plugin, team.getDisplayName().append(Component.text(": Control Panel")), 3);
            gui.placeElement(1, 2, getItemQuestButton(plugin, true, GuiUtil.formatItem("Show North Gear", Material.OBSIDIAN, 44), northGearTag, team));
            gui.placeElement(3, 2, getItemQuestButton(plugin, false, GuiUtil.formatItem("Hide North Gear", Material.OBSIDIAN, 44), northGearTag, team));
            gui.placeElement(1, 5, getItemQuestButton(plugin, true, GuiUtil.formatItem("Show South Gear", Material.OBSIDIAN, 45), southGearTag, team));
            gui.placeElement(3, 5, getItemQuestButton(plugin, false, GuiUtil.formatItem("Hide South Gear", Material.OBSIDIAN, 45), southGearTag, team));
            gui.placeElement(3, 8, getItemQuestButton(plugin, true, GuiUtil.formatItem("Hide Lever", Material.LEVER, 0), leverTag, team));
            gui.placeElement(1, 8, getItemQuestButton(plugin, false, GuiUtil.formatItem("Show Lever", Material.LEVER, 0), leverTag, team));
            gui.openInventory(player1);
        }, Element.blank());
    }

    @NotNull
    private static ButtonElement getItemQuestButton(Plugin plugin, boolean showing, ItemStack itemStack, String tag, TowerTeam team) {
        return new ButtonElement(itemStack, player -> {
            List<Entity> entities = Bukkit.selectEntities(Jan2024QuestManager.sender, String.format("@e[tag=%s]", tag));
            for (Player player1 : team.getOnlinePlayers()) {
                for (Entity entity : entities) {
                    if (showing) {
                        player1.showEntity(plugin, entity);
                    } else {
                        player1.hideEntity(plugin, entity);
                    }
                }
            }

        });
    }

}
