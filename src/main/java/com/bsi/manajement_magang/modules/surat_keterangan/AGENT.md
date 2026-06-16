# Agent Docs: surat_keterangan

Detail Endpoint untuk Frontend Developer

## GET `/api/surat-keterangan`
**Operation:** listSuratKeterangan

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<List<SuratKeteranganResponse>>>`
- Refer to ResponseEntity<APIResponse<List<SuratKeteranganResponse>>>

---

## POST `/api/surat-keterangan`
**Operation:** uploadSuratKeterangan

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<SuratKeteranganResponse>>`
- `id`: UUID
- `periodeMagangId`: UUID
- `mahasiswaId`: UUID
- `nim`: String
- `namaMahasiswa`: String
- `url`: String
- `statusSurat`: String
- `createdAt`: LocalDateTime

---

## GET `/api/surat-keterangan/statistik`
**Operation:** getSuratKeteranganStatistics

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<SuratKeteranganStatResponse>>`
- `totalSuratDiunggah`: long
- `totalSuratBelumDiunggah`: long
- `totalJumlahSurat`: long

---

