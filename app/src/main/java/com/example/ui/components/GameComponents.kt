package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.CulturalDiscoveryCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberGoldDark
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.MalachiteGreen
import com.example.ui.theme.NileAzure
import com.example.ui.theme.TerracottaDark
import com.example.ui.theme.TerracottaPrimary

@Composable
fun TopCurrencyBar(
  coins: Int,
  gems: Int,
  stars: Int,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // Coins badge
    CurrencyChip(
      emoji = "🪙",
      value = "$coins",
      color = AmberGold
    )

    // Gems badge
    CurrencyChip(
      emoji = "💎",
      value = "$gems",
      color = NileAzure
    )

    // Stars badge
    CurrencyChip(
      emoji = "⭐",
      value = "$stars",
      color = Color(0xFFFFD700)
    )
  }
}

@Composable
fun CurrencyChip(
  emoji: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .clip(RoundedCornerShape(20.dp))
      .background(DarkSurfaceElevated)
      .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
      .padding(horizontal = 10.dp, vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(4.dp)
  ) {
    Text(emoji, fontSize = 14.sp)
    Text(
      text = value,
      color = Color.White,
      fontWeight = FontWeight.Bold,
      fontSize = 13.sp
    )
  }
}

@Composable
fun GameHeaderBar(
  levelNumber: Int,
  countryFlag: String,
  countryName: String,
  score: Int,
  comboStreak: Int,
  timeElapsed: Int,
  timeLimit: Int,
  onPauseClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val remainingTime = (timeLimit - timeElapsed).coerceAtLeast(0)
  val minutes = remainingTime / 60
  val seconds = remainingTime % 60
  val timeString = String.format("%02d:%02d", minutes, seconds)
  val timeFraction = (remainingTime.toFloat() / timeLimit.toFloat()).coerceIn(0f, 1f)

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(DarkSurface)
      .border(1.dp, DarkBorder)
      .padding(horizontal = 14.dp, vertical = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
          onClick = onPauseClick,
          modifier = Modifier
            .size(36.dp)
            .background(DarkSurfaceElevated, CircleShape)
            .testTag("pause_button")
        ) {
          Icon(Icons.Default.Pause, contentDescription = "Pause", tint = AmberGold)
        }
        Spacer(Modifier.width(8.dp))
        Column {
          Text(
            text = "Level $levelNumber • $countryFlag $countryName",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = "Score: $score",
            color = AmberGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        if (comboStreak >= 2) {
          Box(
            modifier = Modifier
              .padding(end = 8.dp)
              .clip(RoundedCornerShape(12.dp))
              .background(TerracottaPrimary)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "🔥 ${comboStreak}x Combo",
              color = Color.White,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }

        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(DarkSurfaceElevated)
            .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
          Text(
            text = "⏱️ $timeString",
            color = if (remainingTime <= 30) Color(0xFFEF4444) else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }
    }

    Spacer(Modifier.height(6.dp))

    LinearProgressIndicator(
      progress = { timeFraction },
      modifier = Modifier
        .fillMaxWidth()
        .height(4.dp)
        .clip(RoundedCornerShape(2.dp)),
      color = if (timeFraction < 0.2f) Color(0xFFEF4444) else MalachiteGreen,
      trackColor = Color(0xFF38291F)
    )
  }
}

@Composable
fun PowerUpBottomBar(
  hintCount: Int,
  shuffleCount: Int,
  timeBonusCount: Int,
  powerTileCount: Int,
  onHintClick: () -> Unit,
  onShuffleClick: () -> Unit,
  onTimeBonusClick: () -> Unit,
  onPowerTileClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier
      .fillMaxWidth()
      .background(DarkSurface)
      .border(1.dp, DarkBorder)
      .padding(horizontal = 12.dp, vertical = 10.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {
    PowerUpButton(
      icon = Icons.Default.Lightbulb,
      label = "Hint",
      count = hintCount,
      accentColor = AmberGold,
      testTag = "powerup_hint",
      onClick = onHintClick
    )
    PowerUpButton(
      icon = Icons.Default.Shuffle,
      label = "Shuffle",
      count = shuffleCount,
      accentColor = NileAzure,
      testTag = "powerup_shuffle",
      onClick = onShuffleClick
    )
    PowerUpButton(
      icon = Icons.Default.HourglassBottom,
      label = "+60s",
      count = timeBonusCount,
      accentColor = MalachiteGreen,
      testTag = "powerup_time",
      onClick = onTimeBonusClick
    )
    PowerUpButton(
      icon = Icons.Default.Bolt,
      label = "Burst",
      count = powerTileCount,
      accentColor = Color(0xFFA855F7),
      testTag = "powerup_powertile",
      onClick = onPowerTileClick
    )
  }
}

@Composable
fun PowerUpButton(
  icon: ImageVector,
  label: String,
  count: Int,
  accentColor: Color,
  testTag: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = modifier
      .testTag(testTag)
      .clip(RoundedCornerShape(12.dp))
      .clickable { onClick() }
      .padding(horizontal = 8.dp, vertical = 4.dp)
  ) {
    Box(
      modifier = Modifier
        .size(46.dp)
        .clip(CircleShape)
        .background(DarkSurfaceElevated)
        .border(1.5.dp, accentColor, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(icon, contentDescription = label, tint = accentColor, modifier = Modifier.size(24.dp))

      // Badge count
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .size(18.dp)
          .clip(CircleShape)
          .background(accentColor),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = "$count",
          color = Color.Black,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
    Spacer(Modifier.height(4.dp))
    Text(
      text = label,
      color = Color.LightGray,
      fontSize = 11.sp,
      fontWeight = FontWeight.Medium
    )
  }
}

@Composable
fun LevelCompleteDialog(
  levelNumber: Int,
  stars: Int,
  timeElapsedSeconds: Int,
  matches: Int,
  coinsEarned: Int,
  gemsEarned: Int,
  culturalCard: CulturalDiscoveryCard?,
  onContinue: () -> Unit,
  onReplay: () -> Unit
) {
  val minutes = timeElapsedSeconds / 60
  val seconds = timeElapsedSeconds % 60
  val timeFormatted = String.format("%02d:%02d", minutes, seconds)

  val starScale1 = remember { Animatable(0f) }
  val starScale2 = remember { Animatable(0f) }
  val starScale3 = remember { Animatable(0f) }

  LaunchedEffect(Unit) {
    if (stars >= 1) starScale1.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
    if (stars >= 2) starScale2.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
    if (stars >= 3) starScale3.animateTo(1f, tween(300, easing = FastOutSlowInEasing))
  }

  Dialog(onDismissRequest = {}) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = DarkSurface,
      border = androidx.compose.foundation.BorderStroke(2.dp, AmberGold),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text(
          text = "LEVEL COMPLETE!",
          color = AmberGold,
          fontSize = 22.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp
        )

        Spacer(Modifier.height(14.dp))

        // Stars Row
        Row(
          horizontalArrangement = Arrangement.Center,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            Icons.Default.Star,
            contentDescription = "Star 1",
            tint = if (stars >= 1) Color(0xFFFFD700) else Color(0xFF4A382A),
            modifier = Modifier
              .size(44.dp)
              .scale(if (stars >= 1) starScale1.value else 1f)
          )
          Spacer(Modifier.width(8.dp))
          Icon(
            Icons.Default.Star,
            contentDescription = "Star 2",
            tint = if (stars >= 2) Color(0xFFFFD700) else Color(0xFF4A382A),
            modifier = Modifier
              .size(54.dp)
              .scale(if (stars >= 2) starScale2.value else 1f)
          )
          Spacer(Modifier.width(8.dp))
          Icon(
            Icons.Default.Star,
            contentDescription = "Star 3",
            tint = if (stars >= 3) Color(0xFFFFD700) else Color(0xFF4A382A),
            modifier = Modifier
              .size(44.dp)
              .scale(if (stars >= 3) starScale3.value else 1f)
          )
        }

        Spacer(Modifier.height(14.dp))

        // Stats Box
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .padding(12.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Time", color = Color.Gray, fontSize = 13.sp)
            Text(timeFormatted, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Matches", color = Color.Gray, fontSize = 13.sp)
            Text("$matches", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Rewards", color = Color.Gray, fontSize = 13.sp)
            Text("+$coinsEarned 🪙  +$gemsEarned 💎", color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
        }

        // Cultural Card Unlock Banner
        if (culturalCard != null) {
          Spacer(Modifier.height(12.dp))
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(TerracottaDark.copy(alpha = 0.6f))
              .border(1.dp, AmberGold, RoundedCornerShape(12.dp))
              .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "🎉 NEW DISCOVERY CARD!",
              color = AmberGold,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
              text = "${culturalCard.countryFlag} ${culturalCard.title}",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
            Text(
              text = culturalCard.subtitle,
              color = Color.LightGray,
              fontSize = 11.sp,
              textAlign = TextAlign.Center
            )
          }
        }

        Spacer(Modifier.height(18.dp))

        // Action Buttons
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onReplay,
            modifier = Modifier
              .weight(1f)
              .testTag("dialog_replay_button"),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmberGold)
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(4.dp))
            Text("Replay")
          }

          Button(
            onClick = onContinue,
            modifier = Modifier
              .weight(1.3f)
              .testTag("dialog_continue_button"),
            colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color(0xFF261300))
          ) {
            Text("Continue", fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(4.dp))
            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
          }
        }
      }
    }
  }
}

