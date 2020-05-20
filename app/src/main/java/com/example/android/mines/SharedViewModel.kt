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
                newSectorContent.childNum = index
                newSectorContent.hasMine = haveMines[index]
                neighborSearch(newSectorContent)
                gameState.add(newSectorContent)
                index++
            }
        }
    }

    fun neighborSearch(theSectorContent: SectorContent) {
        if (theSectorContent.column > 0) {
            theSectorContent.neighbors.add(theSectorContent.childNum - 1)
        }
        if (theSectorContent.column < numColumns -1) {
            theSectorContent.neighbors.add(theSectorContent.childNum + 1)
        }
        if (theSectorContent.row > 0) {
            val precedingChildNum = theSectorContent.childNum - numColumns
            theSectorContent.neighbors.add(precedingChildNum)
            if (theSectorContent.column > 0) {
                theSectorContent.neighbors.add(precedingChildNum - 1)
            }
            if (theSectorContent.column < numColumns - 1) {
                theSectorContent.neighbors.add(precedingChildNum + 1)
            }
        }
        if (theSectorContent.row < numRows - 1) {
            val followingChildNum = theSectorContent.childNum + numColumns
            theSectorContent.neighbors.add(followingChildNum)
            if (theSectorContent.column > 0) {
                theSectorContent.neighbors.add(followingChildNum - 1)
            }
            if (theSectorContent.column < numColumns - 1) {
                theSectorContent.neighbors.add(followingChildNum + 1)
            }
        }
    }
}