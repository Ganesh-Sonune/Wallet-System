# 🪙 Wallet System with Idempotency & Concurrency

A secure digital wallet backend system similar to **Paytm / PhonePe**, built using **Spring Boot**.  
The system ensures **financial consistency**, **idempotent APIs**, **concurrency safety**, and **JWT-based security**.

---

## 📌 Overview

Each user owns exactly **one wallet**.  
Wallet balance must always remain consistent, even under:
- Concurrent requests
- Duplicate API calls (retries)

This project demonstrates real-world backend design for financial systems.

---

## 🚀 Features

- User & Admin roles
- One wallet per user
- Add money & transfer money
- Idempotent money APIs
- Atomic wallet transfers
- Optimistic locking for concurrency
- JWT authentication & authorization
- Transaction history with pagination
- Admin system-wide visibility

---

## 🏗️ Tech Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- H2 Database
- JWT (jjwt)
- Maven

---

## 🧩 Domain Model

### User
- id
- email
- password
- role (USER, ADMIN)

### Wallet
- id
- user
- balance
- version (optimistic locking)

### Transaction
- id
- fromWallet
- toWallet
- amount
- type (ADD, TRANSFER)
- status (SUCCESS, FAILED)
- idempotencyKey
- createdAt

---

## 🔐 Security Implementation

- JWT-based authentication using `Authorization: Bearer <token>`
- Passwords encrypted using **BCrypt**
- Role-based access control:
  - USER → wallet APIs
  - ADMIN → admin APIs
- Wallet ID is derived from authenticated user
- Wallet ID is never accepted from request parameters
- All APIs are secured using Spring Security

---

## 👥 Roles & Permissions

### USER
- Owns exactly one wallet
- Can add money
- Can transfer money
- Can view wallet balance
- Can view transaction history

### ADMIN
- Can view all wallets
- Can view all transactions
- Cannot perform wallet operations

---

## 🔁 Idempotency Handling

- Money APIs require an `Idempotency-Key` header
- Idempotency key is stored in the `Transaction` table
- Duplicate requests with the same key:
  - Are not reprocessed
  - Return the existing transaction result
- Enforced at the **database level**

---

## ⚙️ Concurrency Strategy

- Optimistic locking using `@Version` in `Wallet` entity
- Prevents lost updates under concurrent requests
- No in-memory locks or synchronized blocks used
- Database ensures wallet balance consistency

---

## 🔒 Transaction Management

- Critical operations use `@Transactional`
- Wallet transfers update:
  - Sender wallet
  - Receiver wallet
  - Transaction record
- All operations are **atomic**
- Failures cause full rollback

---

## 📡 API Endpoints

### Authentication
- `POST /auth/register`
- `POST /auth/login`

### Wallet APIs (USER)
- `POST /wallet/add`
- `POST /wallet/transfer`
- `GET /wallet`
- `GET /wallet/transactions`

### Admin APIs (ADMIN)
- `GET /admin/wallets`
- `GET /admin/transactions`

---

## 🗃️ Database

- H2 in-memory database
- Persistent transaction history
- Optimistic locking enabled
- Suitable for local development

---

## ▶️ How to Run Locally

### Prerequisites
- Java 17+
- Maven
- IDE (IntelliJ recommended)

### Steps

```bash
git clone <your-github-repo-url>
cd wallet
./mvnw spring-boot:run
