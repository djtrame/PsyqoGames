package com.example.psyqogames.Blackjack

class Deck {
    private val cards = mutableListOf<Card>()

    init {
        reset()
    }

    fun drawCard(): Card? {
        return if (cards.isNotEmpty()) {
            cards.removeAt(0)
        } else {
            null
        }
    }

    fun reset() {
        cards.clear()
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                cards.add(Card(suit, rank))
            }
        }
        cards.shuffle()
    }
}
