package com.example.web.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static utils.EntityUtils.generateValidTournamentCreateDto;

import com.example.web.dto.tournament.TournamentCreateDto;
import com.example.web.dto.tournament.TournamentResponseDto;
import com.example.web.model.enums.TournamentStatus;
import com.example.web.utils.JsonUtils;
import org.junit.jupiter.api.Test;
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
class TournamentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void createTournamentSuccess() throws Exception {
    TournamentCreateDto createDto = generateValidTournamentCreateDto();

    String requestBody = JsonUtils.toJson(createDto);

    mockMvc.perform(post("/api/tournament")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void getTournamentByIdSuccess() throws Exception {
    TournamentCreateDto createDto = generateValidTournamentCreateDto();
    String requestBody = JsonUtils.toJson(createDto);

    String response = mockMvc.perform(post("/api/tournament")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    TournamentResponseDto created = JsonUtils.fromJson(response, TournamentResponseDto.class);

    String fetched = mockMvc.perform(get("/api/tournament/{id}", created.id()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andReturn().getResponse().getContentAsString();

    TournamentResponseDto fetchedDto = JsonUtils.fromJson(fetched, TournamentResponseDto.class);
    assertThat(fetchedDto).isEqualTo(created);
  }

  @Test
  void getAllTournaments() throws Exception {
    mockMvc.perform(get("/api/tournament"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deleteTournamentSuccess() throws Exception {
    TournamentCreateDto createDto = generateValidTournamentCreateDto();
    String requestBody = JsonUtils.toJson(createDto);

    String response = mockMvc.perform(post("/api/tournament")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    TournamentResponseDto created = JsonUtils.fromJson(response, TournamentResponseDto.class);

    mockMvc.perform(delete("/api/tournament/{id}", created.id()))
        .andExpect(status().isNoContent());

    mockMvc.perform(get("/api/tournament/{id}", created.id()))
        .andExpect(status().isNotFound());
  }

  @Test
  void updateTournamentSuccess() throws Exception {
    TournamentCreateDto createDto = generateValidTournamentCreateDto();
    String requestBody = JsonUtils.toJson(createDto);

    String response = mockMvc.perform(post("/api/tournament")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestBody))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    TournamentResponseDto created = JsonUtils.fromJson(response, TournamentResponseDto.class);

    TournamentCreateDto updatedDto = TournamentCreateDto.builder()
        .name("Updated Tournament")
        .numberOfPlayers(created.numberOfPlayers())
        .status(TournamentStatus.IN_PROGRESS)
        .roundsToWin(created.roundsToWin())
        .build();

    System.out.println(updatedDto);

    mockMvc.perform(put("/api/tournament/{id}", created.id())
            .contentType(MediaType.APPLICATION_JSON)
            .content(JsonUtils.toJson(updatedDto)))
        .andExpect(status().isOk());

    String updatedJson = mockMvc.perform(get("/api/tournament/{id}", created.id()))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    TournamentResponseDto updated = JsonUtils.fromJson(updatedJson, TournamentResponseDto.class);
    assertThat(updated.name()).isEqualTo("Updated Tournament");
    assertThat(updated.status()).isEqualTo(TournamentStatus.IN_PROGRESS);
  }
}
