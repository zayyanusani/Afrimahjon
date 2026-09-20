package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AfricaRegistry
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkWoodBg
import com.example.ui.theme.NileAzure
import com.example.ui.theme.TerracottaPrimary
import com.example.viewmodel.GameScreen
import com.example.viewmodel.GameViewModel

data class AvatarOption(val id: String, val name: String, val emoji: String)

@Composable
fun ProfileScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.profile.collectAsState()
  val progressList by viewModel.levelProgressList.collectAsState()
  val unlockedCards by viewModel.unlockedCards.collectAsState()

  val totalStars = progressList.sumOf { it.stars }
  val countriesUnlocked = AfricaRegistry.COUNTRIES.count { totalStars >= it.unlockStarsRequired }
  val collectionPct = if (AfricaRegistry.CULTURAL_CARDS.isNotEmpty()) {
    (unlockedCards.size * 100) / AfricaRegistry.CULTURAL_CARDS.size
  } else 0

  val avatars = listOf(
    AvatarOption("explorer", "Explorer", "🧭"),
    AvatarOption("warrior", "Maasai Scout", "🛡️"),
    AvatarOption("queen", "Savannah Queen", "👑"),
    AvatarOption("nomad", "Atlas Nomad", "🐪"),
    AvatarOption("guide", "Nile Navigator", "⛵")
  )

  var isEditingName by remember { mutableStateOf(false) }
  var nameInput by remember(profile?.username) { mutableStateOf(profile?.username ?: "Explorer") }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkWoodBg)
      .padding(horizontal = 16.dp)
      .padding(bottom = 70.dp)
  ) {
    item {
      Spacer(Modifier.height(16.dp))
      Text(
        text = "PLAYER IDENTITY",
        color = AmberGold,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 1.sp
      )
      Text(
        text = "Explorer Profile",
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp
      )
      Spacer(Modifier.height(16.dp))
    }

    // Avatar and Profile Header Card
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f)),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          modifier = Modifier.padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Current Avatar
          val currentAvatar = avatars.find { it.id == profile?.avatarId } ?: avatars.first()
          Box(
            modifier = Modifier
              .size(80.dp)
              .clip(CircleShape)
              .background(TerracottaPrimary)
              .border(2.5.dp, AmberGold, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Text(currentAvatar.emoji, fontSize = 40.sp)
          }

          Spacer(Modifier.height(10.dp))

          // Username with inline edit
          if (isEditingName) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.fillMaxWidth()
            ) {
              OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                modifier = Modifier.weight(1f),
                singleLine = true
              )
              IconButton(onClick = {
                viewModel.updateUsername(nameInput)
                isEditingName = false
              }) {
                Icon(Icons.Default.Check, contentDescription = "Save", tint = AmberGold)
              }
            }
          } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = profile?.username ?: "Explorer",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
              )
              IconButton(onClick = { isEditingName = true }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Name", tint = Color.LightGray, modifier = Modifier.size(16.dp))
              }
            }
          }

          Text(
            text = "Level ${(profile?.currentLevel ?: 1)} • ${currentAvatar.name}",
            color = AmberGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )

          Spacer(Modifier.height(16.dp))

          // Avatar picker options
          Text("CHOOSE AVATAR", color = Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
          Spacer(Modifier.height(6.dp))
          Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            avatars.forEach { av ->
              val isSelected = av.id == profile?.avatarId
              Box(
                modifier = Modifier
                  .size(46.dp)
                  .clip(CircleShape)
                  .background(if (isSelected) AmberGold else DarkSurface)
                  .border(if (isSelected) 2.dp else 1.dp, if (isSelected) Color.White else DarkBorder, CircleShape)
                  .clickable { viewModel.updateAvatar(av.id) }
                  .testTag("avatar_${av.id}"),
                contentAlignment = Alignment.Center
              ) {
                Text(av.emoji, fontSize = 22.sp)
              }
            }
          }
        }
      }
    }

    // Stats Grid
    item {
      Spacer(Modifier.height(16.dp))
      Text("ADVENTURE STATISTICS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Spacer(Modifier.height(8.dp))

      Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          StatRow("Total Stars Earned", "$totalStars ⭐")
          StatRow("Coins Balance", "${profile?.coins ?: 0} 🪙")
          StatRow("Gems Balance", "${profile?.gems ?: 0} 💎")
          StatRow("Countries Unlocked", "$countriesUnlocked / 18 🌍")
          StatRow("Collection Unlocked", "$collectionPct% 📜")
          StatRow("Puzzles Solved", "${profile?.puzzlesSolved ?: 0} 🧩")
          StatRow("Perfect 3-Star Clears", "${profile?.perfectPuzzles ?: 0} ✨")
        }
      }
    }

    // Quick Achievements Link
    item {
      Spacer(Modifier.height(16.dp))
      Button(
        onClick = { viewModel.navigateTo(GameScreen.ACHIEVEMENTS) },
        colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated, contentColor = AmberGold),
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f)),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("view_achievements_button")
      ) {
        Icon(Icons.Default.EmojiEvents, contentDescription = null)
        Spacer(Modifier.width(8.dp))
        Text("View Achievements & Claim Rewards", fontWeight = FontWeight.Bold, fontSize = 14.sp)
      }
    }

    // Audio & Game Settings
    item {
      Spacer(Modifier.height(16.dp))
      Text("GAME AUDIO SETTINGS", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
      Spacer(Modifier.height(8.dp))

      Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Sound Effects", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
              Text("Wooden clacks & Kalimba chimes", color = Color.Gray, fontSize = 12.sp)
            }
            Switch(
              checked = profile?.soundEnabled ?: true,
              onCheckedChange = { viewModel.toggleSound(it) },
              colors = SwitchDefaults.colors(checkedThumbColor = AmberGold, checkedTrackColor = Color(0xFF6B4810))
            )
          }

          Spacer(Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text("Ambient Music", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
              Text("Gentle acoustic African melodies", color = Color.Gray, fontSize = 12.sp)
            }
            Switch(
              checked = profile?.musicEnabled ?: true,
              onCheckedChange = { viewModel.toggleMusic(it) },
              colors = SwitchDefaults.colors(checkedThumbColor = NileAzure, checkedTrackColor = Color(0xFF034C75))
            )
          }
        }
      }
    }
  }
}

@Composable
private fun StatRow(label: String, value: String) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(label, color = Color.LightGray, fontSize = 13.sp)
    Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
  }
}
