import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

function GameRoom() {
  const { roomId, playerId } = useParams();
  const [messages, setMessages] = useState([]);

  useEffect(() => {
    const socket = new WebSocket(`ws://localhost:8080/game/${roomId}`);

    socket.onopen = () => {
      console.log(`Connected to game room ${roomId}.`);
      console.log(`Player id ${playerId}.`);

      socket.send(
        JSON.stringify({
          type: "GET_CARDS",
          gameRoomId: roomId,
          playerId: playerId,
        })
      );
    };

    socket.onmessage = (event) => {
      const message = event.data;
      console.log("Message received : " + message);
      setMessages((prev) => [...prev, message]);
    };

    socket.onerror = (error) => {
      console.error("WebSocket error:", error);
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
