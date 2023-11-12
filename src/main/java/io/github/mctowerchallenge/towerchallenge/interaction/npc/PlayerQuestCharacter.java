package io.github.mctowerchallenge.towerchallenge.interaction.npc;

import io.github.mctowerchallenge.towerchallenge.team.TowerTeam;
import io.github.mystievous.mysticore.Color;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.SkinTrait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An abstract class representing a player-based quest character with specific interactions and traits.
 */
public abstract class PlayerQuestCharacter extends QuestCharacter {

    private final TowerTeam team;

    /**
     * Creates a new PlayerQuestCharacter instance.
     *
     * @param plugin      The plugin instance.
     * @param team        The TowerTeam associated with the character.
     * @param playerName The name of the player character.
     * @param nameColor   The color for the character's name.
     * @param textColor   The color for the character's text.
     */
    public PlayerQuestCharacter(Plugin plugin, @Nullable TowerTeam team, String playerName, Color nameColor, Color textColor) {
        super(plugin, EntityType.PLAYER, playerName, nameColor, textColor);
        this.team = team;
    }

    /**
     * Creates a new PlayerQuestCharacter instance without an associated TowerTeam.
     *
     * @param plugin      The plugin instance.
     * @param playerName The name of the player character.
     * @param nameColor   The color for the character's name.
     * @param textColor   The color for the character's text.
     */
    public PlayerQuestCharacter(Plugin plugin, String playerName, Color nameColor, Color textColor) {
        this(plugin, null, playerName, nameColor, textColor);
    }

    /**
     * Formats the given {@link Component} into a chat message from this player character.
     * If a team is associated, their prefix is added.
     *
     * @param text The message to format.
     * @return The resulting component with the chat message.
     */
    public Component playerMessage(Component text) {
        Component prefix = Component.empty();
        Component name = Component.text("<").append(Component.text(getTextName())).append(Component.text("> "));

        if (team != null) {
            prefix = team.getTeam().prefix();
        }

        return prefix.append(name).append(text);
    }

    /**
     * Formats the given {@link String} into a chat message from this player character.
     * If a team is associated, their prefix is added.
     *
     * @param text The message to format.
     * @return The resulting component with the chat message.
     */
    public Component playerMessage(String text) {
        return formatMessage(Component.text(text));
    }

    @Override
    public String getMiniMessageName() {
        return MiniMessage.miniMessage().serialize((team != null ? team.getTeam().prefix() : Component.empty()).append(Component.text(getTextName())));
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinName(getTextName());
        npc.setName(getMiniMessageName());
//        TeamTrait teamTrait = npc.getOrAddTrait(TeamTrait.class);
//        teamTrait.setTeam(team);
        return npc;
    }

}
