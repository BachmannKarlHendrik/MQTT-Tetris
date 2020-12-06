package com.example.tetris

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameCanvas(context: Context, attrs: AttributeSet) : View(context, attrs) {

    val TAG = "MqttActivity"

    var isPaused = false
    var score = 0
    var activeBlockNr = 0
    var activeBlockCoords = arrayOf(0, 4)
    var paintArray: ArrayList<Paint> = ArrayList()

    private val pixelNrWidth = 10F
    private val pixelNrHeight = 16F
    var matrix = Array(pixelNrHeight.toInt()) {Array(pixelNrWidth.toInt()) {0} }



    private fun getRandPaint(): Paint {
        return Paint().apply {
            color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        }
    }

    private val bluePaint = Paint().apply {
        color = Color.BLUE
        textSize = 100F
    }

    private val borderPaint = Paint().apply {
        color = Color.DKGRAY
        style = Paint.Style.STROKE
        strokeWidth = 15F
    }

    fun scalX(value: Int): Int {
        return ((value.toFloat()/pixelNrWidth) * width).toInt()
    }

    fun scalY(value: Int): Int {
        return ((value.toFloat()/pixelNrHeight) * height).toInt()
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        launchGameloop()
        newPiece()
        super.onLayout(changed, left, top, right, bottom)
    }

    private fun newPiece() {
        activeBlockNr += 1
        paintArray.add(getRandPaint())

        for (i in 0..1){
            for (j in 4..5){
                matrix[i][j] = activeBlockNr
            }

        }
    }




    private fun launchGameloop() {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            var counter = 0
            while (true){
                if(!isPaused) {
                    delay(50L) //So therefore we have 20 refreshes per second.
                    counter += 1
                    if(counter == 20){
                        counter = 0
                        score += 1
                        if (checkDown()) activeDown()
                        else newPiece()
                    }
                    invalidate()
                }

            }// loop
        }
    }



    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        var r: Rect
        var number: Int
        for (i in 0 until pixelNrHeight.toInt()) {
            for (j in 0 until pixelNrWidth.toInt()) {
                number = matrix[i][j]
                if (number != 0){
                    r = Rect(scalX(j), scalY(i), scalX(j+1), scalY(i+1))
                    canvas?.drawRect(r, paintArray[number-1])
                    canvas?.drawRect(r, borderPaint)
               }
            }
        }

        canvas?.drawText(score.toString(), 50F, 75F, bluePaint)
    }










    // ********************  REMOTE  ****************************


    fun remote(command: String) {
        if(!isPaused){ //So when the isPause is true, the block couldn't be moved.
            if (command == "Left"){
                if (checkLeft()) activeLeft()
            }
            if (command == "Right") {
                if (checkRight()) activeRight()
            }
            if (command == "Down") {
                if (checkDown()) {
                    activeDown()
                    score += 5
                }
            }
            if (command == "Up") {
                //if (canChange()) activeChange()
            }
        }
    }// remote

    // "Checks": Boolean functions, which return true/false depending of whether block can be moved to
    //           designated direction without interference, or not.

    private fun checkDown(): Boolean {
        if (matrix[15].contains(activeBlockNr)) return false
        var number: Int
        var downBlock: Int
        for (i in pixelNrHeight.toInt() - 1 downTo 0){
            for (j in 0 until pixelNrWidth.toInt()){
                number = matrix[i][j]
                if (number == activeBlockNr && i != pixelNrHeight.toInt() - 1){
                    downBlock = matrix[i+1][j]
                    if (downBlock != 0 && downBlock != activeBlockNr) return false
                }
            }
        }
        return true
    }

    private fun checkLeft(): Boolean {
        var number: Int
        var leftBlock: Int
        for (i in 0 until pixelNrHeight.toInt()){
            if (matrix[i][0] == activeBlockNr) return false
            for (j in 0 until pixelNrWidth.toInt()){
                number = matrix[i][j]
                if (number == activeBlockNr && j != 0){
                    leftBlock = matrix[i][j-1]
                    if (leftBlock != 0 && leftBlock != activeBlockNr) return false
                }
            }
        }
        return true
    }

    private fun checkRight(): Boolean {
        var number: Int
        var rightBlock: Int
        for (i in 0 until pixelNrHeight.toInt()){
            if (matrix[i][matrix[0].size - 1] == activeBlockNr) return false
            for (j in pixelNrWidth.toInt()-1 downTo 0){
                number = matrix[i][j]
                if (number == activeBlockNr && j != pixelNrWidth.toInt()-1){
                    rightBlock = matrix[i][j+1]
                    if (rightBlock != 0 && rightBlock != activeBlockNr) return false
                }
            }
        }
        return true
    }


    // "Actives": void functions, which move the current active block to the destignated direction.

    private fun activeDown() {
        var number: Int
        for (i in pixelNrHeight.toInt() - 1 downTo 0){
            for (j in 0 until pixelNrWidth.toInt()){
                number = matrix[i][j]
                if (number == activeBlockNr && i != pixelNrHeight.toInt() - 1){
                    matrix[i][j] = matrix[i+1][j]
                    matrix[i+1][j] = activeBlockNr
                }
            }
        }
    }

    private fun activeLeft() {
        var number: Int
        for (i in 0 until pixelNrHeight.toInt()){
            for (j in 0 until pixelNrWidth.toInt()){
                number = matrix[i][j]
                if (number == activeBlockNr && j != 0){
                    matrix[i][j] = matrix[i][j-1]
                    matrix[i][j-1] = activeBlockNr
                }
            }
        }
    }

    private fun activeRight() {
        var number: Int
        for (i in 0 until pixelNrHeight.toInt()){
            for (j in pixelNrWidth.toInt()-1 downTo 0){
                number = matrix[i][j]
                if (number == activeBlockNr && j != 9){
                    matrix[i][j] = matrix[i][j+1]
                    matrix[i][j+1] = activeBlockNr
                }
            }
        }
    }

}