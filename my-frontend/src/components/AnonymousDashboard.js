import React, { useState } from "react";
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
  const navigate = useNavigate();
  let socket;

  // Function to handle WebSocket connection
  const joinGame = (numberOfPlayers) => {
    if (!name.trim()) {
      alert("You did not enter a name.");
      return;
    }

    socket = new WebSocket("ws://localhost:8080/game/prepare"); // Change port if needed

    socket.onopen = () => {
      setStatus("Connected to the server successfully!");
      socket.send(
        JSON.stringify({
          type: "JOIN_ROOM",
          playerName: name,
          numberOfPlayers: numberOfPlayers,
        })
      );
    };

    socket.onmessage = (event) => {
      const message = event.data;

      if (message.includes("GAME_STARTED")) {
        console.log("Game starting...");
        const [_, roomId, playerId] = message.split(" ");
        navigate(`/game/${roomId}/${playerId}`);
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

  const startGame = () => {
    navigate("/start-game");
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
            <button onClick={() => joinGame(3)}>Join Game (3v3)</button>
            <button onClick={() => joinGame(4)}>Join Game (4v4)</button>

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
