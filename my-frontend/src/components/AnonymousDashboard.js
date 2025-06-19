import React, { useState, useRef, useEffect } from "react";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  useNavigate,
} from "react-router-dom";

function PrepareGame() {
  const [status, setStatus] = useState("Click 'Join Game' to connect.");
  const [receivedMessage, setReceivedMessage] = useState("");
  const [name, setName] = useState("");
  const [isStartEnabled, setIsStartEnabled] = useState(false);
  const [shouldShowPoints, setShouldShowPoints] = useState(false);
  const navigate = useNavigate();
  let socket;

  const [isDisabled, setIsDisabled] = useState(false);

  const socketRef = useRef(null);

  const parseWebSocketMessage = (message) => {
    try {
      const parsed = JSON.parse(message);
      return {
        type: parsed.type,
        roomId: parsed.roomId,
        playerId: parsed.playerId,
        content: parsed.content,
      };
    } catch (error) {
      console.error("Invalid JSON.", error);
      return null;
    }
  };

  const handleCheckboxChange = (event) => {
    setShouldShowPoints(event.target.checked);
  };

  useEffect(() => {
    const socket = new WebSocket("ws://localhost:8080/game/prepare");
    socketRef.current = socket;

    return () => {
      socket.close();
    };
  }, [navigate]);

  const joinGame = (numberOfPlayers) => {
    if (!name.trim()) {
      alert("You did not enter a name.");
      return;
    }

    const socket = socketRef.current;

    if (socket && socket.readyState === WebSocket.OPEN) {
      socket.send(
        JSON.stringify({
          type: "JOIN_ROOM",
          playerName: name,
          numberOfPlayers: numberOfPlayers,
          shouldShowPoints: shouldShowPoints,
        })
      );

      setIsDisabled(true);
    }

    socket.onmessage = (event) => {
      const message = event.data;
      const parsedMessage = parseWebSocketMessage(message);

      if (!parsedMessage) return;

      if (message.includes("GAME_STARTED")) {
        console.log("Game starting...");
        navigate(`/game/${parsedMessage.roomId}/${parsedMessage.playerId}`);
      } else {
        setReceivedMessage(message);
      }
    };

    socket.onerror = () => {
      setStatus("Failed to connect to the server.");
    };

    socket.onclose = () => {
      setStatus("Disconnected from server.");
    };
  };

  return (
    <Routes>
      <Route
        path="/"
        element={
          <div>
            <h1>WebSocket Connection Test</h1>

            <input
              type="text"
              placeholder="Enter the name: "
              value={name}
              onChange={(e) => setName(e.target.value)}
            />

            <button onClick={() => joinGame(2)}>Join Game (2v2)</button>
            <button onClick={() => joinGame(3)} disabled={isDisabled}>
              Join Game (3v3)
            </button>
            <button onClick={() => joinGame(4)} disabled={isDisabled}>
              Join Game (4v4)
            </button>

            <label>
              <input
                type="checkbox"
                checked={shouldShowPoints}
                onChange={handleCheckboxChange}
              />
              Show Points
            </label>

            <button disabled={!isStartEnabled}>Start Game</button>

            <p id="status" style={{ fontWeight: "bold", fontSize: "18px" }}>
              {status}
            </p>
            <h3>Received message: {receivedMessage}</h3>
          </div>
        }
      />
    </Routes>
  );
}

export default PrepareGame;
