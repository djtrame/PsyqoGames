package com.example.psyqogames

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val snakeButton = findViewById<Button>(R.id.snake_button)
        val ticTacToeButton = findViewById<Button>(R.id.tictactoe_button)
        val blackjackButton = findViewById<Button>(R.id.blackjack_button)

        snakeButton.setOnClickListener {
            startActivity(Intent(this, SnakeActivity::class.java))
        }

        ticTacToeButton.setOnClickListener {
            // TODO: Launch TicTacToe game
        }

        blackjackButton.setOnClickListener {
            startActivity(Intent(this, BlackjackActivity::class.java))
        }
    }
}