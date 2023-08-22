package me.numilani.fastrpchat.data;

import me.numilani.fastrpchat.FastRpChat;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteDataSourceConnector extends IDataSourceConnector {

    private FastRpChat plugin;
    private String dbFilename = "fastrpchat.db";

    public SqliteDataSourceConnector(FastRpChat plugin) throws SQLException {
        this.plugin = plugin;
    }

    public Connection conn = DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), dbFilename).getPath());

    public void ensureConnClosed() throws SQLException {
        if (!conn.isClosed()) {
            conn.close();
        }
    }

    public void initDatabase() throws SQLException {
        var statement = conn.createStatement();
        statement.execute("CREATE TABLE Codex (id TEXT PRIMARY KEY, name TEXT)");

//        updateConfig("defaultCodex", codexId);
    }

}
