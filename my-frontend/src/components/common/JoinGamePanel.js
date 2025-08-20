// JoinGamePanel.js
import React from "react";

function JoinGamePanel({
  name,
  setName,
  shouldShowPoints,
  handleCheckboxChange,
  joinGame,
  isDisabled,
  leaveGame,
}) {
  return (
    <div className="game-controls">
      {setName && (
        <input
          type="text"
          placeholder="Enter the name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
      )}

      <button className="primary" onClick={() => joinGame(2)} disabled={isDisabled}>
        Join Game (1v1)
      </button>
      <button className="primary" onClick={() => joinGame(3)} disabled={isDisabled}>
        Join Game (1v1v1)
      </button>
      <button className="primary" onClick={() => joinGame(4)} disabled={isDisabled}>
        Join Game (2v2)
      </button>

      <button className="secondary" onClick={() => leaveGame()} disabled={!isDisabled}>
        Leave Game
      </button>

      <label>
        <input
          type="checkbox"
          checked={shouldShowPoints}
          onChange={handleCheckboxChange}
        />
        Show Points
      </label>
    </div>

  );
}

export default JoinGamePanel;
