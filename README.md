# Employee Creator Backend

A Play Framework (Scala) REST API for employee and contract management.

## 📦 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Setup & Running](#setup--running)
- [API Endpoints](#api-endpoints)
- [Architecture & Layers](#architecture--layers)
- [DTOs & Request Validation](#dtos--request-validation)
- [Testing](#testing)

---

## Overview

This project powers a backend system to manage employees and their contracts. It supports:

- Creating, updating, listing, and deleting employees
- Adding, updating, retrieving, and deleting employee contracts
- Listing contracts by employee

It follows MVC architecture, relies on Play's DI for wiring components, and uses Slick to interact with a MySQL database, managed with Play Evolutions.

---

## Tech Stack

- Scala 2.13
- Play Framework 2.8.x
- Slick + Slick-Evolutions
- MySQL
- Dependency Injection: Guice (built into Play)
- JSON parsing via Play JSON

---

## Features

- REST endpoints for CRUD operations on Employee and Contract models
- DTO-based requests/responses for safety
- Validation: required fields, enums, date ranges
- Transactional database operations (via Slick `.transactionally`)
- Error handling via an `ApiError` abstraction returning structured JSON errors
- Comprehensive test suite using ScalaTest + Play testing utilities

---

## Setup & Running

1. Clone the repo:

   ```bash
   git clone https://github.com/indymoorcroft/employee-creator-be.git
   cd employee-creator-be

2. Configure `conf/application.conf` with your MySQL credentials.

   ```bash
   # DB config
   slick.dbs.default.profile = ""
   slick.dbs.default.db.driver = ""
   slick.dbs.default.db.url = ""
   slick.dbs.default.db.user = ""
   slick.dbs.default.db.password = ""

   # Evolutions
   play.evolutions.db.default.autoApply = true
   play.evolutions.enabled = true
   play.evolutions.autoApplyDowns = true
   
   play.modules.enabled += "core.Module"

3. Evolutions will run automatically on startup.

4. Start the app:

   ```bash
   sbt run

## API Server

The API server will run at:  
**http://localhost:9000**

---

## API Endpoints

### Employees

- `GET    /employees` — List all employees
- `GET    /employees/:id` — Get a single employee
- `POST   /employees` — Create a new employee
- `PATCH  /employees/:id` — Update employee fields
- `DELETE /employees/:id` — Delete an employee

### Contracts

- `POST   /employees/:id/contracts` — Add a contract to an employee
- `GET    /employees/:id/contracts` — List all contracts for an employee
- `GET    /contracts/:id` — Get a single contract
- `PATCH  /contracts/:id` — Update a contract
- `DELETE /contracts/:id` — Delete a contract

---

## Architecture & Layers

### Controller

- Handles HTTP requests
- Converts JSON payloads into DTOs
- Orchestrates service layer logic
- Returns JSON responses or structured error objects

### Service

- Contains domain/business logic
- Performs validation
- Maps DTOs to and from domain models

### Repository

- Manages database operations using Slick
- Provides functions like `findById`, `create`, `update`, `delete`
- Executes DBIO operations with `db.run(...)`
- Uses `.transactionally` for grouped DB updates

---

## DTOs & Request Validation

**DTOs (Data Transfer Objects)** are used to:

- Accept incoming JSON payloads
- Return consistent structured responses

### Validation Rules:

- Required fields are enforced on creation
- Enum fields (`contractType`, `employmentType`) must have valid values
- PATCH supports partial/optional field validation
- Logical date checks (e.g., `endDate` must not precede `startDate`)
- Contracts cannot overlap for the same employee

❗ **Invalid input returns a `400 Bad Request`** with descriptive error messages.

---

## Testing

- Uses **ScalaTest** with `GuiceOneAppPerTest` for integration testing
- Includes a `CleanDatabase` trait to reset the database between tests

### Test Coverage Includes:

- Valid and invalid `POST`, `PATCH`, `GET`, and `DELETE` requests
- `404 Not Found` for missing resources
- Validation rule enforcement 
