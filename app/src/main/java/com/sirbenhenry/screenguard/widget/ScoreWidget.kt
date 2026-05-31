package com.sirbenhenry.screenguard.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.sirbenhenry.screenguard.MainActivity
import com.sirbenhenry.screenguard.R

class ScoreWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val score = prefs.getInt(KEY_SCORE, 100)
        val streak = prefs.getInt(KEY_STREAK, 0)
        for (id in ids) updateWidget(context, manager, id, score, streak)
    }

    companion object {
        private const val PREFS = "widget_cache"
        private const val KEY_SCORE = "score"
        private const val KEY_STREAK = "streak"

        fun push(context: Context, score: Int? = null, streak: Int? = null) {
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val resolvedScore = score ?: prefs.getInt(KEY_SCORE, 100)
            val resolvedStreak = streak ?: prefs.getInt(KEY_STREAK, 0)
            prefs.edit()
                .putInt(KEY_SCORE, resolvedScore)
                .putInt(KEY_STREAK, resolvedStreak)
                .apply()

            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(ComponentName(context, ScoreWidget::class.java))
            for (id in ids) updateWidget(context, manager, id, resolvedScore, resolvedStreak)
        }

        private fun updateWidget(
            context: Context,
            manager: AppWidgetManager,
            id: Int,
            score: Int,
            streak: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_score)

            val scoreColor = when {
                score >= 80 -> Color.parseColor("#44BB88")
                score >= 50 -> Color.parseColor("#FFCC00")
                else -> Color.parseColor("#FF5555")
            }
            views.setTextViewText(R.id.widget_score, "$score")
            views.setTextColor(R.id.widget_score, scoreColor)
            views.setTextViewText(R.id.widget_streak, if (streak > 0) "$streak" else "—")

            val pi = PendingIntent.getActivity(
                context, 0,
                Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pi)

            manager.updateAppWidget(id, views)
        }
    }
}
