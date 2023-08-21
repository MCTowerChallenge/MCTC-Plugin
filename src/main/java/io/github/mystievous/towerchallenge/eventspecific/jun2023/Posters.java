package io.github.mystievous.towerchallenge.eventspecific.jun2023;

import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.towerchallenge.interaction.InteractableTaggedEntity;
import io.github.mystievous.towerchallenge.interaction.InteractableTagManager;
import io.github.mystievous.towerchallenge.team.TeamManager;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.Base64;

/**
 * Utility class for registering interactable posters and their interactions.
 */
public class Posters {

    // Base64-encoded serialized poster data
    public static final String GENERAL_DESERIALIZED = "H4sIAAAAAAAA/22RzU4CMRSFLyI6Yty6Ll2zGEA06Y7wIxoghKiJEWMKU7AR28lMByXI2pfQxN08ga6Mi3kf3sFOw5+JM5v23Pud9J6bBkjCfoUqesU8n0sBcPBqwRZ34PCRC9b36ECRJ48rxcRdT8qHNCQVHe5ByqVD5lsAkEiIKVbsWWGCEYnCo/nH9/znLQqPzeEzCpk5vEehTVAUimb5oozanbNKNQq9mIj//JLKLanimuqKrkCLLwp7tUCgU/rI/MzfQqHmMYZqUjoZ7bNZa/AxQ83A532DdIV21XLuJMb6L4a269JXBC0Ufa91qlVtcX7ZqhotZhp0IIVDVz2lRkO35O18YWGktVwxX0SZDaNK6do8Ro9aMEN96cpyTJvEvjqWOnXdCWp73GGZRTCrphVmEzxLw67DfXdEJxZst3QOQKdY5+9RTG6mmCs64n1MBnTks+xyM3HoeJZdb8pc/u81b8Cz25WAZ2DBDg3UvfQg3Zz4irOxDPT6U4qrEdOa2WnMQQJSZRkIlYBfjYX4Fl4CAAA=";
    public static final String FUNGAL_RECKONING_DESERIALIZED = "H4sIAAAAAAAA/7VUy24TMRR1KC0hiC1rd9Zd5NEUNLuQpC2QRlEKRYgi5E5uUisTO/J40kYhEjt+gkrs5gvICnUx/5N/4HpeSUPFBmFL87j3nHvnHHtcIGSLPGkwzc5AeVwKQp5+y5MHvEeejbgAR7G+tq8U1xrE5wsphwWypdngMdkeswF4eYIFctOZpeFaW7ZF7TDYX/74tbz9HgYH0cPPMIDo4SYMijYNA3FSf1unne6rRjMMlGGYWU5ZpZRVXbHOxbmglLp8AnQMqi/ViAkHknAyDn0xYO4q0gVnKAUXAwPDKmEgk4wU1EMRQMtJCmOl5xS/zflCza14LD1t0ySC74fdZhMrv37XbkYxw2mxvhQ9lmFqrRZCysVyJSmEsVK1XKW7a4UatQ/RV6PqSqRvgZlUcdE2ddGhYzYeT2lH8R7sJh5loIxWtK157kVmfexfjIoh2NaRI6AegNESB9cAN0Z7aqJZFyldlzschKanl0rKEfZO/WwB69Ez6TDXM7T9lc/I7IAG2mFquEYw48jnmimDr9zB1xSn79m1kZaNGPCSOUN/nDUy8z5hqZbb5eLrn8IWNM5solfK0bn2X5371wbxxjpIVKPkU4myNlzdEIt731xWnBquBqBR0yumenetbSh/FFGqmXURpcEmZiUcWIe/gemFxBL/088CedTj3thl0zx52GYjIIOZhf4qZtkfZxbuAxRj2X1UCnup8/Eva833VgdI9HI/OvuhrfmnLGjNSZ7sMF9fSkUKJ1NPc5hIH0+mbc21C2QnbkJyZLsufaFz5DcjVsJ09QQAAA==";
    public static final String WITHERING_GROOVE_DESERIALIZED = "H4sIAAAAAAAA/41UQU8TQRSeimitMd48v+6ZmLZQKnuDtgLaEgIVYsSYYffRrrQzzewUrNjEePFHCAm3vXi1J8Jh/0//gzOz3W0tJXE3mey897739vu+yWQIWSBPK1TSAxS+xxkhz36myQPPJS86HkNH0BNpnwtPSmSfjjk/zZAFSZtPyGKXNtFPE9Ug9fvCkvhFWrYFdhisjK5vRreXYbBqPv6EAZqPqzDI2RAGrF5ulGF3b7tSDQOhEfotxKh8jCpOUEfsiIF5VPtsVq28Tj0G1TNkMoqZArOEwSuNvPkFjRbCoSdbKDzWBBO8NEVhsGyaD2FTcH6GUKdOS9GFKKrHqbFqih5Z2z6oAmewr3gj5MdJlciX9DDnm5mZ2+K+tGEcUfvXe9WqGvXm3U7VxDSmRk84c2lSs16rqZJCrrA8bqRi+WKhCNmpRpX194a/nfy1yoyub0fD71ob3VeJukW73T7sCs/F7FjWpCiB5WxrkPqRuHW/V8OIYZl3EPYRI16FODVrEWhF1oyubJe3qYDDl7CBVGjdJVdJEfsHNaQuHHCHtn2NWh5bxnyJygf/FNvtuyCAhuh1uig1pJiEFazKXA/KLcrkdH1D8M4xZ6jLC1FU16pzoOic0073Tn/Y7HmSCg1wIk0ijuNjArFCw0hTtS/F+6kypUKUn8Dyd8qulAefpzxw588rzcwrzp2X+4950YldTXTYQib6sE+Z67Tw6z86vMX+MafC1YCVSVj7isLpQ82b1a2ijDFOlmInN5TwfVAr7VIhcaZ+g/q+/iENuYd5cYZ5bi7z/CzztXnMrUGGPHY9v9um/TR5uEM7SJoXltJeUMv+cGEp19ueY9kn6kDiUuxKcmtYg6XJ5WY28wHRPWINPiYRa0DS5BHtyRYXJFPv+9LDM95TV+ai9GQbyfPJ1WTQJEUWy7zHZIr8BeQywUSXBQAA";

