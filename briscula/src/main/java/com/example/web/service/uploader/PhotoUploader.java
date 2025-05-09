package com.example.web.service.uploader;

import com.example.web.model.Photo;
import com.example.web.repository.PhotoRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PhotoUploader implements CommandLineRunner {

  @Autowired
  private PhotoRepository photoRepository;

  @Override
  public void run(String... args) throws Exception {
    if (photoRepository.count() == 0) {
      log.info("CommandLineRunner - PhotoUploader.");
      insertExistingPhotos();
    }
  }

  public void insertExistingPhotos() throws IOException {
    Path photoFolderPath = Paths.get("photos");

    if (!Files.exists(photoFolderPath) || !Files.isDirectory(photoFolderPath)) {
      log.info("No photo to insert to database.\nFolder 'photos' was not found in the root of the project.");
      return;
    }

    Files.list(photoFolderPath)
        .forEach(path -> {
          log.info("Found image {}. ", path.getFileName());
          try {
            byte[] photoBytes = Files.readAllBytes(path);
            photoRepository.save(Photo.builder()
                .id(UUID.randomUUID().toString())
                .photo(photoBytes)
                .name(getName(path.getFileName().toString()))
                .build());
          } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + path.getFileName(), e);
          }
        });
  }

  private String getName(String input) {
    return input.split("\\.")[0];
  }
}
