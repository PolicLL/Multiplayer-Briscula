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
            {tournaments.map((tournament) => (
              <li key={tournament.id} className="border p-4 rounded shadow bg-white">
                <div className="mb-1">
                  <strong>{tournament.name}</strong> ({tournament.numberOfPlayers} players,{" "}
                  {tournament.roundsToWin} rounds to win)
                </div>
                <div className="mb-1">Status: {tournament.status}</div>
                <div className="mb-1">
                  Current players: {tournament.currentNumberOfPlayers} / {tournament.numberOfPlayers}
                </div>
                <div className="mb-2">
                  Users joined:{" "}
                  {tournament.userIds && tournament.userIds.length > 0
                    ? tournament.userIds.join(", ")
                    : "No users yet"}
                </div>
                <button
                  onClick={() => onJoin(tournament)}
                  disabled={tournament.currentNumberOfPlayers >= tournament.numberOfPlayers}
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
