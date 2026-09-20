package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AfricaRegistry
import com.example.ui.components.GameHeaderBar
import com.example.ui.components.LevelCompleteDialog
import com.example.ui.components.MahjongBoardView
import com.example.ui.components.PauseDialog
import com.example.ui.components.PowerUpBottomBar
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkWoodBg
import com.example.ui.theme.TerracottaPrimary
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel

@Composable
fun MainGameScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val gameState by viewModel.gameState.collectAsState()
  val profile by viewModel.profile.collectAsState()

  val country = AfricaRegistry.getCountryForLevel(gameState.levelNumber)

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DarkWoodBg)
  ) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Header
      GameHeaderBar(
        levelNumber = gameState.levelNumber,
        countryFlag = country.flag,
        countryName = country.name,
        score = gameState.score,
        comboStreak = gameState.comboStreak,
        timeElapsed = gameState.timeElapsed,
        timeLimit = gameState.timeLimit,
        onPauseClick = { viewModel.togglePause() }
      )

      // No Moves Notice Banner
      if (gameState.isNoMoves && !gameState.isLevelComplete) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFB45309))
            .padding(horizontal = 12.dp, vertical = 6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "⚠️ No legal moves remaining!",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            modifier = Modifier.weight(1f)
          )
          Button(
            onClick = { viewModel.useShuffle() },
            colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color.Black),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(30.dp)
          ) {
            Text("Shuffle", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }
        }
      }

      // Layered 2.5D Mahjong Board
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
      ) {
        MahjongBoardView(
          tiles = gameState.tiles,
          onTileClick = { tile -> viewModel.onTileClicked(tile) }
        )
      }

      // Power-up Toolbar
      PowerUpBottomBar(
        hintCount = profile?.hints ?: 0,
        shuffleCount = profile?.shuffles ?: 0,
        timeBonusCount = profile?.timeBonuses ?: 0,
        powerTileCount = profile?.powerTiles ?: 0,
        onHintClick = { viewModel.useHint() },
        onShuffleClick = { viewModel.useShuffle() },
        onTimeBonusClick = { viewModel.useTimeBonus() },
        onPowerTileClick = { viewModel.usePowerTile() }
      )
    }

    // Level Complete Dialog
    if (gameState.isLevelComplete) {
      LevelCompleteDialog(
        levelNumber = gameState.levelNumber,
        stars = gameState.starsAwarded,
        timeElapsedSeconds = gameState.timeElapsed,
        matches = gameState.matchesMade,
        coinsEarned = gameState.coinsAwarded,
        gemsEarned = gameState.gemsAwarded,
        culturalCard = gameState.culturalCardReward,
        onContinue = {
          if (gameState.isDailyChallenge) {
            viewModel.navigateTo(GameScreen.DAILY)
          } else {
            viewModel.nextLevel()
          }
        },
        onReplay = { viewModel.restartLevel() }
      )
    }

    // Pause Dialog
    if (gameState.isPaused) {
      PauseDialog(
        onResume = { viewModel.togglePause() },
        onRestart = {
          viewModel.togglePause()
          viewModel.restartLevel()
        },
        onMap = {
          viewModel.togglePause()
          viewModel.navigateTo(GameScreen.MAP)
        },
        soundEnabled = profile?.soundEnabled ?: true,
        musicEnabled = profile?.musicEnabled ?: true,
        onToggleSound = { viewModel.toggleSound(it) },
        onToggleMusic = { viewModel.toggleMusic(it) }
      )
    }

    // Time Out Dialog
    if (gameState.isTimeOut && !gameState.isLevelComplete) {
      Dialog(onDismissRequest = {}) {
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = DarkSurface,
          modifier = Modifier.padding(16.dp)
        ) {
          Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text("⏰ TIME'S UP!", color = Color(0xFFEF4444), fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(10.dp))
            Text(
              "Don't give up! Use a Time Bonus to add +60 seconds or restart the puzzle.",
              color = Color.LightGray,
              fontSize = 13.sp,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(Modifier.height(18.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
              OutlinedButton(
                onClick = { viewModel.restartLevel() },
                modifier = Modifier.weight(1f)
              ) {
                Text("Restart", color = Color.White)
              }
              Spacer(Modifier.width(8.dp))
              Button(
                onClick = { viewModel.useTimeBonus() },
                modifier = Modifier.weight(1.3f),
                colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color.Black)
              ) {
                Text("+60s Boost", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }
  }
}
