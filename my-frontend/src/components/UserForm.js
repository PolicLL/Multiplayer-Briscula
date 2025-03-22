import React, { useState } from "react";
import axios from "axios";

function UserForm() {
  const [formData, setFormData] = useState({
    username: "",
    age: "",
    country: "",
    email: "",
    points: 0,
    level: 1,
  });

  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState("");

  const validateForm = () => {
    let tempErrors = {};

    if (!formData.username.trim())
      tempErrors.username = "Username is required.";
    else if (formData.username.length < 3)
      tempErrors.username = "Username should be at least 3 characters long.";

    if (!formData.age) tempErrors.age = "Age is required.";
    if (!formData.country) tempErrors.country = "Country is required.";
    if (!formData.email) tempErrors.email = "Email is required.";

    let isEmailInvalid = !/\S+@\S+\.\S+/.test(formData.email);
    if (isEmailInvalid) tempErrors.email = "Email is invalid.";

    setErrors(tempErrors);

    return Object.keys(tempErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      const response = await axios.post(
        "http://localhost:8080/api/users/create",
        formData
      );

      console.log("response: " + response);
      console.log("response.data: " + response.data);
      console.log("response.data.message: " + response.data.message);

      setMessage(response.data.message || response.data);
      setFormData({
        username: "",
        age: "",
        country: "",
        email: "",
        points: 0,
        level: 1,
      });
      setErrors({});
    } catch (error) {
      console.log("Error : " + error);
      setMessage(error.response?.data || "Something went wrong.");
    }
  };

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <h2>Create user</h2>

      {message && <p style={{ color: "green" }}>{message}</p>}

      <form onSubmit={handleSubmit}>
        <input
          type="text"
          placeholder="Username"
          value={formData.username}
          onChange={(e) =>
            setFormData({ ...formData, username: e.target.value })
          }
        />
        <p style={{ color: "red" }}>{errors.username}</p>

        <input
          type="number"
          placeholder="Age"
          value={formData.age}
          onChange={(e) => setFormData({ ...formData, age: e.target.value })}
        />
        <p style={{ color: "red" }}>{errors.age}</p>

        <input
          type="text"
          placeholder="Country"
          value={formData.country}
          onChange={(e) =>
            setFormData({ ...formData, country: e.target.value })
          }
        />
        <p style={{ color: "red" }}>{errors.country}</p>

        <input
          type="email"
          placeholder="Email"
          value={formData.email}
          onChange={(e) => setFormData({ ...formData, email: e.target.value })}
        />
        <p style={{ color: "red" }}>{errors.email}</p>

        <button type="submit">Create user</button>
      </form>
    </div>
  );
}

export default UserForm;
