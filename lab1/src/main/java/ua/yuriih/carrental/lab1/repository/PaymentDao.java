package ua.yuriih.carrental.lab1.repository;

import ua.yuriih.carrental.lab1.model.Payment;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionWrapper;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentDao {
    public static final PaymentDao INSTANCE = new PaymentDao();

    private PaymentDao() {}

    private static Payment resultToPayment(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        BigDecimal hrnAmount = resultSet.getBigDecimal("hrn_amount");
        int requestId = resultSet.getInt("request_id");
        boolean isRepair = resultSet.getBoolean("is_repair");

        return new Payment(id, hrnAmount, requestId, isRepair);
    }

    public Payment getPayment(ConnectionWrapper connection, int id) {
        String sql = "SELECT * FROM payments WHERE id = ? LIMIT 1";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next())
                return resultToPayment(resultSet);
            else
                return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Payment insertPayment(ConnectionWrapper connection, BigDecimal hrnAmount, int requestId, boolean isRepair) {
        String sql = "INSERT INTO payments" +
                " VALUES (DEFAULT, ?, ?, ?)" +
                " RETURNING id";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBigDecimal(1, hrnAmount);
            statement.setInt(2, requestId);
            statement.setBoolean(3, isRepair);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
                return new Payment(result.getInt("id"), hrnAmount, requestId, isRepair);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
