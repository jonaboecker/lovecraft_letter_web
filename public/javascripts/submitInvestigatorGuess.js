async function submitInvestigatorGuess(guess) {
    //console.log(guess);
    var csrfToken = getCsrfToken();
    await $.ajax({
        url: '/turn/getInvestigatorGuess',
        type: 'POST',
        headers: {
            "Csrf-Token": csrfToken,
            "Content-Type": "application/json"
        },
        data: JSON.stringify({ guess: guess }),
        success: function(data) {
            console.log(data);
            location.reload();
        }
});
};