package com.alzion.sharingapp.serviceImpl;

import com.alzion.sharingapp.common.FileUtils;
import com.alzion.sharingapp.model.FileDataResponse;
import com.alzion.sharingapp.model.FileMetaData;
import com.alzion.sharingapp.repository.FileMetadataRepository;
import com.alzion.sharingapp.service.FileService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @Autowired
    FileUtils fileUtils;

    @Autowired
    FileMetadataRepository fileRepository;


    @Value("${file.upload-dir}")
    public String uploadDir;

    // Encrypt file and save to disk
    @Override
    public String encryptAndSaveFile(MultipartFile file, String passcode) throws Exception {
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        // Create encrypted file path
        String encryptedFileName = DigestUtils.md5Hex(file.getOriginalFilename() + System.currentTimeMillis());
        File encryptedFile = new File(uploadDir + encryptedFileName);

        // Encrypt the file with AES and passcode
        byte[] key = DigestUtils.sha256(passcode);
        SecretKey secretKey = new SecretKeySpec(key, 0, 16, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        try (FileOutputStream outputStream = new FileOutputStream(encryptedFile)) {
            byte[] inputBytes = file.getBytes();
            byte[] outputBytes = cipher.doFinal(inputBytes);
            outputStream.write(outputBytes);
        }

        FileMetaData metadata = new FileMetaData(file.getOriginalFilename(), encryptedFile.getAbsolutePath());
        fileRepository.save(metadata);

        String uniqueURL = "/files/download/" + metadata.getId();

        return uniqueURL;
    }

    // Decrypt file and return byte[]
    @Override
    public FileDataResponse decryptFile(String id, String passcode) throws Exception {

        FileMetaData metadata = fileRepository.findById(id).orElseThrow(() ->
                new RuntimeException("File not found"));
        File encryptedFile = new File(metadata.getEncryptedFilePath());

        byte[] key = DigestUtils.sha256(passcode);
        SecretKey secretKey = new SecretKeySpec(key, 0, 16, "AES");

        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        byte[] fileContent = Files.readAllBytes(encryptedFile.toPath());

        // Get the correct MediaType based on the file extension
        MediaType mediaType = FileUtils.getMediaTypeForFileName(metadata.getFileName());

        return FileDataResponse.builder()
                .fileData(cipher.doFinal(fileContent))
                .mediaType(mediaType)
                .fileName(metadata.getFileName())
                .build();
    }

    // Delete old files older than 48 hours
    @Override
    public void deleteOldFiles() {
        Date expiryDate = new Date(System.currentTimeMillis() - (48 * 60 * 60 * 1000)); // 48 hours ago
        List<FileMetaData> oldFiles = fileRepository.findByUploadTimeBefore(expiryDate);
        for (FileMetaData metadata : oldFiles) {
            File file = new File(metadata.getEncryptedFilePath());
            if (file.exists()) {
                file.delete();
            }
        }
        fileRepository.deleteAll(oldFiles);
    }
}
