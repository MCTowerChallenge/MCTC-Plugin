package io.github.mystievous.towerchallenge.towering.regions;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.mystievous.towerchallenge.ChallengeManager;
import io.github.mystievous.towerchallenge.towering.ParticipantTeam;

public class GingerbreadRegion extends EventRegion {


    public GingerbreadRegion(ParticipantTeam team, ChallengeManager manager, ProtectedRegion region) {
        super(team, manager, region);
    }
}
