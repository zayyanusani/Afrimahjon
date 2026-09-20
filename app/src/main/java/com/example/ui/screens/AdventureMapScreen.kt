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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.db.LevelProgressEntity
import com.example.data.model.AfricaRegistry
import com.example.data.model.CountryChapter
import com.example.ui.components.TopCurrencyBar
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkWoodBg
import com.example.ui.theme.MalachiteGreen
import com.example.ui.theme.TerracottaDark
import com.example.ui.theme.TerracottaPrimary
import com.example.viewmodel.GameViewModel

@Composable
fun AdventureMapScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.profile.collectAsState()
  val progressList by viewModel.levelProgressList.collectAsState()

  val totalStars = progressList.sumOf { it.stars }
  val progressMap = remember(progressList) { progressList.associateBy { it.levelNumber } }

  // Track expanded country chapter
  var expandedCountryId by remember { mutableStateOf("NG") }

  val latestUnlockedLevel = progressList.filter { it.unlocked }.maxOfOrNull { it.levelNumber } ?: 1

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(DarkWoodBg)
      .padding(bottom = 70.dp)
  ) {
    // Hero Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(180.dp)
      ) {
        Image(
          painter = painterResource(R.drawable.hero_africa_adventure),
          contentDescription = "Africa Adventure",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Gradient overlay
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Transparent,
                  DarkWoodBg.copy(alpha = 0.85f),
                  DarkWoodBg
                )
              )
            )
        )

        Column(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(16.dp)
        ) {
          Text(
            text = "AFRIMAHJONG",
            color = AmberGold,
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
          )
          Text(
            text = "Play. Explore. Discover Africa.",
            color = Color(0xFFF3ECE4),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Top Currencies
    item {
      TopCurrencyBar(
        coins = profile?.coins ?: 0,
        gems = profile?.gems ?: 0,
        stars = totalStars
      )
    }

    // Continue Journey CTA
    item {
      val nextCountry = AfricaRegistry.getCountryForLevel(latestUnlockedLevel)
      Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.5f)),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 6.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text("CURRENT EXPEDITION", color = AmberGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(
              text = "Level $latestUnlockedLevel • ${nextCountry.flag} ${nextCountry.name}",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            Text(
              text = "Landmark: ${nextCountry.highlightLandmark}",
              color = Color.LightGray,
              fontSize = 12.sp
            )
          }

          Button(
            onClick = { viewModel.startLevel(latestUnlockedLevel) },
            colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.testTag("continue_journey_button")
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text("Play", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // Chapters Section Header
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "CHAPTERS & COUNTRIES (18)",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
        Text(
          text = "500 Levels",
          color = AmberGold,
          fontWeight = FontWeight.SemiBold,
          fontSize = 12.sp
        )
      }
    }

    // 18 Country Chapters List
    items(AfricaRegistry.COUNTRIES) { country ->
      CountryChapterItem(
        country = country,
        totalStars = totalStars,
        progressMap = progressMap,
        isExpanded = expandedCountryId == country.id,
        onToggleExpand = {
          expandedCountryId = if (expandedCountryId == country.id) "" else country.id
        },
        onLevelClick = { lvl -> viewModel.startLevel(lvl) }
      )
    }
  }
}

