ALTER TABLE absensi
    ADD COLUMN mahasiswa_id UUID REFERENCES mahasiswa(id) ON DELETE RESTRICT,
    ADD COLUMN mentor_id    UUID REFERENCES mentor(id)    ON DELETE SET NULL;

-- Backfill mahasiswa_id from existing rows via periode_magang
UPDATE absensi a
SET mahasiswa_id = pm.mahasiswa_id
FROM periode_magang pm
WHERE a.periode_magang_id = pm.id;

-- Backfill mentor_id from existing rows via mentor_mahasiswa
UPDATE absensi a
SET mentor_id = mm.mentor_id
FROM periode_magang pm
JOIN mentor_mahasiswa mm ON mm.mahasiswa_id = pm.mahasiswa_id
WHERE a.periode_magang_id = pm.id;

-- Enforce NOT NULL on mahasiswa_id after backfill (mentor_id stays nullable)
ALTER TABLE absensi
    ALTER COLUMN mahasiswa_id SET NOT NULL;

CREATE INDEX idx_absensi_mahasiswa ON absensi(mahasiswa_id);
CREATE INDEX idx_absensi_mentor    ON absensi(mentor_id);
