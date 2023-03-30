package io.github.mystievous.towerchallenge.eventspecific.dec2022;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.towerchallenge.teams.TeamManager;
import io.github.mystievous.towerchallenge.quests.entities.NPC;

public class Dec2022NPC {

    public static final String HANK_MARVIN_TAG = "golem";

    public Dec2022NPC(TeamManager teamManager) {
        NPC hankMarvin = new NPC(teamManager, "Hank and Marvin", HANK_MARVIN_TAG, new Color(0xbadde0), new Color(0x8db2b5));
        hankMarvin.addAllowedRegion("candy-village-inner");
        hankMarvin.addDisallowedRegion(".*_gingerbread");
    }
}
