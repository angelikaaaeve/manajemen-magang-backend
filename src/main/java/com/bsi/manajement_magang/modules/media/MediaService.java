package com.bsi.manajement_magang.modules.media;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class MediaService {
    
    private final MediaRepository mediaRepository;

    public MediaService(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String key = UUID.randomUUID().toString() + extension;
        mediaRepository.uploadFile(key, file.getInputStream(), file.getSize(), file.getContentType());
        
        return key;
    }

    public byte[] getFile(String key) {
        return mediaRepository.downloadFile(key);
    }

    public void deleteFile(String key) {
        mediaRepository.deleteFile(key);
    }
}
