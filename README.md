# Spring Boot Authentication System

A complete JWT-based authentication system built with Spring Boot 3, Spring Security 6, and H2 database.

## Features

- ✅ User registration and login
- ✅ JWT token-based authentication
- ✅ Password encryption with BCrypt
- ✅ Role-based access control (USER, ADMIN)
- ✅ Protected API endpoints
- ✅ H2 in-memory database
- ✅ Comprehensive error handling
- ✅ Input validation
- ✅ Unit and integration tests
- ✅ RESTful API design

## Technology Stack

- **Java 17**
- **Spring Boot 3.5.3**
- **Spring Security 6**
- **Spring Data JPA**
- **H2 Database**
- **JWT (JSON Web Tokens)**
- **Gradle**
- **JUnit 5**
- **Mockito**

## Quick Start

### Prerequisites

- Java 17 or higher
- Gradle 7.0 or higher

### Running the Application

1. Clone the repository:
```bash
git clone https://github.com/Tomlee-abila/authdemo.git
cd authdemo
```

2. Run the application:
```bash
./gradlew bootRun
```

3. The application will start on `http://localhost:8080`

4. Access H2 Console (development only): `http://localhost:8080/h2-console`
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: `password`

## API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "confirmPassword": "password123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "role": "USER",
    "expiresIn": 86400000
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

#### Login User
```http
POST /api/auth/login
Content-Type: application/json

{
  "usernameOrEmail": "johndoe",
  "password": "password123"
}
```

**Response:** Same as registration response

#### Validate Token
```http
POST /api/auth/validate?token=your-jwt-token
```

#### Get Current User
```http
GET /api/auth/me
Authorization: Bearer your-jwt-token
```

### User Management Endpoints (Protected)

#### Get User Profile
```http
GET /api/users/profile
Authorization: Bearer your-jwt-token
```

#### Get All Users (Admin Only)
```http
GET /api/users/all
Authorization: Bearer admin-jwt-token
```

#### Get User by ID (Admin Only)
```http
GET /api/users/{id}
Authorization: Bearer admin-jwt-token
```

#### Enable/Disable User (Admin Only)
```http
PUT /api/users/{id}/enabled?enabled=true
Authorization: Bearer admin-jwt-token
```

#### Delete User (Admin Only)
```http
DELETE /api/users/{id}
Authorization: Bearer admin-jwt-token
```

## Testing

### Run All Tests
```bash
./gradlew test
```

### Run Specific Test Class
```bash
./gradlew test --tests UserServiceTest
```

### Test Coverage
The project includes comprehensive tests for:
- Service layer (UserService, AuthService)
- Utility classes (JwtUtil)
- Controllers (AuthController, UserController)
- Integration tests

## Configuration

### Application Properties
Key configuration options in `application.properties`:

```properties
# JWT Configuration
jwt.secret=your-secret-key-here
jwt.expiration=86400000  # 24 hours in milliseconds

# Database Configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=password

# Server Configuration
server.port=8080
```

### Security Configuration
- JWT tokens expire in 24 hours (configurable)
- Passwords are encrypted using BCrypt
- CORS enabled for all origins (configure for production)
- H2 console enabled for development

## Project Structure

```
src/
├── main/
│   ├── java/com/example/demo/
│   │   ├── config/          # Security configuration
│   │   ├── controller/      # REST controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA entities
│   │   ├── repository/     # Data repositories
│   │   ├── security/       # Security components
│   │   ├── service/        # Business logic
│   │   └── util/           # Utility classes
│   └── resources/
│       └── application.properties
└── test/                   # Unit and integration tests
```

## Error Handling

The API returns consistent error responses:

```json
{
  "success": false,
  "message": "Error description",
  "timestamp": "2024-01-01T12:00:00"
}
```

Common HTTP status codes:
- `200` - Success
- `201` - Created (registration)
- `400` - Bad Request (validation errors)
- `401` - Unauthorized (invalid credentials/token)
- `403` - Forbidden (insufficient permissions)
- `404` - Not Found

## Development Notes

### Creating Admin User
Currently, all users are created with USER role. To create an admin user, you can:
1. Register a normal user
2. Manually update the role in H2 console
3. Or extend the registration endpoint to accept role parameter

### Production Considerations
- Change JWT secret to a secure random value
- Use a production database (PostgreSQL, MySQL)
- Configure proper CORS origins
- Disable H2 console
- Add rate limiting
- Implement refresh tokens
- Add email verification
- Configure HTTPS

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.
