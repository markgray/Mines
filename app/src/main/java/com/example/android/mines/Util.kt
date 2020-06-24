package com.example.android.mines

import android.annotation.SuppressLint
import android.text.format.DateUtils
import com.example.android.mines.database.MinesDatum
import java.text.SimpleDateFormat

/**
 * Formats the fields of its [MinesDatum] parameter [game] into a [String] suitable for display to
 * the user. We allocate a [StringBuilder] for our variable `val sb`, then append the string values
 * of the interesting fields of [game] to it. Finally we return the string value of `sb`.
 *
 * @return [String] displaying values of the interesting fields of our [MinesDatum] parameter [game]
 */
fun formatMinesDatum(game: MinesDatum): String {
    val sb = StringBuilder()
    sb.apply {
        append("Game Id: ${game.gameId}\n")
        append("Columns: ${game.numColumns}\n")
        append("Rows: ${game.numRows}\n")
        append("Mines: ${game.numMines}\n")
        append("Date: ${convertLongToDateString(game.startTimeMilli)}\n")
        append("Time: ${DateUtils.formatElapsedTime(game.elapsedTimeMilli/1_000L)}\n")
        //append("Mine Locations: ${game.haveMines}\n")
    }
    return sb.toString()
}

/**
 * Formats its [MinesDatum] parameter [game] into a [String] representation of the game board state
 * for display to the user. Mined sectors are represented as red X's and Safe sectors as green check
 * marks. We initialize our [StringBuilder] variable `val sb` with a new instance, initialize our
 * [Int] variable `val numColumns` to the `numColumns` field of [game], our [Int] variable
 * `val numRows` to the `numRows` field of [game], and our [Int] variable `var index` to 0. We then
 * loop over `r` for the the `numRows` rows, and over `c` for the `numColumns` columns and if the
 * character at `index` in the `haveMines` field of [game] is a ' ' character we append a green
 * check mark to `sb`, otherwise we append a red X to `sb` and in either case increment `index`.
 * At the end of every row except for the last we also append a newline character. When done with
 * all the rows and columns we return the [String] value of `sb` to the caller.
 *
 * @param game the [MinesDatum] whose board we are to display
 * @return a [String] that looks like the game board which can be displayed in a `TextView`
 */
fun formatGameBoard(game: MinesDatum): String {
    val sb = StringBuilder()
    val numColumns = game.numColumns
    val numRows = game.numRows
    var index = 0
    for (r in 0 until numRows) {
        for (c in 0 until numColumns) {
            if (game.haveMines[index] == ' ') {
                sb.append("\u2705")
            } else {
                sb.append("\u274c")
            }
            index++
        }
        if (index < game.haveMines.length) {
            sb.append("\n")
        }
    }

    return sb.toString()
}

/**
 * Take the Long milliseconds returned by the system and stored in Room,
 * and convert it to a nicely formatted string for display.
 *
 *  - EEEE - Display the long letter version of the weekday
 *  - MMM - Display the letter abbreviation of the nmotny
 *  - dd-yyyy - day in month and full year numerically
 *  - HH:mm - Hours and minutes in 24hr format
 */
@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("MMM-dd-yyyy' 'HH:mm")
        .format(systemTime).toString()
}
