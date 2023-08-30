package io.github.mystievous.towerchallenge.interaction.npc;

import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.interaction.npc.character.*;
import io.github.mystievous.towerchallenge.team.TeamManager;
import io.github.mystievous.towerchallenge.team.TowerTeam;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

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
     */
    public CharacterManager(Plugin plugin, TeamManager teamManager) {
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

        registerCharacter(new Mystievous(plugin, teamManager.getGodTeam()));
        registerCharacter(new Apple(plugin, teamManager.getGodTeam()));

        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TeamTrait.class));
        CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(TextDisplayTrait.class));

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Retrieves the dialogue for the event start.
     *
     * @return The start event dialogue.
     */
    public Dialogue getEventStartDialogue() {
        Dialogue startDialogue = new Dialogue(plugin, TextUtil.formatText("* Announcement Sound"), 3.0d, TowerChallenge.key("bell"));
        startDialogue.append(steve.formatMessage("Hello everyone."), 3.0d, TowerChallenge.key("steve.hello_everyone"));
        startDialogue.append(steve.formatMessage("It is with a grave face that I must tell you that, due to lost and damaged equipment, the band The Withering Groove Machine will be unable to perform until further notice."),
                16.0d, TowerChallenge.key("steve.grave_face"));
        startDialogue.append(steve.formatMessage("Please enjoy the rest of the festivities, as we await further news on the matter."), 7.5d, TowerChallenge.key("steve.further_news"));
        startDialogue.append(Dialogue.playerThoughts("Huh, sounds like something’s up with the band."), 4.0d);
        startDialogue.append(Dialogue.playerThoughts("Maybe I should head to the main stage by the beacons to see if they need help?"), 4.0d);
        return startDialogue;
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
}
