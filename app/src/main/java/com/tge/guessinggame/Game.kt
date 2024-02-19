package com.tge.guessinggame

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games_table")
data class Game(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var gameId: Long = 0L,
    @ColumnInfo(name = "word") var word: String = "",
    @ColumnInfo(name = "definition") var definition: String = "",
    @ColumnInfo(name = "lives_left") var livesLeft: Int = 0,
    @ColumnInfo(name = "is_won") var isWon: Boolean = false
)
