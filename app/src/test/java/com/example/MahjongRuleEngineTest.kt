package com.example

import com.example.data.model.TileRegistry
import com.example.game.core.MahjongRuleEngine
import com.example.game.core.MahjongTileInstance
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MahjongRuleEngineTest {

  private val defA = TileRegistry.ALL_TILES[0]
  private val defB = TileRegistry.ALL_TILES[1]

  @Test
  fun testTileFreedomWithNeighbors() {
    // 3 tiles in a horizontal row at layer 0: col 0, 1, 2
    val tile0 = MahjongTileInstance(1, "A", defA, col = 0, row = 0, layer = 0)
    val tile1 = MahjongTileInstance(2, "B", defB, col = 1, row = 0, layer = 0)
    val tile2 = MahjongTileInstance(3, "A", defA, col = 2, row = 0, layer = 0)

    val tiles = listOf(tile0, tile1, tile2)

    // Leftmost tile0 has right neighbor but no left neighbor -> FREE
    assertTrue(MahjongRuleEngine.isTileFree(tile0, tiles))

    // Middle tile1 has both left and right neighbors -> BLOCKED
    assertFalse(MahjongRuleEngine.isTileFree(tile1, tiles))

    // Rightmost tile2 has left neighbor but no right neighbor -> FREE
    assertTrue(MahjongRuleEngine.isTileFree(tile2, tiles))

    // Matching pairs among free tiles: tile0 and tile2 both have definition "A"
    val pairs = MahjongRuleEngine.getLegalMatchingPairs(tiles)
    assertEquals(1, pairs.size)
    assertEquals(tile0.instanceId, pairs[0].first.instanceId)
    assertEquals(tile2.instanceId, pairs[0].second.instanceId)
  }

  @Test
  fun testTileCoveredFromAbove() {
    val bottom = MahjongTileInstance(1, "A", defA, col = 0, row = 0, layer = 0)
    val top = MahjongTileInstance(2, "A", defA, col = 0, row = 0, layer = 1)

    val tiles = listOf(bottom, top)

    // Bottom is covered by top -> BLOCKED
    assertFalse(MahjongRuleEngine.isTileFree(bottom, tiles))

    // Top is not covered and has no horizontal neighbors -> FREE
    assertTrue(MahjongRuleEngine.isTileFree(top, tiles))
  }

  @Test
  fun testBoardCleared() {
    val t1 = MahjongTileInstance(1, "A", defA, 0, 0, 0, isMatched = true)
    val t2 = MahjongTileInstance(2, "A", defA, 1, 0, 0, isMatched = true)

    assertTrue(MahjongRuleEngine.isBoardCleared(listOf(t1, t2)))
  }
}
