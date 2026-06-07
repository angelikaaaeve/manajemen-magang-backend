package com.bsi.manajement_magang.modules.universitas;

import com.bsi.manajement_magang.modules.universitas.schema.UniversitasRequest;
import com.bsi.manajement_magang.modules.universitas.schema.UniversitasResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UniversitasService {
    private final UniversitasRepository repository;

    public UniversitasService(UniversitasRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public UniversitasResponse addUniversitas(UniversitasRequest req) {
        if (repository.existsByName(req.nameUniversity())) {
            throw new IllegalArgumentException("University '" + req.nameUniversity() + "' is already registered");
        }
        Long id = repository.save(req.nameUniversity());
        return repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve created university"));
    }

    @Transactional
    public UniversitasResponse editUniversitas(Long id, UniversitasRequest req) {
        UniversitasResponse university = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("University with ID '" + id + "' was not found"));

        if (req.nameUniversity() != null && !req.nameUniversity().equalsIgnoreCase(university.nameUniversity())) {
            if (repository.existsByNameAndIdNot(req.nameUniversity(), id)) {
                throw new IllegalArgumentException("University '" + req.nameUniversity() + "' is already registered");
            }
        }

        String resolvedName = req.nameUniversity() != null ? req.nameUniversity() : university.nameUniversity();
        repository.update(id, resolvedName);

        return repository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Failed to retrieve updated university"));
    }

    public List<UniversitasResponse> listUniversitas() {
        return repository.findAll();
    }

    @Transactional
    public void deleteUniversitas(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("University with ID '" + id + "' was not found");
        }
        repository.delete(id);
    }
}
