package com.bsi.manajement_magang.modules.sertifikat;

import com.bsi.manajement_magang.modules.sertifikat.SertifikatRepository;
import com.bsi.manajement_magang.modules.sertifikat.schemas.request.SertifikatRequest;
import com.bsi.manajement_magang.modules.sertifikat.schemas.response.SertifikatResponse;
import com.bsi.manajement_magang.modules.sertifikat.schemas.response.SertifikatStatResponse;
import com.bsi.manajement_magang.modules.sertifikat.SertifikatService;
import com.bsi.manajement_magang.shared.DomainException;
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

    public List<SertifikatResponse> listSertifikat(String status, String namaMahasiswa) {
        return repository.listSertifikat(status, namaMahasiswa);
    }

    @Transactional
    public SertifikatResponse uploadSertifikat(SertifikatRequest req) {
        if (!repository.existsPeriod(req.periodeMagangId())) {
            throw DomainException.notFound("Periode Magang with ID '" + req.periodeMagangId() + "' was not found");
        }

        Optional<SertifikatResponse> existingCert = repository.findByPeriodeMagangId(req.periodeMagangId());

        if (existingCert.isPresent() && existingCert.get().id() != null) {
            UUID certificateId = existingCert.get().id();
            repository.update(certificateId, req.url());
        } else {
            UUID newCertificateId = UUID.randomUUID();
            repository.save(newCertificateId, req);
        }

        return repository.findByPeriodeMagangId(req.periodeMagangId())
                .orElseThrow(() -> DomainException.internalError("Failed to retrieve completed certificate details"));
    }

    public SertifikatStatResponse getSertifikatStatistics(String namaMahasiswa) {
        return repository.getSertifikatStatistics(namaMahasiswa);
    }
}
