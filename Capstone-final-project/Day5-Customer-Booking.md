# DAY 5 — Customer Booking

## Business Objective

Now introduce the actual business objective:

> **A customer wants to book a technician.**

The customer should be able to view available appointment slots, select a slot, enter booking details, check availability, and create a booking.

---

## Booking Flow

```text
Available Slots
      ↓
Select Slot
      ↓
Check Availability
      ↓
Create Booking
      ↓
Slot → BOOKED
```

---

## Booking Module

### Booking Attributes

The booking model contains the following core attributes:

| Attribute            | Description                                  |
| -------------------- | -------------------------------------------- |
| `bookingId`          | Unique booking identifier                    |
| `slotId`             | Appointment slot associated with the booking |
| `workType`           | Type of work/service required                |
| `serviceArea`        | Location/service area                        |
| `predictedMinutes`   | Estimated time required                      |
| `status`             | Current booking status                       |
| `cancellationReason` | Reason for cancellation, if applicable       |

---

## APIs

### Create Booking

```http
POST /api/bookings
```

Creates a new booking for an available appointment slot.

### Get All Bookings

```http
GET /api/bookings
```

Returns all bookings.

### Get Booking by ID

```http
GET /api/bookings/{id}
```

Returns details of a specific booking.

---

# Business Flow

```text
Customer
   ↓
Select Slot
   ↓
Check Availability
   ↓
Create Booking
   ↓
Slot → BOOKED
```

The application must ensure that a slot cannot be booked if it is already unavailable.

---

# End-of-Day Application

By the end of Day 5, students should have a working customer booking flow.

The application should allow the customer to:

1. View available slots
2. Select a slot
3. Enter booking details
4. Check slot availability
5. Create a booking
6. Display booking confirmation
7. Show the selected slot as `BOOKED`

---

# Step 1 — Available Slots

The customer should first see available appointment slots.

### Example

**Date:** `15-Aug-2026`

| Technician | Time        | Action |
| ---------- | ----------- | ------ |
| Ravi       | 09:00–09:30 | Book   |
| Ravi       | 10:00–10:30 | Book   |
| Ravi       | 14:00–14:30 | Book   |

Example UI:

```text
Available Slots

Date: [ 15-Aug-2026 ]

Technician     Time             Action
------------------------------------------------
Ravi           09:00-09:30      [ Book ]
Ravi           10:00-10:30      [ Book ]
Ravi           14:00-14:30      [ Book ]
```

When the customer clicks **Book**, the selected slot should be passed to the booking form.

---

# Step 2 — Booking Form

After selecting a slot, display the booking form.

Students should capture:

* Slot
* Work Type
* Service Area
* Predicted Minutes

### Example

```text
Create Booking

Selected Slot:
Technician : Ravi
Date       : 15-Aug-2026
Time       : 09:00 - 09:30

Work Type:
[ Installation ]

Service Area:
[ Bangalore ]

Predicted Minutes:
[ 30 ]

[ Check Availability ]

[ Create Booking ]
```

The booking model also contains:

```text
bookingId
slotId
status
cancellationReason
```

These values may be generated or managed by the backend depending on the application design.

---

# Step 3 — Booking Confirmation

After successful booking creation, display a confirmation page.

```text
Booking Confirmed

Booking ID       : BK001
Technician       : Ravi
Service Area     : Bangalore
Work Type        : Installation
Date             : 15-Aug-2026
Time             : 09:00 - 09:30
Status           : CONFIRMED
```

The selected appointment slot should now be displayed as:

```text
Status: BOOKED
```

and should no longer be available for another booking.

---

# Overall Application Navigation

The complete application can follow this navigation structure:

```text
                    Technician Service App
                            |
        +-------------------+-------------------+
        |                   |                   |
    Dashboard          Technicians          Scheduling
                            |                   |
                     Technician CRUD      Appointment
                                             Slots
                                                |
                                                |
                            +-------------------+
                            |
                  Technician Availability
                            |
                     Customer Booking
```

---

# Day 5 Final Application Flow

```text
Customer Booking
       |
       ↓
View Available Slots
       |
       ↓
Select Slot
       |
       ↓
Enter Booking Details
       |
       ↓
Check Availability
       |
       ↓
Create Booking
       |
       ↓
Booking Confirmation
       |
       ↓
Slot Status = BOOKED
```

## Day 5 Outcome

By the end of Day 5, students should have moved from **technician and scheduling management** to the actual **business-facing customer booking workflow**.

The customer should be able to complete the entire journey:

```text
Find Slot
   ↓
Select Slot
   ↓
Enter Service Details
   ↓
Check Availability
   ↓
Book Technician
   ↓
Receive Confirmation
   ↓
Slot Becomes BOOKED
```
