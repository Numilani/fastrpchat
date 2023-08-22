package me.numilani.fastrpchat.data;

import me.numilani.fastrpchat.FastRpChat;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SqliteDataSourceConnector implements IDataSourceConnector {

    private FastRpChat plugin;
    private String dbFilename = "fastrpchat.db";
    public Connection conn = DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), dbFilename).getPath());

    public SqliteDataSourceConnector(FastRpChat plugin) throws SQLException {
        this.plugin = plugin;
    }

    public void ensureConnClosed() throws SQLException {
        if (!conn.isClosed()) {
            conn.close();
        }
    }

    public void initDatabase() throws SQLException {
        var statement = conn.createStatement();
        statement.execute("CREATE TABLE PlayerChatSettings (playerId TEXT PRIMARY KEY, range TEXT)");
    }

    public void initPlayerRange(String playerId) throws SQLException{
        var statement = conn.prepareStatement("INSERT INTO PlayerChatSettings (playerId, range) values (?,'global')");
        statement.setString(1,playerId);

        statement.execute();
    }

    public String getChatRange(String playerId) throws SQLException {
        var statement = conn.prepareStatement("SELECT range FROM PlayerChatSettings WHERE playerId = ?");
        statement.setString(1, playerId);

        var queryRes = statement.executeQuery();
        // there should only be one row
        if (queryRes.next()){
            return queryRes.getString(1);
        }
        // if there's no DB entry, just return global range
        else return "global";
    }

    public void setChatRange(String playerId, String range) throws SQLException {
        var statement = conn.prepareStatement("UPDATE PlayerChatSettings SET range = ? WHERE playerId = ?");
        statement.setString(1, range);
        statement.setString(1, playerId);
        statement.execute();
        // todo: add try-catch here
    }

}
