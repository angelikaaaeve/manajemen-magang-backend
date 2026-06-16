# Ringkasan Modul Data Mahasiswa (Untuk Frontend Developer)

Modul ini berfungsi sebagai CRUD (Create, Read, Update, Delete) utama untuk mengelola data mahasiswa yang magang di bawah pengawasan mentor.

## Endpoint API

| Method | Endpoint | Deskripsi |
|---|---|---|
| `GET` | `/api/mahasiswa` | Mendapatkan daftar seluruh mahasiswa dengan filter query params (`gender`, `universitas`, `status`). |
| `POST` | `/api/mahasiswa` | Mendaftarkan data mahasiswa magang baru. |
| `GET` | `/api/mahasiswa/{id}` | Mendapatkan detail profil dari mahasiswa tertentu. |
| `PUT` | `/api/mahasiswa/{id}` | Memperbaharui data profil dan periode magang mahasiswa. |
| `GET` | `/api/mahasiswa/statistik` | Mengambil data statistik distribusi status mahasiswa. |
