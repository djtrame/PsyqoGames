package com.example.psyqogames

import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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

        val controlsLayout = findViewById<GridLayout>(R.id.controls_layout)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.snake_activity_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.post {
                val controlsHeight = controlsLayout.height
                //snakeView.setBarriers(systemBars.top, controlsHeight)

                //setting the barriers to be the same height
                snakeView.setBarriers(systemBars.top, systemBars.top)
            }
            insets
        }

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