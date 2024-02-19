package com.tge.guessinggame


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun GameFragment(navController: NavController) {
    val viewModel: GameViewModel = viewModel()
    // :)
    val context = LocalContext.current
    val dao = GameDatabase.getInstance(context).gameDao
    viewModel.dao = dao

    // Observe the game over state as a State<Boolean>
    val gameOverState by viewModel.gameOver.observeAsState()

    // Effect to navigate when gameOverState changes
    LaunchedEffect(gameOverState) {
        if (gameOverState == true) {
            val finalResult = viewModel.wonLostMessage()
            navController.navigate("result/$finalResult")
        }
    }

    return GameFragmentContent(viewModel = viewModel)

}


@Composable
fun GameFragmentContent(viewModel: GameViewModel) {
    val guess = remember {
        mutableStateOf("")
    }

    val isSecretWordInit = (viewModel.secretWordDisplay.observeAsState().value != null)


    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSecretWordInit) {
                SecretWordDisplay(viewModel = viewModel)

            } else {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                )
            }
        }
        LivesLeftText(viewModel = viewModel)
        IncorrectGuessesText(viewModel = viewModel)
        EnterGuess(guess = guess.value) {
            if (it.isNotEmpty()) {
                guess.value = it[it.length - 1].toString()
            } else {
                guess.value = it
            }
        }

        Hint(
            viewModel = viewModel
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GuessButton({
                viewModel.makeGuess(guess.value.uppercase())
                guess.value = ""
            }, isEnabled = isSecretWordInit)
            FinishGameButton(
                clicked = { viewModel.finishGame() },
                isEnabled = (isSecretWordInit && viewModel.isHintLoaded.value == true)
            )
        }
    }
}

@Composable
fun Hint(viewModel: GameViewModel) {
    val hint = viewModel.hint.observeAsState()
    val lives = viewModel.livesLeft.observeAsState()
    if (hint.value != null) {
        AnimatedVisibility(
            visible = lives.value!! < 2,
            enter = fadeIn(animationSpec = tween(durationMillis = 1000, easing = LinearEasing)),
            exit = fadeOut(),
        ) {
            Text(
                text = "Hint: ${hint.value}", modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 8.dp), fontWeight = FontWeight.SemiBold

            )
        }
    }

}

@Composable
fun SecretWordDisplay(viewModel: GameViewModel) {
    val display = viewModel.secretWordDisplay.observeAsState()
    display.value?.let {
        Text(text = it, letterSpacing = 0.1.em, fontSize = 36.sp)
    }
}

@Composable
fun LivesLeftText(viewModel: GameViewModel) {
    val livesLeft = viewModel.livesLeft.observeAsState()
    livesLeft.value?.let {
        Text(text = stringResource(id = R.string.lives_left, it))
    }
}

@Composable
fun IncorrectGuessesText(viewModel: GameViewModel) {
    val incorrectGuesses = viewModel.incorrectGuesses.observeAsState()
    incorrectGuesses.value?.let {
        Text(stringResource(id = R.string.incorrect_guesses, it))
    }
}

@Composable
fun EnterGuess(guess: String, changed: (String) -> Unit) {
    TextField(
        value = guess,
        onValueChange = changed,
        label = { Text(text = "Guess a letter") },
        maxLines = 1,
    )
}

@Composable
fun GuessButton(clicked: () -> Unit, isEnabled: Boolean) {
    Button(onClick = clicked, enabled = isEnabled) {
        Text(text = "Guess!")
    }
}

@Composable
fun FinishGameButton(clicked: () -> Unit, isEnabled: Boolean) {
    Button(onClick = clicked, enabled = isEnabled) {
        Text("Finish game")
    }
}





