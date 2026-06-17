package com.bsi.manajement_magang.modules.penilaian;

import com.bsi.manajement_magang.modules.penilaian.PenilaianRepository;
import com.bsi.manajement_magang.modules.penilaian.schemas.request.PenilaianRequest;
import com.bsi.manajement_magang.modules.penilaian.schemas.response.PenilaianResponse;
import com.bsi.manajement_magang.modules.penilaian.schemas.response.PenilaianStatResponse;
import com.bsi.manajement_magang.modules.penilaian.PenilaianService;
import com.bsi.manajement_magang.shared.DomainException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class PenilaianService {
    private final PenilaianRepository repository;

    public PenilaianService(PenilaianRepository repository) {
        this.repository = repository;
    }

    public com.bsi.manajement_magang.shared.PaginatedResponse<PenilaianResponse> listPenilaian(String status, String namaMahasiswa, int index, int size) {
        int limit = size;
        int offset = (index - 1) * size;
        List<PenilaianResponse> data = repository.listPenilaian(status, namaMahasiswa, limit, offset);
        long total = repository.countPenilaian(status, namaMahasiswa);
        return com.bsi.manajement_magang.shared.PaginatedResponse.success(data, total, index, size);
    }

    @Transactional
    public PenilaianResponse editPenilaian(PenilaianRequest req) {
        UUID actualMentorId;
        if (repository.existsMentor(req.mentorId())) {
            actualMentorId = req.mentorId();
        } else {
            actualMentorId = repository.findMentorIdByUserId(req.mentorId())
                .orElseThrow(() -> DomainException.notFound("Mentor not found for user ID '" + req.mentorId() + "'"));
        }

        if (!repository.existsPeriod(req.periodeMagangId())) {
            throw DomainException.notFound("Periode Magang with ID '" + req.periodeMagangId() + "' was not found");
        }
        if (!repository.existsMentor(actualMentorId)) {
            throw DomainException.notFound("Mentor with ID '" + actualMentorId + "' was not found");
        }

        Optional<PenilaianResponse> existingPenilaian = repository.findByPeriodeMagangId(req.periodeMagangId());

        if (existingPenilaian.isPresent() && existingPenilaian.get().id() != null) {
            UUID penilaianId = existingPenilaian.get().id();
            repository.update(penilaianId, actualMentorId, req);
        } else {
            UUID newPenilaianId = UUID.randomUUID();
            repository.save(newPenilaianId, actualMentorId, req);
        }

        return repository.findByPeriodeMagangId(req.periodeMagangId())
                .orElseThrow(() -> DomainException.internalError("Failed to retrieve completed student assessment details"));
    }

    public PenilaianStatResponse getPenilaianStatistics(String namaMahasiswa) {
        return repository.getPenilaianStatistics(namaMahasiswa);
    }

    public Optional<PenilaianResponse> getMahasiswaNilai(UUID userId) {
        return repository.findByUserId(userId);
    }
}
