package com.example.psyqogames.Blackjack

// Define a data class to hold the result of dealing a single card
data class DealResult(val player: Player, val card: Card)

class BlackjackGame(private val _numPlayers: Int = 1) {

    val numPlayers: Int
        get() = _numPlayers
    val players = mutableListOf<Player>()
    var shoe : Shoe = Shoe(1)

    var totalCardsToDeal: Int = 0
    var cardsDealt: Int = 0

    init {
        totalCardsToDeal = 2

            //add players first
            for (i in 1..numPlayers) {
                players.add(Player(PlayerType.HUMAN))
                totalCardsToDeal = totalCardsToDeal + 2
            }

        //add a dealer last
        players.add(Player(_playerType = PlayerType.DEALER))
    }

    fun deal(): DealResult? {
        //upon a single click of the Deal button the first player will have a card added to their hand
        //if all the players have a card, the dealer gets a face down card
        val player = getNextPlayerToDealTo()
        if (player == null) {
            println("No more players to deal to.")
            return null
        }

        val newCard: Card? = shoe.drawCard()

        if (newCard == null) {
            println("Shoe is empty. No more cards to deal.")
            return null
        }
        else
        {
            player.hand.add(newCard)
            return DealResult(player = player, card = newCard)
        }
    }

    fun getNextPlayerToDealTo(): Player? {
        if (cardsDealt >= totalCardsToDeal) {
            // All cards for this round have been dealt
            println("All cards for this round have been dealt.")
            return null
        }

        // Let's use the modulus operator which correctly cycles through player indices.
        // `cardsDealt` is the current turn number (0-indexed).
        // `players.size` is the number of entities receiving cards (Dealer + Human Players).

        val playerIndexToDealTo = cardsDealt % players.size
        val playerToDealTo = players[playerIndexToDealTo]

        // Increment the count of dealt cards AFTER determining who gets it.
        cardsDealt++

        // Return the player who is to receive the next card.
        return playerToDealTo
    }

    fun printGameState() {
        println("--- Game State ---")
        println("Number of decks in shoe: ${shoe.decks.count()}") // Accessing numberOfDecks
        println("Total cards remaining in shoe: ${shoe.remainingCards()}") // Using the existing function

        println("------------------")
    }

    fun getGameStateAsString(): String {
        var gameState = """
            --- Game State ---
            Number of decks in shoe: ${shoe.decks.count()}
            Total cards remaining in shoe: ${shoe.remainingCards()}
            Total cards to deal: $totalCardsToDeal
            ------------------
        """.trimIndent()

        for (player in players) {
            gameState = gameState + "\nPlayer Type: " + player.playerType + " Cards in hand: " + player.hand.count()
        }

        return gameState
    }
}

