package com.example.web.service;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@Slf4j
public class WebSocketMessageDispatcher {

  private final Map<WebSocketSession, BlockingQueue<String>> sessionQueues = new ConcurrentHashMap<>();
  private final Map<WebSocketSession, Thread> sessionWorkers = new ConcurrentHashMap<>();

  public void registerSession(WebSocketSession session) {
    if (sessionQueues.containsKey(session)) return;

    BlockingQueue<String> queue = new LinkedBlockingQueue<>();
    sessionQueues.put(session, queue);

    Thread worker = new Thread(() -> {
      try {
        while (session.isOpen()) {
          String message = queue.take();
          synchronized (session) {
            session.sendMessage(new TextMessage(message));
          }
        }
      } catch (Exception e) {
        log.warn("WebSocket session {} worker interrupted: {}", session.getId(), e.getMessage());
      } finally {
        sessionQueues.remove(session);
        sessionWorkers.remove(session);
      }
    });

    worker.setDaemon(true);
    worker.start();
    sessionWorkers.put(session, worker);
  }

  public void unregisterSession(WebSocketSession session) {
    Thread thread = sessionWorkers.remove(session);
    if (thread != null) thread.interrupt();
    log.info("Unregistered session {}", session.getId());
  }

  public void sendMessage(WebSocketSession session, String message) {
    BlockingQueue<String> queue = sessionQueues.get(session);
    if (queue != null) {
      boolean isAdded = queue.add(message);
      if (!isAdded)
        throw new RuntimeException(String.format("Message %s is not being send to session %s.",
            message, session));
    } else {
      log.warn("Attempted to send message to unregistered session: {}", session.getId());
    }
  }
}
