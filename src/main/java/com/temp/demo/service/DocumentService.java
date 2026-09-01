package com.temp.demo.service;

import com.temp.demo.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Service
public class DocumentService {

    private final Logger logger = LogManager.getLogger(this);
    private static final String dirFormatter = "documents/%s/%s/%s";
    private static final String IMAGE_STR = "image";

    public String saveImage(String base64Str, String type, String filename) throws IOException {
        String fullDir = String.format(dirFormatter, IMAGE_STR, type, filename);
        insertIntoPath(fullDir, base64Str);
        return String.format("%s%s%s/%s/%s", Constants.API_PATH, Constants.PUBLIC_PATH, Constants.IMAGE_PATH, type, filename);
    }

    private static void insertIntoPath(String directory, String src) throws IOException, NullPointerException {
        byte[] bytes = getStringBytes64(src);
        insertIntoPath(directory, bytes);
    }

    private static void insertIntoPath(String directory, byte[] bytes) throws IOException, NullPointerException {
        Path saveFile = Paths.get(directory);
        if (!Files.exists(saveFile.getParent()))
            Files.createDirectories(saveFile.getParent());
        Files.write(saveFile, bytes);
    }

    private static byte[] getStringBytes64(String source) {
        String trimmed = source.startsWith("data:image") ? source.substring(source.indexOf(',') + 1) : source;
        return Base64.getDecoder().decode(trimmed.getBytes());
    }

    public byte[] getUserProfileImage(String filename) {
        try {
            String path = String.format(dirFormatter, IMAGE_STR, "user_profile", filename);
            return Files.readAllBytes(Paths.get(path));
        } catch (Exception exception) {
            logger.warn(String.format("Error getting image user profile with name %s, reason : %s", filename, exception.getMessage()), exception);
            return null;
        }
    }
}
