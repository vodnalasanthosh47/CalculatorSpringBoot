# Calculator REST API - End-to-End CI/CD Pipeline

A small Calculator REST API built with **Spring Boot 4 (Java 21, Maven)**, wrapped in a fully automated DevOps pipeline: **GitHub -> Jenkins -> Docker -> Docker Hub -> Docker Compose (Nginx + API)**.

---

## Table of Contents

1. [Features](#features)
2. [Tech Stack](#tech-stack)
3. [Project Structure](#project-structure)
4. [API Reference](#api-reference)
5. [Architecture](#architecture)
6. [Run Locally](#run-locally)
7. [Testing](#testing)
8. [Docker](#docker)
9. [Docker Compose Deployment](#docker-compose-deployment)
10. [Jenkins CI/CD Pipeline](#jenkins-cicd-pipeline)
11. [Jenkins Setup Checklist](#jenkins-setup-checklist)
12. [Links](#links)

---

## Features

- REST endpoints for add, subtract, multiply and divide
- Division by zero returns **HTTP 400** with a JSON error body
- Spring Boot **Actuator** health endpoint used for container health checks
- Layered design: Controller -> Service -> Model
- Unit tests (JUnit 5) and a Spring MVC integration test (MockMvc)
- Multi-stage, non-root Docker image
- Docker Compose stack with an **Nginx reverse proxy** in front of the API
- Declarative Jenkins pipeline with Docker Hub push and email notifications

## Tech Stack

| Area | Tool |
| --- | --- |
| Language / Framework | Java 21, Spring Boot 4.1.1 (Spring Web MVC, Actuator) |
| Build | Maven (with Maven Wrapper) |
| Testing | JUnit 5, Spring MockMvc (`@WebMvcTest`) |
| Version control | Git, GitHub |
| CI/CD | Jenkins (declarative pipeline) |
| Containers | Docker (multi-stage build), Docker Hub |
| Deployment | Docker Compose, Nginx (stable-alpine) |

## Project Structure

```
CalculatorSpringBoot/
├── src/
│   ├── main/
│   │   ├── java/org/example/calculatorspringboot/
│   │   │   ├── CalculatorSpringBootApplication.java   # entry point
│   │   │   ├── Controller/CalculatorController.java   # REST endpoints
│   │   │   ├── Service/CalculatorService.java         # arithmetic logic
│   │   │   └── Model/CalculationResponse.java         # JSON response model
│   │   └── resources/application.properties           # app name, port 9090
│   └── test/java/org/example/calculatorspringboot/
│       ├── CalculatorSpringBootApplicationTests.java  # context load test
│       ├── Controller/CalculatorControllerTest.java   # MockMvc tests
│       └── Service/CalculatorServiceTest.java         # unit tests
├── nginx/nginx.conf          # reverse-proxy config
├── Dockerfile                # multi-stage build
├── docker-compose.yml        # calculator + nginx stack
├── Jenkinsfile               # declarative CI/CD pipeline
├── .dockerignore
├── .gitignore
├── pom.xml
├── mvnw / mvnw.cmd           # Maven wrapper
└── README.md
```

## API Reference

Base path: `/api/v1`. All operations are `GET` requests taking integer query parameters `a` and `b`.

| Operation | Endpoint | Behaviour |
| --- | --- | --- |
| Add | `GET /api/v1/add?a=&b=` | Returns `a + b` |
| Subtract | `GET /api/v1/subtract?a=&b=` | Returns `a - b` |
| Multiply | `GET /api/v1/multiply?a=&b=` | Returns `a * b` |
| Divide | `GET /api/v1/divide?a=&b=` | Returns `a / b` (integer division); `b = 0` returns HTTP 400 |
| Health | `GET /actuator/health` | Spring Actuator health probe (`UP` / `DOWN`) |

**Successful response**

```bash
curl "http://localhost:9090/api/v1/add?a=13&b=58"
```

```json
{ "operation": "add", "a": 13, "b": 58, "result": 71 }
```

**Division by zero** (HTTP 400)

```bash
curl -i "http://localhost:9090/api/v1/divide?a=25&b=0"
```

```json
{ "error": "Division by 0" }
```

Missing or non-integer parameters are rejected with HTTP 400 by Spring's standard request-parameter handling.

**Health**

```bash
curl http://localhost:9090/actuator/health
# {"status":"UP"}
```

## Architecture

```
Developer ──push──▶ GitHub ──trigger──▶ Jenkins
                                          │
              ┌───────────────────────────┤
              ▼                           ▼
        mvn clean package           mvn test (JUnit + MockMvc)
              │
              ▼
        docker build  (tags: <build-number>, latest)
              │
              ▼
        docker push ──▶ Docker Hub  (vodnalasanthosh/calculator-api)
              │
              ▼
        docker compose up -d --wait
              │
              ▼
   ┌─────────────── Docker network: calc-net ───────────────┐
   │   Client ─▶ nginx :9090 (host) ─▶ calculator :9090     │
   │                         (only Nginx is published)      │
   └────────────────────────────────────────────────────────┘
              │
              ▼
        Email notification (success / failure)
```

Layers inside the application:

- **Controller** - maps HTTP requests under `api/v1`, handles the divide-by-zero check and returns HTTP 400 with an error body.
- **Service** - performs the arithmetic and builds the response object.
- **Model** - `CalculationResponse` (`operation`, `a`, `b`, `result`), generated with Lombok.

## Run Locally

**Prerequisites:** JDK 21, Maven 3.9+ (or use the included `mvnw`), Docker with the Compose plugin.

```bash
git clone https://github.com/vodnalasanthosh47/CalculatorSpringBoot.git
cd CalculatorSpringBoot

# Build the executable JAR
mvn clean package

# Run it
java -jar target/*.jar
```

The API listens on **http://localhost:9090** (`server.port=9090`).

## Testing

```bash
mvn test
```

| Test class | Type | What it covers |
| --- | --- | --- |
| `CalculatorServiceTest` | JUnit 5 unit test | `add`, `subtract`, `multiply`, `divide` return the expected `CalculationResponse` |
| `CalculatorControllerTest` | Spring MVC integration test (`@WebMvcTest` + `MockMvc`) | Each endpoint returns HTTP 200 with the right JSON; divide-by-zero returns HTTP 400 with `{"error":"Division by 0"}` |
| `CalculatorSpringBootApplicationTests` | `@SpringBootTest` | Application context loads |

Surefire writes JUnit XML reports to `target/surefire-reports/`.

## Docker

The `Dockerfile` is a two-stage build:

1. **Build stage** - `maven:3.9-eclipse-temurin-21`. `pom.xml` is copied first and `dependency:go-offline` is run so dependency layers are cached; then the sources are copied and the JAR is built with `-DskipTests`.
2. **Runtime stage** - `eclipse-temurin:21-jre-alpine`. Only the JAR is copied in, the process runs as a **non-root user** (`app`), and port `9090` is exposed.

A `.dockerignore` keeps `target/`, `.git`, IDE files and the Jenkins/Compose files out of the build context.

```bash
docker build -t calculator-api .
docker run -d -p 9090:9090 --name calculator calculator-api
curl "http://localhost:9090/api/v1/multiply?a=6&b=7"
```

Pull the published image instead of building:

```bash
docker pull vodnalasanthosh/calculator-api:latest
```

## Docker Compose Deployment

`docker-compose.yml` defines two services on a private bridge network (`calc-net`):

| Service | Image | Role |
| --- | --- | --- |
| `calculator` | `${IMAGE_NAME:-calculator-api}:${IMAGE_TAG:-latest}` | The Spring Boot API. Port 9090 is **exposed to the network only**, not to the host. Has a health check on `/actuator/health`, `restart: unless-stopped`, and `SERVER_PORT` set via environment. |
| `nginx` | `nginx:stable-alpine` | Reverse proxy and the **only public entry point** (`9090:80`). Starts only after `calculator` is healthy (`depends_on: condition: service_healthy`). |

`nginx/nginx.conf` forwards all requests to `calculator:9090` and uses Docker's embedded DNS resolver (`127.0.0.11`), so Nginx re-resolves the backend if the container is recreated during a redeploy.

```bash
docker compose up -d --build     # build locally and start the whole stack
docker compose ps                # both services should be running / healthy
curl http://localhost:9090/actuator/health
docker compose down
```

When run by Jenkins, `IMAGE_NAME` and `IMAGE_TAG` (the build number) are set so Compose deploys exactly the image that was just built and pushed.

## Jenkins CI/CD Pipeline

Defined in the `Jenkinsfile` (declarative pipeline, `agent any`). Jenkins checks out the repository from GitHub when the job triggers, then runs:

| # | Stage | What it does |
| --- | --- | --- |
| 1 | **Build** | `mvn clean package` - compiles and packages the JAR |
| 2 | **Test** | `mvn test` - runs JUnit and MockMvc tests; a failure stops the pipeline |
| 3 | **Docker Build** | Builds the image tagged `vodnalasanthosh/calculator-api:<BUILD_NUMBER>` and `:latest` |
| 4 | **Push** | Logs in to Docker Hub using the Jenkins credential `dockerhub-creds` (token piped via `--password-stdin`), pushes both tags, and always runs `docker logout` afterwards |
| 5 | **Deploy** | `docker compose up -d --remove-orphans --wait --wait-timeout 120`, then `docker compose ps` and `curl -fsS http://127.0.0.1:9090/actuator/health`; on failure it dumps `compose ps` and the last 50 log lines |

**Notifications:** the `post` block sends an email to the configured address with the subject `Build PASSED: <job> #<n>` or `Build FAILED: <job> #<n>` and a link to the build.

**Idempotency:** `docker compose up -d` only recreates services whose image or config changed, `--remove-orphans` cleans stale containers, and `--wait` blocks until the health check passes, so re-running the pipeline does not break a running deployment.

**Security:** Docker Hub credentials are never stored in the repository; they come from the Jenkins Credentials store.

## Jenkins Setup Checklist

To reproduce the pipeline on your own Jenkins server:

- [ ] Jenkins host has **JDK 21**, **Maven**, **Docker** and the **Docker Compose plugin**, and the `jenkins` user can run `docker` (member of the `docker` group)
- [ ] Plugins: Pipeline, Git, GitHub, Credentials Binding, Mailer (or Email Extension)
- [ ] Credentials: add a **Username with password** credential with ID `dockerhub-creds` (Docker Hub username + access token)
- [ ] Email: configure SMTP under *Manage Jenkins -> System -> E-mail Notification* and update the recipient address in the `Jenkinsfile`
- [ ] Create a **Pipeline** job using *Pipeline script from SCM*, pointing at this repository, script path `Jenkinsfile`
- [ ] Trigger: GitHub webhook (`http://<jenkins-host>/github-webhook/`) with *GitHub hook trigger for GITScm polling*, or Poll SCM as a fallback
- [ ] Push a commit and watch the pipeline build, test, push and deploy

## Links

- **GitHub:** https://github.com/vodnalasanthosh47/CalculatorSpringBoot
- **Docker Hub image:** `vodnalasanthosh/calculator-api` - https://hub.docker.com/r/vodnalasanthosh/calculator-api