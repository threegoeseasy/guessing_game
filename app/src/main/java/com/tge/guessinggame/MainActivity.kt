package com.tge.guessinggame

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tge.guessinggame.theme.GuessingGameTheme

class MainActivity : ComponentActivity() {
    private val networkReceiver = NetworkReceiver()

    private val networkStatusViewModel: NetworkStatusViewModel by lazy {
        ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val networkStatusTracker = NetworkStatusTracker(this@MainActivity)
                    @Suppress("UNCHECKED_CAST")
                    return NetworkStatusViewModel(networkStatusTracker) as T
                }
            },
        )[NetworkStatusViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(networkReceiver, IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED))


        setContent {
            GuessingGameTheme {
                Surface(
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                ) {
                    AlertNoInternet()
                    MainActivityContent()
                }
            }
        }
    }

    @Composable
    fun MainActivityContent() {
        val navController = rememberNavController()
        NavHost(navController = navController, startDestination = "game") {
            composable("game") { GameFragment(navController) }
            composable("result/{finalResult}") { backStackEntry ->
                val finalResult = backStackEntry.arguments?.getString("finalResult")
                ResultFragment(navController, finalResult.toString())
            }
        }
    }

    @Composable
    fun AlertNoInternet(

    ) {
        val openAlertDialog = remember { mutableStateOf(false) }
        networkStatusViewModel.state.observe(this) { state ->
            when (state) {
                MyState.Fetched -> {
                    Log.i("WWW", "internet working")
                    openAlertDialog.value = false
                }

                MyState.Error -> {
                    Log.e("WWW", "internet error")
                    openAlertDialog.value = true
                }
            }
        }
        when {
            openAlertDialog.value -> {
                AlertDialog(
                    icon = {
                        Image(
                            painter = painterResource(id = R.drawable.baseline_signal_wifi_connected_no_internet_4_24),
                            contentDescription = "no internet connection"
                        )
                    },
                    title = {
                        Text(text = "No internet connection", color = MaterialTheme.colors.primary)
                    },
                    text = {
                        Text(
                            text = "The game requires internet to load words and hints.",
                            color = MaterialTheme.colors.primary
                        )
                    },
                    onDismissRequest = {
                        openAlertDialog.value = false
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                openAlertDialog.value = false
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                openAlertDialog.value = false
                            }
                        ) {
                            Text("Dismiss")
                        }
                    }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(networkReceiver)
    }

}