@Composable
fun PauseDialog(
  onResume: () -> Unit,
  onRestart: () -> Unit,
  onMap: () -> Unit,
  soundEnabled: Boolean,
  musicEnabled: Boolean,
  onToggleSound: (Boolean) -> Unit,
  onToggleMusic: (Boolean) -> Unit
) {
  Dialog(onDismissRequest = onResume) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = DarkSurface,
      border = androidx.compose.foundation.BorderStroke(1.5.dp, DarkBorder),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(
        modifier = Modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Text("GAME PAUSED", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))

        // Audio controls
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceEvenly
        ) {
          OutlinedButton(
            onClick = { onToggleSound(!soundEnabled) },
            colors = ButtonDefaults.outlinedButtonColors(
              containerColor = if (soundEnabled) AmberGoldDark else Color.Transparent,
              contentColor = Color.White
            )
          ) {
            Text(if (soundEnabled) "🔊 Sound ON" else "🔇 Sound OFF", fontSize = 12.sp)
          }

          OutlinedButton(
            onClick = { onToggleMusic(!musicEnabled) },
            colors = ButtonDefaults.outlinedButtonColors(
              containerColor = if (musicEnabled) NileAzure else Color.Transparent,
              contentColor = Color.White
            )
          ) {
            Text(if (musicEnabled) "🎵 Music ON" else "🎶 Music OFF", fontSize = 12.sp)
          }
        }

        Spacer(Modifier.height(20.dp))

        Button(
          onClick = onResume,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("pause_resume_button"),
          colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color(0xFF261300))
        ) {
          Text("Resume Game", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
          onClick = onRestart,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Restart Level", color = Color.White)
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
          onClick = onMap,
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("Africa Adventure Map", color = Color.LightGray)
        }
      }
    }
  }
}
