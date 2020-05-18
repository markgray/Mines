@file:Suppress("MemberVisibilityCanBePrivate")

package com.example.android.mines

import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    lateinit var gameState: MutableList<SectorContent>
    lateinit var haveMines: MutableList<Boolean>
    var numColumns: Int = 0
    var numRows: Int = 0
    var numSectors: Int = 0
    var mineCount: Int = 0
    var startTime: Long = 0

    fun init(columnCount: Int, rowCount: Int, mines: Int) {
        numColumns = columnCount
        numRows = rowCount
        numSectors = columnCount * rowCount
        mineCount = mines
        gameState = ArrayList(numSectors)
        haveMines = ArrayList(numSectors)
        for (index in 0 until mineCount) {
            haveMines.add(true)
        }
        for (index in mineCount until numSectors) {
            haveMines.add(false)
        }
        haveMines.shuffle()
        var index = 0
        for (rows in 0 until numRows) {
            for (columns in 0 until numColumns) {
                val newSectorContent = SectorContent(rows, columns)
                newSectorContent.hasMine = haveMines[index++]
                gameState.add(newSectorContent)
            }
        }

    }
}