export function parseWebSocketMessage(message) {
  try {
    const parsed = JSON.parse(message);
    return {
      type: parsed.type,
      roomId: parsed.roomId,
      playerId: parsed.playerId,
      content: parsed.content,
    };
  } catch (error) {
    console.error("Invalid JSON.", error);
    return null;
  }
}
