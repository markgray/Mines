package com.example.android.mines

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    private lateinit var board : GridLayout
    private lateinit var good : Button
    private lateinit var mine : Button
    private var boardWidth : Int = 0
    private var boardHeight : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        board = findViewById(R.id.board_grid)
        good = findViewById(R.id.button_good)
        mine = findViewById(R.id.button_mine)
        boardWidth = resources.displayMetrics.widthPixels
        boardHeight = resources.displayMetrics.heightPixels - 200
        createBoard(this)
    }

    private fun createBoard(context: Context): View {

        board.columnCount = COLUMN_COUNT
        board.rowCount = ROW_COUNT

        val cellWidth = boardWidth / COLUMN_COUNT
        val cellHeight = boardHeight / ROW_COUNT

        for (column in 1..COLUMN_COUNT) {
            for (row in 1..ROW_COUNT) {
                val textView = TextView(this)
                textView.width = cellWidth
                textView.height = cellHeight
                textView.text = "$column,$row"
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
