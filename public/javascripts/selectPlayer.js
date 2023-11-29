async function submitSelectedPlayer(player) {
        //console.log(player);
        var csrfToken = getCsrfToken();
        await $.ajax({
            url: '/turn/selectPlayer',
            type: 'POST',
            headers: {
                "Csrf-Token": csrfToken,
                "Content-Type": "application/json"
            },
            data: JSON.stringify({ player: player }),
            success: function(data) {
                console.log(data);
                location.reload();
            }
    });
};