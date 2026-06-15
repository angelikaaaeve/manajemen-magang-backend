package com.bsi.manajement_magang.modules.surat_keterangan;

import com.bsi.manajement_magang.modules.surat_keterangan.SuratKeteranganRepository;
import com.bsi.manajement_magang.modules.surat_keterangan.schemas.request.SuratKeteranganRequest;
import com.bsi.manajement_magang.modules.surat_keterangan.schemas.response.SuratKeteranganResponse;
import com.bsi.manajement_magang.modules.surat_keterangan.schemas.response.SuratKeteranganStatResponse;
import com.bsi.manajement_magang.modules.surat_keterangan.SuratKeteranganService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SuratKeteranganService {
    private final SuratKeteranganRepository repository;

    public SuratKeteranganService(SuratKeteranganRepository repository) {
        this.repository = repository;
    }

    // List all letters with filters
    public List<SuratKeteranganResponse> listSuratKeterangan(String status, String namaMahasiswa) {
        return repository.listSuratKeterangan(status, namaMahasiswa);
    }

    // Upload / Save reference letter (One-to-One with Period)

    @Transactional
    public SuratKeteranganResponse uploadSuratKeterangan(SuratKeteranganRequest req) {
        // Validate period existence
        if (!repository.existsPeriod(req.periodeMagangId())) {
            throw new IllegalArgumentException("Periode Magang with ID '" + req.periodeMagangId() + "' was not found");
        }

        // Check if reference letter already exists
        Optional<SuratKeteranganResponse> existingLetter = repository.findByPeriodeMagangId(req.periodeMagangId());

        if (existingLetter.isPresent() && existingLetter.get().id() != null) {
            // Update reference letter URL
            UUID letterId = existingLetter.get().id();
            repository.update(letterId, req.url());
        } else {
            // Save new reference letter
            UUID newLetterId = UUID.randomUUID();
            repository.save(newLetterId, req);
        }

        return repository.findByPeriodeMagangId(req.periodeMagangId())
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve completed reference letter details"));
    }

    // Get statistics
    public SuratKeteranganStatResponse getSuratKeteranganStatistics(String namaMahasiswa) {
        return repository.getSuratKeteranganStatistics(namaMahasiswa);
    }
}
