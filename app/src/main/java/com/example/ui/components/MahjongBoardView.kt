package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.core.MahjongRuleEngine
import com.example.game.core.MahjongTileInstance
import com.example.ui.theme.AmberGold
import com.example.ui.theme.TileHintGlow
import com.example.ui.theme.TileIvoryBottom
import com.example.ui.theme.TileIvorySide
import com.example.ui.theme.TileIvoryTop
import com.example.ui.theme.TileSelectedGlow

@Composable
fun MahjongBoardView(
  tiles: List<MahjongTileInstance>,
  onTileClick: (MahjongTileInstance) -> Unit,
  modifier: Modifier = Modifier
) {
  BoxWithConstraints(
    modifier = modifier
      .fillMaxSize()
      .padding(8.dp),
    contentAlignment = Alignment.Center
  ) {
    if (tiles.isEmpty()) return@BoxWithConstraints

    val minCol = tiles.minOf { it.col }
    val maxCol = tiles.maxOf { it.col }
    val minRow = tiles.minOf { it.row }
    val maxRow = tiles.maxOf { it.row }

    val colsSpan = (maxCol - minCol + 1).coerceAtLeast(1)
    val rowsSpan = (maxRow - minRow + 1).coerceAtLeast(1)

    // Compute tile dimensions to comfortably fit available screen
    val availableW = maxWidth.value
    val availableH = maxHeight.value

    // Tile aspect ratio roughly 1 : 1.3
    val tileW = (availableW / (colsSpan + 0.8f)).coerceIn(40f, 62f)
    val tileH = (tileW * 1.32f).coerceAtMost(availableH / (rowsSpan + 0.8f))

    val layerOffsetX = 4f
    val layerOffsetY = -5f

    // Center board
    val boardW = colsSpan * tileW
    val boardH = rowsSpan * tileH
    val startX = (availableW - boardW) / 2f
    val startY = (availableH - boardH) / 2f

    // Sort tiles by layer ascending so lower layers render behind higher layers
    val sortedTiles = remember(tiles) {
      tiles.sortedWith(compareBy<MahjongTileInstance> { it.layer }.thenBy { it.row }.thenBy { it.col })
    }

    // Hint pulsing animation
    val infiniteTransition = rememberInfiniteTransition(label = "hint_pulse")
    val hintPulseAlpha by infiniteTransition.animateFloat(
      initialValue = 0.4f,
      targetValue = 1.0f,
      animationSpec = infiniteRepeatable(
        animation = tween(600),
        repeatMode = RepeatMode.Reverse
      ),
      label = "hintAlpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
      for (tile in sortedTiles) {
        if (tile.isMatched) continue

        val isFree = remember(tile, tiles) {
          MahjongRuleEngine.isTileFree(tile, tiles)
        }

        val posX = startX + (tile.col - minCol) * tileW + (tile.layer * layerOffsetX)
        val posY = startY + (tile.row - minRow) * tileH + (tile.layer * layerOffsetY)

        // Selected lift
        val liftOffset = if (tile.isSelected) -6f else 0f

        SingleTileItem(
          tile = tile,
          isFree = isFree,
          widthDp = tileW,
          heightDp = tileH,
          hintAlpha = if (tile.isHinted) hintPulseAlpha else 1f,
          modifier = Modifier
            .offset(x = posX.dp, y = (posY + liftOffset).dp)
            .testTag("tile_${tile.instanceId}")
            .clickable(
              interactionSource = remember { MutableInteractionSource() },
              indication = null
            ) {
              onTileClick(tile)
            }
        )
      }
    }
  }
}

@Composable
private fun SingleTileItem(
  tile: MahjongTileInstance,
  isFree: Boolean,
  widthDp: Float,
  heightDp: Float,
  hintAlpha: Float,
  modifier: Modifier = Modifier
) {
  val shape = RoundedCornerShape(8.dp)
  val isSelected = tile.isSelected
  val isHinted = tile.isHinted

  val borderBrush = when {
    isSelected -> Brush.linearGradient(listOf(TileSelectedGlow, AmberGold))
    isHinted -> Brush.linearGradient(listOf(TileHintGlow.copy(alpha = hintAlpha), Color.White))
    isFree -> Brush.linearGradient(listOf(Color(0xFFE8DAC5), Color(0xFF9E8B70)))
    else -> Brush.linearGradient(listOf(Color(0xFF706052), Color(0xFF483A2E)))
  }

  val elevationDp = when {
    isSelected -> 10.dp
    isFree -> (4 + tile.layer * 2).dp
    else -> (1 + tile.layer).dp
  }

  Box(
    modifier = modifier
      .size(widthDp.dp, heightDp.dp)
      .shadow(elevationDp, shape, clip = false)
      .clip(shape)
      .background(
        Brush.verticalGradient(
          colors = listOf(
            TileIvoryTop,
            TileIvorySide,
            TileIvoryBottom
          )
        )
      )
      .border(
        width = if (isSelected || isHinted) 2.5.dp else 1.dp,
        brush = borderBrush,
        shape = shape
      )
      .padding(horizontal = 2.dp, vertical = 3.dp),
    contentAlignment = Alignment.Center
  ) {
    // If not free, slight dimming overlay to aid visual scanning
    if (!isFree) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0x3A000000), shape)
      )
    }

    Column(
      modifier = Modifier.fillMaxSize(),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      // Category color stripe
      Box(
        modifier = Modifier
          .size(width = (widthDp * 0.5f).dp, height = 3.dp)
          .clip(RoundedCornerShape(2.dp))
          .background(tile.definition.category.badgeColor)
      )

      // Main Tile Symbol / Emoji
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxSize(),
        contentAlignment = Alignment.Center
      ) {
        Text(
          text = tile.definition.symbol,
          fontSize = (widthDp * 0.42f).sp,
          textAlign = TextAlign.Center
        )
      }

      // Tile Name Label
      Text(
        text = tile.definition.name,
        color = if (isFree) Color(0xFF26190E) else Color(0xFF786A5E),
        fontSize = (widthDp * 0.16f).coerceIn(7f, 10f).sp,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 1.dp)
      )
    }
  }
}
