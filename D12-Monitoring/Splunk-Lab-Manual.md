# Lab: Adding Splunk Log Monitoring to the Student Management Stack

This lab walks through adding centralized log monitoring to the Phase 9
stack (frontend, gateway, student/course/enrollment services, three
Postgres DBs) using Splunk, step by step, starting from a working Phase 9
checkout. No application code changes are required — everything here is
`docker-compose.yml` plus a small amount of Splunk configuration.

By the end you will be able to run `docker compose up -d`, add a student
through the app, and see that exact event show up in a Splunk search.

**Estimated time:** 30–45 minutes.

## Prerequisites

- Docker and Docker Compose v2 (`docker compose version`)
- A working Phase 9 checkout of this project
- At least ~5GB free disk space (Splunk refuses to run searches below a
  5GB free-space floor — check with `df -h`)
- Ports `8000` and `8088` free on the host, in addition to the `8080`/`9000`
  this project already uses

---

## Step 1 — Copy the project to a new phase directory

Work in a copy so Phase 9 stays intact.

```bash
rsync -a --exclude='target' --exclude='__pycache__' Phase-9/ Phase-10/
cd Phase-10
```

## Step 2 — Add the `splunk` service to `docker-compose.yml`

Open `docker-compose.yml` and add a new service. This is a single-instance
Splunk Enterprise container providing both the search UI (port 8000) and
the HTTP Event Collector, HEC (port 8088) — the endpoint everything else
will POST log events to.

```yaml
services:

  splunk:
    image: splunk/splunk:latest
    environment:
      SPLUNK_START_ARGS: --accept-license
      SPLUNK_GENERAL_TERMS: --accept-sgt-current-at-splunk-com
      SPLUNK_PASSWORD: ${SPLUNK_PASSWORD:-Changeme123!}
      SPLUNK_HEC_TOKEN: ${SPLUNK_HEC_TOKEN:-00000000-0000-0000-0000-000000000000}
      SPLUNK_HEC_SSL: "False"
    ports:
      - "8000:8000"   # Splunk web UI
      - "8088:8088"   # HTTP Event Collector (HEC)
    volumes:
      - splunk-etc:/opt/splunk/etc
      - splunk-var:/opt/splunk/var
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8000/en-US/account/login"]
      interval: 10s
      timeout: 5s
      retries: 30
      start_period: 60s
```

And register the two new named volumes at the bottom of the file, next to
the existing DB volumes:

```yaml
volumes:
  student-db-data:
  course-db-data:
  enrollment-db-data:
  splunk-etc:
  splunk-var:
```

**Why two license env vars?** The current `splunk/splunk` image requires
*both* `SPLUNK_START_ARGS=--accept-license` *and*
`SPLUNK_GENERAL_TERMS=--accept-sgt-current-at-splunk-com` — omitting either
one makes the container refuse to start and print a license-acceptance
error in its logs instead of coming up.

**Why `SPLUNK_HEC_TOKEN` as a plain env var?** The official image
auto-provisions the HEC token from this variable on first boot. That
avoids the usual chicken-and-egg problem: without it, you'd need a
*running* Splunk instance, logged into via the UI, to generate a token
before any other container could start logging to it.

## Step 3 — Wire every other container's logs to Splunk

Add a reusable logging config at the very top of `docker-compose.yml`,
above `services:`:

```yaml
x-splunk-logging: &splunk-logging
  driver: splunk
  options:
    # Not https:// - see the note below.
    splunk-url: "http://127.0.0.1:8088"
    splunk-token: "${SPLUNK_HEC_TOKEN:-00000000-0000-0000-0000-000000000000}"
    splunk-format: "json"
    splunk-sourcetype: "stu-mgmt"
    tag: "{{.Name}}"
```

Then apply it to every other service (`student-db`, `course-db`,
`enrollment-db`, `student-service`, `course-service`,
`enrollment-service`, `gateway`, `frontend`) by adding one line to each:

```yaml
  student-service:
    build: ./student-service
    environment:
      ...
    logging: *splunk-logging     # <-- add this line
    depends_on:
      student-db:
        condition: service_healthy
      splunk:                    # <-- and wait for splunk to be healthy
        condition: service_healthy
```

Repeat the `logging: *splunk-logging` line and the `splunk:
condition: service_healthy` dependency for the other seven services.

**Why no application code changes?** Every service here already logs to
stdout (check `logging.level.*` in each service's
`src/main/resources/application.yml`). Docker's built-in `splunk` logging
driver captures a container's stdout/stderr directly and ships it to HEC —
no logback config, no forwarder agent, no new dependency in any service.

### Two non-obvious pitfalls, explained up front

These two mistakes are easy to make and produce confusing errors. Both are
already baked into the config above — this section explains *why* it's
written that way, so you don't "fix" it back to the more obvious-looking
version.

1. **`http://`, not `https://`, even though HEC is usually TLS.**
   `SPLUNK_HEC_SSL: "False"` above makes HEC serve plain HTTP on 8088. If
   `splunk-url` says `https://`, the logging driver attempts a TLS
   handshake against a non-TLS port and every container that has
   `logging: *splunk-logging` fails to start with an SSL error.

