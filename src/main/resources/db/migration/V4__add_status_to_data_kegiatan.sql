-- ============================================================
--  ADD STATUS TO DATA_KEGIATAN
-- ============================================================
ALTER TABLE data_kegiatan ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'belum disetujui'
    CHECK (status IN ('disetujui', 'belum disetujui', 'ditolak'));
