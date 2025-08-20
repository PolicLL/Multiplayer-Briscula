import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import Menu from "./Menu";

function Home() {
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = useState([]);

  const isRegistered = sessionStorage.getItem("isRegistered") === "true";

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await axios.get("http://localhost:8080/api/users?numberOfElements=10");

        setUserInfo(response.data); // Fix: use correct variable
      } catch (error) {
        console.error(error);
      }
    };

    fetchUserInfo();
  }, []);

  return (
    <>
      <Menu />
      <div style={{ textAlign: "center", marginTop: "50px" }}>
        <h1>Welcome to the game</h1>

        {!isRegistered && (
          <>
            <h4>Please choose option to proceed: </h4>

            <button className="button button-primary" onClick={() => navigate("/login")}>
              Log In
            </button>
            <button className="button button-primary" onClick={() => navigate("/signup")}>
              Sign Up
            </button>
            <button className="button button-secondary" onClick={() => navigate("/anonymous")}>
              Continue Anonymously
            </button>

          </>
        )}

        <h2 style={{ marginTop: "40px" }}>Top 10 players</h2>
        <table className="leaderboard-table">
          <thead>
            <tr>
              <th>Username</th>
              <th>Points</th>
              <th>Total Matches Played</th>
              <th>Total Wins</th>
              <th>Level</th>
            </tr>
          </thead>
          <tbody>
            {userInfo.map((user) => (
              <tr key={user.id}>
                <td>{user.username}</td>
                <td>{user.points}</td>
                <td>{user.totalMatchesPlayed}</td>
                <td>{user.totalWins}</td>
                <td>{user.level}</td>
              </tr>
            ))}
          </tbody>
        </table>

      </div>
    </>
  );
}

export default Home;
