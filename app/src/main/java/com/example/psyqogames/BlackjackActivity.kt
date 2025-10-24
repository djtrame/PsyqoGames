package com.example.psyqogames

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.psyqogames.Blackjack.Card
import android.widget.Toast
import android.app.AlertDialog
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.psyqogames.Blackjack.BlackjackGame
import com.example.psyqogames.Blackjack.Player
import com.example.psyqogames.Blackjack.PlayerChoice
import com.example.psyqogames.Blackjack.PlayerRound
import com.example.psyqogames.Blackjack.PlayerType
import com.example.psyqogames.Blackjack.PlayerRoundResult
import org.w3c.dom.Text


class BlackjackActivity : AppCompatActivity() {
    //private lateinit var blackjackView: BlackjackView
    private lateinit var dealerCardPanel: LinearLayout
    private lateinit var dealerCard1: ImageView
    private lateinit var dealerCard2: ImageView
    private lateinit var player1Card1: ImageView
    private lateinit var player1Card2: ImageView
    private lateinit var newGameButton: Button
    private lateinit var debugButton: Button
    private lateinit var drawCardButton: Button
    private lateinit var betButton: Button
    private lateinit var dealButton: Button
    private lateinit var hitButton: Button
    private lateinit var standButton: Button
    private lateinit var doubleDownButton: Button
    private lateinit var blackjackGame: BlackjackGame
    private lateinit var debugText: TextView
    private lateinit var player1Label: TextView
    private lateinit var dealerHandLabel: TextView
    private lateinit var player1bankroll: TextView
    private lateinit var player1CardPanel: LinearLayout
    private lateinit var context: Context
    private lateinit var dynamicImageView: ImageView
    private lateinit var dynamicLinearLayout: LinearLayout
    private lateinit var currentPlayerRound: PlayerRound
    private lateinit var listDynamicViews: MutableList<ImageView>
    private lateinit var dealer : Player




