// Example usage in AdminDashboard.js or TournamentPage.js

import TournamentForm from "../components/tournament/TournamentForm";

function TournamentPage() {
  return (
    <div className="max-w-md mx-auto mt-10">
      <h2 className="text-xl font-bold mb-4">Create a Tournament</h2>
      <TournamentForm
        onSuccess={(data) => alert(`Tournament created: ${data.name}`)}
        onError={(err) => alert("Something went wrong")}
      />
    </div>
  );
}

export default TournamentPage;
