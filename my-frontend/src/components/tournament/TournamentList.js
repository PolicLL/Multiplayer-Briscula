import { useEffect, useState } from "react";
import axios from "axios";

export default function TournamentList({
  tournaments,
  setTournaments,
  onJoin,
}) {
  if (!tournaments) return <p>Loading tournaments...</p>;

  return (
    <div>
      <h3 className="text-xl font-semibold mb-2">Available Tournaments</h3>
      {tournaments.length === 0 ? (
        <p>No tournaments available.</p>
      ) : (
        <ul className="space-y-3">
          {tournaments.map((t) => (
            <li key={t.id} className="border p-3 rounded shadow">
              <div>
                <strong>{t.name}</strong> ({t.numberOfPlayers} players,{" "}
                {t.roundsToWin} rounds to win)
              </div>
              <div>Status: {t.status}</div>
              <div>
                Current players: {t.currentNumberOfPlayers} /{" "}
                {t.numberOfPlayers}
              </div>
              <div>
                Users joined:{" "}
                {t.userIds && t.userIds.length > 0
                  ? t.userIds.join(", ")
                  : "No users yet"}
              </div>
              <button
                onClick={() => onJoin(t)}
                disabled={t.currentNumberOfPlayers >= t.numberOfPlayers}
                className="mt-2 bg-blue-600 text-white px-3 py-1 rounded"
              >
                Join
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}
