$.ajax({
    type: "GET",
    url: "/getSelectablePlayers/",
    dataType: "json"
}).done(function(result){
   console.log(result)
   for(let i = 1; i <= 6; i++) {
    if(result.player.numbers.includes(i)) {
        continue;
    }
    document.getElementById("item" + i).hidden = true;
   }
})
