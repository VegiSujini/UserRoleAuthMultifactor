# Spring Boot JWT Authentication

This project demonstrates a Spring Boot application integrated with Spring Security for JWT authentication and role-based authorization.

## Features

- User registration with password encryption
- JWT-based authentication
- Role-based access control
- In-memory H2 database

## Getting Started

### End points
1. Register a New User :
     ```sh
    curl -X POST http://localhost:8080/register \
    -H "Content-Type: application/json" \
    -d '{
    "username": "user",
    "password": "password"
    }'
    
   Expected Response :
 
    {
    "id": 1,
    "username": "user",
    "password": "$2a$10$...",
    "roles": [
        {
        "id": 1,
        "name": "ROLE_USER"
        }
    ]
    }
2. Authenticate the User and Receive a JWT:
    ```sh
    curl -X POST http://localhost:8080/authenticate \
    -H "Content-Type: application/json" \
    -d '{
    "username": "user",
    "password": "password"
    }'

    Expected Response :

    {
    "token": "eyJhbGciOiJIUzUxMiJ9..."
    }
3. Access a Protected Resource Using the JWT : 
    ```sh
    curl -X GET http://localhost:8080/hello \
    -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."

    Expected Response :
    
    {
    "message": "Hello, user!"
    }




### Prerequisites

- Java 11 or later
- Maven

### Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/VegiSujini/userRoleAuth.git
   cd userRoleAuth
