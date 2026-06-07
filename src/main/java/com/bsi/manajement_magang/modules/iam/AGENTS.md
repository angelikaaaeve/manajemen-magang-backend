# IAM (Identity & Access Management) API Documentation

This module manages user credentials, JWT authentications, profiles, registration, and role allocations.

## 📂 Code Files
- Controller: [IamController.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/iam/presentation/IamController.java)
- Service: [IamService.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/iam/application/IamService.java)
- Repository: [UserRepositoryImpl.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/iam/infrastructure/UserRepositoryImpl.java)
- DTO Schemas:
  - [RegisterRequest.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/iam/presentation/request/RegisterRequest.java)
  - [LoginRequest.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/iam/presentation/request/LoginRequest.java)
  - [UpdateUserRequest.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/iam/presentation/request/UpdateUserRequest.java)
  - [UserResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/iam/application/response/UserResponse.java)
  - [LoginResponse.java](file:///c:/Users/LENOVO1/Documents/PROJECT%20SKRIPSI/manajement_magang/src/main/java/com/bsi/manajement_magang/modules/iam/application/response/LoginResponse.java)

---

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `POST` | `/api/iam/register` | No | Registers a new user account (`admin` | `mahasiswa` | `mentor`). |
| `POST` | `/api/iam/login` | No | Authenticates a user and returns a session JWT. |
| `GET` | `/api/iam/me` | Yes | Retrieves the profile details of the currently logged-in user. |
| `PUT` | `/api/iam/update` | Yes | Updates profile details (email, NIM, nama, noHp) of the logged-in user. |

---

## 📋 Detailed Endpoints & Payloads

### 1. User Registration
Creates a new account in the system.

- **URL:** `/api/iam/register`
- **Method:** `POST`
- **Request Payload (`RegisterRequest`):**
  - **Type:** JSON
  - **Structure:**
    ```json
    {
      "email": "budi.santoso@student.ui.ac.id", // Required, unique
      "password": "securepassword123", // Required, min length 6
      "role": "mahasiswa", // Options: "admin" | "mahasiswa" | "mentor" (case-insensitive)
      "nim": "2201012001", // Optional (Required if role is "mahasiswa")
      "nama": "Budi Santoso", // Optional
      "noHp": "081234567890" // Optional
    }
    ```
- **SQL Queries Executed:**
  1. Saves user record:
     ```sql
     INSERT INTO "user" (id, email, password, role, is_active, created_at, updated_at) 
     VALUES (:id, :email, :password, :role, true, NOW(), NOW())
     ```
  2. Saves sub-profile depending on role:
     - If role is `mahasiswa`:
       ```sql
       INSERT INTO mahasiswa (id, user_id, nim, nama, no_hp, gender, universitas) 
       VALUES (:id, :userId, :nim, :nama, :noHp, :gender, :universitas)
       ```
     - If role is `mentor`:
       ```sql
       INSERT INTO mentor (id, user_id, nama) 
       VALUES (:id, :userId, :nama)
       ```
- **Response Payload (`UserResponse` - HTTP 200 OK):**
  ```json
  {
    "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", // UUID
    "email": "budi.santoso@student.ui.ac.id",
    "role": "mahasiswa", // "admin" | "mahasiswa" | "mentor"
    "nim": "2201012001", // Null for other roles
    "nama": "Budi Santoso",
    "noHp": "081234567890" // Null for admin role
  }
  ```

---

### 2. User Login
Authenticates a user and issues a JWT token.

- **URL:** `/api/iam/login`
- **Method:** `POST`
- **Request Payload (`LoginRequest`):**
  ```json
  {
    "email": "budi.santoso@student.ui.ac.id",
    "password": "securepassword123"
  }
  ```
- **SQL Query Executed:**
  ```sql
  SELECT id, email, password, role, is_active FROM "user" WHERE email = :email
  ```
- **Response Payload (`LoginResponse` - HTTP 200 OK):**
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." // Bearer token
  }
  ```

---

### 3. Get Current User ("Me")
Retrieves session details using the Bearer token.

- **URL:** `/api/iam/me`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **SQL Queries Executed:**
  1. Finds user by ID:
     ```sql
     SELECT id, email, password, role, is_active FROM "user" WHERE id = :id
     ```
  2. Finds profile based on role:
     - If role is `mahasiswa`:
       ```sql
       SELECT id, user_id, nim, nama, no_hp, gender, universitas FROM mahasiswa WHERE user_id = :userId
       ```
     - If role is `mentor`:
       ```sql
       SELECT id, user_id, nama FROM mentor WHERE user_id = :userId
       ```
- **Response Payload (`UserResponse` - HTTP 200 OK):**
  ```json
  {
    "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "email": "budi.santoso@student.ui.ac.id",
    "role": "mahasiswa",
    "nim": "2201012001",
    "nama": "Budi Santoso",
    "noHp": "081234567890"
  }
  ```

---

### 4. Update Profile
Allows users to update their profile parameters.

- **URL:** `/api/iam/update`
- **Method:** `PUT`
- **Headers:** `Authorization: Bearer <token>`
- **Request Payload (`UpdateUserRequest`):**
  ```json
  {
    "email": "budi.santoso.new@student.ui.ac.id",
    "nim": "2201012001",
    "nama": "Budi Santoso",
    "noHp": "08999888777"
  }
  ```
- **SQL Queries Executed:**
  1. Updates base user email:
     ```sql
     UPDATE "user" SET email = :email WHERE id = :id
     ```
  2. Updates role-specific profile:
     - If role is `mahasiswa`:
       ```sql
       UPDATE mahasiswa SET nim = :nim, nama = :nama, no_hp = :noHp WHERE user_id = :userId
       ```
     - If role is `mentor`:
       ```sql
       UPDATE mentor SET nama = :nama WHERE user_id = :userId
       ```
- **Response Payload (`UserResponse` - HTTP 200 OK):**
  ```json
  {
    "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "email": "budi.santoso.new@student.ui.ac.id",
    "role": "mahasiswa",
    "nim": "2201012001",
    "nama": "Budi Santoso",
    "noHp": "08999888777"
  }
  ```
