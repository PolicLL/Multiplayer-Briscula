import React, { useEffect, useState, useRef, useCallback } from "react";
import { useParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";
import { useWebSocketContext } from "../context/WebSocketContext";
import Menu from "./Menu";

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

  const [winnerInfo, setWinnerInfo] = useState("");

  const { sendMessage, setOnMessage } = useWebSocketContext();

  // Add this near your other useState hooks
  const [animatingThrownCard, setAnimatingThrownCard] = useState(null);

  const [opponentName, setOpponentName] = useState("Opponent");

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

  const parseInitialCards = (initialCardsMessage) => {
    const parsed = JSON.parse(initialCardsMessage);
    const cardString = parsed.cards;
    if (!cardString) return [];

    const cardArray = cardString.trim().split(" ");

    const cards = cardArray.map((cardCode) => ({
      code: cardCode,
      imageUrl: `/cards/${cardCode}.png`,
    }));

    const showingPoints = parsed.showPoints;

    return {
      cards,
      showingPoints,
    };
  };

  const parseCard = (cardString) => {
    const [code, owner, ...playerNameParts] = cardString.trim().split(" ");
    const playerName = playerNameParts.join(" "); // Handles names with spaces
    return {
      code,
      imageUrl: `/cards/${code}.png`,
      owner,
      playerName,
      isPlayer: owner === "players",
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
          const { cards, showingPoints } = parseInitialCards(
            parsedMessage.content
          );
          setCards(cards);
          setShouldShowPoints(showingPoints);

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
          setPoints(numberOfPoints);
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

        case "PLAYER_WON": {
          const resultObject = JSON.parse(parsedMessage.content);
          setWinnerInfo(resultObject);
          setCardsClickable(false);
          setThrownCards([]);
          break;
        }

        case "PLAYER_LOST":
          const resultObject = JSON.parse(parsedMessage.content);
          setWinnerInfo(resultObject);
          setCardsClickable(false);
          setThrownCards([]);
          break;

        case "NO_WINNER":
          setMessage("No winner.");
          setWinnerInfo("No winner.");
          setCardsClickable(false);
          setThrownCards([]);
          break;

        case "RECEIVED_THROWN_CARD": {
          const card = parseCard(parsedMessage.content);
          setAnimatingThrownCard({ ...card, from: card.isPlayer ? "bottom" : "top" });

          setOpponentName(card.playerName);

          // After animation, move to thrownCards and clear animating card
          setTimeout(() => {
            setThrownCards((prevCards) => [
              ...prevCards,
              { ...card, from: card.isPlayer ? "bottom" : "top" },
            ]);
            setAnimatingThrownCard(null);
          }, 500); // 500ms matches your CSS animation duration
          break;
        }

        case "WAIT_FOR_NEXT_MATCH":
          setThrownCards([]);
          // TODO: Also when moving to /dashboard make sure there is some animation or message that will indicate that player is waiting
          navigate(`/dashboard`);
          break;

        case "RESTARTING_MATCH":
          setThrownCards([]);
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
    const handleBeforeUnload = (event) => {
      // Send the disconnect message
      sendMessage({
        type: "DISCONNECT_FROM_GAME",
        roomId,
        playerId,
      });

      // Optional: prevent default dialog
      event.preventDefault();
      event.returnValue = "";
    };

    window.addEventListener("beforeunload", handleBeforeUnload);

    setOnMessage(handleGameRoomMessage);

    sendMessage({
      type: "GET_INITIAL_CARDS",
      roomId,
      playerId,
    });

    return () => {
      if (timerRef.current) clearInterval(timerRef.current);

      setOnMessage(null);

      console.log("Sending disconnect from game message.")

      window.removeEventListener("beforeunload", handleBeforeUnload);
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
    <div className="game-room">
      <Menu />

      {/* Main card in top-left */}
      {mainCard && mainCard.code && mainCard.imageUrl && (
        <div className="main-card top-left">
          <img
            key={mainCard.code}
            src={mainCard.imageUrl}
            alt={mainCard.code}
          />
        </div>
      )}

      <div className="opponent-avatar top-center">
        <div className="opponent-name">{opponentName}</div>
        <img
          src="/images/anonymous.png" // Place your image in public/images/
          alt="Opponent"
          className="opponent-image"
        />
      </div>

      <div className="colleagues-cards top-center">
        {colleaguesCards.map((card, idx) => (
          <img
            key={card.code}
            src={card.imageUrl}
            alt={card.code}
            className="card from-top"
            style={{ animationDelay: `${idx * 0.1}s` }}
          />
        ))}
      </div>

      {/* Opponent's cards at the top center */}
      <div className="colleagues-cards top-center">
        {colleaguesCards.map((card, idx) => (
          <img
            key={card.code}
            src={card.imageUrl}
            alt={card.code}
            className="card from-top"
            style={{ animationDelay: `${idx * 0.1}s` }}
          />
        ))}
      </div>

      <div className="thrown-cards center">
        {/* Winner info overlay */}
        {winnerInfo && typeof winnerInfo === "object" && (
          <div className="winner-info-overlay">
            <h2>{winnerInfo.status}</h2>
            <div>
              <strong>Winner:</strong> {winnerInfo.winner}
            </div>
            <div>
              <strong>Points:</strong> {winnerInfo.points}
            </div>
          </div>
        )}
        {/* First render already thrown cards */}
        {thrownCards.map((card, idx) => (
          <img
            key={card.code + idx}
            src={card.imageUrl} ßß
            alt={card.code}
            className="card thrown-card" ß
          />
        ))}
        {/* Then render the animating card, so it appears on the right */}
        {animatingThrownCard && (
          <img
            key={animatingThrownCard.code + "-animating"}
            src={animatingThrownCard.imageUrl}
            alt={animatingThrownCard.code}
            className={`card thrown-card ${animatingThrownCard.from === "top" ? "from-top-anim" : "from-bottom-anim"}`}
          />
        )}
      </div>

      {/* Your cards centered at the bottom */}
      <div className="your-cards bottom-center">
        {cards.map((card, idx) => (
          <img
            key={card.code}
            src={card.imageUrl}
            alt={card.code}
            className="card from-bottom"
            onClick={() => cardsClickable && handleCardClick(card)}
            style={{ animationDelay: `${idx * 0.1}s` }}
          />
        ))}
      </div>

      {/* HUD Info */}
      <div className="hud-info">
        {timeLeft > 0 && <h4 className="timer">Time left: {timeLeft}s</h4>}
        {shouldShowPoints && <h3 className="points">Points: {points}</h3>}
        <h2 className="messages">{message}</h2>
      </div>
    </div>
  );

}

export default GameRoom;
