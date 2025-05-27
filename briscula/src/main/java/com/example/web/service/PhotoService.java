package com.example.web.service;

import com.example.web.dto.photo.UploadPhotoDto;
import com.example.web.mapper.PhotoMapper;
import com.example.web.model.Photo;
import com.example.web.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhotoService {

  private final PhotoRepository photoRepository;

  private final PhotoMapper photoMapper;

  public String uploadPhoto(UploadPhotoDto uploadPhotoDto)  {
    Photo photoEntity = photoMapper.toEntity(uploadPhotoDto);
    photoEntity.setName(uploadPhotoDto.file().getOriginalFilename());
    photoRepository.save(photoEntity);
    return photoEntity.getId();
  }

  public boolean existsById(String photoId) {
    return photoRepository.existsById(photoId);
  }
}
