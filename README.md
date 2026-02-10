# Polyglot Persons API

A modern microservice demonstration showcasing **Java 21 Virtual Threads** and polyglot communication between Spring Boot and Go.

## Tech Stack
* **Java 21**: Utilizes **Virtual Threads** (Project Loom) for high-efficiency I/O handling.
* **Spring Boot 3**: Backend framework for REST API and database connectivity via JPA.
* **Go**: Specialized microservice located in the `sorting-service` directory for high-performance data sorting.
* **PostgreSQL**: Persistent storage for person records.
* **Docker Compose**: Full orchestration of the entire infrastructure.



## Architecture
The Java application manages person records in a PostgreSQL database. When a request is made to the `/sorted` endpoint, the data is delegated via REST to the Go service, sorted there, and returned to Java. By using Virtual Threads, the Java application does not block operating system threads during the network call to the Go container.

## Getting Started
The entire system can be started with a single command:

```bash
docker compose up --build
