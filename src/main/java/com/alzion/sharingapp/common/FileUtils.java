package com.alzion.sharingapp.common;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileUtils {
    // Map of common file extensions to MIME types
    private static final Map<String, MediaType> MEDIA_TYPE_MAP = new HashMap<>();

    static {
        MEDIA_TYPE_MAP.put("pdf", MediaType.APPLICATION_PDF);
        MEDIA_TYPE_MAP.put("png", MediaType.IMAGE_PNG);
        MEDIA_TYPE_MAP.put("jpg", MediaType.IMAGE_JPEG);
        MEDIA_TYPE_MAP.put("jpeg", MediaType.IMAGE_JPEG);
        MEDIA_TYPE_MAP.put("gif", MediaType.IMAGE_GIF);
        MEDIA_TYPE_MAP.put("txt", MediaType.TEXT_PLAIN);
        MEDIA_TYPE_MAP.put("html", MediaType.TEXT_HTML);
        MEDIA_TYPE_MAP.put("csv", MediaType.valueOf("text/csv"));
        MEDIA_TYPE_MAP.put("zip", MediaType.APPLICATION_OCTET_STREAM); // Or use MediaType.APPLICATION_ZIP if you add it
        // Add more mappings as needed
    }

    // Method to determine MediaType based on file extension
    public static MediaType getMediaTypeForFileName(String fileName) {
        // Extract file extension
        String extension = getFileExtension(fileName);

        // Lookup MediaType from the map, default to application/octet-stream if not found
        return MEDIA_TYPE_MAP.getOrDefault(extension.toLowerCase(), MediaType.APPLICATION_OCTET_STREAM);
    }

    // Helper method to extract file extension
    private static String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        } else {
            return ""; // No extension found
        }
    }

}
