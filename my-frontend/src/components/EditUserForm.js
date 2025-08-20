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
  const [errors, setErrors] = useState({});
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
    <div className="register-container">
      <div className="register-card">
        <h2>Edit Profile</h2>

        {message && <p className="register-message">{message}</p>}

        <form onSubmit={handleSubmit}>
          <input
            type="text"
            placeholder="Username"
            value={formData.username}
            onChange={(e) =>
              setFormData({ ...formData, username: e.target.value })
            }
          />
          {errors.username && <p className="register-error">{errors.username}</p>}

          <input
            type="number"
            placeholder="Age"
            value={formData.age}
            onChange={(e) =>
              setFormData({ ...formData, age: e.target.value })
            }
          />
          {errors.age && <p className="register-error">{errors.age}</p>}

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
          {errors.country && <p className="register-error">{errors.country}</p>}

          <input
            type="email"
            placeholder="Email"
            value={formData.email}
            onChange={(e) =>
              setFormData({ ...formData, email: e.target.value })
            }
          />
          {errors.email && <p className="register-error">{errors.email}</p>}

          <div className="file-upload">
            <input
              type="file"
              id="photoUpload"
              accept="image/*"
              onChange={(e) => setFile(e.target.files[0])}
            />
            <label htmlFor="photoUpload" className="file-upload-label">
              {file ? "Change Photo" : "Choose Photo"}
            </label>
            {file && <p className="file-name">{file.name}</p>}
          </div>
          {errors.photo && <p className="register-error">{errors.photo}</p>}

          <button className="button button-primary" type="submit">
            Update Profile
          </button>
          <button
            className="button button-secondary"
            type="button"
            onClick={onCancel}
            style={{ marginTop: "10px" }}
          >
            Cancel
          </button>
        </form>
      </div>
    </div>

  );
}

export default EditUserForm;
