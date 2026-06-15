# Ringkasan Modul Dashboard Mahasiswa (Untuk Frontend Developer)

Modul ini bertanggung jawab untuk menyediakan data statistik dan informasi umum yang akan ditampilkan di halaman utama (dashboard) mahasiswa.

## Endpoint API

| Method | Endpoint | Deskripsi |
|---|---|---|
| `GET` | `/api/dashboard/mahasiswa` | Mengambil data statistik dashboard mahasiswa. Membutuhkan Auth Token. |

### Contoh Respons:
```json
{
  "namaMahasiswa": "Budi Santoso",
  "totalKehadiran": 45,
  "sisaWaktuMagang": 14
}
```
