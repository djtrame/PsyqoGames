package com.example.psyqogames

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView

class SnakeView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    enum class Direction {
        UP, DOWN, LEFT, RIGHT
    }

    private var gameThread: Thread? = null
    private var running = false
    private var gameOver = false
    private var boxX = 100f
    private var boxY = 100f
    private val boxSize = 50f
    private var direction = Direction.DOWN
    private val boxPaint = Paint().apply {
        color = Color.GREEN
    }
    private val gameOverPaint = Paint().apply {
        color = Color.RED
        textSize = 100f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    init {
        holder.addCallback(this)
    }

    fun startGame() {
        boxX = 100f
        boxY = 100f
        direction = Direction.DOWN
        gameOver = false
        if (gameThread == null || !gameThread!!.isAlive) {
            running = true
            gameThread = Thread { gameLoop() }
            gameThread?.start()
        }
    }

    fun setDirection(newDirection: Direction) {
        if (direction == Direction.UP && newDirection == Direction.DOWN) return
        if (direction == Direction.DOWN && newDirection == Direction.UP) return
        if (direction == Direction.LEFT && newDirection == Direction.RIGHT) return
        if (direction == Direction.RIGHT && newDirection == Direction.LEFT) return
        direction = newDirection
    }

    private fun gameLoop() {
        while (running) {
            update()
            draw()
            Thread.sleep(16) // Aim for ~60fps
        }
    }

    private fun update() {
        when (direction) {
            Direction.UP -> boxY -= 5
            Direction.DOWN -> boxY += 5
            Direction.LEFT -> boxX -= 5
            Direction.RIGHT -> boxX += 5
        }

        if (boxX < 0 || boxX + boxSize > width || boxY < 0 || boxY + boxSize > height) {
            gameOver = true
            running = false
        }
    }

    private fun draw() {
        if (holder.surface.isValid) {
            val canvas: Canvas? = holder.lockCanvas()
            canvas?.let {
                it.drawColor(Color.BLACK)
                if (gameOver) {
                    val centerX = width / 2f
                    val centerY = height / 2f
                    it.drawText("GAME OVER!", centerX, centerY, gameOverPaint)
                } else {
                    it.drawRect(boxX, boxY, boxX + boxSize, boxY + boxSize, boxPaint)
                }
                holder.unlockCanvasAndPost(it)
            }
        }
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        // The game will be started by the button click
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        // No action needed here for now
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        running = false
        try {
            gameThread?.join()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}