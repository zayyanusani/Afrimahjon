package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.AfricaRegistry
import com.example.data.model.CulturalDiscoveryCard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkWoodBg
import com.example.ui.theme.MalachiteGreen
import com.example.ui.theme.TerracottaPrimary
import com.example.viewmodel.GameViewModel

@Composable
fun CollectionScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val unlockedCardsList by viewModel.unlockedCards.collectAsState()
  val unlockedSet = remember(unlockedCardsList) { unlockedCardsList.map { it.cardId }.toSet() }

  val allCards = AfricaRegistry.CULTURAL_CARDS
  val unlockedCount = allCards.count { it.id in unlockedSet }
  val progressFraction = if (allCards.isNotEmpty()) unlockedCount.toFloat() / allCards.size else 0f
  val progressPercent = (progressFraction * 100).toInt()

  val categories = listOf("All", "Landmarks", "Wildlife", "Art", "Cities", "Fashion", "Nature")
  var selectedCategory by remember { mutableStateOf("All") }

  var activeCardDetail by remember { mutableStateOf<CulturalDiscoveryCard?>(null) }

  val filteredCards = remember(selectedCategory, allCards) {
    if (selectedCategory == "All") allCards
    else allCards.filter { it.category.equals(selectedCategory, ignoreCase = true) }
  }

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
        text = "AFRICAN CULTURAL ARCHIVE",
        color = AmberGold,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 1.sp
      )
      Text(
        text = "Cultural Discovery Cards",
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 22.sp
      )
      Spacer(Modifier.height(8.dp))

      // Progress bar
      Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Collection Completion", color = Color.LightGray, fontSize = 13.sp)
            Text("$progressPercent% ($unlockedCount/${allCards.size})", color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Spacer(Modifier.height(8.dp))
          LinearProgressIndicator(
            progress = { progressFraction },
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = AmberGold,
            trackColor = Color(0xFF38291F)
          )
        }
      }

      Spacer(Modifier.height(14.dp))

      // Category Filter Tabs
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(categories) { cat ->
          val isSelected = cat == selectedCategory
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(if (isSelected) AmberGold else DarkSurfaceElevated)
              .border(1.dp, if (isSelected) AmberGold else DarkBorder, RoundedCornerShape(20.dp))
              .clickable { selectedCategory = cat }
              .padding(horizontal = 14.dp, vertical = 6.dp)
          ) {
            Text(
              text = cat,
              color = if (isSelected) Color.Black else Color.White,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 12.sp
            )
          }
        }
      }

      Spacer(Modifier.height(16.dp))
    }

    // Cards list
    items(filteredCards) { card ->
      val isUnlocked = card.id in unlockedSet
      CulturalCardItem(
        card = card,
        isUnlocked = isUnlocked,
        onClick = {
          if (isUnlocked) activeCardDetail = card
        }
      )
      Spacer(Modifier.height(10.dp))
    }
  }

  // Active Card Detail Dialog
  activeCardDetail?.let { card ->
    CardDetailDialog(card = card, onDismiss = { activeCardDetail = null })
  }
}

@Composable
private fun CulturalCardItem(
  card: CulturalDiscoveryCard,
  isUnlocked: Boolean,
  onClick: () -> Unit
) {
  Card(
    colors = CardDefaults.cardColors(
      containerColor = if (isUnlocked) DarkSurface else Color(0xFF1E1712)
    ),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = if (isUnlocked) AmberGold.copy(alpha = 0.6f) else DarkBorder
    ),
    modifier = Modifier
      .fillMaxWidth()
      .clickable(enabled = isUnlocked) { onClick() }
      .testTag("cultural_card_${card.id}")
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Thumbnail or Lock placeholder
      Box(
        modifier = Modifier
          .size(64.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(DarkSurfaceElevated),
        contentAlignment = Alignment.Center
      ) {
        if (isUnlocked) {
          val drawableRes = when (card.id) {
            "card_zuma_rock" -> R.drawable.cultural_zuma_rock
            "card_maasai_mara" -> R.drawable.cultural_serengeti
            else -> R.drawable.hero_africa_adventure
          }
          Image(
            painter = painterResource(drawableRes),
            contentDescription = card.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        } else {
          Icon(Icons.Default.Lock, contentDescription = "Locked", tint = Color.Gray)
        }
      }

      Spacer(Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(card.countryFlag, fontSize = 16.sp)
          Spacer(Modifier.width(6.dp))
          Text(
            text = card.category.uppercase(),
            color = if (isUnlocked) AmberGold else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
          )
        }

        Spacer(Modifier.height(2.dp))

        Text(
          text = if (isUnlocked) card.title else "Mystery Discovery",
          color = if (isUnlocked) Color.White else Color.Gray,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )

        Text(
          text = if (isUnlocked) card.subtitle else "Unlocks at Level ${card.unlockLevel}",
          color = Color.LightGray,
          fontSize = 12.sp
        )
      }
    }
  }
}

@Composable
private fun CardDetailDialog(
  card: CulturalDiscoveryCard,
  onDismiss: () -> Unit
) {
  val drawableRes = when (card.id) {
    "card_zuma_rock" -> R.drawable.cultural_zuma_rock
    "card_maasai_mara" -> R.drawable.cultural_serengeti
    else -> R.drawable.hero_africa_adventure
  }

  Dialog(onDismissRequest = onDismiss) {
    Surface(
      shape = RoundedCornerShape(20.dp),
      color = DarkSurface,
      border = androidx.compose.foundation.BorderStroke(2.dp, AmberGold),
      modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(card.countryFlag, fontSize = 24.sp)
            Spacer(Modifier.width(8.dp))
            Column {
              Text(card.countryName, color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
              Text(card.category, color = Color.Gray, fontSize = 10.sp)
            }
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
          }
        }

        Spacer(Modifier.height(10.dp))

        // Card Image
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(12.dp))
        ) {
          Image(
            painter = painterResource(drawableRes),
            contentDescription = card.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
          )
        }

        Spacer(Modifier.height(12.dp))

        Text(
          text = card.title,
          color = Color.White,
          fontWeight = FontWeight.Black,
          fontSize = 18.sp,
          textAlign = TextAlign.Center
        )

        Text(
          text = card.subtitle,
          color = AmberGold,
          fontSize = 12.sp,
          fontWeight = FontWeight.Medium,
          textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(10.dp))

        // Factual Educational Story Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurfaceElevated)
            .padding(12.dp)
        ) {
          Text(
            text = card.factualDescription,
            color = Color(0xFFE8DDD0),
            fontSize = 13.sp,
            lineHeight = 18.sp
          )
        }
      }
    }
  }
}
