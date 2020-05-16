package com.example.android.mines

import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    lateinit var gameState: MutableList<SectorContent>
    var numColumns: Int = 0
    var numRows: Int = 0
    var mineCount: Int = 0
    var startTime: Long = 0

    fun init(columnCount: Int, rowCount: Int, mines: Int) {
        numColumns = columnCount
        numRows = rowCount
        mineCount = mines
        gameState = ArrayList(numColumns * numRows)
        for (rows in 0 until numRows) {
            for (columns in 0 until numColumns) {
                gameState.add(SectorContent(rows, columns))
            }
        }

    }
}