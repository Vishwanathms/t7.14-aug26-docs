#!/usr/bin/env python3
"""Seeds the running Phase 9 stack with a small, realistic dataset.

Goes through the frontend's nginx origin by default (nginx -> gateway ->
services) - the same path a browser uses - so seeded data goes through the
same validation and business rules a real user would hit, not written to the
databases directly.

Usage:
    python3 seed_data.py                                  # seeds http://localhost:8080
    BASE_URL=http://localhost:9000 python3 seed_data.py   # seed via the gateway directly

No third-party dependencies - uses only the standard library, since this is
the one Python-touching file in an otherwise pure-Java/JS project and
shouldn't need a venv or requirements.txt just to run.
"""
import json
import os
import urllib.error
import urllib.request

BASE = os.environ.get("BASE_URL", "http://localhost:8080")

STUDENT_NAMES = [
    ("Rahul Sharma", "rahul.sharma@example.com"),
    ("Priya Patel", "priya.patel@example.com"),
    ("Arjun Nair", "arjun.nair@example.com"),
    ("Sneha Iyer", "sneha.iyer@example.com"),
    ("Vikram Singh", "vikram.singh@example.com"),
    ("Ananya Rao", "ananya.rao@example.com"),
    ("Karthik Menon", "karthik.menon@example.com"),
    ("Divya Reddy", "divya.reddy@example.com"),
    ("Aditya Kumar", "aditya.kumar@example.com"),
    ("Meera Joshi", "meera.joshi@example.com"),
]

# name, capacity - one deliberately small (capacity 1) so the seeded data can
# demonstrate the capacity-exceeded business rule out of the box.
COURSE_DEFS = [
    ("Java Fundamentals", 30),
    ("Spring Boot in Practice", 25),
    ("Distributed Systems", 20),
    ("Database Design", 15),
    ("Capstone Workshop (limited seats)", 1),
]

# (student index, course index) pairs, into the lists above
ENROLL_PAIRS = [(0, 0), (0, 1), (1, 0), (1, 2), (2, 1), (3, 2), (3, 3)]


class ApiResult:
    def __init__(self, status, body):
        self.status = status
        self.body = body

    @property
    def json(self):
        try:
            return json.loads(self.body)
        except (json.JSONDecodeError, TypeError):
            return None


def request(method, path, payload=None):
    url = f"{BASE}{path}"
    data = json.dumps(payload).encode() if payload is not None else None
    req = urllib.request.Request(url, data=data, method=method)
    if data is not None:
        req.add_header("Content-Type", "application/json")
    try:
        with urllib.request.urlopen(req) as resp:
            body = resp.read().decode()
            return ApiResult(resp.status, body)
    except urllib.error.HTTPError as e:
        return ApiResult(e.code, e.read().decode())


def seed_students():
    print("== Students ==")
    ids = []
    for name, email in STUDENT_NAMES:
        result = request("POST", "/api/students", {"name": name, "email": email})
        student_id = result.json.get("id") if result.json else None
        if student_id:
            ids.append(student_id)
            print(f"  created student id={student_id}  {name}")
        else:
            print(f"  skipped (already exists?): {name}  -- {result.status} {result.body}")
    return ids


def seed_courses():
    print("\n== Courses ==")
    ids = []
    for title, capacity in COURSE_DEFS:
        result = request("POST", "/api/courses", {"title": title, "capacity": capacity})
        course_id = result.json.get("id") if result.json else None
        if course_id:
            ids.append(course_id)
            print(f"  created course id={course_id}  {title} (capacity {capacity})")
        else:
            print(f"  unexpected response for: {title} -- {result.status} {result.body}")
    return ids


def seed_enrollments(student_ids, course_ids):
    print("\n== Enrollments ==")
    if len(student_ids) < 6 or len(course_ids) < 5:
        print("  skipped - fewer than 6 students or 5 courses were created (probably re-run against already-seeded data)")
        return

    for s_idx, c_idx in ENROLL_PAIRS:
        sid, cid = student_ids[s_idx], course_ids[c_idx]
        result = request("POST", f"/api/enrollments?studentId={sid}&courseId={cid}")
        print(f"  enroll student {sid} in course {cid} -> {result.status}")

    # Fill the capacity-1 course (index 4) with one student, then demonstrate
    # the capacity-exceeded rejection with a second.
    capacity_course = course_ids[4]
    s1, s2 = student_ids[4], student_ids[5]
    result = request("POST", f"/api/enrollments?studentId={s1}&courseId={capacity_course}")
    print(f"  enroll student {s1} in the capacity-1 course {capacity_course} -> {result.status} (expect 201)")
    result = request("POST", f"/api/enrollments?studentId={s2}&courseId={capacity_course}")
    print(f"  enroll student {s2} in the same capacity-1 course -> {result.status} (expect 409, at capacity)")


def seed_statuses(student_ids):
    print("\n== Student statuses ==")
    if len(student_ids) < 8:
        print("  skipped - fewer than 8 students were created")
        return
    # One INACTIVE, one GRADUATED, so the UI's status badges/select have all
    # three states represented out of the box.
    inactive_id, graduated_id = student_ids[6], student_ids[7]
    request("PATCH", f"/api/students/{inactive_id}/status", {"status": "INACTIVE"})
    print(f"  student {inactive_id} -> INACTIVE")
    request("PATCH", f"/api/students/{graduated_id}/status", {"status": "GRADUATED"})
    print(f"  student {graduated_id} -> GRADUATED")


def main():
    print(f"Seeding against {BASE}\n")
    student_ids = seed_students()
    course_ids = seed_courses()
    seed_enrollments(student_ids, course_ids)
    seed_statuses(student_ids)

    print(f"\nDone. {len(student_ids)} students, {len(course_ids)} courses created this run.")
    print("Note: student-service rejects duplicate emails (409), so re-running skips students that already exist.")
    print("course-service has no such uniqueness check, though - re-running this script WILL create a second set of courses.")


if __name__ == "__main__":
    main()
