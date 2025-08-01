import React, { useEffect, useState, useRef, useCallback } from "react";
import { useParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { useWebSocketContext } from "../context/WebSocketContext";

function GameRoom() {
  const navigate = useNavigate();
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

  const { sendMessage, setOnMessage } = useWebSocketContext();

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

    sendMessage({
      type: "CARD_CHOSEN",
      roomId,
      playerId,
      card: cardIndex,
    });
  };

  const handleGameRoomMessage = useCallback(
    (parsedMessage) => {
      switch (parsedMessage.type) {
        case "TOURNAMENT_WON":
          setMessage("Won tournament.");
          break;
      }

      if (parsedMessage.roomId !== roomId) return;
      if (parsedMessage.playerId !== parseInt(playerId)) return;

      console.log("Received message type:", parsedMessage.type);

      switch (parsedMessage.type) {
        case "SENT_INITIAL_CARDS": {
          const { cards, numberOfPoints } = parsePlayerStatus(
            parsedMessage.content
          );
          setCards(cards);
          setShouldShowPoints(numberOfPoints !== -1);
          if (numberOfPoints !== -1) setPoints(numberOfPoints);

          sendMessage({
            type: "INITIAL_CARDS_RECEIVED",
            roomId,
            playerId,
          });
          break;
        }

        case "CARDS_STATE_UPDATE": {
          const { cards, numberOfPoints } = parsePlayerStatus(
            parsedMessage.content
          );
          setCards(cards);
          setShouldShowPoints(numberOfPoints !== -1);
          if (numberOfPoints !== -1) setPoints(numberOfPoints);
          setThrownCards([]);
          break;
        }

        case "CHOOSE_CARD": {
          setMessage(parsedMessage.content);
          setCardsClickable(true);
          setTimeLeft(30);

          if (timerRef.current) clearInterval(timerRef.current);

          timerRef.current = setInterval(() => {
            setTimeLeft((prev) => {
              if (prev <= 1) {
                clearInterval(timerRef.current);
                setCardsClickable(false);
                return 0;
              }
              return prev - 1;
            });
          }, 1000);
          break;
        }

        case "REMOVE_CARD":
          setCards((prevCards) => prevCards.slice(1));
          setCardsClickable(false);
          break;

        case "SENT_MAIN_CARD":
          setMainCard(parseCard(parsedMessage.content));
          setCardsClickable(false);
          break;

        case "REMOVE_MAIN_CARD":
          setMainCard({ cardType: "", cardValue: "" });
          setCardsClickable(false);
          break;

        case "PLAYER_LOST":
          setMessage("Player lost.");
          setCardsClickable(false);
          break;

        case "PLAYER_WON":
          setMessage("Player won.");
          setCardsClickable(false);
          break;

        case "NO_WINNER":
          setMessage("No winner.");
          setCardsClickable(false);
          break;

        case "RECEIVED_THROWN_CARD":
          setThrownCards((prevCards) => [
            ...prevCards,
            parseCard(parsedMessage.content),
          ]);
          break;

        case "WAIT_FOR_NEXT_MATCH":
          navigate(`/dashboard`);
          break;

        case "RESTARTING_MATCH":
          navigate(`/dashboard`);
          break;

        case "TOURNAMENT_WON":
          setMessage("Won tournament.");
          break;

        case "SENT_COLLEAGUES_CARDS": {
          const { cards } = parsePlayerStatus(parsedMessage.content);
          setColleaguesCards(cards);
          setTimeLeft(9);

          if (timerRef.current) clearInterval(timerRef.current);

          timerRef.current = setInterval(() => {
            setTimeLeft((prev) => {
              if (prev <= 1) {
                clearInterval(timerRef.current);
                setCardsClickable(false);
                setColleaguesCards([]);
                return 0;
              }
              return prev - 1;
            });
          }, 1000);
          break;
        }

        default:
          console.log("Unhandled message type:", parsedMessage.type);
      }
    },
    [roomId, playerId, sendMessage]
  );

  useEffect(() => {
    setOnMessage(handleGameRoomMessage);

    console.log("Get initial cards sent.");

    sendMessage({
      type: "GET_INITIAL_CARDS",
      roomId,
      playerId,
    });

    return () => {
      if (timerRef.current) clearInterval(timerRef.current);

      setOnMessage(null);

      sendMessage({
        type: "DISCONNECT_FROM_GAME",
        roomId,
        playerId,
      });
    };
  }, [
    roomId,
    playerId,
    sendMessage,
    setOnMessage,
    handleGameRoomMessage,
    setOnMessage,
  ]);

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
