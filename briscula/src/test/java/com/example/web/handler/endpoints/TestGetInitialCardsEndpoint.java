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
public class TestGetInitialCardsEndpoint {

  private final CompletableFuture<String> completableFuture;
  private final GameRoom gameRoom;
  private int tempNumberOfMessages = 0;
  private final StringBuilder tempMessage = new StringBuilder();

  @OnOpen
  public void onOpen(Session session) {
    System.out.println("✅ WebSocket connection established");
    System.out.println("Session id : " + gameRoom.getPlayers().get(0).getWebSocketSession().getId());
    System.out.println("Session id : " + gameRoom.getPlayers().get(1).getWebSocketSession().getId());
    try {
      session.getAsyncRemote().sendText(OBJECT_MAPPER.writeValueAsString(Map.of(
          "type", "GET_INITIAL_CARDS",
          "roomId", gameRoom.getRoomId(),
          "playerId", "1"
      )));
    } catch (Exception e) {
      e.printStackTrace();
      completableFuture.completeExceptionally(e);
    }
  }

  @OnMessage
  public void onMessage(String message) {
    System.out.println("📥 Received message: " + message);

    tempMessage.append(message);
    ++tempNumberOfMessages;

    if (tempNumberOfMessages == 2) {
      completableFuture.complete(tempMessage.toString());
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