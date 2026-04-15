CREATE DATABASE IF NOT EXISTS swiftship_db;
USE swiftship_db;

CREATE TABLE Partners (
    partner_id INT PRIMARY KEY AUTO_INCREMENT,
    partner_name VARCHAR(100) NOT NULL,
    contact_person VARCHAR(100),
    phone VARCHAR(20)
);

CREATE TABLE Shipments (
    shipment_id INT PRIMARY KEY AUTO_INCREMENT,
    partner_id INT NOT NULL,
    customer_name VARCHAR(100) NOT NULL,
    destination_city VARCHAR(100) NOT NULL,
    zone VARCHAR(50),
    promised_date DATE NOT NULL,
    actual_delivery_date DATE,
    status VARCHAR(20) NOT NULL,
    FOREIGN KEY (partner_id) REFERENCES Partners(partner_id)
);

CREATE TABLE DeliveryLogs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    shipment_id INT NOT NULL,
    log_date DATE NOT NULL,
    event_status VARCHAR(30) NOT NULL,
    remarks VARCHAR(255),
    FOREIGN KEY (shipment_id) REFERENCES Shipments(shipment_id)
);

INSERT INTO Partners (partner_name, contact_person, phone) VALUES
('FastTrack Express', 'Amit Sharma', '9876543210'),
('QuickMove Logistics', 'Neha Verma', '9876543211'),
('SafeShip Couriers', 'Rahul Mehta', '9876543212');

INSERT INTO Shipments (partner_id, customer_name, destination_city, zone, promised_date, actual_delivery_date, status) VALUES
(1, 'Ravi Kumar', 'Hyderabad', 'South', '2026-04-01', '2026-04-02', 'Delivered'),
(1, 'Ananya Rao', 'Chennai', 'South', '2026-04-03', '2026-04-05', 'Delivered'),
(2, 'Vikram Singh', 'Delhi', 'North', '2026-04-01', NULL, 'In Transit'),
(2, 'Pooja Jain', 'Mumbai', 'West', '2026-03-28', '2026-04-01', 'Returned'),
(3, 'Kiran Patel', 'Bangalore', 'South', '2026-03-25', '2026-03-26', 'Delivered'),
(3, 'Meera Nair', 'Hyderabad', 'South', '2026-04-05', '2026-04-08', 'Delivered'),
(1, 'Arjun Das', 'Mumbai', 'West', '2026-04-10', '2026-04-11', 'Delivered'),
(2, 'Sneha Kapoor', 'Delhi', 'North', '2026-04-09', '2026-04-12', 'Delivered');

INSERT INTO DeliveryLogs (shipment_id, log_date, event_status, remarks) VALUES
(1, '2026-04-01', 'Picked Up', 'Shipment collected from warehouse'),
(1, '2026-04-02', 'Delivered', 'Delivered to customer'),
(2, '2026-04-03', 'Picked Up', 'Shipment collected'),
(2, '2026-04-05', 'Delivered', 'Delivered late'),
(4, '2026-03-28', 'Picked Up', 'Collected'),
(4, '2026-04-01', 'Returned', 'Customer unavailable'),
(5, '2026-03-25', 'Delivered', 'On time delivery'),
(6, '2026-04-05', 'Picked Up', 'Collected'),
(6, '2026-04-08', 'Delivered', 'Delivered after delay'),
(7, '2026-04-10', 'Picked Up', 'Collected'),
(7, '2026-04-11', 'Delivered', 'Delivered successfully'),
(8, '2026-04-09', 'Picked Up', 'Collected'),
(8, '2026-04-12', 'Delivered', 'Delivered successfully');

SELECT
    s.shipment_id,
    p.partner_name,
    s.customer_name,
    s.destination_city,
    s.promised_date,
    s.actual_delivery_date,
    DATEDIFF(s.actual_delivery_date, s.promised_date) AS delay_days
FROM Shipments s
JOIN Partners p ON s.partner_id = p.partner_id
WHERE s.actual_delivery_date IS NOT NULL
  AND s.actual_delivery_date > s.promised_date
ORDER BY delay_days DESC;

SELECT
    p.partner_id,
    p.partner_name,
    SUM(CASE WHEN s.status = 'Delivered' THEN 1 ELSE 0 END) AS successful_deliveries,
    SUM(CASE WHEN s.status = 'Returned' THEN 1 ELSE 0 END) AS returned_deliveries,
    COUNT(s.shipment_id) AS total_shipments,
    ROUND(
        (SUM(CASE WHEN s.status = 'Delivered' THEN 1 ELSE 0 END) * 100.0) / COUNT(s.shipment_id),
        2
    ) AS success_rate_percent
FROM Partners p
LEFT JOIN Shipments s ON p.partner_id = s.partner_id
GROUP BY p.partner_id, p.partner_name
ORDER BY success_rate_percent DESC;

SELECT
    s.destination_city,
    COUNT(*) AS shipment_count
FROM Shipments s
WHERE s.promised_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY s.destination_city
ORDER BY shipment_count DESC
LIMIT 1;

SELECT
    p.partner_id,
    p.partner_name,
    COUNT(*) AS total_shipments,
    SUM(CASE
            WHEN s.actual_delivery_date IS NOT NULL
             AND s.actual_delivery_date > s.promised_date THEN 1
            ELSE 0
        END) AS delayed_shipments,
    SUM(CASE WHEN s.status = 'Delivered' THEN 1 ELSE 0 END) AS successful_deliveries,
    SUM(CASE WHEN s.status = 'Returned' THEN 1 ELSE 0 END) AS returned_deliveries,
    ROUND(
        (SUM(CASE WHEN s.status = 'Delivered' THEN 1 ELSE 0 END) * 100.0) / NULLIF(COUNT(*), 0),
        2
    ) AS success_rate_percent
FROM Partners p
LEFT JOIN Shipments s ON p.partner_id = s.partner_id
GROUP BY p.partner_id, p.partner_name
ORDER BY delayed_shipments ASC, success_rate_percent DESC;