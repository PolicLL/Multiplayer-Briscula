// src/components/tournament/TournamentForm.js

import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { handleAxiosError } from "../../utils/handleAxiosError";

const initialForm = {
  name: "",
  numberOfPlayers: 4,
  numberOfBots: 0,
  roundsToWin: 1,
};

const TournamentForm = ({ onSuccess, onError }) => {
  const [form, setForm] = useState(initialForm);
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;
    const val =
      name === "numberOfPlayers" || name === "roundsToWin"
        ? parseInt(value)
        : value;
    setForm((prev) => ({ ...prev, [name]: val }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await axios.post(
        "http://localhost:8080/api/tournament",
        form
      );
      onSuccess?.(response.data);
      setForm(initialForm);
      navigate("/dashboard");
    } catch (err) {
      handleAxiosError(err, setMessage);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <form onSubmit={handleSubmit} className="space-y-4">
        <div>
          <label>Name:</label>
          <input
            name="name"
            value={form.name}
            onChange={handleChange}
            required
            className="border p-1 w-full"
          />
        </div>

        <div>
          <label>Number of Players:</label>
          <select
            name="numberOfPlayers"
            value={form.numberOfPlayers}
            onChange={handleChange}
            className="border p-1 w-full"
          >
            {[2, 4, 8, 16, 32].map((n) => (
              <option key={n} value={n}>
                {n}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label>Number of Bots:</label>
          <input
            name="numberOfBots"
            value={form.numberOfBots}
            onChange={handleChange}
            required
            className="border p-1 w-full"
          />
        </div>

        <div>
          <label>Rounds to Win:</label>
          <select
            name="roundsToWin"
            value={form.roundsToWin}
            onChange={handleChange}
            className="border p-1 w-full"
          >
            {[1, 2, 3, 4].map((r) => (
              <option key={r} value={r}>
                {r}
              </option>
            ))}
          </select>
        </div>

        <button
          type="submit"
          disabled={loading}
          className="bg-blue-500 text-white px-4 py-2 rounded"
        >
          {loading ? "Creating..." : "Create Tournament"}
        </button>
      </form>

      {message && <p>{message}</p>}
    </div>
  );
};

export default TournamentForm;
