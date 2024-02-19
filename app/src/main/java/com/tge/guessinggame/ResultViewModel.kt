package com.tge.guessinggame

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class ResultViewModel(finalResult: String, val dao: GameDao) : ViewModel() {
    var result = finalResult
    val allGames: LiveData<List<Game>> = dao.getAll()

}