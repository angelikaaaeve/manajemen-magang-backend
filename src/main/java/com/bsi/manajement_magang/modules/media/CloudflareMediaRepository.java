package com.bsi.manajement_magang.modules.media;

import com.bsi.manajement_magang.shared.infra.CloudflareR2;
import org.springframework.stereotype.Repository;

import java.io.InputStream;

@Repository
public class CloudflareMediaRepository implements MediaRepository {
    
    private final CloudflareR2 cloudflareR2;

    public CloudflareMediaRepository(CloudflareR2 cloudflareR2) {
        this.cloudflareR2 = cloudflareR2;
    }

    @Override
    public void uploadFile(String key, InputStream inputStream, long contentLength, String contentType) {
        cloudflareR2.uploadFile(key, inputStream, contentLength, contentType);
    }

    @Override
    public byte[] downloadFile(String key) {
        return cloudflareR2.downloadFile(key);
    }

    @Override
    public void deleteFile(String key) {
        cloudflareR2.deleteFile(key);
    }
}
