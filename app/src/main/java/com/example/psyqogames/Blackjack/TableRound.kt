package com.example.psyqogames.Blackjack

class TableRound(private val _playerList: List<Player>, private val _turnNumber: Int) {

    var turnNumber: Int = 0
        get() {
            return _turnNumber
        }

    val playerList: List<Player>
        get() {
            return _playerList
        }

    var playerRounds = mutableListOf<PlayerRound>()

//    val playerRounds: List<PlayerRound>
//        get() {
//            return _playerRounds
//        }


    init {
        //create rounds for the players and the dealer
        for (player in _playerList) {
            var playerRound = PlayerRound(0, _turnNumber, player)
            playerRounds.add(playerRound)
        }
    }






    //hold the total amount bet at the start and end of the round (after splits and double downs)
}