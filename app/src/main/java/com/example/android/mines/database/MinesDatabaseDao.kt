package com.example.android.mines.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * Our Data Access Object. The `@Dao` annotation Marks the class as a Data Access Object. Data Access
 * Objects are the main classes where you define your database interactions. They can include a
 * variety of query methods. The class marked with `@Dao` should either be an interface or an abstract
 * class. At compile time, Room will generate an implementation of this class when it is referenced
 * by a Database. An abstract `@Dao` class can optionally have a constructor that takes a Database
 * as its only parameter. It is recommended to have multiple Dao classes in your codebase depending
 * on the tables they touch.
 */
@Dao
interface MinesDatabaseDao {

    /**
     * Inserts the [MinesDatum] parameter [game] into the database. The `@Insert` annotation marks
     * a method in a Dao annotated class as an insert method. The implementation of the method will
     * insert its parameters into the database. All of the parameters of the Insert method must
     * either be classes annotated with `@Entity` or collections/array of it.
     *
     * @param game the [MinesDatum] containing all the information about a game that is necessary to
     * recreate it.
     */
    @Insert
    fun insert(game: MinesDatum)

    /**
     * Updates the [MinesDatum] with the same primary key as our [MinesDatum] parameter [game] in
     * the database. The `@Update` annotation marks a method in a Dao annotated class as an update
     * method. The implementation of the method will update its parameters in the database if they
     * already exist (checked by primary keys). If they don't already exist, this option will not
     * change the database. All of the parameters of the Update method must either be classes
     * annotated with `@Entity` or collections/array of it.
     *
     * @param game the [MinesDatum] whose entry in the database should be updated.
     */
    @Update
    fun update(game: MinesDatum)

    /**
     * Retrieves the [MinesDatum] from the database whose primary key `gameId` is equal to our [Long]
     * parameter [key]. The `@Query` annotation marks a method in a Dao annotated class as a query
     * method. The value of the annotation is the query that will be run when this method is called.
     * This query is verified at compile time by Room to ensure that it compiles fine against the
     * database. The arguments of the method will be bound to the bind arguments in the SQL statement.
     *
     * @param key the `gameId` primary key of the [MinesDatum] we are to retrieve.
     */
    @Query("SELECT * from mines_game_history WHERE gameId = :key")
    fun get(key: Long): MinesDatum?

    /**
     * Deletes all rows in the table "mines_game_history".
     */
    @Query("DELETE FROM mines_game_history")
    fun clear()

    /**
     * Returns only the latest [MinesDatum] from the database. The SQLite statement selects all entries
     * in the "mines_game_history" table, sorts by the `gameId` column in descending order and returns
     * only the first [MinesDatum] (thanks to the LIMIT 1 clause).
     */
    @Query("SELECT * FROM mines_game_history ORDER BY gameId DESC LIMIT 1")
    fun getLatestEntry(): MinesDatum?

    /**
     * Returns a [LiveData] list of all of the [MinesDatum] in the "mines_game_history" table sorted
     * by the `game_elapsed_time` column in ascending order.
     */
    @Query("SELECT * FROM mines_game_history ORDER BY game_elapsed_time ASC")
    fun getAllGames(): LiveData<List<MinesDatum>>

}