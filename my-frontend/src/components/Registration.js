import React, { useState } from "react";
import { getNames } from "country-list";
import axios from "axios";

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
    photo: null,
  });

  const [file, setFile] = useState(null);
  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState("");
  const [user, setUser] = useState(null);

  const validateForm = () => {
    let tempErrors = {};

    // username
    if (!formData.username.trim())
      tempErrors.username = "Username is required.";
    else if (formData.username.length < 3)
      tempErrors.username = "Username should be at least 3 characters long.";
    else if (formData.username.length > 100)
      tempErrors.username = "Username must be between 3 and 100 characters.";

    // age
    if (!formData.age) tempErrors.age = "Age is required.";
    else if (formData.age < 3) tempErrors.age = "Age must be at least 3.";
    else if (formData.age > 100)
      tempErrors.age = "Age must be no more than 100.";

    // country
    if (!formData.country) tempErrors.country = "Country is required.";

    // email
    if (!formData.email) tempErrors.email = "Email is required.";
    let isEmailInvalid = !/\S+@\S+\.\S+/.test(formData.email);
    if (isEmailInvalid) tempErrors.email = "Email is invalid.";

    // password
    if (!formData.password) tempErrors.password = "Password is required.";
    else if (formData.password.length < 6)
      tempErrors.password = "Password must be at least 6 characters long.";

    if (!formData.confirmPassword)
      tempErrors.confirmPassword =
        "Filed for password confirmation is required.";
    else if (formData.password !== formData.confirmPassword)
      tempErrors.confirmPassword = "Passwords are not the same.";

    setErrors(tempErrors);

    return Object.keys(tempErrors).length === 0;
  };

  const uploadPhoto = async (photo) => {
    const formData = new FormData();
    formData.append("file", photo); // 'file' should match your backend param name

    // Let the browser set Content-Type automatically
    const response = await fetch("/api/photo", {
      method: "POST",
      body: formData, // The FormData will automatically include 'multipart/form-data'
    });

    if (!response.ok) {
      throw new Error("Upload failed");
    }

    const id = await response.text(); // Assuming the backend sends back an ID as plain text
    return id;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    const formDataToSend = new FormData();
    formDataToSend.append("file", file);
    formDataToSend.append("name", formData.username); // or any other field for the name

    const photoId = uploadPhoto(file);
    formDataToSend.append("photo", photoId);

    // Append other form data
    Object.keys(formData).forEach((key) => {
      formDataToSend.append(key, formData[key]);
    });

    try {
      const response = await axios.post(
        "http://localhost:8080/api/users/create",
        formDataToSend,
        {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        }
      );

      console.log("response: " + response);
      console.log("response.data: " + response.data);

      setUser(response.data);
      setMessage("User created successfully.");

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
      setErrors({});
      setFile(null); // Reset file input
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
          onChange={(e) =>
            setFormData({ ...formData, photo: e.target.files[0] })
          }
        />
        <p style={{ color: "red" }}>{errors.photo}</p>

        <button type="submit">Create user</button>
      </form>
    </div>
  );
}

export default UserForm;
