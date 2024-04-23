package io.github.mctowerchallenge.mctcplugin.team.regions;

import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mctowerchallenge.mctcplugin.ChallengeManager;
import io.github.mctowerchallenge.mctcplugin.ChallengePhaseChangeEvent;
import io.github.mctowerchallenge.mctcplugin.MCTCPlugin;
import io.github.mctowerchallenge.mctcplugin.team.ParticipantTeam;
import io.github.mctowerchallenge.mctcplugin.utility.BlockSets;
import io.github.mctowerchallenge.mctcplugin.utility.CommandUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import java.sql.SQLException;
import java.util.EnumMap;

public class TowerRegion extends EventRegion {

    public static final String REGION_TAG = "tower";

    private final EnumMap<Material, BlockState> blocks = new EnumMap<>(Material.class);
    private final Score score;
    private int extraScore;

    public TowerRegion(MCTCPlugin plugin, Location[] bounds, ParticipantTeam team, String name) {
        super(plugin, bounds, team, REGION_TAG);
        Objective objective = ChallengeManager.getScoreObjective();
        extraScore = 0;
        if (objective != null) {
            score = objective.getScore(name);
            score.setScore(blocks.size());
        } else {
            score = null;
        }
        setFlags(getRegion());
    }

    public int getScore() {
        return score.getScore() + extraScore;
    }

    @Override
    public ParticipantTeam getTeam() {
        return (ParticipantTeam) super.getTeam();
    }

    @Override
    public String parentRegionName() {
        return "tower-area";
    }

    @Override
    protected void setFlags(ProtectedRegion region) {
        region.setPriority(1);
        region.setFlag(Flags.DENY_MESSAGE, null);
        region.setFlag(Flags.ENTRY_DENY_MESSAGE, null);
        region.setFlag(Flags.EXIT_DENY_MESSAGE, null);
    }

    /**
     * Checks if a block should be
     * excluded from the tower checking.
     * <p></p>
     * Used for shulker boxes provided
     * to players, so they can place
     * them freely and not have them
     * count towards the tower points.
     *
     * @param block The block to check.
     * @return True, if the block should be excluded.
     */
    private boolean exclude(BlockState block) {
        if (block instanceof ShulkerBox box) {
            Component boxName = box.customName();
            return boxName != null && PlainTextComponentSerializer.plainText().serialize(boxName).equals(ParticipantTeam.SHULKER_NAME);
        }
        return false;
    }

    /**
     * Stores the block in the tower, as it is at the moment it's added.
     *
     * @param block Block to be added.
     * @return True, if the tower already has that block.
     */
    private boolean addBlock(Audience audience, BlockState block) {
        Material material = block.getType();

        if (exclude(block)) {
            return false;
        }

        // block is already in tower
        if (!block.getType().equals(Material.BEDROCK)) {
            if (blocks.get(material) == null) {
                blocks.put(material, block);
                if (score != null) {
                    score.setScore(blocks.size());
                }
                return false;
            } else {
                audience.sendActionBar(Component.text("You've already placed ").append(Component.text(TextUtil.formatBlockType(block.getType())).color(NamedTextColor.DARK_RED)));
                return true;
            }
        } else {
            extraScore++;
            audience.sendActionBar(TextUtil.formatText(String.format("1 point added (%d total)", extraScore)));
            return false;
        }
    }

    /**
     * Removes a block from the tower
     *
     * @param block Block to be removed
     */
    private void removeBlock(Audience audience, BlockState block) {
        if (exclude(block)) {
            return;
        }

        Material material = block.getType();
        if (!material.equals(Material.BEDROCK)) {
            blocks.remove(material);
            if (score != null) {
                score.setScore(blocks.size());
            }
        } else {
            extraScore--;
            audience.sendActionBar(TextUtil.formatText(String.format("1 point removed (%d total)", extraScore)));
        }
    }

    /**
     * Checks whether a block is
     * in the list of full blocks.
     *
     * @param block The block to check.
     * @return True, if the block is
     * a full block
     * @see BlockSets#FULL_BLOCKS
     */
    private boolean checkFullBlock(BlockState block) {
        return BlockSets.FULL_BLOCKS.contains(block.getType());
    }

    /**
     * Adds players as members of the
     * region when towering starts,
     * so they can place blocks.
     * When towering ends, removes
     * them again.
     *
     * @param event The challenge phase change event.
     */
    @EventHandler
    public void onChallengePhaseChange(ChallengePhaseChangeEvent event) {
        ChallengeManager.ChallengePhase phase = event.getChallengePhase();
        if (phase == ChallengeManager.ChallengePhase.TOWERING) {
            addPlayers(getTeam().getOfflinePlayers());
        } else {
            clearPlayers();
        }
    }

    @EventHandler
    public void onPlaceBlock(final BlockPlaceEvent event) {
        if (event.isCancelled())
            return;
        BlockState block = event.getBlockPlaced().getState();
        if (checkInRegion(block)) {
            if (checkFullBlock(block)) {
                boolean cancelEvent = addBlock(event.getPlayer(), event.getBlockPlaced().getState());
                event.setCancelled(cancelEvent);
            } else {
                event.getPlayer().sendActionBar(Component.text(TextUtil.formatBlockType(block.getType())).color(NamedTextColor.DARK_RED)
                        .append(Component.text("is not a full block!").color(NamedTextColor.WHITE)));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreakBlock(final BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        BlockState block = event.getBlock().getState();
        if (checkInRegion(block)) {
            removeBlock(event.getPlayer(), event.getBlock().getState());
        }
    }

    @EventHandler
    public void onBlockBurn(final BlockBurnEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockSpread(final BlockSpreadEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(final BlockExplodeEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFade(final BlockFadeEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonExtend(final BlockPistonExtendEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPistonRetract(final BlockPistonRetractEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpongeAbsorb(final SpongeAbsorbEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onTNTPrime(final TNTPrimeEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockGrow(final BlockGrowEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockForm(final BlockFormEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerShearBlock(final PlayerShearBlockEvent event) {
        if (event.isCancelled())
            return;
        if (checkInRegion(event.getBlock().getState())) {
            event.setCancelled(true);
        }
    }

    @Override
    public void unregisterEvents() {
        ChallengePhaseChangeEvent.getHandlerList().unregister(this);
        BlockPlaceEvent.getHandlerList().unregister(this);
        BlockBreakEvent.getHandlerList().unregister(this);
        BlockBurnEvent.getHandlerList().unregister(this);
        BlockSpreadEvent.getHandlerList().unregister(this);
        BlockExplodeEvent.getHandlerList().unregister(this);
        EntityExplodeEvent.getHandlerList().unregister(this);
        BlockFadeEvent.getHandlerList().unregister(this);
        BlockPistonExtendEvent.getHandlerList().unregister(this);
        BlockPistonRetractEvent.getHandlerList().unregister(this);
        SpongeAbsorbEvent.getHandlerList().unregister(this);
        TNTPrimeEvent.getHandlerList().unregister(this);
        BlockGrowEvent.getHandlerList().unregister(this);
        BlockFormEvent.getHandlerList().unregister(this);
        EntityChangeBlockEvent.getHandlerList().unregister(this);
        PlayerShearBlockEvent.getHandlerList().unregister(this);
    }
}

