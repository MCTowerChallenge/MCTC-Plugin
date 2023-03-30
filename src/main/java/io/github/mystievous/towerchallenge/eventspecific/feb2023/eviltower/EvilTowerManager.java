package io.github.mystievous.towerchallenge.eventspecific.feb2023.eviltower;

import com.onarandombox.MultiversePortals.event.MVPortalEvent;
import io.github.mystievous.mystigui.element.ButtonElement;
import io.github.mystievous.mystigui.page.Gui;
import io.github.mystievous.mystigui.page.ListGui;
import io.github.mystievous.mystigui.page.Openable;
import io.github.mystievous.mystigui.page.PresetGui;
import io.github.mystievous.towerchallenge.gui.Icons;
import io.github.mystievous.towerchallenge.quests.Dialogue;
import io.github.mystievous.towerchallenge.quests.entities.NPC;
import io.github.mystievous.towerchallenge.quests.entities.OneTimeItemEntityHandler;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.eventspecific.feb2023.ValentinesUtil;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import io.github.mystievous.mysticore.TextUtil;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages the Evil Tower logic
 * for the Feb2023 event.
 * <p>
 * This mainly handles teleporting
 * players to their corresponding towers.
 */
public class EvilTowerManager implements Listener, Openable {

    private static final Location baseLocation = new Location(Worlds.eviltowers(), 1, 98, -9);

    public static final Location limboLocation = new Location(Worlds.eviltowers(), -20.5, 65, -15.5, 180, 0);
    public static final Location towerExit = new Location(Worlds.Feb2023(), 72.5, 94, -2177, 90, 0);
    public static final Location towerTop = new Location(Worlds.Feb2023(), 80.5, 127, -2180, 0, -20);


    public static final String enterTowerPortalName = "to_towers";
    public static final String exitTowerTopPortalName = "tower_exit";

    private static final Map<Integer, Location> teamLocations = new HashMap<>() {{
        put(1, baseLocation); // God
        put(2, new Location(Worlds.eviltowers(), -199, 98, -9));
        put(3, new Location(Worlds.eviltowers(), -399, 98, -9));
        put(6, new Location(Worlds.eviltowers(), -599, 98, -9));
        put(7, new Location(Worlds.eviltowers(), -799, 98, -9));
        put(8, new Location(Worlds.eviltowers(), -999, 98, -9));
        put(10, new Location(Worlds.eviltowers(), -1199, 98, -9));
        put(11, new Location(Worlds.eviltowers(), -1399, 98, -9));
        put(13, new Location(Worlds.eviltowers(), -1599, 98, -9));
        put(14, new Location(Worlds.eviltowers(), -1799, 98, -9));
    }};

    private final TowerChallenge plugin;
    private final Map<Integer, EvilTower> evilTowers;
    private final QuestManager questManager;
    private final TeamManager teamManager;
    private PresetGui gui;

    private final Dialogue enterTower;

