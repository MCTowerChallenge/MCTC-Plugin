package io.github.mystievous.towerchallenge.eventspecific.valentines;

import io.github.mystievous.towerchallenge.Database;
import io.github.mystievous.towerchallenge.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.misc.CommandUtils;
import io.github.mystievous.towerchallenge.quests.entities.NPC;
import io.github.mystievous.towerchallenge.utility.Color;
import io.github.mystievous.towerchallenge.utility.NBTUtils;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Lovebot extends NPC {

    private enum State {
        IDLE(26, null),
        YES(27, 43),
        MAYBE(29, 45),
        NO(28, 44),
        BAKE(84, null);

        private final int modelId;
        private final Integer cookieId;

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

    public static final String TAG = "lovebot";
    private static final Color nameColor = new Color(0x72a881);
    private static final Color textColor = new Color(0x8bce9e);

    public static @NotNull Component LovebotText(@NotNull Component text) {
        return Component.text("<Lovebot> ").color(nameColor.toTextColor())
                .append(text.color(textColor.toTextColor()));
    }

    private final static State[] ANSWER_STATES = new State[]{
            State.YES,
            State.MAYBE,
            State.NO
    };
    private static final SecureRandom RANDOM = new SecureRandom();

    private final Map<UUID, State> states;

    private final Database database;

    public Lovebot(TowerChallenge plugin, TeamManager teamManager, Database database) {
        super(teamManager, TAG);
        this.database = database;
        this.states = new HashMap<>();
        setDefaultHandler(playerInteractAtEntityEvent -> {
            Player player = playerInteractAtEntityEvent.getPlayer();
            Entity entity = playerInteractAtEntityEvent.getRightClicked();
            if (entity instanceof LivingEntity livingEntity) {
                try {
                    State state = getState(livingEntity);
                    switch (state) {
                        case IDLE -> {
                            setState(livingEntity, State.BAKE);
                            player.sendMessage(LovebotText(Component.text("Calculating... ")));
                            livingEntity.getWorld().playSound(livingEntity.getEyeLocation(), Sound.BLOCK_BEACON_AMBIENT, 1f, 1.5f);
                            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                                State response = ANSWER_STATES[RANDOM.nextInt(ANSWER_STATES.length)];
                                try {
                                    setState(livingEntity, response);
                                    livingEntity.getWorld().playSound(livingEntity.getEyeLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1f, 2f);
                                    player.sendMessage(LovebotText(Component.text("Done! Please use ").append(Component.keybind("key.use")).append(Component.text(" to take your cookie!"))));
                                } catch (SQLException e) {
                                    player.sendMessage(CommandUtils.errorMessage("Error operating Lovebot... Please contact a god for maintenance"));
                                    Bukkit.getLogger().warning("Lovebot failed to change state! " + e.getMessage());
                                }
                            }, 50);
                        }
                        case BAKE -> {
                            player.sendMessage(LovebotText(Component.text("Calculating... ")));
                        }
                        case YES, MAYBE, NO -> {
                            ItemStack cookie = NBTUtils.setUniqueID(database.getModel(state.getCookieId(), false).getItem(), null);
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
        });
    }

    public void setState(LivingEntity entity, State state) throws SQLException {
        EntityEquipment equipment = entity.getEquipment();
        if (equipment != null) {
            equipment.setHelmet(database.getModel(state.getModelId(), false).getItem());
        }
        this.states.put(entity.getUniqueId(), state);
    }

    public State getState(LivingEntity entity) {
        return this.states.getOrDefault(entity.getUniqueId(), State.IDLE);
    }

}
