import React, { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";

function GameRoom() {
  const { roomId, playerId } = useParams();
  const [message, setMessage] = useState("");
  const [cards, setCards] = useState([]);
  const [cardsClickable, setCardsClickable] = useState(false); // control globally

  const socketRef = useRef(null);

  const parseWebSocketMessage = (message) => {
    try {
      const parsed = JSON.parse(message);
      return {
        type: parsed.type,
        roomId: parsed.roomId,
        playerId: parsed.playerId,
        content: parsed.content,
      };
    } catch (error) {
      console.error("Invalid JSON.", error);
      return null;
    }
  };

  const parseCards = (cardString) => {
    if (!cardString) return [];

    return cardString.split(" ").map((cardCode) => ({
      code: cardCode,
      imageUrl: `/cards/${cardCode}.png`,
    }));
  };

  const handleCardClick = (card) => {
    if (!cardsClickable) return;

    const cardIndex = cards.indexOf(card);

    setCards((prevCards) => prevCards.filter((tempCard) => tempCard !== card));
    setCardsClickable(false);
    setMessage("");

    const socket = socketRef.current;

    console.log("socket: " + socket);
    console.log("URI: " + socket.url);

    if (socket && socket.readyState === WebSocket.OPEN) {
      console.log("Socket seems ready");
      console.log(`STATE : ${roomId} ${playerId} ${cardIndex}`);

      setTimeout(() => {
        const message = JSON.stringify({
          type: "CARD_CHOSEN",
          roomId: roomId,
          playerId: playerId,
          card: cardIndex,
        });

        socket.send(message);
      }, 100);

      socket.onerror = (event) => {
        console.error("WebSocket error:", event);
      };

      socket.onclose = (event) => {
        console.log("WebSocket closed:", event.code, event.reason);
      };
    }
  };

  useEffect(() => {
    const socket = new WebSocket(`ws://localhost:8080/game/${roomId}`);
    socketRef.current = socket;

    socket.onopen = () => {
      socket.send(
        JSON.stringify({
          type: "GET_INITIAL_CARDS",
          roomId: roomId,
          playerId: playerId,
        })
      );
    };

    socket.onmessage = (event) => {
      const message = event.data;
      const parsedMessage = parseWebSocketMessage(message);

      if (
        parsedMessage.type === "SENT_INITIAL_CARDS" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        setCards(parseCards(parsedMessage.content));

        socket.send(
          JSON.stringify({
            type: "INITIAL_CARDS_RECEIVED",
            roomId: roomId,
            playerId: playerId,
          })
        );
      }

      if (
        parsedMessage.type === "CARDS_STATE_UPDATE" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        setCards(parseCards(parsedMessage.content));
      }

      if (
        parsedMessage.type === "CHOOSE_CARD" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        setMessage(parsedMessage.content);
        setCardsClickable(true);
      }

      if (
        parsedMessage.type === "REMOVE_CARD" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        setCards((prevCards) => prevCards.slice(1));
        setCardsClickable(false);
      }
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
      <h2>Player id : {playerId}</h2>

      <div style={{ display: "flex", gap: "10px", marginTop: "20px" }}>
        {cards.map((card) => (
          <img
            key={card.code}
            src={card.imageUrl}
            alt={card.code}
            style={{ width: "100px", cursor: "pointer" }}
            onClick={() => cardsClickable && handleCardClick(card)}
          />
        ))}
      </div>

      <div>
        <h3>Messages: {message}</h3>
      </div>
    </div>
  );
}

export default GameRoom;
