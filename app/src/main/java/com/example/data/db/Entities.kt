package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfileEntity(
  @PrimaryKey val id: Int = 1,
  val username: String = "African Explorer",
  val avatarId: String = "explorer_male",
  val currentLevel: Int = 1,
  val totalStars: Int = 0,
  val coins: Int = 500,
  val gems: Int = 25,
  val hints: Int = 3,
  val shuffles: Int = 3,
  val timeBonuses: Int = 2,
  val powerTiles: Int = 2,
  val soundEnabled: Boolean = true,
  val musicEnabled: Boolean = true,
  val hapticEnabled: Boolean = true,
  val puzzlesSolved: Int = 0,
  val perfectPuzzles: Int = 0
)

@Entity(tableName = "level_progress")
data class LevelProgressEntity(
  @PrimaryKey val levelNumber: Int,
  val countryId: String,
  val stars: Int = 0,
  val bestTime: Int = 0,
  val completed: Boolean = false,
  val unlocked: Boolean = false
)

@Entity(tableName = "unlocked_cards")
data class UnlockedCardEntity(
  @PrimaryKey val cardId: String,
  val unlockedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "achievements")
data class AchievementEntity(
  @PrimaryKey val achievementId: String,
  val currentProgress: Int = 0,
  val isCompleted: Boolean = false,
  val isClaimed: Boolean = false
)

@Entity(tableName = "daily_challenges")
data class DailyChallengeRecordEntity(
  @PrimaryKey val dateString: String,
  val isCompleted: Boolean = false,
  val score: Int = 0,
  val stars: Int = 0
)
