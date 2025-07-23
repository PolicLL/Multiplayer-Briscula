package com.example.web.handler.endpoints;

import static com.example.web.utils.Constants.OBJECT_MAPPER;

import com.example.web.model.ConnectedPlayer;
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
public class TestGetChooseCardMessageEndpoint {

  private final GameRoom gameRoom;
  private final CompletableFuture<String> completableFuture;
  private final ConnectedPlayer connectedPlayer;
  private static int playerId = 0;
  private Session session;

  @OnOpen
  public void onOpen(Session session) {
    System.out.println("✅ WebSocket connection established");

    this.session = session;

    try {
      session.getAsyncRemote().sendText(OBJECT_MAPPER.writeValueAsString(Map.of(
          "type", "GET_INITIAL_CARDS",
          "roomId", gameRoom.getRoomId(),
          "playerId", connectedPlayer.getId()
      )));
    } catch (Exception e) {
      e.printStackTrace();
      completableFuture.completeExceptionally(e);
    }

    System.out.println("✅ WebSocket connection established");
  }

  @OnMessage
  public void onMessage(String message) {
    System.out.println("📥 Received message: " + message);

    if (message.contains("SENT_INITIAL_CARDS")) {
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
    } else if (message.contains("CHOOSE_CARD")) {
      completableFuture.complete(message);
    }
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