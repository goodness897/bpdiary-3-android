package kr.co.openit.bpdiary.activity.intro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.activity.common.PermissionsActivity;
import kr.co.openit.bpdiary.activity.main.MainActivity;
import kr.co.openit.bpdiary.common.constants.CommonConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.AppConfig;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.AppUpdateVersionType;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.DataBase;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.Language;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.RequestParamName;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.ResponseParamName;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.ResponseResult;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.model.EventInfoData;
import kr.co.openit.bpdiary.services.BloodPressureService;
import kr.co.openit.bpdiary.services.IntroService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PhoneUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class IntroActivity extends NonMeasureActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /**
     * 뒤로 가기 예외처리
     */
    private boolean isBackKey = false;

    /**
     * GCM Token
     */
    private String regid;

    /**
     * 언어
     */
    private String strLanguage;

    /**
     * IntroService
     */
    private IntroService introService;

    /**
     * blood pressure service
     */
    private BloodPressureService bpService;

    private String operatorName = "";

    private Context context;

    @Override
    protected void onPause() {
        super.onPause();

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showLodingProgress();
        context = IntroActivity.this;
        operatorName = PhoneUtil.getNetworkOperatorName(context).replaceAll("", "_");

        AnalyticsUtil.sendScene(IntroActivity.this, "3_인트로");
        //Intro service
        introService = new IntroService(context);

        bpService = new BloodPressureService(context);
        //        checkLogic();
        //기본 언어 설정
        setDefaultLanguage();

        if(PreferenceUtil.getLanguage(context).equals(Language.KOR)){
            setContentView(R.layout.activity_intro);
        }else {
            setContentView(R.layout.activity_intro_en);
        }

        //Preference Data 마이그레이션
        if ((DataBase.VERSION == 3) && (!PreferenceUtil.getIsPreferenceMigration(context))) {
            PreferenceUtil.doPreferenceMigration(context);
        }
        if ((DataBase.VERSION == 3) && (!PreferenceUtil.getIsDBMigration(context))) {
            //DB Data 마이그레이션
            new MigrationDBDataSync().execute();
        }

        Handler mHandler = new Handler();

        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                //6.0 권한 대응
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    getWindow().setStatusBarColor(getResources().getColor(R.color.color_ffffff));
                    Intent intent = new Intent(context, PermissionsActivity.class);
                    startActivityForResult(intent, CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_PERMISSIONS);
                    overridePendingTransition(0, 0);
                } else {
                    appVersionChecker();
                }
            }
        }, 200);
    }

    /**
     * Back Key
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isBackKey = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_AUTO) {
            if (resultCode == RESULT_OK) {
                //Event 데이터가 있으면 모델을 메인으로 던짐
                if (intent != null) {
                    EventInfoData eventInfoData =
                                                (EventInfoData)intent.getSerializableExtra(CommonConstants.EVENT_DATA);
                    moveMainIntentActivity(eventInfoData);
                    //Event 데이터가 없으면 메인으로 이동
                } else {
                    moveMainIntentActivity();
                }
                //로그인이 cancel됐을때 메인으로 이동
            } else if (resultCode == RESULT_CANCELED) {
                moveMainIntentActivity();
            }
        } else if (CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_PERMISSIONS == requestCode) {
            if (resultCode == RESULT_OK) {
                appVersionChecker();
            } else {
                Toast.makeText(context, "동의 없이 app사용을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * 인터넷이 안될때나 비로그인 상태에서 메인으로 보냄
     */
    private void moveMainIntentActivity() {
        if (PreferenceUtil.getIsLogin(context)) {
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        } else {
            DefaultOneButtonDialog defaultOneButtonDialog = new DefaultOneButtonDialog(context,
                                                                                       getString(R.string.dialog_title_alarm),
                                                                                       getString(R.string.common_error_comment),
                                                                                       getString(R.string.common_txt_confirm),
                                                                                       new IDefaultOneButtonDialog() {

                                                                                           @Override
                                                                                           public void onConfirm() {
                                                                                               finish();
                                                                                           }
                                                                                       });
            defaultOneButtonDialog.show();
        }
    }

    /**
     * 로그인 API 호출 후, Event 데이터가 있을때 메인으로 보냄
     *
     * @param eventInfoData
     */
    private void moveMainIntentActivity(EventInfoData eventInfoData) {
        if (PreferenceUtil.getIsLogin(context)) {
            Intent intent = new Intent(IntroActivity.this, MainActivity.class);
            intent.putExtra(CommonConstants.EVENT_DATA, eventInfoData);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        } else {
            DefaultOneButtonDialog defaultOneButtonDialog = new DefaultOneButtonDialog(context,
                                                                                       "",
                                                                                       getString(R.string.common_error_comment),
                                                                                       getString(R.string.common_txt_confirm),
                                                                                       new IDefaultOneButtonDialog() {

                                                                                           @Override
                                                                                           public void onConfirm() {
                                                                                               finish();
                                                                                           }
                                                                                       });
            defaultOneButtonDialog.show();
        }
    }

    /**
     * 외부 저장 장치 확인
     */
    private void checkLogic() {
        //Preference 체크 Gender문제..
        PreferenceUtil.versionCheck(getApplicationContext());

        //TODO  DB쪽도 Gender 문제 해결해야함

        //Configure 에러 class not found 때문에... 삭제해야됨
        int code = PreferenceUtil.getPreVersionCode(getApplicationContext());

        if (code != ManagerUtil.getAppCode(getApplicationContext())) {
            deleteDir(AppConfig.STORAGE_PATH);
            PreferenceUtil.setPreVersionCode(getApplicationContext(), code);
        }
    }

    /**
     * 인터넷 On : 버전체크, 인터넷 Off : 메인으로
     */
    private void appVersionChecker() {
        if (BPDiaryApplication.isNetworkState(context)) {
            new AppUpdateAsync().execute();
        } else {
            moveMainIntentActivity();
        }
    }

    /**
     * 외부 저장 장치 초기화
     *
     * @param strPath
     */
    private void deleteDir(String strPath) {
        File file = new File(strPath);
        if (file.exists()) {
            File[] childFileList = file.listFiles();
            if (childFileList != null) {
                for (File childFile : childFileList) {
                    if (childFile.isDirectory()) {
                        deleteDir(childFile.getAbsolutePath());

                    } else {
                        childFile.delete();
                    }
                }
                file.delete();
            }
        }
    }

    /**
     * 언어 설정
     */
    private void setDefaultLanguage() {

        // 기본 언어 설정
        if ("".equals(PreferenceUtil.getLanguage(context))) {

            Configuration conf = getResources().getConfiguration();

            if (Locale.ENGLISH.equals(conf.locale)) {
                //영어
                strLanguage = Language.ENG;
            } else if (Locale.KOREA.equals(conf.locale) || Locale.KOREAN.equals(conf.locale)) {
                //한국어
                strLanguage = Language.KOR;
            } else if ("es_ES".equals(conf.locale.toString()) || "es".equals(conf.locale.toString())) {
                //스페인어
                strLanguage = Language.SPN;
            } else if (Locale.JAPAN.equals(conf.locale) || Locale.JAPANESE.equals(conf.locale)) {
                //일본
                strLanguage = Language.JPN;
            } else if (Locale.GERMANY.equals(conf.locale) || Locale.GERMAN.equals(conf.locale)) {
                //독일어
                strLanguage = Language.GER;
            } else if ("ru_RU".equals(conf.locale.toString()) || "ru".equals(conf.locale.toString())) {
                //러시아어
                strLanguage = Language.RUS;
            } else if (Locale.FRANCE.equals(conf.locale) || Locale.FRENCH.equals(conf.locale)) {
                //프랑스어
                strLanguage = Language.FRN;
            } else if ("cs_CZ".equals(conf.locale.toString()) || "cs".equals(conf.locale.toString())) {
                //체코어
                strLanguage = Language.CZE;
            } else if ("pt_PT".equals(conf.locale.toString()) || "pt".equals(conf.locale.toString())) {
                //포르투갈어
                strLanguage = Language.POR;
            } else if (Locale.ITALY.equals(conf.locale) || Locale.ITALIAN.equals(conf.locale)) {
                //이탈리아어
                strLanguage = Language.ITA;
            } else if ("hi_IN".equals(conf.locale.toString()) || "hi".equals(conf.locale.toString())) {
                //인도어(힌두어)
                strLanguage = Language.IND;
            } else if (Locale.TAIWAN.equals(conf.locale) || Locale.TRADITIONAL_CHINESE.equals(conf.locale)) {
                //대만(중문)
                strLanguage = Language.TPE;
            } else if (Locale.CHINA.equals(conf.locale) || Locale.CHINESE.equals(conf.locale)
                       || Locale.PRC.equals(conf.locale)
                       || Locale.SIMPLIFIED_CHINESE.equals(conf.locale)) {
                //중국(중문)
                strLanguage = Language.CHN;
            } else {
                //기본 - 영어
                strLanguage = Language.ENG;
            }

            PreferenceUtil.setLanguage(context, strLanguage);

        } else {

            strLanguage = PreferenceUtil.getLanguage(context);
        }
        ManagerUtil.setLocale(strLanguage, context);
    }

    /**
     * Google Play Service 확인
     *
     * @return
     */
    private boolean checkGooglePlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * 버전체크 완료 후 로그인 로그인
     */
    private void doLogin() {
        if (BPDiaryApplication.isNetworkState(context)) {
            //Google Play Services 확인
            if (checkGooglePlayServices()) {
                regid = PreferenceUtil.getGcmToken(context);
                if (regid.isEmpty() || regid.length() < 1) {
                    registerInBackground();
                } else {
                    setCheckLoginUser();
                }
            }
        } else {
            //다이얼로그 호출 후 앱 종료
            DefaultOneButtonDialog defaultOneButtonDialog = new DefaultOneButtonDialog(context,
                                                                                       "",
                                                                                       getString(R.string.common_error_comment),
                                                                                       getString(R.string.common_txt_confirm),
                                                                                       new IDefaultOneButtonDialog() {

                                                                                           @Override
                                                                                           public void onConfirm() {
                                                                                               finish();
                                                                                           }
                                                                                       });
            defaultOneButtonDialog.show();
        }
    }

    /**
     * Store 이동
     *
     * @param strStoreUrl
     */
    private void moveUpdateStore(String strStoreUrl) {
        Intent updateIntent = new Intent(Intent.ACTION_VIEW);
        updateIntent.setData(Uri.parse(strStoreUrl));
        startActivity(updateIntent);
        finish();
    }

    /**
     * Dialog 업데이트
     *
     * @param strAppVersionType
     * @param strStoreUrl
     * @param strMessage
     */
    private void updateDailog(String strAppVersionType, final String strStoreUrl, String strMessage) {

        AlertDialog.Builder alertDlg = new AlertDialog.Builder(context);
        alertDlg.setMessage(strMessage);
        alertDlg.setCancelable(false);

        //업데이트 (필수)
        alertDlg.setPositiveButton(getResources().getString(R.string.intro_btn_update),
                                   new DialogInterface.OnClickListener() {

                                       @Override
                                       public void onClick(DialogInterface dialog, int id) {
                                           if (!ManagerUtil.isClicking()) {
                                               moveUpdateStore(strStoreUrl);
                                           }
                                       }
                                   });

        if (AppUpdateVersionType.VERSION_TYPE_O.equals(strAppVersionType)) {

            //업데이트 (선택)
            alertDlg.setNegativeButton(getResources().getString(R.string.intro_btn_after),
                                       new DialogInterface.OnClickListener() {

                                           @Override
                                           public void onClick(DialogInterface dialog, int id) {
                                               if (!ManagerUtil.isClicking()) {
                                                   doLogin();
                                               }
                                           }
                                       });
        }

        if (!isBackKey) {
            alertDlg.show();
        }
    }

    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's shared preferences.
     */
    private void registerInBackground() {

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {

                String msg = "";
                try {
                    GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
                    regid = gcm.register(AppConfig.GCM_SENDER_ID);

                    msg = "Device registered, registration ID=" + regid;

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    PreferenceUtil.setGcmToken(context, regid);

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                    ex.printStackTrace();
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                setCheckLoginUser();
            }
        }.execute(null, null, null);
    }

    /**
     * 로그인 여부를 체크해서 자동로그인 또는 랜딩페이지로 이동
     */
    private void setCheckLoginUser() {
        //로그인이 돼있으면 로그인 로직타서 result로 main으로 보냄
        if (PreferenceUtil.getIsLogin(context)) {
            Intent intent = new Intent(context, LoginLogicActivity.class);
            startActivityForResult(intent, CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_AUTO);
            overridePendingTransition(0, 0);
        } else {
            //첫 사용자 등록
            //2016/12/20 추가
            Intent intent = new Intent(context, OnboardActivity.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        }
    }

    /**
     * App 업데이트 확인 Async
     */
    private class AppUpdateAsync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();
            try {
                data.put(RequestParamName.APP_VERSION, ManagerUtil.getAppVersion(context));
                data.put(RequestParamName.APP_MARKET, AppConfig.MARTKET_CD);

                resultJSON = introService.searchVersion(data);

            } catch (Exception e) {
                //LogUtil.e(context, e.getLocalizedMessage());
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {
            hideLodingProgress();
            try {

                if (resultJSON.has(ResponseParamName.RESULT)) {

                    if (ResponseResult.RESULT_TRUE.equals(resultJSON.get(ResponseParamName.RESULT).toString())) {

                        String strAppVersionType = resultJSON.get(ResponseParamName.APP_VERSION_TYPE).toString();

                        if (AppUpdateVersionType.VERSION_TYPE_N.equals(strAppVersionType)) {
                            //최신버전
                            doLogin();

                        } else {
                            //업데이트 (선택 & 필수)
                            updateDailog(strAppVersionType,
                                         resultJSON.get(ResponseParamName.APP_MARKET_URL).toString(),
                                         resultJSON.get(ResponseParamName.MESSAGE).toString());
                        }
                    } else {
                        AlertDialog.Builder alertDlg = new AlertDialog.Builder(context);
                        alertDlg.setTitle(getString(R.string.common_txt_noti));
                        alertDlg.setMessage((String)resultJSON.get("message"));
                        alertDlg.setPositiveButton(getString(R.string.common_txt_confirm),
                                                   new DialogInterface.OnClickListener() {

                                                       @Override
                                                       public void onClick(DialogInterface dialog, int which) {
                                                           if (!ManagerUtil.isClicking()) {
                                                               finish();
                                                           }
                                                       }
                                                   });
                        if (!isFinishing()) {
                            alertDlg.show();
                        }
                    }
                } else {
                    doLogin();
                }

            } catch (Exception e) {
                //LogUtil.e(context, e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * 혈압 데이터 마이그레이션 Sync
     */
    private class MigrationDBDataSync extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected Void doInBackground(Void... param) {

            try {
                bpService.migrationDBData();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            hideLodingProgress();

            PreferenceUtil.setIsDBMigration(context, true);

        }
    }
}
