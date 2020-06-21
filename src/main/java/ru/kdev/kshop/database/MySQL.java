package ru.kdev.kshop.database;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import de.tr7zw.nbtapi.NBTContainer;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.kdev.kshop.KShop;
import ru.kdev.kshop.item.CartItem;
import ru.kdev.kshop.util.ThrowableConsumer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MySQL {

    private final KShop plugin;
    private Connection connection;

    public MySQL(KShop plugin) {
        this.plugin = plugin;
    }

    private PreparedStatement createStatement(String query, Object... objects) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(query, Statement.NO_GENERATED_KEYS);
        ps.setQueryTimeout(5);

        if (objects != null) {
            for (int i = 0; i < objects.length; i++) {
                Object object = objects[i];

                if (object == null) {
                    ps.setNull(i + 1, Types.VARCHAR);
                } else {
                    ps.setObject(i + 1, objects[i]);
                }
            }
        }

        if (objects == null || objects.length == 0) {
            ps.clearParameters();
        }

        return ps;
    }

    private void handleError(SQLException e) {
        e.printStackTrace();
    }

    private void async(ThrowableConsumer<PreparedStatement, SQLException> result,
                       String query, Object... objects) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (PreparedStatement ps = createStatement(query, objects)) {
                result.accept(ps);
            } catch (SQLException e) {
                handleError(e);
            }
        });
    }

    public void connect(ConfigurationSection section) {
        connect(
                section.getString("host"),
                section.getInt("port"),
                section.getString("database"),
                section.getString("user"),
                section.getString("password")
        );
    }

    public void connect(String host, int port, String database, String user, String password) {
        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setServerName(host);
            dataSource.setPort(port);
            dataSource.setDatabaseName(database);
            dataSource.setUser(user);
            dataSource.setPassword(password);
            dataSource.setServerTimezone("UTC");

            connection = dataSource.getConnection();
        } catch (SQLException e) {
            handleError(e);
        }
    }

    public void executeQuery(ThrowableConsumer<ResultSet, SQLException> result, String query, Object... objects) {
        async(ps -> {
            try (ResultSet rs = ps.executeQuery()) {
                result.accept(rs);
            }
        }, query, objects);
    }

    public void execute(String query, Object... objects) {
        async(PreparedStatement::execute, query, objects);
    }

    public void addItem(Player player, String pattern, int quantity, int data, String nbt) {

        execute("INSERT INTO items (nickname, pattern, quantity, data, nbt) VALUES (?, ?, ?, ?, ?)",
                player.getName(), pattern, quantity, data, nbt);
    }

    public void removeItem(CartItem item) {
        execute("DELETE FROM items WHERE id = ?", item.getDatabaseIndex());
    }

    public void removeItems(Player player) {
        execute("DELETE FROM items WHERE nickname = ?", player.getName());
    }

    public void getGroups(Player player, Consumer<List<String>> groupCallback) {
        executeQuery(rs -> {
            List<String> groups = new ArrayList<>();

            while (rs.next()) {
                groups.add(rs.getString("groupName"));
            }

            groupCallback.accept(groups);
        }, "SELECT `groupName` FROM groups WHERE nickname = ?", player.getName());
    }

    public void getItems(Player player, Consumer<List<CartItem>> itemCallback) {
        executeQuery(rs -> {
            List<CartItem> items = new ArrayList<>();

            while (rs.next()) {
                int index = rs.getInt("id");

                ItemStack item = new ItemStack(
                        Material.getMaterial(rs.getString("pattern").toUpperCase()),
                        rs.getInt("quantity"), (byte) rs.getInt("data")
                );

                String nbt = rs.getString("nbt");

                if (nbt != null && !nbt.isEmpty()) {
                    NBTItem nbti = new NBTItem(item);
                    nbti.mergeCompound(new NBTContainer(nbt));

                    item = nbti.getItem();
                }

                items.add(new CartItem(index, item));
            }

            itemCallback.accept(items);
        }, "SELECT * FROM items WHERE nickname = ?", player.getName());
    }

    public void removeGroup(Player player, String groupName) {
        execute("DELETE FROM groups WHERE nickname = ? AND groupName = ?", player.getName(), groupName);
    }
}
