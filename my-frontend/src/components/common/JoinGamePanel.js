// JoinGamePanel.js
import React from "react";

function JoinGamePanel({
  name,
  setName,
  shouldShowPoints,
  handleCheckboxChange,
  joinGame,
  isDisabled,
}) {
  return (
    <div>
      {setName && (
        <input
          type="text"
          placeholder="Enter the name"
          value={name}
          onChange={(e) => setName(e.target.value)}
        />
      )}

      <button onClick={() => joinGame(2)} disabled={isDisabled}>
        Join Game (1v1)
      </button>
      <button onClick={() => joinGame(3)} disabled={isDisabled}>
        Join Game (1v1v1)
      </button>
      <button onClick={() => joinGame(4)} disabled={isDisabled}>
        Join Game (2v2)
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
