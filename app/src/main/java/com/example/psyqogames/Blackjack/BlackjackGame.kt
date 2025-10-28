package com.example.psyqogames.Blackjack

// Define a data class to hold the result of dealing a single card
data class DealResult(val player: Player, val card: Card, val bust: Boolean)

enum class BlackJackCheckResult {
    PLAYER, DEALER, BOTH, NONE
}

class BlackjackGame(private val _numPlayers: Int = 1, private val _numDecks: Int = 2) {

    val numPlayers: Int
        get() = _numPlayers
    val numDecks: Int
        get() = _numDecks

    val players = mutableListOf<Player>()
    lateinit var shoe : Shoe

    //constant representing 3:2 payout
    val BLACKJACK_PAYOUT: Float = 1.5f

    var totalCardsToDeal: Int = 0
    var cardsDealt: Int = 0

    var listTableRounds = mutableListOf<TableRound>()

    var turnNumber: Int = 1

    lateinit var currentTableRound: TableRound
    lateinit var currentPlayerRound: PlayerRound
    lateinit var dealer: Player


    // Initialize the game with the specified number of players
    init {
        newShoe()
        totalCardsToDeal = 2

            //add players first
            for (i in 1..numPlayers) {
                players.add(Player(PlayerType.HUMAN, 100))
                totalCardsToDeal = totalCardsToDeal + 2
            }

        //add a dealer last
        players.add(Player(_playerType = PlayerType.DEALER, 0))
        dealer = players.last()
    }

    fun newShoe() {
        shoe = Shoe(numDecks)
    }

    //when a player makes a bet, start the round
    fun startRound(bet: Int) {
        currentTableRound = TableRound(players, turnNumber)
        listTableRounds.add(currentTableRound)
        currentPlayerRound = currentTableRound.playerRounds[0]
        currentPlayerRound.startingBet = bet
        currentPlayerRound.player.bankRoll -= bet
        cardsDealt = 0
    }

