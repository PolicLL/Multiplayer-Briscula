package com.example.web.handler.endpoints.tournament;

import static com.example.web.utils.Constants.OBJECT_MAPPER;
import static utils.EntityUtils.getPlayerName;
import static utils.EntityUtils.getUserId;

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
public class TestJoinRoomThenJoinTournamentEndpoint {

  private final CompletableFuture<String> completableFuture;

  private final String tournamentId;
  private final String userId;


  @OnOpen
  public void onOpen(Session session) {
    System.out.println("✅ WebSocket connection established");
    try {
      session.getAsyncRemote().sendText(OBJECT_MAPPER.writeValueAsString(Map.of(
          "type", "LOGGED_IN"
      )));

      session.getAsyncRemote().sendText(OBJECT_MAPPER.writeValueAsString(Map.of(
          "type", "JOIN_ROOM",
          "playerName", getPlayerName(),
          "shouldShowPoints", "true",
          "numberOfPlayers", 2,
          "userId" , getUserId()
      )));

      session.getAsyncRemote().sendText(OBJECT_MAPPER.writeValueAsString(Map.of(
          "type", "JOIN_TOURNAMENT",
          "tournamentId", tournamentId,
          "playerId", userId
      )));
    } catch (Exception e) {
      e.printStackTrace();
      completableFuture.completeExceptionally(e);
    }
  }

  @OnMessage
  public void onMessage(String message) {
    System.out.println("📥 Received message: " + message);

    if (message.contains("USER_ALREADY_IN_GAME_OR_TOURNAMENT")) {
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