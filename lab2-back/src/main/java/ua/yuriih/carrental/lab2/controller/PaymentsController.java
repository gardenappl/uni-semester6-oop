package ua.yuriih.carrental.lab2.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ua.yuriih.carrental.lab2.model.Payment;
import ua.yuriih.carrental.lab2.service.PaymentService;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class PaymentsController {
    private final PaymentService paymentService;

    //admin only
    @GetMapping("/payments")
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    //admin only
    @GetMapping("/payments/car/{id}")
    public ResponseEntity<List<Payment>> getPaymentsForCar(@PathVariable("id") int carId) {
        return ResponseEntity.ok(paymentService.getPaymentsForCar(carId));
    }
}
