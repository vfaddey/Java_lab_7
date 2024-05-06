package server.db;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {
    public abstract DatabaseConnection createConnection() throws SQLException;
}
