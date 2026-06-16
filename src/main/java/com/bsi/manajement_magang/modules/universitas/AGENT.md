# Agent Docs: universitas

Detail Endpoint untuk Frontend Developer

## POST `/api/universitas`
**Operation:** addUniversitas

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<UniversitasResponse>>`
- `id`: Long
- `nameUniversity`: String
- `createdAt`: LocalDateTime

---

## GET `/api/universitas`
**Operation:** listUniversitas

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<List<UniversitasResponse>>>`
- Refer to ResponseEntity<APIResponse<List<UniversitasResponse>>>

---

## PUT `/api/universitas/{id}`
**Operation:** editUniversitas

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<UniversitasResponse>>`
- `id`: Long
- `nameUniversity`: String
- `createdAt`: LocalDateTime

---

## DELETE `/api/universitas/{id}`
**Operation:** deleteUniversitas

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<Void>>`
- Refer to ResponseEntity<APIResponse<Void>>

---

