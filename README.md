# kafka-order-demo

A beginner-friendly Java 21 Spring Boot and React application for learning Apache Kafka. Orders are stored in memory, so they reset whenever the backend restarts.

## Packages

- `controller`: Defines HTTP endpoints and handles incoming web requests.
- `service`: Holds application logic that controllers can call.
- `producer`: Publishes messages to Kafka topics.
- `consumer`: Receives messages from Kafka topics.
- `model`: Contains the data objects used by the application.
- `config`: Holds simple application and Kafka configuration when it is introduced.

## Order events

`OrderCreatedEvent` is the message published to Kafka when a user creates an order. It contains the order ID, item, quantity, and creation time.

Event-driven systems publish events instead of directly calling another service or class so the order creator does not need to know which components will react. Multiple consumers can process the same event independently, and temporarily unavailable consumers can catch up from Kafka later.

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

## Run the frontend

In another terminal, install the frontend dependencies and start the Vite development server:

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`. Keep Kafka and the Spring Boot application running to place orders and see their status update automatically.

## Run the tests

```bash
mvn test
```