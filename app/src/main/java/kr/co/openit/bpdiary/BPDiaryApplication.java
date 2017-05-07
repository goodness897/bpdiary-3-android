package kr.co.openit.bpdiary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

import java.util.HashMap;
import java.util.Set;

import io.fabric.sdk.android.Fabric;
import kr.co.openit.bpdiary.activity.main.MainActivity;
import kr.co.openit.bpdiary.common.constants.BleConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.constants.ReturnCode;
import kr.co.openit.bpdiary.common.controller.BlueToothBLEService;
import kr.co.openit.bpdiary.common.controller.BlueToothBPBLEDeviceInterface;
import kr.co.openit.bpdiary.common.controller.BlueToothWSBLEDeviceInterface;

/**
 * Created by Openit on 2016-04-20.
 */
public class BPDiaryApplication extends Application {

    private static final String PROPERTY_ID = "UA-77952443-1";

    public static int GENERAL_TRACKER = 0;

    /**
     * true : 디버깅모드, false : 배포용
     */
    private boolean D = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // 페이스북 로그인 sdk 시작
        FacebookSdk.sdkInitialize(this);
        Fabric.with(this, new Crashlytics());
    }

    public enum TrackerName {
        APP_TRACKER, // Tracker used only in this app.
        GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg: roll-up tracking.
        ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a company.
    }

    HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

    /**
     * 인터넷 연결 상태 체크
     *
     * @param context
     * @return 인터넷이 정상적으로 연결 되었을때 true 리턴
     */
    public static boolean isNetworkState(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI
                    || activeNetwork.getType() == ConnectivityManager.TYPE_WIMAX
                    || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE
                    || activeNetwork.getType() == ConnectivityManager.TYPE_BLUETOOTH) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public synchronized Tracker getTracker(TrackerName trackerId) {
        if (!mTrackers.containsKey(trackerId)) {

            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            Tracker t = analytics.newTracker(PROPERTY_ID);
            //            Tracker t = (trackerId == TrackerName.APP_TRACKER) ? analytics.newTracker(PROPERTY_ID)
            //                                                              : (trackerId == TrackerName.GLOBAL_TRACKER) ? analytics.newTracker(R.xml.global_tracker)
            //                                                                                                         : analytics.newTracker(R.xml.ecommerce_tracker);
            t.enableAdvertisingIdCollection(true);
            t.enableExceptionReporting(true);
            mTrackers.put(trackerId, t);
        }
        return mTrackers.get(trackerId);
    }
}
