package com.example.tetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class GameCanvas(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val bluePaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 15f
    }

    var number = 0
    var isPaused = false;


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        launchGameloop()
        super.onLayout(changed, left, top, right, bottom)
    }

    private fun launchGameloop() {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            var counter = 0
            while (true){
                if(!isPaused) {
                    invalidate()
                    delay(40L) //So therefore we have 25 refreshes per second.
                    if(counter == 25){ //That means that after every second a blockStep will be triggered,
                                        //even though the canvas still refreshes 25 times per second.
                        counter = 0
                        blockStep()
                    }
                }
            }
        }
    }

    private fun blockStep() {
        //TODO Move block down one row or make new block fall.
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawText(number.toString(), 50F, 50F,bluePaint) //To test gameloop
        number++
        //draw
    }

    fun remote(command: String) {
        if(!isPaused){ //So when the isPause is true, the block couldn't be moved.
            //TODO Parse command
        }
    }
}