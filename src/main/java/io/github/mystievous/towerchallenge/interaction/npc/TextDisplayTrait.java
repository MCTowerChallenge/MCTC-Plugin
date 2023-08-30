package io.github.mystievous.towerchallenge.interaction.npc;

import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.HologramTrait;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@TraitName("textdisplaytrait")
public class TextDisplayTrait extends HologramTrait {

    public static void logException(Exception exception) {
        Bukkit.getLogger().severe(exception.getMessage());
        exception.printStackTrace();
    }

    public TextDisplayTrait() {
        super();
        try {
            Field name = Class.forName("net.citizensnpcs.api.trait.Trait").getDeclaredField("name");
            name.setAccessible(true);
            name.set(this, "textdisplaytrait");
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            logException(e);
        }
    }

    public void reloadNameHologram() {
        try {
            Class<?> hologramTrait = Class.forName("net.citizensnpcs.trait.HologramTrait");

            Field nameLineField = hologramTrait.getDeclaredField("nameLine");
            nameLineField.setAccessible(true);
            Object nameLine = nameLineField.get(this);

            Class<?> hologramLine = Class.forName("net.citizensnpcs.trait.HologramTrait$HologramLine");
            Method removeNPC = hologramLine.getDeclaredMethod("removeNPC");
            removeNPC.setAccessible(true);

            removeNPC.invoke(nameLine);

            if (!npc.isSpawned()) {
                return;
            }

            Method spawnNPC = hologramLine.getDeclaredMethod("spawnNPC", double.class);
            spawnNPC.setAccessible(true);
            spawnNPC.invoke(nameLine, 0.0d);
        } catch (NoSuchFieldException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            logException(e);
        }
    }

    @Override
    public void onAttach() {
        super.onAttach();
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        setUseTextDisplay(true);
        reloadNameHologram();
        npc.removeTrait(HologramTrait.class);
    }
}
