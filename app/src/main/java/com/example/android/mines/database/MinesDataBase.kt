package com.example.android.mines.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * The [RoomDatabase] we use to store the [MinesDatum] entries describing our game history (in its
 * table "mines_game_history"). The `Database` annotation marks the class as a RoomDatabase and the
 * `entities` parameter is the list of entities included in the database. Each entity turns into a
 * table in the database. Our only table is the "mines_game_history" table defined by our [MinesDatum]
 * data class. Our database `version` is 1, and the `exportSchema` false parameter disables the
 * export of our database schema into a folder (we do not have a good reason to have a version
 * history of our schema in our codebase).
 */
@Database(entities = [MinesDatum::class], version = 1, exportSchema = false)
abstract class MinesDataBase : RoomDatabase() {

    /**
     * The `DAO` class used to access our database table.
     */
    abstract val minesDatabaseDao: MinesDatabaseDao

    companion object {
        /**
         * The singleton instance of our [MinesDataBase] which is lazily built by our [getInstance]
         * method.
         */
        @Volatile
        private var INSTANCE: MinesDataBase? = null

        /**
         * Returns our singleton [MinesDataBase] instance, building it first if it null.
         *
         * @param context the [Context] of the application that owns our activity
         * @return reference to our singleton [MinesDataBase] instance
         */
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
