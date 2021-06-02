package ua.yuriih.carrental.lab1.repository;

import ua.yuriih.carrental.lab1.model.Car;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionWrapper;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class CarDao {
    public static final CarDao INSTANCE = new CarDao();

    private CarDao() {}

    private static Car resultToCar(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String model = resultSet.getString("model");
        String manufacturer = resultSet.getString("manufacturer");
        BigDecimal hrnPerDay = resultSet.getBigDecimal("hrn_per_day");
        Long currentUserId = resultSet.getLong("current_user_id");
        if (resultSet.wasNull())
            currentUserId = null;
        String thumbnailUrl = resultSet.getString("thumbnail_url");

        return new Car(id, model, manufacturer, hrnPerDay, currentUserId, thumbnailUrl);
    }

    public Car getCar(ConnectionWrapper connection, int id) {
        String sql = "SELECT * FROM cars WHERE id = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                return resultToCar(resultSet);
            else
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void update(ConnectionWrapper connection, Car car) {
        String sql = "UPDATE cars" +
                " SET model = ?," +
                "     manufacturer = ?," +
                "     hrn_per_day = ?," +
                "     current_user_id = ?," +
                "     thumbnail_url = ?" +
                " WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, car.getModel());
            statement.setString(2, car.getManufacturer());
            statement.setBigDecimal(3, car.getHrnPerDay());
            statement.setObject(4, car.getCurrentUserId());
            statement.setString(5, car.getThumbnailUrl());
            statement.setInt(6, car.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public List<Car> getAllFreeCars(ConnectionWrapper connection) {
        String sql = "SELECT * FROM cars WHERE current_user_id IS NULL";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ArrayList<Car> cars = new ArrayList<>();

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                cars.add(resultToCar(resultSet));
            }

            return cars;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