@Composable
private fun CountryChapterItem(
  country: CountryChapter,
  totalStars: Int,
  progressMap: Map<Int, LevelProgressEntity>,
  isExpanded: Boolean,
  onToggleExpand: () -> Unit,
  onLevelClick: (Int) -> Unit
) {
  val isCountryUnlocked = totalStars >= country.unlockStarsRequired
  val levels = (country.startLevel..country.endLevel).toList()

  val countryStars = levels.sumOf { progressMap[it]?.stars ?: 0 }
  val maxCountryStars = levels.size * 3
  val starsFraction = if (maxCountryStars > 0) countryStars.toFloat() / maxCountryStars else 0f

  Card(
    colors = CardDefaults.cardColors(
      containerColor = if (isExpanded) DarkSurfaceElevated else DarkSurface
    ),
    shape = RoundedCornerShape(16.dp),
    border = androidx.compose.foundation.BorderStroke(
      width = if (isExpanded) 1.5.dp else 1.dp,
      color = if (isExpanded) AmberGold else DarkBorder
    ),
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
      .clickable { onToggleExpand() }
      .testTag("country_chapter_${country.id}")
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(country.flag, fontSize = 28.sp)
          Spacer(Modifier.width(10.dp))
          Column {
            Text(
              text = country.name,
              color = Color.White,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Text(
              text = "${country.region} • Capital: ${country.capital}",
              color = Color.Gray,
              fontSize = 12.sp
            )
          }
        }

        if (isCountryUnlocked) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "$countryStars/$maxCountryStars",
              color = AmberGold,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
            Icon(Icons.Default.Star, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
          }
        } else {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(Color(0xFF382618))
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Icon(Icons.Default.Lock, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text("${country.unlockStarsRequired} ⭐", color = Color.LightGray, fontSize = 12.sp)
          }
        }
      }

      Spacer(Modifier.height(8.dp))
      LinearProgressIndicator(
        progress = { starsFraction },
        modifier = Modifier
          .fillMaxWidth()
          .height(4.dp)
          .clip(RoundedCornerShape(2.dp)),
        color = if (isCountryUnlocked) MalachiteGreen else Color.Gray,
        trackColor = Color(0xFF38291F)
      )

      if (isExpanded) {
        Spacer(Modifier.height(12.dp))
        Text(
          text = country.description,
          color = Color(0xFFD4C5B8),
          fontSize = 12.sp
        )

        Spacer(Modifier.height(10.dp))
        Text(
          text = "Levels (${country.startLevel} – ${country.endLevel})",
          color = AmberGold,
          fontWeight = FontWeight.SemiBold,
          fontSize = 13.sp
        )

        Spacer(Modifier.height(8.dp))

        // Level grid chips in rows of 7
        val chunked = levels.chunked(7)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          for (rowLevels in chunked) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              for (lvl in rowLevels) {
                val lvlData = progressMap[lvl]
                val isUnlocked = isCountryUnlocked && (lvlData?.unlocked == true || lvl == country.startLevel)
                val stars = lvlData?.stars ?: 0

                LevelTileButton(
                  levelNumber = lvl,
                  isUnlocked = isUnlocked,
                  stars = stars,
                  onClick = { if (isUnlocked) onLevelClick(lvl) }
                )
              }
              // Pad empty spaces if last row is incomplete
              for (i in rowLevels.size until 7) {
                Spacer(modifier = Modifier.size(40.dp))
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun LevelTileButton(
  levelNumber: Int,
  isUnlocked: Boolean,
  stars: Int,
  onClick: () -> Unit
) {
  val shape = RoundedCornerShape(10.dp)
  Box(
    modifier = Modifier
      .size(40.dp)
      .clip(shape)
      .background(
        when {
          !isUnlocked -> Color(0xFF261D16)
          stars == 3 -> AmberGold.copy(alpha = 0.25f)
          stars > 0 -> DarkSurfaceElevated
          else -> DarkSurfaceElevated
        }
      )
      .border(
        width = 1.dp,
        color = when {
          !isUnlocked -> DarkBorder
          stars == 3 -> AmberGold
          else -> Color(0xFF6B5542)
        },
        shape = shape
      )
      .clickable(enabled = isUnlocked) { onClick() }
      .testTag("level_btn_$levelNumber"),
    contentAlignment = Alignment.Center
  ) {
    if (!isUnlocked) {
      Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
    } else {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
          text = "$levelNumber",
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 11.sp
        )
        if (stars > 0) {
          Row {
            repeat(stars) {
              Icon(Icons.Default.Star, contentDescription = null, tint = AmberGold, modifier = Modifier.size(7.dp))
            }
          }
        }
      }
    }
  }
}
