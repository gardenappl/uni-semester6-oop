package ua.yuriih.carrental.lab2.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ua.yuriih.carrental.lab2.service.UserService;

@RestController
@CrossOrigin
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Data
    public static class LoginRequest {
        public final String username;
        public final String password;
    }

    @Data
    private static class LoginResponse {
        public final String token;
        public final boolean shouldUseAdminFrontend;
    }

    @PostMapping("login")
    public ResponseEntity<LoginResponse> login(@Validated @RequestBody LoginRequest request) {
        String token = userService.logIn(request.username, request.password);
        return ResponseEntity.ok(new LoginResponse(
                token,
                userService.shouldEnableAdminFrontend(token)
        ));
    }

    @Data
    public static class RegisterRequest {
        public long passportId;
        public final String username;
        public final String password;
    }

    @PostMapping("register")
    public ResponseEntity<LoginResponse> registerAndLogin(@Validated @RequestBody RegisterRequest request) {
        String token = userService.registerAndLogIn(request.passportId, request.username, request.password);
        return ResponseEntity.ok(new LoginResponse(
                token,
                userService.shouldEnableAdminFrontend(token)
        ));
    }
}
