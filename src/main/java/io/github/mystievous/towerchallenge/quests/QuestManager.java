package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.*;
import io.github.mystievous.towerchallenge.configs.Config;
import io.github.mystievous.towerchallenge.eventspecific.winter.presents.PresentEntityHandler;
import io.github.mystievous.towerchallenge.gui.GuiHeldItem;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.Gui;
import io.github.mystievous.towerchallenge.gui.page.Openable;
import io.github.mystievous.towerchallenge.gui.page.PresetGui;
import io.github.mystievous.towerchallenge.quests.legacy.BlockVoucher;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestManager implements Openable {

    public static final String GUI_ID = "questgui";

    public static final String NO_QUEST = "no-quest";

    public static final String PENELOPE_START = "penelope-start";
    public static final String PENELOPE_ARMOR = "penelope-armor";
    public static final String STEVE_START = "steve-start";
    public static final String STEVE_LIST = "steve-list";
    public static final String STEVE_ITEMS = "steve-items";
    public static final String SPIRIT_START = "spirit-start";
    public static final String SPIRIT_PRESENTS = "spirit-PRESENTS";

    public static final PresetGui NO_QUEST_GUI = new PresetGui(Component.text("No quests!"), 3){{
        Element element = new Element(new ItemStack(Material.PAPER){{
            ItemMeta meta = getItemMeta();
            meta.displayName(Component.text("You have no available quests!"));
            setItemMeta(meta);
        }});

        placeElement(5, 2, element);

    }};

    public static void resetTeamQuests(TowerTeam team) {
        YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(Config.teamDataConfigFile);
        String teamPath = team.getTextName()+".QuestProgress";
        teamDataConfig.set(teamPath, null);
        try {
            teamDataConfig.save(Config.teamDataConfigFile);
//            team.getAudience().sendMessage(Component.text("Setting Quest: "+questId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void resetTeamItems(TowerTeam team) {
        YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(Config.teamDataConfigFile);
        String itemsPath = team.getTextName()+".CollectedItems";
        teamDataConfig.set(itemsPath, null);
        try {
            teamDataConfig.save(Config.teamDataConfigFile);
//            team.getAudience().sendMessage(Component.text("Setting Quest: "+questId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setTeamQuest(TowerTeam team, String questId) {
        YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(Config.teamDataConfigFile);
        teamDataConfig.set(team.getTextName()+".CurrentQuest", questId);
        try {
            teamDataConfig.save(Config.teamDataConfigFile);
//            team.getAudience().sendMessage(Component.text("Setting Quest: "+questId));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        team.getAudience().sendMessage(Component.text("New Quest: "+getTeamQuest(team)));
    }

    public static void setTeamQuest(Player player, String questId) {
        TowerTeam team = TowerChallenge.getInstance().getChallengeManager().getPlayerTeam(player);
        if (team != null) {
            setTeamQuest(team, questId);
        } else {
            Bukkit.getLogger().info("What? How? Quest Manager SetTeamQuest without a team?");
        }
    }

    public static String getTeamQuest(TowerTeam team) {
        YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(Config.teamDataConfigFile);
        if (!hasTeamQuest(team)) {
            setTeamQuest(team, "no-quest");
        }
        return teamDataConfig.getString(team.getTextName()+".CurrentQuest", NO_QUEST);
    }

    public static String getTeamQuest(Player player) {
        TowerTeam team = TowerChallenge.getInstance().getChallengeManager().getPlayerTeam(player);
        if (team != null) {
            return getTeamQuest(team);
        }
        return NO_QUEST;
    }

    public static boolean hasTeamQuest(TowerTeam team) {
        if (team == null)
            return false;
        YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(Config.teamDataConfigFile);
        return teamDataConfig.isString(team.getTextName()+".CurrentQuest");
    }

    private final List<Quest> quests;
    private final Map<String, List<Quest>> teamQuests;
    private final GuiHeldItem questBook;
    private final ItemStack steveListItem;
    private final ChallengeManager challengeManager;
    private final NPCManager npcManager;

    public QuestManager(ChallengeManager challengeManager) {
        this.challengeManager = challengeManager;
        quests = new ArrayList<>();
        teamQuests = new HashMap<>();

        ItemStack book = new ItemStack(Material.BOOK);
        ItemMeta bookMeta = book.getItemMeta();
        bookMeta.displayName(Component.text("Better Quest Book").decoration(TextDecoration.ITALIC, false));
        bookMeta.setCustomModelData(2);
        bookMeta.lore(new ArrayList<>(){{
            add(Component.text("Right click with me in your hand").decoration(TextDecoration.ITALIC, false).color(Palette.PRIMARY.toTextColor()));
            add(Component.text("to open the quest menu!").decoration(TextDecoration.ITALIC, false).color(Palette.PRIMARY.toTextColor()));
        }});
        book.setItemMeta(bookMeta);
        questBook = new GuiHeldItem(GUI_ID, book, this);

        //Stage 1: “Talk to Penelope”
        //Stage 2: “Find Penelope’s ‘Fancy Fit’”
        //Stage 3: “Talk to Steve”
        //Stage 4: “Find Steve’s List”
        //Stage 5: “Find the items on the list, and bring them to steve”
        //Stage 6: “Find the ritual”
        //Stage 7: “Bring PRESENTS to the ritual”

        Quest penelopeStart;
        Quest penelopeArmor;
        Quest steveStart;
        Quest steveList;
        Quest steveItems;
        Quest spiritStart;
        Quest spiritPresents;

        List<QuestReward> penelopeRewards = new ArrayList<>();
        penelopeRewards.add(new QuestReward(BlockVoucher.getVouchers(1)));

        penelopeStart = new Quest(PENELOPE_START, "Talk to Penelope");
        penelopeStart.setDescription(TextUtil.formatTexts("Talk to Penelope in", "the iceberg at Spawn!"));
//        penelopeStart.getRewards().addAll(penelopeRewards);
        quests.add(penelopeStart);

        penelopeArmor = new Quest(PENELOPE_ARMOR, "Find Penelope's Fancy Fit");
        penelopeArmor.setDescription(TextUtil.formatTexts("Penelope has lost", "her outfit for steve's", "party! Help her", "find it.", "", "She mentioned climbing", "the north-west side", "of the iceberg...", "maybe it's up there?"));
        penelopeArmor.getRequirements().add(new QuestRequirement(penelopeArmor, new ItemStack(Material.DIAMOND_HORSE_ARMOR), 1, 0));
        penelopeArmor.getRewards().addAll(penelopeRewards);
        quests.add(penelopeArmor);
        penelopeStart.setNext(penelopeArmor);

        steveListItem = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta listMeta = (BookMeta) steveListItem.getItemMeta();
        listMeta.title(Component.text("steve's list").color(NamedTextColor.WHITE));
        listMeta.displayName(TextUtil.noItalic("steve's list"));
        listMeta.author(Component.text("steve skellington"));
        listMeta.pages(new ArrayList<>(){{
                add(Component.text("32 Watermelon Slices").append(Component.newline())
                        .append(Component.text("16 String")).append(Component.newline())
                        .append(Component.text("16 Redstone Lamps")).append(Component.newline())
                        .append(Component.text("6 Potions of Swiftness")).append(Component.newline())
                        .append(Component.text("64 Cookies")).append(Component.newline())
                        .append(Component.text("5 Cakes")).append(Component.newline())
                        .append(Component.text("64 Snowballs")).append(Component.newline())
                        .append(Component.text("16 Pickles")).append(Component.newline())
                        .append(Component.text("18 Fireworks"))
                );
            }}
        );
        steveListItem.setItemMeta(listMeta);

        List<QuestReward> steveRewards = new ArrayList<>();
        steveRewards.add(new QuestReward(BlockVoucher.getVouchers(2)));

        steveStart = new Quest(STEVE_START, "Talk to steve skellington");
        steveStart.setDescription(TextUtil.formatTexts("Penelope said to go", "talk to steve", "over at his home", "to the south-west"));
//        steveStart.getRewards().addAll(steveRewards);
        quests.add(steveStart);
        penelopeArmor.setNext(steveStart);

        steveList = new Quest(STEVE_LIST, "Retrieve steve skellington's List");
        steveList.setDescription(TextUtil.formatTexts("Go upstairs to look", "for steve's list."));
//        steveList.getRequirements().add(new QuestRequirement(steveList, steveListItem, 1, 0));
        steveList.getRewards().add(new QuestReward(steveListItem));
//        steveList.getRewards().addAll(steveRewards);
        quests.add(steveList);
        steveStart.setNext(steveList);

        steveItems = new Quest(STEVE_ITEMS, "Find the items for steve!");
        steveItems.setDescription(TextUtil.formatTexts("Help steve find all", "the items he needs", "to decorate for the", "party!"));
        List<QuestRequirement> steveItemsReq = steveItems.getRequirements();
        steveItemsReq.add(new QuestRequirement(steveItems, new ItemStack(Material.MELON_SLICE), TextUtil.formatTexts("Give this to steve", "for 1 Nether Wart!"), 32, 0));
        steveItemsReq.add(new QuestRequirement(steveItems, new ItemStack(Material.STRING), TextUtil.formatTexts("Give this to steve", "for 16 Coal!"), 16, 0));
        steveItemsReq.add(new QuestRequirement(steveItems, new ItemStack(Material.REDSTONE_LAMP), TextUtil.formatTexts("Give this to steve", "for 32 Golden Carrots!"), 16, 0));
        steveItemsReq.add(new QuestRequirement(steveItems, new ItemStack(Material.POTION){{
            PotionMeta meta = (PotionMeta) getItemMeta();
            meta.setBasePotionData(new PotionData(PotionType.SPEED, false, false));
            setItemMeta(meta);
        }}, TextUtil.formatTexts("Give this to steve", "for Boots of Swiftness!"), 6, 0));
        steveItemsReq.add(new QuestRequirement(steveItems, new ItemStack(Material.COOKIE), TextUtil.formatTexts("Give this to steve", "for 24 Golden Carrots!"), 64, 0));
        steveItemsReq.add(new QuestRequirement(steveItems, new ItemStack(Material.CAKE), TextUtil.formatTexts("Give this to steve", "for 24 Iron Ingots!"), 5, 0));
        steveItemsReq.add(new QuestRequirement(steveItems, new ItemStack(Material.SNOWBALL), TextUtil.formatTexts("Give this to steve", "for a Snow Shooter!"), 64, 0));
        steveItemsReq.add(new QuestRequirement(steveItems, new ItemStack(Material.SEA_PICKLE), TextUtil.formatTexts("Give this to steve", "for 1 Blaze Rod!"), 16, 0));
        steveItemsReq.add(new QuestRequirement(steveItems, new ItemStack(Material.FIREWORK_ROCKET), TextUtil.formatTexts("Give this to steve", "for 8 Iron Ingots!"), 18, 0));
        steveItems.getRewards().addAll(steveRewards);
        quests.add(steveItems);
        steveList.setNext(steveItems);

        List<QuestReward> spiritRewards = new ArrayList<>();
        spiritRewards.add(new QuestReward(BlockVoucher.getVouchers(3)));
        spiritRewards.add(new QuestReward(new ItemStack(Material.LEATHER_HORSE_ARMOR){{
            ItemMeta meta = getItemMeta();
            meta.displayName(Component.text("Mystery Hat"));
            setItemMeta(meta);
        }}));

        spiritStart = new Quest(SPIRIT_START, "Investigate the strange voice");
        spiritStart.setDescription(TextUtil.formatTexts("You heard a strange voice", "coming from the bushes", "behind steve's house...", "", "Maybe you should go", "check it out?"));
//        spiritStart.getRewards().addAll(spiritRewards);
        quests.add(spiritStart);
        steveItems.setNext(spiritStart);

        spiritPresents = new Quest(SPIRIT_PRESENTS, "Collect PRESENTS for the spirit");
        spiritPresents.setDescription(TextUtil.formatTexts("Collect all ## PRESENTS", "around the challenge", "areas and bring", "them to the spirit", "behind steve's house!"));
        spiritPresents.getRequirements().add(new QuestRequirement(spiritPresents, PresentEntityHandler.getPresentItem(), 24));
        spiritPresents.getRewards().addAll(spiritRewards);
        quests.add(spiritPresents);
        spiritStart.setNext(spiritPresents);

        npcManager = new NPCManager(this);

        QuestCommands commands = new QuestCommands(this);
        challengeManager.getPlugin().getCommand("questbook").setExecutor(commands);
    }

    public ChallengeManager getChallengeManager() {
        return challengeManager;
    }

    public NPCManager getNpcManager() {
        return npcManager;
    }

    public GuiHeldItem getQuestBook() {
        return questBook;
    }

    public List<String> getQuestIds() {
        return quests.stream().map(Quest::getId).toList();
    }

    public @Nullable Quest getQuest(TowerTeam team, String id) {
        return teamQuests.getOrDefault(team.getTextName(), List.copyOf(quests)).stream().filter(quest -> quest.getId().equals(id)).findFirst().orElse(null);
    }

    public ItemStack getSteveListItem() {
        return steveListItem;
    }

    @Override
    public Gui getGui(Player player) {
        YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(Config.teamDataConfigFile);
        TowerTeam team = TowerChallenge.getInstance().getChallengeManager().getPlayerTeam(player);
        if (team != null) {
            String currentQuestPath = team.getTextName()+".CurrentQuest";
            String currentQuestId = teamDataConfig.getString(currentQuestPath, "");
            Quest currentQuest = getQuest(team, currentQuestId);
            if (currentQuest != null){
                return currentQuest.getGui(player);
            }
            try {
                teamDataConfig.save(Config.teamDataConfigFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return NO_QUEST_GUI;
    }
}
