package top.baldmonkey.notebook.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;

import top.baldmonkey.notebook.R;
import top.baldmonkey.notebook.activity.MainActivity;

/**
 * 这是继承自 BroadcastReceiver 的
 * 自定义的 AlarmReceiver 广播接收器
 * 用于向用户发出待办提醒
 */
public class AlarmReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        // 获取系统通知服务
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 创建意图
        Intent intent1 = new Intent(context, MainActivity.class);
        // 创建点击通知后的跳转意图
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent1, PendingIntent.FLAG_IMMUTABLE);
        // 创建通知
        Notification notification;
        Bundle bundle = intent.getExtras();
        // 判断如果为安卓8.0以上设置频道id
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建频道id
            NotificationChannel notificationChannel = new NotificationChannel(
                    "BaldMonkey", "待办提醒",
                    NotificationManager.IMPORTANCE_HIGH); // 弹出并在通知栏显示
            // 给通知管理器创建频道id
            notificationManager.createNotificationChannel(notificationChannel);
            // 实例化通知,channelId与上面设置的一致
            notification = new Notification.Builder(context,"BaldMonkey")
                    .setSmallIcon(R.drawable.ic_baseline_alarm_24)
                    .setTicker("待办提醒")
                    .setContentTitle("待办事项提醒")
                    .setContentText(bundle.getString("content"))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
            // 发送通知
            notificationManager.notify(3, notification);
        }
    }
}