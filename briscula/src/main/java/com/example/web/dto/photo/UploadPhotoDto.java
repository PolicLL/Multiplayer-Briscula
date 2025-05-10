package com.example.web.dto.photo;

import org.springframework.web.multipart.MultipartFile;

public record UploadPhotoDto(MultipartFile file) {

}
