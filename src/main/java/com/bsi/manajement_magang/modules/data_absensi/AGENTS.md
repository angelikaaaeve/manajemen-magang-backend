# Ringkasan Modul Data Absensi (Untuk Frontend Developer)

Modul ini menangani presensi/absensi harian mahasiswa serta verifikasi dan pelacakan oleh mentor.

## Endpoint API

### Mahasiswa
| Method | Endpoint | Deskripsi |
|---|---|---|
| `POST` | `/api/absensi/mahasiswa/submit` | Submit absensi harian (mendukung `multipart/form-data` untuk unggah surat izin/sakit). |
| `GET` | `/api/absensi/mahasiswa/riwayat` | Mengambil riwayat absensi (logbook) 30 hari terakhir. |
| `GET` | `/api/absensi/mahasiswa/statistik` | Mengambil statistik absensi milik mahasiswa yang sedang login. |

### Mentor
| Method | Endpoint | Deskripsi |
|---|---|---|
| `GET` | `/api/absensi` | Mengambil daftar absensi semua mahasiswa (mendukung query `status` dan `namaMahasiswa`). |
| `POST` | `/api/absensi/{id}/verifikasi` | Verifikasi absensi (query `action=setujui` atau `action=tolak`). |
| `DELETE`| `/api/absensi/{id}` | Menghapus record absensi tertentu. |
| `GET` | `/api/absensi/statistik` | Mengambil total statistik absensi keseluruhan mahasiswa. |
| `GET` | `/api/absensi/{id}/surat-keterangan` | Mendapatkan URL untuk melihat lampiran file absensi. |
| `GET` | `/api/absensi/ekspor` | Download CSV rekap absensi. |
