package com.alzion.sharingapp.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "fileMetadata")
public class FileMetaData {

    @Id
    private String id;
    private String fileName;
    private String encryptedFilePath;
    private Date uploadTime;
    private LocalDateTime expiryTime;

    public FileMetaData(String fileName, String encryptedFilePath) {
        this.fileName = fileName;
        this.encryptedFilePath = encryptedFilePath;
        this.uploadTime = new Date();
    }

}
