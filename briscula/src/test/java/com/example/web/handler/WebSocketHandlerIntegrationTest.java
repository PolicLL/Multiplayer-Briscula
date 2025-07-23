package com.example.web.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.EntityUtils.getConnectedPlayer;

import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.handler.endpoints.TestCardChosenEndpoint;
import com.example.web.handler.endpoints.TestGetChooseCardMessageEndpoint;
import com.example.web.handler.endpoints.TestGetInitialCardsEndpoint;
import com.example.web.handler.endpoints.TestJoinRoomEndpoint;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.GameRoom;
import com.example.web.service.GameRoomService;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

class WebSocketHandlerIntegrationTest extends AbstractIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private GameRoomService gameRoomService;

  private List<ConnectedPlayer> connectedPlayers = new ArrayList<>();

  private GameRoom gameRoom;

  private URI WS_URI;

  @BeforeEach
  void setUp() throws Exception {
    WS_URI = new URI("ws://localhost:" + port + "/game");

    connectedPlayers.addAll(List.of(getConnectedPlayer(), getConnectedPlayer()));

    gameRoom = gameRoomService.createRoom(connectedPlayers, GameOptionNumberOfPlayers.TWO_PLAYERS, true);

    gameRoom.getPlayers()
        .stream()
        .map(ConnectedPlayer::getPlayer)
        .map(RealPlayer.class::cast)
        .forEach(realPlayer -> realPlayer.setSelectedCardFuture(new CompletableFuture<>()));
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
  @Order(1)
  void testRawWebSocketInitialCardsReceived() throws Exception {
    CompletableFuture<String> future1 = new CompletableFuture<>();
    CompletableFuture<String> future2 = new CompletableFuture<>();
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();

    TestGetChooseCardMessageEndpoint endpoint1 = new TestGetChooseCardMessageEndpoint(gameRoom, future1, connectedPlayers.get(0));
    TestGetChooseCardMessageEndpoint endpoint2 = new TestGetChooseCardMessageEndpoint(gameRoom, future2,  connectedPlayers.get(1));

    container.connectToServer(endpoint1, WS_URI); // ✅ separate instance
    container.connectToServer(endpoint2, WS_URI); // ✅ separate instance

    String response1 = "";
    String response2 = "";

    try {
      response1 = future1.get(30, TimeUnit.SECONDS);
    } catch (Exception e) {
      System.out.println("⚠️ future1 did not complete in time: " + e.getMessage());
    }

    try {
      response2 = future2.get(30, TimeUnit.SECONDS);
    } catch (Exception e) {
      System.out.println("⚠️ future2 did not complete in time: " + e.getMessage());
    }

    assertThat(response1 + response2).contains("CHOOSE_CARD");

    System.out.println("✅ Test completed successfully: " + response1);
    System.out.println("✅ Test completed successfully: " + response2);
  }

  /**
   * This test will pass if no exception has been thrown.
   */
  @Test
  void testRawWebSocketCardChosen() throws Exception {
    CompletableFuture<String> future = new CompletableFuture<>();

    WebSocketContainer container = ContainerProvider.getWebSocketContainer();

    TestCardChosenEndpoint endpoint = new TestCardChosenEndpoint(gameRoom, future);

    container.connectToServer(endpoint, WS_URI);

    RealPlayer realPlayer = (RealPlayer) gameRoom.getPlayers().get(0).getPlayer();
    assertThat(realPlayer.getSelectedCardFuture().get()).isEqualTo(0);

    System.out.println("✅ Test completed successfully!");
  }

}