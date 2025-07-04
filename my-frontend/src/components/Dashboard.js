import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import JoinGamePanel from "./common/JoinGamePanel";
import { useGameWebSocket } from "../hooks/useGameWebSocket";
import EditUserForm from "./EditUserForm";
import Menu from "./Menu";
import axios from "axios";
import TournamentForm from "../components/tournament/TournamentForm";
import TournamentList from "./tournament/TournamentList";

function Dashboard() {
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = useState({});
  const [username] = useState(() => localStorage.getItem("username"));
  const [message, setMessage] = useState("");
  const [shouldShowPoints, setShouldShowPoints] = useState(false);
  const [isDisabled, setIsDisabled] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [receivedMessage, setReceivedMessage] = useState("");
  const [status, setStatus] = useState("");
  const [showTournaments, setShowTournaments] = useState(false);

  const socketRef = useGameWebSocket({
    onGameStart: (roomId, playerId) => {
      navigate(`/game/${roomId}/${playerId}`);
    },
    onMessage: setReceivedMessage,
    onStatusChange: setStatus,
  });

  useEffect(() => {
    const token = localStorage.getItem("jwtToken");
    if (!token) return navigate("/");

    axios
      .get("http://localhost:8080/api/users/by", {
        params: { username },
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => setUserInfo(res.data))
      .catch(() => setMessage("Error fetching user info."));
  }, [navigate, username]);

  const joinGame = (numberOfPlayers) => {
    if (!socketRef.current || socketRef.current.readyState !== WebSocket.OPEN) {
      setStatus("Socket not connected.");
      return;
    }

    socketRef.current.send(
      JSON.stringify({
        type: "JOIN_ROOM",
        userId: userInfo.id,
        playerName: username,
        numberOfPlayers,
        shouldShowPoints,
      })
    );

    setIsDisabled(true);
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
          <h3>Welcome, {userInfo.username}!</h3>
          <p>Age: {userInfo.age}</p>
          <p>Country: {userInfo.country}</p>
          <p>Email: {userInfo.email}</p>
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
              onJoin={async (tournament) => {
                const joinTournament = {
                  tournamentId: tournament.id,
                  userId: userInfo.id,
                };

                const response = await axios.post(
                  "http://localhost:8080/api/tournament/join",
                  joinTournament
                );
              }}
            />
          )}

          <JoinGamePanel
            shouldShowPoints={shouldShowPoints}
            handleCheckboxChange={(e) => setShouldShowPoints(e.target.checked)}
            joinGame={joinGame}
            isDisabled={isDisabled}
            isStartEnabled={false}
          />
        </>
      )}
    </div>
  );
}

export default Dashboard;
