package com.mycompany.foundit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Adds rows, writing parent and child tables inside one transaction.
 */
public final class InsertMenu {

    private static final String LOCATION = "INSERT INTO Location (location_name) VALUES (?)";

    private static final String ITEM = "INSERT INTO Item (location_id, reporter_id, item_name, item_description, "
            + "item_category, item_type) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String FOUND_ITEM = "INSERT INTO FoundItem (item_id, found_at) VALUES (?, ?)";

    private static final String LOST_ITEM = "INSERT INTO LostItem (item_id, lost_at) VALUES (?, ?)";

    private static final String CLAIM_REQUEST = "INSERT INTO ClaimRequest (item_id, claimant_id, validator_id, "
            + "request_type) VALUES (?, ?, ?, ?)";

    private static final String VERIFIED_REQUEST = "INSERT INTO VerifiedRequest (claim_request_id, verification_date) "
            + "VALUES (?, NOW())";

    private static final String UNVERIFIED_REQUEST = "INSERT INTO UnverifiedRequest (claim_request_id, "
            + "rejection_reason) VALUES (?, ?)";

    private static final String ITEM_PHOTO = "INSERT INTO ItemPhoto (item_id, photo_url) VALUES (?, ?)";

    private final Connection conn;
    private final Console console;

    public InsertMenu(Connection conn, Console console) {
        this.conn = conn;
        this.console = console;
    }

    public void run() throws SQLException {
        console.printMenu("INSERT RECORD", "Location", "Item (found or lost)", "Claim request", "Item photo");

        switch (console.readChoice("Choose table: ")) {
            case 1 -> insertLocation();
            case 2 -> insertItem();
            case 3 -> insertClaimRequest();
            case 4 -> insertItemPhoto();
            default -> System.out.println("Invalid choice.");
        }
    }

    private void insertLocation() throws SQLException {
        String locationName = console.readLine("New location name: ");

        try (PreparedStatement ps = conn.prepareStatement(LOCATION)) {
            ps.setString(1, locationName);
            System.out.println(ps.executeUpdate() + " location inserted.");
        }
    }

    private void insertItem() throws SQLException {
        String typeInput = console.readLine("Item type (F = Found, L = Lost): ").toUpperCase();
        if (!typeInput.equals("F") && !typeInput.equals("L")) {
            System.out.println("Invalid item type.");
            return;
        }
        boolean isLost = typeInput.equals("L");

        String itemName = console.readLine("Item name: ");
        String itemDesc = console.readLine("Description: ");
        String itemCat = console.readLine("Category: ");
        int locId = console.readInt("Location ID: ");
        int reporterId = console.readInt("Reporter ID (user ID of a Reporter): ");
        Timestamp when = Timestamp.valueOf(console.readLine("Date and time (yyyy-MM-dd HH:mm:ss): "));

        conn.setAutoCommit(false);

        int newItemId;
        try (PreparedStatement ps = conn.prepareStatement(ITEM, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, locId);
            ps.setInt(2, reporterId);
            ps.setString(3, itemName);
            ps.setString(4, itemDesc);
            ps.setString(5, itemCat);
            ps.setString(6, isLost ? "LostItem" : "FoundItem");
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                newItemId = keys.getInt(1);
            }
        }

        try (PreparedStatement ps = conn.prepareStatement(isLost ? LOST_ITEM : FOUND_ITEM)) {
            ps.setInt(1, newItemId);
            ps.setTimestamp(2, when);
            ps.executeUpdate();
        }

        conn.commit();
        conn.setAutoCommit(true);
        System.out.println("Item inserted with ID " + newItemId + ".");
    }

    private void insertClaimRequest() throws SQLException {
        int itemId = console.readInt("Item ID being claimed: ");
        int claimantId = console.readInt("Claimant ID (user ID of a Claimant): ");
        int validatorId = console.readInt("Validator ID (user ID of a Validator): ");
        String typeInput = console.readLine("Request type (V = Verified, U = Unverified): ").toUpperCase();

        if (!typeInput.equals("V") && !typeInput.equals("U")) {
            System.out.println("Invalid request type.");
            return;
        }

        String requestType = "UnverifiedRequest";
        String reason = null;
        if (typeInput.equals("V")) {
            requestType = "VerifiedRequest";
        } else {
            reason = console.readLine("Rejection reason (press Enter to leave blank): ");
            if (reason.isEmpty()) {
                reason = null;
            }
        }

        conn.setAutoCommit(false);

        int newClaimId;
        try (PreparedStatement ps = conn.prepareStatement(CLAIM_REQUEST, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, itemId);
            ps.setInt(2, claimantId);
            ps.setInt(3, validatorId);
            ps.setString(4, requestType);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                newClaimId = keys.getInt(1);
            }
        }

        if (requestType.equals("VerifiedRequest")) {
            try (PreparedStatement ps = conn.prepareStatement(VERIFIED_REQUEST)) {
                ps.setInt(1, newClaimId);
                ps.executeUpdate();
            }
        } else {
            try (PreparedStatement ps = conn.prepareStatement(UNVERIFIED_REQUEST)) {
                ps.setInt(1, newClaimId);
                ps.setString(2, reason);
                ps.executeUpdate();
            }
        }

        conn.commit();
        conn.setAutoCommit(true);
        System.out.println("Claim request inserted with ID " + newClaimId + ".");
    }

    private void insertItemPhoto() throws SQLException {
        int itemId = console.readInt("Item ID: ");
        String photoUrl = console.readLine("Photo URL: ");

        try (PreparedStatement ps = conn.prepareStatement(ITEM_PHOTO)) {
            ps.setInt(1, itemId);
            ps.setString(2, photoUrl);
            System.out.println(ps.executeUpdate() + " photo inserted.");
        }
    }
}
