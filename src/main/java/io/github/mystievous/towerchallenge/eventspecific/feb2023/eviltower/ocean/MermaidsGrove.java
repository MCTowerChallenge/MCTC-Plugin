package io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.ocean;

import io.github.mystievous.towerchallenge.quests.Dialogue;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.quests.entities.NPC;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.ValentinesUtil;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.EvilTower;
import io.github.mystievous.towerchallenge.quests.entities.OneTimeItemEntityHandler;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.towerchallenge.utility.NBTUtils;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.Dispenser;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MermaidsGrove implements Listener {

    public static final Location baseCraftingLocation = new Location(Worlds.eviltowers(), 43, 105, -70);
    public static final Location baseButtonLocation = new Location(Worlds.eviltowers(), 43, 106, -70);

    private final QuestManager questManager;
    private final EvilTower evilTower;
    private final TeamManager teamManager;
    private final int teamId;

    private final Location craftingLocation;
    private final Location buttonLocation;

    private final Dialogue finishOcean;

    public MermaidsGrove(TowerChallenge plugin, QuestManager questManager, EvilTower evilTower, TeamManager teamManager, int teamId) {
        this.questManager = questManager;
        this.evilTower = evilTower;
        this.teamManager = teamManager;
        this.teamId = teamId;

        this.craftingLocation = evilTower.offsetLocation(baseCraftingLocation);
        this.buttonLocation = evilTower.offsetLocation(baseButtonLocation);

        Bukkit.getPluginManager().registerEvents(this, plugin);

        NPC spirit = questManager.getSpirit();

        finishOcean = new Dialogue(teamManager, spirit.formatMessage("What, you thought I meant I loved the mermaid?"), 2.5d);
        finishOcean.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.literally_stole_from_me"));
        {
            Dialogue literallyStole = new Dialogue(teamManager, spirit.formatMessage("Ha! No, she literally stole from me."), 5.5d);
            finishOcean.setNext(literallyStole);
        }

    }

//    @EventHandler
//    public void onPlayerInteract(final PlayerInteractEvent event) {
//        EquipmentSlot hand = event.getHand();
//        if (hand != null && !hand.equals(EquipmentSlot.HAND))
//            return;
//        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
//            return;
//        Block block = event.getClickedBlock();
//        if (block != null && block.getLocation().equals(buttonLocation.toBlockLocation())) {
//            // Code for when the button is pushed
//        }
//    }

    @EventHandler
    public void onDispenserShoot(final BlockDispenseEvent event) {
        Block block = event.getBlock();
        if (block.getLocation().equals(craftingLocation.toBlockLocation())) {
            if (block.getState() instanceof Dispenser dispenser) {
//                Bukkit.getServer().sendMessage(Component.text("I am a dispenser"));
                Inventory inventory = dispenser.getInventory();

                List<ItemStack> items = new ArrayList<>();
                Collections.addAll(items, inventory.getContents());
                items.add(event.getItem());

                boolean hasTags = Arrays.stream(ValentinesUtil.oceanKeyFragmentTags).allMatch(
                    tag -> {
                        boolean value = items.stream().anyMatch(itemStack -> NBTUtils.boolState(tag, itemStack));
//                        Bukkit.getServer().sendMessage(Component.text(String.format("tag %s; match: %s", tag, value)));
                        return value;
                    }
                );

                if (hasTags) {
                    inventory.clear();
                    event.setItem(ValentinesUtil.oceanKey);
                    event.setCancelled(false);
                    TowerTeam team = teamManager.getTeam(teamId);
                    if (team != null && questManager.getTeamQuest(team).equals(QuestManager.OCEAN_SEARCH)) {
                        team.setInDialogue(true);
                        questManager.setTeamQuest(team, QuestManager.PICK_TOWER_ROOM);
                        finishOcean.play(team, () -> {
                            team.setInDialogue(false);
                        });
                    }
                    Location itemLocation = craftingLocation.toBlockLocation().add(0.5, 1, 0.5);
                    Item droppedItem = (Item) itemLocation.getWorld().spawnEntity(itemLocation, EntityType.DROPPED_ITEM, false);
                    droppedItem.setVelocity(new Vector(0, 0.2, 0));
                    droppedItem.setItemStack(ValentinesUtil.randomDyeBundle());
                    return;
                }
                event.setCancelled(true);
            }
        }
    }

}
