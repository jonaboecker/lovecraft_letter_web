function connectWebSocket() {
    var websocket = new WebSocket("ws://localhost:9000/websocket");
    websocket.setTimeout

    websocket.onopen = function(event) {
        console.log("Connected to Websocket");
        // Send a ping every 50 seconds to keep the connection open
        setInterval(function() {
            websocket.send('ping');
        }, 50000);
    }

    websocket.onclose = function () {
        console.log('Connection with Websocket Closed!');
    };

    websocket.onerror = function (error) {
        console.log('Error in Websocket Occured: ' + error);
    };

    websocket.onmessage = function (e) {
        console.log('Message from Websocket: ' + e.data);
        let json = JSON.parse(e.data);
        data = json.player;
        var string = document.getElementById("firstLine").innerText;
        //console.log(string);
        let number = string.match(/[1-6]/)[0];
        //console.log(number);
        //console.log(Number(number) !== (data + 1));
        if(Number(number) !== (data + 1)) {
            location.reload();
            //console.log("reload");
        }
        /* if (typeof e.data === "string") {
            let json = JSON.parse(e.data);
            let cells = json.grid.cells;
            grid.fill(cells);
            updateGrid(grid);
            registerClickListener();
        } */

    };
}