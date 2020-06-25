package ru.kdev.kshop.util;

import java.util.regex.Matcher;

/**
 * @author artem
 */
public class SlotUtil {

    public static int parseSlot(String location) {
        Matcher matcher = Patterns.SLOT.matcher(location);

        if (!matcher.matches()) {
            throw new IllegalStateException("Can't parse slot: " + location);
        }

        int x = Integer.parseInt(matcher.group(1));
        int y = Integer.parseInt(matcher.group(2));

        return y * 9 + x;
    }

}
