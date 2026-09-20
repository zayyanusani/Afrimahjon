package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AchievementDef
import com.example.data.model.AchievementRegistry
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkWoodBg
import com.example.ui.theme.MalachiteGreen
import com.example.ui.theme.TerracottaPrimary
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel

@Composable
fun AchievementsScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val achievementsList by viewModel.achievements.collectAsState()
  val progressMap = remember(achievementsList) { achievementsList.associateBy { it.achievementId } }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkWoodBg)
      .padding(horizontal = 16.dp)
      .padding(bottom = 70.dp)
  ) {
    item {
      Spacer(Modifier.height(16.dp))
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { viewModel.navigateTo(GameScreen.PROFILE) }) {
          Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AmberGold)
        }
        Spacer(Modifier.width(4.dp))
        Column {
          Text(
            text = "TROPHY ROOM",
            color = AmberGold,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.sp
          )
          Text(
            text = "Achievements",
            color = Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 22.sp
          )
        }
      }
      Spacer(Modifier.height(16.dp))
    }

    items(AchievementRegistry.ALL_ACHIEVEMENTS) { def ->
      val progress = progressMap[def.id]
      val curProgress = (progress?.currentProgress ?: 0).coerceAtMost(def.target)
      val isCompleted = curProgress >= def.target
      val isClaimed = progress?.isClaimed == true

      AchievementCard(
        def = def,
        currentProgress = curProgress,
        isCompleted = isCompleted,
        isClaimed = isClaimed,
        onClaim = { viewModel.claimAchievement(def.id) }
      )
      Spacer(Modifier.height(10.dp))
    }
  }
}

@Composable
private fun AchievementCard(
  def: AchievementDef,
  currentProgress: Int,
  isCompleted: Boolean,
  isClaimed: Boolean,
  onClaim: () -> Unit
) {
  val fraction = (currentProgress.toFloat() / def.target.toFloat()).coerceIn(0f, 1f)

  Card(
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = if (isCompleted && !isClaimed) AmberGold else DarkBorder
    ),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface),
          contentAlignment = Alignment.Center
        ) {
          Text(def.iconEmoji, fontSize = 24.sp)
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(def.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Text(def.description, color = Color.LightGray, fontSize = 12.sp)
        }

        Spacer(Modifier.width(8.dp))

        if (isClaimed) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF263820))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.Check, contentDescription = null, tint = MalachiteGreen, modifier = Modifier.size(14.dp))
              Spacer(Modifier.width(2.dp))
              Text("Claimed", color = MalachiteGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
          }
        } else if (isCompleted) {
          Button(
            onClick = onClaim,
            colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("claim_${def.id}")
          ) {
            Text("Claim", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }

      Spacer(Modifier.height(10.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        LinearProgressIndicator(
          progress = { fraction },
          modifier = Modifier
            .weight(1f)
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp)),
          color = if (isCompleted) MalachiteGreen else AmberGold,
          trackColor = Color(0xFF38291F)
        )
        Spacer(Modifier.width(12.dp))
        Text(
          text = "$currentProgress/${def.target}",
          color = Color.LightGray,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
      }

      Spacer(Modifier.height(6.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
      ) {
        Text("Reward: +${def.rewardCoins} 🪙  +${def.rewardGems} 💎", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  }
}
