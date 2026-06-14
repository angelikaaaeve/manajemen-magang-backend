package com.bsi.manajement_magang.modules.media.repository;

import java.io.InputStream;

public interface MediaRepository {
    void uploadFile(String key, InputStream inputStream, long contentLength, String contentType);
    byte[] downloadFile(String key);
    void deleteFile(String key);
}
