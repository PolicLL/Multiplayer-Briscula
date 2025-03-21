import React, { useState, useEffect } from "react";
import axios from "axios";

function App() {
  const [items, setItems] = useState([]);
  const [receivedMessage, setReceivedMessage] = useState("");

  // Fetch existing items on component mount
  useEffect(() => {
    axios
      .get("http://localhost:8080/api/items")
      .then((response) => setItems(response.data))
      .catch((error) => console.error(error));
  }, []);

  // Function to handle POST request and update items
  const handlePostRequest = () => {
    const newItem = { name: `Item ${items.length + 1}` }; // Example payload
    axios
      .post("http://localhost:8080/api/items", newItem, {
        headers: { "Content-Type": "application/json" },
      })
      .then((response) => {
        console.log("POST response:", response.data);
        setItems((prevItems) => [...prevItems, response.data]); // Add new item to list
        setReceivedMessage(response.data); // Capture response (string) and display it
      })
      .catch((error) => {
        console.error("Error:", error);
        alert("An error occurred.");
      });
  };

  return (
    <div>
      <h1>Items</h1>
      <ul>
        {items.map((item, index) => (
          <li key={index}>{item.name || item}</li>
        ))}
      </ul>
      <button onClick={handlePostRequest}>Add Item</button>

      {/* Display the message received from backend */}
      {receivedMessage && <p>Backend Response: {receivedMessage}</p>}
    </div>
  );
}

export default App;
