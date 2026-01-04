# Tracker

Tracker is a general-purpose counter system.

## Local development (Postgres via Docker)
Start Postgres:

```bash
docker compose up -d
```

If you already have something using port 5432, either stop it or change `docker-compose.yml` to map a different local port (e.g. `5433:5432`) and set:

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5433/tracker
```

Run the app (uses the Gradle wrapper):

```bash
./gradlew bootRun
```

Then open:
- UI: `http://localhost:8080/counters`
- API: `POST http://localhost:8080/api/counters`

Example API request:

```bash
curl -s -X POST localhost:8080/api/counters \
  -H 'content-type: application/json' \
  -d '{"name":"Water","unit":"glasses"}'
```


