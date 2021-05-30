package ua.yuriih.carrental.lab1.controller;

import ua.yuriih.carrental.lab1.model.User;
import ua.yuriih.carrental.lab1.repository.UserDao;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionPool;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionWrapper;

import java.util.HashMap;
import java.util.Random;

public final class UserController {
    public static final UserController INSTANCE = new UserController();

    private static final Random RNG = new Random();
    private final HashMap<Integer, Integer> userTokens = new HashMap<>();

    private UserController() {}

    public Integer registerAndLogIn(String username, String password) {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();

        return connection.doTransaction(() -> {
            User user = UserDao.INSTANCE.getUserByUsername(connection, username);

            if (user == null) {
                UserDao.INSTANCE.insertUser(connection, username, password);
            }
            return logIn(username, password);
        });
    }

    public Integer logIn(String username, String password) {
        ConnectionWrapper connection = ConnectionPool.INSTANCE.getConnection();

        User user = UserDao.INSTANCE.getUserByUsername(connection, username);

        if (user == null)
            return null;
        if (!password.equals(user.getPassword()))
            return null;

        synchronized (userTokens) {
            Integer token = userTokens.getOrDefault(user.getPassportId(), null);

            if (token == null) {
                token = RNG.nextInt();
                userTokens.put(user.getPassportId(), token);
            }

            return token;
        }
    }
}
