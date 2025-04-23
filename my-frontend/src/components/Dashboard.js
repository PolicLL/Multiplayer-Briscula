import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function Dashboard() {
  const navigate = useNavigate();
  const [message, setMessage] = useState("");
  const [userInfo, setUserInfo] = useState({});
  const [status, setStatus] = useState("Click 'Join Game' to connect.");
  const [username] = useState(() => localStorage.getItem("username"));
  const [isStartEnabled, setIsStartEnabled] = useState(false);
  const [receivedMessage, setReceivedMessage] = useState("");

  const socketRef = useRef(null);

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
            params: { username },
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

    // 🔌 Create WebSocket connection once on mount
    const socket = new WebSocket("ws://localhost:8080/game/prepare");
    socketRef.current = socket;

    socket.onopen = () => {
      setStatus("Connected to the server successfully!");
    };

    socket.onmessage = (event) => {
      const message = event.data;

      if (message.includes("GAME_STARTED")) {
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

    // 🔌 Cleanup on unmount
    return () => {
      socket.close();
    };
  }, [navigate, username]);

  const joinGame = () => {
    const socket = socketRef.current;
    if (socket && socket.readyState === WebSocket.OPEN) {
      socket.send(
        JSON.stringify({
          type: "JOIN_ROOM",
          playerName: username,
        })
      );
    } else {
      setStatus("Socket not connected.");
    }
  };

  return (
    <div>
      <h2>Hello!</h2>
      {message && <p>{message}</p>}

      {userInfo && (
        <div>
          <h3>Welcome, {userInfo.username}!</h3>
          <p>Age: {userInfo.age}</p>
          <p>Country: {userInfo.country}</p>
          <p>Email: {userInfo.email}</p>
        </div>
      )}

      <button onClick={joinGame}>Join Game</button>

      <button disabled={!isStartEnabled}>Start Game</button>
    </div>
  );
}

export default Dashboard;
