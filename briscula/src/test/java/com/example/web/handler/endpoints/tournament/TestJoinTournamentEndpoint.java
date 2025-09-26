package com.example.web.handler.endpoints.tournament;

import static com.example.web.utils.Constants.OBJECT_MAPPER;
import static com.example.web.utils.JsonUtils.fromJson;

import com.example.web.dto.Message;
import com.example.web.dto.tournament.JoinTournamentResponse;
import com.example.web.model.enums.ServerToClientMessageType;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.websocket.ClientEndpoint;
import jakarta.websocket.CloseReason;
import jakarta.websocket.OnClose;
import jakarta.websocket.OnError;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import lombok.RequiredArgsConstructor;

@ClientEndpoint
@RequiredArgsConstructor
public class TestJoinTournamentEndpoint {

    private final CompletableFuture<JoinTournamentResponse> completableFuture;

    private final String tournamentId;
    private final String userId;


    @OnOpen
    public void onOpen(Session session) {
        System.out.println("✅ WebSocket connection established");
        try {
            session.getAsyncRemote().sendText(OBJECT_MAPPER.writeValueAsString(Map.of(
                    "type", "LOGGED_IN"
            )));
            session.getAsyncRemote().sendText(OBJECT_MAPPER.writeValueAsString(Map.of(
                    "type", "JOIN_TOURNAMENT",
                    "tournamentId", tournamentId,
                    "playerId", userId
            )));
        } catch (Exception e) {
            e.printStackTrace();
            completableFuture.completeExceptionally(e);
        }
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("📥 Received message: " + message);
        ;

        try {
            // Step 1: parse the outer wrapper message
            Message parsedMessage = OBJECT_MAPPER.readValue(message, Message.class);

            // Step 2: check type and parse content accordingly
            if (parsedMessage.type() == ServerToClientMessageType.TOURNAMENT_UPDATE) {
                JoinTournamentResponse joinTournamentResponse = OBJECT_MAPPER.readValue(
                        parsedMessage.content(), JoinTournamentResponse.class
                );

                if (joinTournamentResponse.currentNumberOfPlayers() >= 4) {
                    System.out.println("CONTAINS");
                    completableFuture.complete(joinTournamentResponse);
                }
            }

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse WebSocket message", e);
        }
    }


    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("❌ WebSocket error: " + throwable.getMessage());
        completableFuture.completeExceptionally(throwable);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("❌ WebSocket connection closed: " + closeReason.getReasonPhrase());
    }
}