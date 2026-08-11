DAY 1 — Technician Status Dashboard
Application Goal

Students start by seeing a real application screen/API, rather than starting with isolated Java programs.

Build

Technician Status Dashboard

Technician Dashboard

------------------------------------------------
Technician        Area        Status
------------------------------------------------
Ravi              Bangalore   AVAILABLE
Suresh            Mysore      BUSY
Arun              Bangalore   OFFLINE
Kiran             Tumkur      AVAILABLE
------------------------------------------------

Total Technicians : 4
Available         : 2
Busy              : 1
Offline            : 1
Backend

Create the initial Spring Boot application.

GET /api/technicians/status

Response:

{
  "total": 4,
  "available": 2,
  "busy": 1,
  "offline": 1
}

Technology USED
Java basics
OOP
Spring Boot
REST
JSON
Controller
Basic collections


End-of-day application
Browser/Postman
       ↓
Spring Boot
       ↓
Technician Status API
       ↓
In-memory Technician data

Day 1 deliverable: Working Technician Status Dashboard/API.