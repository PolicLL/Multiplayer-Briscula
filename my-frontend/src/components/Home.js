import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

function Home() {
  const navigate = useNavigate();
  const [userInfo, setUserInfo] = useState([]);

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await axios.get("http://localhost:8080/api/users");
        setUserInfo(response.data); // Fix: use correct variable
      } catch (error) {
        console.error(error);
      }
    };

    fetchUserInfo();
  }, []);

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <h1>Welcome to the game</h1>
      <p>Please choose option to proceed: </p>

      <button onClick={() => navigate("/login")}>Log In</button>
      <button onClick={() => navigate("/signup")}>Sign Up</button>
      <button onClick={() => navigate("/anonymous")}>
        Continue Anonymously
      </button>

      <h2 style={{ marginTop: "40px" }}>Top 15 players</h2>
      <table
        style={{
          margin: "0 auto",
          borderCollapse: "collapse",
          width: "80%",
        }}
        border="1"
      >
        <thead>
          <tr>
            <th>Username</th>
            <th>Email</th>
            <th>Age</th>
            <th>Country</th>
            <th>Points</th>
            <th>Level</th>
          </tr>
        </thead>
        <tbody>
          {userInfo.map((user) => (
            <tr key={user.id}>
              <td>{user.username}</td>
              <td>{user.email}</td>
              <td>{user.age}</td>
              <td>{user.country}</td>
              <td>{user.points}</td>
              <td>{user.level}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default Home;
