@file:Suppress("MemberVisibilityCanBePrivate")

package com.example.android.mines

import androidx.lifecycle.ViewModel

class SharedViewModel: ViewModel() {

    lateinit var gameState: MutableList<SectorContent>
    lateinit var haveMines: MutableList<Boolean>
    var zeroMinedNeighbors: MutableList<Int>? = null
    var numColumns: Int = 0
    var numRows: Int = 0
    var numSectors: Int = 0
    var numMines: Int = 0
    var numChecked: Int = 0
    var numCheckedSafe: Int = 0
    var numCheckedMine: Int = 0
    var buttonTop: Int = 0
    var startTime: Long = 0
    var modeMine: Boolean = false
    var modeSafe: Boolean = true

    fun init(columnCount: Int, rowCount: Int, mines: Int) {
        numColumns = columnCount
        numRows = rowCount
        numSectors = columnCount * rowCount
        numMines = mines
        numChecked = 0
        numCheckedSafe = 0
        numCheckedMine = 0
        modeMine = false
        modeSafe = true

        gameState = ArrayList(numSectors)
        haveMines = ArrayList(numSectors)
        zeroMinedNeighbors = null
        for (index in 0 until numMines) {
            haveMines.add(true)
        }
        for (index in numMines until numSectors) {
            haveMines.add(false)
        }
        haveMines.shuffle()
        var index = 0
        for (rowNumber in 0 until numRows) {
            for (columnNumber in 0 until numColumns) {
                val newSectorContent = SectorContent(rowNumber, columnNumber)
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