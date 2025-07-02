package com.example.validation;

import static com.example.web.utils.JsonUtils.fromJsonUsingJavaTimeModule;
import static com.example.web.utils.JsonUtils.toJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.web.dto.ErrorResponse;
import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.model.enums.TournamentStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class TournamentValidationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldFailWhenNameIsBlank() throws Exception {
    TournamentCreateDto dto = TournamentCreateDto.builder()
        .name("  ")
        .numberOfPlayers(8)
        .status(TournamentStatus.INITIALIZING)
        .roundsToWin(2)
        .build();

    String responseJson = mockMvc.perform(post("/api/tournament")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(dto)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse().getContentAsString();

    ErrorResponse error = fromJsonUsingJavaTimeModule(responseJson, ErrorResponse.class);
    assertThat(error.message()).contains("Name is mandatory");
  }

  @Test
  void shouldFailWhenNumberOfPlayersIsTooLow() throws Exception {
    TournamentCreateDto dto = TournamentCreateDto.builder()
        .name("Tournament")
        .numberOfPlayers(2)
        .status(TournamentStatus.INITIALIZING)
        .roundsToWin(2)
        .build();

    String responseJson = mockMvc.perform(post("/api/tournament")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(dto)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse().getContentAsString();

    ErrorResponse error = fromJsonUsingJavaTimeModule(responseJson, ErrorResponse.class);
    assertThat(error.message()).contains("Number of players must be one of 4, 8, 16, 32");
  }

  @Test
  void shouldFailWhenRoundsToWinIsTooHigh() throws Exception {
    TournamentCreateDto dto = TournamentCreateDto.builder()
        .name("Tournament")
        .numberOfPlayers(8)
        .status(TournamentStatus.INITIALIZING)
        .roundsToWin(5)
        .build();

    String responseJson = mockMvc.perform(post("/api/tournament")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(dto)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse().getContentAsString();

    ErrorResponse error = fromJsonUsingJavaTimeModule(responseJson, ErrorResponse.class);
    assertThat(error.message()).contains("Rounds to win must be between 1 and 4");
  }

  @Test
  void shouldFailWhenStatusIsNull() throws Exception {
    // send raw JSON without "status" field
    String json = """
        {
          "name": "Tournament",
          "numberOfPlayers": 8,
          "roundsToWin": 2
        }
        """;

    String responseJson = mockMvc.perform(post("/api/tournament")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse().getContentAsString();

    ErrorResponse error = fromJsonUsingJavaTimeModule(responseJson, ErrorResponse.class);
    assertThat(error.message()).contains("Status is mandatory");
  }

  @Test
  void shouldReturnMultipleErrorsWhenAllFieldsInvalid() throws Exception {
    TournamentCreateDto dto = TournamentCreateDto.builder()
        .name("")
        .numberOfPlayers(99)
        .roundsToWin(0)
        .status(null)
        .build();

    String responseJson = mockMvc.perform(post("/api/tournament")
            .contentType(MediaType.APPLICATION_JSON)
            .content(toJson(dto)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse().getContentAsString();

    ErrorResponse error = fromJsonUsingJavaTimeModule(responseJson, ErrorResponse.class);
    assertThat(error.message())
        .contains("Name is mandatory")
        .contains("Number of players must be one of 4, 8, 16, 32")
        .contains("Rounds to win must be between 1 and 4")
        .contains("Status is mandatory");
  }
}