package com.example.web.configuration;

import com.example.web.handler.TournamentWebSocketHandler;
import com.example.web.handler.WebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Slf4j
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  @Autowired
  private WebSocketHandler webSocketHandler;

  @Autowired
  private TournamentWebSocketHandler tournamentWebSocketHandler;

  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(webSocketHandler, "/game/prepare")
        .addHandler(webSocketHandler, "/game/**")
        .addHandler(tournamentWebSocketHandler, "/tournament/**")
        .setAllowedOrigins("*");

    log.info("WebSocket handler registered for path /game");
  }
}
