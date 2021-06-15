package ua.yuriih.carrental.lab2.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.yuriih.carrental.lab2.model.RentRequest;
import ua.yuriih.carrental.lab2.service.RentRequestService;
import ua.yuriih.carrental.lab2.service.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class RequestsController {
    private final RentRequestService rentRequestService;
    private final UserService userService;

    @GetMapping("/requests/outdated/{id}")
    public ResponseEntity<List<RentRequest>> getOutdatedRequests(@PathVariable("id") long userId) {
        return ResponseEntity.ok(rentRequestService.getActiveOutdatedRequests(userId));
    }

    @GetMapping("/requests/status/{status}/{id}")
    public ResponseEntity<List<RentRequest>> getRequests(@PathVariable("status") int status, @PathVariable("id") long userId) {
        return ResponseEntity.ok(rentRequestService.getRequestsWithStatusForUser(status, userId));
    }

    //admin only
    @GetMapping("/requests/outdated")
    public ResponseEntity<List<RentRequest>> getOutdatedRequests() {
        return ResponseEntity.ok(rentRequestService.getActiveOutdatedRequests());
    }

    //admin only
    @GetMapping("/requests/status/{status}")
    public ResponseEntity<List<RentRequest>> getRequests(@PathVariable("status") int status) {
        return ResponseEntity.ok(rentRequestService.getRequestsWithStatus(status));
    }

    //admin only
    @PostMapping("/requests/approve/{id}")
    public ResponseEntity<RentRequest> approve(@PathVariable("id") int requestId) {
        return ResponseEntity.ok(rentRequestService.approve(requestId));
    }

    @Data
    public static class DenyRequest {
        private final String message;
    }

    //admin only
    @PostMapping("/requests/deny/{id}")
    public ResponseEntity<RentRequest> deny(@PathVariable("id") int requestId, @Validated @RequestBody DenyRequest request) {
        return ResponseEntity.ok(rentRequestService.deny(requestId, request.message));
    }

    @Data
    public static class EndSuccessfullyRequest {
        private final BigDecimal maintenanceCostUah;
    }

    //admin only
    @PostMapping("/requests/end/{id}")
    public ResponseEntity<RentRequest> end(@PathVariable("id") int requestId, @Validated @RequestBody EndSuccessfullyRequest request) {
        return ResponseEntity.ok(rentRequestService.endSuccessfully(requestId, request.maintenanceCostUah));
    }

    @Data
    public static class BrokenRequest {
        private final String message;
        private final BigDecimal repairCostUah;
    }

    //admin only
    @PostMapping("/requests/broken/{id}")
    public ResponseEntity<RentRequest> broken(@PathVariable("id") int requestId, @Validated @RequestBody BrokenRequest request) {
        return ResponseEntity.ok(rentRequestService.setNeedsRepair(requestId, request.message, request.repairCostUah));
    }

    @Data
    public static class RepairRequest {
        private final BigDecimal paymentUah;
    }

    @PostMapping("/requests/repair/{id}")
    public ResponseEntity<RentRequest> repair(@PathVariable("id") int requestId, @Validated @RequestBody RepairRequest request) {
        return ResponseEntity.ok(rentRequestService.payForRepair(requestId, request.paymentUah));
    }

    @Data
    public static class NewRentRequest {
        private final BigDecimal paymentUah;
        private final String token;
        private final int carId;
        private final int days;
        private final LocalDate startDate;
    }

    @PostMapping("/requests/new")
    public ResponseEntity<RentRequest> newPending(@Validated @RequestBody NewRentRequest request) {
        long userId = userService.getUserIdFromToken(request.token);
        return ResponseEntity.ok(rentRequestService.addNewPending(
                userId,
                request.carId,
                request.days,
                request.startDate,
                request.paymentUah
        ));
    }
}
