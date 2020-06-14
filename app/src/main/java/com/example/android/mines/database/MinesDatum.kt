package com.example.android.mines.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * This is the data class representing a game that is inserted into the "mines_game_history" table
 * of our database when the game ends. The `@Entity` annotation marks a class as an entity. This
 * class will have a mapping SQLite table in the database (tableName = "mines_game_history" in our
 * case). Each entity must have at least 1 field annotated with `@PrimaryKey` ([gameId] in our case).
 */
@Entity(tableName = "mines_game_history")
data class MinesDatum
    (
    /**
     * The primary key of our database table. The `@PrimaryKey` annotation marks a field in an Entity
     * as the primary key. The `autoGenerate` field of [PrimaryKey] is set to *true* to let SQLite
     * generate the unique id (starts at 1, 0 is treated as not-set). Name of the column in the
     * database defaults to "gameId"
     */
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

    @ColumnInfo(name = "game_elapsed_time")
    var elapsedTimeMilli: Long = 0,

    /**
     * Each character in this [String] field represents a sector in the SharedViewModel.haveMines
     * list. Sectors with Mines are encoded as a "*" character, safe sectors are encoded as a blank
     * character. The name of the column in the database is "sectors_with_mines"
     */
    @ColumnInfo(name = "sectors_with_mines")
    var haveMines: String

)