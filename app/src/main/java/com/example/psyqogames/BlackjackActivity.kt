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
import com.example.psyqogames.Blackjack.PlayerType

//todo add a button for debug info and count the cards in the shoe and deck and print them out as we draw more
class BlackjackActivity : AppCompatActivity() {
    private lateinit var blackjackView: BlackjackView
    private lateinit var dealerCard1: ImageView
    private lateinit var dealerCard2: ImageView
    private lateinit var player1Card1: ImageView
    private lateinit var player1Card2: ImageView
    private lateinit var newGameButton: Button
    private lateinit var debugButton: Button
    private lateinit var drawCardButton: Button
    private lateinit var dealButton: Button
    private lateinit var doubleDownButton: Button
    private lateinit var blackjackGame: BlackjackGame
    private lateinit var debugText: TextView
    private lateinit var player1CardPanel: LinearLayout
    private lateinit var context: Context
    private lateinit var dynamicImageView: ImageView
    private lateinit var dynamicLinearLayout: LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blackjack)

        //create a new game with 1 dealer and 1 player
        blackjackGame = BlackjackGame()

        //blackjackGame.printGameState()

        blackjackView = findViewById(R.id.blackjack_view)
        dealerCard1 = findViewById(R.id.dealer_card1_image)
        dealerCard2 = findViewById(R.id.dealer_card2_image)
        debugText = findViewById(R.id.debug_text)
        newGameButton = findViewById(R.id.new_game_button)
        debugButton = findViewById(R.id.debug_button)
        drawCardButton = findViewById(R.id.draw_card_button)
        player1CardPanel = findViewById(R.id.player1_card_panel)
        player1Card1 = findViewById(R.id.player1_card1_image)
        player1Card2 = findViewById(R.id.player1_card2_image)
        dealButton = findViewById(R.id.deal_button)
        doubleDownButton = findViewById(R.id.double_down_button)

        context = this
        dynamicImageView = ImageView(context)
        dynamicLinearLayout = LinearLayout(context)

        drawCardButton.setOnClickListener {
            val drawnCard = blackjackGame.shoe.drawCard()
            drawnCard?.let {
                val resourceId = getResourceIdForCard(it)
                if (resourceId != 0) {
                    dealerCard1.setImageResource(resourceId)
                }
            }
        }

        // New Game Button
        newGameButton.setOnClickListener {
            showInputPrompt()
        }

        // Debug Button
        debugButton.setOnClickListener {
            //shrinkPlayer1CardPanel()
            debugText.text = blackjackGame.getGameStateAsString()
        }

        // Deal Button
        dealButton.setOnClickListener {
            val dealResult = blackjackGame.deal()

            //display the cards dealt to the players and dealer
            dealResult?.let { dealInfo ->
                // Update UI for THIS SPECIFIC player/card
                displayCardForPlayer(dealInfo.player, dealInfo.card)
            } ?: run { // If result is null, means no more cards or error
                Toast.makeText(this, "No more cards to deal for this round.", Toast.LENGTH_SHORT).show()
            }
        }

        // Double Down Button
        //todo dynamically create a panel to display a card
        //i'm stuck here because I can't seem to add anything to the blackjackView... it is it's own thing, not a Constraint or Linear Layout
        doubleDownButton.setOnClickListener {
            dynamicImageView.setImageResource(R.drawable.card_back)

            dynamicLinearLayout.orientation = LinearLayout.HORIZONTAL
            dynamicLinearLayout.id = View.generateViewId()

            val params = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )

            params.bottomToTop = R.id.player1_card_panel

            dynamicLinearLayout.layoutParams = params

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
    private fun displayCardForPlayer(player: Player, card: Card) {
        val cardImageView = getImageViewForPlayerAndCard(player, card)
        if (cardImageView != null) {
            val resourceId = getResourceIdForCard(card) // Your existing helper function
            cardImageView.setImageResource(resourceId)
            cardImageView.visibility = ImageView.VISIBLE // Make sure the ImageView is visible
        }

        //todo use code similar to this to hide the dealers 2nd card
        if (player.playerType == PlayerType.DEALER && player.hand.size > 1) {
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
    fun showInputPrompt() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Game Settings") // More descriptive title

        // Use a LinearLayout to arrange the two EditTexts vertically
        val layout = android.widget.LinearLayout(this)
        layout.orientation = android.widget.LinearLayout.VERTICAL
        layout.setPadding(48, 0, 48, 0) // Add some padding

        // First EditText for the first value
        val input1 = EditText(this)
        input1.hint = "Enter Number of Decks" // Example hint for the first input
        input1.inputType = android.text.InputType.TYPE_CLASS_NUMBER // Specify it's for numbers
        layout.addView(input1)

        // Second EditText for the second value
        val input2 = EditText(this)
        input2.hint = "Enter Starting Bet" // Example hint for the second input
        input2.inputType = android.text.InputType.TYPE_CLASS_NUMBER // Specify it's for numbers
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
                    "Starting game with ${userInput1} decks and bet of ${userInput2}",
                    Toast.LENGTH_LONG
                ).show()

                // TODO: Pass these values to your game initialization logic
                // For example: val numDecks = userInput1.toInt(); val startingBet = userInput2.toInt()
                //             blackjackView.startGame(numDecks, startingBet)
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
}
