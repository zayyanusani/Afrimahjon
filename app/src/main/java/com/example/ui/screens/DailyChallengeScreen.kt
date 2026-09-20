package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.data.model.AfricaRegistry
import com.example.ui.components.TopCurrencyBar
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.DarkWoodBg
import com.example.ui.theme.MalachiteGreen
import com.example.ui.theme.TerracottaPrimary
import com.example.viewmodel.GameViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun DailyChallengeScreen(
  viewModel: GameViewModel,
  modifier: Modifier = Modifier
) {
  val profile by viewModel.profile.collectAsState()
  val progressList by viewModel.levelProgressList.collectAsState()
  val totalStars = progressList.sumOf { it.stars }

  val calendar = remember { Calendar.getInstance() }
  val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
  val todayFormatted = remember { SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.US).format(Date()) }

  // Deterministic featured country for today
  val featuredCountry = remember(dayOfYear) {
    AfricaRegistry.COUNTRIES[dayOfYear % AfricaRegistry.COUNTRIES.size]
  }

  // Pick special daily level index from country's level range
  val dailyLevelIndex = remember(dayOfYear, featuredCountry) {
    featuredCountry.startLevel + (dayOfYear % (featuredCountry.endLevel - featuredCountry.startLevel + 1))
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
      TopCurrencyBar(
        coins = profile?.coins ?: 0,
        gems = profile?.gems ?: 0,
        stars = totalStars
      )
      Spacer(Modifier.height(8.dp))

      Text(
        text = "DAILY EXPEDITION",
        color = AmberGold,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp,
        letterSpacing = 1.sp
      )
      Text(
        text = "Daily Africa Challenge",
        color = Color.White,
        fontWeight = FontWeight.Black,
        fontSize = 24.sp
      )
      Text(
        text = todayFormatted,
        color = Color.LightGray,
        fontSize = 13.sp
      )
      Spacer(Modifier.height(16.dp))
    }

    // Featured Daily Challenge Card
    item {
      Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, AmberGold),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Banner Image
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp)
              .clip(RoundedCornerShape(14.dp))
          ) {
            Image(
              painter = painterResource(R.drawable.hero_africa_adventure),
              contentDescription = "Daily Africa",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.verticalGradient(
                    listOf(Color.Transparent, Color(0xCC000000))
                  )
                )
            )

            Row(
              modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(featuredCountry.flag, fontSize = 28.sp)
              Spacer(Modifier.width(8.dp))
              Column {
                Text(
                  text = "Today: ${featuredCountry.name}",
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                )
                Text(
                  text = "Special Landmark: ${featuredCountry.highlightLandmark}",
                  color = AmberGold,
                  fontSize = 12.sp
                )
              }
            }
          }

          Spacer(Modifier.height(14.dp))

          // Rewards Box
          Text("CHALLENGE REWARDS", color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          Spacer(Modifier.height(6.dp))
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(DarkSurface)
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceAround
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🪙", fontSize = 18.sp)
              Spacer(Modifier.width(6.dp))
              Text("+500 Coins", color = AmberGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("💎", fontSize = 18.sp)
              Spacer(Modifier.width(6.dp))
              Text("+5 Gems", color = Color(0xFF38BDF8), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("⭐", fontSize = 18.sp)
              Spacer(Modifier.width(6.dp))
              Text("+1 Star", color = Color(0xFFFFD700), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }

          Spacer(Modifier.height(16.dp))

          Button(
            onClick = { viewModel.startLevel(dailyLevelIndex, isDaily = true) },
            colors = ButtonDefaults.buttonColors(containerColor = AmberGold, contentColor = Color.Black),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("play_daily_challenge_button")
          ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(6.dp))
            Text("Play Daily Challenge", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }
        }
      }
    }

    item {
      Spacer(Modifier.height(20.dp))
      Text(
        text = "7-DAY EXPLORER STREAK",
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
      Spacer(Modifier.height(8.dp))

      Card(
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
          days.forEachIndexed { idx, day ->
            val isPassed = idx < 3 // Current streak progress visualization
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(day, color = Color.Gray, fontSize = 11.sp)
              Spacer(Modifier.height(6.dp))
              Box(
                modifier = Modifier
                  .size(34.dp)
                  .clip(CircleShape)
                  .background(if (isPassed) AmberGold else DarkSurfaceElevated)
                  .border(1.dp, if (isPassed) AmberGold else DarkBorder, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                if (isPassed) {
                  Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                } else {
                  Text("${idx + 1}", color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }
  }
}
