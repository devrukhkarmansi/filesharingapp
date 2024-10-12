package com.alzion.sharingapp.controller;

import com.alzion.sharingapp.model.FileDataResponse;
import com.alzion.sharingapp.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileControllerTest {

    @InjectMocks
    private FileController fileController;

    @Mock
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFile_Success() throws Exception {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        String passcode = "secret";
        String expectedUrl = "http://example.com/file/test";

        when(fileService.encryptAndSaveFile(file, passcode)).thenReturn(expectedUrl);

        // Act
        ResponseEntity<String> response = fileController.uploadFile(file, passcode);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUrl, response.getBody());
        verify(fileService, times(1)).encryptAndSaveFile(file, passcode);
    }

    @Test
    void testUploadFile_Failure() throws Exception {
        // Arrange
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World".getBytes());
        String passcode = "secret";

        when(fileService.encryptAndSaveFile(file, passcode)).thenThrow(new RuntimeException("Error"));

        // Act
        ResponseEntity<String> response = fileController.uploadFile(file, passcode);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("File upload failed", response.getBody());
        verify(fileService, times(1)).encryptAndSaveFile(file, passcode);
    }

    @Test
    void testDownloadFile_Success() throws Exception {
        // Arrange
        String fileId = "123";
        String passcode = "secret";
        byte[] fileData = "Hello World".getBytes();
        FileDataResponse fileResponse = FileDataResponse.builder()
                .mediaType(MediaType.APPLICATION_PDF)
                .fileName("application/pdf\", fileData")
                .fileData(fileData)
                .build();
//                new FileDataResponse("test.txt", "application/pdf", fileData);

        when(fileService.decryptFile(fileId, passcode)).thenReturn(fileResponse);

        // Act
        ResponseEntity<?> response = fileController.downloadFile(fileId, passcode);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().containsKey(HttpHeaders.CONTENT_DISPOSITION));
        assertTrue(response.getHeaders().containsKey(HttpHeaders.CONTENT_TYPE));
        assertArrayEquals(fileData, (byte[]) response.getBody());
        verify(fileService, times(1)).decryptFile(fileId, passcode);
    }

    @Test
    void testDownloadFile_Failure_InvalidPasscode() throws Exception {
        // Arrange
        String fileId = "123";
        String passcode = "wrongpasscode";

        when(fileService.decryptFile(fileId, passcode)).thenThrow(new RuntimeException("Invalid passcode"));

        // Act
        ResponseEntity<?> response = fileController.downloadFile(fileId, passcode);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid passcode or file not found", response.getBody());
        verify(fileService, times(1)).decryptFile(fileId, passcode);
    }

    @Test
    void testDeleteOldFiles() {
        // Act
        fileController.deleteOldFiles();

        // Assert
        verify(fileService, times(1)).deleteOldFiles();
    }
}
