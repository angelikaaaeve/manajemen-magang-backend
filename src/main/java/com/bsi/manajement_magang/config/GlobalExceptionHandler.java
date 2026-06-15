package com.bsi.manajement_magang.config;

import com.bsi.manajement_magang.shared.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<APIResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(APIResponse.error(ex.getMessage(), null));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<APIResponse<Void>> handleIllegalStateException(IllegalStateException ex) {
        return ResponseEntity.badRequest().body(APIResponse.error(ex.getMessage(), null));
    }
}
