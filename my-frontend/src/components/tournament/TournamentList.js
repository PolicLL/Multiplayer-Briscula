import { useEffect, useState } from "react";
import axios from "axios";

export default function TournamentList({ onJoin }) {
  const [tournaments, setTournaments] = useState([]);
  const [error, setError] = useState(null);

  const fetchTournaments = async () => {
    try {
      const response = await axios.get("http://localhost:8080/api/tournament");
      setTournaments(response.data);
    } catch (err) {
      console.error("Failed to fetch tournaments:", err);
      setError("Failed to load tournaments.");
    }
  };

  useEffect(() => {
    fetchTournaments();
  }, []);

  const handleJoin = async (tournament) => {
    if (!onJoin) return;

    const updated = await onJoin(tournament);
    if (!updated) return;

    setTournaments((prev) =>
      prev.map((t) => (t.id === updated.id ? updated : t))
    );
  };

  return (
    <div>
      <h3 className="text-xl font-semibold mb-2">Available Tournaments</h3>
      {error && <p className="text-red-500">{error}</p>}
      {tournaments.length === 0 ? (
        <p>No tournaments available.</p>
      ) : (
        <ul className="space-y-3">
          {tournaments.map((tournament) => (
            <li key={tournament.id} className="border p-3 rounded shadow">
              <div>
                <strong>{tournament.name}</strong> ({tournament.numberOfPlayers}{" "}
                players, {tournament.roundsToWin} rounds to win)
              </div>
              <div>Status: {tournament.status}</div>
              <div>
                Current players: {tournament.currentNumberOfPlayers} /
                {tournament.numberOfPlayers}
              </div>
              <button
                className="mt-2 bg-blue-600 text-white px-3 py-1 rounded"
                onClick={() => handleJoin(tournament)}
                disabled={
                  tournament.currentNumberOfPlayers >=
                  tournament.numberOfPlayers
                }
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
