package com.example.web.handler.endpoints;

import static com.example.web.utils.Constants.OBJECT_MAPPER;

import com.example.web.model.GameRoom;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;

@ClientEndpoint
@RequiredArgsConstructor
public class TestInitialCardsReceivedEndpoint {

  private final GameRoom gameRoom;
  private final CompletableFuture<String> completableFuture;
  private int playerId = 0;

  @OnOpen
  public void onOpen(Session session) {
    System.out.println("✅ WebSocket connection established");

    try {
      session.getAsyncRemote().sendText(OBJECT_MAPPER.writeValueAsString(Map.of(
          "type", "INITIAL_CARDS_RECEIVED",
          "roomId", gameRoom.getRoomId(),
          "playerId", String.valueOf(playerId++)
      )));
    } catch (Exception e) {
      e.printStackTrace();
      completableFuture.completeExceptionally(e);
    }
  }

  @OnMessage
  public void onMessage(String message) {
    System.out.println("📥 Received message: " + message);
    completableFuture.complete(message);
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    System.err.println("❌ WebSocket error: " + throwable.getMessage());
    completableFuture.completeExceptionally(throwable);
  }

  @OnClose
  public void onClose(Session session, CloseReason closeReason) {
    System.out.println("❌ WebSocket connection closed: " + closeReason.getReasonPhrase());
  }
}