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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.TopCurrencyBar
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkWoodBg
import com.example.ui.theme.MalachiteGreen
import com.example.ui.theme.NileAzure
import com.example.viewmodel.GameViewModel

@Composable
fun MarketScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.profile.collectAsState()
  val progressList by viewModel.levelProgressList.collectAsState()
  val totalStars = progressList.sumOf { it.stars }

  val coins = profile?.coins ?: 0
  val gems = profile?.gems ?: 0

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkWoodBg)
      .padding(horizontal = 16.dp)
      .padding(bottom = 70.dp)
  ) {
    item {
      Spacer(Modifier.height(16.dp))
      TopCurrencyBar(coins = coins, gems = gems, stars = totalStars)
      Spacer(Modifier.height(8.dp))

      Text(
        text = "IN-GAME BAZAAR",
        color = AmberGold,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 1.sp
      )
      Text(
        text = "Expedition Market",
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp
      )
      Text(
        text = "Exchange earned coins and gems for booster supplies.",
        color = Color.LightGray,
        fontSize = 13.sp
      )
      Spacer(Modifier.height(16.dp))
    }

    item {
      MarketItemCard(
        title = "3x Hint Sparks",
        description = "Reveals an available matching pair with African gold glow.",
        icon = Icons.Default.Lightbulb,
        accentColor = AmberGold,
        coinCost = 150,
        gemCost = 0,
        playerCoins = coins,
        playerGems = gems,
        onBuy = { viewModel.purchasePowerUp("hint", 3, 150, 0) },
        testTag = "buy_hints_button"
      )
      Spacer(Modifier.height(10.dp))
    }

    item {
      MarketItemCard(
        title = "3x Board Shuffles",
        description = "Rearranges remaining tiles on the board, ensuring open moves.",
        icon = Icons.Default.Shuffle,
        accentColor = NileAzure,
        coinCost = 200,
        gemCost = 0,
        playerCoins = coins,
        playerGems = gems,
        onBuy = { viewModel.purchasePowerUp("shuffle", 3, 200, 0) },
        testTag = "buy_shuffles_button"
      )
      Spacer(Modifier.height(10.dp))
    }

    item {
      MarketItemCard(
        title = "3x Time Bonus (+60s)",
        description = "Adds 60 precious seconds to your countdown timer.",
        icon = Icons.Default.HourglassBottom,
        accentColor = MalachiteGreen,
        coinCost = 200,
        gemCost = 0,
        playerCoins = coins,
        playerGems = gems,
        onBuy = { viewModel.purchasePowerUp("time", 3, 200, 0) },
        testTag = "buy_time_button"
      )
      Spacer(Modifier.height(10.dp))
    }

    item {
      MarketItemCard(
        title = "2x Power Tile Strikes",
        description = "Instantly selects and eliminates a valid free matching pair.",
        icon = Icons.Default.Bolt,
        accentColor = Color(0xFFA855F7),
        coinCost = 300,
        gemCost = 0,
        playerCoins = coins,
        playerGems = gems,
        onBuy = { viewModel.purchasePowerUp("powertile", 2, 300, 0) },
        testTag = "buy_powertile_button"
      )
      Spacer(Modifier.height(10.dp))
    }

    item {
      MarketItemCard(
        title = "Safari Grand Bundle",
        description = "5 Hints, 5 Shuffles, 5 Time Boosts, and 3 Power Tile Strikes.",
        icon = Icons.Default.Bolt,
        accentColor = Color(0xFFF59E0B),
        coinCost = 0,
        gemCost = 15,
        playerCoins = coins,
        playerGems = gems,
        onBuy = { viewModel.purchasePowerUp("hint", 5, 0, 15) },
        testTag = "buy_bundle_button"
      )
    }
  }
}

@Composable
private fun MarketItemCard(
  title: String,
  description: String,
  icon: ImageVector,
  accentColor: Color,
  coinCost: Int,
  gemCost: Int,
  playerCoins: Int,
  playerGems: Int,
  onBuy: () -> Unit,
  testTag: String
) {
  val canAfford = (coinCost == 0 || playerCoins >= coinCost) && (gemCost == 0 || playerGems >= gemCost)

  Card(
    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, if (canAfford) accentColor.copy(alpha = 0.5f) else DarkBorder),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(DarkSurface)
          .border(1.5.dp, accentColor, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
      ) {
        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(26.dp))
      }

      Spacer(Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(description, color = Color.LightGray, fontSize = 12.sp, lineHeight = 16.sp)
      }

      Spacer(Modifier.width(8.dp))

      Button(
        onClick = onBuy,
        enabled = canAfford,
        colors = ButtonDefaults.buttonColors(
          containerColor = if (gemCost > 0) NileAzure else AmberGold,
          contentColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag(testTag)
      ) {
        val priceText = if (gemCost > 0) "$gemCost 💎" else "$coinCost 🪙"
        Text(priceText, fontWeight = FontWeight.Bold, fontSize = 12.sp)
      }
    }
  }
}
