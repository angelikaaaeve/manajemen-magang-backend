# Agent Docs: iam

Detail Endpoint untuk Frontend Developer

## POST `/api/iam/register`
**Operation:** register

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<UserResponse>>`
- `id`: UUID
- `email`: String
- `role`: String
- `nim`: String
- `nama`: String
- `noHp`: String
- `gender`: Gender
- `universitas`: String

---

## POST `/api/iam/login`
**Operation:** login

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<LoginResponse>>`
- `accessToken`: String

---

## POST `/api/iam/logout`
**Operation:** logout

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<Void>>`
- Refer to ResponseEntity<APIResponse<Void>>

---

## GET `/api/iam/me`
**Operation:** me

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<UserResponse>>`
- `id`: UUID
- `email`: String
- `role`: String
- `nim`: String
- `nama`: String
- `noHp`: String
- `gender`: Gender
- `universitas`: String

---

## PUT `/api/iam/update`
**Operation:** update

### Request Structure
**Type:** `UpdateUserRequest`
- `email`: String
- `nim`: String
- `nama`: String
- `noHp`: String
- `gender`: Gender
- `universitas`: String

### Response Structure
**Type:** `ResponseEntity<APIResponse<UserResponse>>`
- `id`: UUID
- `email`: String
- `role`: String
- `nim`: String
- `nama`: String
- `noHp`: String
- `gender`: Gender
- `universitas`: String

---

