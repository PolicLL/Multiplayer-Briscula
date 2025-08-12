package com.example.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static utils.EntityUtils.generateValidUserDtoWithoutPhoto;

import com.example.web.dto.user.UpdateUserRequest;
import com.example.web.dto.user.UserDto;
import com.example.web.exception.UserWithUsernameAlreadyExistsException;
import com.example.web.handler.AbstractIntegrationTest;
import com.example.web.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserServiceTest extends AbstractIntegrationTest {

  @Autowired
  private UserService userService;

  @Test
  void testUpdateUserWithExistingNameThrowsException() {
    userService.createUser( generateValidUserDtoWithoutPhoto("Name1"));
    UserDto createdUser = userService.createUser( generateValidUserDtoWithoutPhoto("Name2"));

    UpdateUserRequest updateUserRequest = UpdateUserRequest.builder()
        .username("Name1")
        .age(createdUser.age())
        .country(createdUser.country())
        .email(createdUser.email())
        .build();

    assertThrows(UserWithUsernameAlreadyExistsException.class, () -> {
      userService.updateUser(createdUser.id(), updateUserRequest);
    });
  }
}