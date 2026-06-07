-- ============================================================
-- Seed Data for InternFlow Database
-- ============================================================

-- 1. Insert Mentor User & Profile
INSERT INTO "user" (id, email, password, role, is_active, created_at, updated_at)
VALUES (
    'd1b10000-0000-0000-0000-000000000000',
    'mentor@internflow.com',
    '$argon2id$v=19$m=16384,t=2,p=1$NjvdoZcD9zs11qMftk6DaQ$Rpj6lxNwGcIGQ/5Hd0l60RykJniFuXB9TC1fBnCJETE', -- password123
    'mentor',
    true,
    NOW(),
    NOW()
);

INSERT INTO mentor (id, user_id, nama)
VALUES (
    'd1b10000-0000-0000-0000-000000000001',
    'd1b10000-0000-0000-0000-000000000000',
    'Dr. Ahmad Hidayat, M.T.'
);

-- 2. Insert Student Users
INSERT INTO "user" (id, email, password, role, is_active, created_at, updated_at)
VALUES
('d1b20000-0000-0000-0000-000000000000', 'angelika@student.ui.ac.id', '$argon2id$v=19$m=16384,t=2,p=1$NjvdoZcD9zs11qMftk6DaQ$Rpj6lxNwGcIGQ/5Hd0l60RykJniFuXB9TC1fBnCJETE', 'mahasiswa', true, NOW(), NOW()),
('d1b20000-0000-0000-0000-000000000001', 'rizky@student.itb.ac.id', '$argon2id$v=19$m=16384,t=2,p=1$NjvdoZcD9zs11qMftk6DaQ$Rpj6lxNwGcIGQ/5Hd0l60RykJniFuXB9TC1fBnCJETE', 'mahasiswa', true, NOW(), NOW()),
('d1b20000-0000-0000-0000-000000000002', 'rahma@student.ugm.ac.id', '$argon2id$v=19$m=16384,t=2,p=1$NjvdoZcD9zs11qMftk6DaQ$Rpj6lxNwGcIGQ/5Hd0l60RykJniFuXB9TC1fBnCJETE', 'mahasiswa', true, NOW(), NOW()),
('d1b20000-0000-0000-0000-000000000003', 'budi.santoso@student.ui.ac.id', '$argon2id$v=19$m=16384,t=2,p=1$NjvdoZcD9zs11qMftk6DaQ$Rpj6lxNwGcIGQ/5Hd0l60RykJniFuXB9TC1fBnCJETE', 'mahasiswa', true, NOW(), NOW()),
('d1b20000-0000-0000-0000-000000000004', 'dewi.lestari@student.undip.ac.id', '$argon2id$v=19$m=16384,t=2,p=1$NjvdoZcD9zs11qMftk6DaQ$Rpj6lxNwGcIGQ/5Hd0l60RykJniFuXB9TC1fBnCJETE', 'mahasiswa', true, NOW(), NOW()),
('d1b20000-0000-0000-0000-000000000005', 'fajar@student.unpad.ac.id', '$argon2id$v=19$m=16384,t=2,p=1$NjvdoZcD9zs11qMftk6DaQ$Rpj6lxNwGcIGQ/5Hd0l60RykJniFuXB9TC1fBnCJETE', 'mahasiswa', true, NOW(), NOW()),
('d1b20000-0000-0000-0000-000000000006', 'larasati@student.ub.ac.id', '$argon2id$v=19$m=16384,t=2,p=1$NjvdoZcD9zs11qMftk6DaQ$Rpj6lxNwGcIGQ/5Hd0l60RykJniFuXB9TC1fBnCJETE', 'mahasiswa', true, NOW(), NOW()),
('d1b20000-0000-0000-0000-000000000007', 'hendra@student.unair.ac.id', '$argon2id$v=19$m=16384,t=2,p=1$NjvdoZcD9zs11qMftk6DaQ$Rpj6lxNwGcIGQ/5Hd0l60RykJniFuXB9TC1fBnCJETE', 'mahasiswa', true, NOW(), NOW());

-- 3. Insert Student Profiles
INSERT INTO mahasiswa (id, user_id, nim, nama, no_hp, gender, universitas)
VALUES
('d1b30000-0000-0000-0000-000000000000', 'd1b20000-0000-0000-0000-000000000000', '12220999', 'Angelika Eve', '081211112222', 'Perempuan', 'Universitas Indonesia'),
('d1b30000-0000-0000-0000-000000000001', 'd1b20000-0000-0000-0000-000000000001', '12220456', 'Muhammad Rizky', '081222223333', 'Laki-laki', 'Institut Teknologi Bandung'),
('d1b30000-0000-0000-0000-000000000002', 'd1b20000-0000-0000-0000-000000000002', '12220789', 'Siti Rahma', '081233334444', 'Perempuan', 'Universitas Gadjah Mada'),
('d1b30000-0000-0000-0000-000000000003', 'd1b20000-0000-0000-0000-000000000003', '12220123', 'Budi Santoso', '081234567890', 'Laki-laki', 'Universitas Indonesia'),
('d1b30000-0000-0000-0000-000000000004', 'd1b20000-0000-0000-0000-000000000004', '12220234', 'Dewi Lestari', '081255556666', 'Perempuan', 'Universitas Diponegoro'),
('d1b30000-0000-0000-0000-000000000005', 'd1b20000-0000-0000-0000-000000000005', '12220345', 'Fajar Nugraha', '081266667777', 'Laki-laki', 'Universitas Padjadjaran'),
('d1b30000-0000-0000-0000-000000000006', 'd1b20000-0000-0000-0000-000000000006', '12220567', 'Larasati Putri', '081277778888', 'Perempuan', 'Universitas Brawijaya'),
('d1b30000-0000-0000-0000-000000000007', 'd1b20000-0000-0000-0000-000000000007', '12220678', 'Hendra Wijaya', '081288889999', 'Laki-laki', 'Universitas Airlangga');

