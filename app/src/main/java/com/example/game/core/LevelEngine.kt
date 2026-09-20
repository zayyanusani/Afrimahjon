package com.example.game.core

import com.example.data.model.AfricaRegistry
import com.example.data.model.TileCategory
import com.example.data.model.TileDefinition
import com.example.data.model.TileRegistry
import kotlin.random.Random

enum class GameDifficulty(val title: String, val levelRange: String) {
  BEGINNER("Beginner", "Levels 1–50"),
  INTERMEDIATE("Intermediate", "Levels 51–200"),
  ADVANCED("Advanced", "Levels 201–400"),
  EXPERT("Expert", "Levels 401–500");

  companion object {
    fun fromLevel(level: Int): GameDifficulty = when (level) {
      in 1..50 -> BEGINNER
      in 51..200 -> INTERMEDIATE
      in 201..400 -> ADVANCED
      else -> EXPERT
    }
  }
}

data class LevelMetadata(
  val levelNumber: Int,
  val countryId: String,
  val difficulty: GameDifficulty,
  val layoutName: String,
  val totalTiles: Int,
  val targetTimeSeconds: Int,
  val star3Time: Int,
  val star2Time: Int,
  val star1Time: Int,
  val coinReward: Int,
  val gemReward: Int,
  val culturalCardRewardId: String? = null
)

data class BoardPosition(
  val col: Int,
  val row: Int,
  val layer: Int
)

data class MahjongTileInstance(
  val instanceId: Int,
  var tileId: String,
  var definition: TileDefinition,
  val col: Int,
  val row: Int,
  val layer: Int,
  var isSelected: Boolean = false,
  var isMatched: Boolean = false,
  var isHinted: Boolean = false
)

object LevelEngine {

  fun getLevelMetadata(levelNumber: Int): LevelMetadata {
    val country = AfricaRegistry.getCountryForLevel(levelNumber)
    val difficulty = GameDifficulty.fromLevel(levelNumber)

    val (tileCount, targetTime, star3Time, star2Time, star1Time, coinReward, gemReward, layout) = when (difficulty) {
      GameDifficulty.BEGINNER -> {
        val count = if (levelNumber <= 10) 24 else if (levelNumber <= 25) 32 else 40
        val baseTime = 180
        EightTuple(count, baseTime, 90, 130, 180, 150, 1, "Zuma & Shield")
      }
      GameDifficulty.INTERMEDIATE -> {
        val count = if (levelNumber <= 100) 44 else if (levelNumber <= 150) 52 else 60
        val baseTime = 210
        EightTuple(count, baseTime, 110, 160, 210, 250, 2, "Pyramid & Dunes")
      }
      GameDifficulty.ADVANCED -> {
        val count = if (levelNumber <= 300) 64 else 72
        val baseTime = 240
        EightTuple(count, baseTime, 130, 180, 240, 400, 3, "Kilimanjaro Peak")
      }
      GameDifficulty.EXPERT -> {
        val count = if (levelNumber <= 450) 76 else 84
        val baseTime = 270
        EightTuple(count, baseTime, 150, 210, 270, 600, 5, "Great Zimbabwe Fortress")
      }
    }

    val cardReward = AfricaRegistry.getCardByLevel(levelNumber)?.id

    return LevelMetadata(
      levelNumber = levelNumber,
      countryId = country.id,
      difficulty = difficulty,
      layoutName = layout,
      totalTiles = tileCount,
      targetTimeSeconds = targetTime,
      star3Time = star3Time,
      star2Time = star2Time,
      star1Time = star1Time,
      coinReward = coinReward,
      gemReward = gemReward,
      culturalCardRewardId = cardReward
    )
  }

