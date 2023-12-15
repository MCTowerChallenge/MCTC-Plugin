package io.github.mctowerchallenge.mctcplugin.data.database;

import io.github.mctowerchallenge.mctcplugin.team.ParticipantTeam;
import io.github.mystievous.mysticore.Color;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.EndPortalFrame;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.util.Vector;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class PortalFrameDB {

    private final DataSource dataSource;

    public PortalFrameDB(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Entity tag for the armor stands
     * holding the frame borders.
     */
    public static final String FRAME_TAG = "portal-frame-colorborder";
    /**
     * Custom Model ID for the
     * portal frame borders.
     */
    private static final int FRAME_CUSTOM_MODEL = 1000;

    /**
     * Offset from the block location to
     * summon the armor stand for the borders.
     */
    private static final org.bukkit.util.Vector FRAME_OFFSET = new Vector(0.5, -1.31, 0.5);

    /**
     * Places the portal frame borders for
     * all teams.
     */
    public void placePortalBorders() throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT filled, facing, x, y, z, worlds.name AS world, t.color, t.name AS team_name FROM portalframes
                        LEFT JOIN worlds ON portalframes.world_id = worlds.id
                        INNER JOIN teams t on portalframes.team_id = t.id;
                        """
        )) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Location location = new Location(Bukkit.getWorld(resultSet.getString("world")), resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                BlockFace facing = BlockFace.valueOf(resultSet.getString("facing"));
                location.setDirection(facing.getDirection());
                Block block = location.getBlock();
                if (block.getType().equals(Material.END_PORTAL_FRAME)) {
                    EndPortalFrame blockData = (EndPortalFrame) block.getBlockData();
                    blockData.setFacing(facing);
                    blockData.setEye(resultSet.getBoolean("filled"));
                    block.setBlockData(blockData);
                } else {
                    Bukkit.getLogger().warning("End Portal location for " + resultSet.getString("team_name") + " is not a frame.");
                }
                Color color = new Color(resultSet.getInt("color"));
                ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location.clone().add(FRAME_OFFSET), EntityType.ARMOR_STAND);
                armorStand.addScoreboardTag(FRAME_TAG);
                armorStand.setItem(EquipmentSlot.HEAD, new ItemStack(Material.LEATHER_HORSE_ARMOR) {{
                    LeatherArmorMeta meta = (LeatherArmorMeta) getItemMeta();
                    meta.setColor(color.toBukkitColor());
                    meta.setCustomModelData(FRAME_CUSTOM_MODEL);
                    setItemMeta(meta);
                }});
                armorStand.setGravity(false);
                armorStand.setInvulnerable(true);
                armorStand.setInvisible(true);
                armorStand.setDisabledSlots(EquipmentSlot.values());
                Bukkit.getLogger().info("Loaded portal frame for " + resultSet.getString("team_name") + "at location " + location.getX() + " " + location.getY() + " " + location.getZ());
            }
        }
    }

    /**
     * Unloads all portal frame borders from the world.
     */
    public static void unloadPortalBorders() {
        CommandSender sender = Bukkit.createCommandSender(component -> {
        });
        List<Entity> entities = Bukkit.selectEntities(sender, String.format("@e[tag=%s]", FRAME_TAG));
        for (Entity entity : entities) {
            entity.remove();
        }
    }

    /**
     * Sets a team's portal frame's
     * filled state in the database.
     *
     * @param team   The team to set
     * @param filled The state to set it to.
     */
    public void setPortalFrameFilled(ParticipantTeam team, boolean filled) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement updateFrameStatement = conn.prepareStatement(
                """
                        UPDATE portalframes
                        SET portalframes.filled = ?
                        WHERE portalframes.team_id = ?;
                        """
        )) {
            updateFrameStatement.setBoolean(1, filled);
            updateFrameStatement.setInt(2, team.getDatabaseId());
            updateFrameStatement.executeUpdate();
        }
    }

    /**
     * Gets and sets in-world the portal frame
     * for the given team.
     *
     * @param team The team to get the frame
     *             of.
     * @return The location of the portal frame.
     */
    public Location getPortalFrame(ParticipantTeam team) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement getFrameStatement = conn.prepareStatement(
                """
                        SELECT filled, facing, x, y, z, worlds.name FROM portalframes
                        LEFT JOIN worlds ON portalframes.world_id = worlds.id
                        WHERE portalframes.team_id = ?;
                        """
        )) {
            getFrameStatement.setInt(1, team.getDatabaseId());
            ResultSet resultSet = getFrameStatement.executeQuery();
            if (resultSet.next()) {
                Location location = new Location(Bukkit.getWorld(resultSet.getString("name")), resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z"));
                BlockFace facing = BlockFace.valueOf(resultSet.getString("facing"));
                location.setDirection(facing.getDirection());
                Block block = location.getBlock();
                if (block.getType().equals(Material.END_PORTAL_FRAME)) {
                    EndPortalFrame blockData = (EndPortalFrame) block.getBlockData();
                    blockData.setFacing(facing);
                    blockData.setEye(resultSet.getBoolean("filled"));
                    block.setBlockData(blockData);
                } else {
                    Bukkit.getLogger().warning("End Portal location for " + team.getTextName() + " is not a frame.");
                }
                return location;
            } else {
                return null;
            }
        }
    }

    /**
     * Creates or updates a team's end portal frame location in the database.
     *
     * @param team     The team to update.
     * @param location The new location to set it to.
     * @param facing   The direction the block is facing.
     * @return The number of rows updated.
     */
    public int upsertPortalFrame(ParticipantTeam team, Location location, BlockFace facing) throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        INSERT INTO portalframes (team_id, x, y, z, facing, world_id)
                        VALUES (?, ?, ?, ?, ?, (SELECT worlds.id FROM worlds WHERE worlds.name = ?))
                        ON DUPLICATE KEY UPDATE portalframes.x        = VALUES(x),
                                                portalframes.y        = VALUES(y),
                                                portalframes.z        = VALUES(z),
                                                portalframes.facing   = VALUES(facing),
                                                portalframes.world_id = VALUES(world_id);
                        """
        )) {
            statement.setInt(1, team.getDatabaseId());
            statement.setInt(2, location.getBlockX());
            statement.setInt(3, location.getBlockY());
            statement.setInt(4, location.getBlockZ());
            statement.setString(5, facing.name());
            statement.setString(6, location.getWorld().getName());
            return statement.executeUpdate();
        }
    }

    /**
     * Gets the number of remaining unfilled portal frames.
     *
     * @return The number of frames.
     */
    public int getRemainingPortalFrames() throws SQLException {
        try (Connection conn = dataSource.getConnection(); PreparedStatement statement = conn.prepareStatement(
                """
                        SELECT COUNT(DISTINCT p.id) AS count
                        FROM teams
                                 JOIN portalframes p on teams.id = p.team_id
                        WHERE teams.disabled = 0
                          AND p.filled = 0;
                        """
        )) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("count");
            }
            return -1;
        }
    }

}
