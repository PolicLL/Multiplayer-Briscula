package com.example.web.utils;

import com.example.web.model.enums.ClientToServerMessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketMessage;

public class WebSocketMessageReader {

  private static final String MESSAGE_TYPE = "type";

  public static String getValueFromJsonMessage(@NonNull WebSocketMessage<?> message, String key)
      throws JsonProcessingException {
    String payload = (String) message.getPayload();
    JsonNode json = new ObjectMapper().readTree(payload);
    return json.get(key).asText();

  }

  public static ClientToServerMessageType getMessageType(@NonNull WebSocketMessage<?> message)
      throws JsonProcessingException {
    String payload = (String) message.getPayload();
    JsonNode json = new ObjectMapper().readTree(payload);
    String messageType = json.get(MESSAGE_TYPE).asText();

    try {
      return ClientToServerMessageType.valueOf(messageType);
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new IllegalArgumentException("Unknown message type: " + messageType);
    }
  }

}
