-- Insert Shopkeeper
INSERT INTO Users (Username, Email, Role)
VALUES
    ('Banjara Restaurant', 'banjararestaurantpatna@gmail.com', 'shopkeeper');


-- Insert Customers
INSERT INTO Users (Username, Email, Role)
VALUES
    ('Ayush Mishra', 'mishraayush0018@gmail.com', 'customer'),
    ('Harsh Sinha', 'harshsinhaed@gmail.com', 'customer');


-- Insert pending orders
INSERT INTO Orders (UserId, Status)
VALUES
    (2, 'pending'), -- Ayush
    (2, 'pending'), -- Ayush
    (3, 'pending'); -- Harsh


-- Insert completed orders
INSERT INTO Orders (UserId, Status)
VALUES
    (2, 'complete'),  -- Ayush
    (3, 'complete'),  -- Harsh
    (3, 'complete');  -- Harsh

-- Insert feedback
INSERT INTO Feedback (OrderId, Rating, Comment)
VALUES
    (4, 5, 'Loved the food!'),
    (5, 4, 'Tasty, but delivery was slow.');

-- Insert items into Items table with ReviewCount and AverageRating
INSERT INTO Items (Name, Description, Price, Category, ReviewCount, AverageRating)
VALUES
    ('Paneer Butter Masala', 'Soft cubes of paneer in rich tomato gravy.', 299.00, 'Main Course', 1, 5.00),
    ('Chicken Tikka', 'Grilled chicken marinated with spices.', 249.00, 'Starter', 0, 0.00),
    ('Aloo Gobi', 'A flavorful dry curry made with potatoes and cauliflower.', 179.00, 'Main Course', 1, 5.00),
    ('Mutton Korma', 'Mutton pieces cooked in rich, creamy, and mildly spiced gravy.', 349.00, 'Main Course', 0, 0.00),
    ('Butter Naan', 'Soft, buttery flatbread.', 59.00, 'Bread', 2, 4.50),  -- reviewed in both Order 4 & 5
    ('Tandoori Roti', 'Whole wheat flatbread cooked in a tandoor.', 49.00, 'Bread', 0, 0.00),
    ('Chole Bhature', 'Deep-fried bread served with spicy chickpeas.', 189.00, 'Main Course', 1, 4.00),
    ('Samosa', 'Crispy pastry filled with spiced potatoes and peas.', 99.00, 'Starter', 1, 5.00),
    ('Veg Biryani', 'Fragrant rice mixed with vegetables and spices.', 229.00, 'Main Course', 1, 4.00),
    ('Lassi', 'Sweet or salty yogurt drink, refreshing and cool.', 79.00, 'Beverage', 0, 0.00),
    ('Gulab Jamun', 'Deep-fried dough balls soaked in sugar syrup.', 89.00, 'Dessert', 1, 4.00),
    ('Rogan Josh', 'Kashmiri lamb curry cooked with aromatic spices.', 389.00, 'Main Course', 0, 0.00),
    ('Malai Kofta', 'Soft dumplings in rich, creamy tomato gravy.', 269.00, 'Main Course', 0, 0.00),
    ('Mango Lassi', 'A sweet, tangy yogurt drink made with mangoes.', 99.00, 'Beverage', 0, 0.00),
    ('Gajar Halwa', 'A warm dessert made with grated carrots, milk, and sugar.', 129.00, 'Dessert', 0, 0.00);

-- Insert items for orders 1 to 5
INSERT INTO OrderItems (OrderId, ItemId, Quantity)
VALUES
    (1, 1, 2),  -- Order 1 (Ayush): 2 Paneer Butter Masala
    (1, 2, 1),  -- Order 1 (Ayush): 1 Chicken Tikka
    (2, 3, 3),  -- Order 2 (Ayush): 3 Aloo Gobi
    (2, 5, 2),  -- Order 2 (Ayush): 2 Butter Naan
    (3, 6, 1),  -- Order 3 (Harsh): 1 Tandoori Roti
    (3, 4, 2),  -- Order 3 (Harsh): 2 Mutton Korma

    -- Completed Orders
    (4, 1, 1),  -- Paneer Butter Masala
    (4, 3, 2),  -- Aloo Gobi
    (4, 8, 1),  -- Samosa
    (4, 5, 1),  -- Butter Naan ← included

    (5, 7, 2),  -- Chole Bhature
    (5, 9, 1),  -- Veg Biryani
    (5, 11, 2), -- Gulab Jamun
    (5, 5, 1);  -- Butter Naan ← also included here

