import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

function GameRoom() {
  const { roomId, playerId } = useParams();
  const [messages, setMessages] = useState([]);
  const [cards, setCards] = useState([]);

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
    console.log("Card clicked " + card.code + ".");
  };

  useEffect(() => {
    const socket = new WebSocket(`ws://localhost:8080/game/${roomId}`);

    socket.onopen = () => {
      console.log(`Connected to game room ${roomId}.`);
      console.log(`Player id ${playerId}.`);

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

      console.log("Message received : " + message);

      if (
        parsedMessage.type === "SENT_INITIAL_CARDS" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        setMessages((prev) => [...prev, parsedMessage]);
        console.log("Show message." + parsedMessage.content);

        setCards(parseCards(parsedMessage.content));

        socket.send(
          JSON.stringify({
            type: "INITIAL_CARDS_RECEIVED",
            roomId: roomId,
            playerId: playerId,
          })
        );

        console.log("Initial cards received.");
      }

      if (
        parsedMessage.type === "CHOOSE_CARD" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        console.log("POINT 1");

        setMessages((prev) => [...prev, parsedMessage]);
        console.log("Show message." + parsedMessage.content);

        setCards(parseCards(parsedMessage.content));

        console.log("Choose card.");
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

      <div style={{ display: "flex", gap: "10px", marginTop: "20px" }}>
        {cards.map((card) => (
          <img
            key={card.code}
            src={card.imageUrl}
            alt={card.code}
            style={{ width: "100px", cursor: "pointer" }}
            onClick={() => handleCardClick(card)}
          />
        ))}
      </div>

      <div>
        <h3>Messages: </h3>
      </div>
    </div>
  );
}

export default GameRoom;
