package com.example.web.utils;

import com.example.web.model.enums.ClientToServerMessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketMessage;

public class WebSocketMessageReader {

  private static final String MESSAGE_TYPE = "type";
  private static final String MESSAGE_NAME = "playerName";

  public static String getValueFromJsonMessage(@NonNull WebSocketMessage<?> message, String key)
      throws JsonProcessingException {
    String payload = (String) message.getPayload();
    JsonNode json = new ObjectMapper().readTree(payload);
    return json.get(key).asText();

  }

  public static boolean contains(@NonNull WebSocketMessage<?> message, String key)
      throws JsonProcessingException {
    String payload = (String) message.getPayload();
    JsonNode json = new ObjectMapper().readTree(payload);
    return json.has(key);
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

  public static String getName(@NonNull WebSocketMessage<?> message)
      throws JsonProcessingException {
    String payload = (String) message.getPayload();
    JsonNode json = new ObjectMapper().readTree(payload);

    String name = "";

    if (json.has(MESSAGE_NAME)) {
      name = json.get(MESSAGE_NAME).asText();
    }

    try {
      return name;
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new IllegalArgumentException("Unknown message type: " + name);
    }
  }

}
