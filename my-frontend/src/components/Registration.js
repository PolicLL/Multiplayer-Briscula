import React, { useState } from "react";
import { getNames } from "country-list";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const countries = getNames();

function UserForm() {
  const [formData, setFormData] = useState({
    username: "",
    age: "",
    country: "",
    email: "",
    password: "",
    confirmPassword: "",
    points: 0,
    level: 1,
  });

  const navigate = useNavigate();
  const [file, setFile] = useState(null);
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState("");
  const [user, setUser] = useState(null);

  const [loginFormData, setLoginFormData] = useState({
    username: "",
    password: "",
  });

  const validateForm = () => {
    let tempErrors = {};

    if (!formData.username.trim()) {
      tempErrors.username = "Username is required.";
    } else if (formData.username.length < 3) {
      tempErrors.username = "Username should be at least 3 characters long.";
    } else if (formData.username.length > 100) {
      tempErrors.username = "Username must be between 3 and 100 characters.";
    }

    if (!formData.age) {
      tempErrors.age = "Age is required.";
    } else if (formData.age < 3) {
      tempErrors.age = "Age must be at least 3.";
    } else if (formData.age > 100) {
      tempErrors.age = "Age must be no more than 100.";
    }

    if (!formData.country) {
      tempErrors.country = "Country is required.";
    }

    if (!formData.email) {
      tempErrors.email = "Email is required.";
    } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
      tempErrors.email = "Email is invalid.";
    }

    if (!formData.password) {
      tempErrors.password = "Password is required.";
    } else if (formData.password.length < 6) {
      tempErrors.password = "Password must be at least 6 characters long.";
    }

    if (!formData.confirmPassword) {
      tempErrors.confirmPassword = "Password confirmation is required.";
    } else if (formData.password !== formData.confirmPassword) {
      tempErrors.confirmPassword = "Passwords do not match.";
    }

    setErrors(tempErrors);
    return Object.keys(tempErrors).length === 0;
  };

  const uploadPhoto = async (file) => {
    const photoFormData = new FormData();
    photoFormData.append("photo", file);

    try {
      const response = await axios.post(
        "http://localhost:8080/api/photo",
        photoFormData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error("Error uploading photo:", error);
      throw new Error(error.response?.data || "Error uploading photo.");
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    try {
      const formDataToSend = new FormData();

      if (file !== null) {
        const photoId = await uploadPhoto(file);
        formDataToSend.append("photoId", photoId);
      }

      Object.keys(formData).forEach((key) => {
        formDataToSend.append(key, formData[key]);
      });

      const response = await axios.post(
        "http://localhost:8080/api/users/create",
        formDataToSend
      );

      setUser(response.data);
      setMessage("User created successfully.");

      // Reset form
      setFormData({
        username: "",
        age: "",
        country: "",
        email: "",
        password: "",
        confirmPassword: "",
        points: 0,
        level: 1,
      });
      setFile(null);
      setErrors({});

      //

      setLoginFormData();

      const loginResponse = await axios.post(
        "http://localhost:8080/api/users/login",
        formData
      );

      const token = loginResponse.data;

      localStorage.setItem("jwtToken", token);
      localStorage.setItem("username", formData.username);
      setMessage("Login successful!");

      navigate("/dashboard");
    } catch (error) {
      console.error("Error creating user:", error);
      const errorData = error.response?.data;
      const readableError =
        typeof errorData === "string"
          ? errorData
          : errorData?.error || "Something went wrong.";
      setMessage(readableError);
    }
  };

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <h2>Create User</h2>

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
          type="password"
          placeholder="Password"
          value={formData.password}
          onChange={(e) =>
            setFormData({ ...formData, password: e.target.value })
          }
        />
        <p style={{ color: "red" }}>{errors.password}</p>

        <input
          type="password"
          placeholder="Confirm Password"
          value={formData.confirmPassword}
          onChange={(e) =>
            setFormData({ ...formData, confirmPassword: e.target.value })
          }
        />
        <p style={{ color: "red" }}>{errors.confirmPassword}</p>

        <input
          type="number"
          placeholder="Age"
          value={formData.age}
          onChange={(e) => setFormData({ ...formData, age: e.target.value })}
        />
        <p style={{ color: "red" }}>{errors.age}</p>

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
        <p style={{ color: "red" }}>{errors.country}</p>

        <input
          type="email"
          placeholder="Email"
          value={formData.email}
          onChange={(e) => setFormData({ ...formData, email: e.target.value })}
        />
        <p style={{ color: "red" }}>{errors.email}</p>

        <input
          type="file"
          accept="image/*"
          onChange={(e) => {
            const selectedFile = e.target.files[0];
            setFile(selectedFile);
          }}
        />
        <p style={{ color: "red" }}>{errors.photo}</p>

        <button type="submit">Create User</button>
      </form>
    </div>
  );
}

export default UserForm;
