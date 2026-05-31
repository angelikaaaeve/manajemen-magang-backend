# IAM (Identity & Access Management) API Documentation

This module manages user credentials, JWT authentications, profiles, registration, and role allocations.

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `POST` | `/api/iam/register` | No | Registers a new user account (Mahasiswa/Mentor/Super Admin). |
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
```json
{
  "email": "budi.santoso@student.ui.ac.id",
  "password": "securepassword123", // Min length 6
  "role": "Mahasiswa", // Options: "Mahasiswa" | "Mentor" | "Super Admin"
  "nim": "2201012001", // Optional for other roles
  "nama": "Budi Santoso", // Optional
  "noHp": "081234567890" // Optional
}
```
- **Response Payload (`UserResponse` - HTTP 200 OK):**
```json
{
  "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", // UUID
  "email": "budi.santoso@student.ui.ac.id",
  "role": "Mahasiswa",
  "nim": "2201012001",
  "nama": "Budi Santoso",
  "noHp": "081234567890"
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
- **Response Payload (`LoginResponse` - HTTP 200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", // Bearer token
  "email": "budi.santoso@student.ui.ac.id",
  "role": "Mahasiswa"
}
```

---

### 3. Get Current User ("Me")
Retrieves session details using the Bearer token.

- **URL:** `/api/iam/me`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Response Payload (`UserResponse` - HTTP 200 OK):**
```json
{
  "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
  "email": "budi.santoso@student.ui.ac.id",
  "role": "Mahasiswa",
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
- **Response Payload (`UserResponse` - HTTP 200 OK):**
```json
{
  "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
  "email": "budi.santoso.new@student.ui.ac.id",
  "role": "Mahasiswa",
  "nim": "2201012001",
  "nama": "Budi Santoso",
  "noHp": "08999888777"
}
```
