# Agent Docs: sertifikat

Detail Endpoint untuk Frontend Developer

## GET `/api/sertifikat`
**Operation:** listSertifikat

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<List<SertifikatResponse>>>`
- Refer to ResponseEntity<APIResponse<List<SertifikatResponse>>>

---

## POST `/api/sertifikat`
**Operation:** uploadSertifikat

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<SertifikatResponse>>`
- `id`: UUID
- `periodeMagangId`: UUID
- `mahasiswaId`: UUID
- `nim`: String
- `namaMahasiswa`: String
- `url`: String
- `statusSertifikat`: String
- `createdAt`: LocalDateTime

---

## GET `/api/sertifikat/statistik`
**Operation:** getSertifikatStatistics

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<SertifikatStatResponse>>`
- `totalSertifikatDiunggah`: long
- `totalSertifikatBelumDiunggah`: long
- `totalJumlahSertifikat`: long

---

