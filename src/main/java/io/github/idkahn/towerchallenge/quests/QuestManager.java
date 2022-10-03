package io.github.idkahn.towerchallenge.quests;

import com.ibm.icu.impl.UtilityExtensions;
import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.hats.HatGUI;
import io.github.idkahn.towerchallenge.hats.HatUtil;
import io.github.idkahn.towerchallenge.towering.TowerListener;
import io.github.idkahn.towerchallenge.towering.TowerTeam;
import it.unimi.dsi.fastutil.Hash;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class QuestManager implements Listener {

    public static final String UI_NAME = "Quests:";

    private QuestListener listener;
    private QuestCommands commands;
    private ArrayList<Quest> quests;
    private EventManager eventManager;

    private Inventory questPicker;

    public QuestManager(EventManager eventManager) {
        this.eventManager = eventManager;
        listener = new QuestListener(this);
        commands = new QuestCommands(this);
        this.quests = new ArrayList<>();

        eventManager.getPlugin().getCommand("questbook").setExecutor(commands);
        Bukkit.getServer().getPluginManager().registerEvents(this, eventManager.getPlugin());

        loadQuests();
    }

    public EventManager getEventManager() {
        return eventManager;
    }

    public void initQuest() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.questConfigFile);

        HashMap questMap = new HashMap();

        String name = "Test Quest";
        ArrayList<String> description = new ArrayList<String>() {{
            add("This is a");
            add("test of a quest");
        }};
        ArrayList<String> criteria = new ArrayList<String>() {{
            add("1 Diamond");
            add("1 Healing or Regen Potion");
        }};
        ArrayList<String> reward = new ArrayList<String>() {{
            add("4 EXP");
            add("2 Speed Potions");
        }};

        questMap.put("name", name);
        questMap.put("description", description);
        questMap.put("criteria", criteria);
        questMap.put("reward", reward);

        ArrayList<Map> mapArrayList = new ArrayList<>() {{
            add(questMap);
        }};

        config.set("Quests", mapArrayList);

        try {
            config.save(TowerChallenge.questConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public ItemStack getBook() {
        ItemStack book = QuestUtil.setQuestbook(new ItemStack(Material.BOOK));
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.displayName(Component.text("Quest Book").decoration(TextDecoration.ITALIC, false));
        bookMeta.addEnchant(Enchantment.MENDING, 0, true);
        bookMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        bookMeta.lore(new ArrayList<Component>(){{
            add(Component.text("Right click with me in your hand"));
            add(Component.text("to open the quest menu!"));
        }});
        book.setItemMeta(bookMeta);
        return book;
    }

    private void configItemGroup(HashMap<String, Object> map, ArrayList<ItemStack> list) {
        String type = (String) map.get("type");
        ItemStack item;
        if (type.equals("potion")) {
            item = new ItemStack(Material.POTION, (int) map.get("amount"));
            PotionMeta itemMeta = (PotionMeta) item.getItemMeta();
            itemMeta.setBasePotionData(new PotionData(PotionType.valueOf((String) map.get("potion_type"))));
            String itemName = (String) map.get("name");
            if (itemName != null) {
                itemMeta.displayName(Component.text(itemName));
            }
            item.setItemMeta(itemMeta);
        } else if (type.equals("or")) {
            item = new ItemStack(Material.BAMBOO);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setCustomModelData(1);
            itemMeta.displayName(Component.text("OR").decoration(TextDecoration.ITALIC, false));
            item.setItemMeta(itemMeta);
        } else if (type.equals("item")) {
            String id = (String) map.get("item");
            if (id == null) {
                return;
            }
            String[] itemId = id.split(":");
            Material itemType;
            if (itemId.length == 1) {
                itemType = Material.valueOf(itemId[0].toUpperCase());
            } else {
                itemType = Material.valueOf(itemId[1].toUpperCase());
            }
            item = new ItemStack(itemType, (int) map.get("amount"));
            ItemMeta itemMeta = item.getItemMeta();
            String itemName = (String) map.get("name");
            if (itemName != null) {
                itemMeta.displayName(Component.text(itemName));
            }
            Object modelData = map.get("custom_model_data");
            if (modelData != null) {
                itemMeta.setCustomModelData((int) modelData);
            }
            item.setItemMeta(itemMeta);
        } else {
            item = new ItemStack(Material.BEDROCK);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.displayName(Component.text("ERROR: Check the quest config")
                    .color(NamedTextColor.DARK_RED)
                    .decoration(TextDecoration.ITALIC, false));
            item.setItemMeta(itemMeta);
        }
        list.add(item);
    }

    public void loadQuests() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.questConfigFile);
        quests.clear();

        List maps = config.getMapList("Quests");
//        Bukkit.getServer().sendMessage(Component.text(maps.size()));
        this.questPicker = Bukkit.createInventory(null, HatGUI.getInventorySize(quests.size()+1), Component.text(UI_NAME));

        ItemStack exit = QuestUtil.setButton(new ItemStack(Material.REDSTONE_BLOCK));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.displayName(Component.text("Exit").decoration(TextDecoration.ITALIC, false));
        exitMeta.setCustomModelData(2);
        exit.setItemMeta(exitMeta);
        this.questPicker.setItem(this.questPicker.getSize()-1, exit);


        for (Object o : maps) {
            Map<String, Object> quest = (Map<String, Object>) o;
            String configName = (String) quest.get("name");
            ArrayList<String> configDescription = (ArrayList<String>) quest.get("description");
            ArrayList<HashMap<String, Object>> configCriteria = (ArrayList<HashMap<String, Object>>) quest.get("criteria");
            ArrayList<HashMap<String, Object>> configReward = (ArrayList<HashMap<String, Object>>) quest.get("reward");
            String configCompleted = (String) quest.get("completed");

            Component name = Component.text(configName);

            ArrayList<Component> description = new ArrayList<>();
            for (String line : configDescription) {
                description.add(Component.text(line).decoration(TextDecoration.ITALIC, false));
            }

            ArrayList<ItemStack> criteria = new ArrayList<>();
            for (HashMap<String, Object> map : configCriteria) {
                configItemGroup(map, criteria);
            }

            ArrayList<ItemStack> reward = new ArrayList<>();
            for (HashMap<String, Object> map : configReward) {
                configItemGroup(map, reward);
            }

            TowerTeam completedTeam = eventManager.getTowerListener().getTeams().get(configCompleted);

            Quest newQuest = new Quest(this, name, description, criteria, reward, completedTeam);
            quests.add(newQuest);
            questPicker.addItem(newQuest.getItem());
//            Bukkit.getServer().sendMessage(Component.text(quests.size()));
//            Bukkit.getServer().sendMessage(Component.text(questPicker.getSize()));
        }

    }

    public void openQuestPicker(Player player) {
        player.openInventory(questPicker);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        if (!event.getInventory().equals(questPicker))
            return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir())
            return;
        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();

        if (QuestUtil.isButton(event.getCurrentItem())) {
            if (event.getSlot() == questPicker.getSize()-1) {
                player.closeInventory();
            } else {
                quests.get(event.getSlot()).openInventory(player);
            }
        }
    }

}
