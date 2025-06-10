package com.example.web.handler;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;
import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketHandlerIntegrationTest {

  @LocalServerPort
  private int port;

  private URI WS_URI;

  @BeforeEach
  void setUp() throws Exception {
    WS_URI = new URI("ws://localhost:" + port + "/game/prepare");
  }

  @Test
  void testRawWebSocketJoinRoom() throws Exception {
    CompletableFuture<String> future = new CompletableFuture<>();

    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    TestEndpoint endpoint = new TestEndpoint(future);

    container.connectToServer(endpoint, WS_URI);
    container.connectToServer(endpoint, WS_URI);

    String response = future.get(5, TimeUnit.SECONDS);

    assertThat(response).contains("GAME_STARTED");

    System.out.println("✅ Test completed successfully: " + response);
  }
}