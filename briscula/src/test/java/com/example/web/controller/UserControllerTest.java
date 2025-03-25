package com.example.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.web.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import utils.EntityUtils;
import utils.JsonUtils;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;


  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createUserSuccess() throws Exception {
    String userRegistrationPayload = EntityUtils.generateValidUserDtoInJson();

    mockMvc.perform(post("/api/users/create")
            .contentType("application/json")
            .content(userRegistrationPayload))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/users")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andDo(print());
  }

  @Test
  void getAllUsersSuccess() throws Exception {
    mockMvc.perform(get("/api/users")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andDo(print());
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
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/users/" + userId)
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
        .email("UPDATE")
        .country(createdUser.country())
        .build());

    // UPDATE
    mockMvc.perform(put("/api/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(userUpdatePayload))
        .andExpect(status().isOk())
        .andDo(print());

    // GET AND CHECK
    String finalUserJson = mockMvc.perform(get("/api/users/{id}", userId)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    UserDto finalUserObject = JsonUtils.fromJson(finalUserJson, UserDto.class);
    assertThat(finalUserObject.email()).isEqualTo("UPDATE");

  }


}
