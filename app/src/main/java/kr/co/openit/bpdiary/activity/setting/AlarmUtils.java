package kr.co.openit.bpdiary.activity.setting;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.services.SettingAlarmReceiver;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.DataBase;
import kr.co.openit.bpdiary.services.MedicineMeasureAlarmService;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Created by hwangem on 2017-01-06.
 */

public class AlarmUtils {

    /**
     * service
     */
    private MedicineMeasureAlarmService mAlarmService;

    private static AlarmUtils instance;

    public static AlarmUtils getInstance(Context context) {
        if (instance == null)
            instance = new AlarmUtils(context);

        return instance;
    }

    public AlarmUtils(Context context) {
        mAlarmService = new MedicineMeasureAlarmService(context);
    }

    /**
     * 복약 알림을 위한 모델 값 셋팅과 DB 저장, isToggleOn : 알림 전체 토글ON/OFF상태 (AlarmActivity)
     */
    public void setMedicineAlarmData(Context context, long hour, long minute, String alarmFlag) {
        String time = null;

        StringBuilder builder = new StringBuilder();

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

        Map<String, String> rMap = new HashMap<>();
        rMap.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_TYPE, alarmFlag);
        rMap.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_STATUS, ManagerConstants.AlarmToggleState.ALARM_TOGGLE_ON);
        rMap.put(DataBase.COLUMN_NAME_MEDICINE_MEASURE_ALARM_NOTIFY_TIME, time);

        // DB 저장 
        mAlarmService.insertMedicineMeasureAlarmData(rMap);

        setAlarmManager(context, time, alarmFlag, null, true);
    }

    /**
     * 복약/측정 알림을 위한 AlarmManager 셋팅, isWholeToggleOn : 전체 토글 on/off (AlarmActivity)
     */
    public void setAlarmManager(Context context,
                                String time,
                                String alarmFlag,
                                String notifySEQ,
                                boolean cvToggleState) {
        boolean isToggle = PreferenceUtil.getWholeToggleState(context);
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        long hour = Long.parseLong(time.substring(0, 2));
        long minute = Long.parseLong(time.substring(2, 4));

        int notifySeq = 0;
        // 새로 추가한 알림인 경우
        if (notifySEQ == null && alarmFlag != null) {

            if (alarmFlag.equals(ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y)) { // 복약 알림
                // 가장 최근의 값의 노티ID
                notifySeq =
                          mAlarmService.getMedicineMeasureAlarmLatestSEQ(ManagerConstants.AlarmSyncFlag.MEDICINE_SYNC_Y);

            } else if (alarmFlag.equals(ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y)) { // 측정 알림
                // 가장 최근의 값의 노티ID
                notifySeq =
                          mAlarmService.getMedicineMeasureAlarmLatestSEQ(ManagerConstants.AlarmSyncFlag.MEASURE_SYNC_Y);
            }

            // (토글 상태변경을 위한)알림인 경우
        } else {
            notifySeq = Integer.parseInt(notifySEQ);
        }

        // 노티를 위한 Receiver
        Intent intent = new Intent(context, SettingAlarmReceiver.class);
        intent.putExtra(ManagerConstants.IntentData.ALARM_FLAG, alarmFlag); // 알람구분플래그
        intent.putExtra(ManagerConstants.IntentData.ALARM_SEQ, notifySeq); // 알람SEQ
        intent.putExtra(ManagerConstants.IntentData.ALARM_HOUR, hour); // 알람 HOUR
        intent.putExtra(ManagerConstants.IntentData.ALARM_MINUTE, minute); // 알람 MINUTE

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notifySeq, intent, 0);

        // 알림 시간 셋팅
        Calendar calendar1 = Calendar.getInstance();
        int year = calendar1.get(Calendar.YEAR);
        int month = calendar1.get(Calendar.MONTH);
        int date = calendar1.get(Calendar.DAY_OF_MONTH);
        int week = calendar1.get(Calendar.WEEK_OF_MONTH);

        calendar1.set(Calendar.YEAR, year);
        calendar1.set(Calendar.MONTH, month);
        calendar1.set(Calendar.DAY_OF_MONTH, date);

        calendar1.set(Calendar.WEEK_OF_MONTH, week);
        calendar1.set(Calendar.HOUR_OF_DAY, (int)hour);
        calendar1.set(Calendar.MINUTE, (int)minute);
        calendar1.set(Calendar.SECOND, 0);

        // 전체 토글 상태가 ON일때
        if (isToggle == true) {
            // 토글 상태가 ON일때 
            if (cvToggleState == true) {
                setCallAlarm(calendar1, manager, pendingIntent);
            } else {
                setCancelAlarm(context, notifySeq);
            }
        }
    }

    /**
     * 해당 notifySeq로 이미 생성된 알람이 있는지 확인
     */
    public PendingIntent setCheckAlarm(Context context, int notifySeq) {
        Intent intent = new Intent(context, SettingAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notifySeq, intent, PendingIntent.FLAG_NO_CREATE);

        return pendingIntent;
    }

    /**
     * Call Alarm
     */
    public void setCallAlarm(Calendar calendar, AlarmManager manager, PendingIntent pendingIntent) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            } else {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }

        } else {
            if (Build.VERSION.SDK_INT >= 19) {
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

                } else {
                    manager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }

            } else {
                if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                                         calendar.getTimeInMillis(),
                                         AlarmManager.INTERVAL_DAY,
                                         pendingIntent);

                } else {
                    manager.setRepeating(AlarmManager.RTC_WAKEUP,
                                         calendar.getTimeInMillis(),
                                         AlarmManager.INTERVAL_DAY,
                                         pendingIntent);
                }
            }
        }
    }

    /**
     * 해당 노티SEQ의 복약 알림 해제
     */
    public void setCancelAlarm(Context context, int notifySEQ) {
        AlarmManager manager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SettingAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                                                                 notifySEQ,
                                                                 intent,
                                                                 PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
        pendingIntent.cancel();
    }
}
