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
        append("Number of columns: ${game.numColumns}\n")
        append("Number of rows: ${game.numRows}\n")
        append("Number of mines: ${game.numMines}\n")
        append("Start time: ${convertLongToDateString(game.startTimeMilli)}\n")
        append("Elapsed Time: ${DateUtils.formatElapsedTime(game.elapsedTimeMilli/1_000L)}\n")
        //append("Mine Locations: ${game.haveMines}\n")
    }
    return sb.toString()
}

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
 * EEEE - Display the long letter version of the weekday
 * MMM - Display the letter abbreviation of the nmotny
 * dd-yyyy - day in month and full year numerically
 * HH:mm - Hours and minutes in 24hr format
 */
@SuppressLint("SimpleDateFormat")
fun convertLongToDateString(systemTime: Long): String {
    return SimpleDateFormat("MMM-dd-yyyy' 'HH:mm")
        .format(systemTime).toString()
}