    public EvilTowerManager(TowerChallenge plugin, TeamManager teamManager, QuestManager questManager) {
        this.plugin = plugin;
        this.teamManager = teamManager;
        this.questManager = questManager;

        NPC spirit = questManager.getSpirit();

        enterTower = new Dialogue(teamManager, spirit.formatMessage("Ah, guests! Welcome to my domain."), 4.0d);
        enterTower.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.welcome_to_my_domain"));
        {
            Dialogue seekYourFriend = new Dialogue(teamManager, spirit.formatMessage("I'm sure you seek your friend, my sibling cursed with fame."), 4.5d);
            seekYourFriend.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.seek_your_friend"));
            enterTower.setNext(seekYourFriend);

            Dialogue playMyGame = new Dialogue(teamManager, spirit.formatMessage("Why yes, he does reside here, but first you'll play my game."), 5.0d);
            playMyGame.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.play_my_game"));
            seekYourFriend.setNext(playMyGame);

            Dialogue completeMyTasks = new Dialogue(teamManager, spirit.formatMessage("Complete my many tasks, and you may free him all the same."), 4.0d);
            completeMyTasks.setSoundKey(Key.key(TowerChallenge.MCTC_NAMESPACE, "spirit.complete_my_tasks"));
            playMyGame.setNext(completeMyTasks);
        }

        evilTowers = new HashMap<>();
        for (Map.Entry<Integer, Location> entry : teamLocations.entrySet()) {
            evilTowers.put(entry.getKey(), new EvilTower(plugin, questManager, teamManager, entry.getValue().clone().subtract(baseLocation).toVector(), entry.getKey()));
        }

        new OneTimeItemEntityHandler(plugin, teamManager, ValentinesUtil.OCEAN_TAG, ValentinesUtil.oceanKeyFragmentTags[0], null, ValentinesUtil.oceanKeyFragments[0]);
        new OneTimeItemEntityHandler(plugin, teamManager, ValentinesUtil.OCEAN_TAG, ValentinesUtil.oceanKeyFragmentTags[1], null, ValentinesUtil.oceanKeyFragments[1]);
        new OneTimeItemEntityHandler(plugin, teamManager, ValentinesUtil.OCEAN_TAG, ValentinesUtil.oceanKeyFragmentTags[2], null, ValentinesUtil.oceanKeyFragments[2]);
        new OneTimeItemEntityHandler(plugin, teamManager, ValentinesUtil.OCEAN_TAG, ValentinesUtil.oceanKeyFragmentTags[3], null, ValentinesUtil.oceanKeyFragments[3]);
        new OneTimeItemEntityHandler(plugin, teamManager, ValentinesUtil.OCEAN_TAG, ValentinesUtil.oceanKeyFragmentTags[4], null, ValentinesUtil.oceanKeyFragments[4]);
        new OneTimeItemEntityHandler(plugin, teamManager, ValentinesUtil.OCEAN_TAG, ValentinesUtil.oceanKeyFragmentTags[5], null, ValentinesUtil.oceanKeyFragments[5]);
        new OneTimeItemEntityHandler(plugin, teamManager, ValentinesUtil.OCEAN_TAG, ValentinesUtil.oceanKeyFragmentTags[6], null, ValentinesUtil.oceanKeyFragments[6]);
        new OneTimeItemEntityHandler(plugin, teamManager, ValentinesUtil.OCEAN_TAG, ValentinesUtil.oceanKeyFragmentTags[7], null, ValentinesUtil.oceanKeyFragments[7]);
        new OneTimeItemEntityHandler(plugin, teamManager, ValentinesUtil.OCEAN_TAG, ValentinesUtil.oceanKeyFragmentTags[8], null, ValentinesUtil.oceanKeyFragments[8]);

        loadGui();

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    public void loadGui() {
        gui = new PresetGui(plugin, Component.text("Evil Tower Management"), 3);

        try {
            ListGui questItems = new ListGui(plugin, Component.text("Quest Items: "), new ButtonElement(Icons.backItem(), gui::openInventory));

            questItems.addElement(new ButtonElement(ValentinesUtil.galleryKey, player -> player.getInventory().addItem(ValentinesUtil.galleryKey)));
            questItems.addElement(new ButtonElement(ValentinesUtil.mazeKey, player -> player.getInventory().addItem(ValentinesUtil.mazeKey)));
            questItems.addElement(new ButtonElement(ValentinesUtil.oceanKey, player -> player.getInventory().addItem(ValentinesUtil.oceanKey)));

            for (ItemStack item : ValentinesUtil.oceanKeyFragments) {
                questItems.addElement(new ButtonElement(item, player -> player.getInventory().addItem(item)));
            }

            questItems.addElement(new ButtonElement(ValentinesUtil.bundles[0], player -> player.getInventory().addItem(ValentinesUtil.randomDyeBundle())));
            questItems.addElement(new ButtonElement(new ItemStack(Material.BUNDLE), player -> player.getInventory().addItem(ValentinesUtil.randomBlockBundle())));
            questItems.addElement(new ButtonElement(ValentinesUtil.diamondHorseshoe, player -> player.getInventory().addItem(ValentinesUtil.diamondHorseshoe)));

            ButtonElement giveItems = teamManager.getDatabase().getModel(94, false, false);
            ItemStack item = giveItems.getItem();
            ItemMeta meta = item.getItemMeta();
            meta.displayName(TextUtil.noItalic("Quest Items"));
            item.setItemMeta(meta);
            giveItems.setConsumer(questItems::openInventory);
            gui.placeElement(2, 5, giveItems);
        } catch (SQLException e) {
            Bukkit.getLogger().warning("Error getting icon for Evil Tower Manager: " + e.getMessage());
        }
    }

    @EventHandler
    public void onPortal(final MVPortalEvent event) {

//        event.getTeleportee().sendMessage(event.getSendingPortal().getName());
        if (event.getSendingPortal().getName().equals(enterTowerPortalName)) {
            event.setCancelled(true);
            Player player = event.getTeleportee();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team != null && !(team.getCurrentQuestId().equals(QuestManager.BUTTSTALLION_START))) {
                EvilTower evilTower = evilTowers.get(team.getDatabaseId());
                if (evilTower != null) {
                    player.teleport(evilTower.getEnterLocation());
                    if (team.getCurrentQuestId().equals(QuestManager.INVESTIGATE_TOWER)) {
                        team.setInDialogue(true);
                        questManager.setTeamQuest(team, QuestManager.PICK_TOWER_ROOM);
                        enterTower.play(team, () -> team.setInDialogue(false));
                    }
                    return;
                }

                // teleport to Limbo
                player.teleport(EvilTowerManager.limboLocation);

            }

            player.sendMessage(TextUtil.formatText("A dark force keeps you from entering...").decoration(TextDecoration.ITALIC, true));
        }

        if (event.getSendingPortal().getName().equals(exitTowerTopPortalName)) {
            event.setCancelled(true);
            Player player = event.getTeleportee();
            TowerTeam team = teamManager.getPlayerTeam(player);
            if (team != null) {

                EvilTower evilTower = evilTowers.get(team.getDatabaseId());
                if (evilTower != null) {
                    player.teleport(evilTower.getTopEnterLocation());
                    return;
                }

                // teleport to Limbo
                player.teleport(EvilTowerManager.limboLocation);

            }
        }

    }

    @Override
    public Gui getGui(Player player) {
        return gui;
    }
}
