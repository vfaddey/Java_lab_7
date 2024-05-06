package server.db;

import java.sql.SQLException;

public class PostgressDatabase extends Database {
    private final String url;
    private final String login;
    private final String password;

    public PostgressDatabase(String url, String login, String password) {
        this.url = url;
        this.login = login;
        this.password = password;
    }

    @Override
    public DatabaseConnection createConnection() throws SQLException {
        return new PostgresConnection(url, login, password);
    }

}
