package com.example.web.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketMessage;

public class WebSocketMessageReader {

  public static String getValueFromJsonMessage(@NonNull WebSocketMessage<?> message, String key)
      throws JsonProcessingException {
    String payload = (String) message.getPayload();
    JsonNode json = new ObjectMapper().readTree(payload);
    return json.get(key).asText();

  }

}
