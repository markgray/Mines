package com.example.android.mines.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mines_game_history")
data class MinesDatum(
    @PrimaryKey(autoGenerate = true)
    var gameId: Long = 0L,

    @ColumnInfo(name = "number_of_columns")
    val numColumns: Int = -1,

    @ColumnInfo(name = "number_of_rows")
    val numRows: Int = -1,

    @ColumnInfo(name = "number_of_mines")
    val numMines: Int = 0,

    @ColumnInfo(name = "game_date_and_time")
    val startTimeMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "game_elaped_time")
    var elapsedTimeMilli: Long = 0,

    /**
     * Each character represents a sector in the SharedViewModel.haveMines list
     */
    @ColumnInfo(name = "sectors_with_mines")
    var haveMines: String

)