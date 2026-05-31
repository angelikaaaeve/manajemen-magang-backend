-- ============================================================
--  ADD STATUS VERIFIKASI TO ABSENSI
-- ============================================================
ALTER TABLE absensi ADD COLUMN status_verifikasi VARCHAR(20) NOT NULL DEFAULT 'PENDING' 
    CHECK (status_verifikasi IN ('PENDING', 'DISETUJUI', 'DITOLAK'));
