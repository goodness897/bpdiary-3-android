package kr.co.openit.bpdiary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

/**
 * 단말 환경 데이터 조회 유틸리티
 * 
 * @author : 김일해
 * @date : 2013-07-05
 * @id : $Id$
 */
public class PhoneUtil {

    public static float dpToPx(Resources r, int value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, r.getDisplayMetrics());
    }

    public static int dpToPx(Context context, int dp) {
        int px = Math.round(dp * getPixelScaleFactor(context));
        return px;
    }

    public static int pxToDp(Context context, int px) {
        int dp = Math.round(px / getPixelScaleFactor(context));
        return dp;
    }
    private static float getPixelScaleFactor(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }


    public static int convertPixelsToDp(float px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return Math.round(dp);
    }

    public static int convertDpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }



    /**
     * 리소스 식별자를 얻는다.
     * 
     * @param context context
     * @param name 리소스 아이디
     * @param defType 리소스 타입
     * @return 리소스 식별자
     * @throws BuddyException
     */
    public static int getResId(Context context, String name, String defType) {

        return context.getResources().getIdentifier(name, defType, context.getPackageName());

    }

    /**
     * mdn을 얻는다
     * 
     * @param context context
     * @return mdn
     */
    public static String getMdn(Context context) {

        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        String number = tm.getLine1Number();
        if (number.contains(",")) {
            number = number.substring(0, number.indexOf(","));
        }
        return number.replace("-", "").replace("+82", "0");

    }

    /**
     * Mac Address를 얻는다. 인증에서만 사용(테스트 중)
     * 
     * @param context
     * @return MAC Address private static String MacAddress = null; public static String getMacAddr(Context context) {
     * if (MacAddress == null || MacAddress.equals("")) { WifiManager wifiManager =
     * (WifiManager)context.getSystemService(Context.WIFI_SERVICE); WifiInfo wifiInfo = wifiManager.getConnectionInfo();
     * String mac = wifiInfo.getMacAddress(); if (!StringUtil.isEmpty(mac)) { mac = mac.replaceAll(":", ""); } if
     * ("".equals(mac) || mac == null) { boolean isWifiEnabled = wifiManager.isWifiEnabled(); if
     * (!wifiManager.isWifiEnabled()) { try { wifiManager.setWifiEnabled(true); } catch (Exception e) {
     * e.printStackTrace(); } } mac = wifiInfo.getMacAddress(); if (!StringUtil.isEmpty(mac)) { mac =
     * mac.replaceAll(":", ""); } //원래 꺼져있었음 if (!isWifiEnabled) { try { wifiManager.setWifiEnabled(false); } catch
     * (Exception e) { e.printStackTrace(); } } } MacAddress = mac; return MacAddress; } else { return MacAddress; } }
     */

    /**
     * imsi를 얻는다.
     * 
     * @param context context
     * @return imsi
     */
    public static String getImsi(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getSubscriberId();
    }

    /**
     * imei를 얻는다
     * 
     * @param context context
     * @return imei
     */
    public static String getImei(Context context) {
        TelephonyManager mTelephonyMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getDeviceId();
    }

    /**
     * WIFI 네트웍 정보를 얻는다
     * 
     * @param context context
     * @return 네트웍 정보
     */
    public static NetworkInfo getWifiNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

    /**
     * 3G 네트웍 정보를 얻는다
     * 
     * @param context context
     * @return 네트웍 정보
     */
    public static NetworkInfo getMobileNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    }

    /**
     * 어플아이디를 얻는다
     * 
     * @param context context
     * @return 어플아이디
     */
    public static String getAppId(Context context) {
        String appId = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo i = pm.getPackageInfo(context.getPackageName(), 0);
            appId = i.applicationInfo.loadDescription(pm) + "";
        } catch (NameNotFoundException e) {
        }
        return appId;
    }

    /**
     * 버전을 얻는다
     * 
     * @param context context
     * @return 버전
     */
    public static String getVersion(Context context) {
        String version = "";
        try {
            PackageInfo i = context.getPackageManager().getPackageInfo("com.komsco.tsm.wallet", 0);
            version = i.versionName + "";
        } catch (NameNotFoundException e) {
        }
        return version;
    }

    /**
     * OS 버전을 얻는다
     * 
     * @return 버전
     */
    public static String getOsVersion() {
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 버전코드를 얻는다
     * 
     * @param context context
     * @return 버전코드
     */
    public static String getVersionCode(Context context) {

        //		Log.d("rlalfo", android.os.Build.VERSION.CODENAME);
        //		Log.d("rlalfo", android.os.Build.VERSION.INCREMENTAL);
        //		Log.d("rlalfo", android.os.Build.VERSION.RELEASE);
        //		Log.d("rlalfo", android.os.Build.VERSION.SDK);
        //		Log.d("rlalfo", android.os.Build.VERSION.SDK_INT+"");

        return android.os.Build.VERSION.RELEASE;

    }

    /**
     * 어플명을 얻는다
     * 
     * @param context context
     * @return 어플명
     */
    public static String getAppName(Context context) {
        String appName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo i = pm.getPackageInfo(context.getPackageName(), 0);
            appName = i.applicationInfo.loadLabel(pm) + "";
        } catch (NameNotFoundException e) {
        }
        return appName;
    }

    /**
     * 언어코드를 얻는다
     * 
     * @param context context
     * @return 언어코드
     */
    public static String getLang(Context context) {
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    public static boolean chkPackageInstalled(Context context, String PackageName) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo pi = manager.getPackageInfo(PackageName, PackageManager.GET_META_DATA);
            if (pi != null) {
                return true;
            }
            return false;
        } catch (NameNotFoundException NNE) {
            //			NNE.printStackTrace();
            return false;
        }
    }

    public static PackageInfo getPackageInfo(Context context, String AppId, String PackageName) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo pi = manager.getPackageInfo(PackageName, PackageManager.GET_META_DATA);
            ApplicationInfo app = pi.applicationInfo;
            String appid = (String)app.loadDescription(manager);
            if (AppId.equals(appid)) {
                return pi;
            }
            return null;
        } catch (NameNotFoundException NNE) {
            //			NNE.printStackTrace();
            return null;
        }
    }

    public static String isInstallPackage(Context context, String AppId, String PackageName) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo pi = manager.getPackageInfo(PackageName, PackageManager.GET_META_DATA);
            return pi.versionName + "";
        } catch (NameNotFoundException NNE) {
            //			NNE.printStackTrace();
            return null;
        }
    }

    public static String getAppVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo pi = manager.getPackageInfo("com.komsco.tsm.wallet", PackageManager.GET_META_DATA);
            return pi.versionName + "";
        } catch (NameNotFoundException NNE) {
            NNE.printStackTrace();
            return null;
        }
    }

    public static String getBuildId() {
        int length = android.os.Build.ID.length();
        String buildId = android.os.Build.ID;
        if (length < 6) {
            for (int i = length - 1; i < 6; i++) {
                buildId += "0";
            }
        } else if (length > 6) {
            buildId = buildId.substring(0, 5);
        }

        return buildId;
    }

    public static String getMnoInfo(Context context) {
        String mno = PhoneUtil.getNetworkOperatorName(context);
        mno = mno.toUpperCase();

        if (mno.contains("SKT")) {
            return "901";
        } else if (mno.contains("KT") || mno.contains("OLLEH")) {
            return "902";
        } else if (mno.contains("LG")) {
            return "903";
        } else {
            return "999";
        }
    }

    public static String getMnoInfo1(Context context) {
        String mno = PhoneUtil.getNetworkOperatorName(context);

        String operator = PhoneUtil.getNetworkOperator(context);

        mno = mno.toUpperCase();
        if (mno.contains("SKT")) {
            return "SKT";
        } else if (mno.contains("KT") || mno.contains("OLLEH")) {
            return "KT";
        } else if (mno.contains("LG")) {
            return "LGU";
        } else {
            if ("45005".equals(operator)) {
                //SKT
                return "SKT";
            } else if ("45008".equals(operator)) {
                return "KT";
            } else if ("45006".equals(operator)) {
                return "LGU";
            } else {
                return "Other";
            }
        }
    }

    /**
     * 최소 앱 버젼 체크
     * 
     * @param installedSsmVersion
     * @param needMinVer
     * @return
     */
    public static boolean checkAppVersion(String installedAppVersion, String needMinVer) {
        boolean needUpdate = false;
        final int DEFINED_LENGTH = 3;

        String[] splitCurrentVer = installedAppVersion.split("\\.");
        String[] splitRequireVer = needMinVer.split("\\.");

        if (splitCurrentVer.length < DEFINED_LENGTH) { // 현재 설치된 App의 version 규격이 잘못되었음.
            needUpdate = true;

        } else if (splitCurrentVer.length >= splitRequireVer.length) {
            int i = 0;
            int count = splitRequireVer.length;
            for (; i < count; i++) {
                if (Integer.parseInt(splitRequireVer[i]) < Integer.parseInt(splitCurrentVer[i])) {
                    needUpdate = false;
                    break;
                } else if (Integer.parseInt(splitRequireVer[i]) > Integer.parseInt(splitCurrentVer[i])) {
                    needUpdate = true;
                    break;
                }
            }

        }

        return needUpdate;
    }

    /**
     * 설치여부를 판단한다
     * 
     * @param context context
     * @param pkgName 패키지명
     * @return 설치여부
     */
    public static boolean isInstallPackage_2(Context context, String pkgName) {
        PackageManager manager = context.getPackageManager();
        boolean flag = false;
        PackageInfo pi;
        try {
            pi = manager.getPackageInfo(pkgName, PackageManager.GET_META_DATA);
            if (pi != null) {
                //return pi.versionName+"";
                flag = true;
            } else {
                flag = false;
            }
        } catch (NameNotFoundException e) {
            //			e.printStackTrace();
            flag = false;
        }

        return flag;
    }

    /**
     * 어플리케이션 실행한다
     * 
     * @param context context
     * @param packageName 패키지명
     */
    public static void runApp(Context context, String packageName) {
        Context appContext = context.getApplicationContext();
        PackageManager pm = appContext.getPackageManager();

        Intent intent = null;
        if (pm != null) {
            intent = pm.getLaunchIntentForPackage(packageName);
            if (intent == null) {
                return;
            }
        } else {
            return;
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 타임스탬프 로그를 찍는다
     * 
     * @param context context
     * @param startTime 시작시간
     * @param primitive 액션명
     * @param msg 문자열
     * @return 타임스탬프
     */
    public static long timestamp(Context context, long startTime, String primitive, String msg) {
        long t = System.currentTimeMillis();

        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(t);

            String mdn = getMdn(context);

            //            DateUtil.format(cal.getTime(), "yyyy.MM.dd HH:mm:ss.SSS");

            //			android.util.Log.i(primitive + " > " + mdn + " > " + msg, "timestamp > " + DateUtil.format(cal.getTime(), "yyyy.MM.dd HH:mm:ss.SSS") + " / " + (t - startTime));
        } catch (Exception e) {
        }

        return System.currentTimeMillis();
    }

    /**
     * 키보드를 숨긴다
     * 
     * @param a_oView view
     * @return 키보드숨김여부
     */
    public static boolean hideKeyboard(View a_oView) {
        InputMethodManager imm = (InputMethodManager)a_oView.getContext()
                                                            .getSystemService(Context.INPUT_METHOD_SERVICE);
        return imm.hideSoftInputFromWindow(a_oView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 키보드를 숨긴다
     * 
     * @param activity activity
     */
    public static void hideKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /**
     * 단말의 GPS On/Off 상태를 체크
     * 
     * @param Context context
     * @return 네트웍 정보
     */
    public static boolean chechGpsStatus(Context context) {

        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //			Toast.makeText(context, "GPS 설정을 켜 주세요!", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * 단말의 네트워크 접속 상태를 체크한다
     * 
     * @param context context
     * @return 접속 가능 상태 여부
     */
    public static boolean checkNetWorkStatus(Context context) {
        NetworkInfo mobile = getMobileNetwork(context);
        NetworkInfo wifi = getWifiNetwork(context);

        if (!wifi.isAvailable() && !mobile.isAvailable()) {
            //throw new BuddyException("Network-Setting is invalid!", "");
            return false;
        }

        else if (!wifi.isConnectedOrConnecting() && !mobile.isConnectedOrConnecting()) {
            //throw new SKTException("Network isn't connected!", Constants.Status.E_NETWORK);
            return false;
        }

        else if (!wifi.isRoaming() && mobile.isRoaming() && mobile.isConnectedOrConnecting()) {
            //throw new SKTException("Roaming not allow When 3G", Constants.Status.E_ROAMING);
            return false;
        }

        else {
            return true;
        }
    }

    /**
     * 비행기모드 상태를 체크한다
     * 
     * @param context context
     * @return 접속 가능 상태 여부
     */
    public static boolean isAirplaneModeOn(Context context) {
        return Settings.System.getInt(context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) != 0;
    }

    /**
     * 단말의 소리모드 상태를 체크한다
     * 
     * @param context context
     * @return 접속 가능 상태 여부
     */
    public static int checkSoundStatus(Context context) {
        AudioManager clsAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);

        switch (clsAudioManager.getRingerMode()) {

            case AudioManager.RINGER_MODE_VIBRATE:
                // 진동 모드
                return AudioManager.RINGER_MODE_VIBRATE;
            case AudioManager.RINGER_MODE_NORMAL:
                // 소리 모드
                return AudioManager.RINGER_MODE_NORMAL;
            case AudioManager.RINGER_MODE_SILENT:
                // 무음 모드 
                return AudioManager.RINGER_MODE_SILENT;

        }
        return -1;
    }

    public static String getRequester() {
        return "SK C&C";
    }

    /**
     * 단말의 OS 이름을 리턴한다
     * 
     * @param context context
     * @return OS 이름
     */
    public static String getOsName(Context context) {
        return "AND";
    }

    /**
     * 단말의 개통된 통신사 정보를 리턴한다.
     * 
     * @param context context
     * @return 통신사 이름
     */
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkOperatorName();
    }

    /**
     * 단말의 개통된 통신사 정보를 리턴한다.
     * 
     * @param context context
     * @return 통신사 이름
     */
    public static String getNetworkOperator(Context context) {
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getNetworkOperator();
    }

    /**
     * 단말의 모델명을 리턴한다.
     * 
     * @param context context
     * @return 단말 모델 명
     */
    public static String getModelName(Context context) {
        return android.os.Build.MODEL;
    }

    /**
     * 단말의 명을 리턴한다.
     * 
     * @param context context
     * @return 단말 모델 명
     */
    public static String getDevice(Context context) {
        return android.os.Build.DEVICE;
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1) {
                    break;
                }
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    /*
     * AudioManager를 통해 볼륨 정보를 얻고, 특정 볼륨으로 셋팅할 수 있다.
     */
    public static void getSoundVolume(Context context) {

        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public static int getSoundVolumeMax(Context context) {
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        return maxVol;
    }

    public static int getSoundVolumeCur(Context context) {
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int curVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        return curVol;
    }

    public static void setMinSoundVolum(Context context) {
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int minVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
    }

    public static void setMaxSoundVolum(Context context) {
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        int maxVol = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, maxVol, 0);
    }

    public static float dipInfo(Context context) {
        WindowManager wm = ((Activity)context).getWindowManager(); // getWindowManager는 Activity 의 메쏘드
        Display dp = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        dp.getMetrics(metrics);
        float dpi = metrics.density; //<- density/160 으로 계산된 값 출력 (ex, 160 dpi 해상도의 경우 1.0 출력)
        int densityDpi = metrics.densityDpi; //<- density 출력 (160 dpi 해상도의 경우 160 출력)

        return dpi;
    }

    public static String getMemoryUseage(Context context) {
        //String[] cmd = {"/system/bin/sh", "-c", "cat /proc/meminfo"};
        String[] cmd = {"procrank"};
        Runtime op = Runtime.getRuntime();
        try {
            Process proc = op.exec(cmd);

            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            while (in.readLine() != null) {
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return "";
    }

    public static void LogCurrentTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.KOREA);
        Date currentTime = new Date();
        String dTime = formatter.format(currentTime);
    }

    public static String getCurrentTime() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = df.format(new Date(System.currentTimeMillis()));
        return date;
    }

    public static String getUUID() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
}
