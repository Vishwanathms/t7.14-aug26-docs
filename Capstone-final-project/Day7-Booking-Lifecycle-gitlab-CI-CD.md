# DAY 7 — Booking Lifecycle

Now make the application behave like a **real business application**.

The booking is no longer just created and displayed. Students now implement the **complete booking lifecycle**, including starting, completing, and cancelling bookings.

---

# Part 1 — Booking Lifecycle

## Build

### Booking Lifecycle

```text
CONFIRMED
    ↓
IN_PROGRESS
    ↓
COMPLETED
```

### Cancellation Flow

```text
CONFIRMED
    ↓
CANCELLED
```

This lifecycle is already defined in the application design.

---

# Booking States

| State         | Description                                         |
| ------------- | --------------------------------------------------- |
| `CONFIRMED`   | Booking has been successfully created and confirmed |
| `IN_PROGRESS` | Technician has started working on the booking       |
| `COMPLETED`   | Technician has completed the work                   |
| `CANCELLED`   | Booking has been cancelled                          |

---

# APIs

### Confirm Booking

```http
POST /api/bookings/{id}/confirm
```

Moves a booking to:

```text
CONFIRMED
```

---

### Start Booking

```http
POST /api/bookings/{id}/start
```

Moves the booking from:

```text
CONFIRMED
     ↓
IN_PROGRESS
```

---

### Complete Booking

```http
POST /api/bookings/{id}/complete
```

Moves the booking from:

```text
IN_PROGRESS
     ↓
COMPLETED
```

---

### Cancel Booking

```http
POST /api/bookings/{id}/cancel
```

Moves the booking from:

```text
CONFIRMED
     ↓
CANCELLED
```

---

# Booking State Transitions

The application should enforce valid state transitions.

```text
                    ┌───────────────┐
                    │   CONFIRMED   │
                    └───────┬───────┘
                            │
                 ┌──────────┴──────────┐
                 ↓                     ↓
          IN_PROGRESS              CANCELLED
                 │
                 ↓
             COMPLETED
```

---

# Important Business Rules

The application must not allow arbitrary state changes.

### Rule 1 — Completed Booking Cannot Be Cancelled

```text
COMPLETED
    ↓
Cannot cancel
```

---

### Rule 2 — Cancelled Booking Cannot Be Completed

```text
CANCELLED
    ↓
Cannot complete
```

---

### Rule 3 — Confirmed Booking Can Start

```text
CONFIRMED
    ↓
IN_PROGRESS
```

---

### Rule 4 — In-Progress Booking Can Complete

```text
IN_PROGRESS
    ↓
COMPLETED
```

---

# Valid Transitions

| Current Status | Action   | New Status    |
| -------------- | -------- | ------------- |
| `CONFIRMED`    | Start    | `IN_PROGRESS` |
| `IN_PROGRESS`  | Complete | `COMPLETED`   |
| `CONFIRMED`    | Cancel   | `CANCELLED`   |

---

# Invalid Transitions

Examples:

```text
COMPLETED
    ↓
Cancel
    ↓
REJECT
```

```text
CANCELLED
    ↓
Complete
    ↓
REJECT
```

```text
CONFIRMED
    ↓
Complete
    ↓
REJECT
```

```text
IN_PROGRESS
    ↓
Start
    ↓
REJECT
```

The service layer should validate these transitions before updating the database.

---

# Booking Dashboard

Create a dashboard that displays the current state of all bookings.

### Example

```text
Booking Dashboard

B1001   Ravi      CONFIRMED
B1002   Arun      IN_PROGRESS
B1003   Suresh    COMPLETED
B1004   Kiran     CANCELLED
```

The UI should provide appropriate actions based on the current booking state.

Example:

```text
CONFIRMED
    [Start] [Cancel]

IN_PROGRESS
    [Complete]

COMPLETED
    No actions

CANCELLED
    No actions
```

---

# Service-Layer Business Logic

The lifecycle rules should be implemented in the service layer.

```text
BookingController
       ↓
BookingService
       ↓
Validate State Transition
       ↓
Update Booking Status
       ↓
BookingRepository
       ↓
PostgreSQL
```

The controller should not directly change the booking status without validation.

---

# Part 2 — GitLab CI/CD

Now automate application startup using **GitLab CI/CD**.

Add the following file to the repository:

```text
.gitlab-ci.yml
```

The pipeline must start the application using:

```bash
docker compose up --build -d
```

---

# Pipeline Requirement

The GitLab pipeline should:

1. Build the application.
2. Build the Docker images.
3. Start the application using Docker Compose.
4. Run the containers in detached mode.
5. Make the application available for further testing.

Example pipeline command:

```bash
docker compose up --build -d
```

---

# Important Rule

> **NO MORE RUNNING DOCKER COMPOSE MANUALLY**

From this day onward, students should start the application through the **GitLab CI/CD pipeline**.

Instead of:

```text
Student
   ↓
docker compose up --build -d
   ↓
Application
```

the workflow becomes:

```text
Student
   ↓
Git Push
   ↓
GitLab CI/CD Pipeline
   ↓
docker compose up --build -d
   ↓
Application
```

---

# Day 6 End-of-Day Outcome

By the end of Day 6, students should have:

### Application

* Booking lifecycle management
* Booking status transitions
* Start booking functionality
* Complete booking functionality
* Cancel booking functionality
* State transition validation
* Booking dashboard
* Conditional actions based on booking status

### CI/CD

* `.gitlab-ci.yml` added to the repository
* Docker Compose integrated into the pipeline
* Application automatically built using:

```bash
docker compose up --build -d
```

* No manual Docker Compose startup required

---

# Overall Day 6 Flow

```text
Customer Booking
       ↓
   CONFIRMED
       ↓
  IN_PROGRESS
       ↓
   COMPLETED
```

or:

```text
Customer Booking
       ↓
   CONFIRMED
       ↓
   CANCELLED
```

And the deployment flow becomes:

```text
Code Change
    ↓
Git Push
    ↓
GitLab CI/CD
    ↓
Docker Compose
    ↓
Build + Start Application
    ↓
Running Application
```
