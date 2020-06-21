package ru.kdev.kshop.util;

import ru.kdev.kshop.KShop;

public class MessageGetter {

    @Deprecated
    public static String getMessage(String path) {
        return KShop.getPlugin(KShop.class).getMessage(path.substring(7));
    }
}
