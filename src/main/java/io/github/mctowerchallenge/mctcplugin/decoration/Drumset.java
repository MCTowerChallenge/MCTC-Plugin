package io.github.mctowerchallenge.mctcplugin.decoration;

import io.github.mctowerchallenge.mctcplugin.interaction.InteractableTagManager;
import io.github.mctowerchallenge.mctcplugin.interaction.InteractableTaggedEntity;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;

public class Drumset {

    public static void register() {
        InteractableTaggedEntity snare = new InteractableTaggedEntity("snare");
        snare.addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        snare.setDefaultInteractionHandler((team, playerInteractEntityEvent) -> {
            Entity entity = playerInteractEntityEvent.getRightClicked();
            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_NOTE_BLOCK_SNARE, SoundCategory.RECORDS, 1, (float) Math.pow(2, (float) 2/12));
        });
        InteractableTagManager.registerTag(snare);

        InteractableTaggedEntity kick = new InteractableTaggedEntity("kick");
        kick.addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        kick.setDefaultInteractionHandler((team, playerInteractEntityEvent) -> {
            Entity entity = playerInteractEntityEvent.getRightClicked();
            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.RECORDS, 1, (float) Math.pow(2, (float) -13/12));
        });
        InteractableTagManager.registerTag(kick);

        InteractableTaggedEntity hihat = new InteractableTaggedEntity("hihat");
        hihat.addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        hihat.setDefaultInteractionHandler((team, playerInteractEntityEvent) -> {
            Entity entity = playerInteractEntityEvent.getRightClicked();
            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, SoundCategory.RECORDS, 1, (float) Math.pow(2, (float) 6/12));
        });
        InteractableTagManager.registerTag(hihat);

        InteractableTaggedEntity floorTom = new InteractableTaggedEntity("floor-tom");
        floorTom.addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        floorTom.setDefaultInteractionHandler((team, playerInteractEntityEvent) -> {
            Entity entity = playerInteractEntityEvent.getRightClicked();
            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.RECORDS, 1, (float) Math.pow(2, (float) 1/12));
        });
        InteractableTagManager.registerTag(floorTom);

        InteractableTaggedEntity highTom = new InteractableTaggedEntity("high-tom");
        highTom.addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        highTom.setDefaultInteractionHandler((team, playerInteractEntityEvent) -> {
            Entity entity = playerInteractEntityEvent.getRightClicked();
            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.RECORDS, 1, (float) Math.pow(2, (float) 11/12));
        });
        InteractableTagManager.registerTag(highTom);

        InteractableTaggedEntity midTom = new InteractableTaggedEntity("mid-tom");
        midTom.addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        midTom.setDefaultInteractionHandler((team, playerInteractEntityEvent) -> {
            Entity entity = playerInteractEntityEvent.getRightClicked();
            entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, SoundCategory.RECORDS, 1, (float) Math.pow(2, (float) 6/12));
        });
        InteractableTagManager.registerTag(midTom);

        InteractableTaggedEntity highCrash = new InteractableTaggedEntity("high-crash");
        highCrash.addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        highCrash.setDefaultInteractionHandler((team, playerInteractEntityEvent) -> {
            Entity entity = playerInteractEntityEvent.getRightClicked();
            entity.getWorld().playSound(entity.getLocation(), "mctc:pipe", SoundCategory.RECORDS, 1, (float) Math.pow(2, (float) 3/12));
        });
        InteractableTagManager.registerTag(highCrash);

        InteractableTaggedEntity crash = new InteractableTaggedEntity("crash");
        crash.addQuestInteractionHandler(QuestTags.PERFORMANCE, (team, playerInteractEntityEvent) -> {
        });
        crash.setDefaultInteractionHandler((team, playerInteractEntityEvent) -> {
            Entity entity = playerInteractEntityEvent.getRightClicked();
            entity.getWorld().playSound(entity.getLocation(), "mctc:pipe", SoundCategory.RECORDS, 1, 1);
        });
        InteractableTagManager.registerTag(crash);
    }

}
