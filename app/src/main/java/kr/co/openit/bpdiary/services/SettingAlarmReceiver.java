package kr.co.openit.bpdiary.services;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.main.MainActivity;
import kr.co.openit.bpdiary.activity.setting.AlarmUtils;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.vo.apdu.ConfirmedActionPrstApdu;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Created by hwangem on 2017-01-12.
 */
/**
 * 복약/측정 알람 노티 리시버
 */
public class SettingAlarmReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        // 복약/측정 구분 플래그
        String alarmFlag = intent.getStringExtra(ManagerConstants.IntentData.ALARM_FLAG);
        int notifySEQ = intent.getIntExtra(ManagerConstants.IntentData.ALARM_SEQ, 0);
        long hour = intent.getLongExtra(ManagerConstants.IntentData.ALARM_HOUR, 0);
        long minute = intent.getLongExtra(ManagerConstants.IntentData.ALARM_MINUTE, 0);
        StringBuilder builder = new StringBuilder();

        String time = null;

        // 알림시간 형태 : ex) 오전 9시 00분 -> "0900"
        if (hour < 10 && minute < 10) {
            time = builder.append("0").append(hour).append("0").append(minute).toString();

        } else if (hour < 10 && minute >= 10) {
            time = builder.append("0").append(hour).append(minute).toString();

        } else if (hour >= 10 && minute < 10) {
            time = builder.append(hour).append("0").append(minute).toString();

        } else if (hour >= 10 && minute >= 10) {
            time = builder.append(hour).append(minute).toString();
        }

        Intent alarmIntent = new Intent(context, MainActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        alarmIntent.putExtra(ManagerConstants.IntentData.ALARM_SEQ, notifySEQ);
        alarmIntent.putExtra(ManagerConstants.IntentData.ALARM_FLAG, alarmFlag);
        alarmIntent.putExtra(ManagerConstants.IntentData.ALARM_TIME, time);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                                                                notifySEQ,
                                                                alarmIntent,
                                                                PendingIntent.FLAG_UPDATE_CURRENT);

        int bgColor = context.getResources().getColor(R.color.color_ffffff);

        Notification foregroundNote;
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setContentIntent(pendingIntent);

        // 복약 노티일 경우
        if (alarmFlag.equals(ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y)) {
            foregroundNote = notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                                .setLargeIcon((BitmapFactory.decodeResource(context.getResources(),
                                                                                            R.drawable.bpdiary_noti_icon)))
                                                .setSmallIcon(R.drawable.bpdiary_noti_icon)
                                                .setColor(bgColor)
                                                .setContentTitle(context.getText(R.string.medicine_alarm_title))
                                                .setContentText(context.getText(R.string.medicine_alarm_content))
                                                .setContentIntent(pendingIntent)
                                                .setAutoCancel(true)
                                                .build();

            // 측정 노티일 경우
        } else {
            foregroundNote = notificationBuilder.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                                                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                                                                                           R.drawable.bpdiary_noti_icon))
                                                .setSmallIcon(R.drawable.bpdiary_noti_icon)
                                                .setColor(bgColor)
                                                .setContentTitle(context.getText(R.string.measure_alarm_title))
                                                .setContentText(context.getText(R.string.measure_alarm_content))
                                                .setContentIntent(pendingIntent)
                                                .setAutoCancel(true)
                                                .build();
        }

        foregroundNote.flags = NotificationCompat.FLAG_AUTO_CANCEL;

        NotificationManager mNotifyManager =
                                           (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        mNotifyManager.notify(notifySEQ, foregroundNote);
    }
}
