import React, { useState } from "react";

function App() {
  const [status, setStatus] = useState("Click 'Join Game' to connect.");
  const [receivedMessage, setReceivedMessage] = useState("");
  const [name, setName] = useState("");
  let socket;

  // Function to handle WebSocket connection
  const connectWebSocket = () => {
    if (!name.trim()) {
      alert("You did not enter a name.");
      return;
    }

    socket = new WebSocket("ws://localhost:8080/game/prepare"); // Change port if needed

    socket.onopen = () => {
      setStatus("Connected to the server successfully!");
      socket.send(name);
    };

    socket.onmessage = (event) => {
      const message = event.data;
      setReceivedMessage(message);
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

      <input
        type="text"
        placeholder="Enter the name: "
        value={name}
        onChange={(e) => setName(e.target.value)}
      />

      <button onClick={connectWebSocket}>Join Game</button>
      <p id="status" style={{ fontWeight: "bold", fontSize: "18px" }}>
        {status}
      </p>
      <h3>Received message: {receivedMessage}</h3>
    </div>
  );
}

export default App;
