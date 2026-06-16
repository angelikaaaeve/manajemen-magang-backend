# Ringkasan Modul Media (Untuk Frontend Developer)

Modul ini berfungsi sebagai layanan terpusat untuk mengelola berkas (Cloudflare R2 atau S3). Komponen front-end akan memanggil API ini ketika ada operasi unggah file (misal di modul absensi/kegiatan), lalu mengambil URL kembaliannya.

## Endpoint API

| Method | Endpoint | Deskripsi |
|---|---|---|
| `POST` | `/api/media/upload` | Menerima payload `multipart/form-data` dengan key `file`. Mengembalikan JSON berisi `url` file yang telah sukses di-hosting. |

**Alur Penggunaan:**
1. Pengguna memilih file di UI.
2. Hit endpoint ini untuk mendapatkan `url` file.
3. Kirim `url` tersebut sebagai string/payload ke modul lain (seperti modul absensi atau kegiatan).