  /**
   * Generates a balanced, mobile-friendly board layout for the specified level.
   * Guarantees an even number of tile positions and generates matching pairs.
   */
  fun generateBoardForLevel(levelNumber: Int): List<MahjongTileInstance> {
    val meta = getLevelMetadata(levelNumber)
    val totalTiles = meta.totalTiles
    val random = Random(levelNumber.toLong() * 9973L)

    // Generate positions tailored for vertical mobile screen (max 7-8 cols, 8-9 rows)
    val positions = generatePositions(totalTiles, meta.difficulty, random)

    // Select distinct tile definitions to use
    val pairCount = totalTiles / 2
    // Use 6 to 12 distinct tile types for healthy matchability
    val distinctTypesCount = when (meta.difficulty) {
      GameDifficulty.BEGINNER -> 6.coerceAtMost(pairCount)
      GameDifficulty.INTERMEDIATE -> 8.coerceAtMost(pairCount)
      GameDifficulty.ADVANCED -> 10.coerceAtMost(pairCount)
      GameDifficulty.EXPERT -> 12.coerceAtMost(pairCount)
    }

    val availableDefs = TileRegistry.getTilesForLevel(levelNumber, distinctTypesCount)

    // Distribute pairs
    val tilePairs = mutableListOf<TileDefinition>()
    for (i in 0 until pairCount) {
      val def = availableDefs[i % availableDefs.size]
      tilePairs.add(def)
      tilePairs.add(def)
    }

    // Shuffle the pairs with deterministic seed
    val shuffledDefs = tilePairs.shuffled(random)

    return positions.mapIndexed { index, pos ->
      val def = shuffledDefs[index]
      MahjongTileInstance(
        instanceId = index,
        tileId = def.id,
        definition = def,
        col = pos.col,
        row = pos.row,
        layer = pos.layer
      )
    }
  }

  private fun generatePositions(targetCount: Int, difficulty: GameDifficulty, random: Random): List<BoardPosition> {
    val positions = mutableListOf<BoardPosition>()
    // Generate layered grid centered around (col 3, row 4)
    // 0: base layer, 1: layer 1, 2: layer 2, 3: layer 3
    val maxLayer = when (difficulty) {
      GameDifficulty.BEGINNER -> 1
      GameDifficulty.INTERMEDIATE -> 2
      GameDifficulty.ADVANCED -> 3
      GameDifficulty.EXPERT -> 3
    }

    // Layout templates based on central symmetry
    val baseCols = 6
    val baseRows = 7

    // Base layer (z = 0)
    for (r in 0 until baseRows) {
      for (c in 0 until baseCols) {
        // Exclude extreme corners for an organic rounded/turtle African shape
        val isCorner = (r == 0 && (c == 0 || c == baseCols - 1)) ||
                       (r == baseRows - 1 && (c == 0 || c == baseCols - 1))
        if (!isCorner) {
          positions.add(BoardPosition(c, r, 0))
        }
      }
    }

    // Second layer (z = 1) - centered inward
    if (maxLayer >= 1) {
      for (r in 1..(baseRows - 2)) {
        for (c in 1..(baseCols - 2)) {
          positions.add(BoardPosition(c, r, 1))
        }
      }
    }

    // Third layer (z = 2) - central cross
    if (maxLayer >= 2) {
      for (r in 2..(baseRows - 3)) {
        for (c in 2..(baseCols - 3)) {
          positions.add(BoardPosition(c, r, 2))
        }
      }
    }

    // Fourth layer (z = 3) - crown apex
    if (maxLayer >= 3) {
      positions.add(BoardPosition(2, 3, 3))
      positions.add(BoardPosition(3, 3, 3))
    }

    // Trim or expand symmetrically to match targetCount exactly (guaranteeing even number)
    var result = positions.toMutableList()
    if (result.size > targetCount) {
      // Symmetrically prune while keeping valid structure
      val toRemove = result.size - targetCount
      // Remove from lowest layer outer edges
      val removable = result.filter { it.layer == 0 }.shuffled(random)
      val removedSet = removable.take(toRemove).toSet()
      result.removeAll(removedSet)
    } else if (result.size < targetCount) {
      // Pad by adding wing tiles to layer 0 left and right
      var cOffset = 0
      while (result.size < targetCount) {
        result.add(BoardPosition(-1, 3 + cOffset % 2, 0))
        if (result.size < targetCount) {
          result.add(BoardPosition(baseCols, 3 + cOffset % 2, 0))
        }
        cOffset++
      }
    }

    // Ensure strictly even count
    if (result.size % 2 != 0) {
      result.removeLast()
    }

    return result
  }
}

private data class EightTuple(
  val a: Int, val b: Int, val c: Int, val d: Int,
  val e: Int, val f: Int, val g: Int, val h: String
)
