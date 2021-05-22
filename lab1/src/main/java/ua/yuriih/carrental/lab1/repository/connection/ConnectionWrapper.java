package ua.yuriih.carrental.lab1.repository.connection;

import ua.yuriih.carrental.lab1.repository.connection.ConnectionPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectionWrapper {
    private final PreparedStatement beginTransactionStatement;
    private final PreparedStatement commitTransactionStatement;
    private final PreparedStatement rollbackTransactionStatement;

    final Connection connection;
    private boolean isClosed = false;
    private final ConnectionPool pool;

    ConnectionWrapper(Connection connection, ConnectionPool pool) {
        this.connection = connection;
        this.pool = pool;
        try {
            beginTransactionStatement = connection.prepareStatement("BEGIN");
            commitTransactionStatement = connection.prepareStatement("COMMIT");
            rollbackTransactionStatement = connection.prepareStatement("ROLLBACK");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public PreparedStatement prepareStatement(String command) {
        if (isClosed)
            throw new IllegalStateException();

        try {
            return connection.prepareStatement(command);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doTransaction(Transaction transaction) {
        try {
            connection.setAutoCommit(false);
            beginTransactionStatement.execute();

            transaction.doTransaction();
            commitTransactionStatement.execute();
            connection.setAutoCommit(true);
        } catch (Exception e) {
            try {
                rollbackTransactionStatement.execute();
                connection.setAutoCommit(true);
            } catch (SQLException sqlException) {
                throw new RuntimeException(sqlException);
            }
            throw new RuntimeException(e);
        }
    }

    public void close() {
        synchronized (pool) {
            if (!isClosed) {
                isClosed = true;
                pool.release(this);
            }
        }
    }

    public interface Transaction {
        void doTransaction() throws SQLException;
    }
}