-- 4. Assign Students to Mentor
INSERT INTO mentor_mahasiswa (id, mentor_id, mahasiswa_id, created_at)
VALUES
(gen_random_uuid(), 'd1b10000-0000-0000-0000-000000000001', 'd1b30000-0000-0000-0000-000000000000', NOW()),
(gen_random_uuid(), 'd1b10000-0000-0000-0000-000000000001', 'd1b30000-0000-0000-0000-000000000001', NOW()),
(gen_random_uuid(), 'd1b10000-0000-0000-0000-000000000001', 'd1b30000-0000-0000-0000-000000000002', NOW()),
(gen_random_uuid(), 'd1b10000-0000-0000-0000-000000000001', 'd1b30000-0000-0000-0000-000000000003', NOW()),
(gen_random_uuid(), 'd1b10000-0000-0000-0000-000000000001', 'd1b30000-0000-0000-0000-000000000004', NOW()),
(gen_random_uuid(), 'd1b10000-0000-0000-0000-000000000001', 'd1b30000-0000-0000-0000-000000000005', NOW()),
(gen_random_uuid(), 'd1b10000-0000-0000-0000-000000000001', 'd1b30000-0000-0000-0000-000000000006', NOW()),
(gen_random_uuid(), 'd1b10000-0000-0000-0000-000000000001', 'd1b30000-0000-0000-0000-000000000007', NOW());

-- 5. Insert Internship Periods
INSERT INTO periode_magang (id, mahasiswa_id, tanggal_mulai, tanggal_berakhir, status, created_at)
VALUES
('d1b40000-0000-0000-0000-000000000000', 'd1b30000-0000-0000-0000-000000000000', '2026-02-01', '2026-07-31', 'aktif', NOW()),
('d1b40000-0000-0000-0000-000000000001', 'd1b30000-0000-0000-0000-000000000001', '2026-02-01', '2026-07-31', 'aktif', NOW()),
('d1b40000-0000-0000-0000-000000000002', 'd1b30000-0000-0000-0000-000000000002', '2026-02-01', '2026-07-31', 'aktif', NOW()),
('d1b40000-0000-0000-0000-000000000003', 'd1b30000-0000-0000-0000-000000000003', '2026-02-01', '2026-07-31', 'aktif', NOW()),
('d1b40000-0000-0000-0000-000000000004', 'd1b30000-0000-0000-0000-000000000004', '2026-02-01', '2026-07-31', 'aktif', NOW()),
('d1b40000-0000-0000-0000-000000000005', 'd1b30000-0000-0000-0000-000000000005', '2026-02-01', '2026-07-31', 'aktif', NOW()),
('d1b40000-0000-0000-0000-000000000006', 'd1b30000-0000-0000-0000-000000000006', '2026-02-01', '2026-07-31', 'aktif', NOW()),
('d1b40000-0000-0000-0000-000000000007', 'd1b30000-0000-0000-0000-000000000007', '2026-02-01', '2026-07-31', 'aktif', NOW());

-- 6. Insert Attendance Logs for '2026-05-28' (matching the mock data day)
INSERT INTO absensi (id, periode_magang_id, tanggal, waktu_masuk, waktu_keluar, status, attachment_url, created_at, status_verifikasi)
VALUES
(gen_random_uuid(), 'd1b40000-0000-0000-0000-000000000000', '2026-05-28', '2026-05-28 07:45:00+07', '2026-05-28 17:05:00+07', 'hadir', null, NOW(), 'DISETUJUI'),
(gen_random_uuid(), 'd1b40000-0000-0000-0000-000000000001', '2026-05-28', '2026-05-28 08:00:00+07', '2026-05-28 17:15:00+07', 'hadir', null, NOW(), 'DISETUJUI'),
(gen_random_uuid(), 'd1b40000-0000-0000-0000-000000000002', '2026-05-28', '2026-05-28 07:30:00+07', '2026-05-28 16:45:00+07', 'hadir', null, NOW(), 'DISETUJUI'),
(gen_random_uuid(), 'd1b40000-0000-0000-0000-000000000003', '2026-05-28', null, null, 'izin', '/uploads/absensi/doc.pdf', NOW(), 'PENDING'),
(gen_random_uuid(), 'd1b40000-0000-0000-0000-000000000004', '2026-05-28', '2026-05-28 07:55:00+07', '2026-05-28 17:00:00+07', 'hadir', null, NOW(), 'DISETUJUI'),
(gen_random_uuid(), 'd1b40000-0000-0000-0000-000000000005', '2026-05-28', null, null, 'sakit', '/uploads/absensi/doc.pdf', NOW(), 'PENDING'),
(gen_random_uuid(), 'd1b40000-0000-0000-0000-000000000006', '2026-05-28', '2026-05-28 08:15:00+07', null, 'hadir', null, NOW(), 'PENDING'),
(gen_random_uuid(), 'd1b40000-0000-0000-0000-000000000007', '2026-05-28', '2026-05-28 07:50:00+07', '2026-05-28 17:02:00+07', 'hadir', null, NOW(), 'DISETUJUI');
 