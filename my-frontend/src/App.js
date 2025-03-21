import React, { useState } from "react";

function App() {
  const [status, setStatus] = useState("Click 'Join Game' to connect.");
  let socket;

  // Function to handle WebSocket connection
  const connectWebSocket = () => {
    socket = new WebSocket("ws://localhost:8080/game"); // Change port if needed

    socket.onopen = () => {
      setStatus("Connected to the server successfully!");
    };

    socket.onerror = () => {
      setStatus("Failed to connect to the server.");
    };

    socket.onclose = () => {
      setStatus("Disconnected from server.");
    };
  };

  return (
    <div>
      <h1>WebSocket Connection Test</h1>
      <button onClick={connectWebSocket}>Join Game</button>
      <p id="status" style={{ fontWeight: "bold", fontSize: "18px" }}>
        {status}
      </p>
    </div>
  );
}

export default App;
