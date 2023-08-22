package me.numilani.fastrpchat.data;

import me.numilani.fastrpchat.FastRpChat;

import java.sql.Connection;
import java.sql.SQLException;

public interface IDataSourceConnector {

    FastRpChat plugin = null;
    Connection conn = null;

    public void ensureConnClosed() throws SQLException;
    public void initDatabase() throws SQLException;
    public void initPlayerRange(String playerId) throws SQLException;
    public String getChatRange(String playerId) throws SQLException;
    public void setChatRange(String playerId, String range) throws SQLException;
}
