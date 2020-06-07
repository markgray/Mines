@file:Suppress("MemberVisibilityCanBePrivate")

package com.example.android.mines

import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.android.mines.database.MinesDatabaseDao
import com.example.android.mines.database.MinesDatum
import kotlinx.coroutines.*

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

    /**
     * The user is marking Mine sectors at the moment
     */
    var modeMine: Boolean = false

    /**
     * The user is marking Safe sectors at the moment
     */
    var modeSafe: Boolean = true

    /**
     * The [CompletableJob] we can use to cancel any outstanding coroutines in our [onCleared]
     * override (I did not bother to implement [onCleared] because this is the parent activity's
     * [ViewModel] and will be destroyed when the parent activity is destroyed (I think))
     */
    private var viewModelJob = Job()

    /**
     * [CoroutineScope] used to launch our [insertMinesDatum] suspend method to insert the
     * [MinesDatum] version of the current game into our database by our [toMinesDatum] method.
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * The [MinesDatabaseDao] we use to access our database. It is initialized by `ChooseFragment`
     * in its `onActivityCreated` override to the [MinesDatabaseDao] to use to access the table
     * "mines_game_history" in our database if it is still null at that point.
     */
    var minesDatabaseDao: MinesDatabaseDao? = null

    /**
     * This is the [LiveData] holding the results of calling the `getAllGames` method of our
     * [MinesDatabaseDao] field [minesDatabaseDao] to read the entire contents of our database
     * sorted in ascending order by the "game_elapsed_time" column. It is updated automatically
     * by ROOM, initialized in the `onActivityCreated` override of `ChooseFragment` if it is
     * still null at that point, and observed by a lambda created in the `onCreateView` override
     * of `ScoreFragment` in order to update the data of the adapter of the RecyclerView displaying
     * the game history.
     */
    var gameHistory: LiveData<List<MinesDatum>>? = null

    /**
     * This is called to initialize our game state to a game with [columnCount] columns, [rowCount]
     * rows, and [mines] Mines randomly placed on the board. We begin by using our parameters to
     * initialize the basic properties of our game board. We allocate an [ArrayList] which will
     * hold [numSectors] individual [SectorContent] entries for our [gameState] property, and an
     * [ArrayList] which will hold [numSectors] individual [Boolean] entries for our [haveMines]
     * property. We fill the first [numMines] entries of [haveMines] with the value *true* and the
     * rest with the value *false*. We then shuffle [haveMines] to randomly position the *true*
     * values in the list. Now we populate the game board in [gameState] with [SectorContent]
     * objects for each sector on the board. To do this we initialize our [Int] variable `var index`
     * to 0 then loop over `rowNumber` for the [numRows] rows and over `columnNumber` for the
     * [numColumns] columns constructing a [SectorContent] for variable `val newSectorContent`
     * located at `rowNumber` and `columnNumber` then setting its `childNum` property to `index`,
     * its `hasMine` property to the [Boolean] stored at location `index` in [haveMines], and
     * calling our [neighborSearch] method to have it set its `neighbors` property to a list
     * of all the the indices of the adjacent sectors of that location. We then add the
     * `newSectorContent` to our [gameState] list and increment `index`.
     *
     * @param columnCount number of columns on our game board
     * @param rowCount number of rows on our game board
     * @param mines number of Mines to randomly place on the game board
     */
    fun randomGame(columnCount: Int, rowCount: Int, mines: Int) {
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

    /**
     * Calculates the indices of sectors which are adjacent to our [SectorContent] parameter
     * [theSectorContent] and adds them to the `neighbors` property of [theSectorContent].
     * This we do using a complex chain of if statements:
     *
     *  - If the `column` of [theSectorContent] is greater than 0 we add the index of the sector
     *  immediately before this sector.
     *
     *  - If the `column` of [theSectorContent] is less than the last column of the board we add
     *  the index of the sector immediately following this sector.
     *
     *  - If the `row` of [theSectorContent] is greater than 0 we initialize `val precedingChildNum`
     *  to the index of the sector directly above this sector and add it that index. If the
     *  `column` of [theSectorContent] is greater than 0 we add the index of the sector immediately
     *  before `precedingChildNum`, and if the `column` of [theSectorContent] is less than the last
     *  column we add the index of the sector immediately after this `precedingChildNum`.
     *
     *  - If the `row` of [theSectorContent] is less than the last row of the board we initialize
     *  `val followingChildNum` to the index of the sector directly below this sector and add that
     *  index. If the `column` of [theSectorContent] is greater than 0 we add the index of the
     *  sector immediately before `followingChildNum`, and if the `column` of [theSectorContent] is
     *  less than the last column we add the index of the sector immediately after this
     *  `followingChildNum`.
     *
     * @param theSectorContent the [SectorContent] whose adjacent sectors we are searching for.
     */
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