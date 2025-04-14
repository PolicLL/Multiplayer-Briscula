package com.example.web.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LobbyService {
  private static final int MAX_PLAYERS_PER_ROOM = 2;

  private final Map<String, String> waitingPlayers = new LinkedHashMap<>();
  private final Map<String, String> playerInRoomMap = new HashMap<>();
  private final Map<String, List<String>> roomPlayers = new HashMap<>();

  public synchronized String addPlayer(String playerName) {
    String playerId = UUID.randomUUID().toString();
    waitingPlayers.put(playerId, playerName);
    log.info("Player '{}' joined with ID: {}", playerName, playerId);

    if (waitingPlayers.size() >= MAX_PLAYERS_PER_ROOM) {
      List<String> playersToAssign = new ArrayList<>();
      Iterator<Map.Entry<String, String>> iterator = waitingPlayers.entrySet().iterator();

      for (int i = 0; i < MAX_PLAYERS_PER_ROOM && iterator.hasNext(); i++) {
        playersToAssign.add(iterator.next().getKey());
      }

      String roomId = UUID.randomUUID().toString();
      log.info("Creating room {} for players: {}", roomId, playersToAssign);

      for (String tempPlayerId : playersToAssign) {
        playerInRoomMap.put(tempPlayerId, roomId);
        waitingPlayers.remove(tempPlayerId);
      }

      roomPlayers.put(roomId, playersToAssign);
      log.info("Room {} now has players: {}", roomId, roomPlayers.get(roomId));
    } else {
      log.info("Waiting for more players to join. Current count: {}", waitingPlayers.size());
    }

    return playerId;
  }

  public synchronized Optional<String> getAssignedRoomId(String playerId) {
    String roomId = playerInRoomMap.get(playerId);
    if (roomId != null) {
      log.info("Found room {} for player {}", roomId, playerId);
    }
    return Optional.ofNullable(roomId);
  }

  public synchronized List<String> getPlayersInRoom(String roomId) {
    List<String> players = roomPlayers.getOrDefault(roomId, Collections.emptyList());
    log.info("Retrieved players in room {}: {}", roomId, players);
    return players;
  }

  public synchronized void removePlayer(String playerId) {
    String roomId = playerInRoomMap.remove(playerId);
    waitingPlayers.remove(playerId);
    log.info("Removing player {} from lobby and room {}", playerId, roomId);

    if (roomId != null) {
      List<String> players = roomPlayers.getOrDefault(roomId, new ArrayList<>());
      players.remove(playerId);
      log.info("Updated players in room {}: {}", roomId, players);
    }
  }
}
