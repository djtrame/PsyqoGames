package com.example.psyqogames.Blackjack

enum class RoundResult {
    BLACKJACK, WIN, LOSE, PUSH
}
enum class PlayerChoice {
    HIT, STAND, DOUBLE_DOWN, SPLIT
}

class PlayerRound(private var _startingBet: Int, private val _turnNumber: Int = 0, private val _player: Player) {

    //consider adding a Player object to this class..
    lateinit var _roundResult: RoundResult
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

    init {

    }

    fun getRoundResult(): RoundResult {
        return _roundResult
    }

    fun getPlayerRoundStateAsString(): String {
        var playerRoundState: String = ""

        playerRoundState += "Player Type: " + _player.playerType + "\n"
        playerRoundState += "Turn Number: " + turnNumber + "\n"
        playerRoundState += "Starting Bet: " + startingBet + "\n"


        //if (::listChoices.isInitialized && listChoices.count() > 0) {
        if (listChoices.count() > 0) {
            for (choice in listChoices) {
                playerRoundState += "Player Choice: " + choice + "\n"
            }
        }
        playerRoundState += "Ending Bet: " + endingBet + "\n"

        if (::_roundResult.isInitialized) {
            playerRoundState += "Round Result: " + _roundResult + "\n"
        } else {
            playerRoundState += "Round Result: Not yet determined\n"
        }


        return playerRoundState
    }
}