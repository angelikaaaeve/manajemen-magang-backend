# Ringkasan Modul Dashboard Mentor (Untuk Frontend Developer)

Modul ini menyediakan data agregasi dan fitur pendaftaran mahasiswa yang dikelola oleh mentor untuk ditampilkan pada dashboard mentor.

## Endpoint API

| Method | Endpoint | Deskripsi |
|---|---|---|
| `GET` | `/api/dashboard/mentor/statistik` | Mengambil statistik jumlah mahasiswa (aktif & selesai). |
| `GET` | `/api/dashboard/mentor/diagram-absensi` | Mengambil data untuk chart/diagram rekap absensi keseluruhan. |
| `POST` | `/api/dashboard/mentor/register-mahasiswa` | Mendaftarkan akun mahasiswa baru ke sistem. |
| `GET` | `/api/dashboard/mentor/search` | Mencari mahasiswa berdasarkan nama. |
