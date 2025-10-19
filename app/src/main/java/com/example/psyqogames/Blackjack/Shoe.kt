package com.example.psyqogames.Blackjack

class Shoe(private val numberOfDecks: Int = 1) {

    val decks = mutableListOf<Deck>()

    // a shoe becomes a list of intermixed cards, not simply intermixed decks
    val cards = mutableListOf<Card>()

    init {
        repeat(numberOfDecks) {
            decks.add(Deck())
        }
        for (deck in decks) {
            cards.addAll(deck.cards)
        }
        shuffle()
    }

    fun drawCard(): Card? {
        return if (cards.isNotEmpty()) {
            cards.removeAt(0)
        } else {
            null
        }
    }

    fun shuffle() {
        cards.shuffle()
    }

    fun remainingCards(): Int {
        // Now correctly calculates remaining cards using the public 'cards' property of Deck
        //return decks.sumOf { it.cards.size }

        return cards.count()
    }
}
