package ru.kdev.KShop.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import ru.kdev.KShop.KShop;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQL {
    private Connection connection;
    private KShop plugin;

    public MySQL(KShop plugin) {
        this.plugin = plugin;
    }

    public void connect() throws ClassNotFoundException, SQLException {
        String host = plugin.getConfig().getString("connection.host");
        String database = plugin.getConfig().getString("connection.database");
        String username = plugin.getConfig().getString("connection.user");
        String password = plugin.getConfig().getString("connection.password");
        int port = plugin.getConfig().getInt("connection.port");
        Class.forName("com.mysql.jdbc.Driver");
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName(host);
        dataSource.setPort(port);
        dataSource.setDatabaseName(database);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setServerTimezone("UTC");
        connection = dataSource.getConnection();
    }

    public void addItem(Player player, String pattern, int quantity, int data, String nbt) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO items (nickname, pattern, quantity, data, nbt) VALUES (?, ?, ?, ?, ?)");
        statement.setString(1, player.getName());
        statement.setString(2, pattern);
        statement.setInt(3, quantity);
        statement.setInt(4, data);
        statement.setString(5, nbt);
        statement.executeUpdate();
    }

    public ResultSet getItem(Player player, int index) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE nickname = ?");
        statement.setString(1, player.getName());
        ResultSet resultSet = statement.executeQuery();
        if(!resultSet.next()) {
            return null;
        }
        resultSet.previous();
        List<Integer> rows = new ArrayList<Integer>();
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            rows.add(id);
        }
        PreparedStatement getStatement = connection.prepareStatement("SELECT * FROM items WHERE id = ?");
        getStatement.setInt(1, rows.get(index));
        return getStatement.executeQuery();
    }

    public void removeItem(Player player, int index) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE nickname = ?");
        statement.setString(1, player.getName());
        ResultSet resultSet = statement.executeQuery();
        if(!resultSet.next()) {
            return;
        }
        resultSet.previous();
        List<Integer> rows = new ArrayList<Integer>();
        while (resultSet.next()) {
            int id = resultSet.getInt(1);
            rows.add(id);
        }
        PreparedStatement removeStatement = connection.prepareStatement("DELETE FROM items WHERE id = ?");
        removeStatement.setInt(1, rows.get(index));
        removeStatement.executeUpdate();
    }

    public void removeItems(Player player) throws SQLException {
        PreparedStatement removeStatement = connection.prepareStatement("DELETE FROM items WHERE nickname = ?");
        removeStatement.setString(1, player.getName());
        removeStatement.executeUpdate();
    }

    public ResultSet getGroups(Player player) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM groups WHERE nickname = ?");
        statement.setString(1, player.getName());
        return statement.executeQuery();
    }

    public ResultSet getItems(Player player) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE nickname = ?");
        statement.setString(1, player.getName());
        return statement.executeQuery();
    }

    public void removeGroup(Player player, String gName) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("DELETE FROM groups WHERE nickname = ? AND groupName = ?");
        statement.setString(1, player.getName());
        statement.setString(2, gName);
        statement.executeUpdate();
    }
}
