import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

function GameRoom() {
  const { roomId } = useParams();
  const [messages, setMessages] = useState([]);

  useEffect(() => {
    const socket = new WebSocket("ws://localhost:8080/game/start");

    socket.onopen = () => {
      console.log(`Connected to game room ${roomId}.`);
      socket.send(roomId);
    };

    socket.onmessage = (event) => {
      const message = event.data;
      console.log("Message received : " + message);
      setMessages((prev) => [...prev, message]);
    };

    socket.onclose = () => {
      console.log("Websocket connection closed.");
    };

    return () => {
      socket.close();
    };
  }, [roomId]);

  return (
    <div>
      <h2>Game Room id : {roomId}</h2>

      <div>
        <h3>Messages: </h3>

        <ul>
          {messages.map((message, index) => (
            <li key={index}>{message}</li>
          ))}
        </ul>
      </div>
    </div>
  );
}

export default GameRoom;
