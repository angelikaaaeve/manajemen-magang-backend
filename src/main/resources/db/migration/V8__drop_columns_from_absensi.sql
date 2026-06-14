ALTER TABLE absensi
    DROP COLUMN IF EXISTS waktu_masuk,
    DROP COLUMN IF EXISTS waktu_keluar,
    DROP COLUMN IF EXISTS status_verifikasi;
