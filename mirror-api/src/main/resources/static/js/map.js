const MAP_LEGEND = document.getElementById("map_legend");
const MAX_CIRCLES = 1000;

/**
 * Gets the hue for a project's color from its name
 * @param {String} project Project name to hash to get hue
 * @returns {Number} Hue for this project
 */
function getHue(project) {
    let hash = 0;
    for (let i = 0, len = project.length; i < len; i++) {
        let chr = project.charCodeAt(i);
        hash = (hash << 5) - hash + chr;
        hash |= 0;
    }
    return hash % 359;
}

/**
 * Renders legend entries
 * @param {Map<String, Number>} legend_entries 
 */
async function renderLegend(legend_entries) {
    legend_html = "";
    legend_entries.forEach((v, k) => {
        legend_html += `<span style="color: hsl(${v}, 100%, 50%)">${k}</span><br>`;
    });
    MAP_LEGEND.innerHTML = legend_html;
}


/* --- On page load --- */
window.onload = async function() {
    const canvas = document.getElementById("canvas");
    const img = document.getElementById("map");
    const ctx = canvas.getContext("2d");

    var circles = [];
    var legend_entries = new Map();
    var socket = new WebSocket((window.location.protocol === "https:" ? "wss://" : "ws://") + window.location.host + "/ws");

    // Define an event listener for the 'open' event
    socket.onopen = function() {
        console.log("Connected to the WebSocket server");
    };
    
    // Define an event listener for the 'message' event
    socket.onmessage = function(event) {
        console.log("Received message: " + event.data);
        var data = event.data.split("\n");

        // Update legend
        var hue = getHue(data[0]);
        legend_entries.set(data[0], hue);
        renderLegend(legend_entries);

        // Add circle
        data[1] = 1 - ((parseFloat(data[1]) + 90) / 180);
        data[2] = (parseFloat(data[2]) + 180) / 360;
        data.push(hue);
        circles.push(data);

        if(circles.length > MAX_CIRCLES) { circles.shift(); }
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