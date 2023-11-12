package io.github.mctowerchallenge.towerchallenge.quest;

import io.github.mystievous.mysticore.DefaultFontInfo;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.mystigui.page.PresetGui;
import io.github.mctowerchallenge.towerchallenge.TowerChallenge;
import io.github.mctowerchallenge.towerchallenge.utility.FontUtils;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

public class QuestGui extends PresetGui {

    public QuestGui(TowerChallenge plugin, String name, @Nullable String body) {
        super(plugin, Component.text(name).append(TextUtil.space(-DefaultFontInfo.getPixelLength(name)))
                .append(QuestbookTextUtil.toBookBodyFormat(body)), -15, FontUtils.toGuiFont("\uE003"), -175, 6);
    }

}
