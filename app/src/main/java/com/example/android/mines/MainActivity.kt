package com.example.android.mines

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private lateinit var board: GridLayout
    private lateinit var good: Button
    private lateinit var mine: Button
    private var screenWidth: Int = 0
    private var screenHeight: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        board = findViewById(R.id.board_grid)
        good = findViewById(R.id.button_good)
        mine = findViewById(R.id.button_mine)
        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = resources.displayMetrics.heightPixels - 200
        createBoard(this)
    }

    @Suppress("UNUSED_PARAMETER")
    private fun createBoard(context: Context): View {

        board.columnCount = COLUMN_COUNT
        board.rowCount = ROW_COUNT

        val cellWidth = screenWidth / COLUMN_COUNT
        val cellHeight = screenHeight / ROW_COUNT
        val cellSize = if (cellWidth < cellHeight) {
            cellWidth
        } else {
            cellHeight
        }

        for (row in 1..ROW_COUNT) {
            for (column in 1..COLUMN_COUNT) {
                val textView = TextView(this)
                textView.width = cellSize
                textView.height = cellSize
                textView.setPadding(4, 4, 4, 4)
                textView.text = "($row,$column)"
                textView.setOnClickListener {
                    it.setBackgroundColor(Color.GRAY)
                }
                board.addView(textView)
            }
        }

        return board
    }

    companion object {
        const val COLUMN_COUNT = 8
        const val ROW_COUNT = 8
    }
}
