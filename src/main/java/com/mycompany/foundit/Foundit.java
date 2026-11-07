/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.foundit;

/**
 *
 * @author Shawn
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Scanner;

public class Foundit {

    public static void main(String[] args) {
        // Connection parameters
        String url = "jdbc:mysql://localhost:3306/foundit_db";
        String username = "root";
        String password = "root";

        Scanner sc = new Scanner(System.in);

        System.out.println("Connecting to database...");

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            System.out.println("Database connected successfully!");

            boolean running = true;

            while (running) {
                System.out.println("\n===== FoundIt Lost and Found System =====");
                System.out.println("1. View records");
                System.out.println("2. Insert record");
                System.out.println("3. Update record");
                System.out.println("4. Delete record");
                System.out.println("5. Exit");
                System.out.print("Choose: ");
                String mainChoice = sc.nextLine().trim();

                try {
                    conn.setAutoCommit(true);

                    // ======================= VIEW =======================
                    if (mainChoice.equals("1")) {
                        System.out.println("\n--- VIEW RECORDS ---");
                        System.out.println("1. Users");
                        System.out.println("2. Locations");
                        System.out.println("3. Items");
                        System.out.println("4. Claim requests");
                        System.out.println("5. Item photos");
                        System.out.print("Choose table: ");
                        String t = sc.nextLine().trim();

                        Statement st = conn.createStatement();
                        ResultSet rs = null;

                        if (t.equals("1")) {
                            rs = st.executeQuery("SELECT user_id, first_name, last_name, email, contact_number, user_type FROM User ORDER BY user_id");
                            System.out.printf("%-4s %-12s %-12s %-32s %-14s %-10s%n", "ID", "First name", "Last name", "Email", "Contact", "Type");
                            while (rs.next()) {
                                System.out.printf("%-4d %-12s %-12s %-32s %-14s %-10s%n",
                                        rs.getInt("user_id"), rs.getString("first_name"), rs.getString("last_name"),
                                        rs.getString("email"), rs.getString("contact_number"), rs.getString("user_type"));
                            }
                        } else if (t.equals("2")) {
                            rs = st.executeQuery("SELECT location_id, location_name FROM Location ORDER BY location_id");
                            System.out.printf("%-4s %-30s%n", "ID", "Location");
                            while (rs.next()) {
                                System.out.printf("%-4d %-30s%n", rs.getInt("location_id"), rs.getString("location_name"));
                            }
                        } else if (t.equals("3")) {
                            rs = st.executeQuery("SELECT i.item_id, i.item_name, i.item_category, i.item_type, l.location_name, "
                                    + "CONCAT(u.first_name, ' ', u.last_name) AS reporter, "
                                    + "COALESCE(f.found_at, lo.lost_at) AS event_time "
                                    + "FROM Item i "
                                    + "JOIN Location l ON i.location_id = l.location_id "
                                    + "JOIN User u ON i.reporter_id = u.user_id "
                                    + "LEFT JOIN FoundItem f ON i.item_id = f.item_id "
                                    + "LEFT JOIN LostItem lo ON i.item_id = lo.item_id "
                                    + "ORDER BY i.item_id");
                            System.out.printf("%-4s %-24s %-14s %-10s %-22s %-18s %-19s%n", "ID", "Item", "Category", "Type", "Location", "Reporter", "Date/Time");
                            while (rs.next()) {
                                System.out.printf("%-4d %-24s %-14s %-10s %-22s %-18s %-19s%n",
                                        rs.getInt("item_id"), rs.getString("item_name"), rs.getString("item_category"),
                                        rs.getString("item_type"), rs.getString("location_name"), rs.getString("reporter"),
                                        rs.getString("event_time"));
                            }
                        } else if (t.equals("4")) {
                            rs = st.executeQuery("SELECT c.claim_request_id, i.item_name, "
                                    + "CONCAT(cu.first_name, ' ', cu.last_name) AS claimant, "
                                    + "CONCAT(vu.first_name, ' ', vu.last_name) AS validator, "
                                    + "c.request_date, c.request_type "
                                    + "FROM ClaimRequest c "
                                    + "JOIN Item i ON c.item_id = i.item_id "
                                    + "JOIN User cu ON c.claimant_id = cu.user_id "
                                    + "JOIN User vu ON c.validator_id = vu.user_id "
                                    + "ORDER BY c.claim_request_id");
                            System.out.printf("%-4s %-24s %-18s %-18s %-19s %-18s%n", "ID", "Item", "Claimant", "Validator", "Request date", "Type");
                            while (rs.next()) {
                                System.out.printf("%-4d %-24s %-18s %-18s %-19s %-18s%n",
                                        rs.getInt("claim_request_id"), rs.getString("item_name"), rs.getString("claimant"),
                                        rs.getString("validator"), rs.getString("request_date"), rs.getString("request_type"));
                            }
                        } else if (t.equals("5")) {
                            rs = st.executeQuery("SELECT item_photo_id, item_id, photo_url FROM ItemPhoto ORDER BY item_photo_id");
                            System.out.printf("%-8s %-8s %-40s%n", "PhotoID", "ItemID", "Photo URL");
                            while (rs.next()) {
                                System.out.printf("%-8d %-8d %-40s%n", rs.getInt("item_photo_id"), rs.getInt("item_id"), rs.getString("photo_url"));
                            }
                        } else {
                            System.out.println("Invalid choice.");
                        }
                        st.close();

                    // ======================= INSERT =======================
                    } else if (mainChoice.equals("2")) {
                        System.out.println("\n--- INSERT RECORD ---");
                        System.out.println("1. Location");
                        System.out.println("2. Item (found or lost)");
                        System.out.println("3. Claim request");
                        System.out.println("4. Item photo");
                        System.out.print("Choose table: ");
                        String t = sc.nextLine().trim();

                        if (t.equals("1")) {
                            System.out.print("New location name: ");
                            String locName = sc.nextLine().trim();

                            PreparedStatement ps = conn.prepareStatement("INSERT INTO Location (location_name) VALUES (?)");
                            ps.setString(1, locName);
                            int rows = ps.executeUpdate();
                            ps.close();
                            System.out.println(rows + " location inserted.");

                        } else if (t.equals("2")) {
                            System.out.print("Item type (F = Found, L = Lost): ");
                            String typeInput = sc.nextLine().trim().toUpperCase();

                            if (!typeInput.equals("F") && !typeInput.equals("L")) {
                                System.out.println("Invalid item type.");
                            } else {
                                String itemType = "FoundItem";
                                if (typeInput.equals("L")) {
                                    itemType = "LostItem";
                                }

                                System.out.print("Item name: ");
                                String itemName = sc.nextLine().trim();
                                System.out.print("Description: ");
                                String itemDesc = sc.nextLine().trim();
                                System.out.print("Category: ");
                                String itemCat = sc.nextLine().trim();
                                System.out.print("Location ID: ");
                                int locId = Integer.parseInt(sc.nextLine().trim());
                                System.out.print("Reporter ID (user ID of a Reporter): ");
                                int reporterId = Integer.parseInt(sc.nextLine().trim());
                                System.out.print("Date and time (yyyy-MM-dd HH:mm:ss): ");
                                Timestamp when = Timestamp.valueOf(sc.nextLine().trim());

                                // Parent row first, then the FoundItem / LostItem row
                                conn.setAutoCommit(false);

                                PreparedStatement ps = conn.prepareStatement(
                                        "INSERT INTO Item (location_id, reporter_id, item_name, item_description, item_category, item_type) VALUES (?, ?, ?, ?, ?, ?)",
                                        Statement.RETURN_GENERATED_KEYS);
                                ps.setInt(1, locId);
                                ps.setInt(2, reporterId);
                                ps.setString(3, itemName);
                                ps.setString(4, itemDesc);
                                ps.setString(5, itemCat);
                                ps.setString(6, itemType);
                                ps.executeUpdate();

                                ResultSet keys = ps.getGeneratedKeys();
                                keys.next();
                                int newItemId = keys.getInt(1);
                                ps.close();

                                String childSql = "INSERT INTO FoundItem (item_id, found_at) VALUES (?, ?)";
                                if (itemType.equals("LostItem")) {
                                    childSql = "INSERT INTO LostItem (item_id, lost_at) VALUES (?, ?)";
                                }
                                ps = conn.prepareStatement(childSql);
                                ps.setInt(1, newItemId);
                                ps.setTimestamp(2, when);
                                ps.executeUpdate();
                                ps.close();

                                conn.commit();
                                conn.setAutoCommit(true);
                                System.out.println("Item inserted with ID " + newItemId + ".");
                            }

                        } else if (t.equals("3")) {
                            System.out.print("Item ID being claimed: ");
                            int itemId = Integer.parseInt(sc.nextLine().trim());
                            System.out.print("Claimant ID (user ID of a Claimant): ");
                            int claimantId = Integer.parseInt(sc.nextLine().trim());
                            System.out.print("Validator ID (user ID of a Validator): ");
                            int validatorId = Integer.parseInt(sc.nextLine().trim());
                            System.out.print("Request type (V = Verified, U = Unverified): ");
                            String typeInput = sc.nextLine().trim().toUpperCase();

                            if (!typeInput.equals("V") && !typeInput.equals("U")) {
                                System.out.println("Invalid request type.");
                            } else {
                                String requestType = "UnverifiedRequest";
                                String reason = null;
                                if (typeInput.equals("V")) {
                                    requestType = "VerifiedRequest";
                                } else {
                                    System.out.print("Rejection reason (press Enter to leave blank): ");
                                    reason = sc.nextLine().trim();
                                    if (reason.equals("")) {
                                        reason = null;
                                    }
                                }

                                // Parent row first, then the Verified / Unverified row
                                conn.setAutoCommit(false);

                                PreparedStatement ps = conn.prepareStatement(
                                        "INSERT INTO ClaimRequest (item_id, claimant_id, validator_id, request_type) VALUES (?, ?, ?, ?)",
                                        Statement.RETURN_GENERATED_KEYS);
                                ps.setInt(1, itemId);
                                ps.setInt(2, claimantId);
                                ps.setInt(3, validatorId);
                                ps.setString(4, requestType);
                                ps.executeUpdate();

                                ResultSet keys = ps.getGeneratedKeys();
                                keys.next();
                                int newClaimId = keys.getInt(1);
                                ps.close();

                                if (requestType.equals("VerifiedRequest")) {
                                    ps = conn.prepareStatement("INSERT INTO VerifiedRequest (claim_request_id, verification_date) VALUES (?, NOW())");
                                    ps.setInt(1, newClaimId);
                                } else {
                                    ps = conn.prepareStatement("INSERT INTO UnverifiedRequest (claim_request_id, rejection_reason) VALUES (?, ?)");
                                    ps.setInt(1, newClaimId);
                                    ps.setString(2, reason);
                                }
                                ps.executeUpdate();
                                ps.close();

                                conn.commit();
                                conn.setAutoCommit(true);
                                System.out.println("Claim request inserted with ID " + newClaimId + ".");
                            }

                        } else if (t.equals("4")) {
                            System.out.print("Item ID: ");
                            int itemId = Integer.parseInt(sc.nextLine().trim());
                            System.out.print("Photo URL: ");
                            String photoUrl = sc.nextLine().trim();

                            PreparedStatement ps = conn.prepareStatement("INSERT INTO ItemPhoto (item_id, photo_url) VALUES (?, ?)");
                            ps.setInt(1, itemId);
                            ps.setString(2, photoUrl);
                            int rows = ps.executeUpdate();
                            ps.close();
                            System.out.println(rows + " photo inserted.");

                        } else {
                            System.out.println("Invalid choice.");
                        }

                    // ======================= UPDATE =======================
                    } else if (mainChoice.equals("3")) {
                        System.out.println("\n--- UPDATE RECORD ---");
                        System.out.println("1. Location name");
                        System.out.println("2. Item details (name, description, category)");
                        System.out.println("3. User contact number");
                        System.out.println("4. Rejection reason of an unverified claim");
                        System.out.print("Choose: ");
                        String t = sc.nextLine().trim();

                        if (t.equals("1")) {
                            System.out.print("Location ID to update: ");
                            int locId = Integer.parseInt(sc.nextLine().trim());
                            System.out.print("New location name: ");
                            String newName = sc.nextLine().trim();

                            PreparedStatement ps = conn.prepareStatement("UPDATE Location SET location_name = ? WHERE location_id = ?");
                            ps.setString(1, newName);
                            ps.setInt(2, locId);
                            int rows = ps.executeUpdate();
                            ps.close();
                            System.out.println(rows + " row(s) updated.");

                        } else if (t.equals("2")) {
                            System.out.print("Item ID to update: ");
                            int itemId = Integer.parseInt(sc.nextLine().trim());
                            System.out.print("New item name: ");
                            String newName = sc.nextLine().trim();
                            System.out.print("New description: ");
                            String newDesc = sc.nextLine().trim();
                            System.out.print("New category: ");
                            String newCat = sc.nextLine().trim();

                            PreparedStatement ps = conn.prepareStatement("UPDATE Item SET item_name = ?, item_description = ?, item_category = ? WHERE item_id = ?");
                            ps.setString(1, newName);
                            ps.setString(2, newDesc);
                            ps.setString(3, newCat);
                            ps.setInt(4, itemId);
                            int rows = ps.executeUpdate();
                            ps.close();
                            System.out.println(rows + " row(s) updated.");

                        } else if (t.equals("3")) {
                            System.out.print("User ID to update: ");
                            int userId = Integer.parseInt(sc.nextLine().trim());
                            System.out.print("New contact number: ");
                            String newContact = sc.nextLine().trim();

                            PreparedStatement ps = conn.prepareStatement("UPDATE User SET contact_number = ? WHERE user_id = ?");
                            ps.setString(1, newContact);
                            ps.setInt(2, userId);
                            int rows = ps.executeUpdate();
                            ps.close();
                            System.out.println(rows + " row(s) updated.");

                        } else if (t.equals("4")) {
                            System.out.print("Claim request ID (must be an unverified request): ");
                            int claimId = Integer.parseInt(sc.nextLine().trim());
                            System.out.print("New rejection reason: ");
                            String newReason = sc.nextLine().trim();

                            PreparedStatement ps = conn.prepareStatement("UPDATE UnverifiedRequest SET rejection_reason = ? WHERE claim_request_id = ?");
                            ps.setString(1, newReason);
                            ps.setInt(2, claimId);
                            int rows = ps.executeUpdate();
                            ps.close();
                            System.out.println(rows + " row(s) updated.");

                        } else {
                            System.out.println("Invalid choice.");
                        }

                    // ======================= DELETE =======================
                    } else if (mainChoice.equals("4")) {
                        System.out.println("\n--- DELETE RECORD ---");
                        System.out.println("1. Item photo");
                        System.out.println("2. Claim request");
                        System.out.println("3. Item (only if it has no claim requests)");
                        System.out.println("4. Location (only if no item uses it)");
                        System.out.print("Choose: ");
                        String t = sc.nextLine().trim();

                        if (t.equals("1")) {
                            System.out.print("Item photo ID to delete: ");
                            int photoId = Integer.parseInt(sc.nextLine().trim());

                            PreparedStatement ps = conn.prepareStatement("DELETE FROM ItemPhoto WHERE item_photo_id = ?");
                            ps.setInt(1, photoId);
                            int rows = ps.executeUpdate();
                            ps.close();
                            System.out.println(rows + " row(s) deleted.");

                        } else if (t.equals("2")) {
                            System.out.print("Claim request ID to delete: ");
                            int claimId = Integer.parseInt(sc.nextLine().trim());

                            // Child rows first, then the ClaimRequest row
                            conn.setAutoCommit(false);

                            PreparedStatement ps = conn.prepareStatement("DELETE FROM VerifiedRequest WHERE claim_request_id = ?");
                            ps.setInt(1, claimId);
                            ps.executeUpdate();
                            ps.close();

                            ps = conn.prepareStatement("DELETE FROM UnverifiedRequest WHERE claim_request_id = ?");
                            ps.setInt(1, claimId);
                            ps.executeUpdate();
                            ps.close();

                            ps = conn.prepareStatement("DELETE FROM ClaimRequest WHERE claim_request_id = ?");
                            ps.setInt(1, claimId);
                            int rows = ps.executeUpdate();
                            ps.close();

                            conn.commit();
                            conn.setAutoCommit(true);
                            System.out.println(rows + " claim request(s) deleted.");

                        } else if (t.equals("3")) {
                            System.out.print("Item ID to delete: ");
                            int itemId = Integer.parseInt(sc.nextLine().trim());

                            // Child rows first. If a claim request still uses the item,
                            // the last delete fails and everything is rolled back.
                            conn.setAutoCommit(false);

                            PreparedStatement ps = conn.prepareStatement("DELETE FROM ItemPhoto WHERE item_id = ?");
                            ps.setInt(1, itemId);
                            ps.executeUpdate();
                            ps.close();

                            ps = conn.prepareStatement("DELETE FROM FoundItem WHERE item_id = ?");
                            ps.setInt(1, itemId);
                            ps.executeUpdate();
                            ps.close();

                            ps = conn.prepareStatement("DELETE FROM LostItem WHERE item_id = ?");
                            ps.setInt(1, itemId);
                            ps.executeUpdate();
                            ps.close();

                            ps = conn.prepareStatement("DELETE FROM Item WHERE item_id = ?");
                            ps.setInt(1, itemId);
                            int rows = ps.executeUpdate();
                            ps.close();

                            conn.commit();
                            conn.setAutoCommit(true);
                            System.out.println(rows + " item(s) deleted.");

                        } else if (t.equals("4")) {
                            System.out.print("Location ID to delete: ");
                            int locId = Integer.parseInt(sc.nextLine().trim());

                            PreparedStatement ps = conn.prepareStatement("DELETE FROM Location WHERE location_id = ?");
                            ps.setInt(1, locId);
                            int rows = ps.executeUpdate();
                            ps.close();
                            System.out.println(rows + " row(s) deleted.");

                        } else {
                            System.out.println("Invalid choice.");
                        }

                    // ======================= EXIT =======================
                    } else if (mainChoice.equals("5")) {
                        System.out.println("Goodbye!");
                        running = false;

                    } else {
                        System.out.println("Invalid choice. Please enter 1-5.");
                    }

                } catch (SQLException e) {
                    System.out.println("Error: " + e.getMessage());
                    try {
                        if (!conn.getAutoCommit()) {
                            conn.rollback();
                            System.out.println("Changes were rolled back.");
                        }
                    } catch (SQLException ex) {
                        System.out.println("Rollback failed: " + ex.getMessage());
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number. Please enter digits only.");
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid date. Use the format yyyy-MM-dd HH:mm:ss");
                }
            }

        } catch (SQLException e) {
            System.out.println("Connection failed!");
            System.out.println(e.getMessage());
        }

        sc.close();
    }
}