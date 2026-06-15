# Ringkasan Modul Data Kegiatan (Untuk Frontend Developer)

Modul ini mengelola pencatatan aktivitas/kegiatan magang harian mahasiswa dan proses peninjauan (review) oleh mentor.

## Endpoint API

### Mahasiswa
| Method | Endpoint | Deskripsi |
|---|---|---|
| `POST` | `/api/kegiatan` | Menambahkan kegiatan baru beserta file attachment (`multipart/form-data`). |
| `GET` | `/api/kegiatan/mahasiswa` | List kegiatan mahasiswa (dengan query filter `status` dan `nama`). |
| `DELETE`| `/api/kegiatan/{id}` | Menghapus log kegiatan milik sendiri. |

### Mentor
| Method | Endpoint | Deskripsi |
|---|---|---|
| `GET` | `/api/kegiatan` | Melihat seluruh log kegiatan mahasiswa. Mendukung query filter. |
| `PUT` | `/api/kegiatan/{id}/status` | Mengubah status persetujuan kegiatan (Disetujui/Ditolak). |
| `DELETE`| `/api/kegiatan/{id}/admin` | Menghapus log kegiatan mahasiswa tertentu. |
| `GET` | `/api/kegiatan/statistik` | Mendapatkan angka statistik persetujuan kegiatan. |
