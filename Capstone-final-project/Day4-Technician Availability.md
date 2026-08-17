# DAY 4 — Technician Availability

## Business Objective

Now combine **Technician + Appointment Slots**.

The goal is to build a **Technician Availability Dashboard** that shows a technician's profile, working hours, appointment slots, and current availability.

This is the first stage where students move beyond simple CRUD and start implementing **business rules**.

---

# Technician Availability Dashboard

### Example

```text
Ravi
Bangalore
Skills: Installation, Repair

Today's Availability

09:00 ───── AVAILABLE
10:00 ───── AVAILABLE
11:00 ───── BOOKED
12:00 ───── AVAILABLE
14:00 ───── AVAILABLE
```

---

# APIs

### Get Technician Availability

```http
GET /api/technicians/{id}/availability
```

Returns the technician's availability and appointment slots.

### Get Available Slots

```http
GET /api/slots/available
```

Returns slots that are currently available for booking.

### Get Technician Slots

```http
GET /api/slots/technician/{technicianId}
```

Returns appointment slots associated with a specific technician.

---

# Business Rules

## Rule 1 — Technician Must Be Active

```text
Technician inactive
        ↓
No new slots
```

An inactive technician must not be allowed to create new appointment slots.

---

## Rule 2 — Booked Slot Is Not Available

```text
Slot already booked
        ↓
Not available
```

A slot that has already been booked must not appear as an available slot.

---

## Rule 3 — Slot Must Be Within Working Hours

```text
Outside working hours
        ↓
Cannot create slot
```

The application must validate that a new slot falls within the technician's configured working hours.

---

# Dashboard Features

The Technician Availability page should provide:

* Technician profile summary
* Technician skills
* Service area
* Working hours
* Today's slots
* Available/Booked timeline
* Available-slots view
* Date filtering
* Validation for working hours
* Validation for inactive technicians
* Validation for already-booked slots

---

# Suggested Screen

```text
--------------------------------------------------
             Technician Availability
--------------------------------------------------

Ravi
Bangalore
Skills: Installation, Repair

Date: [ 15-Aug-2026 ]

Today's Availability

09:00 ───── AVAILABLE
10:00 ───── AVAILABLE
11:00 ───── BOOKED
12:00 ───── AVAILABLE
14:00 ───── AVAILABLE

--------------------------------------------------

Available Slots: 4
Booked Slots:    1
```

---

# User Flow

```text
Select Technician
        ↓
View Technician Information
        ↓
View Working Hours
        ↓
Select Date
        ↓
View Appointment Slots
        ↓
Identify Available / Booked Slots
        ↓
Validate Slot Rules
```

---

# Technology Focus

Day 4 introduces important backend concepts beyond basic CRUD.

### Entity Relationships

Students should work with relationships between:

```text
Technician
    |
    +---- Appointment Slots
```

Depending on the application model, this can be implemented using JPA relationships such as `@OneToMany` and `@ManyToOne`.

---

### JPA Queries

Students should implement queries for:

* Slots belonging to a technician
* Available slots
* Booked slots
* Slots for a specific date
* Slots within working hours

Example:

```text
GET /api/slots/available
        ↓
Repository
        ↓
JPA Query
        ↓
Available Slots
```

---

### Service-Layer Business Rules

Business rules should be implemented in the **service layer**, rather than directly inside controllers.

Examples:

```text
Technician inactive?
        ↓
Reject slot creation

Outside working hours?
        ↓
Reject slot creation

Slot already booked?
        ↓
Reject booking
```

---

### Request / Response DTOs

Students should introduce DTOs for API requests and responses.

Example response:

```json
{
  "technicianId": 1,
  "technicianName": "Ravi",
  "serviceArea": "Bangalore",
  "skills": [
    "Installation",
    "Repair"
  ],
  "date": "2026-08-15",
  "availableSlots": 4,
  "bookedSlots": 1
}
```

---

# Key Learning Outcome

Day 4 is an important transition point.

Students move from:

```text
CRUD Operations
      ↓
Technician Management
      ↓
Appointment Slot Management
```

to:

```text
Business Rules
      ↓
Technician Availability
      ↓
Availability Validation
      ↓
Available / Booked Slots
```

The application now starts behaving like a real **Technician Service Management System**, rather than simply displaying database records.

---

# Day 4 End-of-Day Outcome

By the end of Day 4, students should be able to:

1. Select a technician.
2. View technician information.
3. View skills and service area.
4. View working hours.
5. Filter availability by date.
6. Display today's appointment slots.
7. Identify available and booked slots.
8. Retrieve available slots through an API.
9. Prevent slot creation for inactive technicians.
10. Prevent slots outside working hours.
11. Prevent booking of already-booked slots.
12. Implement the above rules in the service layer.

The resulting page becomes the foundation for **Day 5 — Customer Booking**.

```text
DAY 4
Technician
    +
Appointment Slots
    ↓
Technician Availability
    ↓
Business Rules
    ↓
Available Slots
    ↓
DAY 5
Customer Booking
```
