package ua.yuriih.carrental.lab1.repository.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public final class ConnectionPool {
    public static ConnectionPool INSTANCE = new ConnectionPool();
    private static final int MAX_CONNECTIONS = 10;

    private final ArrayList<Connection> allConnections = new ArrayList<>();
    private final LinkedBlockingQueue<Connection> unusedConnections = new LinkedBlockingQueue<>();

    public ConnectionWrapper getConnection() throws InterruptedException {
        synchronized (allConnections) {
            if (allConnections.size() < MAX_CONNECTIONS) {
                Connection connection = createConnection();
                allConnections.add(connection);
                unusedConnections.put(connection);
            }
        }
        return new ConnectionWrapper(unusedConnections.take(), this);
    }

    void release(ConnectionWrapper connectionWrapper) {
        unusedConnections.add(connectionWrapper.connection);
    }

    private Connection createConnection() {
        try {
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/oop", "oop", "");
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }
}
