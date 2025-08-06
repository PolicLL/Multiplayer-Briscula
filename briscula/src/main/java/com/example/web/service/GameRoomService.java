package com.example.web.service;

import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.GameRoom;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GameRoomService {

  private final Map<String, GameRoom> activeRooms = new ConcurrentHashMap<>();

  public GameRoom createRoom(Collection<ConnectedPlayer> playerList,
      GameOptionNumberOfPlayers gameOptionNumberOfPlayers, boolean showPoints) {
    GameRoom room = new GameRoom(playerList, gameOptionNumberOfPlayers, showPoints);
    activeRooms.put(room.getRoomId(), room);
    return room;
  }

  public synchronized void notifyRoomPlayerReceivedInitialCards(String roomId, String playerId) {
    int playerIdInt = Integer.parseInt(playerId);
    List<ConnectedPlayer> playerList = activeRooms.get(roomId).getPlayers();
    Optional<ConnectedPlayer> connectedPlayerOptional = playerList.stream().filter(player -> player.getId() == playerIdInt).findFirst();
    connectedPlayerOptional.ifPresent(connectedPlayer -> connectedPlayer.setInitialCardsReceived(true));
  }

  public boolean areInitialCardsReceived(String roomId) {
    return activeRooms.get(roomId).getPlayers().stream().allMatch(
        ConnectedPlayer::isInitialCardsReceived);
  }

  public GameRoom getRoom(String roomId) {
    return activeRooms.get(roomId);
  }
}
