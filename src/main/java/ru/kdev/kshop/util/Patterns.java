package ru.kdev.kshop.util;

import java.util.regex.Pattern;

/**
 * @author artem
 */
public class Patterns {

    public static final Pattern DIGIT = Pattern.compile("^\\d+$");
    public static final Pattern NEW_LINE = Pattern.compile("\n");

    public static final Pattern PLUGIN_VERSION = Pattern.compile("^([\\w.-]+) \\[(\\w+)]$");
    public static final Pattern COMMIT_MESSAGE = Pattern.compile("^\\[(\\w+)]\\s*.+$");
    public static final Pattern SLOT = Pattern.compile("^(\\d+),\\s*(\\d+)$");

}
