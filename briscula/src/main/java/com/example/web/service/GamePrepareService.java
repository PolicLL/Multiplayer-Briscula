package com.example.web.service;

import com.example.briscula.user.player.Bot;
import com.example.briscula.user.player.RealPlayer;
import com.example.briscula.utilities.constants.GameOptionNumberOfPlayers;
import com.example.web.exception.UserIsAlreadyInTournamentOrGame;
import com.example.web.exception.UserWithUsernameAlreadyExistsException;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.GameRoom;
import com.example.web.model.Match;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

import static com.example.web.model.enums.ServerToClientMessageType.GAME_STARTED;
import static com.example.web.utils.WebSocketMessageSender.sendMessage;

@Slf4j
@Component
public class GamePrepareService {

    private final GameRoomService gameRoomService;
    private final GameStartService gameStartService;
    private final UserService userService;
    private final WebSocketMessageDispatcher messageDispatcher = WebSocketMessageDispatcher.getInstance();

    public GamePrepareService(@Lazy GameStartService gameStartService, GameRoomService gameRoomService,
                              UserService userService) {
        this.gameStartService = gameStartService;
        this.gameRoomService = gameRoomService;
        this.userService = userService;
    }

    private final Map<Integer, Set<ConnectedPlayer>> mapPreparingPlayers = new HashMap<>();

    {
        mapPreparingPlayers.put(2, new LinkedHashSet<>());
        mapPreparingPlayers.put(3, new LinkedHashSet<>());
        mapPreparingPlayers.put(4, new LinkedHashSet<>());
    }

    // TODO: Make sure that user can leave the tournament/game before it starts.
    // TODO: While waiting for another player to join room/tournament, make some animation on front
    public void handle(@NonNull WebSocketSession session, @NonNull WebSocketMessage<?> message)
            throws JsonProcessingException {

        String playerName = WebSocketMessageReader.getValueFromJsonMessage(message, "playerName");
        boolean shouldPointsShow = Boolean.parseBoolean(
                WebSocketMessageReader.getValueFromJsonMessage(message, "shouldShowPoints"));
        int numberOfPlayersOption = Integer.parseInt(
                WebSocketMessageReader.getValueFromJsonMessage(message, "numberOfPlayers"));

        String userId = null;

        boolean isRegisteredUser = WebSocketMessageReader.contains(message, "userId");

        if (isRegisteredUser) {
            userId = WebSocketMessageReader.getValueFromJsonMessage(message, "userId");
        } else {
            // TODO Check for the name used by anonymous, make sure it is not used by registered player
            if (userService.existsByUsername(playerName))
                throw new UserWithUsernameAlreadyExistsException(playerName);
        }


        if (!mapPreparingPlayers.containsKey(numberOfPlayersOption)) {
            log.warn("Invalid numberOfPlayers value: {}", numberOfPlayersOption);
            return;
        }

        synchronized (mapPreparingPlayers) {
            ConnectedPlayer connectedPlayer = new ConnectedPlayer(new RealPlayer(playerName, session), shouldPointsShow);

            if (userId != null)
                connectedPlayer.setUserId(userId);

            Set<ConnectedPlayer> waitingPlayers = mapPreparingPlayers.get(numberOfPlayersOption);

            if (messageDispatcher.isSessionInGameOrTournament(session)) {
                throw new UserIsAlreadyInTournamentOrGame(session);
            }

            messageDispatcher.joinGameOrTournament(session);

            if (!waitingPlayers.add(connectedPlayer)) {
                throw new RuntimeException("User already entered this game.");
            }

            if (waitingPlayers.size() == numberOfPlayersOption) {
                ConnectedPlayer firstPlayer = waitingPlayers.iterator().next();

                GameRoom gameRoom = gameRoomService.createRoom(waitingPlayers,
                        GameOptionNumberOfPlayers.fromInt(numberOfPlayersOption), firstPlayer.isDoesWantPointsToShow());

                notifyPlayersGameIsStarting(waitingPlayers, gameRoom);

                waitingPlayers.clear();
            }
        }
    }

    public void startProcessForGameStartForMatch(Match match, Set<ConnectedPlayer> connectedPlayers) {
        GameRoom gameRoom = gameRoomService.createRoom(connectedPlayers, GameOptionNumberOfPlayers.TWO_PLAYERS, true);

        log.info("Created game room with id {}.", gameRoom.getRoomId());

        gameRoom.setMatchId(match.getId());

        notifyPlayersGameIsStarting(connectedPlayers, gameRoom);
    }

    private void notifyPlayersGameIsStarting(Set<ConnectedPlayer> connectedPlayers, GameRoom gameRoom) {
        connectedPlayers.forEach(tempUser -> {
            if (tempUser.getPlayer() instanceof RealPlayer) {
                log.info("Sending message that game room started with id {}.", gameRoom.getRoomId());
                sendMessage(tempUser.getWebSocketSession(), GAME_STARTED, gameRoom.getRoomId(), tempUser.getId());
            } else {
                tempUser.setInitialCardsReceived(true);
            }
        });

        if (areAllPlayersBots(connectedPlayers)) {
            gameStartService.startGame(gameRoom.getRoomId());
        }
    }

    public static boolean areAllPlayersBots(Set<ConnectedPlayer> waitingPlayers) {
        for (ConnectedPlayer tempConnectedPlayer : waitingPlayers) {
            if (!(tempConnectedPlayer.getPlayer() instanceof Bot)) {
                return false;
            }
        }
        return true;
    }

    public void handleLeavingRoom(WebSocketSession session, WebSocketMessage<?> message)
            throws JsonProcessingException {
        String playerName = WebSocketMessageReader.getValueFromJsonMessage(message, "playerName");
        Integer numberOfPlayers = Integer.valueOf(WebSocketMessageReader.getValueFromJsonMessage(message, "numberOfPlayers"));

        synchronized (mapPreparingPlayers) {
            Set<ConnectedPlayer> players = mapPreparingPlayers.get(numberOfPlayers);
            Optional<ConnectedPlayer> player = players.stream().filter(
                    tempPlayer -> tempPlayer.getPlayer().getNickname().equals(playerName) &&
                            tempPlayer.getWebSocketSession().equals(session)).findFirst();
            player.ifPresent(players::remove);

            // check will it work
            messageDispatcher.leftGameOrTournament(session);

            log.info("User with player name {}, left room for {} players.", playerName, numberOfPlayers);
        }
    }
}
