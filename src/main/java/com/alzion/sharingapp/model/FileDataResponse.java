package com.alzion.sharingapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.MediaType;

import java.awt.*;

@Data
@Builder
public class FileDataResponse {
    private MediaType mediaType;
    private byte[] fileData;
    private String fileName;
}
