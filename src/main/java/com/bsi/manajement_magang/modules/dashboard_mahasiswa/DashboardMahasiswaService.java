package com.bsi.manajement_magang.modules.dashboard_mahasiswa;

import com.bsi.manajement_magang.modules.dashboard_mahasiswa.schema.DashboardMahasiswaStatResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class DashboardMahasiswaService {
    private final DashboardMahasiswaRepository repository;

    public DashboardMahasiswaService(DashboardMahasiswaRepository repository) {
        this.repository = repository;
    }

    // Get statistics for the student dashboard
    public DashboardMahasiswaStatResponse getDashboardStats(UUID mahasiswaId) {
        // 1. Calculate total attendance with 'hadir' status
        long totalKehadiran = repository.countAttendanceByMahasiswaId(mahasiswaId);

        // 2. Calculate remaining internship time (sisa waktu magang)
        long sisaWaktuDays = 0L;
        String sisaWaktuFormatted = "Belum terdaftar periode";

        Optional<Map<String, Object>> latestPeriodOpt = repository.findLatestPeriodByMahasiswaId(mahasiswaId);

        if (latestPeriodOpt.isPresent()) {
            Map<String, Object> period = latestPeriodOpt.get();
            LocalDate tanggalMulai = toLocalDate(period.get("tanggal_mulai"));
            LocalDate tanggalBerakhir = toLocalDate(period.get("tanggal_berakhir"));
            String status = (String) period.get("status");

            LocalDate today = LocalDate.now();

            if (status != null && status.equalsIgnoreCase("batal")) {
                sisaWaktuDays = 0L;
                sisaWaktuFormatted = "Magang dibatalkan";
            } else if (status != null && status.equalsIgnoreCase("selesai")) {
                sisaWaktuDays = 0L;
                sisaWaktuFormatted = "Magang selesai";
            } else {
                // Active period status ('aktif')
                if (today.isAfter(tanggalBerakhir)) {
                    sisaWaktuDays = 0L;
                    sisaWaktuFormatted = "Magang selesai";
                } else if (today.isBefore(tanggalMulai)) {
                    long totalDaysToFinish = ChronoUnit.DAYS.between(today, tanggalBerakhir);
                    sisaWaktuDays = totalDaysToFinish;
                    sisaWaktuFormatted = totalDaysToFinish + " Hari (Belum Mulai)";
                } else {
                    long remainingDays = ChronoUnit.DAYS.between(today, tanggalBerakhir);
                    sisaWaktuDays = Math.max(0L, remainingDays);
                    sisaWaktuFormatted = sisaWaktuDays + " Hari";
                }
            }
        }

        return new DashboardMahasiswaStatResponse(totalKehadiran, sisaWaktuDays, sisaWaktuFormatted);
    }

    // Helper method to safely convert DB values to LocalDate
    private LocalDate toLocalDate(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof java.sql.Date) {
            return ((java.sql.Date) obj).toLocalDate();
        }
        if (obj instanceof LocalDate) {
            return (LocalDate) obj;
        }
        return LocalDate.parse(obj.toString());
    }
}
