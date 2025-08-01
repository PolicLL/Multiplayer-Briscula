import React, {
  createContext,
  useContext,
  useEffect,
  useRef,
  useCallback,
} from "react";

import { useNavigate } from "react-router-dom";

const WebSocketContext = createContext();

export const WebSocketProvider = ({ children }) => {
  const socketRef = useRef(null);
  const onMessageRef = useRef(null);
  const navigate = useNavigate();

  const sendMessage = useCallback((message) => {
    if (socketRef.current && socketRef.current.readyState === WebSocket.OPEN) {
      socketRef.current.send(JSON.stringify(message));
    } else {
      console.warn("WebSocket not connected.");
    }
  }, []);

  // Update the ref whenever setOnMessage is called
  const setOnMessage = useCallback((handler) => {
    console.log("WebSocket message handler registered.");
    onMessageRef.current = handler;
  }, []);

  useEffect(() => {
    const socket = new WebSocket("ws://localhost:8080/game");
    socketRef.current = socket;

    socket.onopen = () => {
      console.log("WebSocket connected");
      sendMessage({ type: "LOGGED_IN" });
    };
    socket.onclose = () => console.log("WebSocket closed");
    socket.onerror = (err) => console.error("WebSocket error:", err);

    socket.onmessage = (event) => {
      try {
        const parsed = JSON.parse(event.data);
        console.log("Parsed WebSocket message:", parsed);

        if (parsed.type === "GAME_STARTED") {
          navigate(`/game/${parsed.roomId}/${parsed.playerId}`); // ✅ new
          return;
        }

        if (onMessageRef.current) {
          onMessageRef.current(parsed);
        } else {
          console.warn("No onMessage handler set!");
        }
      } catch (err) {
        console.error("Error parsing WebSocket message:", err);
      }
    };

    return () => {
      socket.close();
    };
  }, []); // empty dependency, only run once on mount

  return (
    <WebSocketContext.Provider value={{ sendMessage, setOnMessage }}>
      {children}
    </WebSocketContext.Provider>
  );
};

export const useWebSocketContext = () => useContext(WebSocketContext);
