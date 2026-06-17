-- ============================================================
--  ERD - SISTEM MANAJEMEN MAGANG
--  Database : PostgreSQL
--  File ini murni berisi struktur tabel (DDL) terkini untuk
--  kemudahan membaca relasi dan skema database.
-- ============================================================

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ============================================================
--  UNIVERSITY
-- ============================================================
CREATE TABLE university (
    id              BIGSERIAL   PRIMARY KEY,
    name_university TEXT        NOT NULL,
    created_at      TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ============================================================
--  USER
-- ============================================================
CREATE TABLE "user" (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
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
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id         UUID         NOT NULL UNIQUE REFERENCES "user"(id) ON DELETE CASCADE,
    id_university   BIGINT       REFERENCES university(id) ON DELETE SET NULL,
    nim             VARCHAR(20)  NOT NULL UNIQUE,
    nama            VARCHAR(255) NOT NULL,
    no_hp           VARCHAR(20),
    gender          VARCHAR(20)  CHECK (gender IN ('Laki-laki', 'Perempuan'))
);

-- ============================================================
--  MENTOR
-- ============================================================
CREATE TABLE mentor (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL UNIQUE REFERENCES "user"(id) ON DELETE CASCADE,
    nama        VARCHAR(255) NOT NULL
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

CREATE INDEX idx_periode_magang_mahasiswa ON periode_magang(mahasiswa_id);

-- ============================================================
--  ABSENSI
-- ============================================================
CREATE TABLE absensi (
    id                  UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    periode_magang_id   UUID        NOT NULL REFERENCES periode_magang(id) ON DELETE RESTRICT,
    mahasiswa_id        UUID        NOT NULL REFERENCES mahasiswa(id) ON DELETE RESTRICT,
    mentor_id           UUID        REFERENCES mentor(id) ON DELETE SET NULL,
    tanggal             DATE        NOT NULL,
    status              VARCHAR(10) NOT NULL
                            CHECK (status IN ('hadir', 'izin', 'sakit', 'alpha')),
    attachment_url      TEXT,
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW(),

    UNIQUE (periode_magang_id, tanggal)
);

CREATE INDEX idx_absensi_periode_tanggal ON absensi(periode_magang_id, tanggal);
CREATE INDEX idx_absensi_mahasiswa       ON absensi(mahasiswa_id);
CREATE INDEX idx_absensi_mentor          ON absensi(mentor_id);

-- ============================================================
--  DATA_KEGIATAN
-- ============================================================
CREATE TABLE data_kegiatan (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    periode_magang_id   UUID         NOT NULL REFERENCES periode_magang(id) ON DELETE RESTRICT,
    mentor_id           UUID         REFERENCES mentor(id) ON DELETE SET NULL,
    judul               VARCHAR(255) NOT NULL,
    deskripsi           TEXT,
    waktu               TIMESTAMPTZ  NOT NULL,
    status              VARCHAR(20)  NOT NULL DEFAULT 'belum disetujui'
                            CHECK (status IN ('disetujui', 'belum disetujui', 'ditolak')),
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_data_kegiatan_periode ON data_kegiatan(periode_magang_id);
CREATE INDEX idx_data_kegiatan_mentor  ON data_kegiatan(mentor_id);

-- ============================================================
--  FILE_KEGIATAN
-- ============================================================
CREATE TABLE file_kegiatan (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    data_kegiatan_id    UUID         NOT NULL REFERENCES data_kegiatan(id) ON DELETE CASCADE,
    nama_file           VARCHAR(255),
    url                 TEXT         NOT NULL,
    tipe_file           VARCHAR(50),
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_file_kegiatan_kegiatan ON file_kegiatan(data_kegiatan_id);

-- ============================================================
--  PENILAIAN
-- ============================================================
CREATE TABLE penilaian (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    periode_magang_id   UUID         NOT NULL UNIQUE REFERENCES periode_magang(id) ON DELETE RESTRICT,
    mentor_id           UUID         NOT NULL REFERENCES mentor(id) ON DELETE RESTRICT,
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
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
--  SURAT_KETERANGAN_MAGANG
-- ============================================================
CREATE TABLE surat_keterangan_magang (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    periode_magang_id   UUID         NOT NULL UNIQUE REFERENCES periode_magang(id) ON DELETE RESTRICT,
    url                 TEXT         NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ============================================================
--  SERTIFIKAT
-- ============================================================
CREATE TABLE sertifikat (
    id                  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    periode_magang_id   UUID         NOT NULL UNIQUE REFERENCES periode_magang(id) ON DELETE RESTRICT,
    url                 TEXT         NOT NULL,
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);
