package server.db;

import java.sql.SQLException;

public class PostgresConnection extends DatabaseConnection {
    protected PostgresConnection(String url, String login, String password) throws SQLException {
        super(url, login, password);
    }
}
