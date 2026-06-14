package com.bsi.manajement_magang.modules.iam.service;

import com.bsi.manajement_magang.modules.iam.schema.request.LoginRequest;
import com.bsi.manajement_magang.modules.iam.schema.request.RegisterRequest;
import com.bsi.manajement_magang.modules.iam.schema.request.UpdateUserRequest;
import com.bsi.manajement_magang.modules.iam.schema.response.LoginResponse;
import com.bsi.manajement_magang.modules.iam.schema.response.UserResponse;

import java.util.UUID;

public interface IamService {

    UserResponse register(RegisterRequest req);

    LoginResponse login(LoginRequest req);

    UserResponse getMe(UUID userId);

    UserResponse update(UUID userId, UpdateUserRequest req);
}
