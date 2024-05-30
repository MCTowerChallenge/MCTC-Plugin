package io.github.mctowerchallenge.mctcplugin.interaction.npc.character;

import io.github.mctowerchallenge.mctcplugin.interaction.npc.Dialogue;
import io.github.mctowerchallenge.mctcplugin.interaction.npc.QuestCharacter;
import io.github.mctowerchallenge.mctcplugin.quest.QuestManager;
import io.github.mctowerchallenge.mctcplugin.quest.QuestTags;
import io.github.mctowerchallenge.mctcplugin.quest.QuestUtil;
import io.github.mctowerchallenge.mctcplugin.quest.util.FullInventory;
import io.github.mystievous.mysticore.Color;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PaulNIzer extends QuestCharacter {

    public static final String NAME = "Paul N Izer";
    public static final Color NAME_COLOR = new Color(0xd17507);
    public static final Color TEXT_COLOR = new Color(0xdb8e10);
    public static final String TRAIT_NAME = "paulnizer";

    public final Dialogue[] dialogues;
    public final ItemStack[] rewards;

    public PaulNIzer(Plugin plugin) {
        super(plugin, EntityType.BEE, NAME, NAME_COLOR, TEXT_COLOR);

        MiniMessage mm = MiniMessage.miniMessage();

        dialogues = new Dialogue[]{
                new Dialogue(plugin, formatMessage(mm.deserialize("I can't <i>bee</i>-lieve my 5 eyes, how'd you get here??")), 5.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("Aww, you miss us bees? You want to apologize for taking us for granted?")), 5.75d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("Well? Go ahead.")), 7.0d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("How sweet of you! Well I, Paul N Izer <i>who's definitely representative of all bees</i>, forgive you.")), 8.0d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("It'll take some time for us bees to pack up the pollen and honey and spread the dance message of our move back to the Overworld.")), 7.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("For now, I can provide you with the <i>bee</i>-terials you'll need for your blocks! <i>For a price...</i>")), 7.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("Please let me ramble about my bee facts!!")), 5.0d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("YIP-<i>BEE</i> TALK TO ME AGAIN WHEN YOU WANT THE FIRST ONE!!!!")), 4.0d, Sound.ENTITY_BEE_POLLINATE.getKey()),
                new Dialogue(plugin, formatMessage(mm.deserialize("Okok let's start simple with a pretty well-known one straight from <click:open_url:'https://www.ontariohoney.ca/kids-zone/bee-facts'><u><color:#0080ff>ontariohoney.ca</color></u></click>.")), 6.0d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("There are three kinds of bees in a hive: Queen, Worker, and Drone!")), 4.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("<i>w a x  f a c t !</i>")), 1.5d, Sound.ENTITY_BEE_POLLINATE.getKey()),
                new Dialogue(plugin, formatMessage(mm.deserialize("Y'know, I was just reading <click:open_url:'https://link.springer.com/book/10.1007/978-3-030-60090-7'><u><color:#0080ff>Stingless Bees: Their Behaviour, Ecology and Evolution</color></u></click>, and it said that bees are found on every continent except Antarctica!")), 7.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("Us bees aren't big on the cold so that makes sense.")), 3.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("<i>w a x  f a c t !</i>")), 1.5d, Sound.ENTITY_BEE_POLLINATE.getKey()),
                new Dialogue(plugin, formatMessage(mm.deserialize("I saw on <click:open_url:'https://www.natgeokids.com/au/discover/animals/insects/honey-bees/'><u><color:#0080ff>natgeokids.com</color></u></click> that honey bees can fly up to 25km/15.5Z3mi per hour while beating their wings 200 times/sec!")), 11.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("Call me Bee Flash! Or Flash Bee!")), 3.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("<i>w a x  f a c t !</i>")), 1.5d, Sound.ENTITY_BEE_POLLINATE.getKey()),
                new Dialogue(plugin, formatMessage(mm.deserialize("If you were confused by my saying 5 eyes earlier then I bet you'd love to know us bees have 2 compound eyes and 3 tiny ocelli eyes according to <click:open_url:'https://www.ontariohoney.ca/kids-zone/bee-facts'><u><color:#0080ff>ontariohoney.ca</color></u></click>.")), 9.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("<i>w a x  f a c t !</i>")), 1.5d, Sound.ENTITY_BEE_POLLINATE.getKey()),
                new Dialogue(plugin, formatMessage(mm.deserialize("Speaking of having multiples, we actually have 2 stomachs too!")), 4.0d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("<click:open_url:'https://www.ontariohoney.ca/kids-zone/bee-facts'><u><color:#0080ff>ontariohoney.ca</color></u></click> says 1 stomach is for eating and the other special stomach is for storing nectar collected from flowers or water so it can be carried back home to our hive.")), 9.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("<i>w a x  f a c t !</i>")), 1.5d, Sound.ENTITY_BEE_POLLINATE.getKey()),
                new Dialogue(plugin, formatMessage(mm.deserialize("Actually another thing I can clarify from earlier is the dance message!")), 3.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("Honeybees have a dance move called the 'waggle dance'.")), 3.0d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("Funnily enough it's not a dance move at all, but a clever way for us to communicate and tell our nestmates where to go for the best food!")), 6.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("According to <click:open_url:'https://www.wwf.org.uk/learn/fascinating-facts/bees'><u><color:#0080ff>WWF.org.uk</color></u></click>, it took the researchers at Sussex University two years to decode the waggle dance.")), 6.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("<i>w a x  f a c t !</i>")), 1.5d, Sound.ENTITY_BEE_POLLINATE.getKey()),
                new Dialogue(plugin, formatMessage(mm.deserialize("Buzzzzing back into that <click:open_url:'https://link.springer.com/book/10.1007/978-3-030-60090-7'><u><color:#0080ff>Stingless Bees: Their Behaviour, Ecology and Evolution</color></u></click> book I mentioned;")), 6.0d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("Did you know that human beekeeping, aka apiculture and meliponiculture (which is for stingless bees), has been practiced for literally millennia-")), 8.0d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("Since at least the times of Ancient Egypt and Ancient Greece?")), 2.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("<i>w a x  f a c t !</i>")), 1.5d, Sound.ENTITY_BEE_POLLINATE.getKey()),
                new Dialogue(plugin, formatMessage(mm.deserialize("Finally to round this fact dump off, <click:open_url:'https://www.ontariohoney.ca/kids-zone/bee-facts'><u><color:#0080ff>ontariohoney.ca</color></u></click> says that in the summertime beehives can have as many as 50,000 to 80,000 bees!")), 8.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("With that many of us in here I better start helping move everything back!")), 3.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("<i>w a x  f a c t !</i>")), 1.5d, Sound.ENTITY_BEE_POLLINATE.getKey()),
                new Dialogue(plugin, formatMessage(mm.deserialize("Thanks for listening to my w a x  f a c t s !")), 2.5d, Sound.ENTITY_BEE_POLLINATE.getKey())
                        .append(formatMessage(mm.deserialize("I hope that's enough materials for your blocks, good luck with your sky touching contest!")), 4.0d, Sound.ENTITY_BEE_POLLINATE.getKey())
        };
        rewards = new ItemStack[]{
                ItemStack.empty(),
                new ItemStack(Material.HONEYCOMB, 2),
                new ItemStack(Material.HONEY_BOTTLE, 2),
                new ItemStack(Material.HONEYCOMB, 2),
                new ItemStack(Material.HONEYCOMB, 5),
                new ItemStack(Material.HONEY_BOTTLE, 3),
                new ItemStack(Material.HONEY_BOTTLE, 3),
                new ItemStack(Material.HONEYCOMB, 3),
                new ItemStack(Material.HONEYCOMB, 6),
        };

        setDefaultInteractionHandler((team, event) -> {
            if (team.canStartDialogue()) {
                team.setInDialogue(true);
                int dialogue = team.getObjective(QuestTags.BEE_FACTS, "dialogue");
                if (dialogue < (dialogues.length - 1)) {
                    dialogues[dialogue].play(team, () -> {
                        team.setInDialogue(false);
                        if (dialogue > 0) {
                            ItemStack itemStack = rewards[dialogue].clone();
                            team.sendMessage(QuestManager.getRewards(itemStack));
                            FullInventory.givePlayerItems(event.getClicker(), itemStack);
                        }
                        team.addObjectiveScore(QuestTags.BEE_FACTS, "dialogue", 1);
                    });
                } else {
                    dialogues[dialogues.length - 1].play(team, () -> {
                        team.setInDialogue(false);
                    });
                }
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
        return PaulNIzerTrait.class;
    }

    @TraitName(TRAIT_NAME)
    public static class PaulNIzerTrait extends Trait {
        public PaulNIzerTrait() {
            super(TRAIT_NAME);
        }
    }

}
