package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AfriMahjongBottomNav
import com.example.ui.screens.AchievementsScreen
import com.example.ui.screens.AdventureMapScreen
import com.example.ui.screens.CollectionScreen
import com.example.ui.screens.DailyChallengeScreen
import com.example.ui.screens.MainGameScreen
import com.example.ui.screens.MarketScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.AfriMahjongTheme
import com.example.ui.theme.DarkWoodBg
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      AfriMahjongTheme {
        AfriMahjongApp()
      }
    }
  }
}

@Composable
fun AfriMahjongApp(viewModel: GameViewModel = viewModel()) {
  val currentScreen by viewModel.currentScreen.collectAsState()

  // Handle system back navigation gracefully
  BackHandler(enabled = currentScreen != GameScreen.MAP) {
    when (currentScreen) {
      GameScreen.GAME -> viewModel.navigateTo(GameScreen.MAP)
      GameScreen.ACHIEVEMENTS -> viewModel.navigateTo(GameScreen.PROFILE)
      else -> viewModel.navigateTo(GameScreen.MAP)
    }
  }

  Scaffold(
    contentWindowInsets = WindowInsets.safeDrawing,
    modifier = Modifier
      .fillMaxSize()
      .background(DarkWoodBg)
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      // Screen Content
      when (currentScreen) {
        GameScreen.MAP -> AdventureMapScreen(viewModel = viewModel)
        GameScreen.GAME -> MainGameScreen(viewModel = viewModel)
        GameScreen.COLLECTION -> CollectionScreen(viewModel = viewModel)
        GameScreen.DAILY -> DailyChallengeScreen(viewModel = viewModel)
        GameScreen.MARKET -> MarketScreen(viewModel = viewModel)
        GameScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
        GameScreen.ACHIEVEMENTS -> AchievementsScreen(viewModel = viewModel)
      }

      // Show bottom navigation bar only when not in active game board
      if (currentScreen != GameScreen.GAME) {
        AfriMahjongBottomNav(
          currentScreen = currentScreen,
          onSelect = { screen -> viewModel.navigateTo(screen) },
          modifier = Modifier.align(Alignment.BottomCenter)
        )
      }
    }
  }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
  androidx.compose.material3.Text(text = "Hello $name!", modifier = modifier)
}

