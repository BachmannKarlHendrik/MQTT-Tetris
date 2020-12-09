package com.example.tetris.activity

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Button
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class GameCanvas(context: Context, attrs: AttributeSet) : View(context, attrs) {

    val TAG = "Game"

    var isPaused = false
    var gameOver = false
    var score = 0

    var activeBlockNr = 0
    var activeBlockCoords = arrayOf(0, 4)
    var activeBlock = ""
    var mutation = ""
    val startBlockArray = arrayOf("I", "O", "T", "J", "L", "S", "Z")
    var paintArray: ArrayList<Paint> = ArrayList()
    lateinit var gameOverBtn: Button

    fun sendButton(button: Button) {
        gameOverBtn = button
    }

    private val pixelNrWidth = 10
    private val pixelNrHeight = 16
    var matrix = Array(pixelNrHeight) { Array(pixelNrWidth) { 0 } }

    private val gridPaint = Paint().apply {
        color = Color.LTGRAY
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

    private fun getRandPaint(): Paint {
        return Paint().apply {
            color = Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
        }
    }

    fun scalX(value: Int): Int {
        return ((value.toFloat() / pixelNrWidth.toFloat()) * width).toInt()
    }

    fun scalY(value: Int): Int {
        return ((value.toFloat() / pixelNrHeight.toFloat()) * height).toInt()
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        launchGameloop()
        super.onLayout(changed, left, top, right, bottom)
    }

    private fun launchGameloop() {
        val scope = CoroutineScope(Dispatchers.Default)
        scope.launch {
            var counter = 0
            while (!gameOver) {
                if (!isPaused) {
                    delay(50L) //So therefore we have 20 refreshes per second.
                    counter += 1
                    if (counter == 20) {
                        counter = 0
                        score += 1
                        if (checkDown()) activeDown()
                        else {
                            activeBlockNr += 1
                            scoringSequence()
                            newPiece()
                        }
                    }
                    invalidate()
                }
            }// loop
        }
    }

    private fun scoringSequence() {
        var count = 0
        var added = 0
        for (i in 0 until pixelNrHeight) {
            if (!matrix[i].contains(0)) {
                count += 1
                added += count
                for (j in i downTo 0) {
                    if (j == 0) matrix[j] = Array(pixelNrWidth) { 0 }
                    else matrix[j] = matrix[j - 1]
                }
            }
        }
        score += added * 100
    }

    private fun newPiece() {
        paintArray.add(getRandPaint())
        val newBlock = startBlockArray[Random.nextInt(0, startBlockArray.size)]

        if (newBlock == "I") {
            if (matrix[0][4] != 0 || matrix[0][5] != 0 || matrix[0][6] != 0 || matrix[0][7] != 0) {
                gameOver = true
                if (isEmptyRow(matrix[0])) { //So that the shape could be drawn partially even though game is over.
                    matrix[0][4] = activeBlockNr
                    matrix[0][5] = activeBlockNr
                    matrix[0][6] = activeBlockNr
                    matrix[0][7] = activeBlockNr
                }
            } else {
                mutation = "I1"
                matrix[0][4] = activeBlockNr
                matrix[0][5] = activeBlockNr
                matrix[0][6] = activeBlockNr
                matrix[0][7] = activeBlockNr
            }
        } else if (newBlock == "O") {
            if (matrix[0][4] != 0 || matrix[0][5] != 0 || matrix[1][4] != 0 || matrix[1][5] != 0) {
                gameOver = true
                if (isEmptyRow(matrix[0])) {
                    matrix[0][4] = activeBlockNr
                    matrix[0][5] = activeBlockNr
                }
                if (isEmptyRow(matrix[1])) { //Shape is drawn partially.
                    matrix[1][4] = activeBlockNr
                    matrix[1][5] = activeBlockNr
                }
            } else {
                mutation = "O"
                matrix[0][4] = activeBlockNr
                matrix[0][5] = activeBlockNr
                matrix[1][4] = activeBlockNr
                matrix[1][5] = activeBlockNr
            }
        } else if (newBlock == "T") {
            if (matrix[0][5] != 0 || matrix[1][4] != 0 || matrix[1][5] != 0 || matrix[1][6] != 0) {
                gameOver = true
                if (isEmptyRow(matrix[0])) {
                    matrix[0][5] = activeBlockNr
                }
                if (isEmptyRow(matrix[1])) {
                    matrix[1][4] = activeBlockNr
                    matrix[1][5] = activeBlockNr
                    matrix[1][6] = activeBlockNr
                }
            } else {
                mutation = "T1"
                matrix[0][5] = activeBlockNr
                matrix[1][4] = activeBlockNr
                matrix[1][5] = activeBlockNr
                matrix[1][6] = activeBlockNr
            }
        } else if (newBlock == "J") {
            if (matrix[0][5] != 0 || matrix[1][5] != 0 || matrix[2][5] != 0 || matrix[2][4] != 0) {
                gameOver = true
                if (isEmptyRow(matrix[0])) {
                    matrix[0][5] = activeBlockNr
                }
                if (isEmptyRow(matrix[1])) {
                    matrix[1][5] = activeBlockNr
                }
                if (isEmptyRow(matrix[2])) {
                    matrix[2][5] = activeBlockNr
                    matrix[2][4] = activeBlockNr
                }
            } else {
                mutation = "J1"
                matrix[0][5] = activeBlockNr
                matrix[1][5] = activeBlockNr
                matrix[2][5] = activeBlockNr
                matrix[2][4] = activeBlockNr
            }
        } else if (newBlock == "L") {
            if (matrix[0][5] != 0 || matrix[1][5] != 0 || matrix[2][5] != 0 || matrix[2][6] != 0) {
                gameOver = true
                if (isEmptyRow(matrix[0])) {
                    matrix[0][5] = activeBlockNr
                }
                if (isEmptyRow(matrix[1])) {
                    matrix[1][5] = activeBlockNr
                }
                if (isEmptyRow(matrix[2])) {
                    matrix[2][5] = activeBlockNr
                    matrix[2][6] = activeBlockNr
                }
            } else {
                mutation = "L1"
                matrix[0][5] = activeBlockNr
                matrix[1][5] = activeBlockNr
                matrix[2][5] = activeBlockNr
                matrix[2][6] = activeBlockNr
            }
        } else if (newBlock == "S") {
            if (matrix[0][5] != 0 || matrix[0][6] != 0 || matrix[1][4] != 0 || matrix[1][5] != 0) {
                gameOver = true
                if (isEmptyRow(matrix[0])) {
                    matrix[0][5] = activeBlockNr
                    matrix[0][6] = activeBlockNr
                }
                if (isEmptyRow(matrix[1])) {
                    matrix[1][4] = activeBlockNr
                    matrix[1][5] = activeBlockNr
                }
            } else {
                mutation = "S1"
                matrix[0][5] = activeBlockNr
                matrix[0][6] = activeBlockNr
                matrix[1][4] = activeBlockNr
                matrix[1][5] = activeBlockNr
            }
        } else if (newBlock == "Z") {
            if (matrix[0][4] != 0 || matrix[0][5] != 0 || matrix[1][5] != 0 || matrix[1][6] != 0) {
                gameOver = true
                if (isEmptyRow(matrix[0])) {
                    matrix[0][4] = activeBlockNr
                    matrix[0][5] = activeBlockNr
                }
                if (isEmptyRow(matrix[1])) {
                    matrix[1][5] = activeBlockNr
                    matrix[1][6] = activeBlockNr
                }
            } else {
                mutation = "Z1"
                matrix[0][4] = activeBlockNr
                matrix[0][5] = activeBlockNr
                matrix[1][5] = activeBlockNr
                matrix[1][6] = activeBlockNr
            }
        }
        Log.i(TAG, Arrays.toString(matrix[0]))
        activeBlock = newBlock
        activeBlockCoords[0] = 0
        activeBlockCoords[1] = 4
    }

    private fun isEmptyRow(ints: Array<Int>): Boolean { //Checks if that specific row in matrix is empty.
        for (i in ints) {
            if (i != 0) {
                return false
            }
        }
        return true
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (i in 0 until pixelNrWidth) { //Draw the grid
            canvas?.drawLine(
                scalX(i).toFloat(),
                0F,
                scalX(i).toFloat(),
                height.toFloat(),
                gridPaint
            )
        }
        for (i in 0 until pixelNrHeight) {
            canvas?.drawLine(0F, scalY(i).toFloat(), width.toFloat(), scalY(i).toFloat(), gridPaint)
        }

        var r: Rect
        var number: Int
        for (i in 0 until pixelNrHeight) { //Draw all the blocks
            for (j in 0 until pixelNrWidth) {
                number = matrix[i][j]
                if (number != 0) {
                    r = Rect(scalX(j), scalY(i), scalX(j + 1), scalY(i + 1))
                    canvas?.drawRect(r, paintArray[number - 1])
                    canvas?.drawRect(r, borderPaint)
                }
            }
        }
        canvas?.drawText("Score:", 50F, 95F, bluePaint) //Draw score
        canvas?.drawText(score.toString(), 50F, 200F, bluePaint)
        if (gameOver) {
            gameOverBtn.performClick()
        }
    }


    fun remote(command: String) {
        if (!isPaused) { //So when the isPause is true, the block couldn't be moved.
            if (command == "Left") {
                if (checkLeft()) activeLeft()
            } else if (command == "Right") {
                if (checkRight()) activeRight()
            } else if (command == "Down") {
                if (checkDown()) {
                    activeDown()
                    score += 2
                }
            } else if (command == "Up") {
                if (checkRotation()) activeRotate()
            }
        }
    }// remote


    /*
          ***************************  BLOCK MUTATION FUNCTIONS  ****************************
    */


    // "Checks": Boolean functions, which return true/false depending of whether block can be mutated
    //           designated way, or not.

    private fun checkDown(): Boolean {
        if (matrix[15].contains(activeBlockNr)) return false
        var number: Int
        var downBlock: Int
        for (i in pixelNrHeight - 1 downTo 0) {
            for (j in 0 until pixelNrWidth) {
                number = matrix[i][j]
                if (number == activeBlockNr && i != pixelNrHeight - 1) {
                    downBlock = matrix[i + 1][j]
                    if (downBlock != 0 && downBlock != activeBlockNr) return false
                }
            }
        }
        return true
    }

    private fun checkLeft(): Boolean {
        var number: Int
        var leftBlock: Int
        for (i in 0 until pixelNrHeight) {
            if (matrix[i][0] == activeBlockNr) return false
            for (j in 0 until pixelNrWidth) {
                number = matrix[i][j]
                if (number == activeBlockNr && j != 0) {
                    leftBlock = matrix[i][j - 1]
                    if (leftBlock != 0 && leftBlock != activeBlockNr) return false
                }
            }
        }
        return true
    }

    private fun checkRight(): Boolean {
        var number: Int
        var rightBlock: Int
        for (i in 0 until pixelNrHeight) {
            if (matrix[i][matrix[0].size - 1] == activeBlockNr) return false
            for (j in pixelNrWidth - 1 downTo 0) {
                number = matrix[i][j]
                if (number == activeBlockNr && j != pixelNrWidth - 1) {
                    rightBlock = matrix[i][j + 1]
                    if (rightBlock != 0 && rightBlock != activeBlockNr) return false
                }
            }
        }
        return true
    }

    private fun checkRotation(): Boolean {
        val y = activeBlockCoords[1]
        val x = activeBlockCoords[0]
        if (activeBlock == "I") {
            if (x > 12 || y > 6) return false
            if (mutation == "I1") {
                if (matrix[x + 1][y] == 0 && matrix[x + 2][y] == 0 && matrix[x + 3][y] == 0) {
                    return true
                }
            } else if (mutation == "I2") {
                if (matrix[x][y + 1] == 0 && matrix[x][y + 2] == 0 && matrix[x][y + 3] == 0) {
                    return true
                }
            }
        }
        //Because all other blocks fit into 3x3 cube, efficient
        if (x > 13 || y > 7) return false
        if (activeBlock == "T") {
            if (mutation == "T1") {
                if (matrix[x + 2][y + 1] == 0) return true
            } else if (mutation == "T2") {
                if (matrix[x + 1][y + 2] == 0) return true
            } else if (mutation == "T3") {
                if (matrix[x][y + 1] == 0) return true
            } else if (mutation == "T4") {
                if (matrix[x + 1][y] == 0) return true
            }
        }
        if (activeBlock == "J") {
            if (mutation == "J1") {
                if (matrix[x][y] == 0 && matrix[x + 1][y] == 0 && matrix[x + 1][y + 2] == 0) return true
            } else if (mutation == "J2") {
                if (matrix[x][y + 1] == 0 && matrix[x][y + 2] == 0 && matrix[x + 2][y + 1] == 0) return true
            } else if (mutation == "J3") {
                if (matrix[x + 1][y] == 0 && matrix[x + 1][y + 2] == 0 && matrix[x + 2][y + 2] == 0) return true
            } else if (mutation == "J4") {
                if (matrix[x][y + 1] == 0 && matrix[x + 2][y] == 0 && matrix[x + 2][y + 1] == 0) return true
            }
        }
        if (activeBlock == "L") {
            if (mutation == "L1") {
                if (matrix[x + 1][y] == 0 && matrix[x + 1][y + 2] == 0 && matrix[x + 2][y] == 0) return true
            } else if (mutation == "L2") {
                if (matrix[x][y] == 0 && matrix[x][y + 1] == 0 && matrix[x + 2][y + 1] == 0) return true
            } else if (mutation == "L3") {
                if (matrix[x][y + 2] == 0 && matrix[x + 1][y] == 0 && matrix[x + 1][y + 2] == 0) return true
            } else if (mutation == "L4") {
                if (matrix[x][y + 1] == 0 && matrix[x + 2][y + 1] == 0 && matrix[x + 2][y + 2] == 0) return true
            }
        }
        if (activeBlock == "S") {
            if (mutation == "S1") {
                if (matrix[x][y] == 0 && matrix[x + 2][y + 1] == 0) return true
            } else if (mutation == "S2") {
                if (matrix[x][y + 1] == 0 && matrix[x][y + 2] == 0) return true
            }
        }
        if (activeBlock == "Z") {
            if (mutation == "Z1") {
                if (matrix[x + 1][y] == 0 && matrix[x + 2][y] == 0) return true
            } else if (mutation == "Z2") {
                if (matrix[x][y] == 0 && matrix[x + 1][y + 2] == 0) return true
            }
        }
        // No turn for "O" block, because it's not needed
        return false
    }


    // "Actives": void functions, which mutate the current active block designated way.

    private fun activeDown() {
        var number: Int
        for (i in pixelNrHeight - 1 downTo 0) {
            for (j in 0 until pixelNrWidth) {
                number = matrix[i][j]
                if (number == activeBlockNr && i != pixelNrHeight - 1) {
                    matrix[i][j] = matrix[i + 1][j]
                    matrix[i + 1][j] = activeBlockNr
                }
            }
        }
        activeBlockCoords[0] += 1
    }

    private fun activeLeft() {
        var number: Int
        for (i in 0 until pixelNrHeight) {
            for (j in 0 until pixelNrWidth) {
                number = matrix[i][j]
                if (number == activeBlockNr && j != 0) {
                    matrix[i][j] = matrix[i][j - 1]
                    matrix[i][j - 1] = activeBlockNr
                }
            }
        }
        activeBlockCoords[1] -= 1
    }

    private fun activeRight() {
        var number: Int
        for (i in 0 until pixelNrHeight) {
            for (j in pixelNrWidth - 1 downTo 0) {
                number = matrix[i][j]
                if (number == activeBlockNr && j != 9) {
                    matrix[i][j] = matrix[i][j + 1]
                    matrix[i][j + 1] = activeBlockNr
                }
            }
        }
        activeBlockCoords[1] += 1
    }

    private fun activeRotate() {
        val y = activeBlockCoords[1]
        val x = activeBlockCoords[0]
        if (activeBlock == "I") {
            if (mutation == "I1") {
                mutation = "I2"
                matrix[x + 1][y] = activeBlockNr
                matrix[x + 2][y] = activeBlockNr
                matrix[x + 3][y] = activeBlockNr
                matrix[x][y + 1] = 0
                matrix[x][y + 2] = 0
                matrix[x][y + 3] = 0
            } else if (mutation == "I2") {
                mutation = "I1"
                matrix[x][y + 1] = activeBlockNr
                matrix[x][y + 2] = activeBlockNr
                matrix[x][y + 3] = activeBlockNr
                matrix[x + 1][y] = 0
                matrix[x + 2][y] = 0
                matrix[x + 3][y] = 0
            }
        } else if (activeBlock == "T") {
            if (mutation == "T1") {
                mutation = "T2"
                matrix[x + 2][y + 1] = activeBlockNr
                matrix[x + 1][y + 2] = 0
            } else if (mutation == "T2") {
                mutation = "T3"
                matrix[x + 1][y + 2] = activeBlockNr
                matrix[x][y + 1] = 0
            } else if (mutation == "T3") {
                mutation = "T4"
                matrix[x][y + 1] = activeBlockNr
                matrix[x + 1][y] = 0
            } else if (mutation == "T4") {
                mutation = "T1"
                matrix[x + 1][y] = activeBlockNr
                matrix[x + 2][y + 1] = 0
            }

        } else if (activeBlock == "J") {
            if (mutation == "J1") {
                mutation = "J2"
                matrix[x][y] = activeBlockNr
                matrix[x + 1][y] = activeBlockNr
                matrix[x + 1][y + 2] = activeBlockNr
                matrix[x][y + 1] = 0
                matrix[x + 2][y] = 0
                matrix[x + 2][y + 1] = 0
            } else if (mutation == "J2") {
                mutation = "J3"
                matrix[x][y + 1] = activeBlockNr
                matrix[x][y + 2] = activeBlockNr
                matrix[x + 2][y + 1] = activeBlockNr
                matrix[x][y] = 0
                matrix[x + 1][y] = 0
                matrix[x + 1][y + 2] = 0
            } else if (mutation == "J3") {
                mutation = "J4"
                matrix[x + 1][y] = activeBlockNr
                matrix[x + 1][y + 2] = activeBlockNr
                matrix[x + 2][y + 2] = activeBlockNr
                matrix[x][y + 1] = 0
                matrix[x][y + 2] = 0
                matrix[x + 2][y + 1] = 0
            } else if (mutation == "J4") {
                mutation = "J1"
                matrix[x][y + 1] = activeBlockNr
                matrix[x + 2][y] = activeBlockNr
                matrix[x + 2][y + 1] = activeBlockNr
                matrix[x + 1][y] = 0
                matrix[x + 1][y + 2] = 0
                matrix[x + 2][y + 2] = 0
            }

        } else if (activeBlock == "L") {
            if (mutation == "L1") {
                mutation = "L2"
                matrix[x + 1][y] = activeBlockNr
                matrix[x + 1][y + 2] = activeBlockNr
                matrix[x + 2][y] = activeBlockNr
                matrix[x][y + 1] = 0
                matrix[x + 2][y + 1] = 0
                matrix[x + 2][y + 2] = 0
            } else if (mutation == "L2") {
                mutation = "L3"
                matrix[x][y] = activeBlockNr
                matrix[x][y + 1] = activeBlockNr
                matrix[x + 2][y + 1] = activeBlockNr
                matrix[x + 1][y] = 0
                matrix[x + 1][y + 2] = 0
                matrix[x + 2][y] = 0
            } else if (mutation == "L3") {
                mutation = "L4"
                matrix[x][y + 2] = activeBlockNr
                matrix[x + 1][y] = activeBlockNr
                matrix[x + 1][y + 2] = activeBlockNr
                matrix[x][y] = 0
                matrix[x][y + 1] = 0
                matrix[x + 2][y + 1] = 0
            } else if (mutation == "L4") {
                mutation = "L1"
                matrix[x][y + 1] = activeBlockNr
                matrix[x + 2][y + 1] = activeBlockNr
                matrix[x + 2][y + 2] = activeBlockNr
                matrix[x][y + 2] = 0
                matrix[x + 1][y] = 0
                matrix[x + 1][y + 2] = 0
            }
        } else if (activeBlock == "S") {
            if (mutation == "S1") {
                mutation = "S2"
                matrix[x][y] = activeBlockNr
                matrix[x + 2][y + 1] = activeBlockNr
                matrix[x][y + 1] = 0
                matrix[x][y + 2] = 0
            } else if (mutation == "S2") {
                mutation = "S1"
                matrix[x][y + 1] = activeBlockNr
                matrix[x][y + 2] = activeBlockNr
                matrix[x][y] = 0
                matrix[x + 2][y + 1] = 0
            }
        } else if (activeBlock == "Z") {
            if (mutation == "Z1") {
                mutation = "Z2"
                matrix[x + 1][y] = activeBlockNr
                matrix[x + 2][y] = activeBlockNr
                matrix[x][y] = 0
                matrix[x + 1][y + 2] = 0
            } else if (mutation == "Z2") {
                mutation = "Z1"
                matrix[x][y] = activeBlockNr
                matrix[x + 1][y + 2] = activeBlockNr
                matrix[x + 1][y] = 0
                matrix[x + 2][y] = 0
            }
        }
    }

}