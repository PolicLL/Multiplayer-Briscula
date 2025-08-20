import { useEffect, useState } from "react";
import axios from "axios";

export default function TournamentList({
  tournaments,
  setTournaments,
  onJoin,
}) {
  if (!tournaments) return <p>Loading tournaments...</p>;

  return (
    <div className="form-input-container">
      <div className="register-card">
        <h3 className="text-xl font-semibold mb-4">Available Tournaments</h3>

        {tournaments.length === 0 ? (
          <p className="register-message">No tournaments available.</p>
        ) : (
          <ul className="space-y-4">
            {tournaments.map((t) => (
              <li key={t.id} className="border p-4 rounded shadow bg-white">
                <div className="mb-1">
                  <strong>{t.name}</strong> ({t.numberOfPlayers} players,{" "}
                  {t.roundsToWin} rounds to win)
                </div>
                <div className="mb-1">Status: {t.status}</div>
                <div className="mb-1">
                  Current players: {t.currentNumberOfPlayers} / {t.numberOfPlayers}
                </div>
                <div className="mb-2">
                  Users joined:{" "}
                  {t.userIds && t.userIds.length > 0
                    ? t.userIds.join(", ")
                    : "No users yet"}
                </div>
                <button
                  onClick={() => onJoin(t)}
                  disabled={t.currentNumberOfPlayers >= t.numberOfPlayers}
                  className="button button-primary"
                >
                  Join
                </button>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>

  );
}
