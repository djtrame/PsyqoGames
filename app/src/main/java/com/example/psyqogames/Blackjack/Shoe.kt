package com.example.psyqogames.Blackjack

class Shoe(private val numberOfDecks: Int = 1) {

    private val decks = mutableListOf<Deck>()

    init {
        repeat(numberOfDecks) {
            decks.add(Deck())
        }
        shuffle()
    }

    fun drawCard(): Card? {
        var card = decks.lastOrNull()?.drawCard()
        if (card == null) {
            // If the last deck is empty, remove it and try drawing from the new last deck
            if (decks.isNotEmpty()) {
                decks.removeAt(decks.size - 1)
            }
            if (decks.isNotEmpty()) {
                card = decks.lastOrNull()?.drawCard()
            }
            if (card == null && decks.isEmpty()) {
                // If all decks are empty, reshuffle and try again
                shuffle()
                card = decks.lastOrNull()?.drawCard()
            }
        }
        return card
    }

    fun shuffle() {
        decks.forEach { it.reset() } // Reset each deck
        decks.shuffle() // Shuffle the order of the decks themselves
    }

    fun remainingCards(): Int {
        // Now correctly calculates remaining cards using the public 'cards' property of Deck
        return decks.sumOf { it.cards.size }
    }
}
