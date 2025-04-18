package com.example.web.handler;

import com.example.web.model.ConnectedPlayer;
import com.example.web.model.GameRoom;
import com.example.web.service.GameRoomService;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@RequiredArgsConstructor
public class GameStartWebSocketHandler extends TextWebSocketHandler {

  private static final Set<ConnectedPlayer> setOfPlayers = new HashSet<>();


  private final GameRoomService gameRoomService;
  private final int MAX_NUMBER_OF_PLAYERS = 2;
  @Override
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message)
      throws IOException {

    String uri = session.getUri().toString(); // e.g., ws://localhost:8080/game/abc123
    String roomId = uri.substring(uri.lastIndexOf("/") + 1); // Extracts "abc123"

    log.info("Received message in room: " + roomId);

    log.info("Received room from service {}" , gameRoomService.getRoom(roomId));

    GameRoom gameRoom = gameRoomService.getRoom(roomId);
    //gameRoom.sendMessage(0, "TEST 123 123");
    //session.sendMessage(new TextMessage("TEST 123 123"));

  }
}
