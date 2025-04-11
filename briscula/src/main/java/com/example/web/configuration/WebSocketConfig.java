package com.example.web.configuration;

import com.example.web.handler.GamePreparingWebSocketHandler;
import com.example.web.handler.GameStartWebSocketHandler;
import com.example.web.handler.PrepareGameService;
import com.example.web.service.GameRoomService;
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
  private PrepareGameService prepareGameService;

  @Autowired
  private GameRoomService gameRoomService;

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(new GamePreparingWebSocketHandler(prepareGameService), "/game/prepare")
        .addHandler(new GameStartWebSocketHandler(gameRoomService), "/game/**")
        .setAllowedOrigins("*");

    log.info("WebSocket handler registered for path /game");
  }
}
