import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";

import AnonymousDashboard from "./components/AnonymousDashboard";
import Home from "./components/Home";
import Login from "./components/Login";
import Registration from "./components/Registration";
import Dashboard from "./components/Dashboard";
import GameRoom from "./components/GameRoom";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />}></Route>
        <Route path="/login" element={<Login />}></Route>
        <Route path="/signup" element={<Registration />}></Route>
        <Route path="/dashboard" element={<Dashboard />}></Route>
        <Route path="/anonymous" element={<AnonymousDashboard />}></Route>
        <Route path="/game/:roomId" element={<GameRoom />} />
      </Routes>
    </Router>
  );
}

export default App;
