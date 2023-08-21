package io.github.mystievous.towerchallenge.interaction.npc.character;

import io.github.mystievous.mysticore.Color;
import io.github.mystievous.towerchallenge.interaction.npc.Dialogue;
import io.github.mystievous.towerchallenge.interaction.npc.QuestCharacter;
import io.github.mystievous.towerchallenge.quest.QuestManager;
import io.github.mystievous.towerchallenge.quest.util.FullInventory;
import io.github.mystievous.towerchallenge.team.TowerTeam;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Base64;

public class Dave extends QuestCharacter {

    public static final String NAME = "Dave";
    public static final Color NAME_COLOR = new Color(0x663b5a);
    public static final Color TEXT_COLOR = new Color(0xca60ad);
    public static final String TRAIT_NAME = "dave";

    public Dave(Plugin plugin) {
        super(plugin, EntityType.STRIDER, NAME, NAME_COLOR, TEXT_COLOR);

        String noteBase64 = "H4sIAAAAAAAA/02PT0vDQBDFJ5aUGPHqechZinjsVUEQvIgIQkGmybRZk+yW3RdjEM9+Mz+Xm3ioh2H+vcdvJida0NmtQJ7VB+Ms0fl3RiemoovOWC297LAevAHUvm6da3JaQPanlB5kryEjooR+PgvoB4p18eR4Z2zFqJXd9k1L8Oh6DqrN5cZu7Etsuj7gqNq2YhtF4KFWr39qA5a4VsFqMt25WE4e77rZ00lZx+N4MKjnQZAWI8NHx4y5nxDXVxygh8CuB5v/gPmCgIkxRbAqzar4ymgpPWrnKX8YA4y+uz5+mMKgVVo+mqqKOaH0xvUWCf0CbmzokT0BAAA=";
        ItemStack noteItem = ItemStack.deserializeBytes(Base64.getDecoder().decode(noteBase64));
        BookMeta noteMeta = (BookMeta) noteItem.getItemMeta();
        noteMeta.setAuthor("Alice Wayward");
        noteItem.setItemMeta(noteMeta);

        Dialogue daveMeet = new Dialogue(plugin, formatMessage("Thanks for meeting up with me, sorry for being all cryptic but I had to make sure no one sus would catch on."), 6.5d);
        daveMeet.append(new Dialogue(plugin, formatMessage("I heard you were asking about what's happening over at the main stage, and I may have some info."), 5.0d));
        daveMeet.append(new Dialogue(plugin, formatMessage("Earlier, I stumbled into Alice and Ari whispering about some hidden cave."), 4.0d));
        daveMeet.append(new Dialogue(plugin, formatMessage("They kept giggling about it like they did something."), 3.5d));
        daveMeet.append(new Dialogue(plugin, formatMessage("I found this lying around backstage and think it might have something to do with it."), 4.5d));

        addQuestInteractionHandler(QuestManager.MEETING, (team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                daveMeet.play(team, () -> {
                    team.setInDialogue(false);
                    Player player = event.getClicker();
                    FullInventory.givePlayerItems(player, noteItem);
                    team.sendMessage(QuestManager.getRewards(noteItem));
                    team.setQuest(QuestManager.RIDDLE);
                });
            }
        });
    }

    @Override
    public @NotNull NPC setNPCProperties(NPC npc) {
        super.setNPCProperties(npc);
        return npc;
    }

    @Override
    public @NotNull Class<? extends Trait> getTrait() {
        return DaveTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class DaveTrait extends Trait {
        public DaveTrait() {
            super(TRAIT_NAME);
        }
    }

}
