Spring Boot E-Commerce Platform

A backend e-commerce application built using Java, Spring Boot, Spring Data JPA, PostgreSQL, and Docker. The project follows a microservices architecture, with separate services for user management, product management, shopping carts, and order processing.

Project Architecture

The platform currently contains four independent microservices.

Microservice

Port

Database

User Service

8082

user_db

Product Service

8083

product_db

Cart Service

8084

cart_db

Order Service

8085

order_db

Technology Stack

Java

Spring Boot

Spring Data JPA

Hibernate

PostgreSQL

Maven

Docker

Docker Compose

REST APIs

Postman

Project Structure

ecommerce-platform/
├── microservices/
│   ├── user-service/
│   ├── product-service/
│   ├── cart-service/
│   └── order-service/
├── monolithic/
│   └── eco-application/
├── infrastructure/
│   ├── docker/
│   └── database/
├── docker-compose.yml
├── .gitignore
└── README.md

Database Configuration

The project uses PostgreSQL with separate databases for each microservice:

user_db

product_db

cart_db

order_db

Configure the database password through a local .env file:

DB_PASSWORD=your_postgresql_password

The .env file is excluded from Git.

Running the Project

1. Clone the repository

git clone https://github.com/YOUR_USERNAME/spring-boot-ecommerce-microservices.git
cd spring-boot-ecommerce-microservices

2. Configure environment variables

Create a .env file:

DB_PASSWORD=your_postgresql_password

3. Start the microservices

Ensure PostgreSQL is running and accessible using the hostname configured in the service properties.

docker compose up -d --build

4. Check running containers

docker compose ps

Current Implementation

Separate Spring Boot microservices

REST API endpoints

PostgreSQL database integration

Independent database for each service

Dockerfiles for individual services

Docker Compose configuration

JPA and Hibernate entity persistence

Future Enhancements

API Gateway

Service Discovery

Inter-service communication

Payment Service

Spring Boot Actuator

Centralized configuration

Authentication and authorization improvements

Author

Shubham Sajjan

Java Developer | Spring Boot Developer