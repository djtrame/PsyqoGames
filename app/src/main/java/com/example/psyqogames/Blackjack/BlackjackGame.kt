package com.example.psyqogames.Blackjack

// Define a data class to hold the result of dealing a single card
data class DealResult(val player: Player, val card: Card, val bust: Boolean)

class BlackjackGame(private val _numPlayers: Int = 1, private val _numDecks: Int = 1) {

    val numPlayers: Int
        get() = _numPlayers
    val numDecks: Int
        get() = _numDecks

    val players = mutableListOf<Player>()
    var shoe : Shoe = Shoe(numDecks)

    var totalCardsToDeal: Int = 0
    var cardsDealt: Int = 0

    var listTableRounds = mutableListOf<TableRound>()

    var turnNumber: Int = 1

    lateinit var currentPlayerRound: PlayerRound


    // Initialize the game with the specified number of players
    init {
        totalCardsToDeal = 2

            //add players first
            for (i in 1..numPlayers) {
                players.add(Player(PlayerType.HUMAN))
                totalCardsToDeal = totalCardsToDeal + 2
            }

        //add a dealer last
        players.add(Player(_playerType = PlayerType.DEALER))

        //start the first round
        listTableRounds.add(TableRound(players, turnNumber))
        currentPlayerRound = listTableRounds[0].playerRounds[0]
        //currentTurn = CurrentTurn(players[0], turnNumber)
    }

    fun startGame() {

    }

    //fun getCurrentPlayerRound(): PlayerRound? {
        //return currentPlayerRound

        //get the current TableRound
//        var tableRound : TableRound = listTableRounds.last()
//
//        //get the current PlayerRound if they haven't bet yet
//        for (playerRound in tableRound.playerRounds) {
//        if (playerRound.getRoundResult() == null) {
//            return playerRound
//        }


        //this works but i'm going for a more elegant solution
        //            if (playerRound.startingBet == 0) {
//                return playerRound
//            }
        //}
        //all of the players have made a starting bet
        //return null
    //}

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
            return DealResult(player = player, card = newCard, false)
        }
    }

    //i just added the Player object to the PlayerRound
    //this should help the BlackjackGame object keep track of the current turn and player, so the rest of the management of that player's turn should be easy
    fun hit(): DealResult {
        var bust = false

        val currentPlayer = currentPlayerRound.player

        val newCard: Card? = shoe.drawCard()

        if (newCard != null) {
            currentPlayer.hand.add(newCard)
            currentPlayerRound.listChoices.add(PlayerChoice.HIT)

            //if this player busted, move the game to the next player's turn
            if (currentPlayer.getHandSoftValue() > 21) {
                currentPlayerRound.roundResult = RoundResult.LOSE
                nextPlayerRound()
                bust = true
            }
        }
        else {
            throw Exception("Shoe is empty. No more cards to deal.")
        }

        return DealResult(player = currentPlayer, card = newCard, bust)

        //handle dealer stuff, like when they'll stand on a softhand > 16
        //then move onto round end stuff and determine winners/losers
    }

    fun stand() {
        //the player is satisfied with their hand, so they can stand
        //var currentPlayerRound = getCurrentPlayerRound()

        currentPlayerRound.listChoices.add(PlayerChoice.STAND)

        //the dealer will now take their turn
        //if we had multiple players, handle that here, otherwise set the currentRound to the dealer
        //currentPlayerRound = listTableRounds[listTableRounds.count()-1].playerRounds.last()
        nextPlayerRound()

    }

    fun nextPlayerRound() {
        //loop through the player rounds to see if they have a result or they Stood
        //if so, then move onto the next player
        for (playerRound in listTableRounds[turnNumber-1].playerRounds) {
            //if (playerRound.roundResult != null && playerRound.listChoices.last() != PlayerChoice.STAND) {

            //todo figure out uninitiazlied property access exception when this gets called on the dealer, who's result is null
            //maybe just make a roundstatus and set it to in progress on creation, and can change to other values later?
            if (playerRound.roundResult == RoundResult.PENDING) {
                currentPlayerRound = playerRound
            }
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

        for (tableRound in listTableRounds) {
            gameState = gameState + "\nTable round turn number: " + tableRound.turnNumber

            for (playerRound in tableRound.playerRounds) {
                gameState = gameState + "\n" + playerRound.player.playerType + " turn number: " + playerRound.turnNumber + " Starting Bet: " + playerRound.startingBet
            }
        }

        gameState = gameState + "\nCurrent Round Player: " + currentPlayerRound.player.playerType

        return gameState
    }
}

