package com.BanjaraHotels.repository;

import com.BanjaraHotels.utilities.SendEmail;
import databaseConnection.GetConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
        // System.out.println("function called");
        // completed reviewing
        List<String[]> feedbackList = new ArrayList<>();

        String query = """
                    select f.FeedbackId, u.Username, f.OrderId, f.Rating, f.Comment
                    from Feedback f
                    inner join Orders o ON f.OrderId = o.OrderId
                    inner join Users u ON o.UserId = u.UserId
                    where f.Rating >= ?
                """;
        

        // only if sorting is true we will add this in our string.
        if (sorting) {
            query += " order by f.Rating " + (asc ? "asc" : "desc");
        }

        try (PreparedStatement pstmt = this.connection.prepareStatement(query)) {
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
        // completed reviewing
        System.out.println("function called for checking feedback");
        String query = """
                    select count(*) from Orders
                    where OrderId = ? AND Status = 'complete'
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }

        return false;
    }














    public void addFeedback(int orderId, int rating, String comment) throws SQLException {
        // completed reviewing
        String insertQuery = """
                    insert into Feedback (OrderId, Rating, Comment)
                    VALUES (?, ?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
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
        // completed reviewing
        String getItemsQuery = "select ItemId from OrderItems where OrderId = ?";

        try (PreparedStatement getItemsStmt = connection.prepareStatement(getItemsQuery)) {
            getItemsStmt.setInt(1, orderId);
            ResultSet rs = getItemsStmt.executeQuery();

            while (rs.next()) {
                int itemId = rs.getInt("ItemId");

                String selectItem = "select ReviewCount, AverageRating FROM Items where ItemId = ?";
                try (PreparedStatement selectStmt = connection.prepareStatement(selectItem)) {
                    selectStmt.setInt(1, itemId);
                    ResultSet itemRs = selectStmt.executeQuery();

                    if (itemRs.next()) {
                        int oldCount = itemRs.getInt("ReviewCount");
                        double oldAvg = itemRs.getDouble("AverageRating");

                        
                        int newCount = oldCount + 1;
                        double newAvg = ((oldAvg * oldCount) + newRating) / newCount;

                        String updateQuery = "update Items SET ReviewCount = ?, AverageRating = ? where ItemId = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, newCount);
                            updateStmt.setDouble(2, newAvg);
                            updateStmt.setInt(3, itemId);
                            updateStmt.executeUpdate();
                            connection.commit(); 
                        }
                    }
                }
            }
        }
    }















    public boolean addResponse(int feedbackId, String responseText) throws SQLException {
        // completed reviewing
        String insertQuery = """
                    insert into Response (FeedbackId, ResponseText)
                    values (?, ?)
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
        // completed reviewing
        List<String[]> responseList = new ArrayList<>();

        String query = """
                    select r.ResponseText, f.FeedbackId, f.OrderId, f.Comment, f.Rating
                    from Response r
                    join Feedback f ON r.FeedbackId = f.FeedbackId
                    join Orders o ON f.OrderId = o.OrderId
                    where o.UserId = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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

        // completed reviewing
        List<String[]> topRatedItems = new ArrayList<>();

        String query = """
                    select i.Name, i.AverageRating, i.ReviewCount, i.ItemId
                    from Items i
                    order by i.AverageRating desc
                    limit 5
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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

        // completed reviewing
        List<String[]> topRatedFeedback = new ArrayList<>();

        String query = """
                    select u.Username, f.Rating, f.Comment
                    from Feedback f
                    join Orders o ON f.OrderId = o.OrderId
                    join Users u ON o.UserId = u.UserId
                    order BY f.Rating desc
                    limit 2
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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
        // completed reviewing
        String emailQuery = """
                    select u.Email
                    from Feedback f
                    join Orders o ON f.OrderId = o.OrderId
                    join Users u ON o.UserId = u.UserId
                    where f.FeedbackId = ?
                """;
        String userEmail = null;
        try (PreparedStatement stmt = connection.prepareStatement(emailQuery)) {
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
                Thank you for your feedback.\n
                Here is our response:
                """ +
                response + "\n\n" +
                "Best Regards,\n" +
                "Banjara Hotels";
        System.out.println("Sending email to: " + userEmail);
        SendEmail.mail(userEmail, subject, body);
    }



























    public boolean isUserValid(int currentUser) {

        // completed reviewing
        // System.out.println("error in checking user function called");
        String query = "select count(*) from Users where UserId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, currentUser);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking user validity: " + e.getMessage());
        }
        // System.out.println("i am here");
        return false;
    }



















    public boolean isValidFeedbackId(int feedbackId) {
        // completed reviewing
        // System.out.println("function called");
        String query = "select count(*) from Feedback where FeedbackId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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























    public boolean isValidOrderId(int orderId, int userId) {
        // completed reviewing
        // Check if the order ID exists and belongs to the user
        String query = "select count(*) FROM Orders WHERE OrderId = ? AND UserId = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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






















    public List<Integer> getAllPendingOrders() {
        // completed reviewing
        List<Integer> pendingOrders = new ArrayList<>();
        String query = "select OrderId from Orders where Status = 'pending'";
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
        // completed reviewing
        
        String updateQuery = "update Orders set Status = 'complete' where OrderId = ? and status = 'pending'";
        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
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
