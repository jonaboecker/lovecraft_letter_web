$('#submit-playerName-btn').on('click', function() {
    var csrfToken = getCsrfToken();
    var playerName = $('#playerName').val();
    console.log(playerName);
    $.ajax({
        url: '/init/playerName',
        type: 'POST',
        headers: {
            "Csrf-Token": csrfToken,
            "Content-Type": "application/json"
          },
        data: JSON.stringify({ playerName: playerName }),
        success: function(data) {
            console.log(data);
            window.location.replace("/gameBoard");
        }
    });
});
