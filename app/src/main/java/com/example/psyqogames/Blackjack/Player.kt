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



}