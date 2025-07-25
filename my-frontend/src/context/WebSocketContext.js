import React, {
  createContext,
  useContext,
  useEffect,
  useRef,
  useCallback,
} from "react";

const WebSocketContext = createContext();

export const WebSocketProvider = ({ children }) => {
  const socketRef = useRef(null);
  const onMessageRef = useRef(null);

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

    socket.onopen = () => console.log("WebSocket connected");
    socket.onclose = () => console.log("WebSocket closed");
    socket.onerror = (err) => console.error("WebSocket error:", err);

    socket.onmessage = (event) => {
      console.log("Raw WebSocket message:", event.data); // 👈 always shows incoming data
      try {
        const parsed = JSON.parse(event.data);
        console.log("Parsed WebSocket message:", parsed); // 👈 always logs the parsed object
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
