package com.payby.pos.common.helper

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object MonthWeekHelper {

    private val locale = Locale.US

    /**
     * 获取今天是几号
     */
    fun getDayOfMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取某天的 n 天前是几号
     */
    fun getDayOfMonthWithPrevious(date: Date, distance: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - distance)
        return calendar.get(Calendar.DAY_OF_MONTH)
    }

    /**
     * 获取某天的 n 天前的日期
     */
    fun getDayWithPrevious(date: Date, distance: Int): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - distance)
        return calendar.time
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取某周第一天的日期
     */
    fun getWeekWithFirstDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        // 获得当前日期是一个星期的第几天
        val dayWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (dayWeek == Calendar.SUNDAY) {
            // 星期天向前减一天即得到上一周
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }
        // 设置一个星期的第一天, 按中国的习惯一个星期的第一天是星期一
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        // calendar.set(Calendar.HOUR_OF_DAY, 0)
        // calendar.set(Calendar.MINUTE, 0)
        // calendar.set(Calendar.SECOND, 0)
        return calendar.time
    }

    /**
     * 获取某周最后一天的日期
     */
    fun getWeekWithLastDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val dayWeek = calendar.get(Calendar.DAY_OF_WEEK)
        if (dayWeek == Calendar.SUNDAY) {
            calendar.add(Calendar.DAY_OF_MONTH, -1)
        }
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY)
        calendar.add(Calendar.DAY_OF_WEEK, 1)
        return calendar.time
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取今天是几月 - 1月从 0 开始
     */
    fun getMonth(): Int {
        val calendar = Calendar.getInstance()
        return calendar.get(Calendar.MONTH)
    }

    /**
     * 获取某天的 n 天前是几月
     */
    fun getMonthWithPrevious(date: Date, distance: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - distance)
        return calendar.get(Calendar.MONTH)
    }

    /**
     * 获取某天的这个月第一天的日期
     */
    fun getMonthWithFirstDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        return calendar.time
    }

    /**
     * 获取某天的这个月最后一天的日期
     */
    fun getMonthWithLastDay(date: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        calendar.set(Calendar.DAY_OF_MONTH, actualMaximum)
        return calendar.time
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * 获取某天的开始时间 - 00:00:00 开始
     */
    fun getDateWithStart(date: Date): Date {
        var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", locale)
        val formatString = simpleDateFormat.format(date) + " 00:00:00"
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
        return simpleDateFormat.parse(formatString)
    }

    /**
     * 获取某天的结束时间 - 23:59:59 结束
     */
    fun getDateWithEnd(date: Date): Date {
        var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", locale)
        val formatString = simpleDateFormat.format(date) + " 23:59:59"
        simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale)
        return simpleDateFormat.parse(formatString)
    }

}