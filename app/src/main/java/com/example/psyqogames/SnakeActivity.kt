package com.example.psyqogames

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class SnakeActivity : AppCompatActivity() {

    private lateinit var snakeView: SnakeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snake)

        snakeView = findViewById(R.id.snake_view)
        val newButton = findViewById<Button>(R.id.new_button)
        val upButton = findViewById<Button>(R.id.up_button)
        val leftButton = findViewById<Button>(R.id.left_button)
        val downButton = findViewById<Button>(R.id.down_button)
        val rightButton = findViewById<Button>(R.id.right_button)
        val endButton = findViewById<Button>(R.id.end_button)

        newButton.setOnClickListener {
            snakeView.startGame()
        }

        upButton.setOnClickListener {
            snakeView.setDirection(SnakeView.Direction.UP)
        }

        leftButton.setOnClickListener {
            snakeView.setDirection(SnakeView.Direction.LEFT)
        }

        downButton.setOnClickListener {
            snakeView.setDirection(SnakeView.Direction.DOWN)
        }

        rightButton.setOnClickListener {
            snakeView.setDirection(SnakeView.Direction.RIGHT)
        }

        endButton.setOnClickListener {
            finish()
        }
    }
}