package com.example.web.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class TournamentWebSocketHandler extends TextWebSocketHandler {

  private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
  private final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    sessions.add(session);
    log.info("New tournament WebSocket connection: {}", session.getId());
  }

  @Override
  public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) {

  }

  @Override
  public void handleTransportError(WebSocketSession session, Throwable exception) {
    log.error("Tournament WebSocket error:", exception);
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
    sessions.remove(session);
    log.info("Tournament WebSocket disconnected: {}", session.getId());
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }

  public void broadcastTournamentUpdate(Object updatedTournament) {
    synchronized (sessions) {
      for (WebSocketSession session : sessions) {
        try {
          if (session.isOpen()) {
            String json = objectMapper.writeValueAsString(updatedTournament);
            session.sendMessage(new TextMessage(json));
          }
        } catch (Exception e) {
          log.error("Error sending tournament update to session {}: {}", session.getId(), e.getMessage());
        }
      }
    }
  }
}