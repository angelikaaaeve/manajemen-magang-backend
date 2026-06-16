# Agent Docs: data_absensi

Detail Endpoint untuk Frontend Developer

## GET `/api/absensi/total-kehadiran`
**Operation:** getTotalKehadiran

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<Long>>`
- Refer to ResponseEntity<APIResponse<Long>>

---

## GET `/api/absensi/statistik-kehadiran`
**Operation:** listAbsensi

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<List<AbsensiResponse>>>`
- Refer to ResponseEntity<APIResponse<List<AbsensiResponse>>>

---

## DELETE `/api/absensi/{id}`
**Operation:** deleteAbsensi

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<Void>>`
- Refer to ResponseEntity<APIResponse<Void>>

---

## GET `/api/absensi/statistik`
**Operation:** getAbsensiStatistics

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<AbsensiStatResponse>>`
- `totalHadir`: long
- `totalIzinSakit`: long

---

## GET `/api/absensi/{id}/surat-keterangan`
**Operation:** exportAbsensi

### Request Structure
*No Body Request*

### Response Structure
**Type:** `byte[]`
- Refer to byte[]

---

## POST `/api/absensi/mahasiswa/submit`
**Operation:** submitAbsensi

### Request Structure
**Type:** `SubmitAbsensiRequest`
- `status`: String
- `keterangan`: String
- `attachmentUrl`: String

### Response Structure
**Type:** `ResponseEntity<APIResponse<AbsensiResponse>>`
- `id`: UUID
- `periodeMagangId`: UUID
- `mahasiswaId`: UUID
- `nim`: String
- `namaMahasiswa`: String
- `tanggal`: LocalDate
- `status`: StatusAbsensi
- `attachmentUrl`: String

---

## GET `/api/absensi/mahasiswa/riwayat`
**Operation:** getRiwayatAbsensi

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<List<AbsensiResponse>>>`
- Refer to ResponseEntity<APIResponse<List<AbsensiResponse>>>

---

## GET `/api/absensi/mahasiswa/statistik`
**Operation:** getMahasiswaStat

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<AbsensiMahasiswaStatResponse>>`
- `totalHadir`: long
- `totalIzin`: long
- `totalSakit`: long
- `totalAlfa`: long

---

