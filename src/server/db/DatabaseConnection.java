package server.db;

import common.exceptions.WrongPasswordException;
import common.model.*;
import common.network.AuthorizedUser;
import common.network.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;

public abstract class DatabaseConnection {
    protected Connection connection;

    public DatabaseConnection(String url, String login, String password) throws SQLException {
        this.connection = DriverManager.getConnection(url, login, password);
    }

    public abstract boolean authenticateUser(String login, String password) throws SQLException, WrongPasswordException;
    public abstract boolean addUser(String login, String password) throws SQLException;
    public abstract int addOrganization(String name,
                                            Coordinates coordinates,
                                            LocalDate date,
                                            long annualTurnover,
                                            int employeesCount,
                                            OrganizationType type,
                                            Address address,
                                            String ownerLogin) throws SQLException;
    public abstract int addLocation(Location location) throws SQLException;
    public abstract int addAddress(Address address) throws SQLException;
    public abstract int addCoordinates(Coordinates coordinates) throws SQLException;

    public abstract boolean updateOrganization(long id,
                                               String name,
                                               Coordinates coordinates,
                                               long annualTurnover,
                                               int employeesCount,
                                               OrganizationType type,
                                               Address address) throws SQLException;

    public abstract boolean removeById(long id, User user) throws SQLException;
    public abstract LinkedList<Organization> getAllOrganizations() throws SQLException;
    public abstract int clearCollectionForUser(User user) throws SQLException;
}
