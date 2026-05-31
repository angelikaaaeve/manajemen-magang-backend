package com.bsi.manajement_magang.modules.iam.application;

import com.bsi.manajement_magang.modules.iam.application.command.LoginCommand;
import com.bsi.manajement_magang.modules.iam.application.command.RegisterCommand;
import com.bsi.manajement_magang.modules.iam.application.command.UpdateUserCommand;
import com.bsi.manajement_magang.modules.iam.application.response.LoginResponse;
import com.bsi.manajement_magang.modules.iam.application.response.UserResponse;
import com.bsi.manajement_magang.modules.iam.domain.Mahasiswa;
import com.bsi.manajement_magang.modules.iam.domain.Mentor;
import com.bsi.manajement_magang.modules.iam.domain.User;
import com.bsi.manajement_magang.modules.iam.domain.UserRepository;
import com.bsi.manajement_magang.util.TokenProvider;
import com.bsi.manajement_magang.shared.Argon2Hasher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class IamService {
    private final UserRepository userRepository;
    private final Argon2Hasher argon2Hasher;

    public IamService(UserRepository userRepository, Argon2Hasher argon2Hasher) {
        this.userRepository = userRepository;
        this.argon2Hasher = argon2Hasher;
    }

    @Transactional
    public UserResponse register(RegisterCommand cmd) {
        if (userRepository.findByEmail(cmd.email()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        UUID userId = UUID.randomUUID();
        String hashedPassword = argon2Hasher.hash(cmd.password());
        
        User user = new User(
                userId,
                cmd.email(),
                hashedPassword,
                cmd.role(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        userRepository.saveUser(user);

        String nim = null;
        String nama = cmd.nama();
        String noHp = null;

        switch (cmd.role()) {
            case mahasiswa:
                nim = cmd.nim();
                noHp = cmd.noHp();
                Mahasiswa mahasiswa = new Mahasiswa(
                        UUID.randomUUID(),
                        userId,
                        nim,
                        nama,
                        noHp
                );
                userRepository.saveMahasiswa(mahasiswa);
                break;
            case mentor:
                Mentor mentor = new Mentor(
                        UUID.randomUUID(),
                        userId,
                        nama
                );
                userRepository.saveMentor(mentor);
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
                noHp
        );
    }

    public LoginResponse login(LoginCommand cmd) {
        User user = userRepository.findByEmail(cmd.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!argon2Hasher.matches(cmd.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        if (!user.isActive()) {
            throw new IllegalArgumentException("User account is inactive");
        }

        String token = TokenProvider.generateToken(user.getId(), user.getRole().name());
        return new LoginResponse(token, user.getEmail(), user.getRole().name());
    }

    public UserResponse getMe(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String nim = null;
        String nama = null;
        String noHp = null;

        switch (user.getRole()) {
            case mahasiswa:
                Mahasiswa m = userRepository.findMahasiswaByUserId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("Mahasiswa profile not found"));
                nim = m.getNim();
                nama = m.getNama();
                noHp = m.getNoHp();
                break;
            case mentor:
                Mentor mentor = userRepository.findMentorByUserId(userId)
                        .orElseThrow(() -> new IllegalArgumentException("Mentor profile not found"));
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
                noHp
        );
    }

    @Transactional
    public UserResponse update(UpdateUserCommand cmd) {
        User user = userRepository.findById(cmd.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (cmd.email() != null && !cmd.email().equals(user.getEmail())) {
            if (userRepository.findByEmail(cmd.email()).isPresent()) {
                throw new IllegalArgumentException("Email is already taken");
            }
            user.setEmail(cmd.email());
            userRepository.updateUser(user);
        }

        String nim = null;
        String nama = cmd.nama();
        String noHp = null;

        switch (user.getRole()) {
            case mahasiswa:
                Mahasiswa m = userRepository.findMahasiswaByUserId(cmd.userId())
                        .orElseThrow(() -> new IllegalArgumentException("Mahasiswa profile not found"));
                
                if (cmd.nim() != null) {
                    m.setNim(cmd.nim());
                }
                if (cmd.nama() != null) {
                    m.setNama(cmd.nama());
                }
                if (cmd.noHp() != null) {
                    m.setNoHp(cmd.noHp());
                }
                
                userRepository.updateMahasiswa(m);
                nim = m.getNim();
                nama = m.getNama();
                noHp = m.getNoHp();
                break;
                
            case mentor:
                Mentor mentor = userRepository.findMentorByUserId(cmd.userId())
                        .orElseThrow(() -> new IllegalArgumentException("Mentor profile not found"));
                
                if (cmd.nama() != null) {
                    mentor.setNama(cmd.nama());
                }
                
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
                noHp
        );
    }
}
