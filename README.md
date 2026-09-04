# Razorpay — Payment Gateway

> 🚧 **Work in Progress**
>
> This project is an ongoing implementation of a payment gateway inspired by platforms such as Razorpay. The goal is to explore how a payment gateway can be designed and implemented using **Java and Spring Boot**, with an emphasis on payment processing, security, extensibility, state management, and reliability.

## Overview

This project models the core backend components of a payment gateway that allows merchants to create orders and process payments through different payment methods.

The application is designed around a few important concepts found in payment systems:

* Merchant management
* API key based authentication
* JWT based authentication
* Order management
* Payment processing
* Multiple payment methods
* Payment gateway adapters
* Payment processing strategies
* Payment state transitions
* Payment transaction logging
* Refund and settlement modelling
* Payment simulation for development and testing

The project is intentionally being developed incrementally, with the architecture evolving as additional payment gateway capabilities are implemented.

---

## Architecture

The current implementation is a **modular Spring Boot application** rather than a collection of independent microservices.

The code is organized around business domains such as:

```text
                    ┌─────────────────────┐
                    │       Client        │
                    └──────────┬──────────┘
                               │
                               ▼
                    ┌─────────────────────┐
                    │    Spring Boot API  │
                    └──────────┬──────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
              ▼                ▼                ▼
        ┌───────────┐    ┌────────────┐   ┌────────────┐
        │  Merchant │    │   Orders   │   │  Payments  │
        │ Management│    │ Management │   │ Processing │
        └───────────┘    └────────────┘   └─────┬──────┘
                                                 │
                         ┌───────────────────────┼──────────────────────┐
                         │                       │                      │
                         ▼                       ▼                      ▼
                  ┌────────────┐         ┌────────────┐         ┌────────────┐
                  │    Card    │         │    UPI     │         │ NetBanking │
                  │  Adapter   │         │  Adapter   │         │  Adapter   │
                  └────────────┘         └────────────┘         └────────────┘
```

The payment module further separates **gateway integration**, **payment processing**, and **payment state management** to make the design easier to extend.

---

## Core Components

### Merchant Management

The merchant domain handles the entities and operations associated with merchants.

The current implementation includes:

* Merchant
* Customer
* Application users
* API keys
* Merchant webhook configuration

The merchant module is structured into controllers, DTOs, entities, repositories, services, mappers, and security components.

---

### Order Management

Orders represent the business request for a payment.

The payment domain contains an `OrderRecord` entity and an `OrderService`, providing a separate layer for order-related operations.

A simplified flow is:

```text
Merchant
   │
   ▼
Create Order
   │
   ▼
Order Created
   │
   ▼
Initiate Payment
```

---

### Payment Processing

The payment module is the core of the application.

It contains:

* Payment controllers
* Payment services
* Payment entities
* Payment processors
* Gateway adapters
* Payment state machine
* Payment repositories
* Payment configuration
* Payment simulator

The application currently models payment methods including:

* Card
* UPI
* Net Banking

---

## Payment Gateway Adapter Pattern

The gateway layer abstracts the actual payment method from the rest of the application.

```text
                  PaymentGatewayRouter
                           │
                           ▼
                    PaymentAdapter
                     /     |      \
                    /      |       \
                   ▼       ▼        ▼
                Card      UPI    NetBanking
               Adapter   Adapter   Adapter
```

The `PaymentGatewayRouter` selects the appropriate `PaymentAdapter` based on the requested `PaymentMethod`.

This makes it possible to add another payment method without modifying the core payment service extensively.

For example:

```text
PaymentMethod
      │
      ├── CARD
      ├── UPI
      ├── NET_BANKING
      └── FUTURE_METHOD
```

The adapter abstraction keeps payment-method-specific behaviour isolated from the rest of the payment flow.

---

## Payment Processor Strategy

The project also separates payment processing logic using a strategy-based design.

```text
                  PaymentProcessorRouter
                           │
              ┌────────────┼────────────┐
              │            │            │
              ▼            ▼            ▼
       CardPayment     UpiPayment    NetBanking
        Processor       Processor     Processor
```

Currently, dedicated processors exist for:

* Card payments
* UPI payments
* Net Banking payments

This allows different payment methods to have their own processing behaviour while exposing a common interface to the application.

---

## Payment State Machine

Payments are not simply `SUCCESS` or `FAILED`.

A real payment can move through several intermediate states.

This project explicitly models those transitions using a payment state machine.

```text
                    ┌─────────────┐
                    │   CREATED   │
                    └──────┬──────┘
                           │
                    AUTHORIZE_ATTEMPT
                           │
                           ▼
                  ┌────────────────┐
                  │  AUTHORIZING   │
                  └───────┬────────┘
                          │
                 ┌────────┴─────────┐
                 │                  │
                 ▼                  ▼
          AUTHORIZE_SUCCESS   AUTHORIZE_FAIL
                 │                  │
                 ▼                  ▼
          ┌─────────────┐     ┌─────────┐
          │  AUTHORIZED │     │ FAILED  │
          └──────┬──────┘     └─────────┘
                 │
          CAPTURE_REQUEST
                 │
                 ▼
          ┌─────────────┐
          │  CAPTURING  │
          └──────┬──────┘
                 │
          CAPTURE_SUCCESS
                 │
                 ▼
          ┌─────────────┐
          │  CAPTURED   │
          └──────┬──────┘
                 │
             SETTLE
                 │
                 ▼
          ┌─────────────┐
          │   SETTLED   │
          └─────────────┘
```

