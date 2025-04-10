package com.example.web.handler;

import com.example.briscula.game.GameManager;
import com.example.briscula.utilities.constants.GameMode;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.model.ConnectedPlayer;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class GameStartWebSocketHandler extends TextWebSocketHandler {

  private static final Set<ConnectedPlayer> setOfPlayers = new HashSet<>();

  private final int MAX_NUMBER_OF_PLAYERS = 2;
  @Override
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) {
//    GameManager gameManager = new GameManager(
//        GameOptionNumberOfPlayers.TWO_PLAYERS,
//        GameMode.ALL_HUMANS, setOfPlayers.stream().map(ConnectedPlayer::getPlayer).toList()
//    );

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
