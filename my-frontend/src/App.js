import React, { useState, useEffect } from "react";
import axios from "axios";

function App() {
  const [items, setItems] = useState([]);

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
    fetch("http://localhost:8080/api/items", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(newItem),
    })
      .then((response) => {
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
      })
      .then((data) => {
        console.log("POST response:", data);
        setItems((prevItems) => [...prevItems, data]); // Add new item to the list
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
    </div>
  );
}

export default App;
