# Agent Docs: penilaian

Detail Endpoint untuk Frontend Developer

## GET `/api/penilaian`
**Operation:** listPenilaian

### Request Structure
*Query Parameters:*
- `status` (String, optional)
- `namaMahasiswa` (String, optional)
- `index` (int, default 1)
- `size` (int, default 10)

### Response Structure
**Type:** `ResponseEntity<PaginatedResponse<PenilaianResponse>>`
- Refer to `PaginatedResponse<PenilaianResponse>`

---

## POST `/api/penilaian`
**Operation:** editPenilaian

### Request Structure
**Type:** `PenilaianRequest` (JSON Body)
- `periodeMagangId`: UUID
- `mentorId`: UUID
- `kinerja`: BigDecimal
- `kedisiplinan`: BigDecimal
- `tanggungJawab`: BigDecimal
- `komunikasi`: BigDecimal
- `sikap`: BigDecimal
- `kerapihan`: BigDecimal
- `absensi`: BigDecimal
- `kerjasama`: BigDecimal
- `catatan`: String

### Response Structure
**Type:** `ResponseEntity<APIResponse<PenilaianResponse>>`
- `id`: UUID
- `periodeMagangId`: UUID
- `mahasiswaId`: UUID
- `nim`: String
- `namaMahasiswa`: String
- `tanggalMulai`: LocalDate
- `tanggalBerakhir`: LocalDate
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
*Query Parameters:*
- `namaMahasiswa` (String, optional)

### Response Structure
**Type:** `ResponseEntity<APIResponse<PenilaianStatResponse>>`
- `totalPenilaian`: long
- `totalSudahDinilai`: long
- `totalBelumDinilai`: long

---

## GET `/api/penilaian/mahasiswa/nilai`
**Operation:** getMahasiswaNilai

### Request Structure
*No Body Request. Requires ROLE_MAHASISWA token.*

### Response Structure
**Type:** `ResponseEntity<APIResponse<PenilaianResponse>>`
- Refer to `PenilaianResponse` schema.

---

## GET `/api/penilaian/rekap`
**Operation:** getRekapPenilaian

### Request Structure
*No Body Request*

### Response Structure
**Type:** `ResponseEntity<APIResponse<Map<String, List<PenilaianResponse>>>>`
- **Description:** Mengembalikan rekapitulasi penilaian dikelompokkan berdasarkan nama mahasiswa.
- **Key (`String`):** Nama mahasiswa.
- **Value (`List<PenilaianResponse>` atau `null`):** Array data penilaian jika sudah dinilai, atau `null` jika mahasiswa tersebut belum mendapatkan penilaian.
- **Contoh JSON Data:**
  ```json
  {
    "Andreas": [
      {
        "id": "...",
        "kinerja": 80.00,
        "kedisiplinan": 90.00,
        "nilaiTotal": 85.00
      }
    ],
    "Budi": null
  }
  ```

---

## GET `/api/penilaian/rekap/{mahasiswaId}`
**Operation:** getRekapByMahasiswaId

### Request Structure
*Path Variables:*
- `mahasiswaId` (UUID, required)

### Response Structure
**Type:** `ResponseEntity<APIResponse<List<PenilaianResponse>>>`
- **Description:** Mengembalikan rekapitulasi (array) data penilaian spesifik untuk 1 mahasiswa tersebut.
- **Contoh JSON Data:**
  ```json
  [
    {
      "id": "...",
      "kinerja": 80.00,
      "kedisiplinan": 90.00,
      "nilaiTotal": 85.00
    }
  ]
  ```
