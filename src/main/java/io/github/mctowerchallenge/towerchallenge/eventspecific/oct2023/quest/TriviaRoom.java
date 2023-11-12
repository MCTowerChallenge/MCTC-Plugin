package io.github.mctowerchallenge.towerchallenge.eventspecific.oct2023.quest;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import io.github.mctowerchallenge.towerchallenge.Worlds;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CommandBlock;
import org.bukkit.block.data.Directional;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class TriviaRoom implements Listener {

    public static final Location[] TEMPLATE_BOUNDS = new Location[]{
            new Location(Worlds.Oct2023_quest(), -33, 65, -11),
            new Location(Worlds.Oct2023_quest(), -29, 69, -3)
    };

    public static final Location[] TEMPLATE_ANSWERS = new Location[]{
            new Location(Worlds.Oct2023_quest(), -27, 66, -10),
            new Location(Worlds.Oct2023_quest(), -27, 66, -8),
            new Location(Worlds.Oct2023_quest(), -27, 66, -6),
            new Location(Worlds.Oct2023_quest(), -27, 66, -4)
    };

    public static final Location TEMPLATE_LEAVE_COMMAND_BLOCK = new Location(Worlds.Oct2023_quest(), -33, 66, -13);

    private final int roomNumber;
    private final Trivia triviaInstance;
    private final Oct2023QuestInstance oct2023QuestInstance;
    private final CuboidRegion instanceBounds;

    public TriviaRoom(String label, int[] correctIndices, int roomNumber, Trivia triviaInstance, Oct2023QuestInstance oct2023QuestInstance, Plugin plugin, boolean finalRoom) {
        this.roomNumber = roomNumber;
        this.triviaInstance = triviaInstance;
        this.oct2023QuestInstance = oct2023QuestInstance;

        this.instanceBounds = new CuboidRegion(
                BukkitAdapter.adapt(TEMPLATE_BOUNDS[0].getWorld()),
                BukkitAdapter.asBlockVector(offsetLocation(TEMPLATE_BOUNDS[0])),
                BukkitAdapter.asBlockVector(offsetLocation(TEMPLATE_BOUNDS[1]))
        );

        for (int i = 0; i < TEMPLATE_ANSWERS.length; i++) {
            Location location = offsetLocation(TEMPLATE_ANSWERS[i]);
            Block commandBlock = location.getBlock();
            commandBlock.setType(Material.COMMAND_BLOCK);
            CommandBlock commandBlockState = (CommandBlock) commandBlock.getState();
            Directional commandBlockData = (Directional) commandBlockState.getBlockData();
            commandBlockData.setFacing(BlockFace.UP);
            commandBlockState.setBlockData(commandBlockData);

            Block soundCommandBlock = commandBlock.getRelative(BlockFace.UP);
            soundCommandBlock.setType(Material.CHAIN_COMMAND_BLOCK);
            CommandBlock soundCommandBlockState = (CommandBlock) soundCommandBlock.getState();
            Directional soundCommandBlockData = (Directional) soundCommandBlockState.getBlockData();
            soundCommandBlockData.setFacing(BlockFace.UP);
            soundCommandBlockState.setBlockData(soundCommandBlockData);

            Block soundCommandBlock2 = soundCommandBlock.getRelative(BlockFace.UP);
            soundCommandBlock2.setType(Material.CHAIN_COMMAND_BLOCK);
            CommandBlock soundCommandBlockState2 = (CommandBlock) soundCommandBlock2.getState();

            boolean correct = false;
            for (int index : correctIndices) {
                if (i == index) {
                    correct = true;
                    break;
                }
            }
            if (correct) {
                if (!finalRoom) {
                    Location teleportLocation = offsetLocation(Trivia.TEMPLATE_ENTER_LOCATION).add(0, Trivia.BLOCK_HEIGHT_BETWEEN_ROOMS, 0);
                    commandBlockState.setCommand(teleportCommand(teleportLocation));
                    soundCommandBlockState.setCommand(String.format("playsound minecraft:block.note_block.bell block @a %f %f %f 1 0.793701", teleportLocation.getX(), teleportLocation.getY(), teleportLocation.getZ()));
                    soundCommandBlockState2.setCommand(String.format("playsound minecraft:block.note_block.bell block @a %f %f %f 1 1", teleportLocation.getX(), teleportLocation.getY(), teleportLocation.getZ()));
                } else {
                    Location teleportLocation = triviaInstance.getLeaveLocation();
                    commandBlockState.setCommand(teleportCommand(teleportLocation));
                    BlockVector3 minPoint = instanceBounds.getMinimumPoint();
                    BlockVector3 maxPoint = instanceBounds.getMaximumPoint();
                    Location leaveLocation = triviaInstance.getLeaveLocation();
                    commandBlockState.setCommand(String.format("tower completetrivia %d %s %d %d %d %d %d %d %f %f %f %f %f",
                            oct2023QuestInstance.getTeam().getDatabaseId(), location.getWorld().getName(),
                            minPoint.getX(), minPoint.getY(), minPoint.getZ(), maxPoint.getX(), maxPoint.getY(), maxPoint.getZ(),
                            leaveLocation.getX(), leaveLocation.getY(), leaveLocation.getZ(), leaveLocation.getYaw(), leaveLocation.getPitch()));
                    soundCommandBlockState.setCommand(String.format("playsound minecraft:block.note_block.bell block @a %f %f %f 1 0.793701", teleportLocation.getX(), teleportLocation.getY(), teleportLocation.getZ()));
                    soundCommandBlockState2.setCommand(String.format("playsound minecraft:block.note_block.bell block @a %f %f %f 1 1", teleportLocation.getX(), teleportLocation.getY(), teleportLocation.getZ()));
                }
            } else {
                Location teleportLocation = triviaInstance.getEnterLocation();
                commandBlockState.setCommand(teleportCommand(teleportLocation));
                soundCommandBlockState.setCommand(String.format("playsound minecraft:block.note_block.bit block @a %f %f %f 1 0", teleportLocation.getX(), teleportLocation.getY(), teleportLocation.getZ()));
                soundCommandBlockState2.setCommand(String.format("playsound minecraft:block.note_block.bit block @a %f %f %f 1 0.707107", teleportLocation.getX(), teleportLocation.getY(), teleportLocation.getZ()));                // 1 0.707107
            }
            commandBlockState.update();
            soundCommandBlockState.update();
            soundCommandBlockState2.update();
        }

        Block commandBlock = oct2023QuestInstance.offsetLocation(TEMPLATE_LEAVE_COMMAND_BLOCK).getBlock();
        commandBlock.setType(Material.COMMAND_BLOCK);
        CommandBlock commandBlockState = (CommandBlock) commandBlock.getState();
        Location leaveLocation = triviaInstance.getLeaveLocation();
        commandBlockState.setCommand(String.format("tp @p %f %f %f %f %f", leaveLocation.getX(), leaveLocation.getY(), leaveLocation.getZ(), leaveLocation.getYaw(), leaveLocation.getPitch()));
        commandBlockState.update();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    private String teleportCommand(Location location) {
        BlockVector3 minPoint = instanceBounds.getMinimumPoint();

        return String.format("tp @a[x=%d,y=%d,z=%d,dx=%d,dy=%d,dz=%d] %f %f %f %f %f",
                minPoint.getX(),
                minPoint.getY(),
                minPoint.getZ(),
                instanceBounds.getWidth(),
                instanceBounds.getHeight(),
                instanceBounds.getLength(),
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch());
    }

    private Location offsetLocation(Location location) {
        return oct2023QuestInstance.offsetLocation(location).add(new Vector(0, roomNumber * Trivia.BLOCK_HEIGHT_BETWEEN_ROOMS, 0));
    }

}
