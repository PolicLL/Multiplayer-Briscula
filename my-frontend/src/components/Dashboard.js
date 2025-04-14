import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function Dashboard() {
  const navigate = useNavigate();
  const [message, setMessage] = useState("");
  const [waiting, setWaiting] = useState();
  const [userInfo, setUserInfo] = useState({});
  const [status, setStatus] = useState("Click 'Join Game' to connect.");
  const [username] = useState(() => localStorage.getItem("username"));
  const [isStartEnabled, setIsStartEnabled] = useState(false);
  const [receivedMessage, setReceivedMessage] = useState("");

  let socket;

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const token = localStorage.getItem("jwtToken");

        if (!token) {
          setMessage("Please log in first.");
          return;
        }

        const userResponse = await axios.get(
          "http://localhost:8080/api/users/by",
          {
            params: { username }, // Only sending username
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        setUserInfo(userResponse.data);
      } catch (error) {
        setMessage("Error fetching user information.");
        console.error(error);
      }
    };

    fetchUserInfo();
  }, []);

  const connectWebSocketWhenReady = async () => {
    try {
      const joinResponse = await axios.post(
        "http://localhost:8080/api/game/join",
        {
          playerName: username,
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

  return (
    <div>
      <h2>Hello !</h2>
      {message && <p>{message}</p>}

      {waiting && <img src="/spinner.gif" alt="Waiting..." />}

      {userInfo && (
        <div>
          <h3>Welcome, {userInfo.username}!</h3>
          <p>Age: {userInfo.age}</p>
          <p>Country: {userInfo.country}</p>
          <p>Email: {userInfo.email}</p>
        </div>
      )}

      <button onClick={connectWebSocketWhenReady}>Join Game</button>

      <button disabled={!isStartEnabled}>Start Game</button>
    </div>
  );
}

export default Dashboard;
