package top.baldmonkey.notebook.util;

import android.annotation.SuppressLint;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 这是一个获取日期的工具类
 */
public class DateUtil {
    // 获取当前日期,并返回一个日期字符串
    public static String getCurrentTime() {
        // 设置格式
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat
                = new SimpleDateFormat("MM月dd日 aa HH:mm");
        // 返回日期字符串
        return dateFormat.format(new Date());
    }

    // 传入一个Date类型对象,转换为字符串
    public static String getFormatTime(Date date) {
        // 设置格式
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat
                = new SimpleDateFormat("MM月dd日 aa HH:mm");
        return simpleDateFormat.format(date);
    }

    // 传入一个util包下Date类型日期时间，返回对应的绝对时间(long类型的毫秒数)
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static long getTimeMillis(String dateTime) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat
                = new SimpleDateFormat("MM月dd日 aa HH:mm");
        try {
            Date date = dateFormat.parse(dateTime);
            // 设置年份当前年份
            // 由于java.util.Date类中年份从1900年开始,需要 - 1900
            date.setYear(Calendar.getInstance().get(Calendar.YEAR) - 1900);
            return date.getTime();
        } catch (ParseException e) {
        Log.e("DateUtil->", "getTimeMillis: " + "捕获异常");
    }
        return System.currentTimeMillis() + 5000;
    }
}
