package com.bsi.manajement_magang.modules.data_kegiatan;

import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityResponse;
import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityStatResponse;
import com.bsi.manajement_magang.shared.DomainException;
import com.bsi.manajement_magang.shared.PaginatedResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DataKegiatanService {
    private final DataKegiatanRepository repository;

    public DataKegiatanService(DataKegiatanRepository repository) {
        this.repository = repository;
    }

    public PaginatedResponse<ActivityResponse> listActivities(String status, String namaMahasiswa, int index, int size) {
        int offset = (index - 1) * size;
        List<ActivityResponse> data = repository.listActivities(status, namaMahasiswa, size, offset);
        long total = repository.countActivities(status, namaMahasiswa);
        return PaginatedResponse.success(data, total, index, size);
    }

    @Transactional
    public ActivityResponse updateStatus(UUID id, String status) {
        repository.findById(id)
                .orElseThrow(() -> DomainException.notFound("Activity record with ID '" + id + "' was not found"));
        if (status == null || status.trim().isEmpty()) throw DomainException.emptyField("status");
        String s = status.toLowerCase().trim();
        if (!s.equals("disetujui") && !s.equals("belum disetujui") && !s.equals("ditolak"))
            throw DomainException.invalidValue("status", "'" + status + "' tidak valid. Pilihan: disetujui, belum disetujui, ditolak");
        repository.updateStatus(id, s);
        return repository.findById(id)
                .orElseThrow(() -> DomainException.internalError("Failed to retrieve updated activity record"));
    }

    @Transactional
    public void deleteActivity(UUID id) {
        repository.findById(id)
                .orElseThrow(() -> DomainException.notFound("Activity record with ID '" + id + "' was not found"));
        repository.deleteActivity(id);
    }

    public ActivityStatResponse getActivityStatistics(String status, String namaMahasiswa) {
        return repository.getActivityStatistics(status, namaMahasiswa);
    }

    public List<ActivityResponse> listMahasiswaActivities(UUID userId) {
        return repository.listActivitiesByUserId(userId);
    }

    @Transactional
    public ActivityResponse createMahasiswaActivity(UUID userId, String judul, String deskripsi, List<String> fileUrls) {
        if (judul == null || judul.isBlank()) throw DomainException.emptyField("judul");
        UUID periodeId = repository.findActivePeriodIdByUserId(userId)
                .orElseThrow(() -> DomainException.notFound("Tidak ada periode magang aktif untuk mahasiswa ini"));
        ActivityResponse activity = repository.createActivity(periodeId, judul.trim(), deskripsi != null ? deskripsi.trim() : "");
        if (fileUrls != null && !fileUrls.isEmpty()) {
            repository.insertFilesKegiatan(activity.id(), fileUrls);
        }
        return repository.findById(activity.id())
                .orElseThrow(() -> DomainException.internalError("Gagal mengambil kegiatan setelah dibuat"));
    }

    @Transactional
    public void addFilesToKegiatan(UUID id, List<String> fileUrls) {
        repository.findById(id)
                .orElseThrow(() -> DomainException.notFound("Activity record with ID '" + id + "' was not found"));
        if (fileUrls != null && !fileUrls.isEmpty()) {
            repository.insertFilesKegiatan(id, fileUrls);
        }
    }

    public List<String> getActivityFiles(UUID id) {
        ActivityResponse record = repository.findById(id)
                .orElseThrow(() -> DomainException.notFound("Activity record with ID '" + id + "' was not found"));
        return record.fileUrls();
    }
}
