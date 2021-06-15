package ua.yuriih.carrental.lab2.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.yuriih.carrental.lab2.model.User;
import ua.yuriih.carrental.lab2.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(token);
    }

    public boolean shouldEnableAdminFrontend(String token) {
        return userRepository.findByPassportId(getUserIdFromToken(token)).getName().equals("admin");
    }

    @Transactional
    public String registerAndLogIn(long passportId, String username, String password) {
        User user = userRepository.findByPassportId(passportId);
        if (user == null) {
            userRepository.save(new User(passportId, username, password));
        }
        return logIn(username, password);
    }

    public String logIn(String username, String password) {
        User user = userRepository.findByName(username);
        if (user == null)
            return null;
        if (!password.equals(user.getKeycloakId()))
            return null;

        return Long.toString(user.getPassportId());
    }
}
