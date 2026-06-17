ALTER TABLE data_kegiatan
    ADD COLUMN IF NOT EXISTS mentor_id UUID REFERENCES mentor(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_data_kegiatan_mentor ON data_kegiatan(mentor_id);
