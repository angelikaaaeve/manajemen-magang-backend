package com.bsi.manajement_magang.modules.iam.schema.request;

import com.bsi.manajement_magang.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    String email,

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    String password,

    @NotBlank(message = "Role is required")
    String role,

    String nim,
    String nama,
    String noHp,
    Gender gender,
    String universitas,

    // Required when role is "mentor", validated against app.mentor.secret-key
    String secretKey
) {}
