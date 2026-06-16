# Agent Docs: penilaian

Detail Endpoint untuk Frontend Developer

## GET `/api/penilaian`
**Operation:** listPenilaian

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<List<PenilaianResponse>>>`
- Refer to ResponseEntity<APIResponse<List<PenilaianResponse>>>

---

## POST `/api/penilaian`
**Operation:** editPenilaian

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<PenilaianResponse>>`
- `id`: UUID
- `periodeMagangId`: UUID
- `mahasiswaId`: UUID
- `nim`: String
- `namaMahasiswa`: String
- `mentorId`: UUID
- `namaMentor`: String
- `kinerja`: BigDecimal
- `kedisiplinan`: BigDecimal
- `tanggungJawab`: BigDecimal
- `komunikasi`: BigDecimal
- `sikap`: BigDecimal
- `kerapihan`: BigDecimal
- `absensi`: BigDecimal
- `kerjasama`: BigDecimal
- `nilaiTotal`: BigDecimal
- `catatan`: String
- `statusPenilaian`: String

---

## GET `/api/penilaian/statistik`
**Operation:** getPenilaianStatistics

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<PenilaianStatResponse>>`
- `totalPenilaian`: long
- `totalSudahDinilai`: long
- `totalBelumDinilai`: long

---

