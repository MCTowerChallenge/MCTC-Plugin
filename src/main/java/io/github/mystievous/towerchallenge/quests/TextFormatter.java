package io.github.mystievous.towerchallenge.quests;

import io.github.mystievous.towerchallenge.utility.DefaultFontInfo;
import io.github.mystievous.towerchallenge.utility.TextUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class TextFormatter {

    private static final int BASE_OFFSET = 57824; // \uE200 minus \u0020
    private static final int LINE_OFFSET = 96;
    private static final String REGEX = "[a-zA-Z0-9 !\"#$%&'()*+,\\-./:;<=>?@\\[\\\\\\]^_`{|}~]*";

    private static final int LINE_MAX = 70;

    public static String toLine(int line, String string) {
        int offset = BASE_OFFSET + (LINE_OFFSET * line);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < string.length(); i++) {
            String charString = string.substring(i, i+1);
            if (charString.matches(REGEX)) {
                int charValue = charString.charAt(0);
                result.append(Character.valueOf((char) (charValue + offset)));
            }
        }

        return result.toString();
    }

    // Function to return a completely formatted string using toLine and DefaultFontInfo
    // Line Max = 68
    public static Component toBookBodyFormat(String string) {
        String[] words = string.strip().split(" ");
        TextComponent.Builder output = Component.text();
        int currentLine = 0;
        int pixelCount = 0;
        for (String word : words) {
            word = word+' ';
            int wordLength = DefaultFontInfo.getPixelLength(word);
            if (pixelCount + wordLength > LINE_MAX) {
                output.append(TextUtil.space(-pixelCount));
                pixelCount = 0;
                currentLine++;
            }
            pixelCount += wordLength;
            output.append(Component.text(toLine(currentLine, word)));
        }
        return output.build();
    }

}
