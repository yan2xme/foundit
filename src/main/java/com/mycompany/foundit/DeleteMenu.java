package com.mycompany.foundit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Removes rows, deleting child tables first inside one transaction.
 */
public final class DeleteMenu {

    private static final String ITEM_PHOTO = "DELETE FROM ItemPhoto WHERE item_photo_id = ?";

    private static final String VERIFIED_REQUEST = "DELETE FROM VerifiedRequest WHERE claim_request_id = ?";

    private static final String UNVERIFIED_REQUEST = "DELETE FROM UnverifiedRequest WHERE claim_request_id = ?";

    private static final String CLAIM_REQUEST = "DELETE FROM ClaimRequest WHERE claim_request_id = ?";

    private static final String ITEM = "DELETE FROM Item WHERE item_id = ?";

    private static final String FOUND_ITEM = "DELETE FROM FoundItem WHERE item_id = ?";

    private static final String LOST_ITEM = "DELETE FROM LostItem WHERE item_id = ?";

    private static final String LOCATION = "DELETE FROM Location WHERE location_id = ?";

    private final Connection conn;
    private final Console console;

    public DeleteMenu(Connection conn, Console console) {
        this.conn = conn;
        this.console = console;
    }

    public void run() throws SQLException {
        console.printMenu("DELETE RECORD",
                "Item photo",
                "Claim request",
                "Item (only if it has no claim requests)",
                "Location (only if no item uses it)");

        switch (console.readChoice("Choose: ")) {
            case 1 -> deleteItemPhoto();
            case 2 -> deleteClaimRequest();
            case 3 -> deleteItem();
            case 4 -> deleteLocation();
            default -> System.out.println("Invalid choice.");
        }
    }

    private void deleteItemPhoto() throws SQLException {
        int photoId = console.readInt("Item photo ID to delete: ");

        try (PreparedStatement ps = conn.prepareStatement(ITEM_PHOTO)) {
            ps.setInt(1, photoId);
            System.out.println(ps.executeUpdate() + " row(s) deleted.");
        }
    }

    private void deleteClaimRequest() throws SQLException {
        int claimId = console.readInt("Claim request ID to delete: ");

        conn.setAutoCommit(false);

        delete(VERIFIED_REQUEST, claimId);
        delete(UNVERIFIED_REQUEST, claimId);
        int rows = delete(CLAIM_REQUEST, claimId);

        conn.commit();
        conn.setAutoCommit(true);
        System.out.println(rows + " claim request(s) deleted.");
    }

    private void deleteItem() throws SQLException {
        int itemId = console.readInt("Item ID to delete: ");

        // Whatever a claim request still references makes the last delete
        // fail, which rolls the whole thing back.
        conn.setAutoCommit(false);

        delete(ITEM_PHOTO, itemId);
        delete(FOUND_ITEM, itemId);
        delete(LOST_ITEM, itemId);
        int rows = delete(ITEM, itemId);

        conn.commit();
        conn.setAutoCommit(true);
        System.out.println(rows + " item(s) deleted.");
    }

    private void deleteLocation() throws SQLException {
        int locId = console.readInt("Location ID to delete: ");

        try (PreparedStatement ps = conn.prepareStatement(LOCATION)) {
            ps.setInt(1, locId);
            System.out.println(ps.executeUpdate() + " row(s) deleted.");
        }
    }

    private int delete(String sql, int id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate();
        }
    }
}
