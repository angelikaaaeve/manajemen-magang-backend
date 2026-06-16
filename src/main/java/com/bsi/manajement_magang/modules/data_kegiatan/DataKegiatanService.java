package com.bsi.manajement_magang.modules.data_kegiatan;

import com.bsi.manajement_magang.modules.data_kegiatan.DataKegiatanRepository;
import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityResponse;
import com.bsi.manajement_magang.modules.data_kegiatan.schemas.response.ActivityStatResponse;
import com.bsi.manajement_magang.modules.data_kegiatan.DataKegiatanService;
import com.bsi.manajement_magang.shared.DomainException;
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

    public com.bsi.manajement_magang.shared.PaginatedResponse<ActivityResponse> listActivities(String status, String namaMahasiswa, int index, int size) {
        int limit = size;
        int offset = (index - 1) * size;
        List<ActivityResponse> data = repository.listActivities(status, namaMahasiswa, limit, offset);
        long total = repository.countActivities(status, namaMahasiswa);
        return com.bsi.manajement_magang.shared.PaginatedResponse.success(data, total, index, size);
    }

    @Transactional
    public ActivityResponse updateStatus(UUID id, String status) {
        repository.findById(id)
                .orElseThrow(() -> DomainException.notFound("Activity record with ID '" + id + "' was not found"));

        if (status == null || status.trim().isEmpty()) {
            throw DomainException.emptyField("status");
        }

        String normalizedStatus = status.toLowerCase().trim();
        if (!normalizedStatus.equals("disetujui") && !normalizedStatus.equals("belum disetujui") && !normalizedStatus.equals("ditolak")) {
            throw DomainException.invalidValue("status",
                    "'" + status + "' tidak valid. Pilihan: disetujui, belum disetujui, ditolak");
        }

        repository.updateStatus(id, normalizedStatus);

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

    public String getActivityFileUrl(UUID id) {
        ActivityResponse record = repository.findById(id)
                .orElseThrow(() -> DomainException.notFound("Activity record with ID '" + id + "' was not found"));

        String url = record.fileUrl();
        if (url == null || url.trim().isEmpty() || url.equalsIgnoreCase("-")) {
            throw DomainException.notFound("No file attachment is uploaded for this activity");
        }
        return url;
    }
}
