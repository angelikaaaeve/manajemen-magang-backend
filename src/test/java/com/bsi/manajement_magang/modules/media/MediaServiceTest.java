package com.bsi.manajement_magang.modules.media;

import com.bsi.manajement_magang.modules.media.repository.MediaRepository;
import com.bsi.manajement_magang.modules.media.service.impl.MediaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;

    @InjectMocks
    private MediaServiceImpl mediaService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testUploadFile_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile(
                "file", "test.txt", "text/plain", "Hello World".getBytes());

        String resultKey = mediaService.uploadFile(file);

        assertNotNull(resultKey);
        assertTrue(resultKey.endsWith(".txt"));
        
        verify(mediaRepository).uploadFile(
                eq(resultKey),
                any(),
                eq(file.getSize()),
                eq(file.getContentType())
        );
    }

    @Test
    public void testGetFile_Success() {
        String key = "test.txt";
        byte[] content = "Hello World".getBytes();
        when(mediaRepository.downloadFile(key)).thenReturn(content);

        byte[] result = mediaService.getFile(key);

        assertArrayEquals(content, result);
        verify(mediaRepository).downloadFile(key);
    }

    @Test
    public void testDeleteFile_Success() {
        String key = "test.txt";

        mediaService.deleteFile(key);

        verify(mediaRepository).deleteFile(key);
    }
}
