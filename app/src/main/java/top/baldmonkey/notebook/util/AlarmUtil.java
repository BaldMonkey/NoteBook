package top.baldmonkey.notebook.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

/**
 * 这是一个闹钟工具类
 * 用于向系统注册或者取消闹钟
 * 来控制何时发出待办提醒通知
 */
public class AlarmUtil {
    // 设置闹钟
    @RequiresApi(api = Build.VERSION_CODES.S)
    public static void setAlarm(Context context, long remindTime,int _id, String content) {
        // 获得系统提供的AlarmManager服务的对象
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        //Intent设置要启动的组件，这里启动广播接收器
        Intent intent = new Intent(context, AlarmReceiver.class);
        // 创建Bundle 传入待办信息
        Bundle bundle = new Bundle();
        bundle.putString("content", content);
        intent.putExtras(bundle); // 将bundle放入intent中
        // PendingIntent对象设置动作,启动的是Activity还是Service,或广播!
        // 传入 _id 的作用是给闹钟定一个id,防止多个闹钟冲突
        PendingIntent sender = PendingIntent.getBroadcast(
                context, _id, intent, PendingIntent.FLAG_IMMUTABLE);
        // 注册闹钟
        // RTC_WAKEUP：指定当系统调用System.currentTimeMillis()方法返回的值
        // 与triggerAtTime相等时启动operation所对应的设备
        // （在指定的时刻，发送广播，并唤醒设备）
        alarmManager.set(AlarmManager.RTC_WAKEUP, remindTime, sender);
    }

    // 取消闹钟
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void cancelAlarm(Context context, int _id) {
        // 创建意图
        Intent intent = new Intent(context, AlarmReceiver.class);
        // 按照闹钟id取消闹钟
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context, _id, intent, PendingIntent.FLAG_IMMUTABLE);
        // 获取闹钟服务
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        // 取消闹钟
        alarmManager.cancel(pendingIntent);
    }
}
