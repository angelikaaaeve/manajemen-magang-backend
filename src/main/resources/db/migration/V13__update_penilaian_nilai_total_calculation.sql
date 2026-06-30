ALTER TABLE penilaian DROP COLUMN nilai_total;

ALTER TABLE penilaian ADD COLUMN nilai_total NUMERIC(5,2) GENERATED ALWAYS AS (
    ROUND(
        (
            (COALESCE(kinerja, 0) * 0.15) +
            (COALESCE(kedisiplinan, 0) * 0.15) +
            (COALESCE(tanggung_jawab, 0) * 0.15) +
            (COALESCE(komunikasi, 0) * 0.10) +
            (COALESCE(sikap, 0) * 0.10) +
            (COALESCE(kerapihan, 0) * 0.10) +
            (COALESCE(absensi, 0) * 0.10) +
            (COALESCE(kerjasama, 0) * 0.15)
        ), 2
    )
) STORED;
