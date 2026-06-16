package com.bsi.manajement_magang.modules.iam;

import com.bsi.manajement_magang.modules.iam.schemas.request.LoginRequest;
import com.bsi.manajement_magang.modules.iam.schemas.request.RegisterRequest;
import com.bsi.manajement_magang.modules.iam.schemas.request.UpdateUserRequest;
import com.bsi.manajement_magang.modules.iam.schemas.response.LoginResponse;
import com.bsi.manajement_magang.modules.iam.schemas.response.UserResponse;
import com.bsi.manajement_magang.modules.iam.IamService;
import com.bsi.manajement_magang.shared.APIResponse;
import com.bsi.manajement_magang.shared.DomainException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/api/iam")
public class IamController {
    private final IamService iamService;

    public IamController(IamService iamService) {
        this.iamService = iamService;
    }

    @PostMapping("/register")
    public ResponseEntity<APIResponse<UserResponse>> register(
            @RequestBody @Valid RegisterRequest req, HttpServletRequest request) {
        UserResponse data = iamService.register(req, resolveClientIp(request));
        return ResponseEntity.ok(APIResponse.success(data, "Registration successful"));
    }

    @PostMapping("/login")
    public ResponseEntity<APIResponse<LoginResponse>> login(
            @RequestBody @Valid LoginRequest req, HttpServletResponse response) {
        LoginResponse data = iamService.login(req);

        Cookie cookie = new Cookie("internflow_token", data.accessToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(3 * 24 * 60 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok(APIResponse.success(data, "Login successful"));
    }

    @PostMapping("/logout")
    public ResponseEntity<APIResponse<Void>> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("internflow_token", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return ResponseEntity.ok(APIResponse.success(null, "Logout successful"));
    }

    @GetMapping("/me")
    public ResponseEntity<APIResponse<UserResponse>> me() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw DomainException.unauthorized("Not authenticated");
        }
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(APIResponse.success(iamService.getMe(userId)));
    }

    @PutMapping("/update")
    public ResponseEntity<APIResponse<UserResponse>> update(@RequestBody UpdateUserRequest req) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw DomainException.unauthorized("Not authenticated");
        }
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(APIResponse.success(iamService.update(userId, req), "Profile updated successfully"));
    }

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
