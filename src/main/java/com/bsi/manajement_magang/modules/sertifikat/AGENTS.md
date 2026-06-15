# Ringkasan Modul Sertifikat (Untuk Frontend Developer)

Modul ini mengelola distribusi sertifikat kelulusan magang secara digital dari mentor ke mahasiswa.

## Endpoint API

### Mentor
| Method | Endpoint | Deskripsi |
|---|---|---|
| `GET` | `/api/sertifikat` | Menampilkan daftar mahasiswa beserta status sertifikatnya. |
| `POST` | `/api/sertifikat/{mahasiswaId}/upload` | Mengunggah file sertifikat (`multipart/form-data`). |
| `GET` | `/api/sertifikat/statistik` | Mengambil data statistik unggahan sertifikat. |

### Mahasiswa
| Method | Endpoint | Deskripsi |
|---|---|---|
| `GET` | `/api/sertifikat/mahasiswa` | Melihat data sertifikat milik sendiri (termasuk metadata: tanggal terbit, tipe file). |
| `GET` | `/api/sertifikat/mahasiswa/download` | Mengunduh file fisik sertifikat (mendapatkan URL atau Blob file). |
