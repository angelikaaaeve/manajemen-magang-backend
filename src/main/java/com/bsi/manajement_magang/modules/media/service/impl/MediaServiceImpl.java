package com.bsi.manajement_magang.modules.media.service.impl;

import com.bsi.manajement_magang.modules.media.repository.MediaRepository;
import com.bsi.manajement_magang.modules.media.service.MediaService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;

    public MediaServiceImpl(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }

    @Override
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

    @Override
    public byte[] getFile(String key) {
        return mediaRepository.downloadFile(key);
    }

    @Override
    public void deleteFile(String key) {
        mediaRepository.deleteFile(key);
    }
}
