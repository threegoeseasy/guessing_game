package com.tge.guessinggame


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL


class GameViewModel : ViewModel() {
    private val randomWordApi = "https://random-word-api.vercel.app/api?words=1"
    var dao: GameDao? = null

    private var secretWord: String = ""
    private val _secretWordDisplay = MutableLiveData<String>(null)
    val secretWordDisplay: LiveData<String> get() = _secretWordDisplay
    private var correctGuesses = ""
    private val _incorrectGuesses = MutableLiveData<String>("")
    val incorrectGuesses: LiveData<String> get() = _incorrectGuesses
    private val _livesLeft = MutableLiveData<Int>(8)
    val livesLeft: LiveData<Int> get() = _livesLeft
    private val _gameOver = MutableLiveData<Boolean>(false)
    val gameOver: LiveData<Boolean> get() = _gameOver
    private val _hint = MutableLiveData<String>("")
    val hint: LiveData<String> get() = _hint
    private val _isHintLoaded = MutableLiveData<Boolean>(false)
    val isHintLoaded: LiveData<Boolean> get() = _isHintLoaded

    init {
        Log.i(
            "CURIOUS",
            " _secretWordDisplay: " + _secretWordDisplay.value + " secretWordDisplay: " + secretWordDisplay.value
        )
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url: URL = URI.create(randomWordApi).toURL()
                val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

                //Request method: GET
                connection.requestMethod = "GET"

                // Response code
                val responseCode: Int = connection.responseCode
                Log.i("API", "Response Code: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read and print the response data
                    val reader: BufferedReader =
                        BufferedReader(InputStreamReader(connection.inputStream))
                    var line: String?
                    val response = StringBuilder()

                    while (reader.readLine().also { line = it } != null) {
                        response.append(line)
                    }

                    reader.close()
                    Log.i("API", "Response Data: $response")
                    val cleanedResponse =
                        response.toString().trim('[', ']').removePrefix("\"").removeSuffix("\"")

                    withContext(Dispatchers.Main) {
                        secretWord = cleanedResponse
                        Log.i("API", "Word is $secretWord")
                        _secretWordDisplay.value = deriveSecretWordDisplay()
                        getHint()
                    }
                } else {
                    Log.e("API", "Error: Unable to fetch data from the API")
                }

                // Close the connection
                connection.disconnect()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    private fun deriveSecretWordDisplay(): String {
        var display = ""
        secretWord.uppercase().forEach {
            display += checkLetter(it.toString())
        }
        return display
    }

    private fun checkLetter(str: String) = when (correctGuesses.contains(str)) {
        true -> str
        false -> "_"
    }


    private fun getHint(): String {
        val call = ApiClient.apiService.getDefinitionByWord(secretWord)
        Log.i("API HINT", "getting hint...")

        call.enqueue(object : Callback<List<DefinitionResponse>> {
            override fun onResponse(
                call: Call<List<DefinitionResponse>>,
                response: Response<List<DefinitionResponse>>
            ) {
                Log.i("API HINT", "on response... $response")
                if (response.isSuccessful) {
                    val definitionResponses = response.body()
                    definitionResponses?.let { responses ->
                        if (responses.isNotEmpty()) {
                            val meanings = responses[0].meanings
                            if (meanings.isNotEmpty()) {
                                val definitions = meanings[0].definitions
                                if (definitions.isNotEmpty()) {
                                    _hint.value = definitions[0].definition
                                    Log.i("API HINT", "Hint: ${hint.value}")
                                } else {
                                    Log.e("API HINT", "Empty definitions list")
                                }
                            } else {
                                Log.e("API HINT", "Empty meanings list")
                            }
                        } else {
                            Log.e("API HINT", "Empty response body")
                        }
                    }
                } else {
                    Log.e("API HINT", "Response is unsuccessful")
                }
            }

            override fun onFailure(call: Call<List<DefinitionResponse>>, t: Throwable) {
                // Handle failure
                Log.e("API HINT", "Failure: ${t.message}", t)
            }
        })
        _isHintLoaded.value = true
        return ""
    }

    fun makeGuess(guess: String) {
        if (guess.length == 1) {
            if (secretWord.uppercase().contains(guess)) {
                correctGuesses += guess
                _secretWordDisplay.value = deriveSecretWordDisplay()
            } else if (!incorrectGuesses.value!!.contains(guess)) {
                _incorrectGuesses.value += "$guess "
                _livesLeft.value = _livesLeft.value?.minus(1)
            }
            if (isWon() || isLost()) {
                addGameRecord()
                _gameOver.value = true
            }
        }
    }

    fun wonLostMessage(): String {
        var message = ""
        if (isWon()) message = "You won!"
        else if (isLost()) message = "You lost!"
        message += " The word was $secretWord."
        return message
    }

    private fun isWon() = secretWord.equals(secretWordDisplay.value, true)
    private fun isLost() = (livesLeft.value ?: 0) <= 0

    private fun addGameRecord() {
        viewModelScope.launch {
            val game = Game()
            game.isWon = isWon()
            game.livesLeft = livesLeft.value!!
            game.word = secretWord
            game.definition = hint.value!!
            Log.i("DB", game.toString())
            dao?.insert(game)
            Log.i("DB", "Game was added")
        }
    }

    fun finishGame() {
        addGameRecord()
        _gameOver.value = true
    }
}