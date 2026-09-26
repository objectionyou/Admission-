package com.aj75.app.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Direct ports of the web prototype's `toBengaliNum` and `formatBengaliDate` helpers, so the
 * Android app reproduces the exact same digit/date presentation the user already designed.
 */
object BengaliFormat {

    private val bnDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')

    private val bnMonths = listOf(
        "জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন",
        "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর",
    )

    private val isoFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    /** e.g. 75 -> "৭৫", "42%" -> "৪২%" */
    fun toBengaliNum(value: Int): String = toBengaliNum(value.toString())

    fun toBengaliNum(value: String): String =
        value.map { ch -> if (ch.isDigit()) bnDigits[ch - '0'] else ch }.joinToString("")

    /** dateStr is an ISO "yyyy-MM-dd" string; returns e.g. "১৫ অক্টোবর" */
    fun formatBengaliDate(dateStr: String): String {
        val date = LocalDate.parse(dateStr, isoFormatter)
        val day = toBengaliNum(date.dayOfMonth)
        val month = bnMonths[date.monthValue - 1]
        return "$day $month"
    }
}
