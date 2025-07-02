import { useEffect, useRef } from "react";
import { parseWebSocketMessage } from "../utils/websocketUtils";

export function useGameWebSocket({ onGameStart, onMessage, onStatusChange }) {
  const socketRef = useRef(null);

  useEffect(() => {
    const socket = new WebSocket("ws://localhost:8080/game/prepare");
    socketRef.current = socket;

    socket.onopen = () => {
      onStatusChange?.("Connected to the server successfully!");
    };

    socket.onmessage = (event) => {
      const message = event.data;
      const parsedMessage = parseWebSocketMessage(message);

      if (message.includes("GAME_STARTED")) {
        onGameStart?.(parsedMessage.roomId, parsedMessage.playerId);
      } else {
        onMessage?.(message);
      }
    };

    socket.onerror = () => {
      onStatusChange?.("Failed to connect to the server.");
    };

    socket.onclose = () => {
      onStatusChange?.("Disconnected from server.");
    };

    return () => socket.close();
  }, []);

  return socketRef;
}
