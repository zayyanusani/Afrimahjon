package com.example.game.core

object MahjongRuleEngine {

  /**
   * Evaluates whether a given tile is selectable according to standard Mahjong Solitaire rules:
   * 1. No other active tile sits directly above it (higher layer at same col & row).
   * 2. It is free on either its left side OR its right side on the same layer.
   */
  fun isTileFree(tile: MahjongTileInstance, allTiles: List<MahjongTileInstance>): Boolean {
    if (tile.isMatched) return false

    // Check if covered from above
    val isCovered = allTiles.any { other ->
      !other.isMatched &&
      other.layer > tile.layer &&
      other.col == tile.col &&
      other.row == tile.row
    }
    if (isCovered) return false

    // Check horizontal freedom on the same layer
    val hasLeftNeighbor = allTiles.any { other ->
      !other.isMatched &&
      other.layer == tile.layer &&
      other.row == tile.row &&
      other.col == tile.col - 1
    }

    val hasRightNeighbor = allTiles.any { other ->
      !other.isMatched &&
      other.layer == tile.layer &&
      other.row == tile.row &&
      other.col == tile.col + 1
    }

    // Free if at least one side is unblocked
    return !hasLeftNeighbor || !hasRightNeighbor
  }

  /**
   * Returns all currently free (selectable) tiles on the board.
   */
  fun getFreeTiles(allTiles: List<MahjongTileInstance>): List<MahjongTileInstance> {
    return allTiles.filter { !it.isMatched && isTileFree(it, allTiles) }
  }

  /**
   * Finds all legal matching pairs among the currently free tiles.
   */
  fun getLegalMatchingPairs(allTiles: List<MahjongTileInstance>): List<Pair<MahjongTileInstance, MahjongTileInstance>> {
    val freeTiles = getFreeTiles(allTiles)
    val pairs = mutableListOf<Pair<MahjongTileInstance, MahjongTileInstance>>()

    for (i in 0 until freeTiles.size) {
      for (j in i + 1 until freeTiles.size) {
        val t1 = freeTiles[i]
        val t2 = freeTiles[j]
        if (t1.tileId == t2.tileId) {
          pairs.add(Pair(t1, t2))
        }
      }
    }
    return pairs
  }

  /**
   * Returns true if there are still active tiles on the board, but no legal moves remain.
   */
  fun hasNoLegalMovesRemaining(allTiles: List<MahjongTileInstance>): Boolean {
    val remaining = allTiles.count { !it.isMatched }
    if (remaining == 0) return false
    return getLegalMatchingPairs(allTiles).isEmpty()
  }

  /**
   * Returns true if all tiles have been cleared.
   */
  fun isBoardCleared(allTiles: List<MahjongTileInstance>): Boolean {
    return allTiles.all { it.isMatched }
  }

  /**
   * Rearranges remaining unmatched tiles' positions or tile definitions to ensure at least one legal match.
   */
  fun shuffleRemainingTiles(allTiles: List<MahjongTileInstance>): List<MahjongTileInstance> {
    val unmatched = allTiles.filter { !it.isMatched }
    if (unmatched.size < 2) return allTiles

    // Extract current definitions of unmatched tiles
    val defs = unmatched.map { it.definition }.shuffled()

    // Assign shuffled definitions to the same physical positions
    unmatched.forEachIndexed { index, tile ->
      val newDef = defs[index]
      tile.tileId = newDef.id
      tile.definition = newDef
      tile.isSelected = false
      tile.isHinted = false
    }

    return allTiles
  }
}