    private val CARD_HEIGHT_TO_WIDTH_RATIO = 1.5f


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blackjack)

        //create a new game with 1 dealer and 1 player
        blackjackGame = BlackjackGame()

        //blackjackGame.printGameState()

        //blackjackView = findViewById(R.id.blackjack_view)
        dealerCardPanel = findViewById(R.id.dealer_card_panel)
        dealerCard1 = findViewById(R.id.dealer_card1_image)
        dealerCard2 = findViewById(R.id.dealer_card2_image)
        debugText = findViewById(R.id.debug_text)
        newGameButton = findViewById(R.id.new_game_button)
        debugButton = findViewById(R.id.debug_button)
        drawCardButton = findViewById(R.id.draw_card_button)

        player1CardPanel = findViewById(R.id.player1_card_panel)
        player1Card1 = findViewById(R.id.player1_card1_image)
        player1Card2 = findViewById(R.id.player1_card2_image)
        betButton = findViewById(R.id.bet_button)
        dealButton = findViewById(R.id.deal_button)
        hitButton = findViewById(R.id.hit_button)
        standButton = findViewById(R.id.stand_button)
        doubleDownButton = findViewById(R.id.double_down_button)
        player1Label = findViewById(R.id.player1_label)
        dealerHandLabel = findViewById(R.id.dealer_hand_label)
        player1bankroll = findViewById(R.id.player1_bankroll)

        context = this
        listDynamicViews = mutableListOf<ImageView>()

        drawCardButton.setOnClickListener {
            debugText.text = blackjackGame.getGameStateAsString()
//            val drawnCard = blackjackGame.shoe.drawCard()
//            drawnCard?.let {
//                val resourceId = getResourceIdForCard(it)
//                if (resourceId != 0) {
//                    dealerCard1.setImageResource(resourceId)
//                }
//            }
        }

        // New Game Button
        newGameButton.setOnClickListener {
            showNewGameInputPrompt()
        }

        // Debug Button
        debugButton.setOnClickListener {
            //shrinkPlayer1CardPanel()
            //debugText.text = blackjackGame.getGameStateAsString()
            //debugText.text = blackjackGame.currentPlayerRound.getPlayerRoundStateAsString()
            if (blackjackGame.listTableRounds.isNotEmpty()) {
                debugText.text = blackjackGame.currentTableRound.getTableRoundStateAsString()
            }
            else {
                debugText.text = "No Table Rounds"
            }

        }

        // Bet Button
        betButton.setOnClickListener {
            dealer = blackjackGame.players.last()
            clearGame()
            //prompt for the first player's bet
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Enter Starting Bet")

            // Use a LinearLayout to arrange the two EditTexts vertically
            val layout = android.widget.LinearLayout(this)
            layout.orientation = android.widget.LinearLayout.VERTICAL
            layout.setPadding(48, 0, 48, 0) // Add some padding

            // First EditText for the first value
            val input1 = EditText(this)
            input1.hint = "Enter Your Starting Bet"
            input1.inputType = android.text.InputType.TYPE_CLASS_NUMBER // Specify it's for numbers
            input1.setText("5")
            layout.addView(input1)

            builder.setView(layout) // Set the LinearLayout containing the EditTexts as the view

            // Set up the buttons
            builder.setPositiveButton("Enter Bet") { dialog, which ->
                val userInput1 = input1.text.toString()

                if (userInput1.isNotEmpty()) {
                    //get the current PlayerRound and set their bet equal to this value
                    //might need better null handling.  not sure what !! does
                    //todo take the starting bet, and begin to handle hitting and standing
                    //maybe move some of this stuff into BlackjackGame.kt
                    blackjackGame.startRound(userInput1.toInt())
                    debugText.text = blackjackGame.currentTableRound.getTableRoundStateAsString()
                    player1bankroll.text = "$" + blackjackGame.currentTableRound.playerRounds[0].player.bankRoll.toString()
                } else {
                    //Toast.makeText(this, "Please enter both values", Toast.LENGTH_SHORT).show()
                }
            }
            builder.setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel() // Close the dialog
            }

            // Show the dialog
            builder.show()
        }

        // Deal Button
        dealButton.setOnClickListener {
            val dealResult = blackjackGame.deal()

            //display the cards dealt to the players and dealer
            dealResult?.let { dealInfo ->
                // Update UI for THIS SPECIFIC player/card
                displayCardForPlayer(dealInfo.player, dealInfo.card)

                //this will handle only 1 player... will need lots of work to loop through future players
                if (dealInfo.player.playerType == PlayerType.HUMAN) {
                    //get the soft and hard values of the hand
                    player1Label.text = showHandValue(dealInfo.player)
                }

                blackjackGame.checkForBlackjack()

            } ?: run { // If result is null, means no more cards or error
                Toast.makeText(this, "No more cards to deal for this round.", Toast.LENGTH_SHORT).show()
            }

            debugText.text = blackjackGame.getGameStateAsString()
        }

        // Hit Button
        hitButton.setOnClickListener {
            var hitResult = blackjackGame.hit()

            //for starters just draw a 3rd card in the player 1 card panel.  We'll worry about the rest later
            dynamicImageView = ImageView(context) //trying to use this same object variable in hit button and dd button sections
            dynamicImageView.setImageResource(getResourceIdForCard(hitResult.card))

            val widthInDP = 60
            val heightInDP = widthInDP * CARD_HEIGHT_TO_WIDTH_RATIO

            val widthInPixels = (widthInDP * resources.displayMetrics.density).toInt()
            val heightInPixels = (heightInDP * resources.displayMetrics.density).toInt()

            val layoutParamsLinear = LinearLayout.LayoutParams(
                widthInPixels,
                heightInPixels
            )
            val marginPx = 2

            layoutParamsLinear.setMargins(marginPx, marginPx,marginPx,marginPx)
            dynamicImageView.layoutParams = layoutParamsLinear

            dynamicImageView.setBackgroundResource(R.color.white)

            listDynamicViews.add(dynamicImageView)

            if (hitResult.player.playerType == PlayerType.HUMAN) {
                player1Label.text = showHandValue(hitResult.player)
                player1CardPanel.addView(dynamicImageView)

                //if the player busted during this hit, we need to move onto the dealer's turn and show their card
                if (hitResult.bust) {
                    player1Label.setTextColor(resources.getColor(R.color.red))
                    displayCardForPlayer(dealer, dealer.hand[1], false)
                    dealerHandLabel.text = showHandValue(dealer)
                }
            }
            else {
                // if the dealer busted color their text red
                if (hitResult.bust) {
                    dealerHandLabel.setTextColor(resources.getColor(R.color.red))
                } //else if their last choice was a Stand (cuz dealer > 16 auto stands)
                else if (blackjackGame.currentPlayerRound.listChoices.last() == PlayerChoice.STAND) {
                    dealerHandLabel.setTextColor(resources.getColor(R.color.black))
                    //dealer will stay, so handle round end stuff and determine winners/losers
                }

                dealerCardPanel.addView(dynamicImageView)
                dealerHandLabel.text = showHandValue(dealer)
            }

            debugText.text = blackjackGame.getGameStateAsString()
            player1bankroll.text = "$" + blackjackGame.currentTableRound.playerRounds[0].player.bankRoll.toString()

            //todo handle coloring dealer hand (with hard 20) after player bust


//            //if the player busted during this hit, we need to move onto the dealer's turn and show their card
//            if (hitResult.bust) {
//                displayCardForPlayer(dealer, dealer.hand[1], false)
//                dealerHandLabel.text = showHandValue(dealer)
//            }
        }

        standButton.setOnClickListener {
            blackjackGame.stand()

            //unhide the dealer's 2nd card
            displayCardForPlayer(dealer, dealer.hand[1], false)

            debugText.text = blackjackGame.getGameStateAsString()
            if (blackjackGame.currentPlayerRound.player.playerType == PlayerType.HUMAN) {
                player1Label.text = showHandValue(blackjackGame.currentPlayerRound.player)
                dealerHandLabel.setTextColor(resources.getColor(R.color.black))
            }
            else {
                dealerHandLabel.text = showHandValue(dealer)
            }

            //a player stand automatically reveals and evaluates the dealers hand.
            // If the dealer is already > 16 then color their text black
            if (blackjackGame.currentPlayerRound.player.playerType == PlayerType.DEALER) {
                if (blackjackGame.currentPlayerRound.player.getBestHandValue() > 16) {
                    dealerHandLabel.setTextColor(resources.getColor(R.color.black))
                }
            }


            player1bankroll.text = "$" + blackjackGame.currentTableRound.playerRounds[0].player.bankRoll.toString()
        }

        // Double Down Button
        doubleDownButton.setOnClickListener {
            dynamicImageView = ImageView(context)
            dynamicLinearLayout = LinearLayout(context)
            dynamicImageView.setImageResource(R.drawable.card_back)
            dynamicImageView.rotation = 90f

            val blackjackTable = findViewById<ConstraintLayout>(R.id.blackjack_table)
            val player1Label = findViewById<TextView>(R.id.player1_label)

            dynamicLinearLayout.orientation = LinearLayout.HORIZONTAL
            dynamicLinearLayout.id = View.generateViewId()

            //generally keep a 1.5 ratio of card height to width (but this one is on its side so yeah)
//            val widthInDP = 120
//            val heightInDP = 60
            //val widthInDP = 150
            val heightInDP = 90
            val widthInDP = heightInDP * CARD_HEIGHT_TO_WIDTH_RATIO

            val widthInPixels = (widthInDP * resources.displayMetrics.density).toInt()
            val heightInPixels = (heightInDP * resources.displayMetrics.density).toInt()

            val params = ConstraintLayout.LayoutParams(
                //ConstraintLayout.LayoutParams.MATCH_PARENT,
                //ConstraintLayout.LayoutParams.WRAP_CONTENT
                widthInPixels,
                heightInPixels
            )

            params.topToBottom = R.id.player1_label
            params.bottomToTop = R.id.player1_card_panel
            params.startToStart = R.id.player1_card_panel
            params.endToEnd = R.id.blackjack_table

            dynamicLinearLayout.layoutParams = params

            blackjackTable.addView(dynamicLinearLayout)

            val marginPx = 8

            val layoutParamsLinear = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParamsLinear.setMargins(10, 10, 10, 10)
            dynamicImageView.layoutParams = layoutParamsLinear

            dynamicLinearLayout.addView(dynamicImageView)

            //val containerLayout: LinearLayout = findViewById(R.id.player1_card_panel)
            //containerLayout.addView(dynamicImageView)
        }


    }

    /**
     * Helper function to display a card for a specific player.
     * This is where you'd update the UI without looping through everyone.
     */
    private fun displayCardForPlayer(player: Player, card: Card, hideDealer: Boolean = true) {
        val cardImageView = getImageViewForPlayerAndCard(player, card)
        if (cardImageView != null) {
            val resourceId = getResourceIdForCard(card) // Your existing helper function
            cardImageView.setImageResource(resourceId)
            cardImageView.visibility = ImageView.VISIBLE // Make sure the ImageView is visible
        }

        //hide the dealers 2nd card
        if (player.playerType == PlayerType.DEALER && player.hand.size > 1 && hideDealer) {
            // This is likely the dealer's second card (or later).
            // If it's the *initial deal's* second card for the dealer, it should be face down.
            // If the game logic itself doesn't distinguish face up/down, you'd add it here.
            // For simplicity, we're assuming the ImageView is already set to card_back.
            // If you draw a card for dealer's second hand, it should NOT overwrite card_back
            // unless you are revealing it.
            // You might need a way to know if it's the face-down card.
            dealerCard2.setImageResource(R.drawable.card_back)
        }
    }

    /**
     * Helper to get the correct ImageView for a player and their card.
     * This function would need to be more sophisticated if players have many cards.
     * For the initial 2-card deal, it's simpler.
     */
    private fun getImageViewForPlayerAndCard(player: Player, card: Card): ImageView? {
        // This is a crucial part where you map which ImageView corresponds to which player and card.
        // You might need to track the order cards were dealt to each player.

        // Simplified logic for the first 2 cards dealt to each player:
        when (player.playerType) {
            PlayerType.DEALER -> {
                return when (player.hand.size) {
                    1 -> dealerCard1 // First card for dealer
                    2 -> dealerCard2 // Second card for dealer (initially face down)
                    else -> null // Handle more cards if needed
                }
            }
            PlayerType.HUMAN -> {
                // If you have multiple human players, you'll need to identify which player
                // and then which card in their hand.
                // For Player 1 (assuming player index 1 in blackjackGame.players)
                if (blackjackGame.players.indexOf(player) == 0) { // Check if it's player 1
                    return when (player.hand.size) {
                        1 -> player1Card1
                        2 -> player1Card2
                        else -> null // Handle more cards if needed
                    }
                }
                // Add cases for other human players (player 2, player 3, etc.)
            }
            else -> {
                return null
            }
        }
        return null // Couldn't find a suitable ImageView
    }

    private fun shrinkPlayer1CardPanel() {
        // Shrink the panel to, say, half its original size (example values)
        val originalWidth = player1CardPanel.width // Get current width if needed
        val originalHeight = player1CardPanel.height // Get current height if needed

        // Calculate new dimensions (e.g., make it 90% of original)
        val desiredWidth = (originalWidth * 0.9).toInt()
        val desiredHeight = (originalHeight * 0.9).toInt()

        // Ensure minimum dimensions if needed
        val minWidth = 100 // Example minimum width in pixels
        val minHeight = 100 // Example minimum height in pixels

        // Get the current LayoutParams of the LinearLayout
        val layoutParams = player1CardPanel.layoutParams as ViewGroup.LayoutParams

        // Modify the width and height
        layoutParams.width = maxOf(desiredWidth, minWidth)
        layoutParams.height = maxOf(desiredHeight, minHeight)

        // Apply the updated LayoutParams back to the view
        player1CardPanel.layoutParams = layoutParams

        // Request a layout pass to update the UI
        player1CardPanel.requestLayout()
    }

    // Helper function to get the resource ID based on the Card object
    private fun getResourceIdForCard(card: Card): Int {
        val suit = card.suit.name.lowercase()
        val rank = card.rank.name.lowercase()
        val resourceName = "${suit}_${rank}"
        return resources.getIdentifier(resourceName, "drawable", packageName)
    }

    // Inside your Activity or Fragment:
    fun showNewGameInputPrompt() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Game Settings") // More descriptive title

        // Use a LinearLayout to arrange the two EditTexts vertically
        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(48, 0, 48, 0) // Add some padding

        // First EditText for the first value
        val input1 = EditText(this)
        input1.hint = "Enter Number of Players" // Example hint for the first input
        input1.inputType = android.text.InputType.TYPE_CLASS_NUMBER // Specify it's for numbers
        input1.setText("1")
        layout.addView(input1)

        // Second EditText for the second value
        val input2 = EditText(this)
        input2.hint = "Enter Number of Decks in the Shoe" // Example hint for the second input
        input2.inputType = android.text.InputType.TYPE_CLASS_NUMBER // Specify it's for numbers
        input2.setText("2")
        layout.addView(input2)

        builder.setView(layout) // Set the LinearLayout containing the EditTexts as the view

        // Set up the buttons
        builder.setPositiveButton("Start Game") { dialog, which ->
            val userInput1 = input1.text.toString()
            val userInput2 = input2.text.toString()

            if (userInput1.isNotEmpty() && userInput2.isNotEmpty()) {
                // Do something with both user inputs
                // For demonstration, we'll show them in a Toast
                Toast.makeText(
                    this,
                    "Starting game with ${userInput1} players and ${userInput2} decks",
                    Toast.LENGTH_SHORT
                ).show()

                startNewGame(userInput1.toInt(), userInput2.toInt())
            } else {
                Toast.makeText(this, "Please enter both values", Toast.LENGTH_SHORT).show()
            }
        }
        builder.setNegativeButton("Cancel") { dialog, which ->
            dialog.cancel() // Close the dialog
        }

        // Show the dialog
        builder.show()
    }

    fun startNewGame(numPlayers: Int, numDecks: Int) {
        clearGame()
        blackjackGame = BlackjackGame(numPlayers, numDecks)
        //todo fix crash after trying to stand in a new game
    }

    fun clearGame() {
        debugText.text = "clear"
        player1Label.text = "clear"
        player1Label.setTextColor(resources.getColor(R.color.white))
        dealerHandLabel.text = "clear"
        dealerHandLabel.setTextColor(resources.getColor(R.color.white))
        dealerCard1.setImageResource(0)
        player1Card1.setImageResource(0)
        player1Card2.setImageResource(0)
        dealerCard2.setImageResource(0)

        //i'm surprised this works
        for (view in listDynamicViews) {
            player1CardPanel.removeView(view)
            dealerCardPanel.removeView(view)
        }
    }

    fun showHandValue(player: Player): String {
        //get the soft and hard values of the hand
        var handString: String
        var softValue: Int
        var hardValue: Int
        var bestValue: Int

        softValue = player.getHandSoftValue()
        hardValue = player.getHandHardValue()
        bestValue = player.getBestHandValue()

        handString = "Soft value: " + softValue.toString() + "\n"
        handString += "Hard value: " + hardValue.toString() +"\n"
        handString += "Best value: " + bestValue.toString()
        if (bestValue > 21) {
            handString += "\nBust!!"
        }
        return handString

    }
}
