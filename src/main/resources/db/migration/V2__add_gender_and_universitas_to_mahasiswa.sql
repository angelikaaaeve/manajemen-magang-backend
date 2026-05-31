-- ============================================================
--  ADD GENDER AND UNIVERSITAS TO MAHASISWA
-- ============================================================
ALTER TABLE mahasiswa ADD COLUMN gender VARCHAR(20) CHECK (gender IN ('Laki-laki', 'Perempuan'));
ALTER TABLE mahasiswa ADD COLUMN universitas VARCHAR(255);
