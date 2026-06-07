package com.bsi.manajement_magang.modules.media;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class MediaControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private MediaService mediaService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testUploadFile_Success() throws Exception {
        String mockKey = "mock-uuid.pdf";
        Mockito.when(mediaService.uploadFile(any())).thenReturn(mockKey);

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", MediaType.APPLICATION_PDF_VALUE, "Dummy Content".getBytes());

        mockMvc.perform(multipart("/api/media/upload").file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("File uploaded successfully"))
                .andExpect(jsonPath("$.key").value(mockKey));
    }

    @Test
    public void testGetFile_Success() throws Exception {
        String mockKey = "test.png";
        byte[] content = "Dummy Image".getBytes();
        Mockito.when(mediaService.getFile(mockKey)).thenReturn(content);

        mockMvc.perform(get("/api/media/{key}", mockKey))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "inline; filename=\"test.png\""))
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(content));
    }

    @Test
    public void testDeleteFile_Success() throws Exception {
        String mockKey = "test.png";
        
        mockMvc.perform(delete("/api/media/{key}", mockKey))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("File deleted successfully"))
                .andExpect(jsonPath("$.key").value(mockKey));
        
        Mockito.verify(mediaService).deleteFile(mockKey);
    }
}
