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

    /**
     * The number of columns our game board uses. The name of the column in the database is
     * "number_of_columns"
     */
    @ColumnInfo(name = "number_of_columns")
    val numColumns: Int = -1,

    /**
     * The number of rows our game board uses. The name of the column in the database is
     * "number_of_rows"
     */
    @ColumnInfo(name = "number_of_rows")
    val numRows: Int = -1,

    /**
     * The number of Mines our game board has. The name of the column in the database is
     * "number_of_mines"
     */
    @ColumnInfo(name = "number_of_mines")
    val numMines: Int = 0,

    /**
     * The current time when the [MinesDatum] was constructed, which is the difference, measured in
     * milliseconds, between the current time and midnight, January 1, 1970 UTC. The name of the
     * column in the database is "game_date_and_time".
     */
    @ColumnInfo(name = "game_date_and_time")
    val startTimeMilli: Long = System.currentTimeMillis(),

    /**
     * The number of milliseconds that it took to play the game. The name of the column in the
     * database is "game_elapsed_time".
     */
    @ColumnInfo(name = "game_elapsed_time")
    var elapsedTimeMilli: Long = 0,

    /**
     * Each character in this [String] field represents a sector in the SharedViewModel.haveMines
     * list. Sectors with Mines are encoded as a "*" character, safe sectors are encoded as a blank
     * character. The name of the column in the database is "sectors_with_mines".
     */
    @ColumnInfo(name = "sectors_with_mines")
    var haveMines: String

)