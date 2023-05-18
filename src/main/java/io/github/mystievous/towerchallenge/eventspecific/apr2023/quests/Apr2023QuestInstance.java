package io.github.mystievous.towerchallenge.eventspecific.apr2023.quests;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.GuiUtil;
import io.github.mystievous.towerchallenge.Worlds;
import io.github.mystievous.towerchallenge.quests.QuestItems;
import io.github.mystievous.towerchallenge.quests.QuestManager;
import io.github.mystievous.towerchallenge.quests.instances.QuestInstance;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class Apr2023QuestInstance extends QuestInstance implements Listener {


    // Anchor point is the composter directly to the right when entering the abandoned tavern
    public static final Location baseLocation = new Location(Worlds.Apr2023_quest(), 1, 65, 32);

    public static final String TP_POTION = "tp-potion";

    private final QuestManager questManager;
    private final TeamManager teamManager;

    public final House house;
    public final BadTavern badTavern;
    public final BadCellar badCellar;
    public final GoodCellar goodCellar;
    public final GoodTavern goodTavern;

    public Apr2023QuestInstance(Plugin plugin, QuestManager questManager, TeamManager teamManager, TowerTeam team, Location instanceLocation) {
        super(team, baseLocation, instanceLocation);
        this.questManager = questManager;
        this.teamManager = teamManager;
        house = new House(this);
        badTavern = new BadTavern(this);
        badCellar = new BadCellar(plugin, this);
        goodCellar = new GoodCellar(plugin, this);
        goodTavern = new GoodTavern(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);

        ItemStack tpPotion = GuiUtil.formatItem("Mystery Potion", Material.POTION, 3);
        NBTUtils.setBool(plugin, TP_POTION, tpPotion);
        ItemMeta meta = tpPotion.getItemMeta();
        meta.lore(TextUtil.formatTexts("Maybe you should", "drink this..."));
        meta.addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS, ItemFlag.HIDE_ATTRIBUTES);
        tpPotion.setItemMeta(meta);
        TextUtil.appendQuestItemLore(tpPotion);

        QuestItems.putItem(TP_POTION, tpPotion);
    }

    /**
     * Event handler for the teleport potion in the cellar
     *
     * @param event Event of the player consuming an item
     */
    @EventHandler
    public void onPlayerConsume(final PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        ItemMeta meta = item.getItemMeta();
        if (item.getType().equals(Material.POTION) && meta.getCustomModelData() == 3) {
            Player player = event.getPlayer();
            TowerTeam playerTeam = teamManager.getPlayerTeam(player);
            event.setCancelled(true);

            if (playerTeam == null || playerTeam.getDatabaseId() != getTeam().getDatabaseId()) {
                return;
            }

            if (badCellar.getRegion().checkInRegion(player)) {
                goodCellar.potionTeleport(player);

                if (playerTeam.getCurrentQuestTag().equals(QuestManager.BOTTLE_PUZZLE)) {
                    questManager.setTeamQuest(getTeam(), QuestManager.RESTORED_TAVERN);
                }

                return;
            }
            if (goodCellar.getRegion().checkInRegion(player)) {
                badCellar.potionTeleport(player);
                return;
            }
            player.sendMessage(TextUtil.formatText("The potion feels weak...").decoration(TextDecoration.ITALIC, true));
        }
    }

}
