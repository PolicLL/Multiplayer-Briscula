package com.example.web.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.EntityUtils.createTournamentCreateDto;
import static utils.EntityUtils.generateValidUserDtoWithoutPhoto;

import com.example.web.dto.tournament.JoinTournamentResponse;
import com.example.web.dto.user.UserDto;
import com.example.web.handler.endpoints.tournament.TestJoinTournamentEndpoint;
import com.example.web.service.TournamentService;
import com.example.web.service.UserService;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

class TournamentWebSocketHandlerIntegrationTest extends AbstractIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TournamentService tournamentService;

  @Autowired
  private UserService userService;

  private String tournamentId;

  private List<String> userIds = new ArrayList<>();

  private URI WS_URI;

  @BeforeEach
  void setUp() throws Exception {
    WS_URI = new URI("ws://localhost:" + port + "/tournament");

    tournamentId = tournamentService.create(createTournamentCreateDto())
        .id();

    for (int i = 0; i < 4; ++i) {
      UserDto created = userService.createUser(generateValidUserDtoWithoutPhoto());
      userIds.add(created.id());
      System.out.println("CREATED : " + created);
    }

  }

  // TODO: Test is sometimes failing, has to be rechecked
  @Test
  void testRawWebSocketJoinTournament() throws Exception {
    List<CompletableFuture<JoinTournamentResponse>> futures = new ArrayList<>();
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    List<TestJoinTournamentEndpoint> endpoints = new ArrayList<>();

    for (int i = 0; i < 4; ++i) {
      futures.add(new CompletableFuture<>());
      endpoints.add(new TestJoinTournamentEndpoint(futures.get(i), tournamentId, userIds.get(i)));
      container.connectToServer(endpoints.get(i), WS_URI);
    }

    JoinTournamentResponse response1 = futures.get(0).get(30, TimeUnit.SECONDS);
    JoinTournamentResponse response2 = futures.get(1).get(30, TimeUnit.SECONDS);
    JoinTournamentResponse response3 = futures.get(2).get(30, TimeUnit.SECONDS);
    JoinTournamentResponse response4 = futures.get(3).get(30, TimeUnit.SECONDS);

    assertThat(response1.currentNumberOfPlayers()).isEqualTo(4);
    assertThat(response2.currentNumberOfPlayers()).isEqualTo(4);
    assertThat(response3.currentNumberOfPlayers()).isEqualTo(4);
    assertThat(response4.currentNumberOfPlayers()).isEqualTo(4);

    System.out.println("✅ Test completed successfully");
  }

}
