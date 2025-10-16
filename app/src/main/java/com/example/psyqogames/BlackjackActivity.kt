package com.example.psyqogames

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BlackjackActivity : AppCompatActivity() {
    private lateinit var blackjackView: BlackjackView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blackjack)

        blackjackView = findViewById(R.id.blackjack_view)
        val drawCardButton: Button = findViewById(R.id.draw_card_button)

        drawCardButton.setOnClickListener {
            blackjackView.drawRandomCard()
        }
    }
}
