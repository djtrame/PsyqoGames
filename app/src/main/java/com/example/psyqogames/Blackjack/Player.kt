package com.example.psyqogames.Blackjack
import com.example.psyqogames.Blackjack.Card

enum class PlayerType {
    DEALER, HUMAN, COMPUTER
}

class Player(private val _playerType: PlayerType) {

    val playerType: PlayerType
        get() = _playerType

    var hand: MutableList<Card> = mutableListOf()

    init{

    }

    fun addCard(card: Card) {
        hand.add(card)
    }

    fun getHandSoftValue(): Int {
        var softValue: Int = 0

        if (hand.count() > 0) {
            for (card in hand) {
                if (card.rank == Rank.ACE) {
                    softValue = softValue + 1
                }
                else {
                    softValue = softValue + card.rank.value
                }
            }
        } else {
            softValue = 0
        }

        return softValue
    }

    fun getHandHardValue(): Int {
        var hardValue: Int = 0

        if (hand.count() > 0) {
            for (card in hand) {
                hardValue = hardValue + card.rank.value
            }
        } else {
            hardValue = 0
        }

        return hardValue
    }



}