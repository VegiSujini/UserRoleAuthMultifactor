# User Role-Based Authentication with OTP Verification

This project is a Spring Boot application that implements user role-based authentication with JWT (JSON Web Token) and OTP (One-Time Password) verification. The application includes endpoints for user registration, authentication, and OTP verification.

## Prerequisites

- Java 11 or higher
- Maven
- Spring Boot
- H2
- A Gmail account with an [App Password](https://myaccount.google.com/u/2/apppasswords?pli=1&rapt=AEjHL4O9OnKOoFfGPULaZVjElff0-E0xeBQrln_lXcWIqkkbRq_4vWAhJpvLnYSKE4b9HWBDTotQWoG_nFgcWtIThwS3Zkht4Vc1GtFNyKSJ6hBYSYY6jl4) for sending OTPs

## Getting Started

### End points
1. Register a New User :
     ```sh
    curl -X POST http://localhost:8080/register \
    -H "Content-Type: application/json" \
    -d '{
    "username": "your-email@gmail.com",
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
    "username": "your-email@gmail.com",
    "password": "password"
    }'

    Expected Response :

    {
    "token": "eyJhbGciOiJIUzUxMiJ9..."
    }
3. Access a Protected Resource Using the JWT : 
    ```sh
    curl -X POST http://localhost:8080/verifyOtp \
    --header 'Content-Type: application/json' \
    --header 'Authorization: Bearer eyJhbGciOiJIUzUxMi......' \
    --data-raw '{
        "otp": "322560"
    }'

    Expected Response :
    
    {
    "message": "OTP Verified"
    }


## Setup
**Clone the repository**:

   ```bash
   git clone https://github.com/VegiSujini/UserRoleAuthMultifactor.git
   cd UserRoleAuthMultifactor

