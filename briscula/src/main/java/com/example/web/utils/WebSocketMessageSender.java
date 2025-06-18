package com.example.web.utils;

import com.example.web.dto.Message;
import com.example.web.model.enums.ServerToClientMessageType;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
public class WebSocketMessageSender {

  public static void sendMessage(WebSocketSession webSocketSession,
      ServerToClientMessageType messageType,
      String roomId,
      int playerId,
      String messageText) {
    try {
      Message message = new Message(messageType, roomId, playerId, messageText);
      webSocketSession.sendMessage(new TextMessage(JsonUtils.toJson(message)));
    } catch (IOException e) {
      log.error("Error sending message: " + e.getMessage());
      throw new RuntimeException(e);
    }
  }
}
