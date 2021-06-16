package ua.yuriih.carrental.lab1.controller;

import org.junit.Test;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import ua.yuriih.carrental.lab1.model.Car;
import ua.yuriih.carrental.lab1.model.Payment;
import ua.yuriih.carrental.lab1.model.RentRequest;
import ua.yuriih.carrental.lab1.repository.CarDao;
import ua.yuriih.carrental.lab1.repository.PaymentDao;
import ua.yuriih.carrental.lab1.repository.RentRequestDao;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionWrapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
public class RentControllerTest {

    @Mock
    private RentRequestDao requestDao;

    @Mock
    private CarDao carDao;

    @Mock
    private PaymentDao paymentDao;

    private final RentController rentController = new RentController();

    private final Car testCar = new Car(
            0,
            "testModel",
            "testManufacturer",
            BigDecimal.valueOf(500),
            null,
            "testThumbnail.jpg",
            "testDescription",
            BigDecimal.valueOf(5000)
    );

    @Before
    public void init() {
        Whitebox.setInternalState(rentController, "requestDao", requestDao);
        Whitebox.setInternalState(rentController, "carDao", carDao);
        Whitebox.setInternalState(rentController, "paymentDao", paymentDao);
    }

    @Test
    public void addNewPending() {
        when(carDao.getCar(any(ConnectionWrapper.class), eq(0))).thenReturn(testCar);

        int carId = 0;
        long userId = 1L;
        int requestId = 2;
        int days = 5;
        LocalDate date = LocalDate.now();
        BigDecimal uahAmount = testCar.getHrnPerDay().multiply(BigDecimal.valueOf(days));

        when(requestDao.insert(
                any(ConnectionWrapper.class),
                eq(RentRequest.STATUS_PENDING),
                eq(""),
                eq(userId),
                eq(carId),
                eq(days),
                eq(date),
                eq(null)
        )).thenReturn(new RentRequest(
                requestId,
                RentRequest.STATUS_PENDING,
                "",
                userId,
                days,
                carId,
                date,
                null
        ));

        rentController.addNewPending(1, 0, days, date, uahAmount);

        verify(requestDao).insert(
                any(ConnectionWrapper.class),
                eq(RentRequest.STATUS_PENDING),
                eq(""),
                eq(userId),
                eq(carId),
                eq(days),
                eq(date),
                eq(null)
        );

        verify(paymentDao).insertPayment(
                any(ConnectionWrapper.class),
                eq(uahAmount),
                eq(requestId),
                eq(Payment.TYPE_REVENUE),
                eq(carId),
                any(Instant.class)
        );
    }

    @Test
    public void addNewPending_badPayment() {
        when(carDao.getCar(any(ConnectionWrapper.class), eq(0))).thenReturn(testCar);

        int days = 5;
        LocalDate date = LocalDate.now();
        BigDecimal uahAmount = testCar.getHrnPerDay().multiply(BigDecimal.valueOf(days))
                .subtract(BigDecimal.ONE);

        assertThrows(IllegalArgumentException.class, () -> {
            try {
                rentController.addNewPending(0, 0, days, date, uahAmount);
            } catch (RuntimeException e) {
                throw e.getCause();
            }
        });

    }


    @Test
    public void approve() {
        when(carDao.getCar(any(ConnectionWrapper.class), eq(0))).thenReturn(testCar);

        int carId = 0;
        long userId = 1L;
        int requestId = 2;
        LocalDate date = LocalDate.now();

        when(requestDao.getRequest(
                any(ConnectionWrapper.class),
                eq(requestId)
        )).thenReturn(new RentRequest(
                requestId,
                RentRequest.STATUS_PENDING,
                "",
                userId,
                5,
                carId,
                date,
                null
        ));

        rentController.approve(requestId);

        verify(requestDao).update(
                any(ConnectionWrapper.class),
                eq(new RentRequest(
                        requestId,
                        RentRequest.STATUS_ACTIVE,
                        "",
                        userId,
                        5,
                        carId,
                        date,
                        null
                ))
        );
        verify(requestDao).deleteALlPendingForCarId(any(ConnectionWrapper.class), eq(carId));

        verify(carDao).update(
                any(ConnectionWrapper.class),
                eq(new Car(
                        carId,
                        testCar.getModel(),
                        testCar.getManufacturer(),
                        testCar.getHrnPerDay(),
                        userId,
                        testCar.getThumbnailUrl(),
                        testCar.getDescription(),
                        testCar.getHrnPurchase()
                ))
        );
    }

    @Test
    public void deny() {
        when(carDao.getCar(any(ConnectionWrapper.class), eq(0))).thenReturn(testCar);

        int carId = 0;
        long userId = 1L;
        int requestId = 2;
        int paymentId = 3;
        int days = 5;
        LocalDate date = LocalDate.now();
        String denialMessage = "testDenialMessage";

        when(requestDao.getRequest(
                any(ConnectionWrapper.class),
                eq(requestId)
        )).thenReturn(new RentRequest(
                requestId,
                RentRequest.STATUS_PENDING,
                "",
                userId,
                days,
                carId,
                date,
                null
        ));

        when(paymentDao.getInitialPaymentForRequest(
                any(ConnectionWrapper.class),
                eq(requestId)
        )).thenReturn(new Payment(
                paymentId,
                testCar.getHrnPerDay().multiply(BigDecimal.valueOf(5)),
                requestId,
                Payment.TYPE_REVENUE,
                carId,
                Instant.now()
        ));

        rentController.deny(requestId, denialMessage);

        verify(requestDao).update(
                any(ConnectionWrapper.class),
                eq(new RentRequest(
                        requestId,
                        RentRequest.STATUS_DENIED,
                        denialMessage,
                        userId,
                        days,
                        carId,
                        date,
                        null
                ))
        );
        verify(paymentDao).insertPayment(
                any(ConnectionWrapper.class),
                eq(testCar.getHrnPerDay().multiply(BigDecimal.valueOf(5)).negate()),
                eq(requestId),
                eq(Payment.TYPE_REFUND),
                eq(carId),
                any(Instant.class)
        );
    }

    @Test
    public void end() {

    }
}