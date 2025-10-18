package com.example.web.service;

import com.example.briscula.user.player.RealPlayer;
import com.example.web.dto.match.MatchDto;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.GameRoom;
import com.example.web.model.enums.GameEndStatus;
import com.example.web.model.enums.GameEndStatus.Status;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static com.example.web.utils.Constants.PLAYER_ID;
import static com.example.web.utils.Constants.ROOM_ID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameEndService {

    // TODO: When player win tournament match then while waiting for the next one make sure
    // some animation is shown on the screen

    private final GameRoomService gameRoomService;
    private final UserService userService;

    private final TournamentService tournamentService;
    private final MatchService matchService;
    private final WebSocketMessageDispatcher messageDispatcher = WebSocketMessageDispatcher.getInstance();

    public void update(GameEndStatus gameEndStatus, String matchId) {
        log.info("Game ended with status {} for match with id {}, updating statistics.", gameEndStatus.status(), matchId);

        gameEndStatus.playerResults().forEach((key, value) -> key.resetValues());

        if (gameEndStatus.status().equals(Status.NO_WINNER)) {
            handleNoWinnerCase(gameEndStatus, matchId);

            gameEndStatus.playerResults().keySet().forEach(player ->
                    messageDispatcher.leftGameOrTournament(player.getWebSocketSession()));

            return;
        }

        List<List<ConnectedPlayer>> resultLists = handleWinnerCase(gameEndStatus);
        List<ConnectedPlayer> winners = resultLists.get(0);
        List<ConnectedPlayer> losers = resultLists.get(1);

        boolean isTournamentMatch = matchId != null;

        // matchId is null if this is not tournament match
        if (!isTournamentMatch) {
            Stream.concat(winners.stream(), losers.stream())
                    .forEach(player ->
                            messageDispatcher.leftGameOrTournament(player.getWebSocketSession()));
            return;
        }

        MatchDto match = matchService.getMatch(matchId);

        log.info("Continuing to next round with {} winner.", winners.get(0));
        tournamentService.collectWinnersForNextPhase(match.tournamentId(), match.id(), winners.get(0), losers.get(0));
    }

    private void handleNoWinnerCase(GameEndStatus gameEndStatus, String matchId) {
        gameEndStatus.playerResults()
                .keySet()
                .forEach(user -> userService.updateUserRecord(null, false, false));

        if (matchId != null) {
            MatchDto match = matchService.getMatch(matchId);
            tournamentService.startRoundForMatch(matchId, match.tournamentId());
        }
    }

    private List<List<ConnectedPlayer>> handleWinnerCase(GameEndStatus gameEndStatus) {
        List<ConnectedPlayer> winners = new ArrayList<>();
        List<ConnectedPlayer> losers = new ArrayList<>();

        for (Map.Entry<ConnectedPlayer, Boolean> entry : gameEndStatus.playerResults().entrySet()) {
            ConnectedPlayer connectedPlayer = entry.getKey();
            if (connectedPlayer.getUserId() != null) {
                userService.updateUserRecord(connectedPlayer.getUserId(), true, entry.getValue());
            }

            boolean hasPlayerWonMatch = entry.getValue();
            if (hasPlayerWonMatch) {
                winners.add(connectedPlayer);
            } else
                losers.add(connectedPlayer);

        }

        return List.of(winners, losers);
    }

    public void handleDisconnectionFromGame(WebSocketMessage<?> message, WebSocketSession session)
            throws JsonProcessingException {
        String roomId = WebSocketMessageReader.getValueFromJsonMessage(message, ROOM_ID);
        int playerId = Integer.parseInt(WebSocketMessageReader.getValueFromJsonMessage(message, PLAYER_ID));

        log.info("Player id={}, left room id={}.", playerId, roomId);

        GameRoom gameRoom = gameRoomService.getRoom(roomId);
        ConnectedPlayer connectedPlayer = gameRoom.getGame().getPlayer(playerId);
        if (connectedPlayer.getPlayer() instanceof RealPlayer realPlayer) {
            CompletableFuture<Integer> completableFuture = realPlayer.getSelectedCardFuture();
            if (completableFuture != null && !completableFuture.isDone()) {
                completableFuture.complete(0);
            }
        }

        gameRoom.notifyPlayerLeft(playerId);

        messageDispatcher.leftGameOrTournament(session);

        // TODO: Temporarily set that closing the tab should mean that user is logout.
        // I think when logged in that should be on the level of browser and if game is exited, when user joins again he should be able
        // to continue playing.
        userService.logoutWithEmail(userService.getUserById(connectedPlayer.getUserId()).email());
    }
}
