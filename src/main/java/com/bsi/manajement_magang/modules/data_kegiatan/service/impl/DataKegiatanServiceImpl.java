package com.bsi.manajement_magang.modules.data_kegiatan.service.impl;

import com.bsi.manajement_magang.modules.data_kegiatan.repository.DataKegiatanRepository;
import com.bsi.manajement_magang.modules.data_kegiatan.schema.response.ActivityResponse;
import com.bsi.manajement_magang.modules.data_kegiatan.schema.response.ActivityStatResponse;
import com.bsi.manajement_magang.modules.data_kegiatan.service.DataKegiatanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class DataKegiatanServiceImpl implements DataKegiatanService {
    private final DataKegiatanRepository repository;

    public DataKegiatanServiceImpl(DataKegiatanRepository repository) {
        this.repository = repository;
    }

    // List all activities with filters
    @Override
    public List<ActivityResponse> listActivities(String status, String namaMahasiswa) {
        return repository.listActivities(status, namaMahasiswa);
    }

    // Update activity status
    @Override
    @Transactional
    public ActivityResponse updateStatus(UUID id, String status) {
        ActivityResponse record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity record with ID '" + id + "' was not found"));

        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status cannot be empty");
        }

        String normalizedStatus = status.toLowerCase().trim();
        if (!normalizedStatus.equals("disetujui") && !normalizedStatus.equals("belum disetujui") && !normalizedStatus.equals("ditolak")) {
            throw new IllegalArgumentException("Invalid status: '" + status + "'. Supported: 'disetujui', 'belum disetujui', 'ditolak'");
        }

        repository.updateStatus(id, normalizedStatus);

        return repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve updated activity record"));
    }

    // Delete activity record
    @Override
    @Transactional
    public void deleteActivity(UUID id) {
        ActivityResponse record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity record with ID '" + id + "' was not found"));

        repository.deleteActivity(id);
    }

    // Get statistics
    @Override
    public ActivityStatResponse getActivityStatistics(String status, String namaMahasiswa) {
        return repository.getActivityStatistics(status, namaMahasiswa);
    }

    // Get activity file URL (lihat file kegiatan)
    @Override
    public String getActivityFileUrl(UUID id) {
        ActivityResponse record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity record with ID '" + id + "' was not found"));

        String url = record.fileUrl();
        if (url == null || url.trim().isEmpty() || url.equalsIgnoreCase("-")) {
            throw new IllegalArgumentException("No file attachment is uploaded for this activity");
        }
        return url;
    }
}
