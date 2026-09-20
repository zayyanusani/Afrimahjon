package com.example.data.model

data class AchievementDef(
  val id: String,
  val title: String,
  val description: String,
  val target: Int,
  val iconEmoji: String,
  val rewardCoins: Int,
  val rewardGems: Int
)

object AchievementRegistry {
  val ALL_ACHIEVEMENTS: List<AchievementDef> = listOf(
    AchievementDef(
      id = "first_match",
      title = "First Match",
      description = "Complete your very first African Mahjong puzzle.",
      target = 1,
      iconEmoji = "🎯",
      rewardCoins = 100,
      rewardGems = 5
    ),
    AchievementDef(
      id = "africa_explorer",
      title = "Africa Explorer",
      description = "Unlock your second chapter country.",
      target = 1,
      iconEmoji = "🌍",
      rewardCoins = 250,
      rewardGems = 10
    ),
    AchievementDef(
      id = "perfect_player",
      title = "Perfect Player",
      description = "Earn 3 stars on 25 levels.",
      target = 25,
      iconEmoji = "⭐",
      rewardCoins = 500,
      rewardGems = 20
    ),
    AchievementDef(
      id = "tile_master",
      title = "Tile Master",
      description = "Complete 50 Mahjong puzzles across Africa.",
      target = 50,
      iconEmoji = "👑",
      rewardCoins = 1000,
      rewardGems = 30
    ),
    AchievementDef(
      id = "cultural_explorer",
      title = "Cultural Explorer",
      description = "Unlock 10 authentic Cultural Discovery Cards.",
      target = 10,
      iconEmoji = "📜",
      rewardCoins = 750,
      rewardGems = 25
    ),
    AchievementDef(
      id = "combo_master",
      title = "Rhythm of Africa",
      description = "Achieve a 5x rapid match combo streak.",
      target = 5,
      iconEmoji = "⚡",
      rewardCoins = 300,
      rewardGems = 10
    ),
    AchievementDef(
      id = "africa_grand_journey",
      title = "Africa Grand Journey",
      description = "Complete all 500 levels across all 18 countries.",
      target = 500,
      iconEmoji = "🏆",
      rewardCoins = 10000,
      rewardGems = 200
    )
  )
}
