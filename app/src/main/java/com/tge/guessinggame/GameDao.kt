package com.tge.guessinggame

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GameDao {
    @Insert
    suspend fun insert(task: Game)

    @Query("SELECT * FROM games_table ORDER BY id DESC")
    fun getAll(): LiveData<List<Game>>
}