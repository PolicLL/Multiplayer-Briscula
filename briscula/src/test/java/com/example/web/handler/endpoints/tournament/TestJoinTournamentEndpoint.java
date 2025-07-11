package com.example.web.handler.endpoints.tournament;

import static com.example.web.utils.Constants.OBJECT_MAPPER;
import static com.example.web.utils.JsonUtils.fromJson;

import com.example.web.dto.tournament.JoinTournamentResponse;
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
public class TestJoinTournamentEndpoint {

  private final CompletableFuture<JoinTournamentResponse> completableFuture;

  private final String tournamentId;
  private final String userId;


  @OnOpen
  public void onOpen(Session session) {
    System.out.println("✅ WebSocket connection established");
    try {
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
    JoinTournamentResponse joinTournamentResponse;
    try {
      joinTournamentResponse = fromJson(message, JoinTournamentResponse.class);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    if (joinTournamentResponse.currentNumberOfPlayers() == 4) {
      System.out.println("CONTAINS");
      completableFuture.complete(joinTournamentResponse);
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