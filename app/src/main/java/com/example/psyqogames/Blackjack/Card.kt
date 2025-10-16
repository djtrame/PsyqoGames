package com.example.psyqogames.Blackjack

enum class Suit {
    HEARTS, DIAMONDS, CLUBS, SPADES
}

enum class Rank(val value: Int) {
    ACE(11), // Ace can be 1 or 11, we'll handle that logic elsewhere
    TWO(2), THREE(3), FOUR(4), FIVE(5), SIX(6), SEVEN(7), EIGHT(8), NINE(9), TEN(10), JACK(10), QUEEN(10), KING(10)
}

data class Card(val suit: Suit, val rank: Rank)