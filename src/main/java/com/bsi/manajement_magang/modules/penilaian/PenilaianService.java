package com.bsi.manajement_magang.modules.penilaian;

import com.bsi.manajement_magang.modules.penilaian.PenilaianRepository;
import com.bsi.manajement_magang.modules.penilaian.schemas.request.PenilaianRequest;
import com.bsi.manajement_magang.modules.penilaian.schemas.response.PenilaianResponse;
import com.bsi.manajement_magang.modules.penilaian.schemas.response.PenilaianStatResponse;
import com.bsi.manajement_magang.modules.penilaian.PenilaianService;
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

    // List all assessments with filters
    public List<PenilaianResponse> listPenilaian(String status, String namaMahasiswa) {
        return repository.listPenilaian(status, namaMahasiswa);
    }

    // Edit/Insert Student Assessment (One-to-One with Period)

    @Transactional
    public PenilaianResponse editPenilaian(PenilaianRequest req) {
        UUID actualMentorId;
        // Check if the provided ID is already a valid mentor ID (from existing assessment)
        if (repository.existsMentor(req.mentorId())) {
            actualMentorId = req.mentorId();
        } else {
            // Otherwise, it might be a user_id from the frontend JWT session, so we map it
            actualMentorId = repository.findMentorIdByUserId(req.mentorId())
                .orElseThrow(() -> new IllegalArgumentException("Mentor not found for user ID '" + req.mentorId() + "'"));
        }

        // Validate existence of relational entities
        if (!repository.existsPeriod(req.periodeMagangId())) {
            throw new IllegalArgumentException("Periode Magang with ID '" + req.periodeMagangId() + "' was not found");
        }
        if (!repository.existsMentor(actualMentorId)) {
            throw new IllegalArgumentException("Mentor with ID '" + actualMentorId + "' was not found");
        }

        // Check if an assessment already exists for the given period
        Optional<PenilaianResponse> existingPenilaian = repository.findByPeriodeMagangId(req.periodeMagangId());

        if (existingPenilaian.isPresent() && existingPenilaian.get().id() != null) {
            // Update existing assessment
            UUID penilaianId = existingPenilaian.get().id();
            repository.update(penilaianId, actualMentorId, req);
        } else {
            // Create a new assessment
            UUID newPenilaianId = UUID.randomUUID();
            repository.save(newPenilaianId, actualMentorId, req);
        }

        return repository.findByPeriodeMagangId(req.periodeMagangId())
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve completed student assessment details"));
    }

    // Get statistics
    public PenilaianStatResponse getPenilaianStatistics(String namaMahasiswa) {
        return repository.getPenilaianStatistics(namaMahasiswa);
    }
}
