package com.example.tetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import kotlin.math.abs

class GameCanvas(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val bluePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 15f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        //draw
    }
}