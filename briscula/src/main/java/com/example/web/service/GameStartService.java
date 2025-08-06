package com.example.web.service;

import com.example.web.model.GameRoom;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GameStartService {

  private final GameRoomService gameRoomService;
  private final GameEndService gameEndService;

  public void startGame(String roomId) {
    if (gameRoomService.areInitialCardsReceived(roomId)) {
      log.info("Initial cards for room {} are received.", roomId);
      GameRoom gameRoom = gameRoomService.getRoom(roomId);
      log.info("Starting the game between {} and {}.", gameRoom.getPlayers().get(0).getPlayer().getNickname(),
          gameRoom.getPlayers().get(1).getPlayer().getNickname());
      CompletableFuture
          .supplyAsync(gameRoom::startGame)
          .thenAccept(gameEndStatus -> gameEndService.update(gameEndStatus, gameRoom.getMatchId()))
          .exceptionally(ex -> {
            log.error("Game failed due to error: ", ex);
            return null;
          });

    }
  }
}
