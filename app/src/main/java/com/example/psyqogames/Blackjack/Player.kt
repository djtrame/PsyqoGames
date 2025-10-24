package com.example.psyqogames.Blackjack
import com.example.psyqogames.Blackjack.Card

enum class PlayerType {
    DEALER, HUMAN, COMPUTER
}

class Player(private val _playerType: PlayerType, var bankRoll: Int) {

    val playerType: PlayerType
        get() = _playerType

    var hand: MutableList<Card> = mutableListOf()

    init{

    }

    fun addCard(card: Card) {
        hand.add(card)
    }

    // New approach for soft value calculation
    fun getHandSoftValue(): Int {
        var softValue = 0
        var aceCount = 0

        for (card in hand) {
            when (card.rank) {
                Rank.ACE -> {
                    aceCount++
                    softValue += 11 // Initially count Ace as 11
                }
                else -> {
                    softValue += card.rank.value // Sum value for non-Ace cards
                }
            }
        }

        // If the soft value busts (is over 21) AND we have Aces,
        // we need to change an Ace from 11 to 1 to make it a valid "soft" hand.
        while (softValue > 21 && aceCount > 0) {
            softValue -= 10 // Change an Ace from 11 to 1 (11 - 10 = 1)
            aceCount--
        }
        return softValue
    }

    //initial version, forgot that multiple aces can be either 1 or 11
//    fun getHandSoftValue(): Int {
//        var softValue: Int = 0
//
//        if (hand.count() > 0) {
//            for (card in hand) {
//                if (card.rank == Rank.ACE) {
//                    softValue = softValue + 1
//                }
//                else {
//                    softValue = softValue + card.rank.value
//                }
//            }
//        } else {
//            softValue = 0
//        }
//
//        return softValue
//    }

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

    fun getBestHandValue(): Int {
        var hardValue: Int = getHandHardValue()
        var softValue: Int = getHandSoftValue()

        if (hardValue > 21) {
            return softValue
        } else {
            return hardValue
        }
    }



}