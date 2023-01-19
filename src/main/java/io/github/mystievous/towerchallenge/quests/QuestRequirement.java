package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.configs.Config;
import io.github.mystievous.towerchallenge.Palette;
import io.github.mystievous.towerchallenge.TextUtil;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.List;

public class QuestRequirement implements Representable {

    private final Quest quest;
    private final ItemStack item;
    private final int requiredAmount;
    private int currentAmount;

    public QuestRequirement(Quest quest, ItemStack item, int requiredAmount) {
        this.quest = quest;
        this.item = item;
        this.requiredAmount = requiredAmount;
        this.currentAmount = 0;
    }

    public QuestRequirement(Quest quest, ItemStack item, int requiredAmount, int currentAmount) {
        this(quest, item, requiredAmount);
        this.currentAmount = currentAmount;
    }

    public QuestRequirement(Quest quest, ItemStack item, List<Component> description, int requiredAmount, int currentAmount) {
        this.quest = quest;
        this.item = item;
        item.lore(description);
        this.requiredAmount = requiredAmount;
        this.currentAmount = currentAmount;
    }

    /**
     * Turns in the item for this requirement
     * @param amount Amount that is being turned in
     * @return the amount that should be removed from the itemstack
     */
    public int turnIn(TowerTeam team, int amount) {
        int amountToTake = amount + Math.min((requiredAmount-currentAmount)-amount, 0);
        setTeamAmount(team, getCurrentAmount()+amountToTake);
        return amountToTake;
    }

    /**
     * Checks if this requirement is fulfilled
     * @return true if the required amount is turned in
     */
    public boolean isFulfilled() {
        return currentAmount >= requiredAmount;
    }

    public Material getType() {
        return item.getType();
    }

    public int getCurrentAmount() {
        return currentAmount;
    }

    public int getTeamAmount(TowerTeam team) {
        YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(Config.teamDataConfigFile);
        String questPath = team.getTextName()+".QuestProgress."+quest.getId();
        String requirementPath = questPath+"."+getType().toString();
        int amount = teamDataConfig.getInt(requirementPath, 0);
        setCurrentAmount(amount);
        return amount;
    }

    public void setCurrentAmount(int currentAmount) {
        this.currentAmount = currentAmount;
    }

    public void setTeamAmount(TowerTeam team, int amount) {
        setCurrentAmount(amount);
        YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(Config.teamDataConfigFile);
        String questPath = team.getTextName()+".QuestProgress."+quest.getId();
        String requirementPath = questPath+"."+getType().toString();
        teamDataConfig.set(requirementPath, getCurrentAmount());
        try {
            teamDataConfig.save(Config.teamDataConfigFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int getRemaining(TowerTeam team) {
        return requiredAmount-getTeamAmount(team);
    }

    /**
     * Gets the ItemStack representation for this requirement, for use in GUIs
     * @return the ItemStack
     */
    public Element getRepresentation() {
        ItemStack item = this.item.clone();
        Component itemName = TextUtil.getItemName(item);
        if (isFulfilled()) {
            item = new ItemStack(Material.PAPER);
            ItemMeta paperMeta = item.getItemMeta();
            paperMeta.setCustomModelData(1);
            item.setItemMeta(paperMeta);
        }
        ItemMeta meta = item.getItemMeta();
        meta.displayName(itemName.append(Component.text(String.format(" (%d/%d)", currentAmount, requiredAmount)).color(Palette.PRIMARY.getTextColor())).decoration(TextDecoration.ITALIC, false));
        item.setItemMeta(meta);
        item.setAmount(Math.max(requiredAmount-currentAmount, 1));
        return new Element(item);
    }
}
