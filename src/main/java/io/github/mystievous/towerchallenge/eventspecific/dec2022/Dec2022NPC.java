package io.github.mystievous.towerchallenge.eventspecific.dec2022;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.towerchallenge.team.TeamManager;
import io.github.mystievous.towerchallenge.quest.npc.LegacyNPC;

public class Dec2022NPC {

    public static final String HANK_MARVIN_TAG = "golem";

    public static void registerNPCs(TeamManager teamManager) {
        LegacyNPC hankMarvin = new LegacyNPC(teamManager, "Hank and Marvin", HANK_MARVIN_TAG, new Color(0xbadde0), new Color(0x8db2b5));
        hankMarvin.addAllowedRegion("candy-village-inner");
        hankMarvin.addDisallowedRegion(".*_gingerbread");
    }

}
