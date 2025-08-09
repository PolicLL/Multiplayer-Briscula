// src/components/Menu.js
import React from "react";
import { useNavigate } from "react-router-dom";

function Menu({ onLogout }) {
  const navigate = useNavigate();
  const isLoggedIn = !!sessionStorage.getItem("jwtToken");

  const handleLogout = async () => {
    const token = sessionStorage.getItem("jwtToken");

    if (token) {
      try {
        const response = await fetch("http://localhost:8080/api/users/logout", {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) {
          const errorText = await response.text();
          console.error("Logout failed:", errorText);
        } else {
          console.log("Logout successful");
        }
      } catch (error) {
        console.error("Error logging out:", error);
      }
    }

    // Always clear session and update UI regardless of server response
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
