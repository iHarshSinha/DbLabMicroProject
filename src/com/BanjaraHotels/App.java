package com.BanjaraHotels;

import com.BanjaraHotels.service.FeedbackService;
import com.BanjaraHotels.utilities.WhiteSpace;
import java.io.IOException;
import static java.lang.System.exit;
import java.sql.SQLException;
import java.util.Scanner;

public class App {
    private static int currentUser = 0;

    private static FeedbackService feedbackService;

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws SQLException, IOException {

        App.feedbackService = new FeedbackService();
        while (true) {
            App.login();

            if (currentUser == 1) {
                App.commandsForShopkeeper();
            } else {
                App.commandForUser();
            }
        }

    }









    private static void commandForUser() {
        while (true) {
            System.out.println("\nAvailable commands:");
            System.out.println("1. add feedback");
            System.out.println("2. view responses");
            System.out.println("3. logout");
            WhiteSpace.blankLine();
            System.out.print("Enter Command: ");

            String command = App.scanner.nextLine().trim().toLowerCase();
  
            switch (command) {
                case "add feedback" -> {
                    WhiteSpace.blankLine();
                    System.out.print("Enter Order ID: ");
                    int orderId;
                    try {
                        orderId = Integer.parseInt(App.scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Order ID. Please enter a valid number.\n");
                        break;
                    }

                    if (!feedbackService.isValidOrderId(orderId, currentUser)) {
                        System.out.println("Order ID does not exist or does not belong to you.\n");
                        break;
                    }
                    if (!feedbackService.canAddFeedback(orderId)) {
                        System.out.println("Cannot add feedback for this order because it is not completed yet.\n");
                        break;
                    }

                    System.out.print("Enter Rating (1-5): ");
                    int rating;
                    try {
                        rating = Integer.parseInt(App.scanner.nextLine().trim());
                        if (rating < 1 || rating > 5) {
                            System.out.println("Rating must be between 1 and 5.\n");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Rating. Please enter a valid number between 1 and 5.\n");
                        break;
                    }

                    System.out.print("Enter Comment (optional): ");
                    String comment = App.scanner.nextLine().trim();

                    feedbackService.addFeedback(orderId, rating, comment);
                }
                case "logout" -> {
                    currentUser = 0;
                    System.out.println("Logged out successfully.\n");
                    return;
                }

                case "view responses" -> {
                    WhiteSpace.blankLine();
                    feedbackService.showResponsesForUser(currentUser);
                }
                default -> System.out.println("Unknown command. Available: add feedback, logout\n");
            }
        }
    }
















    private static void login() {
        WhiteSpace.blankLine();
        System.out.println("  Welcome to Banjara Hotels Feedback Management System");
        System.out.println("  Enter commands listed below");
        System.out.println("  Type 'exit' to close the application");
        System.out.println("  Type 'view top' to view top rated items");
        WhiteSpace.blankLine();
        while (currentUser == 0) {
            System.out.print(
                    "Enter your User ID to login (or type 'exit' to quit or type 'view top' to view top rated items): ");
            String input = App.scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                try (App.scanner) {
                    System.out.println("Goodbye from Banjara Hotels Feedback Desk!");

                }
                feedbackService.closeConnection();
                exit(0);
            } else if (input.equalsIgnoreCase("view top")) {
                feedbackService.showForNewUser();
            } else {
                try {
                    currentUser = Integer.parseInt(input);
                    if (currentUser < 1) {
                        System.out.println("Invalid User ID. Please enter a positive integer.");
                        currentUser = 0;
                    } else {
                        if (App.feedbackService.isUserValid(currentUser)) {
                            WhiteSpace.blankLine();
                            System.out.println("User ID " + currentUser + " logged in successfully.");
                            if (currentUser == 1) {
                                System.out.println("Welcome Shopkeeper");
                            } else {
                                System.out.println("Welcome User");
                            }
                        } else {
                            System.out.println("Invalid User ID. Please enter a valid User ID.");
                            currentUser = 0;
                        }
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid User ID.");
                }
            }
        }
    }















    public static int getCurrentUser() {
        return currentUser;
    }
























    private static void commandsForShopkeeper() {
        while (true) {
            System.out.println("\nAvailable commands:");
            System.out.println("1. show feedback");
            System.out.println("2. add response");
            System.out.println("3. change order status");
            System.out.println("4. logout");
            WhiteSpace.blankLine();
            System.out.print("Enter Command: ");

            String command = App.scanner.nextLine().trim().toLowerCase();

            switch (command) {
                case "show feedback" -> {
                    WhiteSpace.blankLine();
                    System.out.print("Do you want sorting? (yes/no): Press Enter for default (no): ");
                    String sortInput = App.scanner.nextLine().trim().toLowerCase();
                    // if it is other than yes or no, we will go to next iteration
                    if (!sortInput.equals("yes") && !sortInput.equals("no") && !sortInput.isEmpty()) {
                        WhiteSpace.blankLine();
                        System.out.println("Invalid input.");
                        WhiteSpace.blankLine();
                        continue;
                    }
                    boolean sorting = sortInput.equals("yes");

                    boolean asc = true;
                    if (sorting) {
                        System.out.print("Ascending or descending? (asc/desc): Press Enter for default (asc): ");
                        // if it is other than asc or desc, we will go to next iteration

                        String ascInput = App.scanner.nextLine().trim().toLowerCase();
                        if (!ascInput.equals("asc") && !ascInput.equals("desc") && !ascInput.isEmpty()) {
                            WhiteSpace.blankLine();
                            System.out.println("Invalid input.");
                            WhiteSpace.blankLine();
                            continue;
                        }
                        asc = ascInput.equals("asc");
                    }

                    System.out.print("Enter minimum rating (or press Enter for 0): ");
                    String minRatingInput = App.scanner.nextLine().trim();
                    int minRating = 0;
                    if (!minRatingInput.isEmpty()) {
                        try {
                            minRating = Integer.parseInt(minRatingInput);
                            // rating should be between 0 and 5. if it is not the case then we will go to the next iteration
                            if (minRating < 0 || minRating > 5) {
                                WhiteSpace.blankLine();
                                System.out.println("Invalid rating. Rating should be between 0 and 5.");
                                WhiteSpace.blankLine();
                                continue;
                            }

                        } catch (NumberFormatException e) {
                            WhiteSpace.blankLine();
                            System.out.println("Invalid rating input. It must be a number");
                            WhiteSpace.blankLine();
                            continue;
                        }
                    }

                    feedbackService.showFeedback(sorting, asc, minRating);
                }
                case "add response" -> {
                    WhiteSpace.blankLine();
                    System.out.print("Enter Feedback ID: ");
                    int feedbackId;
                    try {
                        feedbackId = Integer.parseInt(App.scanner.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Feedback ID. Please enter a valid number.\n");
                        break;
                    }

                    if (!feedbackService.isValidFeedbackId(feedbackId)) {
                        System.out.println("Feedback ID does not exist. Please try again.\n");
                        break;
                    }

                    System.out.print("Enter Response (Note: It cannot be empty): ");
                    String responseText = App.scanner.nextLine().trim();

                    if (responseText.isEmpty()) {
                        System.out.println("Response text cannot be empty.\n");
                        break;
                    }

                    feedbackService.addResponse(feedbackId, responseText);
                }

                case "change order status" -> {
                    WhiteSpace.blankLine();
                    boolean b = feedbackService.showAllPendingOrders();
                    if (!b) {
                        break;
                    } else {
                        System.out.print("Enter Order ID to mark as complete: ");
                        String input = App.scanner.nextLine().trim();
                        try {
                            int orderId = Integer.parseInt(input);
                            feedbackService.changeOrderStatus(orderId);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Order ID. Please enter a number.");
                        }
                    }

                }
                case "logout" -> {
                    currentUser = 0;
                    WhiteSpace.blankLine();
                    System.out.println("Logged out successfully.\n");
                    return;
                }
                default -> System.out.println("Unknown command. Available: show feedback, add response, logout\n");
            }
        }
    }
}
