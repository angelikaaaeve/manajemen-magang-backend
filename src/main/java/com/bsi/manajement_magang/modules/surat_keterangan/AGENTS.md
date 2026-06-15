# Ringkasan Modul Surat Keterangan (Untuk Frontend Developer)

Modul ini bertanggung jawab menangani surat keterangan administratif (seperti surat keterangan aktif atau selesai magang) yang diberikan oleh mentor.

## Endpoint API

### Mentor
| Method | Endpoint | Deskripsi |
|---|---|---|
| `GET` | `/api/surat-keterangan` | Daftar mahasiswa dan status ketersediaan surat. |
| `POST` | `/api/surat-keterangan/{mahasiswaId}/upload` | Unggah dokumen surat keterangan. |
| `GET` | `/api/surat-keterangan/statistik` | Statistik ketersediaan surat. |

### Mahasiswa
| Method | Endpoint | Deskripsi |
|---|---|---|
| `GET` | `/api/surat-keterangan/mahasiswa` | Mengambil metadata surat keterangan. |
| `GET` | `/api/surat-keterangan/mahasiswa/download` | Mengunduh berkas surat keterangan. |
