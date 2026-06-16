# Ringkasan Modul IAM (Untuk Frontend Developer)

Modul Identity & Access Management menangani seluruh urusan login, registrasi, pembuatan token JWT, dan autentikasi pengguna.

## Endpoint API

| Method | Endpoint | Deskripsi |
|---|---|---|
| `POST` | `/api/auth/login` | Menerima `email` & `password`. Mengembalikan `Token (JWT)` dan identitas User. |
| `POST` | `/api/auth/register` | Membuat akun baru. |
| `GET` | `/api/auth/me` | Memeriksa token saat ini dan mengembalikan profil pengguna yang sedang login. |
| `PUT` | `/api/auth/update` | Memperbarui informasi profil pengguna. |

**Catatan Frontend:** 
Token yang didapatkan dari `/login` harus disimpan (misal di localStorage atau HTTP-only Cookies) dan disertakan pada header `Authorization: Bearer <Token>` untuk setiap request di modul lain.
