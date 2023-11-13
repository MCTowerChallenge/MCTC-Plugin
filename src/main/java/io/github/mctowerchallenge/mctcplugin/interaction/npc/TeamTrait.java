package io.github.mctowerchallenge.mctcplugin.interaction.npc;

import io.github.mctowerchallenge.mctcplugin.team.TowerTeam;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;

@TraitName("team")
public class TeamTrait extends Trait {

    private TowerTeam team;

    public TeamTrait() {
        super("team");
        team = null;
    }

    public void setTeam(TowerTeam team) {
        this.team = team;
    }

    public TowerTeam getTeam() {
        return team;
    }

    @Override
    public void onSpawn() {
        if (team == null) {
            return;
        }

        team.getTeam().addEntities(npc.getEntity());
    }
}
