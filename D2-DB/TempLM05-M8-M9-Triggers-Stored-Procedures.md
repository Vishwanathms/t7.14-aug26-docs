# Lab 8 – Enterprise Stored Procedures
## Telecom Customer Management System

### Module

Stored Procedures – Business Logic

### Duration

90 Minutes

---

# Objective

In this lab, you will learn how enterprises use Stored Procedures to encapsulate business logic.

You will:

- Create a customer table
- Insert customer records
- Create reusable stored procedures
- Retrieve customer information
- Insert customers using procedures
- Recharge customer balances
- Verify updates
- Understand real-world business workflows

---

# Prerequisites

Before starting this lab, ensure:

- Docker Desktop is running.
- MySQL container (`mysql-db`) is running.
- You can connect to MySQL.
- Database **training** exists.

Connect to MySQL.

```bash
docker exec -it mysql-db mysql -uroot -p
```

Password

```
root123
```

Select the database.

```sql
USE training;
```

---

# Business Scenario

You are developing the backend database for a telecom company.

The database stores customer information.

Customers can

- Register
- View Details
- Recharge Balance

Instead of writing SQL in every application screen,

the application calls Stored Procedures.

```
Mobile App

      │

      ▼

Stored Procedures

      │

      ▼

Customer Database
```

---

# Exercise 1 – Create Customer Table

Execute

```sql
CREATE TABLE customer
(
    customer_id INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(50),
    mobile VARCHAR(15),
    balance DECIMAL(10,2),
    city VARCHAR(30)
);
```

Verify

```sql
DESC customer;
```

---

# Exercise 2 – Insert Sample Data

```sql
INSERT INTO customer
(customer_name,mobile,balance,city)

VALUES

('Rahul','9876543210',250,'Bangalore'),
('Anjali','9876500000',500,'Mysore'),
('Kiran','9988776655',100,'Chennai');
```

Verify

```sql
SELECT * FROM customer;
```

Expected Output

|ID|Name|Balance|
|--|----|--------|
|1|Rahul|250|
|2|Anjali|500|
|3|Kiran|100|

---

# Exercise 3 – Create Procedure
## GetCustomer

This procedure returns one customer based on Customer ID.

```sql
DELIMITER $$

CREATE PROCEDURE GetCustomer
(
    IN cust_id INT
)

BEGIN

SELECT *

FROM customer

WHERE customer_id=cust_id;

END $$

DELIMITER ;
```

Expected Output

```
Query OK
```

---

# Exercise 4 – Execute Procedure

Retrieve Customer 1

```sql
CALL GetCustomer(1);
```

Retrieve Customer 2

```sql
CALL GetCustomer(2);
```

Retrieve Customer 3

```sql
CALL GetCustomer(3);
```

Observe

Only one SQL statement is executed from the application.

---

# Exercise 5 – Create Procedure
## InsertCustomer

Instead of using INSERT directly,

applications call a procedure.

```sql
DELIMITER $$

CREATE PROCEDURE InsertCustomer

(
IN pname VARCHAR(50),
IN pmobile VARCHAR(15),
IN pbalance DECIMAL(10,2),
IN pcity VARCHAR(30)
)

BEGIN

INSERT INTO customer
(
customer_name,
mobile,
balance,
city
)

VALUES
(
pname,
pmobile,
pbalance,
pcity
);

END $$

DELIMITER ;
```

---

# Exercise 6 – Execute InsertCustomer

```sql
CALL InsertCustomer
(
'Sneha',
'9123456789',
300,
'Hyderabad'
);
```

Verify

```sql
SELECT * FROM customer;
```

Notice

A new customer has been inserted without writing an INSERT statement.

---

# Exercise 7 – Create Recharge Procedure

A telecom recharge increases the customer's wallet balance.

Create the procedure.

```sql
DELIMITER $$

CREATE PROCEDURE RechargeCustomer

(
IN custid INT,
IN recharge DECIMAL(10,2)
)

BEGIN

UPDATE customer

SET balance=balance+recharge

WHERE customer_id=custid;

END $$

DELIMITER ;
```

---

# Exercise 8 – Recharge a Customer

Recharge Customer 1

₹500

```sql
CALL RechargeCustomer
(
1,
500
);
```

Verify

```sql
SELECT *

FROM customer

WHERE customer_id=1;
```

Expected

Old Balance

```
250
```

New Balance

```
750
```

---

Recharge another customer.

```sql
CALL RechargeCustomer
(
3,
1000
);
```

Verify

```sql
CALL GetCustomer(3);
```

---

# Exercise 9 – Verify Business Flow

Imagine the following application.

```
Recharge Button

      │

      ▼

CALL RechargeCustomer()

      │

      ▼

Customer Balance Updated
```

Observe

The application never executes UPDATE directly.

Business logic remains inside the database.

---

# Exercise 10 – View Stored Procedures

```sql
SHOW PROCEDURE STATUS

WHERE Db='training';
```

Expected Procedures

- GetCustomer
- InsertCustomer
- RechargeCustomer

---

# Challenge Exercise

Create the following procedures.

---

### Task 1

Create

```
GetCustomersByCity()
```

Input

```
City
```

Display

All customers from that city.

---

### Task 2

Create

```
RechargeBonus()
```

Recharge

₹100

for every customer.

---

### Task 3

Create

```
UpdateCity()
```

Input

Customer ID

New City

Update the customer's city.

---

### Task 4

Create

```
DeleteCustomer()
```

Accept Customer ID.

Delete that customer.

---

# Learning Outcome

After completing this lab, you should be able to:

- Design reusable Stored Procedures
- Pass parameters to Procedures
- Encapsulate business logic inside the database
- Build enterprise-style database APIs
- Reduce duplicate SQL code
- Improve application maintainability

---

# Troubleshooting

## Procedure already exists

```sql
DROP PROCEDURE GetCustomer;
```

---

```sql
DROP PROCEDURE InsertCustomer;
```

---

```sql
DROP PROCEDURE RechargeCustomer;
```

Recreate the procedures.

---

## Verify Procedures

```sql
SHOW PROCEDURE STATUS

WHERE Db='training';
```

---

## Verify Customer Data

```sql
SELECT * FROM customer;
```

---

# Lab Summary

In this lab, you built a simplified Telecom Customer Management System using Stored Procedures.

You created procedures to:

- Retrieve customer information
- Register new customers
- Recharge customer balances

These procedures simulate how enterprise applications expose database operations as reusable APIs. In the next module, you will enhance this system by using **Triggers** to automatically log every recharge, validate data before updates, and maintain audit records without changing the application code.