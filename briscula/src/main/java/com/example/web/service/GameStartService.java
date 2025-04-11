package com.example.web.service;

import com.example.web.handler.WebSocketMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@Slf4j
public class GameStartService implements WebSocketMessageHandler {

  @Override
  public void handle(@NonNull WebSocketSession session,@NonNull WebSocketMessage<?> message) {
    log.info("Game start.");
  }
}
