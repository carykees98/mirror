
/* --- On page load --- */
var socket = new WebSocket("/ws");

// Define an event listener for the 'open' event
socket.onopen = function() {
    console.log("Connected to the WebSocket server");
};
  
// Define an event listener for the 'message' event
socket.onmessage = function(event) {
    console.log("Received message: " + event.data);
};

// Define an event listener for the 'close' event
socket.onclose = function() {
    console.log("Connection closed");
};

// Define an event listener for the 'error' event
socket.onerror = function(error) {
    console.log("Error occurred: " + error);
};