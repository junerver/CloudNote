package com.edusoa.ideallecturer.utils

import android.annotation.SuppressLint
import java.text.ParsePosition
import java.text.SimpleDateFormat
import java.util.*


/**
 * 通用时间类
 * @author czh
 */
object TimeUtils {
    /**
     * 系统计时开始时间
     */
    val SYSTEM_START_DATE = intArrayOf(1970, 0, 1, 0, 0, 0)


    val LEVEL_YEAR = 0
    val LEVEL_MONTH = 1
    val LEVEL_DAY = 2
    val LEVEL_HOUR = 3
    val LEVEL_MINUTE = 4
    val LEVEL_SECOND = 5
    val LEVELS =
        intArrayOf(LEVEL_YEAR, LEVEL_MONTH, LEVEL_DAY, LEVEL_HOUR, LEVEL_MINUTE, LEVEL_SECOND)

    val NAME_YEAR = "年"
    val NAME_MONTH = "月"
    val NAME_DAY = "日"
    val NAME_HOUR = "时"
    val NAME_MINUTE = "分"
    val NAME_SECOND = "秒"
    val LEVEL_NAMES = arrayOf(NAME_YEAR, NAME_MONTH, NAME_DAY, NAME_HOUR, NAME_MINUTE, NAME_SECOND)


    val YEAR = 0
    val MONTH = 1
    val DAY_OF_MONTH = 2
    val HOUR_OF_DAY = 3
    val MINUTE = 4
    val SECOND = 5


    val MIN_TIME_DETAILS = intArrayOf(0, 0, 0)
    val MAX_TIME_DETAILS = intArrayOf(23, 59, 59)


    val NAME_THE_DAY_BEFORE_YESTERDAY = "前天"
    val NAME_YESTERDAY = "昨天"
    val NAME_TODAY = "今天"
    val NAME_TOMORROW = "明天"
    val NAME_THE_DAY_AFTER_TOMORROW = "后天"


    val TYPE_SUNDAY = 0
    val TYPE_MONDAY = 1
    val TYPE_TUESDAY = 2
    val TYPE_WEDNESDAY = 3
    val TYPE_THURSDAY = 4
    val TYPE_FRIDAY = 5
    val TYPE_SATURDAY = 6
    val DAY_OF_WEEK_TYPES = intArrayOf(
        TYPE_SUNDAY,
        TYPE_MONDAY,
        TYPE_TUESDAY,
        TYPE_WEDNESDAY,
        TYPE_THURSDAY,
        TYPE_FRIDAY,
        TYPE_SATURDAY
    )

    val NAME_SUNDAY = "日"
    val NAME_MONDAY = "一"
    val NAME_TUESDAY = "二"
    val NAME_WEDNESDAY = "三"
    val NAME_THURSDAY = "四"
    val NAME_FRIDAY = "五"
    val NAME_SATURDAY = "六"
    val DAY_OF_WEEK_NAMES = arrayOf(
        NAME_SUNDAY,
        NAME_MONDAY,
        NAME_TUESDAY,
        NAME_WEDNESDAY,
        NAME_THURSDAY,
        NAME_FRIDAY,
        NAME_SATURDAY
    )

    /**获取日期 年，月， 日 对应值
     * @param date
     * @return
     */
    fun getDateDetail(date: Date?): IntArray? {
        return if (date == null) null else getDateDetail(date.time)
    }

    /**获取日期 年，月， 日 对应值
     * @param time
     * @return
     */
    fun getDateDetail(time: Long): IntArray {
        val mCalendar = Calendar.getInstance()
        mCalendar.timeInMillis = time
        return intArrayOf(
            mCalendar.get(Calendar.YEAR), //0
            mCalendar.get(Calendar.MONTH) + 1, //1
            mCalendar.get(Calendar.DAY_OF_MONTH)
        )//2
    }


    /**
     * 根据生日计算星座
     *
     * @param birthday
     * @return constellation
     */
    fun getStar(birthday: Date): String {
        val c = Calendar.getInstance()
        c.time = birthday
        var month = c.get(Calendar.MONTH)
        val dayOfMonth = c.get(Calendar.DAY_OF_MONTH)
        val DayArr = intArrayOf(19, 18, 20, 19, 20, 21, 22, 22, 22, 23, 22, 21)
        val starArr = arrayOf(
            "魔羯座",
            "水瓶座",
            "双鱼座",
            "白羊座",
            "金牛座",
            "双子座",
            "巨蟹座",
            "狮子座",
            "处女座",
            "天秤座",
            "天蝎座",
            "射手座"
        )
        if (dayOfMonth > DayArr[month]) {
            month = month + 1
            if (month == 12) {
                month = 0
            }
        }
        return starArr[month]
    }


