#!/usr/bin/env bash
# Seeds the running Phase 9 stack with a small, realistic dataset through the
# frontend's nginx origin (the same path a browser uses: nginx -> gateway ->
# services) - not by writing to the databases directly, so the seeded data
# goes through the same validation and business rules a real user would hit.
#
# Usage:
#   ./seed-data.sh                          # seeds http://localhost:8080
#   BASE_URL=http://localhost:9000 ./seed-data.sh   # seed via the gateway directly

set -euo pipefail

BASE="${BASE_URL:-http://localhost:8080}"

echo "Seeding against $BASE"
echo

post() {
  # post <path> <json-body>  ->  echoes the response body, doesn't print status
  curl -s -X POST "$BASE$1" -H "Content-Type: application/json" -d "$2"
}

patch() {
  curl -s -X PATCH "$BASE$1" -H "Content-Type: application/json" -d "$2"
}

extract_id() {
  # extract_id <json>  ->  the numeric "id" field, or empty if not present
  # (e.g. an error response). The trailing "|| true" matters: under
  # `set -e -o pipefail`, grep's exit 1 on "no match" would otherwise abort
  # the whole script the first time this hits a duplicate/error response,
  # instead of letting the caller's own if/else handle "no id" gracefully.
  echo "$1" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*' || true
}

declare -a STUDENT_IDS=()
declare -a COURSE_IDS=()

echo "== Students =="
STUDENT_NAMES=(
  "Rahul Sharma:rahul.sharma@example.com"
  "Priya Patel:priya.patel@example.com"
  "Arjun Nair:arjun.nair@example.com"
  "Sneha Iyer:sneha.iyer@example.com"
  "Vikram Singh:vikram.singh@example.com"
  "Ananya Rao:ananya.rao@example.com"
  "Karthik Menon:karthik.menon@example.com"
  "Divya Reddy:divya.reddy@example.com"
  "Aditya Kumar:aditya.kumar@example.com"
  "Meera Joshi:meera.joshi@example.com"
)
for entry in "${STUDENT_NAMES[@]}"; do
  name="${entry%%:*}"
  email="${entry##*:}"
  resp=$(post /api/students "{\"name\":\"$name\",\"email\":\"$email\"}")
  id=$(extract_id "$resp")
  if [ -n "$id" ]; then
    STUDENT_IDS+=("$id")
    echo "  created student id=$id  $name"
  else
    echo "  skipped (already exists?): $name  -- $resp"
  fi
done

echo
echo "== Courses =="
# name:capacity - one deliberately small (capacity 1) so the seeded data can
# demonstrate the capacity-exceeded business rule out of the box.
COURSE_DEFS=(
  "Java Fundamentals:30"
  "Spring Boot in Practice:25"
  "Distributed Systems:20"
  "Database Design:15"
  "Capstone Workshop (limited seats):1"
)
for entry in "${COURSE_DEFS[@]}"; do
  title="${entry%%:*}"
  capacity="${entry##*:}"
  resp=$(post /api/courses "{\"title\":\"$title\",\"capacity\":$capacity}")
  id=$(extract_id "$resp")
  if [ -n "$id" ]; then
    COURSE_IDS+=("$id")
    echo "  created course id=$id  $title (capacity $capacity)"
  else
    echo "  unexpected response for: $title -- $resp"
  fi
done

echo
echo "== Enrollments =="
if [ "${#STUDENT_IDS[@]}" -ge 6 ] && [ "${#COURSE_IDS[@]}" -ge 5 ]; then
  # A handful of ordinary enrollments, spread across students and courses.
  ENROLL_PAIRS=(
    "0 0" "0 1" "1 0" "1 2" "2 1" "3 2" "3 3"
  )
  for pair in "${ENROLL_PAIRS[@]}"; do
    read -r s_idx c_idx <<< "$pair"
    sid="${STUDENT_IDS[$s_idx]}"
    cid="${COURSE_IDS[$c_idx]}"
    status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/api/enrollments?studentId=$sid&courseId=$cid")
    echo "  enroll student $sid in course $cid -> $status"
  done

  # Fill the capacity-1 course (index 4) with one student, then demonstrate
  # the capacity-exceeded rejection with a second.
  capacity_course="${COURSE_IDS[4]}"
  s1="${STUDENT_IDS[4]}"
  s2="${STUDENT_IDS[5]}"
  status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/api/enrollments?studentId=$s1&courseId=$capacity_course")
  echo "  enroll student $s1 in the capacity-1 course $capacity_course -> $status (expect 201)"
  status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/api/enrollments?studentId=$s2&courseId=$capacity_course")
  echo "  enroll student $s2 in the same capacity-1 course -> $status (expect 409, at capacity)"
else
  echo "  skipped - fewer than 6 students or 5 courses were created (probably re-run against already-seeded data)"
fi

echo
echo "== Student statuses =="
if [ "${#STUDENT_IDS[@]}" -ge 8 ]; then
  # One INACTIVE, one GRADUATED, so the UI's status badges/select have all
  # three states represented out of the box.
  inactive_id="${STUDENT_IDS[6]}"
  graduated_id="${STUDENT_IDS[7]}"
  patch /api/students/$inactive_id/status '{"status":"INACTIVE"}' >/dev/null
  echo "  student $inactive_id -> INACTIVE"
  patch /api/students/$graduated_id/status '{"status":"GRADUATED"}' >/dev/null
  echo "  student $graduated_id -> GRADUATED"
else
  echo "  skipped - fewer than 8 students were created"
fi

echo
echo "Done. ${#STUDENT_IDS[@]} students, ${#COURSE_IDS[@]} courses created this run."
echo "Note: student-service rejects duplicate emails (409), so re-running skips students that already exist."
echo "course-service has no such uniqueness check, though - re-running this script WILL create a second set of courses."
