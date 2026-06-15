# Ringkasan Modul Universitas (Untuk Frontend Developer)

Modul ini digunakan untuk mengelola data master (Master Data) institusi perguruan tinggi atau universitas yang digunakan pada dropdown registrasi mahasiswa.

## Endpoint API

| Method | Endpoint | Deskripsi |
|---|---|---|
| `GET` | `/api/universitas` | Mendapatkan senarai (list) seluruh universitas. Sangat berguna untuk di-fetch pada *Dropdown Input*. |
| `POST` | `/api/universitas` | Menambahkan institusi universitas baru. |
| `PUT` | `/api/universitas/{id}` | Mengubah detail nama universitas. |
| `DELETE`| `/api/universitas/{id}` | Menghapus universitas dari sistem. |
