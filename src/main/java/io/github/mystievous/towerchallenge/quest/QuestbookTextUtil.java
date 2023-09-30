package io.github.mystievous.towerchallenge.quest;

import io.github.mystievous.mysticore.DefaultFontInfo;
import io.github.mystievous.mysticore.TextUtil;
import io.github.mystievous.towerchallenge.TowerChallenge;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.Nullable;

public class QuestbookTextUtil {
    private static final int LINE_MAX_WIDTH = 70;

    /**
     * Returns the input text but shifted
     * onto the given quest book line,
     * if the characters can be.
     *
     * @param line   The line to put the text on.
     * @param string The text to put on the line.
     * @return The shifted text.
     */
    public static Component toLine(int line, String string) {
        Key lineKey = Key.key(TowerChallenge.MCTC_NAMESPACE, String.format("questbook/line-%d", line));
        return Component.text(string).font(lineKey);
    }

    // Function to return a completely formatted string using toLine and DefaultFontInfo

    /**
     * Completely formats a string for the
     * Quest Book body, splitting it
     * between lines.
     *
     * @param string The string to format.
     * @return The formatted {@link Component}
     */
    public static Component toBookBodyFormat(@Nullable String string) {
        if (string != null) {
            String[] words = string.strip().split(" ");
            TextComponent.Builder output = Component.text();
            int currentLine = 0;
            int pixelCount = 0;
            for (String word : words) {
                word = word + ' ';
                int wordLength = DefaultFontInfo.getPixelLength(word);
                if (pixelCount + wordLength > LINE_MAX_WIDTH) {
                    output.append(TextUtil.space(-pixelCount));
                    pixelCount = 0;
                    currentLine++;
                }
                pixelCount += wordLength;
                output.append(toLine(currentLine, word));
            }
            return output.build();
        }
        return Component.empty();
    }

}
