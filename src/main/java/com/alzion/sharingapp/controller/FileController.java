package com.alzion.sharingapp.controller;


import com.alzion.sharingapp.model.FileDataResponse;
import com.alzion.sharingapp.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/files")
public class FileController {

    @Autowired
    FileService fileService;

    // 1. Upload File
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file,
                                             @RequestParam("passcode") String passcode) {
        try {
            String uniqueURL = fileService.encryptAndSaveFile(file, passcode);
            return new ResponseEntity<>(uniqueURL, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("File upload failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Download file API
    @GetMapping("/download/{id}")
    public ResponseEntity<?> downloadFile(@PathVariable String id,
                                          @RequestParam("passcode") String passcode) {
        try {
            FileDataResponse decryptedFile = fileService.decryptFile(id, passcode);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(decryptedFile.getFileName())
                    .build());
            // Set the content type (you might want to dynamically detect the file type, for now PDF example)
            headers.setContentType(decryptedFile.getMediaType());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(decryptedFile.getFileData());
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid passcode or file not found", HttpStatus.BAD_REQUEST);
        }
    }

    // Scheduled task to delete files older than 48 hours
    @Scheduled(cron = "0 0 * * * ?") // Every hour
    public void deleteOldFiles() {

        fileService.deleteOldFiles();

    }
}
