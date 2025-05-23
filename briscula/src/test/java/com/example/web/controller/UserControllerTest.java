package com.example.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utils.EntityUtils.randomAge;
import static utils.EntityUtils.randomCountry;
import static utils.EntityUtils.randomPassword;
import static utils.EntityUtils.randomUsername;

import com.example.web.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import utils.AuthService;
import utils.EntityUtils;
import com.example.web.utils.JsonUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private String userToken, adminToken;

  @BeforeEach
  void setUp() throws Exception {
    this.userToken = new AuthService(mockMvc).getUserBearerToken();
    this.adminToken = new AuthService(mockMvc).getAdminBearerToken();
  }

  @Test
  void loginUser() throws Exception {
    String loginPayload = "{ \"username\": \"" + "user" + "\", \"password\": \"" + "user" + "\" }";

    mockMvc.perform(post("/api/users/login")
            .contentType("application/json")
            .content(loginPayload))
        .andExpect(status().isOk());
  }

  @Test
  void createUserSuccess() throws Exception {
    String userRegistrationPayload = EntityUtils.generateValidUserDtoInJson();

    mockMvc.perform(post("/api/users/create")
            .contentType("application/json")
            .content(userRegistrationPayload))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/users")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andDo(print());
  }

  @Test
  void createUserSuccessThrowsUserAlreadyExistsException_UsernameIsUsed() throws Exception {
    String userRegistrationPayload = EntityUtils.generateValidUserDtoInJson();

    mockMvc.perform(post("/api/users/create")
            .contentType("application/json")
            .content(userRegistrationPayload))
        .andExpect(status().isOk());

    mockMvc.perform(post("/api/users/create")
            .contentType("application/json")
            .content(userRegistrationPayload))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Username is already taken!"));
  }

  @Test
  void createUserSuccessThrowsUserAlreadyExistsException_EmailIsUsed() throws Exception {
    UserDto firstUserDto = UserDto.builder()
        .username(randomUsername())
        .age(randomAge())
        .country(randomCountry())
        .email("usedemail@gmail.com")
        .password(randomPassword())
        .build();

    UserDto secondUserDto = UserDto.builder()
        .username(randomUsername())
        .age(randomAge())
        .country(randomCountry())
        .email("usedemail@gmail.com")
        .password(randomPassword())
        .build();

    String firstUserPayload = JsonUtils.toJson(firstUserDto);
    String secondUserPayload = JsonUtils.toJson(secondUserDto);

    mockMvc.perform(post("/api/users/create")
            .contentType("application/json")
            .content(firstUserPayload))
        .andExpect(status().isOk());

    mockMvc.perform(post("/api/users/create")
            .contentType("application/json")
            .content(secondUserPayload))
        .andExpect(status().isBadRequest())
        .andExpect(content().string("Email is already taken!"));
  }

  @Test
  void getAllUsersSuccess() throws Exception {
    mockMvc.perform(get("/api/users")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andDo(print());
  }

  @Test
  void getUserById() throws Exception {
    String createdUserJson = mockMvc.perform(post("/api/users/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(EntityUtils.generateValidUserDtoInJson()))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    UserDto createdUser = JsonUtils.fromJson(createdUserJson, UserDto.class);


    String userJson = mockMvc.perform(get("/api/users/by?id={id}", createdUser.id())
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    UserDto finalUserObject = JsonUtils.fromJson(userJson, UserDto.class);
    assertThat(createdUser).isEqualTo(finalUserObject);

  }

  @Test
  void getUserByIdThrowsUserNotFoundException() throws Exception {
    mockMvc.perform(get("/api/users/by?id={id}", "NON-EXISTING-ID")
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(content().string("User not found with id: " + "NON-EXISTING-ID"));
  }

  @Test
  void deleteUserSuccess() throws Exception {
    String userRegistrationPayload =  EntityUtils.generateValidUserDtoInJson();

    String responseContent = mockMvc.perform(post("/api/users/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userRegistrationPayload))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();


    UserDto createdUser = JsonUtils.fromJson(responseContent, UserDto.class);
    String userId = createdUser.id();

    mockMvc.perform(delete("/api/users/" + userId)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/users/by?id=" + userId)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }


  @Test
  void updateUserSuccess() throws Exception {
    String userRegistrationPayload = EntityUtils.generateValidUserDtoInJson();;

    // CREATE
    String createdUserJson = mockMvc.perform(post("/api/users/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(userRegistrationPayload))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    UserDto createdUser = JsonUtils.fromJson(createdUserJson, UserDto.class);
    String userId = createdUser.id();

    String userUpdatePayload = JsonUtils.toJson(UserDto.builder()
        .id(createdUser.id())
        .username(createdUser.username())
        .age(createdUser.age())
        .email("updateemail@gmail.com")
        .country(createdUser.country())
        .password(randomPassword())
        .build());

    // UPDATE
    mockMvc.perform(put("/api/users/{id}", userId)
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(userUpdatePayload))
        .andExpect(status().isOk())
        .andDo(print());

    // GET AND CHECK
    String finalUserJson = mockMvc.perform(get("/api/users/by?id={id}", userId)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    UserDto finalUserObject = JsonUtils.fromJson(finalUserJson, UserDto.class);
    assertThat(finalUserObject.email()).isEqualTo("updateemail@gmail.com");

  }


}
