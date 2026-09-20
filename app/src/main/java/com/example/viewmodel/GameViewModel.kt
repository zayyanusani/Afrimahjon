package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AchievementEntity
import com.example.data.db.AppDatabase
import com.example.data.db.LevelProgressEntity
import com.example.data.db.PlayerProfileEntity
import com.example.data.db.UnlockedCardEntity
import com.example.data.model.AchievementRegistry
import com.example.data.model.AfricaRegistry
import com.example.data.model.CulturalDiscoveryCard
import com.example.data.repository.GameRepository
import com.example.game.core.AudioManager
import com.example.game.core.LevelEngine
import com.example.game.core.LevelMetadata
import com.example.game.core.MahjongRuleEngine
import com.example.game.core.MahjongTileInstance
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class GameScreen {
  MAP,
  GAME,
  COLLECTION,
  DAILY,
  ACHIEVEMENTS,
  MARKET,
  PROFILE
}

data class ActiveGameState(
  val levelNumber: Int = 1,
  val metadata: LevelMetadata? = null,
  val tiles: List<MahjongTileInstance> = emptyList(),
  val selectedTileId: Int? = null,
  val hintedInstanceIds: Set<Int> = emptySet(),
  val matchesMade: Int = 0,
  val comboStreak: Int = 0,
  val score: Int = 0,
  val timeElapsed: Int = 0,
  val timeLimit: Int = 180,
  val isPaused: Boolean = false,
  val isLevelComplete: Boolean = false,
  val isTimeOut: Boolean = false,
  val isNoMoves: Boolean = false,
  val starsAwarded: Int = 0,
  val coinsAwarded: Int = 0,
  val gemsAwarded: Int = 0,
  val culturalCardReward: CulturalDiscoveryCard? = null,
  val isDailyChallenge: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: GameRepository
  private var timerJob: Job? = null

  private val _currentScreen = MutableStateFlow(GameScreen.MAP)
  val currentScreen: StateFlow<GameScreen> = _currentScreen.asStateFlow()

  private val _gameState = MutableStateFlow(ActiveGameState())
  val gameState: StateFlow<ActiveGameState> = _gameState.asStateFlow()

  val profile: StateFlow<PlayerProfileEntity?>
  val levelProgressList: StateFlow<List<LevelProgressEntity>>
  val unlockedCards: StateFlow<List<UnlockedCardEntity>>
  val achievements: StateFlow<List<AchievementEntity>>

  init {
    val db = AppDatabase.getDatabase(application)
    repository = GameRepository(
      db.playerProfileDao(),
      db.levelProgressDao(),
      db.unlockedCardDao(),
      db.achievementDao(),
      db.dailyChallengeDao()
    )

    profile = repository.profileFlow.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      null
    )

    levelProgressList = repository.levelProgressFlow.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      emptyList()
    )

    unlockedCards = repository.unlockedCardsFlow.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      emptyList()
    )

    achievements = repository.achievementsFlow.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000),
      emptyList()
    )

    viewModelScope.launch {
      repository.ensureInitialized()
    }
  }

  fun navigateTo(screen: GameScreen) {
    if (screen != GameScreen.GAME && _gameState.value.isPaused.not()) {
      pauseTimer()
    }
    _currentScreen.value = screen
  }

  fun startLevel(levelNumber: Int, isDaily: Boolean = false) {
    val meta = LevelEngine.getLevelMetadata(levelNumber)
    val tiles = LevelEngine.generateBoardForLevel(levelNumber)

    _gameState.value = ActiveGameState(
      levelNumber = levelNumber,
      metadata = meta,
      tiles = tiles,
      timeLimit = meta.targetTimeSeconds,
      timeElapsed = 0,
      isDailyChallenge = isDaily
    )
    _currentScreen.value = GameScreen.GAME
    startTimer()
  }

  fun onTileClicked(tile: MahjongTileInstance) {
    val state = _gameState.value
    if (state.isLevelComplete || state.isPaused || state.isTimeOut || tile.isMatched) return

    val currentTiles = state.tiles

    // Verify if tile is legally free to pick
    if (!MahjongRuleEngine.isTileFree(tile, currentTiles)) {
      AudioManager.playErrorThud()
      return
    }

    val selectedId = state.selectedTileId

    if (selectedId == null) {
      // First tile selection
      AudioManager.playTileClick()
      val updated = currentTiles.map {
        if (it.instanceId == tile.instanceId) it.copy(isSelected = true, isHinted = false)
        else it.copy(isSelected = false, isHinted = false)
      }
      _gameState.value = state.copy(
        tiles = updated,
        selectedTileId = tile.instanceId,
        hintedInstanceIds = emptySet()
      )
    } else if (selectedId == tile.instanceId) {
      // Deselect clicked tile
      AudioManager.playTileClick()
      val updated = currentTiles.map { it.copy(isSelected = false) }
      _gameState.value = state.copy(tiles = updated, selectedTileId = null)
    } else {
      // Second tile selection - Check match!
      val firstTile = currentTiles.find { it.instanceId == selectedId }
      if (firstTile != null && firstTile.tileId == tile.tileId) {
        // MATCH SUCCESS!
        val newCombo = state.comboStreak + 1
        AudioManager.playTileMatch(newCombo)

        val matchPoints = 100 * newCombo
        val updatedTiles = currentTiles.map {
          if (it.instanceId == firstTile.instanceId || it.instanceId == tile.instanceId) {
            it.copy(isMatched = true, isSelected = false, isHinted = false)
          } else {
            it.copy(isSelected = false, isHinted = false)
          }
        }

        val allCleared = MahjongRuleEngine.isBoardCleared(updatedTiles)
        val noLegalMoves = !allCleared && MahjongRuleEngine.hasNoLegalMovesRemaining(updatedTiles)

        _gameState.value = state.copy(
          tiles = updatedTiles,
          selectedTileId = null,
          matchesMade = state.matchesMade + 1,
          comboStreak = newCombo,
          score = state.score + matchPoints,
          isNoMoves = noLegalMoves
        )

        if (allCleared) {
          handleLevelCompleted()
        }
      } else {
        // Invalid match - change selection to newly clicked free tile
        AudioManager.playTileClick()
        val updated = currentTiles.map {
          if (it.instanceId == tile.instanceId) it.copy(isSelected = true, isHinted = false)
          else it.copy(isSelected = false, isHinted = false)
        }
        _gameState.value = state.copy(
          tiles = updated,
          selectedTileId = tile.instanceId,
          comboStreak = 0
        )
      }
    }
  }

  private fun handleLevelCompleted() {
    stopTimer()
    AudioManager.playVictoryFanfare()
    val state = _gameState.value
    val meta = state.metadata ?: LevelEngine.getLevelMetadata(state.levelNumber)

    // Calculate stars:
    // Star 3: completed within star3Time
    // Star 2: completed within star2Time
    // Star 1: completed within targetTime
    val elapsed = state.timeElapsed
    val stars = when {
      elapsed <= meta.star3Time -> 3
      elapsed <= meta.star2Time -> 2
      else -> 1
    }

    val baseCoins = meta.coinReward
    val timeBonusCoins = ((meta.targetTimeSeconds - elapsed).coerceAtLeast(0) / 2)
    val totalCoins = baseCoins + timeBonusCoins
    val totalGems = meta.gemReward + (if (stars == 3) 2 else 0)

    val cardReward = AfricaRegistry.getCardByLevel(state.levelNumber)

    _gameState.value = state.copy(
      isLevelComplete = true,
      starsAwarded = stars,
      coinsAwarded = totalCoins,
      gemsAwarded = totalGems,
      culturalCardReward = cardReward
    )

    viewModelScope.launch {
      if (state.isDailyChallenge) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        repository.recordDailyCompletion(todayStr, state.score, stars, totalCoins, totalGems)
      } else {
        repository.completeLevel(
          levelNumber = state.levelNumber,
          starsEarned = stars,
          completionTimeSeconds = elapsed,
          coinsEarned = totalCoins,
          gemsEarned = totalGems,
          cardIdReward = cardReward?.id
        )
      }
    }
  }

  fun useHint() {
    viewModelScope.launch {
      val success = repository.useHint()
      if (!success) return@launch

      val state = _gameState.value
      val pairs = MahjongRuleEngine.getLegalMatchingPairs(state.tiles)
      if (pairs.isNotEmpty()) {
        AudioManager.playPowerUp()
        val pair = pairs.first()
        val hintedSet = setOf(pair.first.instanceId, pair.second.instanceId)
        val updated = state.tiles.map {
          if (it.instanceId in hintedSet) it.copy(isHinted = true)
          else it.copy(isHinted = false)
        }
        _gameState.value = state.copy(tiles = updated, hintedInstanceIds = hintedSet)
      }
    }
  }

  fun useShuffle() {
    viewModelScope.launch {
      val success = repository.useShuffle()
      if (!success) return@launch

      AudioManager.playShuffle()
      val state = _gameState.value
      val shuffled = MahjongRuleEngine.shuffleRemainingTiles(state.tiles)
      _gameState.value = state.copy(
        tiles = shuffled,
        selectedTileId = null,
        hintedInstanceIds = emptySet(),
        isNoMoves = false
      )
    }
  }

  fun useTimeBonus() {
    viewModelScope.launch {
      val success = repository.useTimeBonus()
      if (!success) return@launch

      AudioManager.playPowerUp()
      val state = _gameState.value
      val newLimit = state.timeLimit + 60
      _gameState.value = state.copy(timeLimit = newLimit)
    }
  }

  fun usePowerTile() {
    viewModelScope.launch {
      val success = repository.usePowerTile()
      if (!success) return@launch

      AudioManager.playPowerUp()
      val state = _gameState.value
      val pairs = MahjongRuleEngine.getLegalMatchingPairs(state.tiles)
      if (pairs.isNotEmpty()) {
        val targetPair = pairs.first()
        val updated = state.tiles.map {
          if (it.instanceId == targetPair.first.instanceId || it.instanceId == targetPair.second.instanceId) {
            it.copy(isMatched = true, isSelected = false, isHinted = false)
          } else it
        }

        val allCleared = MahjongRuleEngine.isBoardCleared(updated)
        val noMoves = !allCleared && MahjongRuleEngine.hasNoLegalMovesRemaining(updated)

        _gameState.value = state.copy(
          tiles = updated,
          selectedTileId = null,
          matchesMade = state.matchesMade + 1,
          score = state.score + 200,
          isNoMoves = noMoves
        )

        if (allCleared) {
          handleLevelCompleted()
        }
      }
    }
  }

  fun togglePause() {
    val state = _gameState.value
    if (state.isPaused) {
      _gameState.value = state.copy(isPaused = false)
      startTimer()
    } else {
      _gameState.value = state.copy(isPaused = true)
      pauseTimer()
    }
  }

  fun restartLevel() {
    startLevel(_gameState.value.levelNumber, _gameState.value.isDailyChallenge)
  }

  fun nextLevel() {
    val nextNum = (_gameState.value.levelNumber + 1).coerceAtMost(500)
    startLevel(nextNum)
  }

  fun purchasePowerUp(type: String, count: Int, coinCost: Int, gemCost: Int) {
    viewModelScope.launch {
      val ok = repository.purchasePowerUp(type, count, coinCost, gemCost)
      if (ok) {
        AudioManager.playPowerUp()
      }
    }
  }

  fun claimAchievement(id: String) {
    viewModelScope.launch {
      val ok = repository.claimAchievement(id)
      if (ok) {
        AudioManager.playVictoryFanfare()
      }
    }
  }

  fun updateAvatar(avatarId: String) {
    viewModelScope.launch {
      repository.updateProfileAvatar(avatarId)
    }
  }

  fun updateUsername(newName: String) {
    viewModelScope.launch {
      repository.updateUsername(newName)
    }
  }

  fun toggleSound(enabled: Boolean) {
    AudioManager.isSoundEnabled = enabled
    viewModelScope.launch {
      repository.toggleSound(enabled)
    }
  }

  fun toggleMusic(enabled: Boolean) {
    AudioManager.isMusicEnabled = enabled
    if (enabled) AudioManager.startAmbientMusic()
    else AudioManager.stopAmbientMusic()
    viewModelScope.launch {
      repository.toggleMusic(enabled)
    }
  }

  private fun startTimer() {
    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (isActive) {
        delay(1000)
        val s = _gameState.value
        if (!s.isPaused && !s.isLevelComplete) {
          val nextElapsed = s.timeElapsed + 1
          if (nextElapsed >= s.timeLimit) {
            _gameState.value = s.copy(timeElapsed = nextElapsed, isTimeOut = true)
            AudioManager.playErrorThud()
            stopTimer()
          } else {
            _gameState.value = s.copy(timeElapsed = nextElapsed)
          }
        }
      }
    }
  }

  private fun pauseTimer() {
    timerJob?.cancel()
    timerJob = null
  }

  private fun stopTimer() {
    timerJob?.cancel()
    timerJob = null
  }

  override fun onCleared() {
    super.onCleared()
    stopTimer()
    AudioManager.stopAmbientMusic()
  }
}
