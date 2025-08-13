import React, { useState } from "react";
import axios from "axios";
import { getNames } from "country-list";
import { handleAxiosError } from "../utils/handleAxiosError";

const countries = getNames();

function EditUserForm({ user, onCancel, onUpdate }) {
  const [formData, setFormData] = useState({
    username: user.username,
    age: user.age,
    country: user.country,
    email: user.email,
    points: user.points || 0,
    level: user.level || 1,
  });

  const [file, setFile] = useState(null);
  const [message, setMessage] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      let photoId = user.photoId;

      if (file) {
        const photoFormData = new FormData();
        photoFormData.append("photo", file);
        const response = await axios.post(
          "http://localhost:8080/api/photo",
          photoFormData,
          {
            headers: { "Content-Type": "multipart/form-data" },
          }
        );
        photoId = response.data;
      }

      const updatedUser = {
        ...formData,
        photoId,
      };

      const token = sessionStorage.getItem("jwtToken");

      if (!token) {
        setMessage("Please log in first.");
        return;
      }

      const response = await axios.put(
        `http://localhost:8080/api/users/${user.id}`,
        updatedUser,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      setMessage("User updated successfully.");
      onUpdate(response.data);
    } catch (error) {
      handleAxiosError(error, setMessage);
    }
  };

  return (
    <div style={{ textAlign: "center", marginTop: "30px" }}>
      <h3>Edit Profile</h3>
      {message && <p style={{ color: "green" }}>{message}</p>}
      <form onSubmit={handleSubmit}>
        <input
          type="text"
          value={formData.username}
          onChange={(e) =>
            setFormData({ ...formData, username: e.target.value })
          }
        />
        <input
          type="number"
          value={formData.age}
          onChange={(e) => setFormData({ ...formData, age: e.target.value })}
        />
        <select
          value={formData.country}
          onChange={(e) =>
            setFormData({ ...formData, country: e.target.value })
          }
        >
          <option value="">Select a country</option>
          {countries.map((country) => (
            <option key={country} value={country}>
              {country}
            </option>
          ))}
        </select>
        <input
          type="email"
          value={formData.email}
          onChange={(e) => setFormData({ ...formData, email: e.target.value })}
        />
        <input
          type="file"
          accept="image/*"
          onChange={(e) => setFile(e.target.files[0])}
        />

        <button type="submit">Update</button>
        <button type="button" onClick={onCancel}>
          Cancel
        </button>
      </form>
    </div>
  );
}

export default EditUserForm;
