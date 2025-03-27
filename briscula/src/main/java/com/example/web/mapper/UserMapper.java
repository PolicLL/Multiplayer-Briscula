package com.example.web.mapper;

import static com.example.web.utils.SecurityUtils.B_CRYPT_PASSWORD_ENCODER;

import com.example.web.dto.UserDto;
import com.example.web.model.User;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "level", constant = "1")
  @Mapping(target = "points", constant = "0")
  @Mapping(target = "id", expression = "java(generateUUID())")
  @Mapping(target = "password", expression = "java(encodePassword(userDto.password()))")
  User toEntity(UserDto userDto);

  UserDto toDto(User user);

  default String generateUUID() {
    return UUID.randomUUID().toString();
  }
  default String encodePassword(String inputPassword) {
    return B_CRYPT_PASSWORD_ENCODER.encode(inputPassword);
  }
}