The state machine validates whether a particular event is valid for the current payment state.

For example:

```text
CREATED
   │
   └── AUTHORIZE_ATTEMPT
             ↓
        AUTHORIZING
```

An invalid transition results in an `InvalidStateTransitionException`.

This prevents arbitrary or inconsistent payment state changes.

---

## Supported Payment States

The current model includes states such as:

* `CREATED`
* `AUTHORIZING`
* `AUTHORIZED`
* `CAPTURING`
* `CAPTURED`
* `FAILED`
* `CANCELLED`
* `AUTH_EXPIRED`
* `PARTIALLY_REFUNDED`
* `REFUNDED`
* `SETTLED`

The state machine defines the allowed transitions between these states.

---

## Authentication & Security

The merchant domain contains two authentication mechanisms.

### JWT Authentication

JWT authentication is used for authenticated application users.

The JWT filter:

1. Reads the `Authorization` header.
2. Extracts the Bearer token.
3. Verifies the token.
4. Extracts the user's role.
5. Establishes the Spring Security authentication.
6. Associates the request with a merchant.

```text
Authorization: Bearer <JWT>
              │
              ▼
      JwtAuthenticationFilter
              │
              ▼
          Verify JWT
              │
              ▼
       Extract Merchant
              │
              ▼
      SecurityContext
```

### API Key Authentication

The application also supports API-key authentication.

The API key filter reads credentials from a Basic Authorization header, looks up the corresponding API key, validates the secret using BCrypt, and associates the request with the corresponding merchant.

```text
Authorization: Basic <credentials>
                    │
                    ▼
          API Key Authentication
                    │
                    ▼
             Find API Key
                    │
                    ▼
          Verify Secret Hash
                    │
                    ▼
           Resolve Merchant
                    │
                    ▼
           Security Context
```

The API-key model also supports key rotation through a previous secret and a configurable grace period.

---

## Merchant Context

After authentication, the application establishes the merchant context for the current request.

This allows downstream business logic to determine which merchant the request belongs to without repeatedly passing the merchant identifier through every method.

```text
Incoming Request
       │
       ▼
 Authentication
       │
       ▼
 MerchantContext
       │
       ▼
 Business Logic
```

This is particularly useful for building a multi-merchant payment platform where data and operations must be associated with the correct merchant.

---

## Payment Simulation

Because this is a development project, the application includes a payment/bank simulator.

The simulator can emulate payment-provider behaviour without requiring an actual external banking system.

The current configuration supports different processing characteristics for:

| Payment Method | Simulated Delay | Success Rate |
| -------------- | --------------: | -----------: |
| Card           |     2–6 seconds |          90% |
| UPI            |     1–3 seconds |          95% |
| Net Banking    |    4–10 seconds |          85% |

The simulator configuration also supports different chaos modes for testing payment behaviour.

This makes it possible to experiment with scenarios such as delayed or unsuccessful payment processing during development.

---

## Data Model

The application uses PostgreSQL through Spring Data JPA / Hibernate.

The current domain model contains entities representing concepts such as:

```text
Merchant
   │
   ├── ApiKey
   ├── Customer
   └── MerchantWebhookConfig

Payment Domain
   │
   ├── OrderRecord
   ├── Payment
   ├── PaymentTransactionLog
   └── Refund
```

The use of dedicated entities for payments and transaction logs allows payment processing history to be represented separately from the current payment state.

---

## Project Structure

```text
src/
└── main/
    ├── java/
    │   └── com/project/razorpay/
    │       │
    │       ├── common/
    │       │   ├── enums/
    │       │   └── exceptions/
    │       │
    │       ├── merchant/
    │       │   ├── controller/
    │       │   ├── dto/
    │       │   ├── entity/
    │       │   ├── mapper/
    │       │   ├── repository/
    │       │   ├── security/
    │       │   └── service/
    │       │
    │       ├── operations/
    │       │   └── entity/
    │       │
    │       ├── payment/
    │       │   ├── config/
    │       │   ├── controller/
    │       │   ├── dto/
    │       │   ├── entity/
    │       │   ├── gateway/
    │       │   │   └── adapter/
    │       │   ├── mapper/
    │       │   ├── processor/
    │       │   │   └── strategy/
    │       │   ├── repository/
    │       │   ├── service/
    │       │   ├── simulator/
    │       │   └── statemachine/
    │       │
    │       └── RazorpayApplication.java
    │
    └── resources/
        └── application.yaml
```

