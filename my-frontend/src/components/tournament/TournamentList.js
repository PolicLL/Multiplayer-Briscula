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

  return (
    <div>
      <h3 className="text-xl font-semibold mb-2">Available Tournaments</h3>
      {error && <p className="text-red-500">{error}</p>}
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
              <button
                className="mt-2 bg-blue-600 text-white px-3 py-1 rounded"
                onClick={() => onJoin?.(t)}
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
