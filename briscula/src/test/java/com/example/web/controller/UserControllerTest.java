package com.example.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utils.EntityUtils.PHOTO_ID;
import static utils.EntityUtils.buildUserDtoMultipartRequest;
import static utils.EntityUtils.buildValidUserDtoMultipartRequest;
import static utils.EntityUtils.randomAge;
import static utils.EntityUtils.randomCountry;
import static utils.EntityUtils.randomPassword;
import static utils.EntityUtils.randomUsername;

import com.example.web.dto.ErrorResponse;
import com.example.web.dto.user.UserDto;
import com.example.web.utils.JsonUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import utils.AuthService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private static String userToken, adminToken;

  private AuthService authService;

  private static boolean loggedIn = false;

  @BeforeEach
  void setUp() throws Exception {
    if (!loggedIn) {
      userToken = new AuthService(mockMvc).getUserBearerToken();
      adminToken = new AuthService(mockMvc).getAdminBearerToken();
      loggedIn = true;
    }
  }

  @Test
  void loginUser() throws Exception {
    String loginPayload = "{ \"username\": \"" + "user1" + "\", \"password\": \"" + "user" + "\" }";

    mockMvc.perform(post("/api/users/login")
            .contentType("application/json")
            .content(loginPayload))
        .andExpect(status().isOk());
  }

  @Test
  void createUserSuccess() throws Exception {
    mockMvc.perform(buildValidUserDtoMultipartRequest("/api/users/create"))
        .andExpect(status().isOk());
  }

  @Test
  void createUserSuccessThrowsUserAlreadyExistsException_UsernameIsUsed() throws Exception {
    MockMultipartHttpServletRequestBuilder firstRequest = buildValidUserDtoMultipartRequest("/api/users/create");

    mockMvc.perform(firstRequest)
        .andExpect(status().isOk());

    mockMvc.perform(firstRequest)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andDo(result -> {
          ObjectMapper objectMapper = new ObjectMapper();
          objectMapper.registerModule(new JavaTimeModule());
          ErrorResponse errorResponse = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              ErrorResponse.class
          );

          assertThat(errorResponse.status()).isEqualTo(400);
          assertThat(errorResponse.error()).isEqualTo("User Already Exists");
          assertThat(errorResponse.message()).isEqualTo("Username is already taken!");
          assertThat(errorResponse.timestamp()).isNotNull();
        });

  }

  @Test
  void createUserSuccessThrowsUserAlreadyExistsException_EmailIsUsed() throws Exception {
    UserDto firstUserDto = UserDto.builder()
        .username(randomUsername())
        .age(randomAge())
        .country(randomCountry())
        .email("usedemail@gmail.com")
        .password(randomPassword())
        .photoId(PHOTO_ID)
        .build();

    UserDto secondUserDto = UserDto.builder()
        .username(randomUsername())
        .age(randomAge())
        .country(randomCountry())
        .email("usedemail@gmail.com")
        .password(randomPassword())
        .photoId(PHOTO_ID)
        .build();

    MockMultipartHttpServletRequestBuilder firstRequest = buildUserDtoMultipartRequest("/api/users/create", firstUserDto);
    MockMultipartHttpServletRequestBuilder secondRequest = buildUserDtoMultipartRequest("/api/users/create", secondUserDto);

    mockMvc.perform(firstRequest)
        .andExpect(status().isOk());

    mockMvc.perform(secondRequest)
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andDo(result -> {
          ObjectMapper objectMapper = new ObjectMapper();
          objectMapper.registerModule(new JavaTimeModule());
          ErrorResponse errorResponse = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              ErrorResponse.class
          );

          assertThat(errorResponse.status()).isEqualTo(400);
          assertThat(errorResponse.error()).isEqualTo("User Already Exists");
          assertThat(errorResponse.message()).isEqualTo("Email is already taken!");
          assertThat(errorResponse.timestamp()).isNotNull();
        });

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
    String createdUserJson = mockMvc.perform(buildValidUserDtoMultipartRequest("/api/users/create"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    UserDto createdUser = JsonUtils.fromJson(createdUserJson, UserDto.class);

    String userJson = mockMvc.perform(get("/api/users/by")
            .param("id", createdUser.id())
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
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andDo(result -> {
          ObjectMapper objectMapper = new ObjectMapper();
          objectMapper.registerModule(new JavaTimeModule());
          ErrorResponse errorResponse = objectMapper.readValue(
              result.getResponse().getContentAsString(),
              ErrorResponse.class
          );

          assertThat(errorResponse.status()).isEqualTo(404);
          assertThat(errorResponse.error()).isEqualTo("Entity Not Found");
          assertThat(errorResponse.message()).isEqualTo("User not found with id: NON-EXISTING-ID");
          assertThat(errorResponse.timestamp()).isNotNull();
        });
  }

  @Test
  void deleteUserSuccess() throws Exception {
    String responseContent = mockMvc.perform(buildValidUserDtoMultipartRequest("/api/users/create"))
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

    mockMvc.perform(get("/api/users/by")
            .param("id", userId)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateUserSuccess() throws Exception {
    String createdUserJson = mockMvc.perform(buildValidUserDtoMultipartRequest("/api/users/create"))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();

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

    mockMvc.perform(put("/api/users/{id}", userId)
            .header("Authorization", userToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(userUpdatePayload))
        .andExpect(status().isOk())
        .andDo(print());

    String finalUserJson = mockMvc.perform(get("/api/users/by")
            .param("id", userId)
            .header("Authorization", adminToken)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn()
        .getResponse()
        .getContentAsString();

    UserDto finalUserObject = JsonUtils.fromJson(finalUserJson, UserDto.class);
    assertThat(finalUserObject.email()).isEqualTo("updateemail@gmail.com");
  }

}
