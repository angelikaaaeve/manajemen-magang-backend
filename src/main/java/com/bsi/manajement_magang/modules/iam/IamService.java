package com.bsi.manajement_magang.modules.iam;

import com.bsi.manajement_magang.enums.Gender;
import com.bsi.manajement_magang.modules.iam.UserRepository;
import com.bsi.manajement_magang.modules.iam.schemas.entity.MahasiswaEntity;
import com.bsi.manajement_magang.modules.iam.schemas.entity.MentorEntity;
import com.bsi.manajement_magang.modules.iam.schemas.entity.Role;
import com.bsi.manajement_magang.modules.iam.schemas.entity.UserEntity;
import com.bsi.manajement_magang.modules.iam.schemas.request.LoginRequest;
import com.bsi.manajement_magang.modules.iam.schemas.request.RegisterRequest;
import com.bsi.manajement_magang.modules.iam.schemas.request.UpdateUserRequest;
import com.bsi.manajement_magang.modules.iam.schemas.response.LoginResponse;
import com.bsi.manajement_magang.modules.iam.schemas.response.UserResponse;
import com.bsi.manajement_magang.shared.DomainException;
import com.bsi.manajement_magang.util.TokenProvider;
import com.bsi.manajement_magang.shared.Argon2Hasher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class IamService {
    private final UserRepository userRepository;
    private final Argon2Hasher argon2Hasher;
    private final MentorRegistrationGuard mentorRegistrationGuard;

    @Value("${app.mentor.secret-key:}")
    private String mentorSecretKey;

    public IamService(UserRepository userRepository, Argon2Hasher argon2Hasher, MentorRegistrationGuard mentorRegistrationGuard) {
        this.userRepository = userRepository;
        this.argon2Hasher = argon2Hasher;
        this.mentorRegistrationGuard = mentorRegistrationGuard;
    }

    @Transactional
    public UserResponse register(RegisterRequest req, String clientIp) {
        Role role = Role.fromString(req.role());

        if (role == Role.mentor) {
            mentorRegistrationGuard.assertNotBlocked(clientIp);

            if (mentorSecretKey == null || mentorSecretKey.isBlank() || !mentorSecretKey.equals(req.secretKey())) {
                mentorRegistrationGuard.recordFailure(clientIp);
                throw DomainException.unauthorized("Secret key mentor tidak valid");
            }
        }

        if (userRepository.findByEmail(req.email()).isPresent()) {
            throw DomainException.conflict("Email is already registered");
        }

        UUID userId = UUID.randomUUID();
        String hashedPassword = argon2Hasher.hash(req.password());

        UserEntity user = new UserEntity(
                userId,
                req.email(),
                hashedPassword,
                role,
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        userRepository.saveUser(user);

        String nim = null;
        String nama = req.nama();
        String noHp = null;
        String gender = null;
        String universitas = null;

        switch (role) {
            case mahasiswa:
                nim = req.nim();
                noHp = req.noHp();
                gender = req.gender() != null ? req.gender().getValue() : null;
                universitas = req.universitas();

                Long idUniversity = null;
                if (universitas != null && !universitas.trim().isEmpty()) {
                    idUniversity = userRepository.findOrCreateUniversityByName(universitas);
                }

                MahasiswaEntity mahasiswa = new MahasiswaEntity(
                        UUID.randomUUID(),
                        userId,
                        nim,
                        nama,
                        noHp,
                        gender,
                        universitas
                );
                mahasiswa.setIdUniversity(idUniversity);
                userRepository.saveMahasiswa(mahasiswa);
                break;
            case mentor:
                MentorEntity mentor = new MentorEntity(
                        UUID.randomUUID(),
                        userId,
                        nama
                );
                userRepository.saveMentor(mentor);
                mentorRegistrationGuard.recordSuccess(clientIp);
                break;
            default:
                break;
        }

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                nim,
                nama,
                noHp,
                gender != null ? Gender.fromString(gender) : null,
                universitas
        );
    }

    public LoginResponse login(LoginRequest req) {
        UserEntity user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> DomainException.unauthorized("Invalid email or password"));

        if (!argon2Hasher.matches(req.password(), user.getPassword())) {
            throw DomainException.unauthorized("Invalid email or password");
        }

        if (!user.isActive()) {
            throw DomainException.unauthorized("User account is inactive");
        }

        String token = TokenProvider.generateToken(user.getId(), user.getRole().name());
        return new LoginResponse(token);
    }

    public UserResponse getMe(UUID userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> DomainException.notFound("User not found"));

        String nim = null;
        String nama = null;
        String noHp = null;
        String gender = null;
        String universitas = null;

        switch (user.getRole()) {
            case mahasiswa:
                MahasiswaEntity m = userRepository.findMahasiswaByUserId(userId)
                        .orElseThrow(() -> DomainException.notFound("Mahasiswa profile not found"));
                nim = m.getNim();
                nama = m.getNama();
                noHp = m.getNoHp();
                gender = m.getGender();
                universitas = m.getUniversitas();
                break;
            case mentor:
                MentorEntity mentor = userRepository.findMentorByUserId(userId)
                        .orElseThrow(() -> DomainException.notFound("Mentor profile not found"));
                nama = mentor.getNama();
                break;
            default:
                nama = "Admin";
                break;
        }

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                nim,
                nama,
                noHp,
                gender != null ? Gender.fromString(gender) : null,
                universitas
        );
    }

    @Transactional
    public UserResponse update(UUID userId, UpdateUserRequest req) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> DomainException.notFound("User not found"));

        if (req.email() != null && !req.email().equals(user.getEmail())) {
            if (userRepository.findByEmail(req.email()).isPresent()) {
                throw DomainException.conflict("Email is already taken");
            }
            user.setEmail(req.email());
            userRepository.updateUser(user);
        }

        String nim = null;
        String nama = req.nama();
        String noHp = null;
        String gender = null;
        String universitas = null;

        switch (user.getRole()) {
            case mahasiswa:
                MahasiswaEntity m = userRepository.findMahasiswaByUserId(userId)
                        .orElseThrow(() -> DomainException.notFound("Mahasiswa profile not found"));

                if (req.nim() != null) m.setNim(req.nim());
                if (req.nama() != null) m.setNama(req.nama());
                if (req.noHp() != null) m.setNoHp(req.noHp());
                if (req.gender() != null) m.setGender(req.gender().getValue());

                if (req.universitas() != null && !req.universitas().trim().isEmpty()) {
                    Long idUniversity = userRepository.findOrCreateUniversityByName(req.universitas());
                    m.setIdUniversity(idUniversity);
                    m.setUniversitas(req.universitas());
                }

                userRepository.updateMahasiswa(m);
                nim = m.getNim();
                nama = m.getNama();
                noHp = m.getNoHp();
                gender = m.getGender();
                universitas = m.getUniversitas();
                break;

            case mentor:
                MentorEntity mentor = userRepository.findMentorByUserId(userId)
                        .orElseThrow(() -> DomainException.notFound("Mentor profile not found"));

                if (req.nama() != null) mentor.setNama(req.nama());

                userRepository.updateMentor(mentor);
                nama = mentor.getNama();
                break;

            default:
                break;
        }

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().name(),
                nim,
                nama,
                noHp,
                gender != null ? Gender.fromString(gender) : null,
                universitas
        );
    }
}
