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

    lateinit var currentTableRound: TableRound
    lateinit var currentPlayerRound: PlayerRound


    lateinit var dealer: Player


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
        dealer = players.last()

        //start the first round
        currentTableRound = TableRound(players, turnNumber)
        listTableRounds.add(currentTableRound)
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
        //if all the players have a card, the dealer gets a card.  the 2nd dealer card will be displayed face down
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

            //todo handle dealer blackjack here
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

            var handSoftValue = currentPlayer.getHandSoftValue()

            //if this player busted, mark that they lost this round
            if (handSoftValue > 21) {
                currentPlayerRound.roundResult = RoundResult.LOSE
                bust = true

                //if the dealer busted, end the TableRound
                if (currentPlayer.playerType == PlayerType.DEALER) {
                    finalizeTableRound()
                }
                //else, if the player is not a dealer, then move onto the next player
                else {
                    nextPlayerRound()
                }
            }
            //if this is a dealer and the soft value is > 16 the dealer will stand
            else if (currentPlayer.playerType == PlayerType.DEALER && handSoftValue > 16) {
                currentPlayerRound.listChoices.add(PlayerChoice.STAND)

                //end the TableRound
                finalizeTableRound()
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
        currentPlayerRound.listChoices.add(PlayerChoice.STAND)

        //if we're now on the dealer, check if they should stand before any action is taken
        //val currentPlayer = currentPlayerRound.player
        //var handSoftValue = currentPlayer.getHandSoftValue()

        nextPlayerRound()

//        //todo fix this... we're automatically marking the dealer with a Stand in the hit function, no need to here.
//        if (currentPlayer.playerType == PlayerType.DEALER) {
//            if (handSoftValue > 16) {
//                currentPlayerRound.listChoices.add(PlayerChoice.STAND)
//                //end the TableRound
//                finalizeTableRound()
//            }
//        }
//        //else, if the player is not a dealer, then move onto the next player
//        else {
//            nextPlayerRound()
//        }
    }

    fun finalizeTableRound() {
        //todo handle when player stands and dealer shows higher value and thus wins on the spot
        //this came up but the player was still showing PENDING
        //todo multiple aces can be considered 1 or 11

        //get the dealer's result first
        var dealerHandSoftValue = dealer.getHandSoftValue()
        var dealerHandHardValue = dealer.getHandHardValue()
        var dealerBust = false

        //currentRound in this case should still be the dealer


        if (dealerHandSoftValue > 21) {
            dealerBust = true

        }

        //loop through the non-dealer player rounds
        for (playerRound in currentTableRound.playerRounds) {
            if (playerRound.player.playerType != PlayerType.DEALER) {
                //if this round doesn't already have a result
                if (playerRound.roundResult == RoundResult.PENDING) {
                    //compare this players hand against the dealers hand
                    if (!dealerBust) {
                        var bestHandValue = playerRound.player.getBestHandValue()
                        var dealerBestHandValue = dealer.getBestHandValue()
                        if (bestHandValue > dealerBestHandValue) {
                            playerRound.roundResult = RoundResult.WIN
                        }
                        else if (bestHandValue < dealerBestHandValue) {
                            playerRound.roundResult = RoundResult.LOSE
                        } else {
                            playerRound.roundResult = RoundResult.PUSH
                        }
                    }
                    else {
                        //if the dealer busted but we haven't, then we win
                        playerRound.roundResult = RoundResult.WIN
                    }
                }

            }


        }



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

                //if we're onto the dealer then check if we need to finalize the round
                if (currentPlayerRound.player.playerType == PlayerType.DEALER) {
                    var handSoftValue = currentPlayerRound.player.getHandSoftValue()

                    if (handSoftValue > 16) {
                        currentPlayerRound.listChoices.add(PlayerChoice.STAND)

                        //end the TableRound
                        finalizeTableRound()
                    }
                }
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
            Number of decks in shoe: ${shoe.decks.count()}
            Total cards remaining in shoe: ${shoe.remainingCards()}            
        """.trimIndent()

        //--- Game State ---
        //Total cards to deal: $totalCardsToDeal

        for (player in players) {
            gameState = gameState + "\nPlayer Type: " + player.playerType + " Cards in hand: " + player.hand.count()
        }

        for (tableRound in listTableRounds) {
            gameState = gameState + "\nTable round turn number: " + tableRound.turnNumber

            for (playerRound in tableRound.playerRounds) {
                gameState = gameState + "\n" + playerRound.player.playerType + " turn number: " + playerRound.turnNumber + " Starting Bet: " + playerRound.startingBet

                //print each choice the player made
                for (choice in playerRound.listChoices) {
                    gameState = gameState + "\n   Choice: " + choice
                }
            }
        }

        gameState = gameState + "\nCurrent Round Player: " + currentPlayerRound.player.playerType

        return gameState
    }
}

