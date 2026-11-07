package com.mycompany.foundit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Opens the FoundIt connection and cleans up failed transactions.
 */
public final class Database {

    private static final String URL = "jdbc:mysql://localhost:3306/foundit_db";
    private static final String USER = "root";

    private Database() {
    }

    /**
     * Asks for the password and returns a connection, or {@code null} when the attempt fails.
     */
    public static Connection connect(Scanner in) {
        System.out.print("Database password for " + USER + ": ");
        String password = in.nextLine();

        System.out.println("Connecting to database...");
        try {
            Connection conn = DriverManager.getConnection(URL, USER, password);
            System.out.println("Database connected successfully!");
            return conn;
        } catch (SQLException e) {
            System.out.println("Connection failed!");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void rollbackIfNeeded(Connection conn) {
        try {
            if (!conn.getAutoCommit()) {
                conn.rollback();
                conn.setAutoCommit(true);
                System.out.println("Changes were rolled back.");
            }
        } catch (SQLException e) {
            System.out.println("Rollback failed: " + e.getMessage());
        }
    }
}
