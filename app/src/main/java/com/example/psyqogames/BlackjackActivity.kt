package com.example.psyqogames

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.psyqogames.Blackjack.Card
import android.widget.Toast
import android.app.AlertDialog
import android.widget.EditText

//todo add a button for debug info and count the cards in the shoe and deck and print them out as we draw more
class BlackjackActivity : AppCompatActivity() {
    private lateinit var blackjackView: BlackjackView
    private lateinit var dealerCard1: ImageView
    private lateinit var dealerCard2: ImageView
    private lateinit var newGameButton: Button
    private lateinit var debugButton: Button
    private lateinit var drawCardButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blackjack)

        blackjackView = findViewById(R.id.blackjack_view)
        dealerCard1 = findViewById(R.id.dealer_card1_image)
        dealerCard2 = findViewById(R.id.dealer_card2_image)
        newGameButton = findViewById(R.id.new_game_button)
        debugButton = findViewById(R.id.debug_button)
        drawCardButton = findViewById(R.id.draw_card_button)

        //set the dealer cards to appear face down when the view begins
        dealerCard1.setImageResource(R.drawable.card_back)
        dealerCard2.setImageResource(R.drawable.card_back)


        drawCardButton.setOnClickListener {
            val drawnCard = blackjackView.drawRandomCard()
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
            // TODO: Implement Debug functionality
            // For now, we'll just show a brief message to confirm it's working
            Toast.makeText(this, "Debug button clicked!", Toast.LENGTH_SHORT).show()
            println("Debug button clicked!") // Optional: log to Logcat
        }
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
