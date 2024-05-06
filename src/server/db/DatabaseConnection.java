package server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DatabaseConnection {
    protected Connection connection;

    public DatabaseConnection(String url, String login, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, login, password);
    }

}
