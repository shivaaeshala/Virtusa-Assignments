CREATE DATABASE IF NOT EXISTS online_retail_db;
USE online_retail_db;

CREATE TABLE Customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    city VARCHAR(100) NOT NULL
);

CREATE TABLE Products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price > 0)
);

CREATE TABLE Orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    order_date DATE NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES Customers(customer_id)
);

CREATE TABLE Order_Items (
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL CHECK (quantity > 0),
    PRIMARY KEY (order_id, product_id),
    FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    FOREIGN KEY (product_id) REFERENCES Products(product_id)
);

INSERT INTO Customers (name, city) VALUES
('Aarav Sharma', 'Hyderabad'),
('Priya Iyer', 'Chennai'),
('Rohit Verma', 'Delhi'),
('Neha Gupta', 'Pune'),
('Karan Singh', 'Bangalore');

INSERT INTO Products (name, category, price) VALUES
('Laptop', 'Electronics', 55000),
('Mouse', 'Electronics', 800),
('Desk Chair', 'Furniture', 4500),
('Notebook', 'Stationery', 60),
('Pen Set', 'Stationery', 120),
('Smartphone', 'Electronics', 25000),
('Office Table', 'Furniture', 8000);

INSERT INTO Orders (customer_id, order_date) VALUES
(1, '2025-01-10'),
(2, '2025-01-15'),
(1, '2025-02-05'),
(3, '2025-02-18'),
(4, '2025-03-12'),
(2, '2025-03-20'),
(5, '2025-04-01');

INSERT INTO Order_Items (order_id, product_id, quantity) VALUES
(1, 1, 1),
(1, 2, 2),
(2, 4, 5),
(2, 5, 3),
(3, 6, 1),
(3, 2, 1),
(4, 3, 2),
(4, 4, 10),
(5, 7, 1),
(5, 5, 4),
(6, 1, 1),
(6, 4, 2),
(7, 6, 1),
(7, 2, 3);