import React, { useState } from "react";

/* TODO: Add animation while waiting for other players to join room */

function JoinGamePanel({
  name,
  setName,
  shouldShowPoints,
  handleCheckboxChange,
  joinGame,
  leaveGame,
  isAnonymous,
}) {
  const [selectedGame, setSelectedGame] = useState(null);

  const handleJoin = (players) => {
    joinGame(players);
    setSelectedGame(players);
  };

  const handleLeave = () => {
    leaveGame();
    setSelectedGame(null);
  };

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
            onChange={(e) => setName(e.target.value)}
            />
          </div>
        )}

        <div className="join-game-buttons">
          <button
            className={`primary ${selectedGame === 2 ? "active" : ""}`}
            onClick={() => handleJoin(2)}
            disabled={!!selectedGame || (isAnonymous && !name.trim())}
          >
            Join Game (1v1)
          </button>

          <button
            className={`primary ${selectedGame === 3 ? "active" : ""}`}
            onClick={() => handleJoin(3)}
            disabled={!!selectedGame || (isAnonymous && !name.trim())}
          >
            Join Game (1v1v1)
          </button>

          <button
            className={`primary ${selectedGame === 4 ? "active" : ""}`}
            onClick={() => handleJoin(4)}
            disabled={!!selectedGame || (isAnonymous && !name.trim())}
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

        <label>
          <input
            type="checkbox"
            checked={shouldShowPoints}
            onChange={handleCheckboxChange}
          />
          Show Points
        </label>
      </div>
    </main>
  );
}

export default JoinGamePanel;
