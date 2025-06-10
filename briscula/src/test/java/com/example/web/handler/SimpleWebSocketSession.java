package com.example.web.handler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketExtension;
import org.springframework.web.socket.WebSocketSession;

public class SimpleWebSocketSession implements WebSocketSession {

  private final String id;
  private final Map<String, Object> attributes;

  public SimpleWebSocketSession(String id) {
    this.id = id;
    this.attributes = new HashMap<>();
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public URI getUri() {
    return URI.create("ws://localhost:8080");
  }

  @Override
  public HttpHeaders getHandshakeHeaders() {
    return null;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public Principal getPrincipal() {
    return null;
  }

  @Override
  public InetSocketAddress getLocalAddress() {
    return null;
  }

  @Override
  public InetSocketAddress getRemoteAddress() {
    return null;
  }

  @Override
  public String getAcceptedProtocol() {
    return null;
  }

  @Override
  public void setTextMessageSizeLimit(int messageSizeLimit) {

  }

  @Override
  public int getTextMessageSizeLimit() {
    return 0;
  }

  @Override
  public void setBinaryMessageSizeLimit(int messageSizeLimit) {

  }

  @Override
  public int getBinaryMessageSizeLimit() {
    return 0;
  }

  @Override
  public List<WebSocketExtension> getExtensions() {
    return null;
  }

  @Override
  public void sendMessage(org.springframework.web.socket.WebSocketMessage<?> message) throws IOException {
    System.out.println("Message sent: " + message.getPayload());
  }

  @Override
  public boolean isOpen() {
    return true;
  }

  @Override
  public void close() throws IOException {
    System.out.println("Session closed.");
  }

  @Override
  public void close(CloseStatus status) throws IOException {
    System.out.println("Session closed with status: " + status);
  }
}