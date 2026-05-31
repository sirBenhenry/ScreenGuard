package com.sirbenhenry.screenguard.util

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    private val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun today(): String = fmt.format(Date())

    fun dateKey(timestamp: Long): String = fmt.format(Date(timestamp))

    fun daysAgo(n: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -n)
        return fmt.format(cal.time)
    }

    fun daysBetween(from: String, to: String): Int {
        val d1 = fmt.parse(from) ?: return 0
        val d2 = fmt.parse(to) ?: return 0
        return ((d2.time - d1.time) / 86_400_000).toInt()
    }

    fun startOfDayMs(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun startOfWeek(): String {
        val cal = Calendar.getInstance()
        cal.firstDayOfWeek = Calendar.MONDAY
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        return fmt.format(cal.time)
    }
}
