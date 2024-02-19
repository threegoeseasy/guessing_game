package com.tge.guessinggame

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController


@Composable
fun ResultFragment(navController: NavController, finalResult: String) {

    val context = LocalContext.current
    val dao = GameDatabase.getInstance(context).gameDao
    val viewModel: ResultViewModel = viewModel(
        factory = ResultViewModelFactory(finalResult, dao)
    )

    return androidx.compose.material.Surface {
        ResultFragmentContent(viewModel, navController)
    }

}

@Composable
fun ResultFragmentContent(viewModel: ResultViewModel, navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ResultText(result = viewModel.result)
        NewGameButton {
            navController.navigate("game")
        }
        GameList(resultViewModel = viewModel)
    }
}

@Composable
fun ResultText(result: String) {
    Text(text = result, fontSize = 28.sp, textAlign = TextAlign.Center)
}

@Composable
fun NewGameButton(clicked: () -> Unit) {
    Button(onClick = clicked) {
        Text("Start New Game")
    }
}

@Composable
fun GameList(resultViewModel: ResultViewModel) {
    val games by resultViewModel.allGames.observeAsState(initial = emptyList())
    LazyColumn {
        items(games) { game ->
            PlayedGame(game)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayedGame(game: Game) {
    val context = LocalContext.current
    Card(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(18.dp)
            .combinedClickable {
                Toast
                    .makeText(
                        context,
                        if (game.definition != "") "Definition: ${game.definition}" else "Definition not found in vocabulary",
                        Toast.LENGTH_LONG
                    )
                    .show()
            },
        contentColor = if (game.isWon) Color(6, 101, 11, 80) else Color.Red.copy(0.5F),
        shape = RoundedCornerShape(8.dp),
        border = null,
        elevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = game.word)
            Text(text = if (game.isWon) "victory, lives left: ${game.livesLeft}" else "defeat")
        }
    }
}




