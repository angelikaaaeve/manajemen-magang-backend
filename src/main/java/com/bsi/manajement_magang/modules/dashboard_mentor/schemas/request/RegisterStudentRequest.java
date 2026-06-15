package com.bsi.manajement_magang.modules.dashboard_mentor.schemas.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record RegisterStudentRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    String password,

    @NotBlank(message = "NIM is required")
    String nim,

    @NotBlank(message = "Nama is required")
    String nama,

    String noHp,

    @NotBlank(message = "Gender is required")
    @Pattern(regexp = "Laki-laki|Perempuan", message = "Gender must be 'Laki-laki' or 'Perempuan'")
    String gender,

    @NotBlank(message = "Universitas is required")
    String universitas,

    LocalDate tanggalMulai,
    LocalDate tanggalBerakhir
) {}
