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

//    private var cardBitmap: Bitmap? = null
//    private var cardScale: Float = 0.3f // 70% smaller
//    private val shoe = Shoe(numberOfDecks = 1) // Using a Shoe with 1 deck for simulation

    private val cardBasePaint = Paint().apply {
        color = Color.WHITE
    }

    // This function now returns the drawn Card object
//    fun drawRandomCard(): Card? {
//        val card = shoe.drawCard()
//
//        return card // Return the drawn card
//    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Draw the background for the full Blackjack table
        canvas.drawColor(Color.rgb(0, 100, 0))
    }
}
