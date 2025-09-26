package com.example.web.mapper;

import com.example.web.dto.user.UpdateUserRequest;
import com.example.web.dto.user.UserDto;
import com.example.web.dto.user.UserResponse;
import com.example.web.dto.user.UserStatsDto;
import com.example.web.model.User;
import com.example.web.model.entity.UserStatsProjection;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "level", constant = "1")
    @Mapping(target = "points", constant = "0")
    @Mapping(target = "role", constant = "ROLE_USER")
    @Mapping(target = "id", expression = "java(generateUUID())")
    User toEntity(UserDto userDto);

    User toEntity(UpdateUserRequest userDto);

    @Mapping(target = "jwtToken", source = "token")
    UserDto toDtoWithToken(User user, String token);

    UserDto toDto(User user);

    UserResponse toResponse(User user);

    UserStatsDto toUserStats(UserStatsProjection projection);

    default String generateUUID() {
        return UUID.randomUUID().toString();
    }

}