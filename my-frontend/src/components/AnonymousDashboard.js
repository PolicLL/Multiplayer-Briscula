import React, { useState, useCallback, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import JoinGamePanel from "./common/JoinGamePanel";
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
    (parsedMessage) => {},
    [navigate, setOnMessage] // ✅ Do NOT include `tournaments` here!
  );

  const joinGame = (numberOfPlayers) => {
    if (!name.trim()) return alert("Enter a name.");

    setOnMessage(handleMessage);
    sessionStorage.setItem("isRegistered", false);
    sessionStorage.setItem("username", name);

    console.log("Sending message to join room.");

    sendMessage({
      type: "JOIN_ROOM",
      playerName: name,
      numberOfPlayers: numberOfPlayers,
      shouldShowPoints: shouldShowPoints,
    });

    setIsDisabled(true);
  };

  useEffect(() => {
    if (sessionStorage.getItem("isRegistered") === "true") {
      return navigate("/dashboard");
    }

    const storedName = sessionStorage.getItem("username");
    if (storedName) {
      setName(storedName);
    }

    return () => {};
  }, []);

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
