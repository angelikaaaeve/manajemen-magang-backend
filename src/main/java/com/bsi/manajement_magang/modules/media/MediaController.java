package com.bsi.manajement_magang.modules.media;

import com.bsi.manajement_magang.shared.infra.CloudflareR1;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final CloudflareR1 cloudflareR1;

    public MediaController(CloudflareR1 cloudflareR1) {
        this.cloudflareR1 = cloudflareR1;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            // Generate a unique key for the file
            String key = UUID.randomUUID().toString() + extension;
            
            cloudflareR1.uploadFile(key, file.getInputStream(), file.getSize(), file.getContentType());
            
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
            byte[] fileContent = cloudflareR1.downloadFile(key);
            
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
            cloudflareR1.deleteFile(key);
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
