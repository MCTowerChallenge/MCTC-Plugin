package io.github.idkahn.towerchallenge.quests;

import io.github.idkahn.towerchallenge.EventManager;
import io.github.idkahn.towerchallenge.TowerChallenge;
import io.github.idkahn.towerchallenge.hats.HatGUI;
import io.github.idkahn.towerchallenge.towering.ParticipantTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestManager implements Listener {

    public static final String UI_NAME = "Quests:";

    private final HashMap<String, Quest> quests;
    private final EventManager eventManager;

    private Inventory questPicker;
    private Inventory completePicker;
    private int stage;

    public QuestManager(EventManager eventManager) {
        this.eventManager = eventManager;
        QuestListener listener = new QuestListener(this);
        QuestCommands commands = new QuestCommands(this);
        this.quests = new HashMap<>();
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
            add(Component.text("Right click with me in your hand").decoration(TextDecoration.ITALIC, false).color(TowerChallenge.PRIMARY_COLOR));
            add(Component.text("to open the quest menu!").decoration(TextDecoration.ITALIC, false).color(TowerChallenge.PRIMARY_COLOR));
        }});
        book.setItemMeta(bookMeta);
        return book;
    }

    private void configItemGroup(HashMap<String, Object> map, ArrayList<ItemStack> list) {
        String type = (String) map.get("type");
        ItemStack item;
        switch (type) {
            case "potion" -> {
                item = new ItemStack(Material.POTION, (int) map.get("amount"));
                PotionMeta itemMeta = (PotionMeta) item.getItemMeta();
                itemMeta.setBasePotionData(new PotionData(PotionType.valueOf((String) map.get("potion_type"))));
                String itemName = (String) map.get("name");
                if (itemName != null) {
                    itemMeta.displayName(Component.text(itemName));
                }
                item.setItemMeta(itemMeta);
            }
            case "or" -> {
                item = new ItemStack(Material.BAMBOO);
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.setCustomModelData(1);
                itemMeta.displayName(Component.text("OR").decoration(TextDecoration.ITALIC, false));
                item.setItemMeta(itemMeta);
            }
            case "voucher" -> {
                String itemName = (String) map.get("name");
                int amount = (int) map.get("amount");
                item = QuestUtil.setVoucher(new BlockVoucher(itemName, amount));
            }
            case "item" -> {
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
                    itemMeta.displayName(Component.text(itemName).decoration(TextDecoration.ITALIC, false));
                }
                Object objEnchanted = map.get("enchanted");
                if (objEnchanted != null) {
                    if ((boolean) objEnchanted) {
                        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                        itemMeta.addEnchant(Enchantment.MENDING, 1, false);
                    }
                }
                Object modelData = map.get("custom_model_data");
                if (modelData != null) {
                    itemMeta.setCustomModelData((int) modelData);
                }
                item.setItemMeta(itemMeta);
            }
            default -> {
                item = new ItemStack(Material.BEDROCK);
                ItemMeta itemMeta = item.getItemMeta();
                itemMeta.displayName(Component.text("ERROR: Check the quest config")
                        .color(NamedTextColor.DARK_RED)
                        .decoration(TextDecoration.ITALIC, false));
                item.setItemMeta(itemMeta);
            }
        }
        list.add(item);
    }

    public void loadQuests() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(TowerChallenge.questConfigFile);
        HashMap<String, List<Player>> playersOpenInventories = new HashMap<>();
        for (Map.Entry<String, Quest> entry : quests.entrySet()) {
            Quest quest = entry.getValue();
            playersOpenInventories.put(quest.getTextName(), quest.getViewers());
            quest.closeInventories();
        }
        quests.clear();

        stage = config.getInt("Current Stage");
        List<?> stages = config.getList("Stages");
        if (stages == null || stages.size() == 0) {
            return;
        }
        List<String> stageQuests = (List<String>) stages.get(stage);

