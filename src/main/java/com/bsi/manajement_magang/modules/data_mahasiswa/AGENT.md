# Agent Docs: data_mahasiswa

Detail Endpoint untuk Frontend Developer

## POST `/api/mahasiswa`
**Operation:** addStudent

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<StudentResponse>>`
- `id`: UUID
- `userId`: UUID
- `email`: String
- `nim`: String
- `nama`: String
- `noHp`: String
- `gender`: Gender
- `idUniversity`: Long
- `universitas`: String
- `periodeId`: UUID
- `tanggalMulai`: LocalDate
- `tanggalBerakhir`: LocalDate
- `statusPeriode`: StatusPeriode
- `mentorId`: UUID
- `namaMentor`: String

---

## PUT `/api/mahasiswa/edit-by-mentor/{id}`
**Operation:** editStudent

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<StudentResponse>>`
- `id`: UUID
- `userId`: UUID
- `email`: String
- `nim`: String
- `nama`: String
- `noHp`: String
- `gender`: Gender
- `idUniversity`: Long
- `universitas`: String
- `periodeId`: UUID
- `tanggalMulai`: LocalDate
- `tanggalBerakhir`: LocalDate
- `statusPeriode`: StatusPeriode
- `mentorId`: UUID
- `namaMentor`: String

---

## GET `/api/mahasiswa`
**Operation:** listStudents

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<List<StudentResponse>>>`
- Refer to ResponseEntity<APIResponse<List<StudentResponse>>>

---

## GET `/api/mahasiswa/statistik`
**Operation:** getStudentStatistics

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<StudentStatResponse>>`
- `totalAktif`: long
- `totalSelesai`: long
- `totalAktifTanpaPenilaian`: long

---

## GET `/api/mahasiswa/{id}`
**Operation:** getStudentDetail

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<StudentResponse>>`
- `id`: UUID
- `userId`: UUID
- `email`: String
- `nim`: String
- `nama`: String
- `noHp`: String
- `gender`: Gender
- `idUniversity`: Long
- `universitas`: String
- `periodeId`: UUID
- `tanggalMulai`: LocalDate
- `tanggalBerakhir`: LocalDate
- `statusPeriode`: StatusPeriode
- `mentorId`: UUID
- `namaMentor`: String

---

## GET `/api/mahasiswa/sisa-waktu-magang`
**Operation:** getSisaWaktuMagang

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<Long>>`
- Refer to ResponseEntity<APIResponse<Long>>

---

