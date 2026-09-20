package com.example.data.repository

import com.example.data.db.AchievementDao
import com.example.data.db.AchievementEntity
import com.example.data.db.DailyChallengeDao
import com.example.data.db.DailyChallengeRecordEntity
import com.example.data.db.LevelProgressDao
import com.example.data.db.LevelProgressEntity
import com.example.data.db.PlayerProfileDao
import com.example.data.db.PlayerProfileEntity
import com.example.data.db.UnlockedCardDao
import com.example.data.db.UnlockedCardEntity
import com.example.data.model.AchievementRegistry
import com.example.data.model.AfricaRegistry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class GameRepository(
  private val profileDao: PlayerProfileDao,
  private val levelDao: LevelProgressDao,
  private val cardDao: UnlockedCardDao,
  private val achievementDao: AchievementDao,
  private val dailyChallengeDao: DailyChallengeDao
) {

  val profileFlow: Flow<PlayerProfileEntity?> = profileDao.getProfile()
  val levelProgressFlow: Flow<List<LevelProgressEntity>> = levelDao.getAllLevelProgress()
  val unlockedCardsFlow: Flow<List<UnlockedCardEntity>> = cardDao.getAllUnlockedCards()
  val achievementsFlow: Flow<List<AchievementEntity>> = achievementDao.getAllAchievements()

  suspend fun ensureInitialized() {
    var profile = profileDao.getProfileSync()
    if (profile == null) {
      profile = PlayerProfileEntity(
        id = 1,
        username = "Explorer",
        avatarId = "explorer",
        currentLevel = 1,
        totalStars = 0,
        coins = 500,
        gems = 30,
        hints = 4,
        shuffles = 3,
        timeBonuses = 2,
        powerTiles = 2
      )
      profileDao.insertOrUpdateProfile(profile)
    }

    // Ensure level 1 is unlocked
    val l1 = levelDao.getLevel(1)
    if (l1 == null) {
      levelDao.insertOrUpdate(
        LevelProgressEntity(
          levelNumber = 1,
          countryId = "NG",
          stars = 0,
          bestTime = 0,
          completed = false,
          unlocked = true
        )
      )
    }

    // Initialize all achievements if not present
    AchievementRegistry.ALL_ACHIEVEMENTS.forEach { def ->
      achievementDao.insertOrUpdate(
        AchievementEntity(
          achievementId = def.id,
          currentProgress = 0,
          isCompleted = false,
          isClaimed = false
        )
      )
    }
  }

  suspend fun completeLevel(
    levelNumber: Int,
    starsEarned: Int,
    completionTimeSeconds: Int,
    coinsEarned: Int,
    gemsEarned: Int,
    cardIdReward: String?
  ) {
    val existing = levelDao.getLevel(levelNumber)
    val bestStars = maxOf(existing?.stars ?: 0, starsEarned)
    val bestTime = if (existing == null || existing.bestTime == 0) completionTimeSeconds else minOf(existing.bestTime, completionTimeSeconds)

    val country = AfricaRegistry.getCountryForLevel(levelNumber)
    levelDao.insertOrUpdate(
      LevelProgressEntity(
        levelNumber = levelNumber,
        countryId = country.id,
        stars = bestStars,
        bestTime = bestTime,
        completed = true,
        unlocked = true
      )
    )

    // Unlock next level (up to 500)
    if (levelNumber < 500) {
      val nextLvl = levelDao.getLevel(levelNumber + 1)
      val nextCountry = AfricaRegistry.getCountryForLevel(levelNumber + 1)
      if (nextLvl == null || !nextLvl.unlocked) {
        levelDao.insertOrUpdate(
          LevelProgressEntity(
            levelNumber = levelNumber + 1,
            countryId = nextCountry.id,
            stars = nextLvl?.stars ?: 0,
            bestTime = nextLvl?.bestTime ?: 0,
            completed = nextLvl?.completed ?: false,
            unlocked = true
          )
        )
      }
    }

    // Unlock card if provided
    if (!cardIdReward.isNullOrBlank()) {
      cardDao.unlockCard(UnlockedCardEntity(cardIdReward))
    }

    // Update Player Profile stats and currencies
    val currentProf = profileDao.getProfileSync() ?: PlayerProfileEntity()
    val newTotalPuzzles = currentProf.puzzlesSolved + 1
    val newPerfect = if (starsEarned == 3) currentProf.perfectPuzzles + 1 else currentProf.perfectPuzzles
    val nextLevelTarget = maxOf(currentProf.currentLevel, levelNumber + 1)

    profileDao.insertOrUpdateProfile(
      currentProf.copy(
        currentLevel = nextLevelTarget,
        coins = currentProf.coins + coinsEarned,
        gems = currentProf.gems + gemsEarned,
        puzzlesSolved = newTotalPuzzles,
        perfectPuzzles = newPerfect
      )
    )

    // Update Achievements
    updateAchievementProgress("first_match", 1)
    updateAchievementProgress("tile_master", newTotalPuzzles)
    updateAchievementProgress("perfect_player", newPerfect)

    val unlockedCount = cardDao.getAllUnlockedCards().firstOrNull()?.size ?: 0
    updateAchievementProgress("cultural_explorer", unlockedCount)

    if (levelNumber >= 29) {
      updateAchievementProgress("africa_explorer", 1)
    }
  }

  suspend fun useHint(): Boolean {
    val prof = profileDao.getProfileSync() ?: return false
    if (prof.hints > 0) {
      profileDao.adjustHints(-1)
      return true
    }
    return false
  }

  suspend fun useShuffle(): Boolean {
    val prof = profileDao.getProfileSync() ?: return false
    if (prof.shuffles > 0) {
      profileDao.adjustShuffles(-1)
      return true
    }
    return false
  }

  suspend fun useTimeBonus(): Boolean {
    val prof = profileDao.getProfileSync() ?: return false
    if (prof.timeBonuses > 0) {
      profileDao.adjustTimeBonuses(-1)
      return true
    }
    return false
  }

  suspend fun usePowerTile(): Boolean {
    val prof = profileDao.getProfileSync() ?: return false
    if (prof.powerTiles > 0) {
      profileDao.adjustPowerTiles(-1)
      return true
    }
    return false
  }

  suspend fun purchasePowerUp(type: String, count: Int, coinCost: Int, gemCost: Int): Boolean {
    val prof = profileDao.getProfileSync() ?: return false
    if (prof.coins < coinCost || prof.gems < gemCost) return false

    var updated = prof.copy(
      coins = prof.coins - coinCost,
      gems = prof.gems - gemCost
    )

    updated = when (type.lowercase()) {
      "hint" -> updated.copy(hints = updated.hints + count)
      "shuffle" -> updated.copy(shuffles = updated.shuffles + count)
      "time" -> updated.copy(timeBonuses = updated.timeBonuses + count)
      "powertile" -> updated.copy(powerTiles = updated.powerTiles + count)
      else -> updated
    }

    profileDao.insertOrUpdateProfile(updated)
    return true
  }

  suspend fun updateProfileAvatar(avatarId: String) {
    val prof = profileDao.getProfileSync() ?: return
    profileDao.insertOrUpdateProfile(prof.copy(avatarId = avatarId))
  }

  suspend fun updateUsername(name: String) {
    val prof = profileDao.getProfileSync() ?: return
    profileDao.insertOrUpdateProfile(prof.copy(username = name.trim()))
  }

  suspend fun toggleSound(enabled: Boolean) {
    val prof = profileDao.getProfileSync() ?: return
    profileDao.insertOrUpdateProfile(prof.copy(soundEnabled = enabled))
  }

  suspend fun toggleMusic(enabled: Boolean) {
    val prof = profileDao.getProfileSync() ?: return
    profileDao.insertOrUpdateProfile(prof.copy(musicEnabled = enabled))
  }

  private suspend fun updateAchievementProgress(achievementId: String, currentProgress: Int) {
    val def = AchievementRegistry.ALL_ACHIEVEMENTS.find { it.id == achievementId } ?: return
    val isComplete = currentProgress >= def.target
    achievementDao.updateProgress(achievementId, currentProgress, isComplete)
  }

  suspend fun claimAchievement(achievementId: String): Boolean {
    val def = AchievementRegistry.ALL_ACHIEVEMENTS.find { it.id == achievementId } ?: return false
    achievementDao.updateProgress(achievementId, def.target, true)
    // Mark as claimed and grant reward
    val currentProf = profileDao.getProfileSync() ?: return false
    profileDao.insertOrUpdateProfile(
      currentProf.copy(
        coins = currentProf.coins + def.rewardCoins,
        gems = currentProf.gems + def.rewardGems
      )
    )
    achievementDao.insertOrUpdate(
      AchievementEntity(
        achievementId = achievementId,
        currentProgress = def.target,
        isCompleted = true,
        isClaimed = true
      )
    )
    return true
  }

  fun getDailyRecord(dateStr: String): Flow<DailyChallengeRecordEntity?> {
    return dailyChallengeDao.getRecord(dateStr)
  }

  suspend fun recordDailyCompletion(dateStr: String, score: Int, stars: Int, coins: Int, gems: Int) {
    dailyChallengeDao.insertOrUpdate(
      DailyChallengeRecordEntity(
        dateString = dateStr,
        isCompleted = true,
        score = score,
        stars = stars
      )
    )
    val prof = profileDao.getProfileSync() ?: return
    profileDao.insertOrUpdateProfile(
      prof.copy(
        coins = prof.coins + coins,
        gems = prof.gems + gems
      )
    )
  }
}
