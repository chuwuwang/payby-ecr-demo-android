package com.payby.pos.common.helper

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateHelper {

    const val FORMAT_DATE = "dd/MM/yyyy HH:mm:ss"
    const val FORMAT_DATE_BY_LIST = "MMM dd, yyyy · h:mm a"
    const val FORMAT_DATE_TIME_ZONE = "yyyy-MM-dd" + "'T'" + "HH:mm:ss.SSS" + "Z"

    private val locale = Locale.US

    fun formatTimeZoneWithZero(string: String): Date {
        val simpleDateFormat = SimpleDateFormat(FORMAT_DATE_TIME_ZONE, locale)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("GMT+0:00")
        return simpleDateFormat.parse(string)
    }

    fun formatTime(string: String, pattern: String = "yyyy-MM-dd HH:mm:ss"): Date {
        val simpleDateFormat = SimpleDateFormat(pattern, locale)
        return simpleDateFormat.parse(string)
    }

    fun formatDate(millis: Long, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        val date = Date(millis)
        val simpleDateFormat = SimpleDateFormat(pattern, locale)
        return simpleDateFormat.format(date)
    }

    fun formatDate(date: Date, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        val simpleDateFormat = SimpleDateFormat(pattern, locale)
        return simpleDateFormat.format(date)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    fun formatCurrentDate(pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
        val date = Date()
        val simpleDateFormat = SimpleDateFormat(pattern, locale)
        return simpleDateFormat.format(date)
    }

    fun getHoursLater(nCount: Int): Date {
        val currentTime = System.currentTimeMillis() + nCount * 60 * 60 * 1000
        return Date(currentTime)
    }
}