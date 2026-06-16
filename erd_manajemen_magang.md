# ERD - Sistem Manajemen Magang

> Dihasilkan dari 8 file migration Flyway di `src/main/resources/db/migration`

## Entity Relationship Diagram

```mermaid
erDiagram

    USER {
        UUID id PK
        VARCHAR email "UNIQUE NOT NULL"
        VARCHAR password "NOT NULL"
        VARCHAR role "admin | mahasiswa | mentor"
        BOOLEAN is_active "DEFAULT true"
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    UNIVERSITY {
        BIGSERIAL id PK
        TEXT name_university "NOT NULL"
        TIMESTAMP created_at
    }

    MAHASISWA {
        UUID id PK
        UUID user_id FK "UNIQUE → user.id"
        BIGINT id_university FK "→ university.id"
        VARCHAR nim "UNIQUE NOT NULL"
        VARCHAR nama "NOT NULL"
        VARCHAR no_hp
        VARCHAR gender "Laki-laki | Perempuan"
    }

    MENTOR {
        UUID id PK
        UUID user_id FK "UNIQUE → user.id"
        VARCHAR nama "NOT NULL"
    }

    MENTOR_MAHASISWA {
        UUID id PK
        UUID mentor_id FK "→ mentor.id"
        UUID mahasiswa_id FK "UNIQUE → mahasiswa.id"
        TIMESTAMP created_at
    }

    PERIODE_MAGANG {
        UUID id PK
        UUID mahasiswa_id FK "→ mahasiswa.id"
        DATE tanggal_mulai "NOT NULL"
        DATE tanggal_berakhir "NOT NULL"
        VARCHAR status "aktif | selesai | batal"
        TIMESTAMP created_at
    }

    ABSENSI {
        UUID id PK
        UUID periode_magang_id FK "→ periode_magang.id"
        DATE tanggal "NOT NULL"
        VARCHAR status "hadir | izin | sakit | alpha"
        TEXT attachment_url
        TIMESTAMP created_at
    }

    DATA_KEGIATAN {
        UUID id PK
        UUID periode_magang_id FK "→ periode_magang.id"
        VARCHAR judul "NOT NULL"
        TEXT deskripsi
        TIMESTAMPTZ waktu "NOT NULL"
        VARCHAR status "disetujui | belum disetujui | ditolak"
        TIMESTAMP created_at
        TIMESTAMP updated_at
    }

    FILE_KEGIATAN {
        UUID id PK
        UUID data_kegiatan_id FK "→ data_kegiatan.id"
        VARCHAR nama_file
        TEXT url "NOT NULL"
        VARCHAR tipe_file
        TIMESTAMP created_at
    }

    PENILAIAN {
        UUID id PK
        UUID periode_magang_id FK "UNIQUE → periode_magang.id"
        UUID mentor_id FK "→ mentor.id"
        NUMERIC kinerja "0–100"
        NUMERIC kedisiplinan "0–100"
        NUMERIC tanggung_jawab "0–100"
        NUMERIC komunikasi "0–100"
        NUMERIC sikap "0–100"
        NUMERIC kerapihan "0–100"
        NUMERIC absensi_nilai "0–100"
        NUMERIC kerjasama "0–100"
        NUMERIC nilai_total "GENERATED avg 8 aspek"
        TEXT catatan
        TIMESTAMP created_at
    }

    SURAT_KETERANGAN_MAGANG {
        UUID id PK
        UUID periode_magang_id FK "UNIQUE → periode_magang.id"
        TEXT url "NOT NULL"
        TIMESTAMP created_at
    }

    SERTIFIKAT {
        UUID id PK
        UUID periode_magang_id FK "UNIQUE → periode_magang.id"
        TEXT url "NOT NULL"
        TIMESTAMP created_at
    }

    %% Relasi
    USER ||--|| MAHASISWA : "1 user - 1 mahasiswa"
    USER ||--|| MENTOR : "1 user - 1 mentor"

    UNIVERSITY ||--o{ MAHASISWA : "1 university - 0..N mahasiswa"

    MENTOR ||--o{ MENTOR_MAHASISWA : "1 mentor - N mahasiswa"
    MAHASISWA ||--|| MENTOR_MAHASISWA : "1 mahasiswa - 1 mentor"

    MAHASISWA ||--o{ PERIODE_MAGANG : "1 mahasiswa - N periode"

    PERIODE_MAGANG ||--o{ ABSENSI : "1 periode - N absensi"
    PERIODE_MAGANG ||--o{ DATA_KEGIATAN : "1 periode - N kegiatan"
    PERIODE_MAGANG ||--o| PENILAIAN : "1 periode - 0..1 penilaian"
    PERIODE_MAGANG ||--o| SURAT_KETERANGAN_MAGANG : "1 periode - 0..1 SKM"
    PERIODE_MAGANG ||--o| SERTIFIKAT : "1 periode - 0..1 sertifikat"

    DATA_KEGIATAN ||--o{ FILE_KEGIATAN : "1 kegiatan - N file"

    MENTOR ||--o{ PENILAIAN : "1 mentor - N penilaian"
```

---

## Ringkasan Tabel Final (setelah semua migration)

| No | Tabel | PK Type | Keterangan |
|----|-------|---------|------------|
| 1 | `user` | UUID | Akun login; role: admin/mahasiswa/mentor |
| 2 | `university` | BIGSERIAL | Master data universitas (V6) |
| 3 | `mahasiswa` | UUID | Profil mahasiswa; FK ke user & university |
| 4 | `mentor` | UUID | Profil mentor; FK ke user |
| 5 | `mentor_mahasiswa` | UUID | Mapping 1 mentor → banyak mahasiswa |
| 6 | `periode_magang` | UUID | Periode magang mahasiswa |
| 7 | `absensi` | UUID | Absensi harian per periode *(V8: waktu_masuk/keluar/status_verifikasi dihapus)* |
| 8 | `data_kegiatan` | UUID | Log kegiatan magang; + kolom status (V4) |
| 9 | `file_kegiatan` | UUID | File lampiran kegiatan |
| 10 | `penilaian` | UUID | Penilaian 8 aspek oleh mentor; nilai_total GENERATED |
| 11 | `surat_keterangan_magang` | UUID | SKM 1-to-1 dengan periode |
| 12 | `sertifikat` | UUID | Sertifikat 1-to-1 dengan periode |

---

## Perubahan per Migration

| Migration | Perubahan |
|-----------|-----------|
| V1 | Buat semua tabel dasar (12 tabel + index) |
| V2 | Tambah kolom `gender` & `universitas` ke `mahasiswa` |
| V3 | Tambah kolom `status_verifikasi` ke `absensi` |
| V4 | Tambah kolom `status` ke `data_kegiatan` |
| V5 | Insert seed data |
| V6 | Buat tabel `university`; ganti kolom `universitas` (text) → `id_university` (FK) |
| V7 | Fix seed data university |
| V8 | Drop kolom `waktu_masuk`, `waktu_keluar`, `status_verifikasi` dari `absensi` |
