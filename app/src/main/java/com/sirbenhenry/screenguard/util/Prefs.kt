package com.sirbenhenry.screenguard.util

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("screenguard_prefs")

object Prefs {
    private val KEY_ONBOARDED = booleanPreferencesKey("onboarded")
    private val KEY_CURRENT_STREAK = intPreferencesKey("current_streak")
    private val KEY_LONGEST_STREAK = intPreferencesKey("longest_streak")
    private val KEY_TOTAL_SAVED_MINUTES = intPreferencesKey("total_saved_minutes")
    private val KEY_LAST_VERSION_CHECKED = stringPreferencesKey("last_version_checked")
    private val KEY_PIN_HASH = stringPreferencesKey("pin_hash")
    private val KEY_COOLDOWN_SECONDS = intPreferencesKey("cooldown_seconds")
    private val KEY_ENABLE_BREATHING = booleanPreferencesKey("enable_breathing")

    fun onboardedFlow(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[KEY_ONBOARDED] ?: false }

    suspend fun setOnboarded(ctx: Context) {
        ctx.dataStore.edit { it[KEY_ONBOARDED] = true }
    }

    fun currentStreakFlow(ctx: Context): Flow<Int> =
        ctx.dataStore.data.map { it[KEY_CURRENT_STREAK] ?: 0 }

    suspend fun updateStreak(ctx: Context, current: Int, longest: Int) {
        ctx.dataStore.edit {
            it[KEY_CURRENT_STREAK] = current
            if (longest > (it[KEY_LONGEST_STREAK] ?: 0)) it[KEY_LONGEST_STREAK] = longest
        }
    }

    fun longestStreakFlow(ctx: Context): Flow<Int> =
        ctx.dataStore.data.map { it[KEY_LONGEST_STREAK] ?: 0 }

    suspend fun addSavedMinutes(ctx: Context, minutes: Int) {
        ctx.dataStore.edit {
            it[KEY_TOTAL_SAVED_MINUTES] = (it[KEY_TOTAL_SAVED_MINUTES] ?: 0) + minutes
        }
    }

    fun totalSavedMinutesFlow(ctx: Context): Flow<Int> =
        ctx.dataStore.data.map { it[KEY_TOTAL_SAVED_MINUTES] ?: 0 }

    suspend fun setLastVersionChecked(ctx: Context, v: String) {
        ctx.dataStore.edit { it[KEY_LAST_VERSION_CHECKED] = v }
    }

    suspend fun getLastVersionChecked(ctx: Context): String {
        var v = ""
        ctx.dataStore.data.collect { v = it[KEY_LAST_VERSION_CHECKED] ?: "" }
        return v
    }

    fun cooldownSecondsFlow(ctx: Context): Flow<Int> =
        ctx.dataStore.data.map { it[KEY_COOLDOWN_SECONDS] ?: 60 }

    suspend fun setCooldownSeconds(ctx: Context, seconds: Int) {
        ctx.dataStore.edit { it[KEY_COOLDOWN_SECONDS] = seconds }
    }

    fun enableBreathingFlow(ctx: Context): Flow<Boolean> =
        ctx.dataStore.data.map { it[KEY_ENABLE_BREATHING] ?: true }

    suspend fun setEnableBreathing(ctx: Context, enabled: Boolean) {
        ctx.dataStore.edit { it[KEY_ENABLE_BREATHING] = enabled }
    }

    suspend fun setPinHash(ctx: Context, hash: String) {
        ctx.dataStore.edit { it[KEY_PIN_HASH] = hash }
    }

    fun pinHashFlow(ctx: Context): Flow<String> =
        ctx.dataStore.data.map { it[KEY_PIN_HASH] ?: "" }
}
