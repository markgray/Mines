@file:Suppress("MemberVisibilityCanBePrivate")

package com.example.android.mines

import android.app.Application
import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.android.mines.database.MinesDatabaseDao
import com.example.android.mines.database.MinesDatum
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The [ViewModel] which is shared by all of our fragments. It contains almost all the information
 * that the fragments need, and almost all of their business logic as well.
 *
 * @param application the global [Application] instance for this app, supplied by `activityViewModels`
 * @param savedStateHandle a handle to saved state passed down to this [AndroidViewModel]. This is a
 * key-value map that will let you write and retrieve objects to and from the saved state. These
 * values will persist after the process is killed by the system and remain available via the same
 * object. You can read a value from it via [SavedStateHandle.get] or observe it via [LiveData]
 * returned by [SavedStateHandle.getLiveData]. You can write a value to it via [SavedStateHandle.set]
 * or setting a value to [MutableLiveData] returned by [SavedStateHandle.getLiveData].
 */
class SharedViewModel(
    application: Application,
    val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    companion object {
        /**
         * TAG used for logging.
         */
        @Suppress("unused")
        const val TAG: String = "SharedViewModel"
    }

    /**
     * The [Context] of our [Application].
     */
    private val context: Context
        get() = getApplication()

    /**
     * The [Narrator] instance that does our text to speech.
     */
    var narrator: Narrator? = Narrator(context)

    /**
     * Flag indicating whether the [Narrator.tellUser] method should be called by our [sayIt] method
     * or not. It is set to `false` by the `onClickListener` of the "CLICK FOR SILENT MODE" button
     * in the UI of `ChooseFragement` and to `true` by the same button when it is labeled "CLICK FOR
     * TALKATIVE MODE" (ie. the label of the button is toggled also).
     */
    var talkEnabled: Boolean = true

    /**
     * Convenience function for calling the [Narrator.tellUser] method of our [narrator] field if
     * and only if our [Boolean] property [talkEnabled] is `true`
     *
     * @param text the text that is to be spoken.
     */
    fun sayIt(text: String) {
        if (talkEnabled) {
            narrator?.tellUser(text)
        }
    }

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
     * Total number of sectors on our game board which the user has correctly marked as Mined
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
     * The user is marking Mined sectors at the moment
     */
    var modeMine: Boolean = false

    /**
     * The user is marking Safe sectors at the moment
     */
    var modeSafe: Boolean = true

    /**
     * The [CompletableJob] we can use to cancel any outstanding coroutines in our [onCleared]
     * override. There should not be any outstanding coroutines when [onCleared] is called as far
     * as I can see, but I leave this in just in case.
     */
    private var viewModelJob = Job()

    /**
     * [CoroutineScope] used to launch our [deleteMinesDatum] and [insertMinesDatum] suspend methods
     * to delete a [MinesDatum] by its [MinesDatum.gameId] and to insert the [MinesDatum] version of
     * the current game into our database by our [toMinesDatum] method respectively.
     */
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * The [MinesDatabaseDao] we use to access our database. It is initialized by `ChooseFragment`
     * in its `onViewStateRestored` override to the [MinesDatabaseDao] to use to access the table
     * "mines_game_history" in our database if it is still null at that point.
     */
    var minesDatabaseDao: MinesDatabaseDao? = null

    /**
     * The [MinesDatum] created by our [toMinesDatum] method from the latest game played. Note that
     * the [MinesDatum.gameId] property will be the invalid value 0, ROOM will automatically assign
     * the next unique ID for the table to the [MinesDatum] when it writes it to the database.
     */
    lateinit var latestDatum: MinesDatum

    /**
     * This is an experiment in the use of the [SavedStateHandle] our constructor is passed. This
     * [MutableLiveData] exposes the entry with the key "newest_id" to the UI
     */
    val newestID: MutableLiveData<Long> =
        savedStateHandle.getLiveData("newest_id")
    /**
     * This method saves its [Long] parameter [id] in our [SavedStateHandle] field [savedStateHandle]
     * under the key "newest_id".
     *
     * @param id the [Long] value to store in our [SavedStateHandle] field [savedStateHandle]
     * under the key "newest_id".
     */
    fun saveNewestId(id: Long) {
        savedStateHandle["newest_id"] = id
    }

    /**
     * This is the [LiveData] holding the results of calling the `getAllGames` method of our
     * [MinesDatabaseDao] field [minesDatabaseDao] to read the entire contents of our database
     * sorted in ascending order by the "game_elapsed_time" column. It is updated automatically
     * by ROOM, initialized in the `onViewStateRestored` override of `ChooseFragment` if it is
     * still null at that point. It is observed by a lambda created in the `onCreateView` override
     * of `ScoreFragment` in order to update the data of the adapter of its RecyclerView displaying
     * the game history, and it is observed by a lambda created in the `onCreateView` override
     * of `EditFragment` in order to update the data of the adapter of its RecyclerView displaying
     * the game history.
     */
    var gameHistory: LiveData<List<MinesDatum>>? = null

    /**
     * This is called to initialize our game state to a game with [columnCount] columns, [rowCount]
     * rows, and [mines] Mines randomly placed on the board. We begin by using our parameters to
     * initialize the basic properties of our game board:
     *  - our [numColumns] property is set to the parameter [columnCount]
     *  - our [numRows] property is set to the parameter [rowCount]
     *  - our [numSectors] property is set to the parameter [columnCount] times parameter [rowCount]
     *  - our [numMines] property is set to the parameter [mines]
     *  - our [numChecked] property is set to 0
     *  - our [numCheckedSafe] property is set to 0
     *  - our [numCheckedMine] property is set to 0
     *  - our [modeMine] property is set to `false`
     *  - our [modeSafe] property is set to `true`
     *
     * Next allocate an [ArrayList] which will hold [numSectors] individual [SectorContent] entries
     * for our [gameState] property, and an [ArrayList] which will hold [numSectors] individual
     * [Boolean] entries for our [haveMines] property. We fill the first [numMines] entries of
     * [haveMines] with the value `true` and the rest with the value `false`. We then shuffle
     * [haveMines] to randomly position the `true` values in the list.
     *
     * Now we populate the game board in [gameState] with [SectorContent] objects for each sector on
     * the board. To do this we initialize our [Int] variable `var index` to 0 then loop over
     * `rowNumber` for the [numRows] rows and over `columnNumber` for the [numColumns] columns
     * constructing a [SectorContent] for variable `val newSectorContent` located at `rowNumber` and
     * `columnNumber` then setting its `childNum` property to `index`, its `hasMine` property to the
     * [Boolean] stored at location `index` in [haveMines], and calling our [neighborSearch] method
     * to have it set its `neighbors` property to a list of all the the indices of the adjacent
     * sectors of that location. We then add the `newSectorContent` to our [gameState] list and
     * increment `index`.
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
     * Loads a game board from a [MinesDatum] which has been retrieved from our ROOM database. First
     * we set our game board geometry properties to those which were stored in our [MinesDatum]
     * parameter [minesDatum], and initialize our game state properties to the beginning state for a
     * game. We allocate an [ArrayList] of [SectorContent] objects for our field [gameState] large
     * enough to hold [numSectors] sectors, and an [ArrayList] of [Boolean] objects for our field
     * [haveMines] large enough to hold [numSectors] flags. We loop over `index` for all of the
     * character indices in the `haveMines` [String] of [minesDatum] adding `true` to [haveMines]
     * if the [Char] at index `index` is not a space character, and adding `false` if it is a
     * space character. Now we populate the game board in [gameState] with [SectorContent] objects
     * for each sector on the board. To do this we initialize our [Int] variable `var index` to 0
     * then loop over `rowNumber` for the [numRows] rows and over `columnNumber` for the [numColumns]
     * columns constructing a [SectorContent] for variable `val newSectorContent` located at
     * `rowNumber` and `columnNumber` then setting its `childNum` property to `index`, its `hasMine`
     * property to the [Boolean] stored at location `index` in [haveMines], and calling our
     * [neighborSearch] method to have it set its `neighbors` property to a list of all of the
     * indices of the adjacent sectors of that location. We then add the `newSectorContent` to our
     * [gameState] list and increment `index`.
     *
     * @param minesDatum the [MinesDatum] read from our ROOM database which we are to reload for the
     * user to replay.
     */
    fun loadGameFromMinesDatum(minesDatum: MinesDatum) {
        numColumns = minesDatum.numColumns
        numRows = minesDatum.numRows
        numSectors = numColumns * numRows
        numMines = minesDatum.numMines
        numChecked = 0
        numCheckedSafe = 0
        numCheckedMine = 0
        modeMine = false
        modeSafe = true

        gameState = ArrayList(numSectors)
        haveMines = ArrayList(numSectors)

        for (index in minesDatum.haveMines.indices) {
            haveMines.add(minesDatum.haveMines[index] != ' ')
        }
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

    /**
     * Converts the current game to a [MinesDatum] representation and inserts that [MinesDatum] into
     * the database. We construct a [MinesDatum] for our variable `val minesDatum` whose `numColumns`,
     * `numRows` and `numMines` fields are the same as the current games properties, whose field
     * `elapsedTimeMilli` is milliseconds since boot minus our [startTime] property and whose
     * `haveMines` field is the string encoding of our [haveMines] property that our method
     * [stringEncodeHaveMines] constructs. We launch a new coroutine without blocking the current
     * thread on the [CoroutineScope] of our field [uiScope] which calls our [insertMinesDatum]
     * suspend function to insert `minesDatum` in the database by executing a suspending block with
     * the [Dispatchers.IO] coroutine context, suspending until it completes, and returning the
     * result of calling the `insert` method of our [MinesDatabaseDao] field [minesDatabaseDao].
     *
     * Finally we return `minesDatum` to the caller (which it ignores at the moment)
     *
     * @return a [MinesDatum] holding the state variables of the current game.
     */
    fun toMinesDatum(): MinesDatum {
        latestDatum = MinesDatum(
            numColumns = numColumns,
            numRows = numRows,
            numMines = numMines,
            elapsedTimeMilli = SystemClock.elapsedRealtime() - startTime,
            haveMines = stringEncodeHaveMines()
        )
        uiScope.launch {
            insertMinesDatum(latestDatum)
        }
        return latestDatum
    }

    /**
     * This method inserts its [MinesDatum] parameter [datum] in the game history database. It does
     * this by calling the `insert` method of our [MinesDatabaseDao] field [minesDatabaseDao] in a
     * lambda which is launched on the [Dispatchers.IO] coroutine context.
     *
     * @param datum the [MinesDatum] we are to insert in our game history database.
     */
    private suspend fun insertMinesDatum(datum: MinesDatum) {
        withContext(Dispatchers.IO) {
            minesDatabaseDao?.insert(datum)
        }
    }

    /**
     * Deletes its [MinesDatum] parameter [datum] from the game history database. It does this by
     * launching a new coroutine without blocking the current thread on the [CoroutineScope] of our
     * field [uiScope] and in its suspend lambda it calls our [deleteMinesDatumByGameId] method with
     * the [MinesDatum.gameId] property of [datum]. [deleteMinesDatumByGameId] then calls the
     * [MinesDatabaseDao.deleteSingleID] method of [minesDatabaseDao] with that game ID in a lambda
     * which is launched on the [Dispatchers.IO] coroutine context.
     *
     * @param datum the [MinesDatum] which is to be deleted from the game history database.
     */
    fun deleteMinesDatum(datum: MinesDatum) {
        uiScope.launch {
            deleteMinesDatumByGameId(datum.gameId)
        }
    }

    /**
     * Deletes the [MinesDatum] whose [MinesDatum.gameId] property is equal to our [Long] parameter
     * [id]. We call a suspending block on the [Dispatchers.IO] coroutine context which calls the
     * [MinesDatabaseDao.deleteSingleID] method of [minesDatabaseDao] with our [Long] parameter [id]
     * to have it delete the [MinesDatum] in the game history database whose [MinesDatum.gameId]
     * property is equal to [id].
     *
     * @param id the [MinesDatum.gameId] of the [MinesDatum] to be deleted.
     */
    private suspend fun deleteMinesDatumByGameId(id: Long) {
        withContext(Dispatchers.IO) {
            minesDatabaseDao?.deleteSingleID(id)
        }
    }

    /**
     * Trims the games history database down to [bestNumber] entries. If [bestNumber] is greater
     * than or equal to the current size of our [gameHistory] database we return having done nothing.
     * Otherwise we initialize our [MutableList] of [Long] variable `val listToDelete` to a new
     * instance, then loop over `index` from [bestNumber] to the size of [gameHistory] adding the
     * [MinesDatum.gameId] of the entry at `index` to `listToDelete`. When done creating the [List]
     * of [MinesDatum.gameId] unique IDs to delete we launch a a new coroutine without blocking the
     * current thread on the [CoroutineScope] of our field [uiScope] and in its suspend lambda we
     * call our [deleteMinesDatumsWhoseGameIdIsInList] suspend function with `listToDelete`.
     *
     * @param bestNumber the number of entries to trim the game history database down to.
     */
    fun trimToBest(bestNumber: Int) {
        if (bestNumber >= gameHistory!!.value!!.size) return
        val listToDelete: MutableList<Long> = mutableListOf()
        for (index in bestNumber until gameHistory!!.value!!.size) {
            listToDelete.add(gameHistory!!.value!![index].gameId)
        }
        uiScope.launch {
            deleteMinesDatumsWhoseGameIdIsInList(listToDelete)
        }
    }

    /**
     * Deletes all the [MinesDatum] whose [MinesDatum.gameId] is in our [List] of [Long] parameter
     * [listOfGameIds]. We call a suspending block on the [Dispatchers.IO] coroutine context which
     * calls the [MinesDatabaseDao.deleteMultipleIDs] method of [minesDatabaseDao] with our [List]
     * of [Long] parameter [listOfGameIds] to have it delete all the [MinesDatum] in the game history
     * database whose [MinesDatum.gameId] property is in [listOfGameIds].
     */
    private suspend fun deleteMinesDatumsWhoseGameIdIsInList(listOfGameIds: List<Long>) {
        withContext(Dispatchers.IO) {
            minesDatabaseDao?.deleteMultipleIDs(listOfGameIds)
        }
    }

    /**
     * Encodes our [Boolean] array List [haveMines] into a [String] which it returns to the caller.
     * We initialize our [StringBuilder] variable `val stringBuilder` sized to hold a [String] the
     * size of [haveMines]. Then we loop through each of the [Boolean] `sectorHasMine` values in
     * [haveMines] appending a '*' character if `sectorHasMine` is true, or a ' ' character if it
     * is false. When done we return the [String] version of `stringBuilder` to the caller.
     *
     * @return a [String] which encodes the contents of our [Boolean] array list field [haveMines]
     */
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

    /**
     * Returns the latest [MinesDatum] which has been inserted into our game history database. It
     * does this by returning the value that the `getLatestEntry` method of our [MinesDatabaseDao]
     * field [minesDatabaseDao] fetches when it is called from a lambda which is launched on the
     * [Dispatchers.IO] coroutine context.
     *
     * @return the [MinesDatum] which was most recently inserted into our game history database.
     */
    @Suppress("unused")
    suspend fun retrieveLatestDatum(): MinesDatum {
        return withContext(Dispatchers.IO) {
            minesDatabaseDao!!.getLatestEntry()!!
        }
    }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel. First we call our super's implementation of `onCleared`,
     * then we call the [Narrator.shutDown] method of our [narrator] field if it is not `null` and
     * set [narrator] to `null`.
     */
    override fun onCleared() {
        super.onCleared()
        narrator?.shutDown()
        narrator = null
    }
}