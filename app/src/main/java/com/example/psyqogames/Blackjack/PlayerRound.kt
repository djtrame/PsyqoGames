package com.example.psyqogames.Blackjack

enum class RoundResult {
    BLACKJACK, WIN, LOSE, PUSH
}
enum class PlayerChoice {
    HIT, STAND, DOUBLE_DOWN, SPLIT
}

class PlayerRound(private var _startingBet: Int, private val _turnNumber: Int = 0, private val _playerType: PlayerType) {

    //consider adding a Player object to this class..
    lateinit var _roundResult: RoundResult
    lateinit var _listChoices: List<PlayerChoice>
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

    fun getRoundResult(): RoundResult {
        return _roundResult
    }
}