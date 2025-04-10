package com.example.web.handler;

import com.example.briscula.game.GameManager;
import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.model.ConnectedPlayer;
import com.example.web.service.GameRoomService;
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
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) {

    String uri = session.getUri().toString(); // e.g., ws://localhost:8080/game/abc123
    String roomId = uri.substring(uri.lastIndexOf("/") + 1); // Extracts "abc123"

    log.info("Received message in room: " + roomId);

    log.info("Received room from service {}" , gameRoomService.getRoom(roomId));

  }
}

/*
public class GamePreparingWebSocketHandler extends TextWebSocketHandler {
  private final Map<String, GameRoom> activeGames = new ConcurrentHashMap<>();

  @Override
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) {
    String roomId = (String) message.getPayload();
    GameRoom room = activeGames.computeIfAbsent(roomId, GameRoom::new);
    room.handleMessage(session, message);
  }
}

 */
