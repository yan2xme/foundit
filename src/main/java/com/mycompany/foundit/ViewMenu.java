package com.mycompany.foundit;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Lists the rows of each table.
 */
public final class ViewMenu {

    private static final String USERS = "SELECT user_id, first_name, last_name, email, contact_number, user_type "
            + "FROM User ORDER BY user_id";

    private static final String LOCATIONS = "SELECT location_id, location_name FROM Location ORDER BY location_id";

    private static final String ITEMS = "SELECT i.item_id, i.item_name, i.item_category, i.item_type, l.location_name, "
            + "CONCAT(u.first_name, ' ', u.last_name) AS reporter, "
            + "COALESCE(f.found_at, lo.lost_at) AS event_time "
            + "FROM Item i "
            + "JOIN Location l ON i.location_id = l.location_id "
            + "JOIN User u ON i.reporter_id = u.user_id "
            + "LEFT JOIN FoundItem f ON i.item_id = f.item_id "
            + "LEFT JOIN LostItem lo ON i.item_id = lo.item_id "
            + "ORDER BY i.item_id";

    private static final String CLAIM_REQUESTS = "SELECT c.claim_request_id, i.item_name, "
            + "CONCAT(cu.first_name, ' ', cu.last_name) AS claimant, "
            + "CONCAT(vu.first_name, ' ', vu.last_name) AS validator, "
            + "c.request_date, c.request_type "
            + "FROM ClaimRequest c "
            + "JOIN Item i ON c.item_id = i.item_id "
            + "JOIN User cu ON c.claimant_id = cu.user_id "
            + "JOIN User vu ON c.validator_id = vu.user_id "
            + "ORDER BY c.claim_request_id";

    private static final String ITEM_PHOTOS = "SELECT item_photo_id, item_id, photo_url "
            + "FROM ItemPhoto ORDER BY item_photo_id";

    private final Connection conn;
    private final Console console;

    public ViewMenu(Connection conn, Console console) {
        this.conn = conn;
        this.console = console;
    }

    public void run() throws SQLException {
        console.printMenu("VIEW RECORDS", "Users", "Locations", "Items", "Claim requests", "Item photos");

        switch (console.readChoice("Choose table: ")) {
            case 1 -> viewUsers();
            case 2 -> viewLocations();
            case 3 -> viewItems();
            case 4 -> viewClaimRequests();
            case 5 -> viewItemPhotos();
            default -> System.out.println("Invalid choice.");
        }
    }

    private void viewUsers() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(USERS)) {
            System.out.printf("%-4s %-12s %-12s %-32s %-14s %-10s%n",
                    "ID", "First name", "Last name", "Email", "Contact", "Type");
            while (rs.next()) {
                System.out.printf("%-4d %-12s %-12s %-32s %-14s %-10s%n",
                        rs.getInt("user_id"), rs.getString("first_name"), rs.getString("last_name"),
                        rs.getString("email"), rs.getString("contact_number"), rs.getString("user_type"));
            }
        }
    }

    private void viewLocations() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(LOCATIONS)) {
            System.out.printf("%-4s %-30s%n", "ID", "Location");
            while (rs.next()) {
                System.out.printf("%-4d %-30s%n", rs.getInt("location_id"), rs.getString("location_name"));
            }
        }
    }

    private void viewItems() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(ITEMS)) {
            System.out.printf("%-4s %-24s %-14s %-10s %-22s %-18s %-19s%n",
                    "ID", "Item", "Category", "Type", "Location", "Reporter", "Date/Time");
            while (rs.next()) {
                System.out.printf("%-4d %-24s %-14s %-10s %-22s %-18s %-19s%n",
                        rs.getInt("item_id"), rs.getString("item_name"), rs.getString("item_category"),
                        rs.getString("item_type"), rs.getString("location_name"), rs.getString("reporter"),
                        rs.getString("event_time"));
            }
        }
    }

    private void viewClaimRequests() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(CLAIM_REQUESTS)) {
            System.out.printf("%-4s %-24s %-18s %-18s %-19s %-18s%n",
                    "ID", "Item", "Claimant", "Validator", "Request date", "Type");
            while (rs.next()) {
                System.out.printf("%-4d %-24s %-18s %-18s %-19s %-18s%n",
                        rs.getInt("claim_request_id"), rs.getString("item_name"), rs.getString("claimant"),
                        rs.getString("validator"), rs.getString("request_date"), rs.getString("request_type"));
            }
        }
    }

    private void viewItemPhotos() throws SQLException {
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(ITEM_PHOTOS)) {
            System.out.printf("%-8s %-8s %-40s%n", "PhotoID", "ItemID", "Photo URL");
            while (rs.next()) {
                System.out.printf("%-8d %-8d %-40s%n",
                        rs.getInt("item_photo_id"), rs.getInt("item_id"), rs.getString("photo_url"));
            }
        }
    }
}
