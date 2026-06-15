# Ringkasan Modul Penilaian (Untuk Frontend Developer)

Modul ini menangani evaluasi akhir/penilaian magang yang diberikan oleh mentor kepada mahasiswa.

## Endpoint API

### Mentor
| Method | Endpoint | Deskripsi |
|---|---|---|
| `GET` | `/api/penilaian` | Mendapatkan status penilaian mahasiswa. |
| `POST` | `/api/penilaian/{mahasiswaId}` | Menyimpan/mengubah nilai yang diberikan kepada mahasiswa. |
| `GET` | `/api/penilaian/statistik` | Mendapatkan statistik jumlah yang sudah/belum dinilai. |

### Mahasiswa
| Method | Endpoint | Deskripsi |
|---|---|---|
| `GET` | `/api/penilaian/mahasiswa` | Melihat nilai akhir dan grade diri sendiri. |
| `GET` | `/api/penilaian/mahasiswa/cetak` | Download dokumen nilai (PDF). |
