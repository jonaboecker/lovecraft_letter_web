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


function turnOverOverwiewCard() {
    //console.log("turnOverOverwiewCard()");
    let card = document.getElementById("overview_img");
    if(card.src.match("assets/images/cards/Overview-mad.jpg")) {
        card.src = "assets/images/cards/Overview.jpg";
    } else {
        card.src = "assets/images/cards/Overview-mad.jpg";
    }
};

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

function playCard(card_id) {
    //console.log("playCard("+card_id+")");
    window.location.replace("/turn/red/playCard/"+card_id);
}