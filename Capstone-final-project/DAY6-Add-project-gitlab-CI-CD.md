DAY 6 — Booking Lifecycle
Now make the application behave like a real business application.
Build
Booking lifecycle:
CONFIRMED
    ↓
IN_PROGRESS
    ↓
COMPLETED
Cancellation:
CONFIRMED
    ↓
CANCELLED
This lifecycle is already defined in the application design. 
APIs
POST /api/bookings/{id}/confirm
POST /api/bookings/{id}/start
POST /api/bookings/{id}/complete
POST /api/bookings/{id}/cancel
Important business rules
COMPLETED → cannot cancel

CANCELLED → cannot complete

CONFIRMED → can start

IN_PROGRESS → can complete
End-of-day application
Booking Dashboard

B1001   Ravi    CONFIRMED
B1002   Arun    IN_PROGRESS
B1003   Suresh  COMPLETED
B1004   Kiran   CANCELLED


==========================================

Part-2

Add .gitlab-ci.yaml to the repo 

Make sure to run the "docker compose up --build -d" in the pipeline 


NOTE: 
NO MORE RUNNING THE DOCKER COMPOSE MANUALLY