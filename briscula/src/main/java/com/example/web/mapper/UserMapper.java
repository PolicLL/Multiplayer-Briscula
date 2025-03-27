package com.example.web.mapper;

import com.example.web.dto.UserDto;
import com.example.web.model.User;
import java.util.UUID;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

  @Mapping(target = "level", expression = "java(source.getLevel() != null ? source.getLevel() : 1)")
  @Mapping(target = "points", expression = "java(source.getPoints() != null ? source.getPoints() : 0)")
  @Mapping(target = "id", expression = "java(generateUUID())")
  User toEntity(UserDto userDto);

  UserDto toDto(User user);

  default String generateUUID() {
    return UUID.randomUUID().toString();
  }
}