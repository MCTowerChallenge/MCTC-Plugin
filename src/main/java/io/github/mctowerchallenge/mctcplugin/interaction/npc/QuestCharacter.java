package io.github.mctowerchallenge.mctcplugin.interaction.npc;

import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import io.github.mystievous.mysticore.Color;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.trait.LookClose;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Represents an abstract quest character with interactions and handlers.
 */
public abstract class QuestCharacter {

    private final Plugin plugin;
    private final EntityType entityType;
    private final String characterName;
    private final Color nameColor;
    private final Color textColor;
    private String worldguardRegion;

    private final Map<String, BiConsumer<TowerTeam, NPCRightClickEvent>> questInteractionHandlers;
    private BiConsumer<TowerTeam, NPCRightClickEvent> defaultInteractionHandler;

    /**
     * Constructs a new QuestCharacter.
     *
     * @param plugin        The plugin instance.
     * @param entityType    The entity type of the character.
     * @param characterName The name of the character.
     * @param nameColor     The color of the character's name.
     * @param textColor     The color of the character's text.
     */
    public QuestCharacter(Plugin plugin, EntityType entityType, String characterName, Color nameColor, Color textColor) {
        this.plugin = plugin;
        this.entityType = entityType;
        this.characterName = characterName;
        this.nameColor = nameColor;
        this.textColor = textColor;
        this.questInteractionHandlers = new HashMap<>();
        this.defaultInteractionHandler = null;
        this.worldguardRegion = null;
    }

    /**
     * Returns the plugin instance associated with this quest character.
     *
     * @return The plugin instance.
     */
    public Plugin getPlugin() {
        return plugin;
    }

    /**
     * Returns the color used for the character's name.
     *
     * @return The name color.
     */
    protected Color getNameColor() {
        return nameColor;
    }

    /**
     * Returns the color used for the character's text.
     *
     * @return The text color.
     */
    protected Color getTextColor() {
        return textColor;
    }

    /**
     * Gets the name of the character formatted using MiniMessage.
     *
     * @return The formatted name.
     */
    public String getMiniMessageName() {
        return MiniMessage.miniMessage().serialize(getDisplayName());
    }

    /**
     * Gets the unformatted name of the character.
     *
     * @return The character's name.
     */
    public String getTextName() {
        return characterName;
    }

    /**
     * Gets the display name of the character with the appropriate color.
     *
     * @return The display name component.
     */
    public Component getDisplayName() {
        return Component.text(characterName, nameColor.toTextColor());
    }

    /**
     * Sets the WorldGuard region associated with this quest character.
     *
     * @param worldguardRegion The WorldGuard region name.
     */
    public void setWorldguardRegion(String worldguardRegion) {
        this.worldguardRegion = worldguardRegion;
    }

    /**
     * Gets the WorldGuard region associated with this quest character.
     *
     * @return The WorldGuard region name, or null if not set.
     */
    public @Nullable String getWorldguardRegion() {
        return worldguardRegion;
    }

    /**
     * Adds a quest-specific interaction handler for this NPC.
     * <p>
     * These handlers are executed asynchronously. If the handler requires
     * synchronous operations, ensure to use {@link BukkitScheduler#runTask(Plugin, Runnable)} explicitly.
     *
     * @param quest   The quest required for the handler to be triggered.
     * @param handler The consumer to execute when a player interacts
     *                with the NPC while the quest is active.
     */
    public void addQuestInteractionHandler(@NotNull String quest, BiConsumer<TowerTeam, NPCRightClickEvent> handler) {
        questInteractionHandlers.put(quest, handler);
    }

    /**
     * Sets the default interaction handler for this NPC.
     * <p>
     * This handler is executed asynchronously. If the handler requires
     * synchronous operations, ensure to use {@link BukkitScheduler#runTask(Plugin, Runnable)} explicitly.
     *
     * @param defaultInteractionHandler The consumer to execute when a player interacts
     *                                  with the NPC  while no matching quest is active.
     */
    public void setDefaultInteractionHandler(BiConsumer<TowerTeam, NPCRightClickEvent> defaultInteractionHandler) {
        this.defaultInteractionHandler = defaultInteractionHandler;
    }

    /**
     * Executes the default interaction handler for the provided event.
     *
     * @param event The NPCRightClickEvent triggering the handler.
     */
    public void runInteractionHandler(NPCRightClickEvent event) {
        runInteractionHandler(null, event);
    }

    /**
     * Executes the appropriate handler for the provided event and quest.
     * If a quest-specific handler is found for the given quest, it will be executed.
     * Otherwise, the default handler will be executed.
     *
     * @param team The team that triggered the handler.
     * @param event The NPCRightClickEvent triggering the handler.
     */
    public void runInteractionHandler(@Nullable TowerTeam team, NPCRightClickEvent event) {
        String quest = null;
        if (team != null) {
            quest = team.getCurrentQuestTag();
        }
        BiConsumer<TowerTeam, NPCRightClickEvent> handler = questInteractionHandlers.getOrDefault(quest, defaultInteractionHandler);
        if (handler != null) {
            handler.accept(team, event);
        }
    }

    /**
     * Formats the given {@link Component} into a chat message from this NPC.
     * Uses the colors specified in the constructor, putting the name before
     * the message similar to a player's chat message.
     *
     * @param text The message to format.
     * @return The resulting component with the chat message.
     */
    public Component formatMessage(Component text) {
        return Component.text(String.format("<%s> ", getTextName())).color(nameColor.toTextColor())
                .append(text.color(textColor.toTextColor()));
    }

    /**
     * Formats the given {@link String} into a chat message from this NPC.
     * Uses the colors specified in the constructor, putting the name before
     * the message similar to a player's chat message.
     *
     * @param text The message to format.
     * @return The resulting component with the chat message.
     */
    public Component formatMessage(String text) {
        return formatMessage(Component.text(text));
    }

    /**
     * Creates a chat message indicating an action taken by this NPC.
     * Uses the colors specified in the constructor, appending the
     * character's name before the action message.
     *
     * @param text The action message to format.
     * @return The resulting component with the formatted action message.
     */
    public Component actionMessage(String text) {
        return Component.text("* ")
                .append(getDisplayName()).append(Component.space())
                .append(Component.text(text)).color(textColor.toTextColor());
    }

    /**
     * Sets the properties and traits of the given NPC.
     *
     * @param npc The NPC to configure.
     * @return The configured NPC.
     */
    public @NotNull NPC setNPCProperties(NPC npc) {
        LookClose lookClose = npc.getOrAddTrait(LookClose.class);
        lookClose.setRandomLook(true);
        lookClose.setLinkedBody(true);
        return npc;
    }

    /**
     * Creates an NPC instance for this quest character.
     * Configures the NPC traits and properties.
     *
     * @return The created NPC.
     */
    public NPC createNPC() {
        NPC npc = CitizensAPI.getNPCRegistry().createNPC(entityType, getTextName());
        npc.getOrAddTrait(getTrait());
        return setNPCProperties(npc);
    }

    /**
     * Gets the trait class associated with this quest character.
     * Subclasses must implement this method to return the specific trait class.
     *
     * @return The trait class.
     */
    public abstract @NotNull Class<? extends Trait> getTrait();

}
