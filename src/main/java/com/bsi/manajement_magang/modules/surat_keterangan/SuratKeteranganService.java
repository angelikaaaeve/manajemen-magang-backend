package com.bsi.manajement_magang.modules.surat_keterangan;

import com.bsi.manajement_magang.modules.surat_keterangan.SuratKeteranganRepository;
import com.bsi.manajement_magang.modules.surat_keterangan.schemas.request.SuratKeteranganRequest;
import com.bsi.manajement_magang.modules.surat_keterangan.schemas.response.SuratKeteranganResponse;
import com.bsi.manajement_magang.modules.surat_keterangan.schemas.response.SuratKeteranganStatResponse;
import com.bsi.manajement_magang.modules.surat_keterangan.SuratKeteranganService;
import com.bsi.manajement_magang.shared.DomainException;
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

    public com.bsi.manajement_magang.shared.PaginatedResponse<SuratKeteranganResponse> listSuratKeterangan(String status, String namaMahasiswa, int index, int size) {
        int limit = size;
        int offset = (index - 1) * size;
        List<SuratKeteranganResponse> data = repository.listSuratKeterangan(status, namaMahasiswa, limit, offset);
        long total = repository.countSuratKeterangan(status, namaMahasiswa);
        return com.bsi.manajement_magang.shared.PaginatedResponse.success(data, total, index, size);
    }

    @Transactional
    public SuratKeteranganResponse uploadSuratKeterangan(SuratKeteranganRequest req) {
        if (!repository.existsPeriod(req.periodeMagangId())) {
            throw DomainException.notFound("Periode Magang with ID '" + req.periodeMagangId() + "' was not found");
        }

        Optional<SuratKeteranganResponse> existingLetter = repository.findByPeriodeMagangId(req.periodeMagangId());

        if (existingLetter.isPresent() && existingLetter.get().id() != null) {
            UUID letterId = existingLetter.get().id();
            repository.update(letterId, req.url());
        } else {
            UUID newLetterId = UUID.randomUUID();
            repository.save(newLetterId, req);
        }

        return repository.findByPeriodeMagangId(req.periodeMagangId())
                .orElseThrow(() -> DomainException.internalError("Failed to retrieve completed reference letter details"));
    }

    public SuratKeteranganStatResponse getSuratKeteranganStatistics(String namaMahasiswa) {
        return repository.getSuratKeteranganStatistics(namaMahasiswa);
    }
}
