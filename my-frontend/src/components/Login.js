import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { handleAxiosError } from "../utils/handleAxiosError";

function Login() {
  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });

  const [errors, setErrors] = useState({});
  const [message, setMessage] = useState("");
  const navigate = useNavigate();

  const validateForm = () => {
    let tempErrors = {};

    if (!formData.username.trim())
      tempErrors.username = "Username is required.";
    if (!formData.password.trim())
      tempErrors.password = "Password is required.";

    setErrors(tempErrors);

    return Object.keys(tempErrors).length === 0;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!validateForm()) return;

    try {
      const response = await axios.post(
        "http://localhost:8080/api/users/login",
        formData
      );

      const token = response.data;

      sessionStorage.setItem("jwtToken", token);
      sessionStorage.setItem("username", formData.username);
      setMessage("Login successful!");

      window.location.href = "/dashboard";
    } catch (error) {
      console.log("Error : " + error);
      handleAxiosError(error, setMessage);
    }
  };

  useEffect(() => {
    if (sessionStorage.getItem("isRegistered")) {
      return navigate("/");
    }
    return () => {};
  }, []);

  return (
    <div style={{ textAlign: "center", marginTop: "50px" }}>
      <h2>Login</h2>

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

        <button type="submit">LogIn</button>
      </form>
    </div>
  );
}

export default Login;