    /**
     * 获取日期 年，月， 日， 时， 分， 秒 对应值
     *
     * @param time
     * @return
     */
    fun getWholeDetail(time: Long): IntArray {
        val mCalendar = Calendar.getInstance()
        mCalendar.timeInMillis = time
        return intArrayOf(
            mCalendar.get(Calendar.YEAR), //0
            mCalendar.get(Calendar.MONTH) + 1, //1
            mCalendar.get(Calendar.DAY_OF_MONTH), //2
            mCalendar.get(Calendar.HOUR_OF_DAY), //3
            mCalendar.get(Calendar.MINUTE), //4
            mCalendar.get(Calendar.SECOND)//5
        )
    }

    /**
     * 智能时间显示，12:30,昨天，前天...
     *
     * @param date
     * @return
     */
    fun Date?.getSmartDate(): String {
        return this?.time?.getSmartDate() ?: ""
    }

    /**
     * 智能时间显示，12:30,昨天，前天...
     *
     * @param date
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    fun Long.getSmartDate(): String {

        val nowDetails = getWholeDetail(System.currentTimeMillis())
        val smartDetail = getWholeDetail(this)

        var smartDate = ""

        if (nowDetails[0] == smartDetail[0]) {
            if (nowDetails[1] == smartDetail[1]) {
                val time = " " + SimpleDateFormat("HH:mm").format(this)
                val day = (nowDetails[2] - smartDetail[2]).toLong()
                if (day >= 3) {
                    smartDate = smartDetail[2].toString() + "日" + time
                } else if (day >= 2) {
                    smartDate = "前天$time"
                } else if (day >= 1) {
                    smartDate = "昨天$time"
                } else if (day >= 0) {
                    if (0 == nowDetails[TimeUtils.HOUR_OF_DAY] - smartDetail[TimeUtils.HOUR_OF_DAY]) {
                        val minute =
                            (nowDetails[TimeUtils.MINUTE] - smartDetail[TimeUtils.MINUTE]).toLong()
                        if (minute < 1) {
                            smartDate = "刚刚"
                        } else if (minute < 31) {
                            smartDate = minute.toString() + "分钟前"
                        } else {
                            smartDate = time
                        }
                    } else {
                        smartDate = time
                    }
                } else if (day >= -1) {
                    smartDate = "明天$time"
                } else if (day >= -2) {
                    smartDate = "后天$time"
                } else {
                    smartDate = smartDetail[2].toString() + "日" + time
                }
            } else {
                smartDate = smartDetail[1].toString() + "月" + smartDetail[2].toString() + "日"
            }
        } else {
            smartDate = smartDetail[0].toString() + "年" + smartDetail[1].toString() + "月"
        }
        return smartDate
    }


    /**
     * 获取智能生日
     *
     * @param birthday
     * @param needYear
     * @return
     */
    fun getSmartBirthday(birthday: Long, needYear: Boolean): String {
        val birthdayDetails = getDateDetail(birthday)
        val nowDetails = getDateDetail(System.currentTimeMillis())

        val birthdayCalendar = Calendar.getInstance()
        birthdayCalendar.set(birthdayDetails[0], birthdayDetails[1], birthdayDetails[2])

        val nowCalendar = Calendar.getInstance()
        nowCalendar.set(nowDetails[0], nowDetails[1], nowDetails[2])

        val days =
            birthdayCalendar.get(Calendar.DAY_OF_YEAR) - nowCalendar.get(Calendar.DAY_OF_YEAR)
        if (days < 8) {
            if (days >= 3) {
                return days.toString() + "天后"
            }
            if (days >= 2) {
                return TimeUtils.NAME_THE_DAY_AFTER_TOMORROW
            }
            if (days >= 1) {
                return TimeUtils.NAME_TOMORROW
            }
            if (days >= 0) {
                return TimeUtils.NAME_TODAY
            }
        }

        return if (needYear) {
            birthdayDetails[0].toString() + "年" + birthdayDetails[1] + "月" + birthdayDetails[2] + "日"
        } else birthdayDetails[1].toString() + "月" + birthdayDetails[2] + "日"
    }

