# kafka-order-demo

A beginner-friendly Java 21 Spring Boot backend for learning Apache Kafka. The project currently includes Spring Web and Spring for Apache Kafka without a database, authentication, or custom Kafka configuration.

## Packages

- `controller`: Defines HTTP endpoints and handles incoming web requests.
- `service`: Holds application logic that controllers can call.
- `producer`: Publishes messages to Kafka topics.
- `consumer`: Receives messages from Kafka topics.
- `model`: Contains the data objects used by the application.
- `config`: Holds simple application and Kafka configuration when it is introduced.

## Start Kafka

Start the local single-node Kafka broker in KRaft mode:

```bash
docker compose up -d
```

Verify that the Kafka container is running:

```bash
docker compose ps
```

The `kafka` service should have a status of `Up` and expose port `9092`. The Spring Boot application connects to it at `localhost:9092`.

## Run the application

```bash
mvn spring-boot:run
```

Then request `http://localhost:8080/api/health`. The response is:

```json
{"status":"UP"}
```

Kafka does not need to be running for the health endpoint.

## Run the tests

```bash
mvn test
```