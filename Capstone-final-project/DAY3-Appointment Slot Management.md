# DAY 3 — Appointment Slot Management

Now the application becomes useful.

Students ask:

> **"When can this technician visit?"**

---

## Build

### Appointment Slot Management

The objective is to allow students to create, view, and manage appointment slots for technicians.

---

## Example

```text
Technician: Ravi

09:00 - 09:30   AVAILABLE
10:00 - 10:30   AVAILABLE
11:00 - 11:30   BOOKED
14:00 - 14:30   AVAILABLE
```

---

# APIs

```http
POST /api/slots
GET  /api/slots
GET  /api/slots/{id}
```

### API Responsibilities

| API                   | Purpose                              |
| --------------------- | ------------------------------------ |
| `POST /api/slots`     | Create a new appointment slot        |
| `GET /api/slots`      | Retrieve all appointment slots       |
| `GET /api/slots/{id}` | Retrieve a specific appointment slot |

---

# Appointment Slot Model

```text
slotId
technicianId
startTime
endTime
state
version
```

### Attribute Description

| Attribute      | Description                                         |
| -------------- | --------------------------------------------------- |
| `slotId`       | Unique identifier for the appointment slot          |
| `technicianId` | Technician assigned to the slot                     |
| `startTime`    | Slot start date/time                                |
| `endTime`      | Slot end date/time                                  |
| `state`        | Current slot state, such as `AVAILABLE` or `BOOKED` |
| `version`      | Version number used later for optimistic locking    |

> The `version` field will later support **optimistic locking** for concurrent booking scenarios.

---

# Technology

Students will work with:

* PostgreSQL
* JPA
* Hibernate
* `@Entity`
* `@Id`
* `@GeneratedValue`
* `JpaRepository`

---

# Application Flow

```text
Technician
     ↓
Select Technician
     ↓
Select Date
     ↓
Create Time Slot
     ↓
Display Slots
     ↓
Show Available / Booked Status
     ↓
View Slot Details
     ↓
Filter by Technician / Date
```

---

# End-of-Day Application

By the end of Day 3, students should be able to:

* Select a technician
* Select a date
* Create an appointment slot
* View all slots
* View an individual slot
* Display slot status
* Show available/booked slots
* Filter slots by technician
* Filter slots by date
* Prevent invalid time ranges

---

# Features

## 1. Select Technician

Allow the user to select the technician for whom the appointment slot should be created.

```text
Technician
[ Ravi ▼ ]
```

---

## 2. Select Date

Allow the user to select the appointment date.

```text
Date
[ 15-Aug-2026 ]
```

---

## 3. Create Appointment Slot

The user should be able to create a new time slot.

### Create Slot Form

```text
Technician      [ Ravi ▼ ]
Date            [ 15-Aug-2026 ]
Start Time      [ 09:00 ]
End Time        [ 09:30 ]

                 [ Create Slot ]
```

---

# 4. View All Slots

Display the appointment slots created for technicians.

### Slot List

```text
Ravi — 15-Aug-2026

09:00 - 09:30    AVAILABLE
10:00 - 10:30    AVAILABLE
11:00 - 11:30    BOOKED
14:00 - 14:30    AVAILABLE
```

---

# 5. View Slot Details

Students should be able to select an individual slot and view its details.

Example:

```text
Slot Details

Slot ID       : SL001
Technician    : Ravi
Date          : 15-Aug-2026
Start Time    : 09:00
End Time      : 09:30
State         : AVAILABLE
Version       : 1
```

---

# 6. Display Slot Status

Each slot should clearly display its current state.

Possible states:

```text
AVAILABLE
BOOKED
```

Example:

```text
09:00 - 09:30    AVAILABLE
10:00 - 10:30    AVAILABLE
11:00 - 11:30    BOOKED
14:00 - 14:30    AVAILABLE
```

---

# 7. Filter Slots

Students should provide filtering options such as:

```text
Technician: [ Ravi ▼ ]
Date:       [ 15-Aug-2026 ]

[ Search ]
```

The application should then display only the matching slots.

---

# 8. Prevent Invalid Time Ranges

The application must validate the slot time before saving.

For example:

```text
Start Time: 10:00
End Time:   09:30
```

This should be rejected because:

```text
End Time <= Start Time
        ↓
Invalid Slot
        ↓
Do not save
```

Valid example:

```text
Start Time: 09:00
End Time:   09:30
        ↓
Valid Slot
```

Additional validation can later be introduced for overlapping slots and technician working hours.

---

# Backend Implementation

The basic backend structure should follow:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL
```

### Example

```text
SlotController
      ↓
SlotService
      ↓
SlotRepository
      ↓
PostgreSQL
```

Students should avoid placing business validation directly inside the controller. The controller should handle the HTTP request, while the service layer handles business logic.

---

# JPA Entity

The `AppointmentSlot` should be represented as a JPA entity.

Conceptually:

```text
AppointmentSlot
      |
      +-- slotId
      +-- technicianId
      +-- startTime
      +-- endTime
      +-- state
      +-- version
```

The `version` field prepares the application for **concurrent booking** scenarios that will be introduced later.

---

# Day 3 Learning Progression

Students have already built the technician management functionality.

Day 3 adds the scheduling layer:

```text
DAY 2
Technician Management
        ↓
DAY 3
Appointment Slot Management
        ↓
Technician + Time Slots
        ↓
DAY 4
Technician Availability
        ↓
DAY 5
Customer Booking
```

---

# Day 3 End-of-Day Outcome

By the end of Day 3, students should have a working **Appointment Slot Management** module where they can:

1. Select a technician.
2. Select an appointment date.
3. Create time slots.
4. View all appointment slots.
5. View individual slot details.
6. Display `AVAILABLE` / `BOOKED` status.
7. Filter slots by technician.
8. Filter slots by date.
9. Validate start and end times.
10. Persist appointment slots using **Spring Data JPA and PostgreSQL**.

The application now has the scheduling foundation required for **Day 4 — Technician Availability**.
