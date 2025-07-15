package com.example.web.service;

import static com.example.web.utils.Constants.PLAYER_ID;
import static com.example.web.utils.Constants.ROOM_ID;

import com.example.web.dto.match.MatchDto;
import com.example.web.model.ConnectedPlayer;
import com.example.web.model.enums.GameEndStatus;
import com.example.web.model.enums.GameEndStatus.Status;
import com.example.web.utils.WebSocketMessageReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameEndService {

  private final GameRoomService gameRoomService;
  private final UserService userService;

  private final TournamentService tournamentService;
  private final MatchService matchService;

  // TODO -> Has to be tested
  public void update(GameEndStatus gameEndStatus, String matchId) {
    log.info("Game ended with status {}, updating statistics.", gameEndStatus.status());
    if (gameEndStatus.status().equals(Status.NO_WINNER)) {
      gameEndStatus.playerResults()
          .keySet()
          .forEach(user -> userService.updateUserRecord(null, false, false));
      return;
    }

    List<ConnectedPlayer> winners = new ArrayList<>();

    for (Map.Entry<ConnectedPlayer, Boolean> entry : gameEndStatus.playerResults().entrySet()) {
      ConnectedPlayer connectedPlayer = entry.getKey();
      if (connectedPlayer.getUserId() != null) {
        userService.updateUserRecord(connectedPlayer.getUserId(), true, entry.getValue());

        boolean hasPlayerWonTournamentMatch = entry.getValue();
        if (hasPlayerWonTournamentMatch) {
          winners.add(connectedPlayer);
        }
      }
    }

    MatchDto match = matchService.getMatch(matchId);

    if (winners.size() > 1) {
      log.info("Continuing to next round with {} winners.", winners.size());
      tournamentService.startNextRound(winners , match.tournamentId());
    } else {
      log.info("Tournament ended. Final winner: {}", winners.get(0));
      tournamentService.finishTournament(winners.get(0));
    }
  }

  public void handleDisconnectionFromGame(WebSocketMessage<?> message)
      throws JsonProcessingException {
    String roomId = WebSocketMessageReader.getValueFromJsonMessage(message, ROOM_ID);
    int playerId = Integer.parseInt(WebSocketMessageReader.getValueFromJsonMessage(message, PLAYER_ID));

    log.info("Player id={}, left room id={}.", playerId, roomId);

    gameRoomService.getRoom(roomId).notifyPlayerLeft(playerId);
  }
}
