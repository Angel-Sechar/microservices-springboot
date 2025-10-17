# 🧩 Campus E-Commerce Microservices

This project is a modular microservice architecture designed for an **E-Commerce platform** named *Campus*.  
It leverages **Spring Boot**, **Spring Cloud**, **Consul**, **Liquibase**, **Redis**, and **SQL Server** to deliver scalable, resilient, and maintainable backend services.

---

## 🚀 Overview

The system follows a **microservice architecture**, with each module encapsulating a specific business responsibility.  
The `microservice-parent` project provides centralized dependency management, build configuration, and shared components for all child modules.

### Main Modules

| Module | Description |
|--------|--------------|
| **auth-service** | Handles authentication and authorization using **JWT**, **Spring Security**, and **bcrypt**. |
| **api-gateway** | Central gateway that routes requests and manages inter-service communication through **Spring Cloud Gateway**. |
| **common** | Shared module containing constants, DTOs, utilities, and reusable classes across services. |

---

## 🧱 Tech Stack

| Layer | Technology |
|--------|-------------|
| **Framework** | Spring Boot 3.4.7 |
| **Language** | Java 21 |
| **Microservice Management** | Spring Cloud 2024.0.1 |
| **Service Discovery** | HashiCorp Consul |
| **API Gateway** | Spring Cloud Gateway |
| **Database** | Microsoft SQL Server |
| **ORM** | Spring Data JPA |
| **Migrations** | Liquibase |
| **Security** | JWT, Spring Security, bcrypt |
| **Cache** | Redis |
| **Mapping** | MapStruct 1.6.0 |
| **Testing** | JUnit, Testcontainers, ArchUnit |
| **Build Tool** | Maven |

---

## ⚙️ Features

- 🔑 **Authentication & Authorization** with JWT and Spring Security  
- 🧩 **Modular Architecture** using Maven multi-module setup  
- 🔄 **Database versioning** through Liquibase  
- 🧠 **Service Discovery and Configuration** powered by Consul  
- 🧰 **DTO Mapping** with MapStruct  
- 🧪 **Integration Testing** with Testcontainers  
- ♻️ **Centralized Dependency Management** in the parent POM  

---

## 📁 Project Structure

