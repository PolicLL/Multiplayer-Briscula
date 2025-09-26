package com.example.web.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static utils.EntityUtils.createTournamentCreateDto;
import static utils.EntityUtils.generateValidUserDtoWithoutPhoto;

import com.example.web.dto.tournament.JoinTournamentResponse;
import com.example.web.dto.user.UserDto;
import com.example.web.handler.endpoints.tournament.TestJoinRoomThenJoinTournamentEndpoint;
import com.example.web.handler.endpoints.tournament.TestJoinTournamentEndpoint;
import com.example.web.handler.endpoints.tournament.TestJoinTournamentThenJoinRoomEndpoint;
import com.example.web.service.TournamentService;
import com.example.web.service.UserService;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.WebSocketContainer;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;

class TournamentWebSocketHandlerIntegrationTest extends AbstractIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private UserService userService;

    private String tournamentId;

    private List<String> userIds = new ArrayList<>();

    private URI WS_URI;

    @BeforeEach
    void setUp() throws Exception {
        WS_URI = new URI("ws://localhost:" + port + "/game");

        tournamentId = tournamentService.create(createTournamentCreateDto())
                .id();

        for (int i = 0; i < 5; ++i) {
            UserDto created = userService.createUser(generateValidUserDtoWithoutPhoto());
            userIds.add(created.id());
            System.out.println("CREATED : " + created);
        }

    }

    @Test
    void testRawWebSocketJoinTournament() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        List<CompletableFuture<JoinTournamentResponse>> futures = new ArrayList<>();
        List<TestJoinTournamentEndpoint> endpoints = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            CompletableFuture<JoinTournamentResponse> future = new CompletableFuture<>();
            futures.add(future);
            TestJoinTournamentEndpoint endpoint = new TestJoinTournamentEndpoint(future, tournamentId, userIds.get(i));
            endpoints.add(endpoint);
            container.connectToServer(endpoint, WS_URI);
        }

        List<JoinTournamentResponse> responses = new ArrayList<>();
        for (CompletableFuture<JoinTournamentResponse> future : futures) {
            responses.add(future.get(60, TimeUnit.SECONDS));
        }

        for (JoinTournamentResponse response : responses) {
            assertThat(response.currentNumberOfPlayers()).isGreaterThanOrEqualTo(4);
        }

        System.out.println("✅ Test completed successfully");
    }

    @Test
    void testRawWebSocketJoinTournamentThenJoinGame() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        CompletableFuture<String> future = new CompletableFuture<>();
        TestJoinTournamentThenJoinRoomEndpoint endpoint = new TestJoinTournamentThenJoinRoomEndpoint(
                future, tournamentId, userIds.get(userIds.size() - 1)
        );

        container.connectToServer(endpoint, WS_URI);

        String response = future.get(60, TimeUnit.SECONDS);

        assertThat(response).contains("USER_ALREADY_IN_GAME_OR_TOURNAMENT");

        System.out.println("✅ Test completed successfully");
    }

    @Test
    void testRawWebSocketJoinGameThenJoinTournament() throws Exception {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        CompletableFuture<String> future = new CompletableFuture<>();
        TestJoinRoomThenJoinTournamentEndpoint endpoint = new TestJoinRoomThenJoinTournamentEndpoint(
                future, tournamentId, userIds.get(userIds.size() - 1)
        );

        container.connectToServer(endpoint, WS_URI);

        String response = future.get(60, TimeUnit.SECONDS);

        assertThat(response).contains("USER_ALREADY_IN_GAME_OR_TOURNAMENT");

        System.out.println("✅ Test completed successfully");
    }

}
