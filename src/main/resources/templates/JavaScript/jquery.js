$(document).ready(function() {
    $('#playerAttack').click(function() {
        $.get('http://localhost:4567/battleJson', function(data) {
//            console.log(data)
//            console.log(data.playersArray[0]["health"])
            $('#player_hp').text(data.playersArray[0]["health"]);
            $('#enemy_hp').text(data.playersArray[1]["health"]);
        });
    });
});