    // Poster identifiers
    public static final String GENERAL_POSTER = "general-poster";
    public static final String WITHERING_GROOVE_POSTER = "withering-groove-poster";
    public static final String FUNGAL_RECKONING_POSTER = "fungal-reckoning-poster";

    /**
     * Registers interactable posters with their corresponding interaction handlers.
     *
     * @param teamManager The TeamManager instance to handle interactions.
     */
    public static void registerPosterInteractables(TeamManager teamManager) {
        ItemStack generalItem = ItemStack.deserializeBytes(Base64.getDecoder().decode(GENERAL_DESERIALIZED));
        BookMeta generalMeta = (BookMeta) generalItem.getItemMeta();
        generalMeta.author(Component.text("MCTC Committee"));
        generalItem.setItemMeta(generalMeta);

        InteractableTaggedEntity generalPoster = new InteractableTaggedEntity(GENERAL_POSTER);
        generalPoster.setDefaultInteractionHandler((team, event) -> {
            Player player = event.getPlayer();
            player.getInventory().addItem(generalItem);
        });
        InteractableTagManager.registerTag(generalPoster);

        ItemStack fungalItem = ItemStack.deserializeBytes(Base64.getDecoder().decode(FUNGAL_RECKONING_DESERIALIZED));
        BookMeta fungalMeta = (BookMeta) fungalItem.getItemMeta();
        fungalMeta.author(Component.text("MCTC Committee"));
        fungalItem.setItemMeta(fungalMeta);

        InteractableTaggedEntity fungalPoster = new InteractableTaggedEntity(FUNGAL_RECKONING_POSTER);
        fungalPoster.setDefaultInteractionHandler((team, event) -> {
            Player player = event.getPlayer();
            player.getInventory().addItem(fungalItem);
        });
        InteractableTagManager.registerTag(fungalPoster);

        ItemStack witheringItem = ItemStack.deserializeBytes(Base64.getDecoder().decode(WITHERING_GROOVE_DESERIALIZED));
        BookMeta witheringMeta = (BookMeta) witheringItem.getItemMeta();
        witheringMeta.displayName(TextUtil.noItalic("The Withering Groove Machine"));
        witheringMeta.author(Component.text("MCTC Committee"));
        witheringItem.setItemMeta(witheringMeta);

        InteractableTaggedEntity witheringPoster = new InteractableTaggedEntity(WITHERING_GROOVE_POSTER);
        witheringPoster.setDefaultInteractionHandler((team, event) -> {
            Player player = event.getPlayer();
            player.getInventory().addItem(witheringItem);
        });
        InteractableTagManager.registerTag(witheringPoster);
    }
}
