package com.example.web.service;

import com.example.web.model.ConnectedPlayer;
import com.example.web.model.GameRoom;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class GameRoomService {

  private final Map<String, GameRoom> activeRooms = new ConcurrentHashMap<>();

  public GameRoom createRoom(Collection<ConnectedPlayer> playerList) {
    GameRoom room = new GameRoom(playerList);
    activeRooms.put(room.getRoomId(), room);
    return room;
  }

  public GameRoom getRoom(String roomId) {
    return activeRooms.get(roomId);
  }

  public void removeRoom(String roomId) {
    activeRooms.remove(roomId);
  }

  public Collection<GameRoom> getAllRooms() {
    return activeRooms.values();
  }
}
