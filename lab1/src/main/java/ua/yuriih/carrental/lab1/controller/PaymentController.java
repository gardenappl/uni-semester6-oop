package ua.yuriih.carrental.lab1.controller;

import ua.yuriih.carrental.lab1.model.Payment;
import ua.yuriih.carrental.lab1.model.PaymentInfo;
import ua.yuriih.carrental.lab1.repository.PaymentDao;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionPool;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionWrapper;

import java.time.LocalDate;
import java.util.List;

public class PaymentController {
    public static final PaymentController INSTANCE = new PaymentController();

    private PaymentController() {}

    public List<PaymentInfo> getAllPaymentInfo(LocalDate since) {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            return PaymentDao.INSTANCE.getAllPaymentInfo(connection, since);
        }
    }

    public List<PaymentInfo> getAllPaymentInfo(int carId) {
        try (ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection()) {
            return PaymentDao.INSTANCE.getAllPaymentInfo(connection, carId);
        }
    }
}
