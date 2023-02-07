package io.github.mystievous.towerchallenge.gui.page;

import io.github.mystievous.towerchallenge.quests.TextFormatter;
import io.github.mystievous.towerchallenge.utility.DefaultFontInfo;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.text.Component;

public class QuestGui extends PresetGui {

    public QuestGui(String name, String body) {
        super(Component.text(name).append(TextUtil.space(-DefaultFontInfo.getPixelLength(name)))
                .append(TextFormatter.toBookBodyFormat(body)), -15, '\uE003', -175, 6);
    }

}
