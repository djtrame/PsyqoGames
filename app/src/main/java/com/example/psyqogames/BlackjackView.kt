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
import com.example.psyqogames.Blackjack.Deck

class BlackjackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var cardBitmap: Bitmap? = null
    private var cardScale: Float = 0.3f // 70% smaller
    private val deck = Deck()

    private val cardBasePaint = Paint().apply {
        color = Color.WHITE
    }

    fun drawRandomCard() {
        val card = deck.drawCard()
        if (card != null) {
            val resourceId = getResourceIdForCard(card)
            if (resourceId != 0) {
                val originalBitmap = BitmapFactory.decodeResource(resources, resourceId)
                val newWidth = (originalBitmap.width * cardScale).toInt()
                val newHeight = (originalBitmap.height * cardScale).toInt()
                cardBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            }
        } else {
            // Optionally, handle the case where the deck is empty
            deck.reset()
            cardBitmap = null
        }
        invalidate() // Request a redraw
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the background
        canvas.drawColor(Color.rgb(0, 100, 0))

        // Draw the card and its white background
        cardBitmap?.let { bitmap ->
            // Draw the white backing rectangle first
            canvas.drawRect(100f, 100f, 100f + bitmap.width, 100f + bitmap.height, cardBasePaint)
            // Draw the card image on top of the white rectangle
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
