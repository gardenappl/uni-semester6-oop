package ua.yuriih.carrental.lab2.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ua.yuriih.carrental.lab2.model.Car;
import ua.yuriih.carrental.lab2.model.Payment;
import ua.yuriih.carrental.lab2.model.RentRequest;
import ua.yuriih.carrental.lab2.repository.CarRepository;
import ua.yuriih.carrental.lab2.repository.PaymentRepository;
import ua.yuriih.carrental.lab2.repository.RentRequestRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class RentRequestServiceTest {
    @MockBean
    private RentRequestRepository requestRepository;

    @MockBean
    private CarRepository carRepository;

    @MockBean
    private PaymentRepository paymentRepository;

    @Autowired
    private RentRequestService requestService;

    private static final Car testCar = new Car();

    @BeforeAll
    public static void init() {
        testCar.setModel("testModel");
        testCar.setManufacturer("testManufacturer");
        testCar.setUahPerDay(BigDecimal.valueOf(500));
        testCar.setThumbnailUrl("thumb.jpg");
        testCar.setDescription("desc");
        testCar.setUahPurchase(BigDecimal.valueOf(50000));
    }

    @Test
    public void addNewPending() {
        when(carRepository.getById(0)).thenReturn(testCar);

        int carId = 0;
        long userId = 1L;
        int days = 5;
        LocalDate date = LocalDate.now();
        BigDecimal uahAmount = testCar.getUahPerDay().multiply(BigDecimal.valueOf(days));

        RentRequest pendingRequest = new RentRequest();
        pendingRequest.setStatus(RentRequest.STATUS_PENDING);
        pendingRequest.setStatusMessage("");
        pendingRequest.setUserId(userId);
        pendingRequest.setCar(testCar);
        pendingRequest.setDays(days);
        pendingRequest.setStartDate(date);
        pendingRequest.setRepairCost(null);
        when(requestRepository.save(pendingRequest)).thenReturn(pendingRequest);

        requestService.addNewPending(userId, carId, days, date, uahAmount);

        verify(paymentRepository).save(any());
        verify(requestRepository).save(pendingRequest);
    }

    @Test
    public void addNewPending_badPayment() {
        when(carRepository.getById(0)).thenReturn(testCar);

        int carId = 0;
        long userId = 1L;
        int days = 5;
        LocalDate date = LocalDate.now();
        BigDecimal uahAmount = testCar.getUahPerDay().multiply(BigDecimal.valueOf(days))
                .subtract(BigDecimal.ONE);

        RentRequest pendingRequest = new RentRequest();
        pendingRequest.setStatus(RentRequest.STATUS_PENDING);
        pendingRequest.setStatusMessage("");
        pendingRequest.setUserId(userId);
        pendingRequest.setCar(testCar);
        pendingRequest.setDays(days);
        pendingRequest.setStartDate(date);
        pendingRequest.setRepairCost(null);
        when(requestRepository.save(pendingRequest)).thenReturn(pendingRequest);

        assertThrows(IllegalArgumentException.class, () -> {
            try {
                requestService.addNewPending(userId, carId, days, date, uahAmount);
            } catch (RuntimeException e) {
                throw e.getCause();
            }
        });
    }


    @Test
    public void approve() {
        when(carRepository.getById(0)).thenReturn(testCar);

        long userId = 1L;
        int requestId = 2;
        int days = 5;
        LocalDate date = LocalDate.now();

        RentRequest pendingRequest = new RentRequest();
        pendingRequest.setStatus(RentRequest.STATUS_PENDING);
        pendingRequest.setStatusMessage("");
        pendingRequest.setUserId(userId);
        pendingRequest.setCar(testCar);
        pendingRequest.setDays(days);
        pendingRequest.setStartDate(date);
        pendingRequest.setRepairCost(null);

        RentRequest approvedRequest = new RentRequest();
        pendingRequest.setStatus(RentRequest.STATUS_ACTIVE);
        pendingRequest.setStatusMessage("");
        pendingRequest.setUserId(userId);
        pendingRequest.setCar(testCar);
        pendingRequest.setDays(days);
        pendingRequest.setStartDate(date);
        pendingRequest.setRepairCost(null);

        when(requestRepository.getById(requestId)).thenReturn(pendingRequest);
        when(requestRepository.save(approvedRequest)).thenReturn(approvedRequest);

        requestService.approve(requestId);

        verify(requestRepository).save(approvedRequest);
        verify(requestRepository).deleteAllByCarAndStatus(testCar, RentRequest.STATUS_PENDING);
    }

    @Test
    public void deny() {

    }

    @Test
    public void end() {

    }
}