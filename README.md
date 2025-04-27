# Banjara Hotels Feedback Management System

A JDBC and MySQL-based application for managing customer feedback, responses, and order tracking with a multi-layered architecture.

## Overview

This Feedback Management System provides a platform for customers to submit feedback for their orders and for shopkeepers to respond to that feedback. The system follows a three-tier architecture:
- **Main Layer**: Handles user interaction and command routing
- **Service Layer**: Contains business logic and validation
- **Repository Layer**: Manages database communication through JDBC

## Project Structure

```
FEEDBACKRESTAURENT/
├── .vscode/          # VS Code configuration files
├── bin/              # Compiled Java classes
├── lib/              # External libraries
│   ├── mysql-connector-j-8.0.32.jar
│   ├── javax.mail.jar
│   └── activation-1.1.1.jar
├── sql/              # SQL scripts for database setup
├── src/              # Source code
│   └── com/
│       └── BanjaraHotels/
│           ├── repository/
│           │   └── FeedbackRepo.java     # Database operations
│           ├── service/
│           │   └── FeedbackService.java  # Business logic
│           ├── utilities/
│           │   └── SendEmail.java        # Email utility
│           ├── databaseConnection/
│           │   └── GetConnection.java    # Database connection management
│           └── App.java                  # Main application entry point
├── db.properties     # Database configuration
└── README.md         # This file
```

## Features

### For Customers
- Submit feedback for completed orders
- Rate products/services
- View responses from shopkeepers
- View top-rated items and feedback

### For Shopkeepers
- View all customer feedback with sorting options
- Respond to customer feedback
- Send email notifications when responding to feedback
- Change order status (pending to completed)
- View pending orders

## Architecture

### Main Layer (App.java)
- Provides the user interface through console
- Handles command routing based on user type
- Manages user authentication and session
- Calls appropriate Service Layer methods

### Service Layer (FeedbackService.java)
- Contains all business logic
- Validates user inputs
- Handles exceptions from the Repository Layer
- Formats data for display
- Orchestrates operations between Main and Repository layers

### Repository Layer (FeedbackRepo.java)
- Manages all database communication via JDBC
- Executes SQL queries
- Fetches and returns data to the Service Layer
- Handles database-specific exceptions

### Utilities
- SendEmail.java: Handles email notification functionality

### Database Connection
- GetConnection.java: Manages database connections
- db.properties: Stores database configuration parameters

## Technical Implementation

### Database
- MySQL database for persistent storage
- Tables for users, orders, feedback, responses, and items
- SQL scripts available in the sql/ directory

### Authentication
- Simple user authentication based on user ID
- Different access levels for shopkeepers (ID: 1) and customers

### Error Handling
- Exception handling at appropriate layers
- Input validation at multiple levels

## Getting Started

### Prerequisites
- Java JDK 8 or higher
- MySQL Server
- Required JAR libraries (included in lib/ folder):
  - mysql-connector-j-8.0.32.jar
  - javax.mail.jar
  - activation-1.1.1.jar

### Installation
1. Clone the repository
2. Configure database connection in `db.properties`
3. Run the SQL scripts from the `sql/` directory to set up your database
4. Compile and run the application using the commands below

### Building and Running

```bash
# Set the classpath to include necessary libraries
export CLASSPATH="bin:lib/mysql-connector-j-8.0.32.jar:lib/javax.mail.jar:lib/activation-1.1.1.jar"

# Compile the Java source files
javac -d bin $(find src -name "*.java")

# Run the application
java com.BanjaraHotels.App
```

## Usage

### Login Options:
- Enter user ID to log in
- Enter "exit" to close the application
- Enter "viewTop" to see top-rated items and feedback without logging in

### Shopkeeper Commands (User ID: 1)
- `Show feedback` - View all feedback with sorting options
- `Add response` - Respond to customer feedback
- `Change order Status` - Update status of pending orders
- `Logout` - Exit shopkeeper mode

### Customer Commands
- `Add feedback` - Submit feedback for a completed order
- `Show responses` - View all responses to your feedback
- `Logout` - Exit customer mode

## Configuration

The `db.properties` file contains the database connection settings:

```properties
# Example db.properties
db.url=jdbc:mysql://localhost:3306/banjarahotels
db.user=username
db.password=password
```

## Future Enhancements
- Implement a graphical user interface
- Add more robust authentication
- Expand reporting capabilities
- Implement product categories and filtering
- Add transaction management for database operations