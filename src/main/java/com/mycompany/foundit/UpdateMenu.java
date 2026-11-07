package com.mycompany.foundit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Edits existing rows.
 */
public final class UpdateMenu {

    private static final String LOCATION =
            "UPDATE Location SET location_name = ? WHERE location_id = ?";

    private static final String ITEM =
            "UPDATE Item SET item_name = ?, item_description = ?, item_category = ? WHERE item_id = ?";

    private static final String USER_CONTACT =
            "UPDATE User SET contact_number = ? WHERE user_id = ?";

    private static final String REJECTION_REASON =
            "UPDATE UnverifiedRequest SET rejection_reason = ? WHERE claim_request_id = ?";

    private final Connection conn;
    private final Console console;

    public UpdateMenu(Connection conn, Console console) {
        this.conn = conn;
        this.console = console;
    }

    public void run() throws SQLException {
        console.printMenu("UPDATE RECORD",
                "Location name",
                "Item details (name, description, category)",
                "User contact number",
                "Rejection reason of an unverified claim");

        switch (console.readChoice("Choose: ")) {
            case 1 -> updateLocation();
            case 2 -> updateItem();
            case 3 -> updateUserContact();
            case 4 -> updateRejectionReason();
            default -> System.out.println("Invalid choice.");
        }
    }

    private void updateLocation() throws SQLException {
        int locId = console.readInt("Location ID to update: ");
        String newName = console.readLine("New location name: ");

        try (PreparedStatement ps = conn.prepareStatement(LOCATION)) {
            ps.setString(1, newName);
            ps.setInt(2, locId);
            System.out.println(ps.executeUpdate() + " row(s) updated.");
        }
    }

    private void updateItem() throws SQLException {
        int itemId = console.readInt("Item ID to update: ");
        String newName = console.readLine("New item name: ");
        String newDesc = console.readLine("New description: ");
        String newCat = console.readLine("New category: ");

        try (PreparedStatement ps = conn.prepareStatement(ITEM)) {
            ps.setString(1, newName);
            ps.setString(2, newDesc);
            ps.setString(3, newCat);
            ps.setInt(4, itemId);
            System.out.println(ps.executeUpdate() + " row(s) updated.");
        }
    }

    private void updateUserContact() throws SQLException {
        int userId = console.readInt("User ID to update: ");
        String newContact = console.readLine("New contact number: ");

        try (PreparedStatement ps = conn.prepareStatement(USER_CONTACT)) {
            ps.setString(1, newContact);
            ps.setInt(2, userId);
            System.out.println(ps.executeUpdate() + " row(s) updated.");
        }
    }

    private void updateRejectionReason() throws SQLException {
        int claimId = console.readInt("Claim request ID (must be an unverified request): ");
        String newReason = console.readLine("New rejection reason: ");

        try (PreparedStatement ps = conn.prepareStatement(REJECTION_REASON)) {
            ps.setString(1, newReason);
            ps.setInt(2, claimId);
            System.out.println(ps.executeUpdate() + " row(s) updated.");
        }
    }
}
