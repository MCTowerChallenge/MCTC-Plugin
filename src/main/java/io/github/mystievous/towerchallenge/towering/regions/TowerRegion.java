package io.github.mystievous.towerchallenge.towering.regions;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.ChallengePhaseChangeEvent;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.gods.GodTeam;
import io.github.mystievous.towerchallenge.towering.ParticipantTeam;
import io.papermc.paper.event.block.PlayerShearBlockEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.scoreboard.Score;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

public class TowerRegion extends EventRegion implements Listener {

    private final EnumMap<Material, BlockState> blocks = new EnumMap<>(Material.class);
    private final Score score;

    public TowerRegion(ParticipantTeam team, ChallengeManager manager, ProtectedRegion region, String name) {
        super(team, manager, region);
        Bukkit.getServer().getPluginManager().registerEvents(this, manager.getPlugin());
        score = manager.getObjective().getScore(name);
        score.setScore(blocks.size());
//        scanBlocks();
    }

    public void sendBlockCount(Audience audience) {
        if (blocks.size() == 1) {
            audience.sendMessage(
                    getTeam().getDisplayName().color(getTeam().getTextColor()).append(Component.text(" Tower"))
                            .append(Component.text(String.format(" has %d block", blocks.size())).color(NamedTextColor.WHITE))
            );
        } else {
            audience.sendMessage(
                    getTeam().getDisplayName().color(getTeam().getTextColor()).append(Component.text(" Tower"))
                            .append(Component.text(String.format(" has %d block", blocks.size())).color(NamedTextColor.WHITE))
            );
        }
    }

    private boolean exclude(BlockState block) {
        if (block instanceof ShulkerBox box) {
            Component boxName = box.customName();
            return boxName != null && PlainTextComponentSerializer.plainText().serialize(boxName).equals(ParticipantTeam.SHULKER_NAME);
        }
        return false;
    }

    /**
     * Stores the block to the tower, as it is at the moment it's added.
     * @param block Block to be added
     * @return true, if the tower already has that block
     */
    private boolean addBlock(Audience audience, BlockState block) {
        Material material = block.getType();

        if (exclude(block)) {
            return false;
        }

        List<Player> godPlayers = new ArrayList<>();
        audience.forEachAudience(audience1 -> {
            if (audience1 instanceof Player player) {
                if (getManager().getPlayerTeam(player) instanceof GodTeam) {
                    godPlayers.add(player);
                }
            }
        });
        // block is already in tower
        if (godPlayers.size() == 0) {
            if (blocks.get(material) == null) {
                blocks.put(material, block);
//            sendBlockCount(audience);
                score.setScore(blocks.size());
                return false;
            } else {
                audience.sendActionBar(Component.text("You've already placed ").append(Component.text(ChallengeManager.formatBlockType(block.getType())).color(NamedTextColor.DARK_RED)));
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Removes a block from the tower
     * @param block Block to be removed
     */
    private void removeBlock(BlockState block) {
        if (exclude(block)) {
            return;
        }

        Material material = block.getType();
        if (blocks.remove(material) != null) {
            // block did not exist in tower
            score.setScore(blocks.size());
        }
    }

    /**
     * Checks whether a block is in the region
     * @param block block to check
     * @return true, if the block is in the region
     */


    private boolean checkFullBlock(BlockState block) {
        return getManager().isFullBlock(block.getType());
    }

    @EventHandler
    public void onChallengePhaseChange(ChallengePhaseChangeEvent event) {
        ChallengeManager.ChallengePhase phase = event.getChallengePhase();
        if (phase == ChallengeManager.ChallengePhase.TOWERING) {
            addPlayers(getTeam().getPlayers());
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
            if (getManager().isTowering()) {
                if (checkFullBlock(block)) {
                    boolean cancelEvent = addBlock(event.getPlayer(), event.getBlockPlaced().getState());
                    event.setCancelled(cancelEvent);
                } else {
                    event.getPlayer().sendActionBar(Component.text(ChallengeManager.formatBlockType(block.getType())).color(NamedTextColor.DARK_RED)
                            .append(Component.text("is not a full block!").color(NamedTextColor.WHITE)));
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBreakBlock(final BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        BlockState block = event.getBlock().getState();
        if (checkInRegion(block)) {
            if (getManager().isTowering()) {
                removeBlock(event.getBlock().getState());
            }
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

}
