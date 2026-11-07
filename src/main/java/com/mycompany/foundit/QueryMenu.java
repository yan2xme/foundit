package com.mycompany.foundit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * Runs the canned report queries.
 */
public final class QueryMenu {

    private static final String[] TITLES = {
        "Items in a category (WHERE)",
        "Items with location and reporter (JOIN)",
        "Items reported per reporter (JOIN + COUNT + GROUP BY)",
        "Found items never claimed (Subquery)",
        "Verified/unverified claims per validator (JOIN + SUM + GROUP BY)",
        "Items with more than one claim (GROUP BY + HAVING)",
        "Five most recently found items (ORDER BY)",
        "Average hours from found to claim, per category (AVG + GROUP BY)"
    };

    private static final String[] SQL = {
        "SELECT item_id, item_name, item_category, item_type FROM Item WHERE item_category = ?",

        "SELECT i.item_id, i.item_name, i.item_type, l.location_name, "
      + "CONCAT(u.first_name,' ',u.last_name) AS reporter "
      + "FROM Item i JOIN Location l ON l.location_id = i.location_id "
      + "JOIN User u ON u.user_id = i.reporter_id ORDER BY i.item_id",

        "SELECT u.user_id, CONCAT(u.first_name,' ',u.last_name) AS reporter, COUNT(i.item_id) AS items_reported "
      + "FROM User u JOIN Item i ON i.reporter_id = u.user_id "
      + "GROUP BY u.user_id, u.first_name, u.last_name ORDER BY items_reported DESC",

        "SELECT i.item_id, i.item_name, f.found_at FROM Item i "
      + "JOIN FoundItem f ON f.item_id = i.item_id "
      + "WHERE i.item_id NOT IN (SELECT item_id FROM ClaimRequest)",

        "SELECT u.user_id, CONCAT(u.first_name,' ',u.last_name) AS validator, "
      + "SUM(c.request_type = 'VerifiedRequest') AS verified, "
      + "SUM(c.request_type = 'UnverifiedRequest') AS unverified "
      + "FROM ClaimRequest c JOIN User u ON u.user_id = c.validator_id "
      + "GROUP BY u.user_id, u.first_name, u.last_name",

        "SELECT i.item_id, i.item_name, COUNT(*) AS claim_count "
      + "FROM ClaimRequest c JOIN Item i ON i.item_id = c.item_id "
      + "GROUP BY i.item_id, i.item_name HAVING COUNT(*) > 1",

        "SELECT i.item_id, i.item_name, f.found_at FROM FoundItem f "
      + "JOIN Item i ON i.item_id = f.item_id ORDER BY f.found_at DESC LIMIT 5",

        "SELECT i.item_category, ROUND(AVG(TIMESTAMPDIFF(HOUR, f.found_at, c.request_date)),1) AS avg_hours_to_claim "
      + "FROM ClaimRequest c JOIN FoundItem f ON f.item_id = c.item_id "
      + "JOIN Item i ON i.item_id = c.item_id GROUP BY i.item_category ORDER BY avg_hours_to_claim"
    };

    private final Connection conn;
    private final Console console;

    public QueryMenu(Connection conn, Console console) {
        this.conn = conn;
        this.console = console;
    }

    public void run() {
        while (true) {
            System.out.println("\n===== REPORTS / QUERIES =====");
            for (int i = 0; i < TITLES.length; i++) {
                System.out.printf("%d. %s%n", i + 1, TITLES[i]);
            }
            System.out.println("0. Back to main menu");

            int choice;
            try {
                choice = Integer.parseInt(console.readLine("Choose: "));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a number from 0 to " + TITLES.length + ".");
                continue;
            }

            if (choice == 0) {
                return;
            }
            if (choice < 1 || choice > TITLES.length) {
                System.out.println("Invalid choice. Enter a number from 0 to " + TITLES.length + ".");
                continue;
            }
            runQuery(choice - 1);
        }
    }

    private void runQuery(int idx) {
        try (PreparedStatement ps = conn.prepareStatement(SQL[idx])) {
            if (idx == 0) {
                String category = console.readLine("Category (e.g. Electronics, Clothing, Keys): ");
                if (category.isEmpty()) {
                    System.out.println("Category cannot be empty.");
                    return;
                }
                ps.setString(1, category);
            }

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n-- " + TITLES[idx] + " --");
                print(rs);
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void print(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();

        StringBuilder header = new StringBuilder();
        for (int c = 1; c <= cols; c++) {
            header.append(String.format("%-28s", md.getColumnLabel(c)));
        }
        System.out.println(header);
        System.out.println("-".repeat(header.length()));

        int rows = 0;
        while (rs.next()) {
            StringBuilder line = new StringBuilder();
            for (int c = 1; c <= cols; c++) {
                String value = rs.getString(c);
                line.append(String.format("%-28s", value == null ? "NULL" : value));
            }
            System.out.println(line);
            rows++;
        }
        if (rows == 0) {
            System.out.println("(no results)");
        }
    }
}
