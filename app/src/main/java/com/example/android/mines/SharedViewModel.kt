@file:Suppress("MemberVisibilityCanBePrivate")

package com.example.android.mines

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.android.mines.database.MinesDatabaseDao
import com.example.android.mines.database.MinesDatum
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.lang.StringBuilder

class SharedViewModel: ViewModel() {

    /**
     * List of the [SectorContent] objects which make up our current game.
     */
    lateinit var gameState: MutableList<SectorContent>

    /**
     * Each entry in this list decides whether the corresponding [SectorContent] in our [gameState]
     * current game board list has a Mine in it or not: true if it has a mine, false if it does not.
     */
    lateinit var haveMines: MutableList<Boolean>

    /**
     * Number of columns in our game board GridLayout.
     */
    var numColumns: Int = 0

    /**
     * Number of rows in our game board GridLayout.
     */
    var numRows: Int = 0

    /**
     * Total number of sectors in our game board GridLayout ([numColumns] times [numRows]).
     */
    var numSectors: Int = 0

    /**
     * Number of sectors in our game board GridLayout which have a Mine in them
     */
    var numMines: Int = 0

    /**
     * Total number of sectors on our game board which the user has correctly marked as Safe or Mine
     */
    var numChecked: Int = 0

    /**
     * Total number of sectors on our game board which the user has correctly marked as Safe
     */
    var numCheckedSafe: Int = 0

    /**
     * Total number of sectors on our game board which the user has correctly marked as Mine
     */
    var numCheckedMine: Int = 0

    /**
     * This is the top "y" coordinate of the button in the ChooseFragment layout, which we then use
     * in the GameFragment to decide how big the GridLayout it uses for the board can be (a bit of
     * a kludge, but the height of a button is not readily known until it has been drawn as far as
     * I could see).
     */
    var buttonTop: Int = 0

    /**
     * Milliseconds since boot when the current game started to be played.
     */
    var startTime: Long = 0
    var modeMine: Boolean = false
    var modeSafe: Boolean = true
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var minesDatabaseDao: MinesDatabaseDao? = null // These two lines are iffy
    var gameHistory: LiveData<List<MinesDatum>>? = null

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

    fun toMinesDatum(): MinesDatum {
        val minesDatum = MinesDatum(
            numColumns = numColumns,
            numRows = numRows,
            numMines = numMines,
            elapsedTimeMilli = SystemClock.elapsedRealtime() - startTime,
            haveMines = stringEncodeHaveMines()
        )
        uiScope.launch {
            insertMinesDatum(minesDatum)
        }
        return minesDatum
    }

    private suspend fun insertMinesDatum(datum: MinesDatum) {
        withContext(Dispatchers.IO) {
            minesDatabaseDao?.insert(datum)
        }
    }

    fun stringEncodeHaveMines(): String {
        val stringBuilder = StringBuilder(haveMines.size)
        for (sectorHasMine in haveMines) {
            when (sectorHasMine) {
                true -> stringBuilder.append('*')
                false -> stringBuilder.append(' ')
            }
        }
        return stringBuilder.toString()
    }

    suspend fun retrieveLatestDatum(): MinesDatum {
        return withContext(Dispatchers.IO) {
            minesDatabaseDao!!.getLatestEntry()!!
        }
    }
}