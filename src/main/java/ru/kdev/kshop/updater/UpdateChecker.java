package ru.kdev.kshop.updater;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.scheduler.BukkitTask;
import ru.kdev.kshop.KShop;
import ru.kdev.kshop.util.Patterns;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;

/**
 * @author artem
 */
public class UpdateChecker {

    public static final long UPDATE_CHECKER_DELAY = TimeUnit.HOURS.toMillis(8);

    public static final String API_URL = "https://api.github.com/repos/kdevmcTeam/KShop/commits?sha=%s";
    public static final String GIT_URL = "https://github.com/kdevmcTeam/KShop/tree/%s";

    public static final String USER_AGENT = "UpdateChecker (Branch: %s, Plugin: %s, Version: %s)";

    private final KShop plugin;
    private final Gson gson;

    private final String prefix;

    private final AtomicLong lastExecution = new AtomicLong();

    private BukkitTask updaterTask;

    public UpdateChecker(KShop plugin) {
        Preconditions.checkNotNull(plugin, "plugin is null");

        this.plugin = plugin;
        this.prefix = "[UpdateChecker: " + plugin.getName() + "] ";
        this.gson = new Gson();
    }

    public void stopUpdateChecker() {
        if (updaterTask != null) {
            updaterTask.cancel();
            updaterTask = null;
        }
    }

    private void printMessage(CommandSender sender, String message) {
        if (sender instanceof ConsoleCommandSender) {
            message = prefix + message;
        }

        sender.sendMessage(message);
    }

    public void checkUpdates(CommandSender sender, String branch) {
        try {
            printMessage(sender, "§eChecking for updates...");

            List<Commit> commits = retrieveCommits(
                    gson, branch, String.format(
                            USER_AGENT, branch, plugin.getName(),
                            plugin.getDescription().getVersion()
                    )
            );

            boolean isHeaderSent = false;

            for (Commit commit : commits) {
                if (commit.getShortSha().equals(plugin.getRevision())) {
                    break;
                }

                String[] commitMessages = Patterns.NEW_LINE.split(commit.getMessage());

                for (String commitMessage : commitMessages) {
                    Matcher matcher = Patterns.COMMIT_MESSAGE.matcher(commitMessage);

                    UpdateType type = matcher.matches()
                            ? UpdateType.getByName(matcher.group(1))
                            : UpdateType.UNKNOWN;

                    if (!isHeaderSent) {
                        printMessage(sender, "§eFound updates: ");
                        isHeaderSent = true;
                    }

                    printMessage(sender, String.format(" - %s%s §r(Author: %s)",
                            type.getColor(), commitMessage, commit.getAuthorName()));
                }
            }

            if (isHeaderSent) {
                printMessage(sender, "§eYou are able to download an update from our github: "
                        + String.format(GIT_URL, branch));
            } else {
                printMessage(sender, "§aNo updates found!");
            }
        } catch (Exception e) {
            printMessage(sender, "§cCannot retrieve updates: " + e.getMessage());
        }
    }

    public void runUpdateChecker(String branch) {
        long executed = lastExecution.get();
        long spent = System.currentTimeMillis() - executed;

        long wait = 0;

        // Когда конфигурация перезагрузится, то таск запустится заново.
        // Чтобы не отправлять запрос каждый перезапуск плагина,
        // нужно установить изначальную задержку (в тиках, 1 tick = 50ms)
        if (spent < UPDATE_CHECKER_DELAY) {
            wait = (UPDATE_CHECKER_DELAY - spent) / 50L;
        }

        updaterTask = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            lastExecution.set(System.currentTimeMillis());

            checkUpdates(plugin.getServer().getConsoleSender(), branch);
        }, wait, UPDATE_CHECKER_DELAY / 50L);
    }

    public static List<Commit> retrieveCommits(Gson gson, String branch, String userAgent) throws IOException {
        HttpURLConnection httpConnection = (HttpURLConnection) new URL(String.format(API_URL, branch))
                .openConnection();

        httpConnection.setRequestProperty("User-Agent", userAgent);
        httpConnection.setRequestProperty("Connection", "Close");

        try (InputStream is = httpConnection.getInputStream(); InputStreamReader isr = new InputStreamReader(is)) {
            return gson.fromJson(isr, new TypeToken<ArrayList<Commit>>() {
            }.getType());
        }
    }

}
