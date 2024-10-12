package com.alzion.sharingapp.repository;


import com.alzion.sharingapp.model.FileMetaData;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface FileMetadataRepository extends MongoRepository<FileMetaData, String> {
    List<FileMetaData> findByUploadTimeBefore(Date expiryDate);
}