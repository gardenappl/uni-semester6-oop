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
    public ResponseEntity approve(@PathVariable("id") int requestId) {
        rentRequestService.approve(requestId);
        return ResponseEntity.ok().build();
    }

    @Data
    public static class DenyRequest {
        private String message;
    }

    //admin only
    @PostMapping("/requests/deny/{id}")
    public ResponseEntity deny(@PathVariable("id") int requestId, @Validated @RequestBody DenyRequest request) {
        rentRequestService.deny(requestId, request.message);
        return ResponseEntity.ok().build();
    }

    @Data
    public static class EndSuccessfullyRequest {
        private BigDecimal maintenanceCostUah;
    }

    //admin only
    @PostMapping("/requests/end/{id}")
    public ResponseEntity end(@PathVariable("id") int requestId, @Validated @RequestBody EndSuccessfullyRequest request) {
        rentRequestService.endSuccessfully(requestId, request.maintenanceCostUah);
        return ResponseEntity.ok().build();
    }

    @Data
    public static class BrokenRequest {
        private String message;
        private BigDecimal repairCostUah;
    }

    //admin only
    @PostMapping("/requests/broken/{id}")
    public ResponseEntity broken(@PathVariable("id") int requestId, @Validated @RequestBody BrokenRequest request) {
        rentRequestService.setNeedsRepair(requestId, request.message, request.repairCostUah);
        return ResponseEntity.ok().build();
    }

    @Data
    public static class RepairRequest {
        private BigDecimal paymentUah;
    }

    @PostMapping("/requests/repair/{id}")
    public ResponseEntity repair(@PathVariable("id") int requestId, @Validated @RequestBody RepairRequest request) {
        rentRequestService.payForRepair(requestId, request.paymentUah);
        return ResponseEntity.ok().build();
    }

    @Data
    public static class NewRentRequest {
        private BigDecimal paymentUah;
        private String token;
        private int carId;
        private int days;
        private LocalDate startDate;
    }

    @PostMapping("/requests/new")
    public ResponseEntity<Integer> newPending(@Validated @RequestBody NewRentRequest request) {
        long userId = userService.getUserIdFromToken(request.token);
        return ResponseEntity.ok(rentRequestService.addNewPending(
                userId,
                request.carId,
                request.days,
                request.startDate,
                request.paymentUah
        ).getId());
    }
}
