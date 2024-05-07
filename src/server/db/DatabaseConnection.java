package server.db;

import common.model.Address;
import common.model.Coordinates;
import common.model.OrganizationType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;

public abstract class DatabaseConnection {
    protected Connection connection;

    public DatabaseConnection(String url, String login, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, login, password);
    }

    public abstract boolean authenticateUser(String login, String password) throws SQLException;
    public abstract boolean addUser(String login, String password) throws SQLException;
    public abstract boolean addOrganization(String name,
                                            Coordinates coordinates,
                                            LocalDate date,
                                            long annualTurnover,
                                            int employeesCount,
                                            OrganizationType type,
                                            Address address);

    public abstract boolean updateOrganization(long id,
                                               Coordinates coordinates,
                                               long annualTurnover,
                                               int employeesCount,
                                               OrganizationType type,
                                               Address address);

    public abstract boolean removeById(long id);
}
