import React, { useEffect, useState, useRef } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import EditUserForm from "./EditUserForm";

function Dashboard() {
  const navigate = useNavigate();
  const [message, setMessage] = useState("");
  const [userInfo, setUserInfo] = useState({});
  const [status, setStatus] = useState("Click 'Join Game' to connect.");
  const [username] = useState(() => localStorage.getItem("username"));
  const [isStartEnabled, setIsStartEnabled] = useState(false);
  const [receivedMessage, setReceivedMessage] = useState("");
  const [isEditing, setIsEditing] = useState(false);

  const socketRef = useRef(null);

  const getPhotoUrl = (photoId) => {
    console.log(`Getting photo by id ${photoId}`);
    return `http://localhost:8080/api/photo/${photoId}`;
  };

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const token = localStorage.getItem("jwtToken");

        console.log("token : " + token);

        if (!token) {
          setMessage("Please log in first.");
          return;
        }

        console.log("before response : ");

        const userResponse = await axios.get(
          "http://localhost:8080/api/users/by",
          {
            params: { username },
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        console.log(userResponse);

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

      {isEditing ? (
        <EditUserForm
          user={userInfo}
          onCancel={() => setIsEditing(false)}
          onUpdate={(updatedUser) => {
            setUserInfo(updatedUser);
            setIsEditing(false);
          }}
        />
      ) : (
        <>
          {userInfo && (
            <div>
              <h3>Welcome, {userInfo.username}!</h3>
              <p>Age: {userInfo.age}</p>
              <p>Country: {userInfo.country}</p>
              <p>Email: {userInfo.email}</p>
            </div>
          )}

          <img
            src={getPhotoUrl(userInfo.photoId)}
            alt="User"
            style={{
              width: "150px",
              height: "150px",
              borderRadius: "50%",
              objectFit: "cover",
            }}
            onError={(e) => {
              e.target.onerror = null;
              e.target.src = "/images/anonymous.png";
            }}
          />

          <button onClick={() => setIsEditing(true)}>Edit Profile</button>
          <button onClick={joinGame}>Join Game</button>
          <button disabled={!isStartEnabled}>Start Game</button>
        </>
      )}
    </div>
  );
}

export default Dashboard;
