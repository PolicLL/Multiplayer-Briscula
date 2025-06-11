package com.example.web.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.EntityUtils.getConnectedPlayer;

import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.handler.endpoints.TestGetInitialCardsEndpoint;
import com.example.web.handler.endpoints.TestInitialCardsReceivedEndpoint;
import com.example.web.handler.endpoints.TestJoinRoomEndpoint;
import com.example.web.model.GameRoom;
import com.example.web.service.GameRoomService;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketHandlerIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private GameRoomService gameRoomService;

  private GameRoom gameRoom;

  private URI WS_URI;

  @BeforeEach
  void setUp() throws Exception {
    WS_URI = new URI("ws://localhost:" + port + "/game/prepare");

    gameRoom = gameRoomService.createRoom(List.of(getConnectedPlayer(), getConnectedPlayer()),
        GameOptionNumberOfPlayers.TWO_PLAYERS);
  }

  @Test
  void testRawWebSocketJoinRoom() throws Exception {
    CompletableFuture<String> future = new CompletableFuture<>();

    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    TestJoinRoomEndpoint endpoint = new TestJoinRoomEndpoint(future);

    container.connectToServer(endpoint, WS_URI);
    container.connectToServer(endpoint, WS_URI);

    String response = future.get(5, TimeUnit.SECONDS);

    assertThat(response).contains("GAME_STARTED");

    System.out.println("✅ Test completed successfully: " + response);
  }

  @Test
  void testRawWebSocketGetInitialCards() throws Exception {
    CompletableFuture<String> future = new CompletableFuture<>();
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();

    TestGetInitialCardsEndpoint endpoint = new TestGetInitialCardsEndpoint(future, gameRoom);

    container.connectToServer(endpoint, WS_URI);

    String response = future.get(5, TimeUnit.SECONDS);

    assertThat(response)
        .contains("SENT_INITIAL_CARDS")
        .contains("SENT_MAIN_CARD");

    System.out.println("✅ Test completed successfully: " + response);
  }

  @Test
  void testRawWebSocketInitialCardsReceived() throws Exception {
    CompletableFuture<String> future = new CompletableFuture<>();
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();

    TestGetInitialCardsEndpoint endpointGetInitial = new TestGetInitialCardsEndpoint(future, gameRoom);
    container.connectToServer(endpointGetInitial, WS_URI);
    container.connectToServer(endpointGetInitial, WS_URI);

    TestInitialCardsReceivedEndpoint endpoint = new TestInitialCardsReceivedEndpoint(gameRoom, future);
    container.connectToServer(endpoint, WS_URI);
    container.connectToServer(endpoint, WS_URI);

    String response = future.get(5, TimeUnit.SECONDS);

    System.out.println("RESPONSE: " + response);

    assertThat(response)
        .contains("CHOOSE_CARD");

    System.out.println("✅ Test completed successfully: " + response);
  }
}