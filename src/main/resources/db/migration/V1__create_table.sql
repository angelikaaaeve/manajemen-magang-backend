-- ============================================================
--  ERD - SISTEM MANAJEMEN MAGANG
--  Database : PostgreSQL
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
--  USER
-- ============================================================
CREATE TABLE "user" (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    email       VARCHAR(255) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL CHECK (role IN ('admin', 'mahasiswa', 'mentor')),
    is_active   BOOLEAN      NOT NULL DEFAULT true,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
--  MAHASISWA
-- ============================================================
CREATE TABLE mahasiswa (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL UNIQUE REFERENCES "user"(id) ON DELETE CASCADE,
    nim         VARCHAR(20)  NOT NULL UNIQUE,
    nama        VARCHAR(255) NOT NULL,
    no_hp       VARCHAR(20)
);

-- ============================================================
--  MENTOR
-- ============================================================
CREATE TABLE mentor (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID        NOT NULL UNIQUE REFERENCES "user"(id) ON DELETE CASCADE,
    nama        VARCHAR(255) NOT NULL
);

-- ============================================================
--  MENTOR_MAHASISWA  (1 mentor : banyak mahasiswa)
-- ============================================================
CREATE TABLE mentor_mahasiswa (
    id              UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    mentor_id       UUID        NOT NULL REFERENCES mentor(id) ON DELETE RESTRICT,
    mahasiswa_id    UUID        NOT NULL UNIQUE REFERENCES mahasiswa(id) ON DELETE CASCADE,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ============================================================
--  PERIODE_MAGANG
-- ============================================================
CREATE TABLE periode_magang (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    mahasiswa_id        UUID        NOT NULL REFERENCES mahasiswa(id) ON DELETE RESTRICT,
    tanggal_mulai       DATE        NOT NULL,
    tanggal_berakhir    DATE        NOT NULL,
    status              VARCHAR(10) NOT NULL DEFAULT 'aktif'
                            CHECK (status IN ('aktif', 'selesai', 'batal')),
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_tanggal CHECK (tanggal_berakhir >= tanggal_mulai)
);

-- ============================================================
--  ABSENSI
-- ============================================================
CREATE TABLE absensi (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    periode_magang_id   UUID        NOT NULL REFERENCES periode_magang(id) ON DELETE RESTRICT,
    tanggal             DATE        NOT NULL,
    waktu_masuk         TIMESTAMPTZ,
    waktu_keluar        TIMESTAMPTZ,
    status              VARCHAR(10) NOT NULL
                            CHECK (status IN ('hadir', 'izin', 'sakit', 'alpha')),
    attachment_url      TEXT,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW(),

    UNIQUE (periode_magang_id, tanggal)
);

-- ============================================================
--  DATA_KEGIATAN
-- ============================================================
CREATE TABLE data_kegiatan (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    periode_magang_id   UUID        NOT NULL REFERENCES periode_magang(id) ON DELETE RESTRICT,
    judul               VARCHAR(255) NOT NULL,
    deskripsi           TEXT,
    waktu               TIMESTAMPTZ  NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
--  FILE_KEGIATAN
-- ============================================================
CREATE TABLE file_kegiatan (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    data_kegiatan_id    UUID        NOT NULL REFERENCES data_kegiatan(id) ON DELETE CASCADE,
    nama_file           VARCHAR(255),
    url                 TEXT        NOT NULL,
    tipe_file           VARCHAR(50),
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ============================================================
--  PENILAIAN
-- ============================================================
CREATE TABLE penilaian (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    periode_magang_id   UUID        NOT NULL UNIQUE REFERENCES periode_magang(id) ON DELETE RESTRICT,
    mentor_id           UUID        NOT NULL REFERENCES mentor(id) ON DELETE RESTRICT,
    kinerja             NUMERIC(5,2) CHECK (kinerja BETWEEN 0 AND 100),
    kedisiplinan        NUMERIC(5,2) CHECK (kedisiplinan BETWEEN 0 AND 100),
    tanggung_jawab      NUMERIC(5,2) CHECK (tanggung_jawab BETWEEN 0 AND 100),
    komunikasi          NUMERIC(5,2) CHECK (komunikasi BETWEEN 0 AND 100),
    sikap               NUMERIC(5,2) CHECK (sikap BETWEEN 0 AND 100),
    kerapihan           NUMERIC(5,2) CHECK (kerapihan BETWEEN 0 AND 100),
    absensi             NUMERIC(5,2) CHECK (absensi BETWEEN 0 AND 100),
    kerjasama           NUMERIC(5,2) CHECK (kerjasama BETWEEN 0 AND 100),
    nilai_total         NUMERIC(5,2) GENERATED ALWAYS AS (
                            ROUND(
                                (COALESCE(kinerja,0) + COALESCE(kedisiplinan,0) +
                                 COALESCE(tanggung_jawab,0) + COALESCE(komunikasi,0) +
                                 COALESCE(sikap,0) + COALESCE(kerapihan,0) +
                                 COALESCE(absensi,0) + COALESCE(kerjasama,0)) / 8, 2
                            )
                        ) STORED,
    catatan             TEXT,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ============================================================
--  SURAT_KETERANGAN_MAGANG
-- ============================================================
CREATE TABLE surat_keterangan_magang (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    periode_magang_id   UUID        NOT NULL UNIQUE REFERENCES periode_magang(id) ON DELETE RESTRICT,
    url                 TEXT        NOT NULL,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ============================================================
--  SERTIFIKAT
-- ============================================================
CREATE TABLE sertifikat (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    periode_magang_id   UUID        NOT NULL UNIQUE REFERENCES periode_magang(id) ON DELETE RESTRICT,
    url                 TEXT        NOT NULL,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ============================================================
--  INDEX
-- ============================================================
CREATE INDEX idx_absensi_periode_tanggal    ON absensi(periode_magang_id, tanggal);
CREATE INDEX idx_data_kegiatan_periode      ON data_kegiatan(periode_magang_id);
CREATE INDEX idx_file_kegiatan_kegiatan     ON file_kegiatan(data_kegiatan_id);
CREATE INDEX idx_periode_magang_mahasiswa   ON periode_magang(mahasiswa_id);
CREATE INDEX idx_mentor_mahasiswa_mentor    ON mentor_mahasiswa(mentor_id);