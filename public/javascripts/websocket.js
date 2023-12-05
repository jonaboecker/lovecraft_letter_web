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
        var number = Number(document.getElementById("currentPlayer").value) + 1;
        if(number !== (data + 1)) {
            //location.reload();
            boardApp.toggleSeen();
            $.ajax({
                type: "GET",
                url: "/getFirstLineOfBoard",
                dataType: "json"
            }).done(function(result){
                    console.log(result)
                    document.getElementById("firstLine").innerHTML = result.firstLine;
                }
            )
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