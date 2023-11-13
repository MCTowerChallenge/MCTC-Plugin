package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mctowerchallenge.mctcplugin.Database;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import io.github.mctowerchallenge.mctcplugin.utility.CommandUtils;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class Lovebot extends QuestCharacter {

    /**
     * Possible states for Lovebot to be in.
     */
    public enum State {
        IDLE(26, null),
        YES(27, 43),
        MAYBE(29, 45),
        NO(28, 44),
        BAKE(84, null);

        private final int modelId;
        private final Integer cookieId;

        /**
         * @param modelId  Model ID for Lovebot's Body
         * @param cookieId Model ID for the Cookie item
         */
        State(int modelId, Integer cookieId) {
            this.modelId = modelId;
            this.cookieId = cookieId;
        }

        public int getModelId() {
            return modelId;
        }

        public Integer getCookieId() {
            return cookieId;
        }
    }
    private final static State[] ANSWER_STATES = new State[]{
            State.YES,
            State.MAYBE,
            State.NO
    };
    private static final Random RANDOM = new Random();

    private final Map<UUID, State> states;
    private final Database database;

    public static final String NAME = "Lovebot";
    private static final Color NAME_COLOR = new Color(0x72a881);
    private static final Color TEXT_COLOR = new Color(0x8bce9e);
    public static final String TRAIT_NAME = "lovebot";

    public Lovebot(Plugin plugin, Database database) {
        super(plugin, EntityType.ARMOR_STAND, NAME, NAME_COLOR, TEXT_COLOR);
        this.database = database;
        this.states = new HashMap<>();

        // NPC Handler for Lovebot's interactions
        setDefaultInteractionHandler((team, playerInteractEntityEvent) -> Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            Player player = playerInteractEntityEvent.getClicker();
            Entity entity = playerInteractEntityEvent.getNPC().getEntity();
            if (entity instanceof LivingEntity livingEntity) {
                try {
                    State state = getState(livingEntity);
                    switch (state) {
                        case IDLE -> {
                            /*
                                Switches Lovebot to a random state from the answers array
                             */
                            setState(livingEntity, State.BAKE);
                            player.sendMessage(formatMessage(Component.text("Calculating... ")));
                            livingEntity.getWorld().playSound(livingEntity.getEyeLocation(), Sound.BLOCK_BEACON_AMBIENT, 1f, 1.5f);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                State response = ANSWER_STATES[RANDOM.nextInt(ANSWER_STATES.length)];
                                try {
                                    setState(livingEntity, response);
                                    livingEntity.getWorld().playSound(livingEntity.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 2f);
                                    player.sendMessage(formatMessage(Component.text("Done! Please use ").append(Component.keybind("key.use")).append(Component.text(" to take your cookie!"))));
                                } catch (SQLException e) {
                                    player.sendMessage(CommandUtils.errorMessage("Error operating Lovebot... Please contact a god for maintenance"));
                                    Bukkit.getLogger().warning("Lovebot failed to change state! " + e.getMessage());
                                }
                            }, 50);
                        }
                        case BAKE -> player.sendMessage(formatMessage(Component.text("Calculating... ")));
                        case YES, MAYBE, NO -> {
                            /*
                                Gives player the correct cookie and resets state to IDLE
                             */
                            ItemStack cookie = database.getModel(state.getCookieId(), true, false).getItem();
                            NBTUtils.setUniqueID(plugin, cookie, null);
                            ItemMeta meta = cookie.getItemMeta();
                            meta.displayName(TextUtil.noItalic("Cookie"));
                            cookie.setItemMeta(meta);
                            player.getInventory().addItem(cookie);
                            setState(livingEntity, State.IDLE);
//                            player.sendMessage(LovebotText("Come again!"));
                        }
                    }
                } catch (SQLException e) {
                    player.sendMessage(CommandUtils.errorMessage("Error operating Lovebot... Please contact a god for maintenance"));
                    Bukkit.getLogger().warning("Lovebot failed to change state! " + e.getMessage());
                }
            }
        }));

    }

    /**
     * Sets the state of the Lovebot NPC.
     *
     * @param entity The LivingEntity representing Lovebot.
     * @param state The new state to set.
     * @throws SQLException If a database operation fails.
     */
    public void setState(LivingEntity entity, State state) throws SQLException {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment != null) {
            equipment.setHelmet(database.getModel(state.getModelId(), true, false).getItem());
        }
        this.states.put(entity.getUniqueId(), state);
    }

    /**
     * Gets the current state of the Lovebot NPC.
     *
     * @param entity The LivingEntity representing Lovebot.
     * @return The current state.
     */
    public State getState(LivingEntity entity) {
        return this.states.getOrDefault(entity.getUniqueId(), State.IDLE);
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return LovebotTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class LovebotTrait extends Trait {
        public LovebotTrait() {
            super(TRAIT_NAME);
        }

        @Override
        public void onSpawn() {
            super.onSpawn();
            if (npc.getEntity() instanceof ArmorStand armorStand) {
                armorStand.setInvisible(true);
            }
        }
    }

}
