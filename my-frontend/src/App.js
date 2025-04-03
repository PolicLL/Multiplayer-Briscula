import React, { useState } from "react";
import {
  BrowserRouter as Router,
  Route,
  Routes,
  useNavigate,
} from "react-router-dom";
import { use } from "react";
import PrepareGame from "./components/PrepareGame";
import Signup from "./components/Signup";
import Home from "./components/Home";
import Login from "./components/Login";
import UserForm from "./components/UserForm";
import Dashboard from "./components/Dashboard";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />}></Route>
        <Route path="/login" element={<Login />}></Route>
        <Route path="/signup" element={<UserForm />}></Route>
        <Route path="/game" element={<PrepareGame />}></Route>
        <Route path="/dashboard" element={<Dashboard />}></Route>
      </Routes>
    </Router>
  );
}

export default App;
