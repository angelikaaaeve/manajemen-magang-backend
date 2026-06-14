package com.bsi.manajement_magang.modules.penilaian.service.impl;

import com.bsi.manajement_magang.modules.penilaian.repository.PenilaianRepository;
import com.bsi.manajement_magang.modules.penilaian.schema.request.PenilaianRequest;
import com.bsi.manajement_magang.modules.penilaian.schema.response.PenilaianResponse;
import com.bsi.manajement_magang.modules.penilaian.schema.response.PenilaianStatResponse;
import com.bsi.manajement_magang.modules.penilaian.service.PenilaianService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PenilaianServiceImpl implements PenilaianService {
    private final PenilaianRepository repository;

    public PenilaianServiceImpl(PenilaianRepository repository) {
        this.repository = repository;
    }

    // List all assessments with filters
    @Override
    public List<PenilaianResponse> listPenilaian(String status, String namaMahasiswa) {
        return repository.listPenilaian(status, namaMahasiswa);
    }

    // Edit/Insert Student Assessment (One-to-One with Period)
    @Override
    @Transactional
    public PenilaianResponse editPenilaian(PenilaianRequest req) {
        // Validate existence of relational entities
        if (!repository.existsPeriod(req.periodeMagangId())) {
            throw new IllegalArgumentException("Periode Magang with ID '" + req.periodeMagangId() + "' was not found");
        }
        if (!repository.existsMentor(req.mentorId())) {
            throw new IllegalArgumentException("Mentor with ID '" + req.mentorId() + "' was not found");
        }

        // Check if an assessment already exists for the given period
        Optional<PenilaianResponse> existingPenilaian = repository.findByPeriodeMagangId(req.periodeMagangId());

        if (existingPenilaian.isPresent() && existingPenilaian.get().id() != null) {
            // Update existing assessment
            UUID penilaianId = existingPenilaian.get().id();
            repository.update(penilaianId, req);
        } else {
            // Create a new assessment
            UUID newPenilaianId = UUID.randomUUID();
            repository.save(newPenilaianId, req);
        }

        return repository.findByPeriodeMagangId(req.periodeMagangId())
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve completed student assessment details"));
    }

    // Get statistics
    @Override
    public PenilaianStatResponse getPenilaianStatistics(String namaMahasiswa) {
        return repository.getPenilaianStatistics(namaMahasiswa);
    }
}
