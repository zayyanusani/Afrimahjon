package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
  entities = [
    PlayerProfileEntity::class,
    LevelProgressEntity::class,
    UnlockedCardEntity::class,
    AchievementEntity::class,
    DailyChallengeRecordEntity::class
  ],
  version = 1,
  exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun playerProfileDao(): PlayerProfileDao
  abstract fun levelProgressDao(): LevelProgressDao
  abstract fun unlockedCardDao(): UnlockedCardDao
  abstract fun achievementDao(): AchievementDao
  abstract fun dailyChallengeDao(): DailyChallengeDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "afri_mahjong_database"
        )
        .fallbackToDestructiveMigration()
        .build()
        INSTANCE = instance
        instance
      }
    }
  }
}
