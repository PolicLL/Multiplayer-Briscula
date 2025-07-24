package com.example.web.handler.endpoints;

import static com.example.web.utils.Constants.OBJECT_MAPPER;
import static utils.EntityUtils.getPlayerName;
import static utils.EntityUtils.getUserId;

import com.example.web.dto.Message;
import com.fasterxml.jackson.core.JsonProcessingException;
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

  private final int playerId;
  private final CompletableFuture<String> completableFuture;
  private Session session;
  private String roomId;

  @OnOpen
  public void onOpen(Session session) {
    System.out.println("✅ WebSocket connection established");

    this.session = session;
    System.out.println("Web socket session: " + session + " id: " + session.getId());

    try {
      session.getAsyncRemote().sendText(OBJECT_MAPPER.writeValueAsString(Map.of(
          "type", "JOIN_ROOM",
          "playerName", getPlayerName(),
          "shouldShowPoints", "true",
          "numberOfPlayers", 2,
          "userId" , getUserId()
      )));
    } catch (Exception e) {
      e.printStackTrace();
      completableFuture.completeExceptionally(e);
    }

    System.out.println("✅ WebSocket connection established");
  }

  @OnMessage
  public void onMessage(String message) throws JsonProcessingException {
    System.out.println("📥 Received message: " + message);

    if (message.contains("GAME_STARTED")) {

      Message convertedMessage = OBJECT_MAPPER.readValue(message, Message.class);

      roomId = convertedMessage.roomId();

      session.getAsyncRemote().sendText(OBJECT_MAPPER.writeValueAsString(Map.of(
          "type", "GET_INITIAL_CARDS",
          "roomId", roomId,
          "playerId", playerId
      )));
    }
    else if (message.contains("SENT_INITIAL_CARDS")) {
      try {
        session.getAsyncRemote().sendText(OBJECT_MAPPER.writeValueAsString(Map.of(
            "type", "INITIAL_CARDS_RECEIVED",
            "roomId", roomId,
            "playerId", String.valueOf(playerId)
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