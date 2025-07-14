// src/hooks/useTournamentWebSocket.js
import { useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";

export function useTournamentWebSocket(onTournamentUpdate) {
  const socketRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    const socket = new WebSocket("ws://localhost:8080/tournament");
    socketRef.current = socket;

    socket.onopen = () => {
      console.log("Tournament WebSocket connected.");
    };

    socket.onmessage = (event) => {
      const parsedMessage = JSON.parse(event.data);

      if (parsedMessage.type === "GAME_STARTED") {
        console.log("Game started.");
        navigate(`/game/${parsedMessage.roomId}/${parsedMessage.playerId}`);
      }

      if (onTournamentUpdate) {
        onTournamentUpdate(parsedMessage);
      }
    };

    socket.onerror = () => {
      console.error("Tournament WebSocket error.");
    };

    socket.onclose = () => {
      console.warn("Tournament WebSocket closed.");
    };

    return () => socket.close();
  }, [onTournamentUpdate]);

  return socketRef;
}
