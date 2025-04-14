import React, { useState } from "react";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  useNavigate,
} from "react-router-dom";
import axios from "axios";

function PrepareGame() {
  const [status, setStatus] = useState("Click 'Join Game' to connect.");
  const [waiting, setWaiting] = useState();
  const [receivedMessage, setReceivedMessage] = useState("");
  const [name, setName] = useState("");
  const [isStartEnabled, setIsStartEnabled] = useState(false);
  const navigate = useNavigate();
  let socket;

  // Function to handle WebSocket connection
  const connectWebSocketWhenReady = async () => {
    try {
      const joinResponse = await axios.post(
        "http://localhost:8080/api/game/join",
        {
          playerName: name,
        }
      );

      const playerId = joinResponse.data.playerId;

      setStatus("Waiting for other players...");
      setWaiting(true); // show spinner or animation

      const pollInterval = setInterval(async () => {
        const statusResponse = await axios.get(
          `http://localhost:8080/api/game/status/${playerId}`
        );
        const { status, roomId } = statusResponse.data;

        if (status === "READY") {
          clearInterval(pollInterval);
          setStatus("Game ready! Connecting...");
          connectWebSocket(roomId);
        }
      }, 2000); // poll every 2 seconds
    } catch (error) {
      console.error("Join error:", error);
      setStatus("Error joining game.");
    }
  };

  const connectWebSocket = (roomId) => {
    const socket = new WebSocket(`ws://localhost:8080/game/${roomId}`);

    socket.onopen = () => {
      setStatus("Connected to the game room!");
      setWaiting(false); // hide waiting animation
    };

    socket.onmessage = (event) => {
      const message = event.data;

      if (message.includes("GAME_STARTED")) {
        const roomIdFromMessage = message.split(" ")[1];
        navigate(`/game/${roomIdFromMessage}`);
      } else {
        setReceivedMessage(message);
      }
    };

    socket.onerror = () => setStatus("WebSocket error occurred.");
    socket.onclose = () => setStatus("Disconnected.");
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

            <button onClick={connectWebSocketWhenReady}>Join Game</button>

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
