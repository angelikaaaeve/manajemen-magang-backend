package com.bsi.manajement_magang.modules.universitas;

import com.bsi.manajement_magang.modules.universitas.UniversitasRepository;
import com.bsi.manajement_magang.modules.universitas.schemas.request.UniversitasRequest;
import com.bsi.manajement_magang.modules.universitas.schemas.response.UniversitasResponse;
import com.bsi.manajement_magang.modules.universitas.UniversitasService;
import com.bsi.manajement_magang.shared.DomainException;
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
            throw DomainException.conflict("University '" + req.nameUniversity() + "' is already registered");
        }
        Long id = repository.save(req.nameUniversity());
        return repository.findById(id)
                .orElseThrow(() -> DomainException.internalError("Failed to retrieve created university"));
    }

    @Transactional
    public UniversitasResponse editUniversitas(Long id, UniversitasRequest req) {
        UniversitasResponse university = repository.findById(id)
                .orElseThrow(() -> DomainException.notFound("University with ID '" + id + "' was not found"));

        if (req.nameUniversity() != null && !req.nameUniversity().equalsIgnoreCase(university.nameUniversity())) {
            if (repository.existsByNameAndIdNot(req.nameUniversity(), id)) {
                throw DomainException.conflict("University '" + req.nameUniversity() + "' is already registered");
            }
        }

        String resolvedName = req.nameUniversity() != null ? req.nameUniversity() : university.nameUniversity();
        repository.update(id, resolvedName);

        return repository.findById(id)
                .orElseThrow(() -> DomainException.internalError("Failed to retrieve updated university"));
    }

    public com.bsi.manajement_magang.shared.PaginatedResponse<UniversitasResponse> listUniversitas(int index, int size) {
        int limit = size;
        int offset = (index - 1) * size;
        List<UniversitasResponse> data = repository.findAll(limit, offset);
        long total = repository.countAll();
        return com.bsi.manajement_magang.shared.PaginatedResponse.success(data, total, index, size);
    }

    @Transactional
    public void deleteUniversitas(Long id) {
        if (repository.findById(id).isEmpty()) {
            throw DomainException.notFound("University with ID '" + id + "' was not found");
        }
        repository.delete(id);
    }
}
