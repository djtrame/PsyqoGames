package com.example.psyqogames

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import java.util.Random

data class Point(var x: Float, var y: Float)

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
    private var topBarrier = 0
    private var bottomBarrier = 0 // Added bottom barrier variable
    private var direction = Direction.DOWN
    private val gameOverPaint = Paint().apply {
        color = Color.RED
        textSize = 100f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private val barrierPaint = Paint().apply {
        color = Color.BLACK
    }

    private var appleBitmap: Bitmap
    private var snakeHeadBitmap: Bitmap
    private var appleX = 0f
    private var appleY = 0f
    private val appleSize = 50f
    private val random = Random()

    private var eatenAppleText: String? = null
    private val eatenApplePaint = Paint().apply {
        color = Color.WHITE
        textSize = 50f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }
    private var textAlpha = 255
    private var textFrameCounter = 0
    private val maxFramesForFade = 20

    private val matrix = Matrix()

    // Snake properties
    private var snakeBody: MutableList<Point> = mutableListOf()
    private var boxX = 100f
    private var boxY = 100f
    private val boxSize = 50f
    private val boxPaint = Paint().apply {
        color = Color.GREEN
    }

    companion object {
        private const val TARGET_FPS = 4.0
        private const val FRAME_PERIOD = (1000.0 / TARGET_FPS).toLong()
    }

    init {
        holder.addCallback(this)
        appleBitmap = BitmapFactory.decodeResource(resources, R.drawable.apple)
        snakeHeadBitmap = BitmapFactory.decodeResource(resources, R.drawable.snakehead)
    }

    fun setBarriers(top: Int, bottom: Int) { // Renamed and modified to accept both barriers
        topBarrier = top
        bottomBarrier = bottom
    }

    fun startGame() {
        boxX = 100f
        boxY = topBarrier + 100f
        direction = Direction.DOWN
        gameOver = false
        eatenAppleText = null
        textAlpha = 255
        textFrameCounter = 0
        snakeBody.clear()
        snakeBody.add(Point(boxX, boxY)) // Add the initial head
        spawnApple()
        if (gameThread == null || !gameThread!!.isAlive) {
            running = true
            gameThread = Thread { gameLoop() }
            gameThread?.start()
        }
    }

    private fun spawnApple() {
        appleX = random.nextInt(width - appleSize.toInt()).toFloat()
        appleY = (topBarrier + random.nextInt(height - topBarrier - appleSize.toInt())).toFloat()
    }

    fun setDirection(newDirection: Direction) {
        if (direction == Direction.UP && newDirection == Direction.DOWN) return
        if (direction == Direction.DOWN && newDirection == Direction.UP) return
        if (direction == Direction.LEFT && newDirection == Direction.RIGHT) return
        if (direction == Direction.RIGHT && newDirection == Direction.LEFT) return
        direction = newDirection
    }

    private fun gameLoop() {
        var lastTime = System.currentTimeMillis()
        while (running) {
            val now = System.currentTimeMillis()
            val elapsedTime = now - lastTime
            val sleepTime = FRAME_PERIOD - elapsedTime

            update(elapsedTime)
            draw()

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime)
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                }
            }
            lastTime = now
        }
    }

    private fun update(elapsedTime: Long) {
        // Move snake head by a fixed amount for discrete steps
        val stepSize = boxSize // Move by the size of one block

        when (direction) {
            Direction.UP -> boxY -= stepSize
            Direction.DOWN -> boxY += stepSize
            Direction.LEFT -> boxX -= stepSize
            Direction.RIGHT -> boxX += stepSize
        }

        // Move snake body
        val newHead = Point(boxX, boxY)
        snakeBody.add(0, newHead)

        // Check for collision with apple
        val snakeRect = RectF(boxX, boxY, boxX + boxSize, boxY + boxSize)
        val appleRect = RectF(appleX, appleY, appleX + appleSize, appleY + appleSize)

        if (snakeRect.intersect(appleRect)) {
            eatenAppleText = "apple eaten!"
            textAlpha = 255
            textFrameCounter = 0
            spawnApple()
            // Snake grows because tail is NOT removed
        } else {
            // Remove tail if no apple was eaten to maintain length
            if (snakeBody.size > 1) {
                snakeBody.removeAt(snakeBody.size - 1)
            }
        }

        // Check for game over conditions (wall collision)
        if (boxX < 0 || boxX + boxSize > width || boxY < topBarrier || boxY + boxSize > height - bottomBarrier) { // Used bottomBarrier here
            gameOver = true
            running = false
        }

        // Check for self-collision
        for (i in 1 until snakeBody.size) { // Start from index 1 to avoid comparing head with itself
            val segment = snakeBody[i]
            val segmentRect = RectF(segment.x, segment.y, segment.x + boxSize, segment.y + boxSize)
            if (snakeRect.intersect(segmentRect)) {
                gameOver = true
                running = false
                break // Exit loop once collision is detected
            }
        }
    }

    private fun draw() {
        if (holder.surface.isValid) {
            val canvas: Canvas? = holder.lockCanvas()
            canvas?.let { canvasObject -> 
                canvasObject.drawColor(Color.rgb(0, 0, 139))
                canvasObject.drawRect(0f, 0f, width.toFloat(), topBarrier.toFloat(), barrierPaint)
                // Draw bottom barrier
                canvasObject.drawRect(0f, (height - bottomBarrier).toFloat(), width.toFloat(), height.toFloat(), barrierPaint)

                if (gameOver) {
                    val centerX = width / 2f
                    val centerY = height / 2f
                    canvasObject.drawText("GAME OVER!", centerX, centerY, gameOverPaint)
                } else {
                    // Draw snake body segments (excluding the head)
                    for (i in 1 until snakeBody.size) { 
                        val segment = snakeBody[i]
                        canvasObject.drawRect(segment.x, segment.y, segment.x + boxSize, segment.y + boxSize, boxPaint)
                    }

                    val rotationAngle = when (direction) {
                        Direction.DOWN -> 180f
                        Direction.RIGHT -> 90f
                        Direction.LEFT -> 270f
                        else -> 0f // UP
                    }

                    matrix.reset()
                    matrix.postRotate(
                        rotationAngle,
                        snakeHeadBitmap.width / 2f,
                        snakeHeadBitmap.height / 2f
                    )
                    matrix.postTranslate(boxX, boxY)

                    canvasObject.drawBitmap(snakeHeadBitmap, matrix, null)

                    val appleRect = RectF(appleX, appleY, appleX + appleSize, appleY + appleSize)
                    canvasObject.drawBitmap(appleBitmap, null, appleRect, null)

                    eatenAppleText?.let { text -> 
                        if (textFrameCounter < maxFramesForFade) {
                            val alphaDecrement = (255f / maxFramesForFade * 1.0f).toInt() // 100% fade over maxFramesForFade
                            textAlpha = (textAlpha - alphaDecrement).coerceAtLeast(0)
                            eatenApplePaint.alpha = textAlpha
                            canvasObject.drawText(text, width / 2f, topBarrier + 50f, eatenApplePaint)
                            textFrameCounter++
                        } else {
                            eatenAppleText = null
                        }
                    }
                }
                holder.unlockCanvasAndPost(canvasObject)
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