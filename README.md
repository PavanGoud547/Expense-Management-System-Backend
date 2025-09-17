# Expense Management System Backend

A comprehensive expense management system built with Spring Boot, featuring JWT authentication, role-based access control, and file upload capabilities.

## Features

### User Role
- User registration and login with JWT authentication
- Submit expenses with proof images
- View personal submitted expenses with status tracking

### Admin Role
- Login as administrator
- View all user expenses
- Update expense status (Pending → Paid)
- Filter and search expenses

## Technology Stack

- **Backend**: Spring Boot 3.5.5
- **Database**: MySQL 8.0
- **Authentication**: JWT (JSON Web Tokens)
- **Security**: Spring Security
- **File Upload**: Multipart file handling
- **Build Tool**: Maven
- **Java Version**: 17

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+

## Setup Instructions

### 1. Database Setup

Create a MySQL database named `expense_management_db`:

```sql
CREATE DATABASE expense_management_db;
```

Update the database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
```

### 2. Build the Application

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8081`

### 4. File Upload Directory

The application creates an `uploads/` directory in the project root for storing expense proof images.

## API Endpoints

### Authentication

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/auth/register` | Register new user/admin | Public |
| POST | `/auth/login` | Login and get JWT token | Public |

### Expense Management

| Method | Endpoint | Description | Access |
|--------|----------|-------------|---------|
| POST | `/expenses` | Submit new expense with image | USER |
| GET | `/expenses/my` | Get user's own expenses | USER |
| GET | `/expenses` | Get all expenses (admin view) | ADMIN |
| PUT | `/expenses/{id}/status` | Update expense status | ADMIN |

## API Usage Examples

### 1. Register a User
```bash
POST http://localhost:8081/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "roles": ["USER"]
}
```

### 2. Login
```bash
POST http://localhost:8081/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "user": {
    "id": 1,
    "name": "John Doe",
    "email": "john.doe@example.com",
    "roles": ["USER"]
  }
}
```

### 3. Submit Expense (with file upload)
```bash
POST http://localhost:8081/expenses
Authorization: Bearer <jwt_token>
Content-Type: multipart/form-data

Form Data:
- expenseName: "Office Lunch"
- price: "25.50"
- date: "2024-01-15"
- proofImage: [file]
```

### 4. Get User Expenses
```bash
GET http://localhost:8081/expenses/my
Authorization: Bearer <jwt_token>
```

### 5. Admin: Get All Expenses
```bash
GET http://localhost:8081/expenses
Authorization: Bearer <admin_jwt_token>
```

### 6. Admin: Update Expense Status
```bash
PUT http://localhost:8081/expenses/1/status
Authorization: Bearer <admin_jwt_token>
Content-Type: application/json

{
  "status": "PAID"
}
```

## Testing

Import the `Expense-Management-API.postman_collection.json` file into Postman to test all endpoints.

### Test Flow:
1. Register a user and admin
2. Login to get JWT tokens
3. Submit expenses as user
4. View expenses as user
5. View all expenses as admin
6. Update expense status as admin

## Security Features

- JWT-based authentication
- Role-based authorization (USER/ADMIN)
- Password encryption with BCrypt
- CSRF protection disabled for API endpoints
- Stateless session management

## File Upload

- Maximum file size: 5MB
- Supported formats: Images (JPEG, PNG, etc.)
- Files stored in `uploads/` directory
- File paths stored in database

## Error Handling

The application includes comprehensive error handling for:
- Authentication failures
- Authorization issues
- Validation errors
- File upload errors
- Database connection issues

## Database Schema

### Users Table
- id (Primary Key)
- name
- email (Unique)
- password (Encrypted)
- roles (ElementCollection)

### Expenses Table
- id (Primary Key)
- expense_name
- price
- date
- proof_image_path
- status (PENDING/PAID)
- user_id (Foreign Key)

## Configuration

Key configuration properties in `application.properties`:

```properties
# Server
server.port=8081

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/expense_management_db
spring.datasource.username=root
spring.datasource.password=your_password

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# File Upload
spring.servlet.multipart.max-file-size=5MB
spring.servlet.multipart.max-request-size=5MB

# JWT
jwt.secret=YourJWTSecretKeyHere
jwt.expiration=3600000
```

## Development Notes

- The application uses Spring Boot's auto-configuration
- Database tables are created automatically on startup
- JWT tokens expire after 1 hour (configurable)
- File uploads are stored locally (can be extended to cloud storage)

## Troubleshooting

1. **Port already in use**: Change `server.port` in application.properties
2. **Database connection failed**: Verify MySQL credentials and database existence
3. **JWT token expired**: Login again to get a new token
4. **File upload failed**: Check file size limits and directory permissions

## Future Enhancements

- Email notifications for expense approvals
- Cloud storage integration (AWS S3, Google Cloud Storage)
- Advanced filtering and search capabilities
- Expense categories and tags
- Reporting and analytics
- Mobile app API support
