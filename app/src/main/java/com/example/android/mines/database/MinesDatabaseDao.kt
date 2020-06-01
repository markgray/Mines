package com.example.android.mines.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MinesDatabaseDao {

    @Insert
    fun insert(game: MinesDatum)

    @Update
    fun update(game: MinesDatum)

    @Query("SELECT * from mines_game_history WHERE gameId = :key")
    fun get(key: Long): MinesDatum?

    @Query("DELETE FROM mines_game_history")
    fun clear()

    @Query("SELECT * FROM mines_game_history ORDER BY gameId DESC LIMIT 1")
    fun getLatestEntry(): MinesDatum?

    @Query("SELECT * FROM mines_game_history ORDER BY gameId DESC")
    fun getAllGames(): LiveData<List<MinesDatum>>

}