package com.BanjaraHotels.service;

import com.BanjaraHotels.repository.FeedbackRepo;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class FeedbackService {
    private final FeedbackRepo feedbackRepo;

    public FeedbackService() throws SQLException, IOException {
        this.feedbackRepo = new FeedbackRepo();
    }

    public void showFeedback(boolean sorting, boolean asc, int filterRating) {
        List<String[]> feedbackList;
        try {
            feedbackList = feedbackRepo.getFeedbackWithUserAndOrder(sorting, asc, filterRating);
            if (feedbackList.isEmpty()) {
                System.out.println("No feedback found.");
                return;
            }
            for (String[] feedback : feedbackList) {
                System.out.println("Feedback ID: " + feedback[0]);
                System.out.println("Username: " + feedback[1]);
                System.out.println("Order ID: " + feedback[2]);
                System.out.println("Rating: " + feedback[3]);
                System.out.println("Comment: " + feedback[4]);
                System.out.println("-----------------------------");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching feedback: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }

    }

    public boolean canAddFeedback(int orderId) {
        try {
            return feedbackRepo.canAddFeedback(orderId);
        } catch (SQLException e) {
            System.err.println("Error checking feedback: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            return false;
        }
    }

    public void addFeedback(int orderId, int rating, String comment)  {

        try {
            feedbackRepo.addFeedback(orderId, rating, comment);
        } catch (SQLException e) {
            System.err.println("Error adding feedback: " + e.getMessage());
            return;
        } 
        try {
            feedbackRepo.updateItemRatingsAfterFeedback(orderId, rating);
        } catch (SQLException e) {
            System.err.println("Error updating item ratings: " + e.getMessage());
            return;
        } 
        System.out.println("Feedback added successfully.");

    }

    public void addResponse(int feedbackId, String response) {
        boolean b;
        try {
            b = feedbackRepo.addResponse(feedbackId, response);
            if (b) {
                System.out.println("Response added successfully.");
                feedbackRepo.sendResponseEmail(feedbackId, response);
            } else {
                System.out.println("Failed to add response.");
            }
        } catch (SQLException e) {
            System.err.println("Error adding response: " + e.getMessage());
        }

    }

    public void showResponsesForUser(int userId) {
        try {
            List<String[]> responses = feedbackRepo.getResponsesForUser(userId);

            if (responses.isEmpty()) {
                System.out.println("No responses found for this user.");
            } else {
                System.out.println("Responses for User ID: " + userId);

                // Loop through each response
                for (String[] response : responses) {
                    System.out.println("Feedback ID: " + response[1]);
                    System.out.println("Order ID: " + response[2]);
                    System.out.println("Rating: " + response[4]);
                    System.out.println("Comment: " + response[3]);
                    System.out.println("Response: " + response[0]);
                    System.out.println("-------------------------------");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error fetching responses: " + e.getMessage());
        }
    }

    public void showForNewUser() {
        showTopRatedItems();
        showTopFeedbacks();
    }

    public void showTopRatedItems() {
        try {
            List<String[]> topRatedItems = feedbackRepo.getTopRatedItems();

            if (topRatedItems.isEmpty()) {
                System.out.println("No items found.");
            } else {
                System.out.println("Top 5 Rated Items:");

                for (String[] item : topRatedItems) {
                    System.out.println("Item Name: " + item[0]);
                    System.out.println("Average Rating: " + item[1]);
                    System.out.println("Review Count: " + item[2]);
                    System.out.println("-------------------------------");
                }
                this.showTopFeedbacks();
            }
        } catch (SQLException e) {
            System.err.println("Error fetching top rated items: " + e.getMessage());
        }
    }

    public void showTopFeedbacks() {
        try {
            // Get Top 2 Rated Feedbacks
            List<String[]> topRatedFeedback = feedbackRepo.getTopRatedFeedback();
            System.out.println();
            System.out.println();
            System.out.println("Top 2 Rated Feedbacks:");

            System.out.println("-------------------------------");
            if (topRatedFeedback.isEmpty()) {
                System.out.println("No feedback found.");
            } else {
                for (String[] feedback : topRatedFeedback) {
                    System.out.println("Username " + feedback[0]);
                    System.out.println("Rating " + feedback[1]);
                    System.out.println("comment " + feedback[2]);
                    System.out.println("-------------------------------");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error fetching top rated feedback: " + e.getMessage());
        }
    }

    public boolean isUserValid(int currentUser) {
        return feedbackRepo.isUserValid(currentUser);
    }

    public boolean isValidFeedbackId(int feedbackId) {

        return feedbackRepo.isValidFeedbackId(feedbackId);
    }

    public boolean isValidOrderId(int orderId, int userId) {
        return feedbackRepo.isValidOrderId(orderId, userId);
    }

    public boolean showAllPendingOrders() {
        List<Integer> pendingOrders = feedbackRepo.getAllPendingOrders();
        if (pendingOrders.isEmpty()) {
            System.out.println("No pending orders found.");
            return false;
        } else {
            System.out.println("Pending Orders:");
            for (int orderId : pendingOrders) {
                System.out.println("Order ID: " + orderId);
            }
            return true;
        }
    }

    public void changeOrderStatus(int orderId) {
        boolean success = feedbackRepo.changeOrderStatus(orderId);
        if (success) {
            System.out.println("Order status updated successfully.");
        } else {
            System.out.println("Failed to update order status.");
        }
    }

    public void closeConnection() {
        feedbackRepo.closeConnection();
    }
}
