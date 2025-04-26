-- Users Table
CREATE TABLE Users (
    UserId INT PRIMARY KEY AUTO_INCREMENT,
    Username VARCHAR(100) NOT NULL,
    Email VARCHAR(100) UNIQUE NOT NULL,
    Role ENUM('customer', 'shopkeeper') NOT NULL
);

-- Orders Table
CREATE TABLE Orders (
    OrderId INT PRIMARY KEY AUTO_INCREMENT,
    UserId INT NOT NULL,
    Status ENUM('pending', 'complete') NOT NULL
);

-- Feedback Table
CREATE TABLE Feedback (
    FeedbackId INT PRIMARY KEY AUTO_INCREMENT,
    OrderId INT NOT NULL,
    Rating INT CHECK (Rating >= 1 AND Rating <= 5),
    Comment TEXT
);

-- Response Table
CREATE TABLE Response (
    ResponseId INT PRIMARY KEY AUTO_INCREMENT,
    FeedbackId INT NOT NULL,
    ResponseText TEXT NOT NULL
);

-- Items Table with 
CREATE TABLE Items (
    ItemId INT PRIMARY KEY AUTO_INCREMENT,
    Name VARCHAR(100) NOT NULL,
    Description TEXT,
    Price DECIMAL(10,2) NOT NULL,
    Category VARCHAR(50),
    ReviewCount INT DEFAULT 0,           -- Number of reviews
    AverageRating DECIMAL(3,2) DEFAULT 0.00  -- Average rating of the item
);

-- Create OrderItems table
CREATE TABLE OrderItems (
    OrderItemId INT PRIMARY KEY AUTO_INCREMENT,
    OrderId INT NOT NULL,
    ItemId INT NOT NULL,
    Quantity INT NOT NULL
);