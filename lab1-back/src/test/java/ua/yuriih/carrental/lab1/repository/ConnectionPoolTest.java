package ua.yuriih.carrental.lab1.repository;

import org.junit.Test;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionPool;
import ua.yuriih.carrental.lab1.repository.connection.ConnectionWrapper;

import static org.junit.Assert.*;

public class ConnectionPoolTest {
    @Test
    public void getConnection() {
        ConnectionPool pool = ConnectionPool.INSTANCE;

        ConnectionWrapper[] wrappers = new ConnectionWrapper[ConnectionPool.MAX_CONNECTIONS];
        for (int i = 0; i < ConnectionPool.MAX_CONNECTIONS; i++) {
            wrappers[i] = pool.getConnection();
        }

        //Fill the pool
        assertEquals(0, pool.getUnusedConnectionsCount());

        //Free everything
        for (int i = 0; i < ConnectionPool.MAX_CONNECTIONS; i++) {
            assertEquals(i, pool.getUnusedConnectionsCount());
            wrappers[i].close();
            assertEquals(i + 1, pool.getUnusedConnectionsCount());
        }
    }

    @Test
    public void getConnection_autoclose() {
        ConnectionPool pool = ConnectionPool.INSTANCE;

        //Fill the pool and free everything, so we have max. unused connections

        ConnectionWrapper[] wrappers = new ConnectionWrapper[ConnectionPool.MAX_CONNECTIONS];
        for (int i = 0; i < ConnectionPool.MAX_CONNECTIONS; i++) {
            wrappers[i] = pool.getConnection();
        }
        for (int i = 0; i < ConnectionPool.MAX_CONNECTIONS; i++) {
            wrappers[i].close();
        }

        //Acquire and auto-close connections

        assertEquals(ConnectionPool.MAX_CONNECTIONS, pool.getUnusedConnectionsCount());

        try (ConnectionWrapper connection1 = pool.getConnection()) {
            assertEquals(ConnectionPool.MAX_CONNECTIONS - 1, pool.getUnusedConnectionsCount());

            try (ConnectionWrapper connection2 = pool.getConnection()) {
                assertEquals(ConnectionPool.MAX_CONNECTIONS - 2, pool.getUnusedConnectionsCount());
            }

            assertEquals(ConnectionPool.MAX_CONNECTIONS - 1, pool.getUnusedConnectionsCount());
        }

        assertEquals(ConnectionPool.MAX_CONNECTIONS, pool.getUnusedConnectionsCount());
    }
}