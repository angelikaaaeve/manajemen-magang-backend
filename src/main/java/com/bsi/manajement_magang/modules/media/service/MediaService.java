package com.bsi.manajement_magang.modules.media.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MediaService {
    String uploadFile(MultipartFile file) throws IOException;

    byte[] getFile(String key);

    void deleteFile(String key);
}
