import React, { useState, useEffect } from "react";
import { getNames } from "country-list";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { handleAxiosError } from "../utils/handleAxiosError";

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
    if (!file) return;

    if (file.size > 1 * 1024 * 1024) {
      setMessage("File size must be less than 5 MB.");
      return;
    }


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
      handleAxiosError(error, setMessage);
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

      sessionStorage.setItem("jwtToken", token);
      sessionStorage.setItem("username", formData.username);
      setMessage("Login successful!");

      navigate("/dashboard");
    } catch (error) {
      handleAxiosError(error, setMessage);
    }
  };

  useEffect(() => {
    if (sessionStorage.getItem("isRegistered")) {
      return navigate("/");
    }
    return () => { };
  }, []);

  /* TODO: Add some animation while registration is being processed */
  /* TODO: Add limit for image size */

  return (
    <div className="form-input-container">
      <div className="register-card">
        <h2>Registration</h2>

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
            type="password"
            placeholder="Password"
            value={formData.password}
            onChange={(e) =>
              setFormData({ ...formData, password: e.target.value })
            }
          />
          {errors.password && <p className="register-error">{errors.password}</p>}

          <input
            type="password"
            placeholder="Confirm Password"
            value={formData.confirmPassword}
            onChange={(e) =>
              setFormData({ ...formData, confirmPassword: e.target.value })
            }
          />
          {errors.confirmPassword && (
            <p className="register-error">{errors.confirmPassword}</p>
          )}

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
              onChange={(e) => {
                const selectedFile = e.target.files[0];
                if (selectedFile) {
                  if (selectedFile.size > 1 * 1024 * 1024) {
                    setMessage("You have choosen an image with size bigger than 1 MB.");
                    setFile(null);
                    e.target.value = "";
                    return;
                  }
                  setFile(selectedFile);
                  setMessage(""); // clear previous errors
                }
              }}
            />
            <label htmlFor="photoUpload" className="file-upload-label">
              {file ? "Change Photo" : "Choose Photo"}
            </label>
            {file && <p className="file-name">{file.name}</p>}
              
            {message && <p className="error-text">{message}</p>}
          </div>

          {errors.photo && <p className="register-error">{errors.photo}</p>}

          <button className="button button-primary" type="submit">
            Create User
          </button>
        </form>
      </div>
    </div>
  );
}

export default UserForm;