2. **`127.0.0.1`, not the container name `splunk`, in `splunk-url`.**
   This is the one that looks wrong but is actually correct. Docker's
   built-in logging drivers (splunk, gelf, syslog, ...) resolve the host
   in their URL using the **Docker daemon's own host-level DNS resolver**
   — not the container network's embedded DNS (`127.0.0.11`) that regular
   containers use to resolve each other by service name. So
   `splunk-url: "http://splunk:8088"` fails with something like
   `dial tcp: lookup splunk on 127.0.0.53:53: server misbehaving`, even
   though `splunk` resolves fine from *inside* any container on the same
   compose network. Because the `splunk` service already publishes port
   8088 to the host (`ports: - "8088:8088"`), pointing at the host
   loopback (`127.0.0.1`) works because the daemon process runs on the
   host itself.

## Step 4 — Bring the stack up

```bash
docker compose up -d --build
```

Splunk takes 30–60 seconds to finish its first-boot setup before its
healthcheck passes. Everything with `depends_on: splunk: condition:
service_healthy` waits for that automatically — you'll see `splunk
Waiting` / `splunk Healthy` lines in the output before the app services
start.

```bash
docker compose ps
```

All eight services plus `splunk` should show `Up` (and `healthy` for the
three DBs and Splunk itself).

## Step 5 — Verify logs are actually arriving

**Via the CLI** (fastest, works even before you've explored the web UI):

```bash
docker exec -u splunk <splunk-container-name> \
  /opt/splunk/bin/splunk search 'index=main sourcetype=stu-mgmt | stats count' \
  -auth admin:Changeme123!
```

You should get back a non-zero count. If it's `0`, see Troubleshooting
below.

**Via the web UI:**

1. Open http://localhost:8000, log in with `admin` / `Changeme123!`
   (or whatever `SPLUNK_PASSWORD` you set).
2. Click into the **Search & Reporting** app — the Splunk home page itself
   shows no data; you have to enter the app.
3. Run:
   ```
   index=main sourcetype=stu-mgmt
   ```
4. Set the time range picker to **Last 60 minutes** (or **All time**) — a
   narrow default range is the most common reason for "I don't see
   anything."

## Step 6 — Prove it end-to-end: trigger an app action, find it in Splunk

1. Open the app frontend at http://localhost:8080 and add a student (or
   `curl -X POST http://localhost:9000/api/students -H 'Content-Type:
   application/json' -d '{"name":"Test Student","email":"test@example.com"}'`).
2. In Splunk, search for a phrase from the log line the app actually
   prints — e.g. `student-service`'s `StudentService` logs
   `Persisted student id=...` on every create:
   ```
   index=main sourcetype=stu-mgmt "Persisted student"
   ```
   Any substring of any real log line works the same way — this isn't a
   special keyword, just literal text matching. To see *all* of one
   container's logs without filtering to a phrase:
   ```
   index=main sourcetype=stu-mgmt tag=student-service
   ```
3. To follow one request across services (using the `X-Request-Id`
   Phase 9's gateway stamps on every request):
   ```
   index=main sourcetype=stu-mgmt (tag=gateway OR tag=student-service)
   ```

## Troubleshooting

| Symptom | Cause | Fix |
|---|---|---|
| `splunk` container logs show "License not accepted" and it keeps exiting | Missing `SPLUNK_GENERAL_TERMS` | Add it alongside `SPLUNK_START_ARGS` (Step 2) |
| App containers fail to start: `error:...SSL routines::wrong version number` | `splunk-url` is `https://` but HEC is plain HTTP | Use `http://` (Step 3, pitfall 1) |
| App containers fail to start: `dial tcp: lookup splunk on 127.0.0.53:53: server misbehaving` | Logging driver resolving `splunk` via host DNS, not container DNS | Use `http://127.0.0.1:8088` (Step 3, pitfall 2) |
| `docker compose up` fails with `Bind for 0.0.0.0:9000 failed: port is already allocated` | An older stack (e.g. Phase 9) is still running and holding that port | `docker compose -p <old-project> down` first |
| Splunk search returns nothing at all, even `index=main \| stats count` | Host disk is full — Splunk enforces a 5GB free-space floor and silently blocks searches below it | `df -h`; free space (e.g. `docker system prune -a`) |
| A specific service's logs never show up, others do | That container was created *before* a logging-config fix and never recreated | `docker compose up -d --force-recreate <service>` |
| Search for a field like `tag` in `stats by tag` returns nothing, but the raw event has it | HEC-ingested JSON isn't auto-extracted into fields for this sourcetype | Use `| spath` first: `... | spath | stats count by tag` |

## What you should have when this lab is done

- A `splunk` service in `docker-compose.yml`, reachable at
  http://localhost:8000 (UI) and port 8088 (HEC).
- All eight other services shipping their stdout logs to it via the
  `x-splunk-logging` anchor.
- The ability to search `index=main sourcetype=stu-mgmt` and see logs from
  every container, tagged by container name, and to trace one user action
  (e.g. creating a student) from the UI through to its log line in Splunk.

See [`Splunk-Monitoring.md`](./Splunk-Monitoring.md) for the condensed
operational reference, and [`CHANGES.md`](./CHANGES.md) for the
phase-level summary of what changed and why.
