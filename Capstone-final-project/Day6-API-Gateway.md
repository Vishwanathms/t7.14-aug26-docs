# DAY 6 — API Gateway

## Business Objective

Now introduce the actual business objective:

> **All client requests (browser/Postman) should go through a single entry point instead of calling each backend API directly.**

The application should route Technician, Scheduling, Availability, and Booking requests through one **API Gateway**, instead of the client knowing the address of every individual API.

---

## Why an API Gateway

* Single entry point for the whole application
* Client does not need to know internal API paths/ports
* Common concerns (routing, logging, future auth) handled in one place
* Backend APIs can change/move without breaking the client

---

## Architecture

```text
Browser/Postman
      ↓
  API Gateway
      ↓
  +---------+---------+---------+
  |         |         |         |
Technician Scheduling Availability Booking
   API        API         API       API
```

---

## Gateway Module

### Route Attributes

The gateway route configuration contains the following core attributes:

| Attribute     | Description                                  |
| ------------- | --------------------------------------------- |
| `routeId`     | Unique identifier for the route                |
| `path`        | Incoming path pattern matched at the gateway   |
| `serviceUri`  | Target backend service address                |
| `stripPrefix` | Whether the gateway prefix is removed before forwarding |

---

## Gateway Routes

| Incoming Path (Gateway)     | Forwarded To                  |
| ---------------------------- | ------------------------------ |
| `/gateway/technicians/**`    | Technician Status/Mgmt API     |
| `/gateway/slots/**`          | Appointment Slot API           |
| `/gateway/availability/**`   | Technician Availability API    |
| `/gateway/bookings/**`       | Booking API                    |

### Example

```http
GET /gateway/technicians/status
```

Gateway forwards this internally to:

```http
GET /api/technicians/status
```

and returns the response back to the client, unchanged.

---

# Business Flow

```text
Client Request
      ↓
API Gateway
      ↓
Match Route (path)
      ↓
Forward to Backend API
      ↓
Return Response to Client
```

The client should no longer call backend service ports/paths directly — every request goes through the gateway.

---

# End-of-Day Application

By the end of Day 6, students should have a working API Gateway in front of the existing application.

The application should allow:

1. Client sends a request to the gateway
2. Gateway matches the request path to a configured route
3. Gateway forwards the request to the correct backend API
4. Backend API processes the request as before (Technician / Scheduling / Availability / Booking)
5. Response flows back through the gateway to the client

---

# Step 1 — Gateway Setup

Create a new Spring Boot application (or Spring Cloud Gateway module) that will act as the API Gateway.

### Example Configuration

```text
Gateway Service
Port: 8080

Route: technicians -> http://localhost:8081/api/technicians
Route: slots        -> http://localhost:8082/api/slots
Route: availability  -> http://localhost:8083/api/availability
Route: bookings      -> http://localhost:8084/api/bookings
```

---

# Step 2 — Route Configuration

Configure each route with its `routeId`, matching `path`, and `serviceUri`.

### Example

```text
Gateway Routes

routeId          path                     serviceUri
-------------------------------------------------------------
technician-route /gateway/technicians/**  http://technician-service
slot-route       /gateway/slots/**        http://scheduling-service
availability-route /gateway/availability/** http://availability-service
booking-route     /gateway/bookings/**     http://booking-service
```

---

# Step 3 — Verify Routing

Test each route through the gateway instead of calling the backend directly.

### Example

```text
Request  : GET /gateway/technicians/status
Forwarded: GET /api/technicians/status

Request  : GET /gateway/bookings
Forwarded: GET /api/bookings

Request  : POST /gateway/bookings
Forwarded: POST /api/bookings
```

The response returned to the client should be identical to calling the backend API directly.

---

# Overall Application Navigation

The complete application can follow this navigation structure:

```text
                    Technician Service App
                            |
                       API Gateway
                            |
        +-------------------+-------------------+-------------------+
        |                   |                   |                   |
    Technicians          Scheduling         Availability          Booking
```

---

# Day 6 Final Application Flow

```text
Client
   |
   ↓
API Gateway
   |
   ↓
Route Match
   |
   ↓
Forward to Backend Service
   |
   ↓
Backend Response
   |
   ↓
Gateway Returns Response
```

## Day 6 Outcome

By the end of Day 6, students should have moved from **directly calling individual backend APIs** to routing every client request through a **centralized API Gateway**.

The client should be able to complete the entire journey through the gateway alone:

```text
Send Request to Gateway
   ↓
Gateway Matches Route
   ↓
Request Forwarded to Backend
   ↓
Backend Processes Request
   ↓
Response Returned via Gateway
```
