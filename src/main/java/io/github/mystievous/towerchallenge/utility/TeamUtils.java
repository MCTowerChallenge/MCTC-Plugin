package io.github.mystievous.towerchallenge.utility;

import io.github.mystievous.towerchallenge.team.TowerTeam;
import org.bukkit.event.Listener;

public class TeamUtils implements Listener {

    /**
     * Formats a tag to be unique to the given team.
     *
     * @param team The team to format with.
     * @param tag  The tag to format.
     * @return The resulting {@link String}
     */
    public static String toTeamTag(TowerTeam team, String tag) {
        return String.format("%s-%s", team.getServerTeamName(), tag);
    }

}
