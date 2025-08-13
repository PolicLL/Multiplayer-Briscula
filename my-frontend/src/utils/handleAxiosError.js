// utils/handleAxiosError.js
export function handleAxiosError(error, setMessage) {
  if (error.response) {
    console.error("API Error:", error.response.data);
    setMessage(error.response.data.message || "An unexpected error occurred");
  } else if (error.request) {
    console.error("No response received:", error.request);
    setMessage("Server did not respond");
  } else {
    console.error("Request setup error:", error.message);
    setMessage("An error occurred");
  }
}