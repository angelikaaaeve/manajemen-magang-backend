-- ============================================================
--  V7: Fix seed data for university FK migration
--  After V6, universitas column was replaced with id_university FK.
--  This migration inserts universities used in V5 seed data
--  and updates the mahasiswa rows accordingly.
-- ============================================================

-- 1. Insert universities from seed data (skip if already exists)
INSERT INTO university (name_university, created_at)
SELECT 'Universitas Indonesia', NOW()
WHERE NOT EXISTS (SELECT 1 FROM university WHERE name_university = 'Universitas Indonesia');

INSERT INTO university (name_university, created_at)
SELECT 'Institut Teknologi Bandung', NOW()
WHERE NOT EXISTS (SELECT 1 FROM university WHERE name_university = 'Institut Teknologi Bandung');

INSERT INTO university (name_university, created_at)
SELECT 'Universitas Gadjah Mada', NOW()
WHERE NOT EXISTS (SELECT 1 FROM university WHERE name_university = 'Universitas Gadjah Mada');

INSERT INTO university (name_university, created_at)
SELECT 'Universitas Diponegoro', NOW()
WHERE NOT EXISTS (SELECT 1 FROM university WHERE name_university = 'Universitas Diponegoro');

INSERT INTO university (name_university, created_at)
SELECT 'Universitas Padjadjaran', NOW()
WHERE NOT EXISTS (SELECT 1 FROM university WHERE name_university = 'Universitas Padjadjaran');

INSERT INTO university (name_university, created_at)
SELECT 'Universitas Brawijaya', NOW()
WHERE NOT EXISTS (SELECT 1 FROM university WHERE name_university = 'Universitas Brawijaya');

INSERT INTO university (name_university, created_at)
SELECT 'Universitas Airlangga', NOW()
WHERE NOT EXISTS (SELECT 1 FROM university WHERE name_university = 'Universitas Airlangga');

-- 2. Update mahasiswa seed rows to reference the university table
UPDATE mahasiswa SET id_university = (SELECT id FROM university WHERE name_university = 'Universitas Indonesia' LIMIT 1)
WHERE id IN (
    'd1b30000-0000-0000-0000-000000000000',
    'd1b30000-0000-0000-0000-000000000003'
);

UPDATE mahasiswa SET id_university = (SELECT id FROM university WHERE name_university = 'Institut Teknologi Bandung' LIMIT 1)
WHERE id = 'd1b30000-0000-0000-0000-000000000001';

UPDATE mahasiswa SET id_university = (SELECT id FROM university WHERE name_university = 'Universitas Gadjah Mada' LIMIT 1)
WHERE id = 'd1b30000-0000-0000-0000-000000000002';

UPDATE mahasiswa SET id_university = (SELECT id FROM university WHERE name_university = 'Universitas Diponegoro' LIMIT 1)
WHERE id = 'd1b30000-0000-0000-0000-000000000004';

UPDATE mahasiswa SET id_university = (SELECT id FROM university WHERE name_university = 'Universitas Padjadjaran' LIMIT 1)
WHERE id = 'd1b30000-0000-0000-0000-000000000005';

UPDATE mahasiswa SET id_university = (SELECT id FROM university WHERE name_university = 'Universitas Brawijaya' LIMIT 1)
WHERE id = 'd1b30000-0000-0000-0000-000000000006';

UPDATE mahasiswa SET id_university = (SELECT id FROM university WHERE name_university = 'Universitas Airlangga' LIMIT 1)
WHERE id = 'd1b30000-0000-0000-0000-000000000007';
