package io.github.mystievous.towerchallenge.eventspecific.jun2023.quests;

import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.quests.instances.QuestInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Piston;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class Door {

    private final QuestInstance instance;
    private final BlockSequence open;
    private final BlockSequence reset;

    public Door(QuestInstance instance, Vector offset) {
        this.instance = instance;

        Piston northPiston = (Piston) Bukkit.createBlockData(Material.STICKY_PISTON);
        northPiston.setFacing(BlockFace.NORTH);

        Piston upPiston = (Piston) Bukkit.createBlockData(Material.STICKY_PISTON);
        upPiston.setFacing(BlockFace.UP);

        Piston eastPiston = (Piston) Bukkit.createBlockData(Material.STICKY_PISTON);
        eastPiston.setFacing(BlockFace.EAST);

        Piston westPiston = (Piston) Bukkit.createBlockData(Material.STICKY_PISTON);
        westPiston.setFacing(BlockFace.WEST);

        Piston downPiston = (Piston) Bukkit.createBlockData(Material.STICKY_PISTON);
        downPiston.setFacing(BlockFace.DOWN);

        BlockData redstoneBlock = Bukkit.createBlockData(Material.REDSTONE_BLOCK);

        BlockData glowstone = Bukkit.createBlockData(Material.GLOWSTONE);

        BlockData air = Bukkit.createBlockData(Material.AIR);

        open = new BlockSequence(4);
        open.putAll(new HashMap<>(){{
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 66, 7)).add(offset), glowstone);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 66, 7)).add(offset), glowstone);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 63, 7)).add(offset), upPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 63, 7)).add(offset), upPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 63, 7)).add(offset), upPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 62, 7)).add(offset), redstoneBlock);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 62, 7)).add(offset), redstoneBlock);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 62, 7)).add(offset), redstoneBlock);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 66, 9)).add(offset), northPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 66, 9)).add(offset), northPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 66, 9)).add(offset), northPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 67, 9)).add(offset), northPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 67, 9)).add(offset), northPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 67, 9)).add(offset), northPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 66, 10)).add(offset), redstoneBlock);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 66, 10)).add(offset), redstoneBlock);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 66, 10)).add(offset), redstoneBlock);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 67, 10)).add(offset), redstoneBlock);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 67, 10)).add(offset), redstoneBlock);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 67, 10)).add(offset), redstoneBlock);
        }});

        BlockSequence pullBack = new BlockSequence(4);
        pullBack.putAll(new HashMap<>(){{
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 62, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 62, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 62, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 66, 10)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 66, 10)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 66, 10)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 67, 10)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 67, 10)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 67, 10)).add(offset), air);
        }});
        open.setNext(pullBack);

        BlockSequence setupSplit = new BlockSequence(4);
        setupSplit.putAll(new HashMap<>(){{
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 63, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 63, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 63, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 66, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 66, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 66, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 67, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 67, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 67, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -5, 66, 8)).add(offset), eastPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -6, 66, 8)).add(offset), redstoneBlock);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), 1, 66, 8)).add(offset), westPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), 2, 66, 8)).add(offset), redstoneBlock);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 64, 8)).add(offset), upPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 63, 8)).add(offset), redstoneBlock);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 69, 8)).add(offset), downPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 69, 8)).add(offset), downPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 69, 8)).add(offset), downPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 69, 9)).add(offset), redstoneBlock);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 69, 9)).add(offset), redstoneBlock);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 69, 9)).add(offset), redstoneBlock);
        }});
        pullBack.setNext(setupSplit);

        BlockSequence split = new BlockSequence(4);
        split.putAll(new HashMap<>(){{
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -6, 66, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), 2, 66, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 63, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 69, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 69, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 69, 9)).add(offset), air);
        }});
        setupSplit.setNext(split);

        BlockSequence clearSplit = new BlockSequence(4);
        clearSplit.putAll(new HashMap<>(){{
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -5, 66, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), 1, 66, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 64, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 69, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 69, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 69, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 63, 8)).add(offset), upPiston);
        }});
        split.setNext(clearSplit);

        BlockSequence setupFloor = new BlockSequence(4);
        setupFloor.putAll(new HashMap<>(){{
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 62, 8)).add(offset), redstoneBlock);
        }});

        BlockSequence floor = new BlockSequence(4);
        floor.putAll(new HashMap<>(){{
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 62, 8)).add(offset), air);
        }});
        clearSplit.setNext(floor);

        BlockSequence setupFlush = new BlockSequence(4);
        setupFlush.putAll(new HashMap<>(){{
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 64, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 63, 8)).add(offset), upPiston);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 62, 8)).add(offset), redstoneBlock);
        }});
        floor.setNext(setupFlush);

        BlockSequence flush = new BlockSequence(4);
        flush.putAll(new HashMap<>(){{
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 62, 8)).add(offset), air);
        }});
        setupFlush.setNext(flush);

        BlockSequence clear = new BlockSequence(4);
        clear.putAll(new HashMap<>(){{
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 63, 8)).add(offset), air);
        }});
        flush.setNext(clear);

        BlockData cutSandstone = Bukkit.createBlockData(Material.CUT_SANDSTONE);
        BlockData redstoneLamp = Bukkit.createBlockData(Material.REDSTONE_LAMP);
        BlockData smoothSandstone = Bukkit.createBlockData(Material.SMOOTH_SANDSTONE);
        BlockData sandstone = Bukkit.createBlockData(Material.SANDSTONE);

        reset = new BlockSequence(0);
        reset.putAll(new HashMap<>(){{
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 63, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 63, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 63, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 62, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 62, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 62, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 66, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 66, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 66, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 67, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 67, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 67, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 66, 10)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 66, 10)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 66, 10)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 67, 10)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 67, 10)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 67, 10)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -5, 66, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -6, 66, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), 1, 66, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), 2, 66, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 63, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 69, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 69, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 69, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 69, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 69, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 69, 9)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 62, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 64, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), 0, 66, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 64, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 64, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 64, 7)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -4, 66, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 68, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 68, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 68, 8)).add(offset), air);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 67, 7)).add(offset), cutSandstone);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 67, 7)).add(offset), cutSandstone);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 67, 7)).add(offset), cutSandstone);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 66, 7)).add(offset), redstoneLamp);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 66, 7)).add(offset), smoothSandstone);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 66, 7)).add(offset), redstoneLamp);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -3, 65, 7)).add(offset), sandstone);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -2, 65, 7)).add(offset), sandstone);
            put(instance.offsetLocation(new Location(Worlds.Jun2023_quest(), -1, 65, 7)).add(offset), sandstone);
        }});

    }

    public void open(Runnable callback) {
        open.play(callback);
    }

    public void reset(Runnable callback) {
        reset.play(callback);
    }

}
