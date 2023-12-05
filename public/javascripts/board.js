$( document ).ready(function() {
    console.log( "Document is ready, initializing..." );
    // init Übersichtskarte und Übersichtskarte verstecken/ anzeigen Button im Menü
    document.getElementById("menu_item_overviewCard").hidden = false;
    if(localStorage.getItem("overview_card") === "false") {
        document.getElementById("overview_card").hidden = true;
        document.getElementById("menu_item_overviewCard_text").textContent = "Übersichtskarte anzeigen";
    } else if(localStorage.getItem("overview_card") === "true") {
        document.getElementById("overview_card").hidden = false;
        document.getElementById("menu_item_overviewCard_text").textContent = "Übersichtskarte ausblenden";
    } else {
        localStorage.setItem("overview_card", "true");
        document.getElementById("overview_card").hidden = false;
        document.getElementById("menu_item_overviewCard_text").textContent = "Übersichtskarte ausblenden";
    }

    // disable cards if not your turn
    var path = window.location.pathname.split('/'); // Gibt "/gameBoard/2" zurück
    var player = path[path.length - 1]; // Nimmt das letzte Element des Arrays
    console.log(player); // Gibt "2" aus
    $.ajax({
        type: "GET",
        url: "/getCurrentPlayer",
        dataType: "json"
    }).done(function(result){
        console.log(result)
        if(result.player.currentPlayer != player - 1) {
            document.getElementById("card1").hidden = true;
            document.getElementById("card2").hidden = true;
            document.getElementById("standardInput").hidden = true;
        } else {
            document.getElementById("card1").hidden = false;
            document.getElementById("card2").hidden = false;
            document.getElementById("standardInput").hidden = false;
        }
        //set player
        document.getElementById("currentPlayer").value = result.player.currentPlayer;
       }
    )

    //loadJson();
    //init Websocket
    connectWebSocket()
});



// Übersichtskarte drehen
function turnOverOverwiewCard() {
    //console.log("turnOverOverwiewCard()");
    let card = document.getElementById("overview_img");
    if(card.src.match("../assets/images/cards/Overview-mad.jpg")) {
        card.src = "../assets/images/cards/Overview.jpg";
    } else {
        card.src = "../assets/images/cards/Overview-mad.jpg";
    }
};

// Übersichtskarte verstecken/ anzeigen
function toggleShowOverwiewCard() {
    if(localStorage.getItem("overview_card") === "true") {
        localStorage.setItem("overview_card", "false");
        document.getElementById("overview_card").hidden = true;
        document.getElementById("menu_item_overviewCard_text").textContent = "Übersichtskarte anzeigen";
    } else {    
        localStorage.setItem("overview_card", "true");
        document.getElementById("overview_card").hidden = false;
        document.getElementById("menu_item_overviewCard_text").textContent = "Übersichtskarte ausblenden";
    }
}

// Karte ausspielen
function playCard(card_id) {
    //console.log("playCard("+card_id+")");
    var path = window.location.pathname.split('/'); // Gibt "/gameBoard/2" zurück
    var player = path[path.length - 1]; // Nimmt das letzte Element des Arrays
    window.location.replace("/turn/red/playCard/" + card_id + "/" + player);
}