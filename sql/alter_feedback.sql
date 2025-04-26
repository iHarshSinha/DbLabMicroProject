-- Foreign key: Orders.UserId → Users.UserId
ALTER TABLE Orders
ADD CONSTRAINT fk_orders_user
FOREIGN KEY (UserId) REFERENCES Users(UserId);

-- Foreign key: Feedback.OrderId → Orders.OrderId
ALTER TABLE Feedback
ADD CONSTRAINT fk_feedback_order
FOREIGN KEY (OrderId) REFERENCES Orders(OrderId);

-- Foreign key: Response.FeedbackId → Feedback.FeedbackId
ALTER TABLE Response
ADD CONSTRAINT fk_response_feedback
FOREIGN KEY (FeedbackId) REFERENCES Feedback(FeedbackId);


ALTER TABLE OrderItems
ADD CONSTRAINT fk_orderitems_order
FOREIGN KEY (OrderId) REFERENCES Orders(OrderId);

ALTER TABLE OrderItems
ADD CONSTRAINT fk_orderitems_item
FOREIGN KEY (ItemId) REFERENCES Items(ItemId);