---

## Technology Stack

### Backend

* **Java 25**
* **Spring Boot 4**
* Spring Web MVC
* Spring Data JPA
* Spring Security
* Hibernate
* Maven

### Security

* JWT
* BCrypt
* API Key authentication

### Database

* PostgreSQL

### Development

* Docker
* Docker Compose
* Lombok
* MapStruct

---

## Getting Started

### Prerequisites

Make sure the following are installed:

* Java 25
* Maven
* Docker
* Docker Compose
* PostgreSQL

---

### Clone the Repository

```bash
git clone https://github.com/Diwakar38/Razorpay.git
cd Razorpay
```

---

### Start PostgreSQL

The project includes a Docker Compose configuration for PostgreSQL.

Start the database:

```bash
docker compose up -d
```

Check the container:

```bash
docker compose ps
```

The default development database configuration is:

```text
Host: localhost
Port: 5432
Database: Razorpay
Username: diwakar
```

For development, these values can be overridden using environment variables.

---

### Configure the Application

The application configuration is located at:

```text
src/main/resources/application.yaml
```

The database configuration supports environment-variable overrides:

```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/Razorpay}
    username: ${DB_USER:diwakar}
    password: ${DB_PW:diwakar}
```

For local development, configure your environment appropriately before starting the application.

> **Security:** Never use development JWT secrets, vault keys, database passwords, or API credentials in a production environment. Use environment variables or a dedicated secret-management solution.

---

### Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

Or build and run the application:

```bash
mvn clean package
java -jar target/razorpay-0.0.1-SNAPSHOT.jar
```

The application runs on:

```text
http://localhost:8000
```

---

## Development Status

This project is actively under development.

### Current Focus

* Merchant management
* Authentication and authorization
* Order processing
* Payment lifecycle
* Multiple payment methods
* Payment gateway abstraction
* Payment processing strategies
* Payment state machine
* Payment simulation
* Transaction logging

### Planned Improvements

Future development may include:

* Complete refund workflows
* Settlement processing
* Webhook delivery and retry mechanisms
* Idempotency support
* Payment reconciliation
* Improved failure handling
* Distributed processing
* Rate limiting
* Observability and metrics
* More realistic payment-provider integrations
* Comprehensive integration and end-to-end testing
* Production-grade secret management
* Containerized deployment

---

## Design Patterns Used

The project intentionally uses several design patterns that are useful when building extensible payment systems.

### Adapter Pattern

Payment gateway adapters isolate payment-method-specific integration details.

```text
PaymentAdapter
    ├── CardPaymentAdapter
    ├── UPIAdapter
    └── NetBankingAdapter
```

### Strategy Pattern

Payment processors provide different processing strategies for different payment methods.

```text
PaymentProcessor
    ├── CardPaymentProcessor
    ├── UpiPaymentProcessor
    └── NetBankingPaymentProcessor
```

### Router / Registry Pattern

Routers select the appropriate adapter or processor based on the payment method.

### State Machine Pattern

The payment state machine controls valid payment lifecycle transitions and prevents invalid state changes.

---

## Payment Lifecycle

The overall payment lifecycle can be represented as:

```text
Merchant
   │
   │ Create Order
   ▼
┌──────────────┐
│    Order     │
└──────┬───────┘
       │
       │ Initiate Payment
       ▼
┌──────────────┐
│   CREATED    │
└──────┬───────┘
       │
       │ Authorization
       ▼
┌──────────────┐
│ AUTHORIZING  │
└──────┬───────┘
       │
       │ Success
       ▼
┌──────────────┐
│  AUTHORIZED  │
└──────┬───────┘
       │
       │ Capture
       ▼
┌──────────────┐
│   CAPTURING  │
└──────┬───────┘
       │
       │ Success
       ▼
┌──────────────┐
│   CAPTURED   │
└──────┬───────┘
       │
       │ Settlement
       ▼
┌──────────────┐
│   SETTLED    │
└──────────────┘
```

Failures, cancellations, timeouts, and refunds are represented as separate transitions rather than being handled as simple boolean success/failure flags.

---

## Why This Project?

Payment systems are particularly interesting from a backend engineering perspective because correctness is often more important than simply returning a successful API response.

This project explores problems such as:

* How should a payment be represented?
* How do we prevent invalid payment state transitions?
* How can different payment methods share a common interface?
* How should gateway-specific logic be isolated?
* How can authentication identify the correct merchant?
* How should payment processing failures be simulated?
* How can payment transaction history be maintained?
* How can the system be extended to support additional payment methods?

The project is being built incrementally with these concerns in mind.

---

## Disclaimer

This is an independent learning project inspired by the architecture and concepts of modern payment gateways.

It is **not an official Razorpay implementation** and should not be used to process real financial transactions.

---

## Author

**Diwakar Arya**

GitHub: [@Diwakar38](https://github.com/Diwakar38)

---

## ⭐ Repository

[View the project on GitHub](https://github.com/Diwakar38/Razorpay)
