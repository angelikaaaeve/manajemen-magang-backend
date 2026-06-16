package com.bsi.manajement_magang.modules.sertifikat;

import com.bsi.manajement_magang.modules.sertifikat.SertifikatRepository;
import com.bsi.manajement_magang.modules.sertifikat.schemas.request.SertifikatRequest;
import com.bsi.manajement_magang.modules.sertifikat.schemas.response.SertifikatResponse;
import com.bsi.manajement_magang.modules.sertifikat.schemas.response.SertifikatStatResponse;
import com.bsi.manajement_magang.modules.sertifikat.SertifikatService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SertifikatService {
    private final SertifikatRepository repository;

    public SertifikatService(SertifikatRepository repository) {
        this.repository = repository;
    }

    // List all certificates with filters
    public List<SertifikatResponse> listSertifikat(String status, String namaMahasiswa) {
        return repository.listSertifikat(status, namaMahasiswa);
    }

    // Upload / Save certificate (One-to-One with Period)

    @Transactional
    public SertifikatResponse uploadSertifikat(SertifikatRequest req) {
        // Validate period existence
        if (!repository.existsPeriod(req.periodeMagangId())) {
            throw new IllegalArgumentException("Periode Magang with ID '" + req.periodeMagangId() + "' was not found");
        }

        // Check if certificate already exists
        Optional<SertifikatResponse> existingCert = repository.findByPeriodeMagangId(req.periodeMagangId());

        if (existingCert.isPresent() && existingCert.get().id() != null) {
            // Update certificate URL
            UUID certificateId = existingCert.get().id();
            repository.update(certificateId, req.url());
        } else {
            // Save new certificate
            UUID newCertificateId = UUID.randomUUID();
            repository.save(newCertificateId, req);
        }

        return repository.findByPeriodeMagangId(req.periodeMagangId())
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve completed certificate details"));
    }

    // Get statistics
    public SertifikatStatResponse getSertifikatStatistics(String namaMahasiswa) {
        return repository.getSertifikatStatistics(namaMahasiswa);
    }
}
