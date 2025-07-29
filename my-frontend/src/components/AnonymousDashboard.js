import React, { useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import JoinGamePanel from "./common/JoinGamePanel";
import { useGameWebSocket } from "../hooks/useGameWebSocket";
import { useWebSocketContext } from "../context/WebSocketContext";

function PrepareGame() {
  const navigate = useNavigate();
  const [name, setName] = useState("");
  const [shouldShowPoints, setShouldShowPoints] = useState(false);
  const [isDisabled, setIsDisabled] = useState(false);
  const [receivedMessage, setReceivedMessage] = useState("");
  const [status, setStatus] = useState("");

  const { sendMessage, setOnMessage } = useWebSocketContext();

  const handleMessage = useCallback(
    (parsedMessage) => {
      if (parsedMessage.type === "GAME_STARTED") {
        console.log("Game started message received.");
        setOnMessage(null);
        navigate(`/game/${parsedMessage.roomId}/${parsedMessage.playerId}`);
      }
    },
    [navigate, setOnMessage] // ✅ Do NOT include `tournaments` here!
  );

  const joinGame = (numberOfPlayers) => {
    if (!name.trim()) return alert("Enter a name.");

    setOnMessage(handleMessage);

    sendMessage({
      type: "JOIN_ROOM",
      playerName: name,
      numberOfPlayers: numberOfPlayers,
      shouldShowPoints: shouldShowPoints,
    });

    setIsDisabled(true);
  };

  return (
    <div>
      <h1>Anonymous Game Lobby</h1>
      <JoinGamePanel
        name={name}
        setName={setName}
        shouldShowPoints={shouldShowPoints}
        handleCheckboxChange={(e) => setShouldShowPoints(e.target.checked)}
        joinGame={joinGame}
        isDisabled={isDisabled}
        isStartEnabled={false}
      />
      <p style={{ fontWeight: "bold", fontSize: "18px" }}>{status}</p>
      <h3>Received message: {receivedMessage}</h3>
    </div>
  );
}

export default PrepareGame;
