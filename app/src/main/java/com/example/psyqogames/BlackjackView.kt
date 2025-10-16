package com.example.psyqogames

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.example.psyqogames.Blackjack.Card
import com.example.psyqogames.Blackjack.Rank
import com.example.psyqogames.Blackjack.Suit

class BlackjackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var cardBitmap: Bitmap? = null
    private var cardBaseBitmap: Bitmap? = null
    private var cardScale: Float = 0.3f // 70% smaller
    private val deck = mutableListOf<Card>()

    private val cardBasePaint = Paint().apply {
        color = Color.WHITE
    }

    init {
        createAndShuffleDeck()
    }

    private fun createAndShuffleDeck() {
        deck.clear()
        for (suit in Suit.values()) {
            for (rank in Rank.values()) {
                deck.add(Card(suit, rank))
            }
        }
        deck.shuffle()
    }

    fun drawRandomCard() {
        if (deck.isEmpty()) {
            // For now, just recreate and shuffle if the deck is empty.
            createAndShuffleDeck()
            cardBitmap = null // Clear the card if deck is empty
        } else {
            val card = deck.removeAt(0)
            val resourceId = getResourceIdForCard(card)
            if (resourceId != 0) {
                val originalBitmap = BitmapFactory.decodeResource(resources, resourceId)
                val newWidth = (originalBitmap.width * cardScale).toInt()
                val newHeight = (originalBitmap.height * cardScale).toInt()
                //cardBaseBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
                cardBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            }
        }
        invalidate() // Request a redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the background
        canvas.drawColor(Color.rgb(0, 100, 0))

        //draw the base of the card as white since they are transparent PNG images
        //canvas.drawRect(100f, segment.y, segment.x + boxSize, segment.y + boxSize, boxPaint)

        // Draw the card
        cardBitmap?.let { bitmap ->
            canvas.drawBitmap(bitmap, 100f, 100f, null)
        }
    }

    private fun getResourceIdForCard(card: Card): Int {
        val suit = card.suit.name.lowercase()
        val rank = card.rank.name.lowercase()
        val resourceName = "${suit}_${rank}"
        return resources.getIdentifier(resourceName, "drawable", context.packageName)
    }
}
