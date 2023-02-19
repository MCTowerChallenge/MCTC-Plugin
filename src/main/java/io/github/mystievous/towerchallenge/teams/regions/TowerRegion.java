package io.github.mystievous.towerchallenge.teams.regions;

import com.destroystokyo.paper.event.block.TNTPrimeEvent;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.*;
import io.github.mystievous.towerchallenge.teams.ParticipantTeam;
import io.github.mystievous.towerchallenge.utility.BlockSets;
import io.github.mystievous.towerchallenge.utility.CommandUtils;
import io.github.mystievous.towerchallenge.utility.TextUtil;
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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class TowerRegion extends EventRegion {

    public static final Location referenceLocation = new Location(Worlds.Feb2023_tower(), -60, -63, 3);
    public static final Location[] baseBounds = new Location[]{
            new Location(Worlds.Feb2023_tower(), -60, -62, 3),
            new Location(Worlds.Feb2023_tower(), -62, 319, 1)
    };

    public static final Map<Integer, Location> teamLocations = new HashMap<>() {{
        put(2, referenceLocation);
        put(3, new Location(Worlds.Feb2023_tower(), -53, -63, 10));
        put(6, new Location(Worlds.Feb2023_tower(), -68, -63, 2));
        put(7, new Location(Worlds.Feb2023_tower(), -63, -63, -3));
        put(8, new Location(Worlds.Feb2023_tower(), -57, -63, -3));
        put(10, new Location(Worlds.Feb2023_tower(), -47, -63, 7));
        put(11, new Location(Worlds.Feb2023_tower(), -47, -63, 13));
        put(13, new Location(Worlds.Feb2023_tower(), -57, -63, 23));
        put(14, new Location(Worlds.Feb2023_tower(), -63, -63, 23));
    }};

    private final EnumMap<Material, BlockState> blocks = new EnumMap<>(Material.class);
    private final Score score;

    public TowerRegion(TowerChallenge plugin, ParticipantTeam team, String name) {
        super(plugin, Arrays.stream(baseBounds).map(location -> {
            Location teamLocation = teamLocations.get(team.getDatabaseId());
            Vector offset = teamLocation.clone().subtract(referenceLocation).toVector();

            return location.clone().add(offset).setDirection(teamLocation.getDirection());
        }).toArray(Location[]::new), team);
        score = ChallengeManager.getScoreObjective().getScore(name);
        score.setScore(blocks.size());
    }

    private Location offsetLocation(Location location) {
        Location teamLocation = teamLocations.get(getTeam().getDatabaseId());
        Vector offset = teamLocation.clone().subtract(referenceLocation).toVector();

        return location.clone().add(offset).setDirection(teamLocation.getDirection());
    }

    @Override
    public ParticipantTeam getTeam() {
        return (ParticipantTeam) super.getTeam();
    }

    @Override
    public String getRegionName() {
        return String.format("%s-tower", getTeam().getServerTeamName());
    }

    @Override
    public String parentRegionName() {
        return "tower_area";
    }

    @Override
    protected void setFlags(ProtectedRegion region) {
        region.setPriority(1);
        region.setFlag(Flags.DENY_MESSAGE, null);
        region.setFlag(Flags.ENTRY_DENY_MESSAGE, null);
        region.setFlag(Flags.EXIT_DENY_MESSAGE, null);
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
     *
     * @param block Block to be added
     * @return true, if the tower already has that block
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
                score.setScore(blocks.size());
                return false;
            } else {
                audience.sendActionBar(Component.text("You've already placed ").append(Component.text(TextUtil.formatBlockType(block.getType())).color(NamedTextColor.DARK_RED)));
                return true;
            }
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    getTeam().addExtraScore(1);
                    audience.sendActionBar(TextUtil.formatText(String.format("1 point added (%d total)", getTeam().getExtraScore())));
                } catch (SQLException e) {
                    audience.sendActionBar(CommandUtils.errorMessage("Error updating database"));
                }
            });
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
            score.setScore(blocks.size());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try {
                    getTeam().addExtraScore(-1);
                    audience.sendActionBar(TextUtil.formatText(String.format("1 point removed (%d total)", getTeam().getExtraScore())));
                } catch (SQLException e) {
                    audience.sendActionBar(CommandUtils.errorMessage("Error updating database"));
                }
            });
        }
    }

    /**
     * Checks whether a block is in the region
     *
     * @param block block to check
     * @return true, if the block is in the region
     */


    private boolean checkFullBlock(BlockState block) {
        return BlockSets.FULL_BLOCKS.contains(block.getType());
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
        if (TNTPrimeEvent.getHandlerList() != null) {
            TNTPrimeEvent.getHandlerList().unregister(this);
        }
        BlockGrowEvent.getHandlerList().unregister(this);
        BlockFormEvent.getHandlerList().unregister(this);
        EntityChangeBlockEvent.getHandlerList().unregister(this);
        PlayerShearBlockEvent.getHandlerList().unregister(this);
    }
}

