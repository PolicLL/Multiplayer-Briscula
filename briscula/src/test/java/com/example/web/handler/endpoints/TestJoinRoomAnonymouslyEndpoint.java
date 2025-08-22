package com.example.web.handler.endpoints;

import jakarta.websocket.*;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.example.web.utils.Constants.OBJECT_MAPPER;

@ClientEndpoint
@RequiredArgsConstructor
public class TestJoinRoomAnonymouslyEndpoint {

  private final CompletableFuture<String> completableFuture;

  @OnOpen
  public void onOpen(Session session) {
    System.out.println("✅ WebSocket connection established");

    String alreadyUsedNameByRegisteredUser = "User1950";

    try {
      session.getAsyncRemote().sendText(OBJECT_MAPPER.writeValueAsString(Map.of(
              "type", "LOGGED_IN"
      )));

      session.getAsyncRemote().sendText(OBJECT_MAPPER.writeValueAsString(Map.of(
          "type", "JOIN_ROOM",
          "playerName", alreadyUsedNameByRegisteredUser,
          "shouldShowPoints", "true",
          "numberOfPlayers", 2
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
    if (!completableFuture.isDone()) {
      completableFuture.completeExceptionally(
          new RuntimeException("WebSocket closed with code " + closeReason.getCloseCode().getCode())
      );
    }
  }

}