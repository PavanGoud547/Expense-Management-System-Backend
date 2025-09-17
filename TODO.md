# Expense Management System Implementation TODO

## Phase 1: Configuration and Setup
- [x] Update application.properties with MySQL and JWT configuration
- [x] Create Role enum for USER/ADMIN roles

## Phase 2: Entity Layer
- [x] Create User entity with proper validations and relationships
- [x] Create Expense entity with validations and file path handling

## Phase 3: Data Access Layer
- [x] Create UserRepository interface
- [x] Create ExpenseRepository interface

## Phase 4: Security Layer
- [x] Create JwtUtil utility class for token operations
- [x] Create SecurityConfig for JWT authentication and authorization
- [x] Create UserDetailsServiceImpl for loading user details

## Phase 5: Service Layer
- [x] Create AuthService for registration and login logic
- [x] Create ExpenseService for expense management logic

## Phase 6: Controller Layer
- [x] Create AuthController for authentication endpoints
- [x] Create ExpenseController for expense management endpoints

## Phase 7: Exception Handling
- [x] Create GlobalExceptionHandler for centralized error handling

## Phase 8: Testing and Verification
- [x] Set up MySQL database (expense_management_db)
- [x] Test all endpoints functionality
- [x] Verify file upload and storage
- [x] Run the application and check database connectivity
- [x] Create Postman collection for API testing
