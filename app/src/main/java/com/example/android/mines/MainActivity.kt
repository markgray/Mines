package com.example.android.mines

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout

class MainActivity : AppCompatActivity() {

    private lateinit var board : GridLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        board = findViewById(R.id.board_grid)
        createBoard()
    }

    @SuppressLint("SetTextI18n")
    private fun createBoard(): View {

        board.columnCount = COLUMN_COUNT
        board.rowCount = ROW_COUNT

        for (column in 1..COLUMN_COUNT) {
            for (row in 1..ROW_COUNT) {
                val textView = TextView(this)
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
