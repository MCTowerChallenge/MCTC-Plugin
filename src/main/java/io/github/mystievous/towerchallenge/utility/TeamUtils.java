package io.github.mystievous.towerchallenge.utility;

import io.github.mystievous.mysticore.NBTUtils;
import io.github.mystievous.towerchallenge.teams.TowerTeam;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class TeamUtils implements Listener {

    public static final String TEAM = "item_team";

    public static String toTeamTag(TowerTeam team, String tag) {
        return String.format("%s-%s", team.getServerTeamName(), tag);
    }

    public static boolean hasTeam(Plugin plugin, ItemStack itemStack) {
        return NBTUtils.hasTag(plugin, TEAM, itemStack);
    }

    public static ItemStack setTeam(Plugin plugin, ItemStack itemStack, @NotNull TowerTeam team) {
        NBTUtils.setString(plugin, TEAM, itemStack, team.getTextName());
        return itemStack;
    }

    public static boolean matchTeam(Plugin plugin, ItemStack itemStack, TowerTeam team) {
        String itemTeam = NBTUtils.getString(plugin, TEAM, itemStack);
        if (itemTeam == null)
            return false;

        return itemTeam.equals(team.getTextName());
    }

}
