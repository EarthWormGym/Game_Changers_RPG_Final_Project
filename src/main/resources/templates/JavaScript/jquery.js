$(document).ready(function() {
    $('#playerAttack').click(function() {
        $.get('https://secret-harbor-57457.herokuapp.com//battleJson', function(data) {
//                $('#player_hp').text(data.playersArray[0]["health"]);
                $('#enemy_hp').text(data.playersArray[1]["health"]);
        });
    });
});