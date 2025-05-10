package com.example.web.controller;

import com.example.web.dto.photo.UploadPhotoDto;
import com.example.web.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/photo")
public class PhotoController {

  private PhotoService photoService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
    // Process the file
    String photoId = photoService.uploadPhoto(new UploadPhotoDto(file));
    return ResponseEntity.ok(photoId);
  }

  @GetMapping
  public ResponseEntity<String> getAll() {
    return ResponseEntity.ok("Test");
  }

  @PostMapping("/test")
  public ResponseEntity<String> test() {
    return ResponseEntity.ok("Test");
  }

}
