package com.example.psyqogames.Blackjack

enum class TableRoundResult {
    COMPLETE, PENDING
}
class TableRound(private val _playerList: List<Player>, private val _turnNumber: Int, var tableRoundResult: TableRoundResult = TableRoundResult.PENDING) {

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
            //clear out of hand of the player
            playerRound.player.hand.clear()
        }
    }

    fun getTableRoundStateAsString(): String {
        var tableRoundState: String = ""
        for (playerRound in playerRounds) {
            tableRoundState += "Player Type: " + playerRound.player.playerType + "\n"
            tableRoundState += "Turn Number: " + turnNumber + "\n"
            tableRoundState += "Starting Bet: " + playerRound.startingBet + "\n"


            //if (::listChoices.isInitialized && listChoices.count() > 0) {
            if (playerRound.listChoices.count() > 0) {
                for (choice in playerRound.listChoices) {
                    tableRoundState += "Player Choice: " + choice + "\n"
                }
            }
            tableRoundState += "Ending Bet: " + playerRound.endingBet + "\n"

            tableRoundState += "Round Result: " + playerRound.playerRoundResult + "\n"

            tableRoundState += "-------------------------\n"

        }



        return tableRoundState
    }






    //hold the total amount bet at the start and end of the round (after splits and double downs)
}