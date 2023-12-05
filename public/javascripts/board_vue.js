const ConditionalRendering = {
    
    data() {
      return {
        seen: false
      }
    },
    created() {
        let self = this;
        var path = window.location.pathname.split('/'); // Gibt "/gameBoard/2" zurück
        var player = Number(path[path.length - 1]); // Nimmt das letzte Element des Arrays
        console.log("created")
        $.ajax({
            type: "GET",
            url: "/getCurrentPlayer",
            dataType: "json"
        }).done(function(result){
            console.log(result)
            if(result.player.currentPlayer !== player - 1) {
                console.log("false")
                self.seen = false;
            } else {
                console.log("true")
                self.seen = true;
            }
            //set player
            document.getElementById("currentPlayer").value = result.player.currentPlayer;
           }
        )
      },
    methods: {
        toggleSeen() {
            let self = this;
            var path = window.location.pathname.split('/'); // Gibt "/gameBoard/2" zurück
            var player = Number(path[path.length - 1]); // Nimmt das letzte Element des Arrays
            $.ajax({
                type: "GET",
                url: "/getCurrentPlayer",
                dataType: "json"
            }).done(function(result){
                console.log(result)
                if(result.player.currentPlayer !== player - 1) {
                    self.seen = false;
                } else {
                    self.seen = true;
                    // change cards
                    $.ajax({
                        type: "GET",
                        url: "/getCurrentCards",
                        dataType: "json"
                    }).done(function(result){
                        console.log(result)
                        document.getElementById("card1img").src = '../assets/images/cards/Card-' + String(result.cards.card1) + ').jpg';
                        document.getElementById("card2img").src = '../assets/images/cards/Card-' + String(result.cards.card2) + ').jpg';
                        document.getElementById("card3img").src = '../assets/images/cards/Card-' + String(result.cards.card3) + ').jpg';
                       }
                    )
                }
                //set player
                document.getElementById("currentPlayer").value = result.player.currentPlayer;
               }
            )
        }
      }
  }
  
  const boardApp = Vue.createApp(ConditionalRendering).mount('#conditionalrendering');
  