package server.db;

import common.exceptions.UserAlreadyExistsException;
import common.exceptions.WrongPasswordException;
import common.model.Address;
import common.model.Coordinates;
import common.model.Location;
import common.model.OrganizationType;

import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class PostgresConnection extends DatabaseConnection {
    private final PasswordManager passwordManager = new PasswordManager();
    protected PostgresConnection(String url, String login, String password) throws SQLException {
        super(url, login, password);
    }

    @Override
    public boolean authenticateUser(String login, String password) throws SQLException {
        PreparedStatement ps = connection.prepareStatement("SELECT * FROM users WHERE login = ?");
        ps.setString(1, login);

        ResultSet resultSet = ps.executeQuery();

        if (resultSet.next()) {
            byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
            byte[] salt = resultSet.getBytes("salt");
            byte[] expectedPassword = resultSet.getBytes("password");

            if (this.passwordManager.isExpectedPassword(passwordBytes, salt, expectedPassword)) {
                return true;
            }
        }
        throw new WrongPasswordException("Неверный пароль!");
    }

    @Override
    public boolean addUser(String login, String password) throws SQLException {
        if (findUser(login)) {
            throw new UserAlreadyExistsException("Пользователь с таким логином уже существует!");
        }

        byte[] salt = this.passwordManager.getSalt();
        byte[] passwordHash = this.passwordManager.hash(password.getBytes(StandardCharsets.UTF_8), salt);

        PreparedStatement ps = connection.prepareStatement("INSERT INTO users (login, password, salt) VALUES (?, ?, ?)");
        ps.setString(1, login);
        ps.setBytes(2, passwordHash);
        ps.setBytes(3, salt);

        return ps.execute();
    }

    private boolean findUser(String login) throws SQLException {
        PreparedStatement ps = this.connection.prepareStatement("SELECT 1 FROM users WHERE login = ?");
        ps.setString(1, login);
        ResultSet resultSet = ps.executeQuery();
        return resultSet.next();
    }

    @Override
    public int addOrganization(String name, Coordinates coordinates, LocalDate date, long annualTurnover, int employeesCount, OrganizationType type, Address address, String ownerLogin) throws SQLException {
        int coordinatesId = addCoordinates(coordinates);
        int addressId = addAddress(address);
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO organizations (" +
                "name, coordinates_id, creation_date, annual_turnover, employees_count, type, official_address_id, owner_login)" +
                "VALUES (?, ?, ?, ?, ?, CAST(? AS organization_type_enum), ?, ?) RETURNING id");

        ps.setString(1, name);
        ps.setInt(2, coordinatesId);
        ps.setDate(3, Date.valueOf(date));
        ps.setLong(4, annualTurnover);
        ps.setInt(5, employeesCount);
        ps.setString(6, type.name());
        ps.setInt(7, addressId);
        ps.setString(8, ownerLogin);

        int id = -1;
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()) {
            id = resultSet.getInt(1);
        }
        return id;
    }

    @Override
    public int addLocation(Location location) throws SQLException {
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO locations (x, y, z) VALUES (?, ?, ?) RETURNING id");
        ps.setDouble(1, location.getX());
        ps.setDouble(2, location.getY());
        ps.setLong(3, location.getZ());

        int id = -1;
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()) {
            id = resultSet.getInt(1);
        }
        return id;
    }

    @Override
    public int addAddress(Address address) throws SQLException {
        int id = -1;
        int locationId;
        if ((locationId = addLocation(address.getTown())) > 0) {
            PreparedStatement ps = this.connection.prepareStatement(
                    "INSERT INTO address (zip_code, town_id) VALUES (?, ?) RETURNING id");
            ps.setString(1, address.getZipCode());
            ps.setInt(2, locationId);

            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        }
        return id;
    }

    @Override
    public int addCoordinates(Coordinates coordinates) throws SQLException {
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO coordinates (x, y) VALUES (?, ?) RETURNING id");
        ps.setInt(1, coordinates.getX());
        ps.setLong(2, coordinates.getY());
        int id = -1;
        ResultSet resultSet = ps.executeQuery();
        if (resultSet.next()) {
            id = resultSet.getInt(1);
        }
        return id;
    }

    @Override
    public boolean updateOrganization(long id, Coordinates coordinates, long annualTurnover, int employeesCount, OrganizationType type, Address address) {
        return false;
    }

    @Override
    public boolean removeById(long id) {
        return false;
    }
}
