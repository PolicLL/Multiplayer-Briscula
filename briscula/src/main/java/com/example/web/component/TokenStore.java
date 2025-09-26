package com.example.web.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class TokenStore {

    private final Map<String, String> activeTokens = new ConcurrentHashMap<>();

    public void storeToken(String email, String token) {
        activeTokens.put(email, token);
    }

    public boolean isTokenActive(String email) {
        return activeTokens.containsKey(email);
    }

    public void removeToken(String email) {
        activeTokens.remove(email);
    }
}