package com.example.web.handler;

import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

  private static final Set<WebSocketSession> sessions = new HashSet<>();

  @Override
  public void afterConnectionEstablished(@NonNull WebSocketSession session) {
    log.info("Entered session.");
    sessions.add(session);
    log.info("Number of sessions: " + sessions.size());
  }

  @Override
  protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {

  }

}
