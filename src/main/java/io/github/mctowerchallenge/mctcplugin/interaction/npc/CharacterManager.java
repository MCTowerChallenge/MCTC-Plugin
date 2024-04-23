package io.github.mctowerchallenge.mctcplugin.interaction.npc;

import io.github.mctowerchallenge.mctcplugin.god.GodTeam;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.character.*;
import io.github.mctowerchallenge.mctcplugin.team.TeamManager;
import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mctowerchallenge.mctcplugin.Database;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityMountEvent;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages registered quest characters and their interactions.
 */
public class CharacterManager implements Listener {

    // Collection of registered quest characters
    private static final Map<Class<? extends Trait>, QuestCharacter> registeredCharacters = new HashMap<>();

    /**
     * Registers a quest character with the manager.
     *
     * @param questCharacter The quest character to register.
     */
    public static void registerCharacter(@NotNull QuestCharacter questCharacter) {
        Class<? extends Trait> trait = questCharacter.getTrait();
        if (!trait.isAnnotationPresent(TraitName.class)) {
            Bukkit.getLogger().severe("Quest Character registered without a valid Trait: " + questCharacter.getTextName());
            Bukkit.getLogger().severe("Missing @TraitName(value) annotation");
            return;
        }

        try {
//            Bukkit.getLogger().info(trait.getName());
            CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(trait));
            registeredCharacters.put(trait, questCharacter);
        } catch (NullPointerException e) {
            Bukkit.getLogger().severe("Quest Character registered with null Character Trait: " + questCharacter.getTextName());
        }
    }

    /**
     * Retrieves a collection of registered quest characters.
     *
     * @return The collection of registered quest characters.
     */
    public static Collection<QuestCharacter> getCharacters() {
        return registeredCharacters.values();
    }

    /**
     * Retrieves a quest character based on its trait class.
     *
     * @param characterTrait The trait class of the character.
     * @return The quest character, or null if not found.
     */
    public static QuestCharacter getCharacter(Class<? extends Trait> characterTrait) {
        return registeredCharacters.get(characterTrait);
    }

    /**
     * Retrieves the quest character associated with an NPC.
     *
     * @param npc The NPC associated with the quest character.
     * @return The quest character, or null if not found.
     */
    public static @Nullable QuestCharacter getNPCCharacter(NPC npc) {
        for (Trait trait : npc.getTraits()) {
            QuestCharacter character = registeredCharacters.get(trait.getClass());
            if (character != null) {
                return character;
            }
        }

        return null;
    }

    // Instance variables
    private final Plugin plugin;
    private final TeamManager teamManager;
    private final QuestCharacter steve;

    /**
     * Creates a new CharacterManager instance.
     *
     * @param plugin The plugin instance.
     * @param teamManager The team manager instance.
     * @param database The database instance
     */
    public CharacterManager(Plugin plugin, TeamManager teamManager, Database database) {
        this.plugin = plugin;
        this.teamManager = teamManager;

        // Character initialization and registration
        steve = new SteveSkellington(plugin);
        registerCharacter(steve);
        registerCharacter(new Penelope(plugin));
        registerCharacter(new ButtStallion(plugin));
        registerCharacter(new EvilSpirit(plugin));
        registerCharacter(new PolarW(plugin));
        registerCharacter(new Endi(plugin));
        registerCharacter(new Erie(plugin));
        registerCharacter(new Henry(plugin));
        registerCharacter(new Percy(plugin));
        registerCharacter(new Boney(plugin));
        registerCharacter(new Moollicient(plugin));
        registerCharacter(new Pete(plugin));
        registerCharacter(new Ari(plugin));
        registerCharacter(new Soup(plugin));
        registerCharacter(new Alice(plugin));
        registerCharacter(new Dave(plugin));
        registerCharacter(new GenericMaintenanceMan(plugin));
        registerCharacter(new GenericBeeConservationistMan(plugin));
        registerCharacter(new PaulNIzer(plugin));

        registerCharacter(new Mystievous(plugin, teamManager.getGodTeam()));
        registerCharacter(new Apple(plugin, teamManager.getGodTeam()));

        registerCharacter(new Lovebot(plugin, database));

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TeamTrait.class));

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Handles the NPC right-click event.
     *
     * @param event The NPC right-click event.
     */
    @EventHandler
    public void onClick(NPCRightClickEvent event) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            NPC npc = event.getNPC();
            QuestCharacter character = getNPCCharacter(npc);
            if (character == null) {
                return;
            }

            Player player = event.getClicker();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team == null) {
                return;
            }

            character.runInteractionHandler(team, event);
        });
    }

    /**
     * Stops non-gods from mounting a god mount
     */
    @EventHandler
    public void onEntityMount(final EntityMountEvent event) {
        if (event.getEntity() instanceof Player player) {
            Entity entity = event.getMount();
            if (CitizensAPI.getNPCRegistry().isNPC(entity)) {
                if (!(TeamManager.getInstance().getPlayerTeam(player) instanceof GodTeam)) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
