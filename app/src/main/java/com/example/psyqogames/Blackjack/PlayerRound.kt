package com.example.psyqogames.Blackjack

enum class RoundResult {
    BLACKJACK, WIN, LOSE, PUSH, PENDING
}
enum class PlayerChoice {
    HIT, STAND, DOUBLE_DOWN, SPLIT
}

class PlayerRound(private var _startingBet: Int, private val _turnNumber: Int = 0, val player: Player, var roundResult: RoundResult = RoundResult.PENDING) {

    //consider adding a Player object to this class..

    var listChoices = mutableListOf<PlayerChoice>()
    private var _endingBet: Int = 0

    var endingBet: Int
        get() {
            return _endingBet
        }
        set(value) {
            _endingBet = value
        }

    var startingBet: Int
        get() {
            return _startingBet
        }
        set(value) {
            _startingBet = value
        }

    var turnNumber: Int = 0
        get() {
            return _turnNumber
        }

//    var roundResult: RoundResult
//        get() {
//            return _roundResult
//        }
//        set(value) {
//            _roundResult = value
//        }


    init {

    }

//    fun getRoundResult(): RoundResult {
//        return _roundResult
//    }

    fun getPlayerRoundStateAsString(): String {
        var playerRoundState: String = ""

        playerRoundState += "Player Type: " + player.playerType + "\n"
        playerRoundState += "Turn Number: " + turnNumber + "\n"
        playerRoundState += "Starting Bet: " + startingBet + "\n"


        //if (::listChoices.isInitialized && listChoices.count() > 0) {
        if (listChoices.count() > 0) {
            for (choice in listChoices) {
                playerRoundState += "Player Choice: " + choice + "\n"
            }
        }
        playerRoundState += "Ending Bet: " + endingBet + "\n"

        playerRoundState += "Round Result: " + roundResult + "\n"


        return playerRoundState
    }
}