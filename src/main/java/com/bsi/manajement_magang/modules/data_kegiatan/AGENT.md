# Agent Docs: data_kegiatan

Detail Endpoint untuk Frontend Developer

## GET `/api/kegiatan`
**Operation:** listActivities

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<List<ActivityResponse>>>`
- Refer to ResponseEntity<APIResponse<List<ActivityResponse>>>

---

## PUT `/api/kegiatan/{id}/status`
**Operation:** updateStatus

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<ActivityResponse>>`
- `id`: UUID
- `mahasiswaId`: UUID
- `namaMahasiswa`: String
- `judul`: String
- `deskripsi`: String
- `waktu`: OffsetDateTime
- `fileUrl`: String
- `status`: StatusKegiatan

---

## DELETE `/api/kegiatan/{id}`
**Operation:** deleteActivity

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<Void>>`
- Refer to ResponseEntity<APIResponse<Void>>

---

## GET `/api/kegiatan/{id}/file`
**Operation:** getActivityStatistics

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<ActivityStatResponse>>`
- `totalKegiatan`: long
- `disetujui`: long
- `ditolak`: long

---

## GET /api/kegiatan/rekap
**Operation:** getRekapKegiatan

### Request Structure
*No Body Request*

### Response Structure
**Type:** ResponseEntity<APIResponse<List<ActivityRekapResponse>>>
- 
amaMahasiswa: String
- 
amaKegiatan: String
- waktu: OffsetDateTime

---

## GET /api/kegiatan/rekap/{mahasiswaId}
**Operation:** getRekapKegiatanByMahasiswaId

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<List<ActivityRekapResponse>>>`
- `namaMahasiswa`: String
- `namaKegiatan`: String
- `waktu`: OffsetDateTime

---
