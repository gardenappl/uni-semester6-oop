package ua.yuriih.carrental.lab1.repository;

import ua.yuriih.carrental.lab1.model.Car;
import ua.yuriih.carrental.lab1.model.RentRequest;
import ua.yuriih.carrental.lab1.model.User;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionPool;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionWrapper;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class UserDao {

    public static final UserDao INSTANCE = new UserDao();

    private UserDao() {}

    private static User resultToUser(ResultSet resultSet) throws SQLException {
        long id = resultSet.getLong("passport_id");
        String name = resultSet.getString("name");
        String password = resultSet.getString("password");
        Integer currentCarId = resultSet.getInt("current_car");
        if (resultSet.wasNull())
            currentCarId = null;
        boolean isAdmin = resultSet.getBoolean("is_admin");

        return new User(id, name, password, currentCarId, isAdmin);
    }

    public User getUserByUsername(ConnectionWrapper connection, String username) {
        String sql = "SELECT * FROM users WHERE name = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next())
                return resultToUser(resultSet);
            else
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public User getUser(ConnectionWrapper connection, long passportId) {
        String sql = "SELECT * FROM users WHERE passport_id = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, passportId);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next())
                return resultToUser(resultSet);
            else
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void update(ConnectionWrapper connection, User user) {
        String sql = "UPDATE users" +
                " SET name = ?," +
                "     password = ?," +
                "     current_car = ?," +
                "     is_admin = ?" +
                " WHERE passport_id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getName());
            statement.setString(2, user.getPassword());
            statement.setInt(3, user.getCurrentCar());
            statement.setBoolean(4, user.isAdmin());
            statement.setLong(5, user.getPassportId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public User insertUser(ConnectionWrapper connection, long passportId, String name, String password) {
        String sql = "INSERT INTO users" +
                " VALUES (?, ?, ?, NULL, FALSE)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, passportId);
            statement.setString(2, name);
            statement.setString(3, password);
            int result = statement.executeUpdate();
            if (result == 1) {
                return new User(passportId, name, password, null, false);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
