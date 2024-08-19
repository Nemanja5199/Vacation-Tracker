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
   git clone https://github.com/your-username/vacation-tracker.git
   cd vacation-tracker
