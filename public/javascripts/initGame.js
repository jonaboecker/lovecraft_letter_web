function setPlayerAmount(playerAmount) {
    document.getElementById("playerAmount").value = playerAmount;
    for(let i = 1; i <= 6; i++) {
        document.getElementById("player" + i + " input").hidden = false;
        if(i > playerAmount) {
            document.getElementById("player" + i + " input").hidden = true;
        }
    }
}

$('#submit-start-game-btn').on('click', async function() {
    console.log("Spiel wird gestartet...");
    var csrfToken = getCsrfToken();
    await $.ajax({
        url: '/init/initNewGame',
        type: 'GET',
        headers: {
            "Content-Type": "application/json"
          },
        success: function(data) {
            console.log(data);
        }
    });
    
    var playerAmount = $('#playerAmount').val();
    //console.log(playerAmount);
    await $.ajax({
        url: '/init/playerAmount',
        type: 'POST',
        headers: {
            "Csrf-Token": csrfToken,
            "Content-Type": "application/json"
          },
        data: JSON.stringify({ playerAmount: playerAmount }),
        success: function(data) {
            console.log(data);
        }
    });
    for (let i = 1; i <= playerAmount; i++) {
        var playerName = $('#player' + i + '-Name').val();
        //console.log(playerName);
        await $.ajax({
            url: '/init/playerName',
            type: 'POST',
            headers: {
                "Csrf-Token": csrfToken,
                "Content-Type": "application/json"
              },
            data: JSON.stringify({ playerName: playerName }),
            success: function(data) {
                console.log(data);
            }
        });
    }
    console.log("Spiel gestartet");
    window.location.replace("/gameStartedPage/" + playerAmount);
});
