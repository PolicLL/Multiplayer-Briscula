package com.example.web.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  public static String toJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      log.error("Error converting object to JSON: {}", e.getMessage());
      throw new RuntimeException("Failed to convert object to JSON", e);
    }
  }

  public static <T> T fromJson(String json, Class<T> clazz) throws JsonProcessingException {
    return objectMapper.readValue(json, clazz);
  }

  public static <T> T fromJsonUsingJavaTimeModule(String json, Class<T> clazz) throws JsonProcessingException {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper.readValue(json, clazz);
  }
}
