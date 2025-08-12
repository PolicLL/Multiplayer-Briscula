import React, { useEffect, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import JoinGamePanel from "./common/JoinGamePanel";
import EditUserForm from "./EditUserForm";
import Menu from "./Menu";
import axios from "axios";
import TournamentList from "./tournament/TournamentList";

import { useWebSocketContext } from "../context/WebSocketContext";

// TODO If I am logged in and go to the dashboard, I should not see log in options

function Dashboard() {
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = useState({});
  const [username] = useState(() => sessionStorage.getItem("username"));
  const [message, setMessage] = useState("");
  const [shouldShowPoints, setShouldShowPoints] = useState(false);
  const [isDisabled, setIsDisabled] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [status, setStatus] = useState("");
  const [showTournaments, setShowTournaments] = useState(false);
  const [numberOfPlayers, setNumberOfPlayers] = useState(-1);

  const [tournaments, setTournaments] = useState([]);

  const { sendMessage, setOnMessage } = useWebSocketContext();

  const handleMessage = useCallback(
    (parsedMessage) => {
      if (parsedMessage.type === "TOURNAMENT_UPDATE") {
        console.log("Tournament update received.");

        // Make sure content is parsed (it's a stringified object in your logs)
        const updatedTournament = JSON.parse(parsedMessage.content);

        // Use functional update to ensure latest state is used
        setTournaments((prev) => {
          const index = prev.findIndex((t) => t.id === updatedTournament.id);
          if (index !== -1) {
            return prev.map((t) =>
              t.id === updatedTournament.id ? updatedTournament : t
            );
          } else {
            return [...prev, updatedTournament];
          }
        });
      }
    },
    [navigate, setOnMessage] // ✅ Do NOT include `tournaments` here!
  );

  useEffect(() => {
    sessionStorage.setItem("isRegistered", true);
    // Register WebSocket message handler
    setOnMessage(handleMessage);

    // Fetch user info
    const token = sessionStorage.getItem("jwtToken");
    if (!token) return navigate("/");

    axios
      .get("http://localhost:8080/api/users/by", {
        params: { username },
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setUserInfo(res.data))
      .catch(() => setMessage("Error fetching user info."));

    // Fetch tournaments
    axios.get("http://localhost:8080/api/tournament").then((res) => {
      console.log("Fetched tournaments:", res.data);
      setTournaments(res.data);
    });

    // Cleanup on unmount
    return () => {
      setOnMessage(null);
    };
  }, [navigate, username, setOnMessage, handleMessage]); // ✅ `handleMessage` is memoized without `tournaments`

  useEffect(() => {
    console.log("Tournaments updated:", tournaments);
  }, [tournaments]);

  const joinGame = (numberOfPlayers) => {
    sendMessage({
      type: "JOIN_ROOM",
      userId: userInfo.id,
      playerName: username,
      numberOfPlayers,
      shouldShowPoints,
    });

    setIsDisabled(true);
    setNumberOfPlayers(numberOfPlayers);
  };

  const leaveGame = () => {
    sendMessage({
      type: "LEAVE_ROOM",
      playerName: username,
      numberOfPlayers: numberOfPlayers,
    });
    setIsDisabled(false);
  };

  const getPhotoUrl = (id) => `http://localhost:8080/api/photo/${id}`;

  return (
    <div>
      <Menu onLogout={() => setUserInfo(null)} />
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
          {userInfo?.photoId && (
            <img
              src={getPhotoUrl(userInfo.photoId)}
              alt="User"
              style={{
                width: "150px",
                height: "150px",
                borderRadius: "50%",
                objectFit: "cover",
              }}
              onError={(e) => (e.target.src = "/images/anonymous.png")}
            />
          )}
          <h3>Welcome, {userInfo?.username}!</h3>
          <p>Age: {userInfo?.age}</p>
          <p>Country: {userInfo?.country}</p>
          <p>Email: {userInfo?.email}</p>
          <button onClick={() => setIsEditing(true)}>Edit Profile</button>

          <div style={{ marginTop: "1rem" }}>
            <button
              onClick={() => navigate("/tournament/create")} // ✅ navigate to creation page
              className="bg-green-600 text-white px-4 py-2 rounded"
            >
              Create Tournament
            </button>
          </div>

          <button
            onClick={() => setShowTournaments((prev) => !prev)}
            className="bg-blue-500 text-white px-4 py-2 rounded mt-4 ml-2"
          >
            {showTournaments ? "Hide Tournaments" : "View Tournaments"}
          </button>

          {showTournaments && (
            <TournamentList
              tournaments={tournaments}
              setTournaments={setTournaments}
              onJoin={async (tournament) => {
                try {
                  sendMessage({
                    type: "JOIN_TOURNAMENT",
                    tournamentId: tournament.id,
                    playerId: userInfo.id,
                  });
                } catch (error) {
                  console.log("Error: " + error);
                  if (error.response) {
                    alert(
                      `Error joining tournament: ${error.response.data.message}`
                    );
                  } else {
                    alert("Error joining tournament. Please try again.");
                  }
                }
              }}
            />
          )}

          <JoinGamePanel
            shouldShowPoints={shouldShowPoints}
            handleCheckboxChange={(e) => setShouldShowPoints(e.target.checked)}
            joinGame={joinGame}
            isDisabled={isDisabled}
            leaveGame={leaveGame}
          />
        </>
      )}
    </div>
  );
}

export default Dashboard;
