package server.db;

import common.exceptions.UserAlreadyExistsException;
import common.exceptions.WrongPasswordException;
import common.model.Address;
import common.model.Coordinates;
import common.model.OrganizationType;

import java.nio.charset.StandardCharsets;
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
        if (resultSet.next()) {
            return true;
        }
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

    @Override
    public boolean removeById(long id) {
        return false;
    }
}
