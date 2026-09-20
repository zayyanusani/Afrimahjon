package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerProfileDao {
  @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
  fun getProfile(): Flow<PlayerProfileEntity?>

  @Query("SELECT * FROM player_profile WHERE id = 1 LIMIT 1")
  suspend fun getProfileSync(): PlayerProfileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateProfile(profile: PlayerProfileEntity)

  @Query("UPDATE player_profile SET coins = coins + :amount WHERE id = 1")
  suspend fun addCoins(amount: Int)

  @Query("UPDATE player_profile SET gems = gems + :amount WHERE id = 1")
  suspend fun addGems(amount: Int)

  @Query("UPDATE player_profile SET hints = hints + :delta WHERE id = 1")
  suspend fun adjustHints(delta: Int)

  @Query("UPDATE player_profile SET shuffles = shuffles + :delta WHERE id = 1")
  suspend fun adjustShuffles(delta: Int)

  @Query("UPDATE player_profile SET timeBonuses = timeBonuses + :delta WHERE id = 1")
  suspend fun adjustTimeBonuses(delta: Int)

  @Query("UPDATE player_profile SET powerTiles = powerTiles + :delta WHERE id = 1")
  suspend fun adjustPowerTiles(delta: Int)
}

@Dao
interface LevelProgressDao {
  @Query("SELECT * FROM level_progress ORDER BY levelNumber ASC")
  fun getAllLevelProgress(): Flow<List<LevelProgressEntity>>

  @Query("SELECT * FROM level_progress WHERE levelNumber = :levelNumber LIMIT 1")
  suspend fun getLevel(levelNumber: Int): LevelProgressEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(progress: LevelProgressEntity)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insertAll(list: List<LevelProgressEntity>)

  @Query("SELECT COUNT(*) FROM level_progress WHERE completed = 1")
  fun getCompletedLevelsCount(): Flow<Int>

  @Query("SELECT SUM(stars) FROM level_progress")
  fun getTotalStarsCount(): Flow<Int?>
}

@Dao
interface UnlockedCardDao {
  @Query("SELECT * FROM unlocked_cards")
  fun getAllUnlockedCards(): Flow<List<UnlockedCardEntity>>

  @Query("SELECT COUNT(*) FROM unlocked_cards")
  fun getUnlockedCount(): Flow<Int>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun unlockCard(card: UnlockedCardEntity)

  @Query("SELECT EXISTS(SELECT 1 FROM unlocked_cards WHERE cardId = :cardId)")
  suspend fun isCardUnlocked(cardId: String): Boolean
}

@Dao
interface AchievementDao {
  @Query("SELECT * FROM achievements")
  fun getAllAchievements(): Flow<List<AchievementEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(achievement: AchievementEntity)

  @Query("UPDATE achievements SET currentProgress = :progress, isCompleted = :completed WHERE achievementId = :id")
  suspend fun updateProgress(id: String, progress: Int, completed: Boolean)
}

@Dao
interface DailyChallengeDao {
  @Query("SELECT * FROM daily_challenges WHERE dateString = :dateString LIMIT 1")
  fun getRecord(dateString: String): Flow<DailyChallengeRecordEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdate(record: DailyChallengeRecordEntity)
}
