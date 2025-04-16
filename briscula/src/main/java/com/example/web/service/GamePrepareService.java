package com.example.web.service;

import com.example.briscula.user.player.RealPlayer;
import com.example.web.handler.WebSocketMessageHandler;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.GameRoom;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Component
@RequiredArgsConstructor
public class GamePrepareService implements WebSocketMessageHandler {

  private final Set<ConnectedPlayer> setOfPlayers = new HashSet<>();

  private final GameRoomService gameRoomService;

  private final int MAX_NUMBER_OF_PLAYERS = 2;

  public void handle(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message) {
    ConnectedPlayer connectedPlayer = new ConnectedPlayer(session,
        new RealPlayer(null, (String) message.getPayload()));


    setOfPlayers.add(connectedPlayer);
    log.info("Added player {}.", connectedPlayer);
    log.info("Room size {}", setOfPlayers.size());

    if (setOfPlayers.size() == MAX_NUMBER_OF_PLAYERS) {
      GameRoom gameRoom = gameRoomService.createRoom(setOfPlayers);

      setOfPlayers.forEach(tempUser -> {
        try {
          log.info("Sending message that game room started with id {}.", gameRoom.getRoomId());
          tempUser.getWebSocketSession().sendMessage(new TextMessage("GAME_STARTED " + gameRoom.getRoomId()));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });

      setOfPlayers.clear();
    }
  }

}
