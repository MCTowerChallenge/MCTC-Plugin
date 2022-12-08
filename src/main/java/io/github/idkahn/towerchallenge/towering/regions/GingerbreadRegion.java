package io.github.idkahn.towerchallenge.towering.regions;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import io.github.idkahn.towerchallenge.ChallengeManager;
import io.github.idkahn.towerchallenge.towering.ParticipantTeam;

public class GingerbreadRegion extends EventRegion {


    public GingerbreadRegion(ParticipantTeam team, ChallengeManager manager, ProtectedRegion region) {
        super(team, manager, region);
    }
}
