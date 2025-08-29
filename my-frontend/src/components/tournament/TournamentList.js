export default function TournamentList({
  tournaments,
  setTournaments,
  onJoin,
}) {
  if (!tournaments) return <p>Loading tournaments...</p>;

  return (
    <div className="tournament-table-container">
        <h3 className="text-xl font-semibold mb-4">Available Tournaments</h3>

        {tournaments.length === 0 ? (
          <p className="register-message">No tournaments available.</p>
        ) : (
          <div className="table-container">
            <table className="tournament-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Players</th>
                  <th>Rounds to Win</th>
                  <th>Status</th>
                  <th>Current Players</th>
                  <th>Users Joined</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {tournaments.map((tournament) => (
                  <tr key={tournament.id}>
                    <td>{tournament.name}</td>
                    <td>{tournament.numberOfPlayers}</td>
                    <td>{tournament.roundsToWin}</td>
                    <td>{tournament.status}</td>
                    <td>
                      {tournament.currentNumberOfPlayers} / {tournament.numberOfPlayers}
                    </td>
                    <td>
                      {tournament.userIds && tournament.userIds.length > 0
                        ? tournament.userIds.join(", ")
                        : "No users yet"}
                    </td>
                    <td>
                      <button
                        onClick={() => onJoin(tournament)}
                        disabled={tournament.currentNumberOfPlayers >= tournament.numberOfPlayers}
                        className="button button-primary"
                      >
                        Join
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
  );
}
