import React, { useEffect, useState, useRef } from "react";
import { useParams } from "react-router-dom";

function GameRoom() {
  const { roomId, playerId } = useParams();
  const [message, setMessage] = useState("");
  const [cards, setCards] = useState([]);
  const [thrownCards, setThrownCards] = useState([]);
  const [colleaguesCards, setColleaguesCards] = useState([]);
  const [points, setPoints] = useState(0);
  const [cardsClickable, setCardsClickable] = useState(false);
  const [mainCard, setMainCard] = useState({ cardType: "", cardValue: "" });

  const [shouldShowPoints, setShouldShowPoints] = useState(false);

  const [timeLeft, setTimeLeft] = useState(0);
  const timerRef = useRef(null);

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

  const parsePlayerStatus = (cardString, n) => {
    if (!cardString) return [];

    const cardArray = cardString.trim().split(" ");

    const lastElement = cardArray[cardArray.length - 1];
    const lastIsNumber = !isNaN(lastElement);

    const numberOfPoints = lastIsNumber ? parseInt(lastElement, 10) : null;
    const cardCodes = lastIsNumber ? cardArray.slice(0, -1) : cardArray;

    const cards = cardCodes.map((cardCode) => ({
      code: cardCode,
      imageUrl: `/cards/${cardCode}.png`,
    }));

    return {
      cards,
      numberOfPoints,
    };
  };

  const parseCard = (cardCode) => {
    return {
      code: cardCode,
      imageUrl: `/cards/${cardCode}.png`,
    };
  };

  const handleCardClick = (card) => {
    if (!cardsClickable) return;

    const cardIndex = cards.indexOf(card);

    setCards((prevCards) => prevCards.filter((tempCard) => tempCard !== card));
    setCardsClickable(false);
    setMessage("");
    setTimeLeft(0);

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
      console.log("Sending get initial cards.");
      console.log("Room id: " + roomId);
      console.log("Player id: " + playerId);

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

      console.log("Received message type: " + parsedMessage.type);

      if (
        parsedMessage.type === "SENT_INITIAL_CARDS" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        const { cards, numberOfPoints } = parsePlayerStatus(
          parsedMessage.content
        );
        setCards(cards);

        if (numberOfPoints === -1) {
          setShouldShowPoints(false);
        } else {
          setShouldShowPoints(true);
          setPoints(numberOfPoints);
        }

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
        const { cards, numberOfPoints } = parsePlayerStatus(
          parsedMessage.content
        );

        setCards(cards);

        if (numberOfPoints === -1) {
          setShouldShowPoints(false);
        } else {
          setShouldShowPoints(true);
          setPoints(numberOfPoints);
        }
        setThrownCards([]);
      }

      if (
        parsedMessage.type === "CHOOSE_CARD" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        setMessage(parsedMessage.content);
        setCardsClickable(true);
        setTimeLeft(30); // start countdown

        if (timerRef.current) clearInterval(timerRef.current);

        timerRef.current = setInterval(() => {
          setTimeLeft((prevTime) => {
            if (prevTime <= 1) {
              clearInterval(timerRef.current);
              setCardsClickable(false);
              return 0;
            }
            return prevTime - 1;
          });
        }, 1000);
      }

      if (
        parsedMessage.type === "REMOVE_CARD" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        setCards((prevCards) => prevCards.slice(1));
        setCardsClickable(false);
      }

      if (
        parsedMessage.type === "SENT_MAIN_CARD" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        setMainCard(parseCard(parsedMessage.content));
        setCardsClickable(false);
      }

      if (
        parsedMessage.type === "REMOVE_MAIN_CARD" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        setMainCard({ cardType: "", cardValue: "" });
        setCardsClickable(false);
      }

      if (
        parsedMessage.type === "PLAYER_LOST" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        setMessage("Player lost.");
        setCardsClickable(false);
      }

      if (
        parsedMessage.type === "PLAYER_WON" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        setMessage("Player won.");
        setCardsClickable(false);
      }

      if (
        parsedMessage.type === "NO_WINNER" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        setMessage("No winner.");
        setCardsClickable(false);
      }

      if (
        parsedMessage.type === "RECEIVED_THROWN_CARD" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        setThrownCards((prevCards) => [
          ...prevCards,
          parseCard(parsedMessage.content),
        ]);
      }

      if (
        parsedMessage.type === "SENT_COLLEAGUES_CARDS" &&
        parsedMessage.playerId === parseInt(playerId)
      ) {
        console.log("Entered block for SENT_COLLEAGUES_CARDS.");

        const { cards } = parsePlayerStatus(parsedMessage.content);

        setColleaguesCards(cards); // Set the value

        setTimeLeft(9);

        if (timerRef.current) clearInterval(timerRef.current);

        timerRef.current = setInterval(() => {
          setTimeLeft((prevTime) => {
            if (prevTime <= 1) {
              clearInterval(timerRef.current);
              setCardsClickable(false);
              setColleaguesCards([]);
              return 0;
            }
            return prevTime - 1;
          });
        }, 1000);
      }
    };

    socket.onerror = (error) => {
      console.error("WebSocket error:", error);
    };

    socket.onclose = () => {
      console.log("Websocket connection closed.");
    };

    // Handle browser/tab close or navigation
    const handleUnload = () => {
      if (socket && socket.readyState === WebSocket.OPEN) {
        socket.send(
          JSON.stringify({
            type: "DISCONNECT_FROM_GAME",
            roomId: roomId,
            playerId: playerId,
          })
        );
        socket.close();
      }
    };

    window.addEventListener("beforeunload", handleUnload);
    window.addEventListener("unload", handleUnload);

    return () => {
      if (timerRef.current) clearInterval(timerRef.current);

      handleUnload(); // ensure clean disconnect on unmount

      window.removeEventListener("beforeunload", handleUnload);
      window.removeEventListener("unload", handleUnload);
    };
  }, [roomId]);

  return (
    <div>
      {mainCard && mainCard.code && mainCard.imageUrl && (
        <>
          <h2>Main Card</h2>
          <div style={{ display: "flex", gap: "10px", marginTop: "20px" }}>
            <img
              key={mainCard.code}
              src={mainCard.imageUrl}
              alt={mainCard.code}
              style={{ width: "100px", cursor: "pointer" }}
            />
          </div>
        </>
      )}

      <h3>Your cards</h3>
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

      <h3>Thrown cards</h3>

      <div style={{ display: "flex", gap: "10px", marginTop: "20px" }}>
        {thrownCards.map((card) => (
          <img
            key={card.code}
            src={card.imageUrl}
            alt={card.code}
            style={{ width: "100px", cursor: "pointer" }}
          />
        ))}
      </div>

      <h3>Colleagues cards:</h3>
      <div style={{ display: "flex", gap: "10px", marginTop: "20px" }}>
        {colleaguesCards.map((card) => (
          <img
            key={card.code}
            src={card.imageUrl}
            alt={card.code}
            style={{ width: "100px", cursor: "pointer" }}
          />
        ))}
      </div>

      {timeLeft > 0 && <h4 style={{ color: "red" }}>Time left: {timeLeft}s</h4>}

      {shouldShowPoints && (
        <div>
          <h3>Points: {points}</h3>
        </div>
      )}

      <div>
        <h3>Messages: {message}</h3>
      </div>
    </div>
  );
}

export default GameRoom;
