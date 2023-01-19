package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.configs.Config;
import io.github.mystievous.towerchallenge.TowerChallenge;
import io.github.mystievous.towerchallenge.gui.element.ButtonElement;
import io.github.mystievous.towerchallenge.gui.element.Element;
import io.github.mystievous.towerchallenge.gui.page.Gui;
import io.github.mystievous.towerchallenge.gui.page.Openable;
import io.github.mystievous.towerchallenge.gui.page.PresetGui;
import io.github.mystievous.towerchallenge.towering.TowerTeam;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Quest implements Openable {

    private final String id;
    private final String friendlyName;
    private final List<QuestRequirement> requirements;
    private final List<QuestReward> rewards;
    private @Nullable List<Component> description;

    private Quest next;

    public Quest(String id, String friendlyName, List<QuestRequirement> requirements, List<QuestReward> rewards) {
        this.id = id;
        this.friendlyName = friendlyName;
        this.requirements = requirements;
        this.rewards = rewards;
        this.description = null;
    }

    public Quest(String id, String friendlyName) {
        this(id, friendlyName, new ArrayList<>(), new ArrayList<>());
    }

    public Quest(String id, String friendlyName, List<QuestRequirement> requirements, List<QuestReward> rewards, @Nullable List<Component> description) {
        this(id, friendlyName, requirements, rewards);
        this.description = description;
    }

    /**
     * Searches this quest for a requirement matching the given item
     *
     * @param itemStack the item to search for
     * @return the corresponding requirement, or null if there is none
     */
    public @Nullable QuestRequirement getRequirement(ItemStack itemStack) {
        return requirements.stream().filter(questRequirement -> questRequirement.getType().equals(itemStack.getType())).findFirst().orElse(null);
    }

    public String getId() {
        return id;
    }

    public Quest getNext() {
        return next;
    }

    public void setNext(Quest next) {
        this.next = next;
    }

    public List<QuestRequirement> getRequirements() {
        return requirements;
    }

    public List<QuestReward> getRewards() {
        return rewards;
    }

    public void setDescription(@Nullable List<Component> description) {
        this.description = description;
    }

    /**
     * Checks the entire quest for completion
     *
     * @return true, if all requirements are fulfilled
     */
    public boolean isFulfilled() {
        for (QuestRequirement requirement : requirements) {
            if (!requirement.isFulfilled()) {
                return false;
            }
        }
        return true;
    }

    public Gui getGui(@NotNull TowerTeam team) {
        YamlConfiguration teamDataConfig = YamlConfiguration.loadConfiguration(Config.teamDataConfigFile);
        String questPath = team.getTextName() + ".QuestProgress." + getId();
        for (QuestRequirement requirement : requirements) {
            String requirementPath = questPath + "." + requirement.getType().toString();
            int requirementAmount = teamDataConfig.getInt(requirementPath, 0);
            requirement.setCurrentAmount(requirementAmount);
        }
        PresetGui gui = new PresetGui(Component.text(friendlyName).color(NamedTextColor.BLACK), -8, '\uE002', -170, 6);
        if (description != null) {
            ItemStack descItem = new ItemStack(Material.PAPER);
            ItemMeta descMeta = descItem.getItemMeta();
            if (description.size() > 0) {
                Component title = description.get(0);
                descMeta.displayName(title);
            } else {
                descMeta.displayName(Component.empty());
            }
            descMeta.lore(description.subList(1, description.size()));
            descItem.setItemMeta(descMeta);
            Element element = new Element(descItem);
            gui.placeElement(1, 1, element);
        }

        gui.placeElement(9, 1, new ButtonElement(ButtonElement.exitItem(), HumanEntity::closeInventory));

        for (int i = 0; i < requirements.size(); i++) {
            QuestRequirement requirement = requirements.get(i);
            Element element = requirement.getRepresentation();
            if (i < 7) {
                gui.placeElement(i + 2, 2, element);
            } else if (i < 14) {
                gui.placeElement(i - 7 + 2, 3, element);
            } else {
                ChallengeManager.log("Quest has too many requirements");
            }
        }

        for (int i = 0; i < rewards.size(); i++) {
            QuestReward reward = rewards.get(i);
            Element element = reward.getRepresentation();
            if (i < 7) {
                gui.placeElement(i + 2, 5, element);
            } else {
                ChallengeManager.log("Quest has too many rewards");
            }
        }

        return gui;
    }

    @Override
    public Gui getGui(Player player) {
        TowerTeam team = TowerChallenge.getInstance().getChallengeManager().getPlayerTeam(player);
        if (team == null) {
            return QuestManager.NO_QUEST_GUI;
        }
        return getGui(team);
    }
}
