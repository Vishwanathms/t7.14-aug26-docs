#!/usr/bin/env bash
# Smoke-tests the running Phase 8 stack through the frontend's nginx proxy,
# the same origin a browser would use.
#
# Every URL is overridable via environment variable so this same script
# works two ways:
#   - run locally against the stack published on the host (defaults below)
#   - run in CI from a throwaway container attached to the compose stack's
#     own Docker network, addressing services by container name instead
#     (see the "smoke-test" job in .gitlab-ci.yml)
#
# Exits non-zero on the first failed check, so a failure here fails the
# GitLab CI job that calls this script.

set -uo pipefail

BASE="${BASE_URL:-http://localhost:8080}"
STUDENT_URL="${STUDENT_URL:-http://localhost:8081}"
COURSE_URL="${COURSE_URL:-http://localhost:8082}"
ENROLLMENT_URL="${ENROLLMENT_URL:-http://localhost:8083}"
FAILED=0

pass() { echo "PASS: $1"; }
fail() { echo "FAIL: $1"; FAILED=1; }

expect_status() {
  local description="$1" expected="$2" actual="$3"
  if [ "$actual" = "$expected" ]; then
    pass "$description (got $actual)"
  else
    fail "$description (expected $expected, got $actual)"
  fi
}

echo "== Waiting for services to report healthy =="
for url in "$STUDENT_URL" "$COURSE_URL" "$ENROLLMENT_URL"; do
  for i in $(seq 1 30); do
    status=$(curl -s -o /dev/null -w "%{http_code}" "${url}/actuator/health")
    if [ "$status" = "200" ]; then
      pass "service at ${url} is UP"
      break
    fi
    if [ "$i" = "30" ]; then
      fail "service at ${url} never became healthy"
    fi
    sleep 2
  done
done

echo
echo "== Frontend reachable =="
status=$(curl -s -o /dev/null -w "%{http_code}" "$BASE/")
expect_status "GET / (frontend)" "200" "$status"

echo
echo "== Create a student =="
# The database now persists across pipeline runs (see .gitlab-ci.yml), so a
# fixed email here would collide with the student the previous run created
# and correctly get rejected as a duplicate. A unique email per run keeps
# this check meaningful instead of failing on success.
RUN_ID="${CI_JOB_ID:-$(date +%s)}"
STUDENT_EMAIL="ci-smoke-${RUN_ID}@example.com"
create_resp=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/students" \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"CI Smoke Student\",\"email\":\"${STUDENT_EMAIL}\"}")
student_status=$(echo "$create_resp" | tail -n1)
student_body=$(echo "$create_resp" | sed '$d')
expect_status "POST /api/students" "201" "$student_status"
student_id=$(echo "$student_body" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "  created student id=$student_id"

echo
echo "== Create a course =="
course_resp=$(curl -s -w "\n%{http_code}" -X POST "$BASE/api/courses" \
  -H "Content-Type: application/json" \
  -d '{"title":"CI Smoke Course","capacity":30}')
course_status=$(echo "$course_resp" | tail -n1)
course_body=$(echo "$course_resp" | sed '$d')
expect_status "POST /api/courses" "201" "$course_status"
course_id=$(echo "$course_body" | grep -o '"id":[0-9]*' | head -1 | grep -o '[0-9]*')
echo "  created course id=$course_id"

echo
echo "== Validation: reject blank name =="
status=$(curl -s -o /dev/null -w "%{http_code}" -X POST "$BASE/api/students" \
  -H "Content-Type: application/json" \
  -d '{"name":"","email":"not-an-email"}')
expect_status "POST /api/students with invalid body" "400" "$status"

if [ -n "$student_id" ] && [ -n "$course_id" ]; then
  echo
  echo "== Enroll the student in the course (cross-service HTTP call) =="
  status=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
    "$BASE/api/enrollments?studentId=${student_id}&courseId=${course_id}")
  expect_status "POST /api/enrollments (first time)" "201" "$status"

  echo
  echo "== Duplicate enrollment is rejected =="
  status=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
    "$BASE/api/enrollments?studentId=${student_id}&courseId=${course_id}")
  expect_status "POST /api/enrollments (duplicate)" "409" "$status"

  echo
  echo "== Enrollment lookup returns the enriched result =="
  lookup_body=$(curl -s "$BASE/api/enrollments/student/${student_id}")
  if echo "$lookup_body" | grep -q "CI Smoke Course"; then
    pass "GET /api/enrollments/student/${student_id} includes course title"
  else
    fail "GET /api/enrollments/student/${student_id} missing expected course title: $lookup_body"
  fi
else
  fail "skipped enrollment checks - student_id or course_id was not captured"
fi

echo
echo "== Enrolling a nonexistent student is rejected =="
status=$(curl -s -o /dev/null -w "%{http_code}" -X POST \
  "$BASE/api/enrollments?studentId=999999&courseId=${course_id:-1}")
expect_status "POST /api/enrollments with unknown studentId" "404" "$status"

echo
if [ "$FAILED" -eq 0 ]; then
  echo "All smoke tests passed."
else
  echo "One or more smoke tests failed."
fi
exit "$FAILED"
