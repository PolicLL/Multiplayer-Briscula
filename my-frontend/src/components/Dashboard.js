import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function Dashboard() {
  const navigate = useNavigate();
  const [message, setMessage] = useState("");
  const [userInfo, setUserInfo] = useState({});
  const [status, setStatus] = useState("Click 'Join Game' to connect.");
  const [username, setUsername] = useState(() =>
    localStorage.getItem("username")
  );
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

  const connectWebSocket = () => {
    socket = new WebSocket("ws://localhost:8080/game/prepare"); // Change port if needed

    socket.onopen = () => {
      setStatus("Connected to the server successfully!");
      socket.send(username);
    };

    socket.onmessage = (event) => {
      const message = event.data;

      if (message === "START_ENABLED") {
        setIsStartEnabled(true);
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

  const handleStartGame = async () => {
    try {
      /*       const response = await axios.post(
        `http://localhost:8080/api/game/start-game/${userInfo.id}`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
          },
        }
      ); */

      // Shift Alt A

      const response = await axios.post(`http://localhost:8080/game/prepare`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
        },
      });

      setMessage("Game started successfully!");
      console.log("Response : " + response);
    } catch (error) {
      setMessage("Error starting game!");
      console.error(error);
    }
  };

  return (
    <div>
      <h2>Hello !</h2>
      {message && <p>{message}</p>}

      {/* Display user info if available */}
      {userInfo && (
        <div>
          <h3>Welcome, {userInfo.username}!</h3>
          <p>Age: {userInfo.age}</p>
          <p>Country: {userInfo.country}</p>
          <p>Email: {userInfo.email}</p>
        </div>
      )}

      <button onClick={connectWebSocket}>Start Game</button>
    </div>
  );
}

export default Dashboard;
