package com.example.web.controller;

import com.example.web.dto.photo.UploadPhotoDto;
import com.example.web.model.Photo;
import com.example.web.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/photo")
public class PhotoController {

  private PhotoService photoService;

  @PostMapping
  //@PreAuthorize("hasRole('USER')")
  public ResponseEntity<Photo> uploadPhoto(@RequestBody UploadPhotoDto uploadPhotoDto) {
    return ResponseEntity.ok(photoService.uploadPhoto(uploadPhotoDto));
  }

}
