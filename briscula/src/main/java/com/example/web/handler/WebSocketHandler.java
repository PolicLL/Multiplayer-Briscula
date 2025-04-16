package com.example.web.handler;

import com.example.web.model.ConnectedPlayer;
import com.example.web.model.GameRoom;
import com.example.web.service.GameRoomService;
import com.example.web.service.GameStartService;
import com.example.web.service.GamePrepareService;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends TextWebSocketHandler {

  private GameRoom gameRoom;
  private List<ConnectedPlayer> connectedPlayerList;
  
  @Override
  public void handleMessage(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message)
      throws JsonProcessingException {


  }

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) throws Exception {
    log.info("WebSocket connection established: {}", session.getId());
  }
}
