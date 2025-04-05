package com.example.web.configuration;

import com.example.web.handler.GamePreparingWebSocketHandler;
import com.example.web.handler.GameStartWebSocketHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Slf4j
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

  @Override
  public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
    registry
        .addHandler(new GamePreparingWebSocketHandler(), "/game/prepare")
        .addHandler(new GameStartWebSocketHandler(), "/game/start")
        .setAllowedOrigins("*");

    log.info("WebSocket handler registered for path /game");
  }
}
