# Vacation-Tracker

## Overview

Vacation Tracker is a microservices-based application designed to manage employee vacations. The project is built using Kotlin and Spring Boot, with two main services: `admin` and `employee`. These services are containerized using Docker and can be easily deployed using Docker Compose.

## Project Structure

- `admin/`: Contains the Admin service code.
- `employee/`: Contains the Employee service code.
- `docker-compose.yml`: Docker Compose file to orchestrate the services and PostgreSQL database.

## Prerequisites

Before you can run the application, make sure you have the following installed on your system:

- [Docker](https://www.docker.com/get-started)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

## Setting Up the Project

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/Nemanja5199/Vacation-Tracker.git
   cd vacation-tracker

2. **Navigate to project directory:**
   ```bash
   cd /path/to/vacation-tracker

## Build the Docker image
Before running the application, you need to build the Docker images for both services.
1. **Build the Docker Images:**
   ```bash
   docker-compose build
   
## Running the Application
1. **Start the Application:**
   ```bash
   docker-compose up -d
   
2. **Access the Services:**
   - `Admin Service/`: Available at [http://localhost:8082](http://localhost:8082)
   - `Employee Service/`: Available at [http://localhost:8080](http://localhost:8080)


3. **Stop the Application:**
    ```bash
      docker-compose down

## API Documentation
Swagger is integrated into the project to provide interactive API documentation.
1. **Accessing Swagger UI:**
- `Admin Service Swagger UI/`: Available at [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
- `Employee Service Swagger UI/`: Available at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)














   



