import React, { useState, useEffect } from "react";

function JoinGamePanel({
  name,
  setName,
  shouldShowPoints,
  handleCheckboxChange,
  joinGame,
  leaveGame,
  isAnonymous,
  status,
  serverError
}) {
  const [selectedGame, setSelectedGame] = useState(null);
  const [error, setError] = useState("");

  const handleJoin = (players) => {
    if (isAnonymous && !name.trim()) {
      setError("Name is required to join a game.");
      return;
    }

    setError(""); // clear error when valid
    joinGame(players);
    setSelectedGame(players);
  };

  const handleLeave = () => {
    leaveGame();
    setSelectedGame(null);
  };

  useEffect(() => {
    if (serverError) {
      setError(serverError);
      setSelectedGame(null);
    }
  }, [serverError]);

  const isWaiting = !!selectedGame;

  return (
    <main className="join-game-controls">
      <div className="game-controls">
        {setName && (
          <div className="anonymous-part">
            <h1>Anonymous User</h1>
            <input
              type="text"
              placeholder="Enter the name"
              value={name}
              onChange={(e) => {
                setName(e.target.value);
                setError("");
              }}
            />

            {error && <p className="error-message">{error}</p>}
          </div>
        )}

        <div className="join-game-buttons">
          <button
            className={`primary ${selectedGame === 2 ? "active" : ""}`}
            onClick={() => handleJoin(2)}
            disabled={!!selectedGame}
          >
            Join Game (1v1)
          </button>

          <button
            className={`primary ${selectedGame === 3 ? "active" : ""}`}
            onClick={() => handleJoin(3)}
            disabled={!!selectedGame}
          >
            Join Game (1v1v1)
          </button>

          <button
            className={`primary ${selectedGame === 4 ? "active" : ""}`}
            onClick={() => handleJoin(4)}
            disabled={!!selectedGame}
          >
            Join Game (2v2)
          </button>

          <button
            className="secondary"
            onClick={handleLeave}
            disabled={!selectedGame}
          >
            Leave Game
          </button>
        </div>

        {isWaiting && (
          <div className="waiting-section">
            <div className="spinner"></div>
            <p className="waiting-text">Waiting for others to join</p>
          </div>
        )}

        <label>
          <input
            type="checkbox"
            checked={shouldShowPoints}
            onChange={handleCheckboxChange}
            disabled={!!selectedGame}
          />
          Show Points
        </label>

      </div>
    </main>
  );
}

export default JoinGamePanel;