    /**根据生日获取年龄
     * @param birthday
     * @return
     */
    fun getAge(birthday: Date?): Int {
        if (birthday == null) {
            return 0
        }
        val calendar = Calendar.getInstance(Locale.CHINA)
        calendar.time = birthday
        return getAge(
            intArrayOf(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        )
    }

    /**根据生日获取年龄
     * @param birthday
     * @return
     */
    fun getAge(birthday: Long): Int {
        return getAge(getDateDetail(birthday))
    }

    /**根据生日获取年龄
     * @param birthday
     * @return
     */
    fun getAge(birthdayDetail: IntArray?): Int {
        if (birthdayDetail == null || birthdayDetail.size < 3) {
            return 0
        }

        val nowDetails = getDateDetail(System.currentTimeMillis())

        var age = nowDetails[0] - birthdayDetail[0]

        if (nowDetails[1] < birthdayDetail[1]) {
            age = age - 1
        } else if (nowDetails[1] == birthdayDetail[1]) {
            if (nowDetails[2] < birthdayDetail[2]) {
                age = age - 1
            }
        }

        return age
    }


    /**根据日期获取指定格式时间
     * @return format yyyy-MM-dd HH:mm:ss
     */

    @SuppressLint("SimpleDateFormat")
    fun Long.dateToString(format: String = "yyyy-MM-dd HH:mm:ss"): String? {
        try {
            return SimpleDateFormat(format).format(this)
        } catch (e: Exception) {
            return null
        }

    }

    /**根据日期获取指定格式时间
     * @return format yyyy-MM-dd HH:mm:ss
     */

    @SuppressLint("SimpleDateFormat")
    fun Date.dateToString(format: String = "yyyy-MM-dd HH:mm:ss"): String? {
        try {
            return SimpleDateFormat(format).format(this.time)
        } catch (e: Exception) {
            return null
        }

    }

    fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    @JvmStatic
    fun currentTimeSecond(): Long {
        return System.currentTimeMillis() / 1000
    }

    fun String.timestampToString(format: String = "yyyy-MM-dd HH:mm:ss"): String? {
        return try {
            val time = this.toLong()
            time.dateToString(format)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 获取系统时间
     * @return
     */
    fun getDate(): String {
        var ca = Calendar.getInstance()
        var year = ca.get(Calendar.YEAR)           // 获取年份
        var month = ca.get(Calendar.MONTH)         // 获取月份
        var day = ca.get(Calendar.DATE)            // 获取日
        var minute = ca.get(Calendar.MINUTE)       // 分
        var hour = ca.get(Calendar.HOUR)           // 小时
        var second = ca.get(Calendar.SECOND)       // 秒
        return "" + year + (month + 1) + day + hour + minute + second
    }

    fun isMorning(): Boolean {
        return currentTimeMillis().dateToString("HH:mm")!!.substring(0..1).toInt() < 12
    }

    /**
     * @Description 将当前的时间转换为数字 用于比较时间是否处于特定时间段内
     * 例如 12:30 ->1230 处于12点到13点之间 1200..1300
     * @Author Junerver
     * Created at 2019-09-11 11:08
     * @param
     * @return
     */
    fun timeToInt(): Int {
        return currentTimeMillis().dateToString("HHmm")!!.toInt()
    }

    /**
     * @Description 将指定的时间转换成今日该时间的时间戳（毫秒）
     * @Author Junerver
     * Created at 2019-09-11 11:11
     * @param time 12:30
     * @return
     */
    fun getTimeInDayByStr(time: String): Long {
        var c = Calendar.getInstance()
        c.time = Date()
        c.set(
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH), time.split(':')[0].toInt(), time.split(':')[1].toInt(), 0
        )
        println(c.timeInMillis)
        return c.timeInMillis
    }

    /**
     * @Description 根据格式化后的时间计算出对应的时间戳
     * @Author Junerver
     * Created at 2021/10/14 09:24
     * @param
     * @return unix时间戳：秒
     */
    fun String.convertToTimestamp(format: String = "yyyy-MM-dd HH:mm:ss"): Long =
        SimpleDateFormat(format).parse(this, ParsePosition(0)).time/1000

}


