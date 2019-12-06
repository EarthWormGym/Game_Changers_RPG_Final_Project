$(document).ready(function() {
    $('#playerAttack').click(function() {
        $.get('http://localhost:4567/battleJson', function(data) {
//                $('#player_hp').text(data.playersArray[0]["health"]);
                $('#enemy_hp').text(data.playersArray[1]["health"]);
        });
    });
});