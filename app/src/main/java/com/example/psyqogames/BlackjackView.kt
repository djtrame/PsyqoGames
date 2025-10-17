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
import com.example.psyqogames.Blackjack.Shoe

class BlackjackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var cardBitmap: Bitmap? = null
    private var cardScale: Float = 0.3f // 70% smaller
    private val shoe = Shoe(numberOfDecks = 1) // Using a Shoe with 1 deck for simulation

    private val cardBasePaint = Paint().apply {
        color = Color.WHITE
    }

    // This function now returns the drawn Card object
    fun drawRandomCard(): Card? {
        val card = shoe.drawCard()
        if (card != null) {
            val resourceId = getResourceIdForCard(card)
            if (resourceId != 0) {
                val originalBitmap = BitmapFactory.decodeResource(resources, resourceId)
                val newWidth = (originalBitmap.width * cardScale).toInt()
                val newHeight = (originalBitmap.height * cardScale).toInt()
                cardBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            }
        } else {
            // If shoe is empty after trying to draw, it means reset/shuffle happened, clear bitmap
            cardBitmap = null
        }
        invalidate() // Request a redraw
        return card // Return the drawn card
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the background for the full Blackjack table
        canvas.drawColor(Color.rgb(0, 100, 0))

//        // Draw the card and its white background
//        cardBitmap?.let {
//            // Draw the white backing rectangle first
//            canvas.drawRect(100f, 100f, 100f + it.width, 100f + it.height, cardBasePaint)
//            // Draw the card image on top of the white rectangle
//            canvas.drawBitmap(it, 100f, 100f, null)
        //}
    }

    private fun getResourceIdForCard(card: Card): Int {
        val suit = card.suit.name.lowercase()
        val rank = card.rank.name.lowercase()
        val resourceName = "${suit}_${rank}"
        return resources.getIdentifier(resourceName, "drawable", context.packageName)
    }
}
