package com.bsi.manajement_magang.modules.media;

import com.bsi.manajement_magang.modules.media.MediaService;
import com.bsi.manajement_magang.shared.APIResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload")
    public ResponseEntity<APIResponse<Map<String, String>>> uploadFile(@RequestParam("file") MultipartFile file) {
        String key = mediaService.uploadFile(file);
        return ResponseEntity.ok(APIResponse.success(Map.of("key", key), "File uploaded successfully"));
    }

    @GetMapping("/{key}")
    public ResponseEntity<byte[]> getFile(@PathVariable String key) {
        byte[] fileContent = mediaService.getFile(key);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + key + "\"");

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
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<APIResponse<Void>> deleteFile(@PathVariable String key) {
        mediaService.deleteFile(key);
        return ResponseEntity.ok(APIResponse.success(null, "File deleted successfully"));
    }
}
