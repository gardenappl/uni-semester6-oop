package ua.yuriih.carrental.lab1.repository.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;

public final class ConnectionPool {
    public static ConnectionPool INSTANCE = new ConnectionPool();
    public static final int MAX_CONNECTIONS = 10;

    private final ArrayList<Connection> allConnections = new ArrayList<>();
    private final LinkedBlockingQueue<Connection> unusedConnections = new LinkedBlockingQueue<>();

    public ConnectionWrapper getConnection() {
        Connection connection;
        synchronized (allConnections) {
            if (unusedConnections.size() == 0 && allConnections.size() < MAX_CONNECTIONS) {
                connection = createConnection();
                allConnections.add(connection);
            } else {
                connection = Objects.requireNonNull(unusedConnections.poll());
            }
        }
        return new ConnectionWrapper(connection, this);
    }

    public int getUnusedConnections() {
        return unusedConnections.size();
    }

    void release(ConnectionWrapper connectionWrapper) {
        unusedConnections.add(connectionWrapper.connection);
    }

    private Connection createConnection() {
//        System.err.println("Creating connection");
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection("jdbc:postgresql://localhost:5432/oop", "oop", "");
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
