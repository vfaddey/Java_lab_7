package server.db;

import common.model.Address;
import common.model.Coordinates;
import common.model.OrganizationType;

import java.sql.SQLException;
import java.time.LocalDate;

public class PostgresConnection extends DatabaseConnection {
    protected PostgresConnection(String url, String login, String password) throws SQLException {
        super(url, login, password);
    }

    @Override
    public boolean authenticateUser(String login, String password) {
        return false;
    }

    @Override
    public boolean addUser(String login, String password) {
        return false;
    }

    @Override
    public boolean addOrganization(String name, Coordinates coordinates, LocalDate date, long annualTurnover, int employeesCount, OrganizationType type, Address address) {
        return false;
    }

    @Override
    public boolean updateOrganization(long id, Coordinates coordinates, long annualTurnover, int employeesCount, OrganizationType type, Address address) {
        return false;
    }
}
