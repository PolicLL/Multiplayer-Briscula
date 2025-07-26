package com.example.web.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class TokenStore {

  private final Map<String, String> activeTokens = new ConcurrentHashMap<>();

  public void storeToken(String username, String token) {
    activeTokens.put(username, token);
  }

  public boolean isTokenActive(String username) {
    return activeTokens.containsKey(username);
  }

  public String getToken(String username) {
    return activeTokens.get(username);
  }

  public void removeToken(String username) {
    activeTokens.remove(username);
  }
}