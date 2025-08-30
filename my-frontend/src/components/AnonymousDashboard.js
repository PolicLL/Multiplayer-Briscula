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
  const [numberOfPlayers, setNumberOfPlayers] = useState(-1);

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

    setNumberOfPlayers(numberOfPlayers);

    sendMessage({
      type: "JOIN_ROOM",
      playerName: name,
      numberOfPlayers: numberOfPlayers,
      shouldShowPoints: shouldShowPoints,
    });

    sessionStorage.setItem("hasEnteredAnonymously", true);

    setIsDisabled(true);
  };

  const leaveGame = () => {
    sendMessage({
      type: "LEAVE_ROOM",
      playerName: name,
      numberOfPlayers: numberOfPlayers,
    });
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
    <div className="anonymous-dashboard-container">
      <h1>Anonymous User</h1>
      <JoinGamePanel
        name={name}
        setName={setName}
        shouldShowPoints={shouldShowPoints}
        handleCheckboxChange={(e) => setShouldShowPoints(e.target.checked)}
        joinGame={joinGame}
        isDisabled={isDisabled}
        isStartEnabled={false}
        leaveGame={leaveGame}
      />
      <p style={{ fontWeight: "bold", fontSize: "18px" }}>{status}</p>
    </div>
  );
}

export default PrepareGame;
