// src/hooks/useTournamentWebSocket.js
import { useEffect, useRef } from "react";

export function useTournamentWebSocket(onTournamentUpdate) {
  const socketRef = useRef(null);

  useEffect(() => {
    const socket = new WebSocket("ws://localhost:8080/tournament");
    socketRef.current = socket;

    socket.onopen = () => {
      console.log("Tournament WebSocket connected.");
    };

    socket.onmessage = (event) => {
      const tournamentUpdate = JSON.parse(event.data);
      console.log("Received tournament update:", tournamentUpdate);

      // if tournament is full and game should start
      //navigate(`/game/${roomId}/${playerId}`);

      if (onTournamentUpdate) {
        onTournamentUpdate(tournamentUpdate);
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
