package com.alzion.sharingapp.service;

import com.alzion.sharingapp.model.FileDataResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    public String encryptAndSaveFile(MultipartFile file, String passcode) throws Exception;

    public FileDataResponse decryptFile(String id, String passcode) throws Exception;

    public void deleteOldFiles();
}