//        Bukkit.getServer().sendMessage(Component.text(maps.size()));
        this.questPicker = Bukkit.createInventory(null, HatGUI.getInventorySize(stageQuests.size()+1), Component.text(UI_NAME));
        this.completePicker = Bukkit.createInventory(null, HatGUI.getInventorySize(stageQuests.size()+1), Component.text(UI_NAME));

        ItemStack exit = QuestUtil.setButton(new ItemStack(Material.REDSTONE_BLOCK));
        ItemMeta exitMeta = exit.getItemMeta();
        exitMeta.displayName(Component.text("Exit").decoration(TextDecoration.ITALIC, false));
        exitMeta.setCustomModelData(2);
        exit.setItemMeta(exitMeta);
        this.questPicker.setItem(this.questPicker.getSize()-1, exit);
        this.completePicker.setItem(this.completePicker.getSize()-1, exit);

        for (String configName : stageQuests) {
            List<String> configDescription = config.getStringList("Quests."+configName+".description");
            List<Map<?, ?>> configCriteria = config.getMapList("Quests."+configName+".criteria");
            List<Map<?, ?>> configReward = config.getMapList("Quests."+configName+".reward");
            String configCompleted = config.getString("Quests."+configName+".completed");

            Component name = Component.text(configName);

            ArrayList<Component> description = new ArrayList<>();
            for (String line : configDescription) {
                TextColor color = TowerChallenge.PRIMARY_COLOR;
                String newLine;
                if (line.startsWith("[") && line.endsWith("]")) {
                    newLine = line.substring(1, line.length()-1);
                    color = TowerChallenge.SECONDARY_COLOR;
                } else {
                    newLine = line;
                }
                description.add(Component.text(newLine).decoration(TextDecoration.ITALIC, false).color(color));
            }

            ArrayList<ItemStack> criteria = new ArrayList<>();
            for (Map<?, ?> map : configCriteria) {
                HashMap<String, Object> quest = (HashMap<String, Object>) map;
                configItemGroup(quest, criteria);
            }

            ArrayList<ItemStack> reward = new ArrayList<>();
            for (Map<?, ?> map : configReward) {
                HashMap<String, Object> quest = (HashMap<String, Object>) map;
                configItemGroup(quest, reward);
            }

            ParticipantTeam completedTeam = eventManager.getTowerListener().getTeams().get(configCompleted);

            Quest newQuest = new Quest(this, name, description, criteria, reward, completedTeam);
            quests.put(configName, newQuest);
            questPicker.addItem(newQuest.getItem());
            completePicker.addItem(newQuest.getItem());
        }

        for (Map.Entry<String, List<Player>> entry : playersOpenInventories.entrySet()) {
            Quest quest = quests.get(entry.getKey());
            for (Player player : entry.getValue()) {
                quest.openInventory(player);
            }
        }

    }

    public Quest getQuest(String name) {
        return quests.get(name);
    }

    public int getStage() {
        return stage;
    }

    public void openQuestPicker(Player player) {
        player.openInventory(questPicker);
    }

    public void openCompletePicker(Player player) {
        player.openInventory(completePicker);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.isCancelled())
            return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType().isAir())
            return;
        if (event.getInventory().equals(questPicker)) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            if (QuestUtil.isButton(event.getCurrentItem())) {
                if (event.getSlot() == questPicker.getSize()-1) {
                    player.closeInventory();
                } else {
                    String questName = PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().getItemMeta().displayName());
                    quests.get(questName).openInventory(player);
                }
            }
        } else if (event.getInventory().equals(completePicker)) {
            event.setCancelled(true);

            Player player = (Player) event.getWhoClicked();

            if (QuestUtil.isButton(event.getCurrentItem())) {
                if (event.getSlot() == questPicker.getSize()-1) {
                    player.closeInventory();
                } else {
                    String questName = PlainTextComponentSerializer.plainText().serialize(event.getCurrentItem().getItemMeta().displayName());
                    quests.get(questName).openCompleteUI(player);
                }
            }
        }
    }

}
