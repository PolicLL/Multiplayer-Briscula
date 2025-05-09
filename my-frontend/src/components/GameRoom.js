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
    console.log("Card clicked " + card.code + ".");

    const cardIndex = cards.indexOf(card);

    setCards((prevCards) => prevCards.filter((tempCard) => tempCard !== card));
    //setCardsClickable(false);
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

        console.log("Message to send:", message); // Log the message
        socket.send(message);

        console.log("Chosen card sent..");
      }, 100);

      socket.onerror = (event) => {
        console.error("WebSocket error:", event);
      };

      socket.onclose = (event) => {
        console.log("WebSocket closed:", event.code, event.reason);
      };
    }
  };

  const test = () => {
    const socket = socketRef.current;

    if (socket && socket.readyState === WebSocket.OPEN) {
      socket.send(
        JSON.stringify({
          type: "GET123",
          roomId: roomId,
          playerId: playerId,
        })
      );

      socket.onerror = (event) => {
        console.error("WebSocket error:", event);
      };

      socket.onclose = (event) => {
        console.log("WebSocket closed:", event.code, event.reason);
      };

      console.log("Chosen card sent.");
    } else {
      console.log("Socket not connected.");
      setMessage("Socket not connected.");
    }
  };

  useEffect(() => {
    const socket = new WebSocket(`ws://localhost:8080/game/${roomId}`);
    socketRef.current = socket;

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

      console.log("Before parsing: " + message);

      const parsedMessage = parseWebSocketMessage(message);

      console.log("Message received : " + message);

      if (
        parsedMessage.type === "SENT_INITIAL_CARDS" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        //setMessage((prev) => [...prev, parsedMessage]);
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
        setMessage(parsedMessage.content);

        console.log("Cards are clickable now.");

        setCardsClickable(true);
        console.log("Show message." + parsedMessage.content);
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
        <button onClick={() => test()}>TEST</button>
        <h3>Messages: {message}</h3>
      </div>
    </div>
  );
}

export default GameRoom;
