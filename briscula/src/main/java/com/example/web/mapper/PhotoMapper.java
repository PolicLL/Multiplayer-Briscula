package com.example.web.mapper;

import com.example.web.dto.photo.UploadPhotoDto;
import com.example.web.model.Photo;
import java.io.IOException;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

@Mapper(componentModel = "spring")
public interface PhotoMapper {

  @Mapping(target = "id", expression = "java(generateUUID())")
  @Mapping(target = "photo", expression = "java(getBytes(uploadPhotoDto.file()))")
  Photo toEntity(UploadPhotoDto uploadPhotoDto);

  default String generateUUID() {
    return UUID.randomUUID().toString();
  }

  default byte[] getBytes(MultipartFile file) {
    try {
      return file.getBytes();
    } catch (IOException e) {
      throw new RuntimeException("Failed to read file bytes", e);
    }
  }

}