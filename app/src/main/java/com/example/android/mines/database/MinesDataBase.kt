package com.example.android.mines.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MinesDatum::class], version = 1, exportSchema = false)
abstract class MinesDataBase : RoomDatabase() {

    abstract val minesDatabaseDao: MinesDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: MinesDataBase? = null

        fun getInstance(context: Context): MinesDataBase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MinesDataBase::class.java,
                        "mines_game_history"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
