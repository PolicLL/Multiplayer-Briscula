package com.example.web.handler;

import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

public interface WebSocketMessageHandler {
  void handle(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message);
}
