package io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.ocean;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.ValentinesUtil;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower.EvilTower;
import io.github.mystievous.towerchallenge.quests.Dialogue;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.quests.entities.NPC;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
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

    private final TowerChallenge plugin;
    private final QuestManager questManager;
    private final TeamManager teamManager;
    private final int teamId;

    private final Location craftingLocation;

    private final Dialogue finishOcean;

    public MermaidsGrove(TowerChallenge plugin, QuestManager questManager, EvilTower evilTower, TeamManager teamManager, int teamId) {
        this.plugin = plugin;
        this.questManager = questManager;
        this.teamManager = teamManager;
        this.teamId = teamId;

        this.craftingLocation = evilTower.offsetLocation(baseCraftingLocation);

        Bukkit.getPluginManager().registerEvents(this, plugin);

        NPC spirit = questManager.getSpirit();

        finishOcean = new Dialogue(teamManager, spirit.formatMessage("What, you thought I meant I loved the mermaid?"), 2.5d);
        finishOcean.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.literally_stole_from_me"));
        {
            Dialogue literallyStole = new Dialogue(teamManager, spirit.formatMessage("Ha! No, she literally stole from me."), 5.5d);
            finishOcean.setNext(literallyStole);
        }

    }

    @EventHandler
    public void onDispenserShoot(final BlockDispenseEvent event) {
        Block block = event.getBlock();
        if (block.getLocation().equals(craftingLocation.toBlockLocation())) {
            if (block.getState() instanceof Dispenser dispenser) {
                Inventory inventory = dispenser.getInventory();

                List<ItemStack> items = new ArrayList<>();
                Collections.addAll(items, inventory.getContents());
                items.add(event.getItem());

                boolean hasTags = Arrays.stream(ValentinesUtil.oceanKeyFragmentTags).allMatch(
                    tag -> items.stream().anyMatch(itemStack -> NBTUtils.boolState(plugin, tag, itemStack))
                );

                if (hasTags) {
                    inventory.clear();
                    event.setItem(ValentinesUtil.oceanKey);
                    event.setCancelled(false);
                    TowerTeam team = teamManager.getTeam(teamId);
                    if (team != null && questManager.getTeamQuest(team).equals(QuestManager.OCEAN_SEARCH)) {
                        team.setInDialogue(true);
                        questManager.setTeamQuest(team, QuestManager.PICK_TOWER_ROOM);
                        finishOcean.play(team, () -> team.setInDialogue(false));
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
