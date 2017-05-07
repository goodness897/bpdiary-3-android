package kr.co.openit.bpdiary.utils;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class DateUtil {

    public static String getDateNow(String format) {
        Date now = new Date();
        SimpleDateFormat fm = new SimpleDateFormat(format);
        String measureDt = fm.format(now);
        return measureDt;
    }

    public static String getEveDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date nowDate = null;
        try {
            nowDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long nowTime = nowDate.getTime();
        nowDate = new Date(nowTime + (1000 * 60 * 60 * 24 * -1));
        String eveDate = simpleDateFormat.format(nowDate);
        return eveDate;
    }

    public static String getNextDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Date nowDate = null;
        try {
            nowDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long nowTime = nowDate.getTime();
        nowDate = new Date(nowTime + (1000 * 60 * 60 * 24));
        String eveDate = simpleDateFormat.format(nowDate);
        return eveDate;
    }

    public static long getMilliSecondDate(String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date milliSecondDate = null;
        try {
            milliSecondDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return milliSecondDate.getTime();
    }

    public static String getLongToStringDate(long date) {
        SimpleDateFormat fm = new SimpleDateFormat("yyyyMMddHHmmss");
        String measureDt = fm.format(date);
        return measureDt;
    }

    public static String getLongToStringDatess(long date) {
        SimpleDateFormat fm = new SimpleDateFormat("mmss");
        String measureDt = fm.format(date);
        return measureDt;
    }

    public static String getLongToStringDate(String format, long date) {
        SimpleDateFormat fm = new SimpleDateFormat(format);
        String measureDt = fm.format(date);
        return measureDt;
    }

    public static long getMilliSecondDate(String format, String date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date milliSecondDate = null;
        try {
            milliSecondDate = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return milliSecondDate.getTime();
    }

    public static BluetoothGattCharacteristic setDateAND(BluetoothGattCharacteristic paramBluetoothGattCharacteristic,
                                                         Calendar paramCalendar) {
        int i = paramCalendar.get(Calendar.YEAR);
        int j = paramCalendar.get(Calendar.MONTH);
        int k = paramCalendar.get(Calendar.DAY_OF_MONTH);
        int m = paramCalendar.get(Calendar.HOUR_OF_DAY);
        int n = paramCalendar.get(Calendar.MINUTE);
        int i1 = paramCalendar.get(Calendar.SECOND);
        paramBluetoothGattCharacteristic.setValue(new byte[] {(byte)(i & 0xFF),
                                                              (byte)(i >> 8),
                                                              (byte)(j + 1),
                                                              (byte)k,
                                                              (byte)m,
                                                              (byte)n,
                                                              (byte)i1});
        return paramBluetoothGattCharacteristic;
    }
}
