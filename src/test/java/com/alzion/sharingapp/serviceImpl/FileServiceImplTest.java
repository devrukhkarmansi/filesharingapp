package com.alzion.sharingapp.serviceImpl;

import com.alzion.sharingapp.common.FileUtils;
import com.alzion.sharingapp.model.FileDataResponse;
import com.alzion.sharingapp.model.FileMetaData;
import com.alzion.sharingapp.repository.FileMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {

    @InjectMocks
    private FileServiceImpl fileService;

    @Mock
    private FileUtils fileUtils;

    @Mock
    private FileMetadataRepository fileRepository;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        fileService.uploadDir = "uploads/";

        // Only necessary stubbings
        lenient().when(fileRepository.save(any(FileMetaData.class))).thenAnswer(i -> {
            FileMetaData metadata = i.getArgument(0);
            metadata.setId(UUID.randomUUID().toString());
            return metadata;
        });
    }




    @Test
    void testEncryptAndSaveFile() throws Exception {
        lenient().when(multipartFile.getOriginalFilename()).thenReturn("test.txt");
        lenient().when(multipartFile.getBytes()).thenReturn("sample content".getBytes());

        String passcode = "testPasscode";
        String uniqueURL = fileService.encryptAndSaveFile(multipartFile, passcode);

        assertNotNull(uniqueURL);
        verify(fileRepository, times(1)).save(any(FileMetaData.class));
    }


    @Test
    void testDecryptFile() throws Exception {
        String id = UUID.randomUUID().toString();
        String passcode = "testPasscode";
        String encryptedFilePath = "uploads/encryptedFile.dat";
        byte[] encryptedContent = "encrypted content".getBytes();

        // Mocking FileMetaData
        FileMetaData metadataMock = mock(FileMetaData.class);
        when(metadataMock.getEncryptedFilePath()).thenReturn(encryptedFilePath);
        when(metadataMock.getFileName()).thenReturn("test.txt");

        // Mock the file repository to return the mock metadata
        when(fileRepository.findById(id)).thenReturn(Optional.of(metadataMock));

        // Mocking the Path object
        Path pathMock = mock(Path.class);
//        when(pathMock.toString()).thenReturn(encryptedFilePath); // Mock path.toString() for verification

        // Mocking static methods
        try (MockedStatic<Files> filesStatic = mockStatic(Files.class);
             MockedStatic<Cipher> cipherStatic = mockStatic(Cipher.class)) {

            filesStatic.when(() -> Files.readAllBytes(pathMock)).thenReturn(encryptedContent);

            Cipher cipherMock = mock(Cipher.class);
            cipherStatic.when(() -> Cipher.getInstance(anyString())).thenReturn(cipherMock);

            // Call the method under test
            FileDataResponse response = fileService.decryptFile(id, passcode);

            // Assertions to verify the response
            assertNotNull(response);
            assertEquals("test.txt", response.getFileName());
            assertEquals(MediaType.TEXT_PLAIN, response.getMediaType());
        }
    }


    @Test
    void testDeleteOldFiles() {
        Date expiryDate = new Date(System.currentTimeMillis() - (48 * 60 * 60 * 1000)); // 48 hours ago

        FileMetaData metadata = new FileMetaData("test.txt", "uploads/encryptedFile.dat");

        when(fileRepository.findByUploadTimeBefore(any(Date.class))).thenReturn(List.of(metadata));

        fileService.deleteOldFiles();

        verify(fileRepository, times(1)).deleteAll(anyList());
        // Remove stubbing that isn't necessary
    }

}
