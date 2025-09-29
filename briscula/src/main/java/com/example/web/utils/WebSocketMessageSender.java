package com.example.web.utils;

import com.example.web.dto.Message;
import com.example.web.model.enums.ServerToClientMessageType;
import com.example.web.service.WebSocketMessageDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Slf4j
@Service
public class WebSocketMessageSender {

    private static final WebSocketMessageDispatcher webSocketMessageDispatcher = WebSocketMessageDispatcher.getInstance();

    public static void sendMessage(WebSocketSession webSocketSession,
                                   ServerToClientMessageType messageType,
                                   String roomId,
                                   int playerId,
                                   String messageText) {
        if (isSessionOpen(webSocketSession)) {
            log.debug("Sending message {}.", messageText);
            Message message = new Message(messageType, roomId, playerId, messageText);
            webSocketMessageDispatcher.sendMessage(webSocketSession, JsonUtils.toJson(message));
        } else {
            log.warn("WebSocket session is closed. Cannot send message of type {} to player {} in room {}",
                    messageType, playerId, roomId);
        }
    }

    public static void sendMessage(WebSocketSession webSocketSession,
                                   ServerToClientMessageType messageType,
                                   String roomId,
                                   int playerId) {
        if (isSessionOpen(webSocketSession)) {
            Message message = new Message(messageType, roomId, playerId);
            webSocketMessageDispatcher.sendMessage(webSocketSession, JsonUtils.toJson(message));
        } else {
            log.warn("WebSocket session is closed. Cannot send message of type {} to player {} in room {}",
                    messageType, playerId, roomId);
        }
    }

    public static void sendMessage(WebSocketSession webSocketSession,
                                   ServerToClientMessageType messageType,
                                   String content) {
        if (isSessionOpen(webSocketSession)) {
            Message message = new Message(messageType, content);
            webSocketMessageDispatcher.sendMessage(webSocketSession, JsonUtils.toJson(message));
        }
    }

    public static void sendMessage(WebSocketSession webSocketSession,
                                   ServerToClientMessageType messageType) {
        if (isSessionOpen(webSocketSession)) {
            Message message = new Message(messageType);
            webSocketMessageDispatcher.sendMessage(webSocketSession, JsonUtils.toJson(message));
        }
    }

    private static boolean isSessionOpen(WebSocketSession session) {
        return session != null && session.isOpen();
    }
}
