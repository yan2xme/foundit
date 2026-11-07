package com.mycompany.foundit;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Console front end for the FoundIt lost-and-found database.
 *
 * @author Shawn
 */
public class Foundit {

    public static void main(String[] args) {
        try (Scanner in = new Scanner(System.in)) {
            try (Connection conn = Database.connect(in)) {
                if (conn == null) {
                    return;
                }
                runMainMenu(conn, new Console(in));
            } catch (SQLException e) {
                System.out.println("Error closing the connection: " + e.getMessage());
            }
        }
    }

    private static void runMainMenu(Connection conn, Console console) {
        boolean running = true;

        while (running) {
            System.out.println("\n===== FoundIt Lost and Found System =====");
            System.out.println("1. View records");
            System.out.println("2. Insert record");
            System.out.println("3. Update record");
            System.out.println("4. Delete record");
            System.out.println("5. Reports / queries");
            System.out.println("6. Exit");

            try {
                conn.setAutoCommit(true);

                switch (console.readChoice("Choose: ")) {
                    case 1 -> new ViewMenu(conn, console).run();
                    case 2 -> new InsertMenu(conn, console).run();
                    case 3 -> new UpdateMenu(conn, console).run();
                    case 4 -> new DeleteMenu(conn, console).run();
                    case 5 -> new QueryMenu(conn, console).run();
                    case 6 -> {
                        System.out.println("Goodbye!");
                        running = false;
                    }
                    default -> System.out.println("Invalid choice. Please enter 1-6.");
                }
            } catch (SQLException e) {
                System.out.println("Error: " + e.getMessage());
                Database.rollbackIfNeeded(conn);
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please enter digits only.");
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid date. Use the format yyyy-MM-dd HH:mm:ss");
            }
        }
    }
}
