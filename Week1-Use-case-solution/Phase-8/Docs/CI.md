# Phase 7 — GitLab CI

**Files:** [`.gitlab-ci.yml`](../.gitlab-ci.yml), [`ci/smoke-test.sh`](../ci/smoke-test.sh)

Two stages, `deploy` and `smoke-test`, run as separate GitLab jobs that share the same running containers:

## `deploy`

1. `docker compose down -v || true` — clean slate, in case a previous run's `smoke-test` never got to run its teardown
2. `docker compose build` — builds all four images (three services + frontend) from their own `Dockerfile`s
3. `docker compose up -d` — brings up all seven containers (three databases, three services, frontend)
4. `docker compose ps` — printed for visibility in the job log

## `smoke-test`

Runs after `deploy` succeeds (`needs: [deploy]`), against the containers `deploy` left running:

1. `bash ci/smoke-test.sh` — runs 9 real HTTP checks through `http://localhost:8080`, the same nginx origin a browser uses:
   - all three services report healthy (`/actuator/health`)
   - the frontend itself is reachable
   - create a student, create a course
   - reject an invalid student (`400`)
   - enroll the student in the course — the actual cross-service HTTP call between `enrollment-service` and the other two
   - reject a duplicate enrollment (`409`)
   - the enrollment lookup returns the course title, proving it came from a real network call, not a shared table
   - reject enrolling a nonexistent student (`404`)
2. `after_script` always runs `docker compose down -v` and dumps container logs as a build artifact, whether the smoke tests passed or failed — so a failed pipeline run has logs attached, not just a red X

Verified locally by running [`ci/smoke-test.sh`](../ci/smoke-test.sh) directly against the running stack before wiring it into the pipeline — all 9 checks passed.

## Why two stages can share one Docker Compose stack

Each GitLab job normally gets its own isolated environment, so a `docker:dind` service (a fresh, empty Docker daemon per job) would make this split impossible — `smoke-test` would see no containers at all. This runner instead has the **host VM's own `/var/run/docker.sock`** mounted, so `deploy` and `smoke-test` both talk to the same real Docker daemon on the same machine. `tags: [docker]` on both jobs (via the shared `.on_docker_runner` template) pins them to that specific runner, which is what makes this actually reliable — without matching tags, GitLab could schedule the two jobs on different runners with different Docker daemons, and `smoke-test` would find nothing running.

## Why there's no separate Maven build stage

Each service's `Dockerfile` is already a multi-stage build — a Maven builder stage compiles the jar, then a slim JRE stage runs it. `docker compose build` in `deploy` runs that Maven build for all three services. An earlier version of this pipeline also had a standalone `build` stage running `mvn package` outside Docker for a faster fail signal — but that meant every pipeline run compiled each service **twice**. Removed in favor of letting `docker compose build` be the only build step.

## Runner requirements

Needs a runner with the host's Docker daemon reachable via a mounted `docker.sock` — not `docker:dind`, which would give each job its own isolated, empty daemon and break the `deploy` → `smoke-test` handoff. Both jobs must land on that same runner, enforced here via the `docker` tag.

## Where this file lives

This `.gitlab-ci.yml` assumes `Phase-7/` is the repository root — job scripts (`cd student-service`, `docker compose build`, etc.) are written relative to that. If `Phase-7/` instead becomes one folder inside a larger monorepo, either move this file to the repo root and prefix every script line with `cd Phase-7 &&`, or point GitLab's **CI/CD → General pipelines → CI/CD configuration file** setting at `Phase-7/.gitlab-ci.yml` and add the same prefix.
