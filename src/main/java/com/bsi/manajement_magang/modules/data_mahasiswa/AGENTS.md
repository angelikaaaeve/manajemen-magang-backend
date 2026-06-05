# Kelola Mahasiswa (Student Administration) API Documentation

This module manages student rosters, administrative fields, and academic/industry internship period details.

## 🚀 Endpoints Summary

| Method | Endpoint | Auth Required | Description |
|---|---|---|---|
| `POST` | `/api/mahasiswa` | Yes | Registers/adds a new student along with their initial internship period. |
| `PUT` | `/api/mahasiswa/{id}` | Yes | Modifies student identity and/or updates their active internship period. |
| `GET` | `/api/mahasiswa` | Yes | Queries student rosters with filters for gender, university, or status. |
| `GET` | `/api/mahasiswa/statistik` | Yes | Obtains statistical counters of student counts by active status. |
| `GET` | `/api/mahasiswa/{id}` | Yes | Retrieves full profile details for a specific student. |

---

## 📋 Detailed Endpoints & Payloads

### 1. Add Student
Admin adds a new student into the system.

- **URL:** `/api/mahasiswa`
- **Method:** `POST`
- **Headers:** `Authorization: Bearer <token>`, `Content-Type: application/json`
- **Request Payload (`StudentRequest`):**
```json
{
  "email": "budi.santoso@student.ui.ac.id",
  "password": "temporarysecurepassword",
  "nim": "2201012001",
  "nama": "Budi Santoso",
  "noHp": "081234567890",
  "gender": "Laki-laki", // must match: "Laki-laki" | "Perempuan"
  "universitas": "Universitas Indonesia",
  "tanggalMulai": "2026-02-01", // yyyy-MM-dd
  "tanggalBerakhir": "2026-07-31", // yyyy-MM-dd
  "periodeStatus": "aktif" // e.g. "aktif" | "selesai"
}
```
- **Response Payload (`StudentResponse` - HTTP 201 Created):**
```json
{
  "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6", // Student ID (UUID)
  "userId": "9e248b11-236b-4ac4-8bde-d23a6f1ea124", // IAM User ID (UUID)
  "email": "budi.santoso@student.ui.ac.id",
  "nim": "2201012001",
  "nama": "Budi Santoso",
  "noHp": "081234567890",
  "gender": "Laki-laki",
  "universitas": "Universitas Indonesia",
  "periodeId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81",
  "tanggalMulai": "2026-02-01",
  "tanggalBerakhir": "2026-07-31",
  "statusPeriode": "aktif",
  "mentorId": "3cb1ab0d-4ea3-4cfb-81d0-d3cdb2413e11",
  "namaMentor": "Dr. Ahmad Hidayat, M.T."
}
```

---

### 2. Update Student Profile & Period
Admin modifies the profile or active internship period details of an existing student.

- **URL:** `/api/mahasiswa/{id}`
- **Method:** `PUT`
- **Headers:** `Authorization: Bearer <token>`, `Content-Type: application/json`
- **Path Parameter:**
  - `id` (UUID): Unique student ID
- **Request Payload (`UpdateStudentRequest`):**
```json
{
  "email": "budi.santoso@student.ui.ac.id",
  "nim": "2201012001",
  "nama": "Budi Santoso",
  "noHp": "081234567890",
  "gender": "Laki-laki",
  "universitas": "Universitas Indonesia",
  "periode": {
    "tanggalMulai": "2026-02-01",
    "tanggalBerakhir": "2026-08-31", // Extended period
    "status": "aktif"
  }
}
```
- **Response Payload (`StudentResponse` - HTTP 200 OK):**
```json
{
  "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
  "userId": "9e248b11-236b-4ac4-8bde-d23a6f1ea124",
  "email": "budi.santoso@student.ui.ac.id",
  "nim": "2201012001",
  "nama": "Budi Santoso",
  "noHp": "081234567890",
  "gender": "Laki-laki",
  "universitas": "Universitas Indonesia",
  "periodeId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81",
  "tanggalMulai": "2026-02-01",
  "tanggalBerakhir": "2026-08-31",
  "statusPeriode": "aktif",
  "mentorId": "3cb1ab0d-4ea3-4cfb-81d0-d3cdb2413e11",
  "namaMentor": "Dr. Ahmad Hidayat, M.T."
}
```

---

### 3. List Students
Retrieve list of registered students in the system.

- **URL:** `/api/mahasiswa`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters:**
  - `gender` (String, optional): Filter by `Laki-laki` | `Perempuan`
  - `universitas` (String, optional): Filter by university name (case-insensitive substring)
  - `status` (String, optional): Filter by period status (`aktif` | `selesai`)
- **Response Payload (`List<StudentResponse>` - HTTP 200 OK):**
```json
[
  {
    "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
    "userId": "9e248b11-236b-4ac4-8bde-d23a6f1ea124",
    "email": "budi.santoso@student.ui.ac.id",
    "nim": "2201012001",
    "nama": "Budi Santoso",
    "noHp": "081234567890",
    "gender": "Laki-laki",
    "universitas": "Universitas Indonesia",
    "periodeId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81",
    "tanggalMulai": "2026-02-01",
    "tanggalBerakhir": "2026-07-31",
    "statusPeriode": "aktif",
    "mentorId": "3cb1ab0d-4ea3-4cfb-81d0-d3cdb2413e11",
    "namaMentor": "Dr. Ahmad Hidayat, M.T."
  }
]
```

---

### 4. Student Roster Statistics
Retrieve cumulative counts of active and completed students.

- **URL:** `/api/mahasiswa/statistik`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Query Parameters (Optional):**
  - `gender` (String): Filter statistics by gender
  - `universitas` (String): Filter statistics by university name
- **Response Payload (`StudentStatResponse` - HTTP 200 OK):**
```json
{
  "totalAktif": 145,
  "totalSelesai": 32,
  "totalAktifTanpaPenilaian": 18
}
```

---

### 5. Student Detail
Gets a student profile by their unique student ID.

- **URL:** `/api/mahasiswa/{id}`
- **Method:** `GET`
- **Headers:** `Authorization: Bearer <token>`
- **Path Parameter:**
  - `id` (UUID): Unique student ID
- **Response Payload (`StudentResponse` - HTTP 200 OK):**
```json
{
  "id": "f81d4fae-7dec-11d0-a765-00a0c91e6bf6",
  "userId": "9e248b11-236b-4ac4-8bde-d23a6f1ea124",
  "email": "budi.santoso@student.ui.ac.id",
  "nim": "2201012001",
  "nama": "Budi Santoso",
  "noHp": "081234567890",
  "gender": "Laki-laki",
  "universitas": "Universitas Indonesia",
  "periodeId": "5c1a8d9b-2e9c-4aa4-8f7b-23fcd10d9e81",
  "tanggalMulai": "2026-02-01",
  "tanggalBerakhir": "2026-07-31",
  "statusPeriode": "aktif",
  "mentorId": "3cb1ab0d-4ea3-4cfb-81d0-d3cdb2413e11",
  "namaMentor": "Dr. Ahmad Hidayat, M.T."
}
```
