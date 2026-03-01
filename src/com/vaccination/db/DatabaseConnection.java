package com.vaccination.db;

import java.sql.*;

/**
 * Singleton JDBC Connection Manager
 * Manages connection lifecycle: open → use → close
 */
public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/vaccination_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String USER     = "root";
    private static final String PASSWORD = "root";   // ← change to your MySQL password

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found! Add mysql-connector-j.jar to lib/", e);
        } catch (SQLException e) {
            throw new RuntimeException("Cannot connect to database: " + e.getMessage(), e);
        }
    }

    public static synchronized DatabaseConnection getInstance() {
        try {
            if (instance == null || instance.connection.isClosed()) {
                instance = new DatabaseConnection();
            }
        } catch (SQLException e) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }

    /** Convenience: close resources without boilerplate */
    public static void close(ResultSet rs, Statement st) {
        try { if (rs != null) rs.close(); } catch (SQLException ignored) {}
        try { if (st != null) st.close(); } catch (SQLException ignored) {}
    }

    public void closeConnection() {
        try { if (connection != null && !connection.isClosed()) connection.close(); }
        catch (SQLException ignored) {}
    }
}
