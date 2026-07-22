package org.example.project.core.util

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import kotlin.time.Clock


object DateTimeUtils {

    fun currentDateTime(): LocalDateTime =
        kotlin.time.Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    fun today(): LocalDate =
        Clock.System.todayIn(TimeZone.currentSystemDefault())

    fun monthStart(month: Int? = null, year: Int? = null): LocalDateTime {
        val today = today()
        val m = month ?: today.month.number
        val y = year ?: today.year
        return LocalDateTime(y, m, 1, 0, 0, 0)
    }

    fun monthEnd(month: Int? = null, year: Int? = null): LocalDateTime {
        val today = today()
        val m = month ?: today.month.number
        val y = year ?: today.year
        val lastDay = when (m) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(y)) 29 else 28
            else -> 30
        }
        return LocalDateTime(y, m, lastDay, 23, 59, 59)
    }

    fun yearStart(year: Int? = null): LocalDateTime {
        val y = year ?: today().year
        return LocalDateTime(y, 1, 1, 0, 0, 0)
    }

    fun yearEnd(year: Int? = null): LocalDateTime {
        val y = year ?: today().year
        return LocalDateTime(y, 12, 31, 23, 59, 59)
    }

    fun isLeapYear(year: Int): Boolean =
        (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

    fun formatAmount(amount: Double, currency: String = "₹"): String {
        val rounded = kotlin.math.round(amount * 100) / 100.0
        return "$currency${formatNumber(rounded)}"
    }

    fun formatNumber(number: Double): String {
        val intPart = number.toLong()
        val decimalPart = ((number - intPart) * 100).toLong()
        val intStr = formatIndianNumber(intPart)
        return if (decimalPart > 0) "$intStr.${decimalPart.toString().padStart(2, '0')}" else intStr
    }

    private fun formatIndianNumber(number: Long): String {
        val str = number.toString()
        if (str.length <= 3) return str
        val result = StringBuilder()
        var count = 0
        for (i in str.length - 1 downTo 0) {
            if (count == 3 || (count > 3 && (count - 3) % 2 == 0)) result.insert(0, ",")
            result.insert(0, str[i])
            count++
        }
        return result.toString()
    }

    fun formatDate(dateTime: LocalDateTime): String {
        val today = today()
        val date = dateTime.date
        return when {
            date == today -> "Today"
            date == today.minus(1, DateTimeUnit.DAY) -> "Yesterday"
            else -> "${date.day} ${monthShortName(date.month.number)} ${date.year}"
        }
    }

    fun formatTime(dateTime: LocalDateTime): String {
        val hour = dateTime.hour
        val minute = dateTime.minute.toString().padStart(2, '0')
        val amPm = if (hour < 12) "AM" else "PM"
        val hour12 = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return "$hour12:$minute $amPm"
    }

    fun monthName(month: Int): String = when (month) {
        1 -> "January"; 2 -> "February"; 3 -> "March"; 4 -> "April"
        5 -> "May"; 6 -> "June"; 7 -> "July"; 8 -> "August"
        9 -> "September"; 10 -> "October"; 11 -> "November"; 12 -> "December"
        else -> ""
    }

    fun monthShortName(month: Int): String = when (month) {
        1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"
        5 -> "May"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Aug"
        9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
        else -> ""
    }

    fun toIsoString(dateTime: LocalDateTime): String = dateTime.toString()

    fun last7DaysRange(): Pair<String, String> {
        val end = currentDateTime()
        val start = today().minus(6, DateTimeUnit.DAY)
        return LocalDateTime(start, LocalTime(0, 0, 0)).toString() to end.toString()
    }

    fun last30DaysRange(): Pair<String, String> {
        val end = currentDateTime()
        val start = today().minus(29, DateTimeUnit.DAY)
        return LocalDateTime(start, LocalTime(0, 0, 0)).toString() to end.toString()
    }

    fun currentMonthRange(): Pair<String, String> {
        val now = currentDateTime()
        val firstDay = LocalDate(now.year, now.month, 1)
        val lastDay = firstDay
            .plus(1, DateTimeUnit.MONTH)
            .minus(1, DateTimeUnit.DAY)
        val start = LocalDateTime(firstDay, LocalTime(0, 0, 0))
        val end = LocalDateTime(lastDay, LocalTime(23, 59, 59, 999_999_999))
        return start.toString() to end.toString()
    }
}

val ClockSystem get() = Clock.System