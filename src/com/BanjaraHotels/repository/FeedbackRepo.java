package com.BanjaraHotels.repository;

import com.BanjaraHotels.utilities.SendEmail;
import databaseConnection.GetConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FeedbackRepo {
    private final Connection connection;

    public FeedbackRepo() throws SQLException, IOException {
        this.connection = GetConnection.connectWithDatabase();
    }

    public List<String[]> getFeedbackWithUserAndOrder(boolean sorting, boolean asc, int filterRating)
            throws SQLException {
        List<String[]> feedbackList = new ArrayList<>();

        // Base query
        StringBuilder queryBuilder = new StringBuilder("""
                    SELECT f.FeedbackId, u.Username, f.OrderId, f.Rating, f.Comment
                    FROM Feedback f
                    JOIN Orders o ON f.OrderId = o.OrderId
                    JOIN Users u ON o.UserId = u.UserId
                    WHERE f.Rating >= ?
                """);

        // Sorting condition
        if (sorting) {
            queryBuilder.append(" ORDER BY f.Rating ");
            queryBuilder.append(asc ? "ASC" : "DESC");
        }

        String query = queryBuilder.toString();

        try (var pstmt = this.connection.prepareStatement(query)) {
            pstmt.setInt(1, filterRating);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String[] row = new String[5];
                row[0] = String.valueOf(rs.getInt("FeedbackId"));
                row[1] = rs.getString("Username");
                row[2] = String.valueOf(rs.getInt("OrderId"));
                row[3] = String.valueOf(rs.getInt("Rating"));
                row[4] = rs.getString("Comment");
                feedbackList.add(row);
            }
        }

        return feedbackList;
    }


    public boolean canAddFeedback(int orderId) throws SQLException {
        String query = """
            SELECT COUNT(*) FROM Orders
            WHERE OrderId = ? AND Status = 'complete'
        """;
    
        try (var stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
    
        return false;
    }

    public void addFeedback(int orderId, int rating, String comment) throws SQLException {
        String insertQuery = """
            INSERT INTO Feedback (OrderId, Rating, Comment)
            VALUES (?, ?, ?)
        """;
    
        try (var stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, rating);
    
            if (comment == null || comment.trim().isEmpty()) {
                stmt.setNull(3, java.sql.Types.VARCHAR);
            } else {
                stmt.setString(3, comment);
            }
    
            stmt.executeUpdate();
        }
    }

    public void updateItemRatingsAfterFeedback(int orderId, int newRating) throws SQLException {
        // Step 1: Get all item IDs from OrderItems for the given order
        String getItemsQuery = "SELECT ItemId FROM OrderItems WHERE OrderId = ?";
    
        try (var getItemsStmt = connection.prepareStatement(getItemsQuery)) {
            getItemsStmt.setInt(1, orderId);
            ResultSet rs = getItemsStmt.executeQuery();
    
            while (rs.next()) {
                int itemId = rs.getInt("ItemId");
    
                // Step 2: Get current review count and avg rating
                String selectItem = "SELECT ReviewCount, AverageRating FROM Items WHERE ItemId = ?";
                try (var selectStmt = connection.prepareStatement(selectItem)) {
                    selectStmt.setInt(1, itemId);
                    ResultSet itemRs = selectStmt.executeQuery();
    
                    if (itemRs.next()) {
                        int oldCount = itemRs.getInt("ReviewCount");
                        double oldAvg = itemRs.getDouble("AverageRating");
    
                        // Step 3: Compute new average
                        int newCount = oldCount + 1;
                        double newAvg = ((oldAvg * oldCount) + newRating) / newCount;
    
                        // Step 4: Update item
                        String updateQuery = "UPDATE Items SET ReviewCount = ?, AverageRating = ? WHERE ItemId = ?";
                        try (var updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, newCount);
                            updateStmt.setDouble(2, newAvg);
                            updateStmt.setInt(3, itemId);
                            updateStmt.executeUpdate();
                            connection.commit(); // Commit the transaction
                        }
                    }
                }
            }
        }
    }


    public boolean addResponse(int feedbackId, String responseText) throws SQLException {
        // Step 1: Check if feedback exists
        String checkQuery = "SELECT COUNT(*) FROM Feedback WHERE FeedbackId = ?";
        try (var checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, feedbackId);
            try (var rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    // Feedback does not exist
                    return false;
                }
            }
        }
    
        // Step 2: Insert the response
        String insertQuery = """
            INSERT INTO Response (FeedbackId, ResponseText)
            VALUES (?, ?)
        """;
        try (var stmt = connection.prepareStatement(insertQuery)) {
            stmt.setInt(1, feedbackId);
            stmt.setString(2, responseText);
            stmt.executeUpdate();
            connection.commit(); // Commit the transaction
            return true;
        }
    }


    public List<String[]> getResponsesForUser(int userId) throws SQLException {
        List<String[]> responseList = new ArrayList<>();
    
        String query = """
            SELECT r.ResponseText, f.FeedbackId, f.OrderId, f.Comment, f.Rating
            FROM Response r
            JOIN Feedback f ON r.FeedbackId = f.FeedbackId
            JOIN Orders o ON f.OrderId = o.OrderId
            WHERE o.UserId = ?
        """;
    
        try (var stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String[] row = new String[5];
                    row[0] = rs.getString("ResponseText");
                    row[1] = String.valueOf(rs.getInt("FeedbackId"));
                    row[2] = String.valueOf(rs.getInt("OrderId"));
                    row[3] = rs.getString("Comment");
                    row[4] = String.valueOf(rs.getInt("Rating"));
                    responseList.add(row);
                }
            }
        }
    
        return responseList;
    }



    public List<String[]> getTopRatedItems() throws SQLException {
        List<String[]> topRatedItems = new ArrayList<>();
    
        String query = """
            SELECT i.Name, i.AverageRating, i.ReviewCount, i.ItemId
            FROM Items i
            ORDER BY i.AverageRating DESC
            LIMIT 5
        """;
    
        try (var stmt = connection.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String[] row = new String[4];
                    row[0] = rs.getString("Name");
                    row[1] = String.valueOf(rs.getDouble("AverageRating"));
                    row[2] = String.valueOf(rs.getInt("ReviewCount"));
                    row[3] = String.valueOf(rs.getInt("ItemId"));
                    topRatedItems.add(row);
                }
            }
        }
    
        return topRatedItems;
    }

    public List<String[]> getTopRatedFeedback() throws SQLException {
        List<String[]> topRatedFeedback = new ArrayList<>();
    
        String query = """
            SELECT u.Username, f.Rating, f.Comment
            FROM Feedback f
            JOIN Orders o ON f.OrderId = o.OrderId
            JOIN Users u ON o.UserId = u.UserId
            ORDER BY f.Rating DESC
            LIMIT 2
        """;
    
        try (var stmt = connection.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String[] row = new String[3];
                    row[0] = rs.getString("Username");
                    row[1] = String.valueOf(rs.getInt("Rating"));
                    row[2] = rs.getString("Comment");
                    topRatedFeedback.add(row);
                }
            }
        }
    
        return topRatedFeedback;
    }

    public void sendResponseEmail(int feedbackId, String response) {
        // we will get the email of the user 
        String emailQuery = """
            SELECT u.Email
            FROM Feedback f
            JOIN Orders o ON f.OrderId = o.OrderId
            JOIN Users u ON o.UserId = u.UserId
            WHERE f.FeedbackId = ?
        """;
        String userEmail = null;
        try (var stmt = connection.prepareStatement(emailQuery)) {
            stmt.setInt(1, feedbackId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userEmail = rs.getString("Email");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching user email: " + e.getMessage());
        }
        if (userEmail == null) {
            System.out.println("No user found for the given feedback ID.");
            return;
        }
        // Send email
        String subject = "Response to Your Feedback";
        String body = """
                      Dear User,
                      Thank you for your feedback. Here is our response:
                      """ +
                      response + "\n\n" +
                      "Best Regards,\n" +
                      "Banjara Hotels";
        // Use the email sending utility
        System.out.println("Sending email to: " + userEmail);
        SendEmail.mail(userEmail, subject, body);
    }

    public boolean isUserValid(int currentUser) {
        String query = "SELECT COUNT(*) FROM Users WHERE UserId = ?";
        try (var stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking user validity: " + e.getMessage());
        }
        return false;
    }

    public boolean isValidFeedbackId(int feedbackId) {
        String query = "SELECT COUNT(*) FROM Feedback WHERE FeedbackId = ?";
        try (var stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, feedbackId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking feedback ID validity: " + e.getMessage());
        }
        return false;
    }

    public boolean isValidOrderId(int orderId,int userId) {
        // Check if the order ID exists and belongs to the user
        String query = "SELECT COUNT(*) FROM Orders WHERE OrderId = ? AND UserId = ?";
        try (var stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking order ID validity: " + e.getMessage());
        }
        return false;
    }

    public List<Integer> getAllPendingOrders(){
        List<Integer> pendingOrders = new ArrayList<>();
        String query = "SELECT OrderId FROM Orders WHERE Status = 'pending'";
        try (var stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                pendingOrders.add(rs.getInt("OrderId"));
            }
        } catch (SQLException e) {
            System.err.println("Error fetching pending orders: " + e.getMessage());
        }
        return pendingOrders;
    }

    public boolean changeOrderStatus(int orderId) {
        String updateQuery = "UPDATE Orders SET Status = 'complete' WHERE OrderId = ?";
        try (var stmt = connection.prepareStatement(updateQuery)) {
            stmt.setInt(1, orderId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                connection.commit(); // Commit the transaction
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error updating order status: " + e.getMessage());
        }
        return false;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