    //upon a single click of the Deal button the first player will have a card added to their hand
    //if all the players have a card, the dealer gets a card.  the 2nd dealer card will be displayed face down
    fun deal(): DealResult? {
        //todo if a shoe runs out of cards mid-hand, the game should shuffle the discard pile up
        //then ensure a full shuffle of all cards after the round ends
        if (shoe.cards.count() < totalCardsToDeal) {
            newShoe()
        }

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

    fun checkForBlackjack(): BlackJackCheckResult {
        //todo when a dealer got a blackjack it showed as a roundresult WIN, instead of Blackjack?  Possibly not a big deal
        //if both have blackjack it's a push
        if (cardsDealt == totalCardsToDeal) {
            var dealerHandValue = dealer.getBestHandValue()
            var playerHandValue = currentPlayerRound.player.getBestHandValue()

            //dealer blackjack
            if (dealerHandValue == 21) {
                if (dealerHandValue == playerHandValue) {
                    currentPlayerRound.playerRoundResult = PlayerRoundResult.PUSH
                    finalizeTableRound()
                    return BlackJackCheckResult.BOTH
                }
                else {
                    currentTableRound.playerRounds.last().playerRoundResult = PlayerRoundResult.BLACKJACK
                    finalizeTableRound()
                    return BlackJackCheckResult.DEALER
                }
            }
            //player blackjack
            else if (playerHandValue == 21) {
                currentPlayerRound.playerRoundResult = PlayerRoundResult.BLACKJACK
                finalizeTableRound()
                return BlackJackCheckResult.PLAYER
            }
            else {
                return BlackJackCheckResult.NONE
            }
        }
        else {
            return BlackJackCheckResult.NONE
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

            var bestHandValue = currentPlayer.getBestHandValue()

            //if this player busted, mark that they lost this round
            if (bestHandValue > 21) {
                currentPlayerRound.playerRoundResult = PlayerRoundResult.LOSE
                bust = true

                //if the dealer busted, end the TableRound
                if (currentPlayer.playerType == PlayerType.DEALER) {
                    finalizeTableRound()
                }
                //else, if the player is not a dealer, then move onto the next player
                else {
                    nextPlayerRound()

                    //now check that we're onto the dealers turn, and if we are then finalize
                    if (currentPlayer.playerType == PlayerType.DEALER) {
                        finalizeTableRound()
                    }
                }
            }
            //if this is a dealer and the soft value is > 16 the dealer will stand
            else if (currentPlayer.playerType == PlayerType.DEALER && bestHandValue > 16) {
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
        //todo mark dealer as a winner when player busts

        //get the dealer's result first
        var dealerBestHandValue = dealer.getBestHandValue()
        var dealerBust = false
        var dealerRound = currentTableRound.playerRounds.last()
        turnNumber++

        if (dealerBestHandValue > 21) {
            dealerBust = true
        }

        //loop through the non-dealer player rounds
        for (playerRound in currentTableRound.playerRounds) {

            //if this is a human player
            if (playerRound.player.playerType != PlayerType.DEALER) {
                //if this round doesn't already have a result
                if (playerRound.playerRoundResult == PlayerRoundResult.PENDING) {
                    //compare this players hand against the dealers hand
                    if (!dealerBust) {
                        var bestHandValue = playerRound.player.getBestHandValue()
                        if (bestHandValue > dealerBestHandValue) {
                            playerRound.playerRoundResult = PlayerRoundResult.WIN
                            playerRound.player.bankRoll += (playerRound.endingBet * 2)
                        }
                        else if (bestHandValue < dealerBestHandValue) {
                            playerRound.playerRoundResult = PlayerRoundResult.LOSE
                            //by the time we get here we've already checked for a blackjack, so if the dealer has it then leave their result alone
                            if (dealerRound.playerRoundResult != PlayerRoundResult.BLACKJACK) {
                                dealerRound.playerRoundResult = PlayerRoundResult.WIN
                            }
                        } else {
                            playerRound.playerRoundResult = PlayerRoundResult.PUSH
                            playerRound.player.bankRoll += playerRound.endingBet
                            dealerRound.playerRoundResult = PlayerRoundResult.PUSH
                        }
                    }
                    else {
                        //if the dealer busted but we haven't, then we win
                        playerRound.playerRoundResult = PlayerRoundResult.WIN
                        playerRound.player.bankRoll += (playerRound.endingBet * 2)
                        dealerRound.playerRoundResult = PlayerRoundResult.LOSE
                    }

                }
                //else if we busted previously, take our money and mark the dealer with a win
                else if (playerRound.playerRoundResult == PlayerRoundResult.LOSE) {
                    //playerRound.player.bankRoll -= playerRound.endingBet
                    dealerRound.playerRoundResult = PlayerRoundResult.WIN
                }
                //else if the player had blackjack, pay them out
                //todo if the player has blackjack, we need to check for dealer blackjack before we pay the player
                //both blackjacks is a push
                else if (playerRound.playerRoundResult == PlayerRoundResult.BLACKJACK) {
                    if (dealerRound.playerRoundResult == PlayerRoundResult.BLACKJACK) {
                        playerRound.playerRoundResult = PlayerRoundResult.PUSH
                        dealerRound.playerRoundResult = PlayerRoundResult.PUSH
                    }
                    else {
                        playerRound.player.bankRoll += playerRound.endingBet + (playerRound.endingBet * BLACKJACK_PAYOUT).toInt()
                    }

                }
            }
            //if this is the dealer
            else {
                //this will get odd marking a dealer as a winner if there are multiple human players
                //but we just have the one for now, so if the dealer beat any player we'll call it a win
                //uhh we handled this above when looking at a player
                //so logically with multiple players the dealer could win against one player, and push against another
                //worry about that later...dealer result isn't really that important
                if (dealerRound.playerRoundResult == PlayerRoundResult.PENDING) {
                    dealerRound.playerRoundResult = PlayerRoundResult.WIN
                }
            }
        }

        //todo prevent buttons like Hit from doing things when the round is over

        currentTableRound.tableRoundResult = TableRoundResult.COMPLETE
    }

    fun nextPlayerRound() {
        //loop through the player rounds to see if they have a result or they Stood
        //if so, then move onto the next player
        for (playerRound in currentTableRound.playerRounds) {
            //if the player's round has no result, and they haven't made any choices, then move onto that player's round
            if (playerRound.playerRoundResult == PlayerRoundResult.PENDING && playerRound.listChoices.count() == 0) {
                currentPlayerRound = playerRound

                //if we're onto the dealer then check if we need to finalize the round
                if (currentPlayerRound.player.playerType == PlayerType.DEALER) {
                    var bestHandValue = currentPlayerRound.player.getBestHandValue()

                    if (bestHandValue > 16) {
                        currentPlayerRound.listChoices.add(PlayerChoice.STAND)

                        //end the TableRound
                        finalizeTableRound()
                    }
                }
            }
            //else if this player just busted, then the dealer wins when there's just 1 player
            else {
                if (currentTableRound.playerList.count() == 2) {
                    if (playerRound.playerRoundResult == PlayerRoundResult.LOSE) {
                        finalizeTableRound()
                        return //short circuit the function if there is just 1 human player and they busted
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
        if (listTableRounds.isEmpty()) {
            gameState = gameState + "\nGame not started"
            return gameState
            }

        for (player in players) {
            gameState = gameState + "\nPlayer Type: " + player.playerType + " Cards in hand: " + player.hand.count()
        }

        //instead of looping through all the table rounds, just print out info from the latest one
//        for (tableRound in listTableRounds) {
//        }
        val tableRound = listTableRounds.last()

        gameState = gameState + "\nTable round turn number: " + tableRound.turnNumber

        for (playerRound in tableRound.playerRounds) {
            gameState = gameState + "\n" + playerRound.player.playerType + " turn number: " + playerRound.turnNumber + " Starting Bet: " + playerRound.startingBet

            //print each choice the player made
            for (choice in playerRound.listChoices) {
                gameState = gameState + "\n   Choice: " + choice
            }
        }

        gameState = gameState + "\nPlayer Bankroll: " + players[0].bankRoll
        gameState = gameState + "\nCurrent Round Player: " + currentPlayerRound.player.playerType
        gameState = gameState + "\nTable Round Result: " + currentTableRound.tableRoundResult


        return gameState
    }
}

