package com.example.web.controller;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.web.model.User;
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

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  private User validUser;
  private User userWithTakenUsername;
  private User userWithRegisteredEmail;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);

    validUser = User.builder()
        .username("John Doe")
        .age(30)
        .country("USA")
        .email("john@gmail.com")
        .level(1)
        .points(100)
        .build();

    userWithTakenUsername = User.builder()
        .username("taken_username")
        .age(25)
        .country("USA")
        .email("taken@example.com")
        .level(2)
        .points(200)
        .build();

    userWithRegisteredEmail = User.builder()
        .username("new_user")
        .age(28)
        .country("USA")
        .email("john@example.com")
        .level(2)
        .points(150)
        .build();

  }

  @Test
  void createUserSuccess() throws Exception {
    String registrationPayload = "{\"username\": \"John Doe\", \"age\": 30, \"country\": \"USA\","
        + " \"email\": \"john@gmail.com\", \"level\": 1, \"points\": 100}";

    mockMvc.perform(post("/api/users/create")
            .contentType("application/json")
            .content(registrationPayload))
        .andExpect(status().isOk());

    mockMvc.perform(get("/api/users")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andDo(print());
  }


}
