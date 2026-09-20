package com.example.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.viewmodel.GameScreen

data class NavItem(
  val screen: GameScreen,
  val label: String,
  val emoji: String,
  val testTag: String
)

@Composable
fun AfriMahjongBottomNav(
  currentScreen: GameScreen,
  onSelect: (GameScreen) -> Unit,
  modifier: Modifier = Modifier
) {
  val items = listOf(
    NavItem(GameScreen.MAP, "Journey", "🗺️", "nav_map"),
    NavItem(GameScreen.COLLECTION, "Archive", "📜", "nav_collection"),
    NavItem(GameScreen.DAILY, "Daily", "☀️", "nav_daily"),
    NavItem(GameScreen.MARKET, "Bazaar", "🛒", "nav_market"),
    NavItem(GameScreen.PROFILE, "Explorer", "👤", "nav_profile")
  )

  Row(
    modifier = modifier
      .fillMaxWidth()
      .height(64.dp)
      .background(DarkSurface)
      .border(1.dp, DarkBorder)
      .padding(horizontal = 6.dp, vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceAround,
    verticalAlignment = Alignment.CenterVertically
  ) {
    items.forEach { item ->
      val isSelected = currentScreen == item.screen
      Column(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .background(if (isSelected) DarkSurfaceElevated else Color.Transparent)
          .clickable { onSelect(item.screen) }
          .padding(vertical = 4.dp)
          .testTag(item.testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = item.emoji,
          fontSize = 18.sp
        )
        Spacer(Modifier.height(2.dp))
        Text(
          text = item.label,
          color = if (isSelected) AmberGold else Color.Gray,
          fontSize = 10.sp,
          fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
      }
    }
  }
}
