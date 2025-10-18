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
  const [serverError, setServerError] = useState("");

  const { sendMessage, setOnMessage } = useWebSocketContext();

  const handleMessage = useCallback(
    (parsedMessage) => {
      if (parsedMessage.type === "USER_WITH_USERNAME_ALREADY_IN_GAME") {
        setStatus(parsedMessage.content);
        setServerError(parsedMessage.content); 
      }
    },
    [navigate, setOnMessage] // ✅ Do NOT include `tournaments` here!
  );

  // TODO set default anonymous user, if it s not choosen ,choose some random, give abilioty to chose custome

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
      <JoinGamePanel
        name={name}
        setName={setName}
        shouldShowPoints={shouldShowPoints}
        handleCheckboxChange={(e) => setShouldShowPoints(e.target.checked)}
        joinGame={joinGame}
        isDisabled={isDisabled}
        isStartEnabled={false}
        leaveGame={leaveGame}
        isAnonymous={true}
        status={status}
        serverError={serverError}
      />
    </div>
  );
}

export default PrepareGame;
