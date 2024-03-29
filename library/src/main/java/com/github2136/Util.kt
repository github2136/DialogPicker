package com.github2136

import android.content.res.Resources
import android.util.TypedValue
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Util {
    const val DATE_PATTERN_YMDHM = "yyyy-MM-dd HH:mm"
    const val DATE_PATTERN_YMD = "yyyy-MM-dd"
    const val DATE_PATTERN_HM = "HH:mm"

    fun dp2px(dp: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics).toInt()

    fun str2date(str: String, pattern: String): Date? {
        return try {
            val sdf = SimpleDateFormat(pattern, Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
            sdf.parse(str)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    fun date2str(date: Date, pattern: String): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.timeZone = TimeZone.getTimeZone(TimeZone.getDefault().id)
        return sdf.format(date)
    }
}