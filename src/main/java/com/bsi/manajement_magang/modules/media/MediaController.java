package com.bsi.manajement_magang.modules.media;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String key = mediaService.uploadFile(file);
            
            return ResponseEntity.ok(Map.of(
                    "message", "File uploaded successfully",
                    "key", key
            ));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to process file upload: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload failed: " + e.getMessage()));
        }
    }

    @GetMapping("/{key}")
    public ResponseEntity<byte[]> getFile(@PathVariable String key) {
        try {
            byte[] fileContent = mediaService.getFile(key);
            
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + key + "\"");
            
            // Simple content type guessing based on extension
            MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
            String lowerKey = key.toLowerCase();
            if (lowerKey.endsWith(".png")) mediaType = MediaType.IMAGE_PNG;
            else if (lowerKey.endsWith(".jpg") || lowerKey.endsWith(".jpeg")) mediaType = MediaType.IMAGE_JPEG;
            else if (lowerKey.endsWith(".gif")) mediaType = MediaType.IMAGE_GIF;
            else if (lowerKey.endsWith(".pdf")) mediaType = MediaType.APPLICATION_PDF;
            else if (lowerKey.endsWith(".txt")) mediaType = MediaType.TEXT_PLAIN;
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(mediaType)
                    .body(fileContent);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<?> deleteFile(@PathVariable String key) {
        try {
            mediaService.deleteFile(key);
            return ResponseEntity.ok(Map.of(
                    "message", "File deleted successfully",
                    "key", key
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to delete file: " + e.getMessage()));
        }
    }
}
