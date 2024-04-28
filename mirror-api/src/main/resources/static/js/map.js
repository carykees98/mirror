/**
 * Gets the hue for a project's color from its name
 * @param {String} project Project name to hash to get hue
 * @returns {Number} Hue for this project
 */
function hue(project) {
    let hash = 0;
    for (let i = 0, len = project.length; i < len; i++) {
        let chr = project.charCodeAt(i);
        hash = (hash << 5) - hash + chr;
        hash |= 0;
    }
    return hash % 359;
}


/* --- On page load --- */
window.onload = async function() {
    const canvas = document.getElementById("canvas");
    const img = document.getElementById("map");
    const ctx = canvas.getContext("2d");

    var circles = []
    var socket = new WebSocket((window.location.protocol === "https:" ? "wss://" : "ws://") + window.location.host + "/ws");

    // Define an event listener for the 'open' event
    socket.onopen = function() {
        console.log("Connected to the WebSocket server");
    };
    
    // Define an event listener for the 'message' event
    socket.onmessage = function(event) {
        console.log("Received message: " + event.data);
        var data = event.data.split("\n");
        //convert lat and long to a number between 0 and 1
        data[1] = 1 - ((parseFloat(data[1]) + 90) / 180);
        data[2] = (parseFloat(data[2]) + 180) / 360;
        data.push(hue(data[0]));
        circles.push(data);
    };

    // Define an event listener for the 'close' event
    socket.onclose = function() {
        console.log("Connection closed");
    };

    // Define an event listener for the 'error' event
    socket.onerror = function(error) {
        console.log("Error occurred: " + error);
    };

    // Event listener for window size change
    window.onresize = function(){
        canvas.width = window.innerWidth;
        canvas.height = window.innerHeight - document.getElementById("header").clientHeight;
    };

    // We call it once manually when the page first loads
    window.onresize();

    // Draw loop
    setInterval(() => {
        ctx.drawImage(img, 0, 0, canvas.width, canvas.height);

        circles.forEach((circle) => {
            ctx.fillStyle = `hsl(${circle[3]}, 100%, 50%)`;
            
            // Draw the circle
            ctx.arc(
                circle[2] * canvas.width,
                circle[1] * canvas.height,
                2.0,
                0,
                2 * Math.PI,
                false
            );
            ctx.closePath();
            ctx.fill();
        })

    }, 100); // Draw every 100 ms
}