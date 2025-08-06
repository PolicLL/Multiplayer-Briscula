// src/components/Menu.js
import React from "react";
import { useNavigate } from "react-router-dom";

function Menu({ onLogout }) {
  const navigate = useNavigate();
  const isLoggedIn = !!sessionStorage.getItem("jwtToken");

  const handleLogout = () => {
    sessionStorage.clear();
    onLogout();
    navigate("/");
  };

  return (
    <div style={{ position: "absolute", top: 10, right: 10 }}>
      <button onClick={() => navigate("/")}>Home</button>
      {isLoggedIn && (
        <button onClick={() => navigate("/dashboard")}>Dashboard</button>
      )}
      {isLoggedIn && <button onClick={handleLogout}>Logout</button>}
    </div>
  );
}

export default Menu;
