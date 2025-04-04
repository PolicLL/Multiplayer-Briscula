import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function Dashboard() {
  const navigate = useNavigate();
  const [message, setMessage] = useState("");
  const [userInfo, setUserInfo] = useState({});

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const token = localStorage.getItem("jwtToken");
        const username = localStorage.getItem("username");

        console.log("token : " + token);
        console.log("username : " + username);

        console.log("Point 1");

        if (!token) {
          setMessage("Please log in first.");
          return;
        }

        console.log("Point 2");

        const response = await axios.get("http://localhost:8080/api/users/by", {
          params: { username }, // Only sending username
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        //const response = await axios.get("http://localhost:8080/api/users/by");

        console.log("Point 3");

        setUserInfo(response.data);
      } catch (error) {
        setMessage("Error fetching user information.");
        console.error(error);
      }
    };

    fetchUserInfo();
  }, []);

  const handleStartGame = async () => {
    try {
      const response = await axios.post(
        "http://localhost:8080/api/game/start-game",
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("jwtToken")}`,
          },
        }
      );
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

      <button onClick={handleStartGame}>Start Game</button>
    </div>
  );
}

export default Dashboard;
