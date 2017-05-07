package kr.co.openit.bpdiary.activity.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.bp.BPGraphActivity;
import kr.co.openit.bpdiary.activity.bp.BPMeasureActivity;
import kr.co.openit.bpdiary.activity.bp.BPMeasureGuideActivity;
import kr.co.openit.bpdiary.activity.bp.BPMemoActivity;
import kr.co.openit.bpdiary.activity.bp.BPStandardActivity;
import kr.co.openit.bpdiary.activity.bp.InputBPActivity;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.activity.glucose.GlucoseGraphActivity;
import kr.co.openit.bpdiary.activity.glucose.GlucoseMemoActivity;
import kr.co.openit.bpdiary.activity.glucose.GlucoseStandardActivity;
import kr.co.openit.bpdiary.activity.glucose.InputGlucoseActivity;
import kr.co.openit.bpdiary.activity.intro.SignUpActivity;
import kr.co.openit.bpdiary.activity.report.ReportMainActivity;
import kr.co.openit.bpdiary.activity.report.view.ReportAvgView;
import kr.co.openit.bpdiary.activity.report.view.ReportVarView;
import kr.co.openit.bpdiary.activity.setting.AlarmActivity;
import kr.co.openit.bpdiary.activity.setting.AlarmUtils;
import kr.co.openit.bpdiary.activity.setting.ConnectDeviceActivity;
import kr.co.openit.bpdiary.activity.setting.DataSendReceiveActivity;
import kr.co.openit.bpdiary.activity.setting.LanguageActivity;
import kr.co.openit.bpdiary.activity.setting.LegalContentActivity;
import kr.co.openit.bpdiary.activity.setting.NoticeActivity;
import kr.co.openit.bpdiary.activity.setting.OnlineShopActivity;
import kr.co.openit.bpdiary.activity.setting.SettingGoalActivity;
import kr.co.openit.bpdiary.activity.setting.SettingInsideActivity;
import kr.co.openit.bpdiary.activity.setting.SettingProfileActivity;
import kr.co.openit.bpdiary.activity.weight.InputWeightActivity;
import kr.co.openit.bpdiary.activity.weight.WeightGraphActivity;
import kr.co.openit.bpdiary.activity.weight.WeightMeasureGuideActivity;
import kr.co.openit.bpdiary.activity.weight.WeightMemoActivity;
import kr.co.openit.bpdiary.activity.weight.WeightStandardActivity;
import kr.co.openit.bpdiary.common.constants.BleConstants;
import kr.co.openit.bpdiary.common.constants.CommonConstants;
import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.constants.ReturnCode;
import kr.co.openit.bpdiary.common.controller.BlueToothBLEService;
import kr.co.openit.bpdiary.common.controller.BlueToothBPBLEDeviceInterface;
import kr.co.openit.bpdiary.common.controller.BlueToothWSBLEDeviceInterface;
import kr.co.openit.bpdiary.customview.CustomViewPager;
import kr.co.openit.bpdiary.databinding.ActivityMainBinding;
import kr.co.openit.bpdiary.dialog.CustomProgressDialog;
import kr.co.openit.bpdiary.dialog.DefaultAdsDialog;
import kr.co.openit.bpdiary.dialog.DefaultChoiceDialog;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.dialog.PairingListCustomDialog;
import kr.co.openit.bpdiary.iab.utils.IabHelper;
import kr.co.openit.bpdiary.iab.utils.IabResult;
import kr.co.openit.bpdiary.iab.utils.Inventory;
import kr.co.openit.bpdiary.iab.utils.Purchase;
import kr.co.openit.bpdiary.interfaces.IChoiceDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultAdsDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.PairingListInterface;
import kr.co.openit.bpdiary.model.EventInfoData;
import kr.co.openit.bpdiary.model.MainBpModel;
import kr.co.openit.bpdiary.model.MainGlucoseModel;
import kr.co.openit.bpdiary.model.MainSettingModel;
import kr.co.openit.bpdiary.model.MainViewModel;
import kr.co.openit.bpdiary.model.MainWsModel;
import kr.co.openit.bpdiary.services.BloodPressureService;
import kr.co.openit.bpdiary.services.GlucoseService;
import kr.co.openit.bpdiary.services.PaymentService;
import kr.co.openit.bpdiary.services.ReportService;
import kr.co.openit.bpdiary.services.WeighingScaleService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.DateUtil;
import kr.co.openit.bpdiary.utils.HealthcareUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Created by hslee on 2016-12-21.
 */

public class MainActivity extends NonMeasureActivity {

    private ActivityMainBinding binding;

    private MainViewModel mainViewModel;

    private MainBpModel mainBpModel;

    private MainWsModel mainWsModel;

    private MainGlucoseModel mainGlucoseModel;

    private MainSettingModel mainSettingModel;

    private Context context;

    private ProgressDialog mConnectionProgressDialog = null;

    private CustomProgressDialog mCustomProgressDialog = null;

    private MyGridViewAdpater adapter;

    private ReportPageAdapter pageAdapter;

    private boolean isGetResult = false;

    private int reportCount = 2;

    private int reportViewNum = 2000;

    private CustomViewPager mCustomViewPager;

    /**
     * BloodPressureService
     */
    private BloodPressureService bpService;

    /**
     * GlucoseService
     */
    private GlucoseService glucoseService;

    /**
     * WeighingScaleService
     */
    private WeighingScaleService wsService;

    /**
     * ReportService
     */
    private ReportService reportService;

    /**
     * 혈압 측정 결과 DB에서 조회
     */
    private SearchBloodPressureDBSync sbpDBSync;

    /**
     * 체중 측정 결과 DB에서 조회
     */
    private SearchWeighingScaleDBSync swsDBSync;

    /**
     * 혈당 측정 결과 DB에서 조회
     */
    private SearchGlucoseDBSync sGlucoseDBSync;

    /**
     * 혈압 데이터 Map
     */
    private Map<String, String> responesBpMap;

    /**
     * 체중 데이터 Map
     */
    private Map<String, String> responesWsMap;

    /**
     * 혈당 데이터 Map
     */
    private Map<String, String> responesGlucoseMap;

    /**
     * 리포트 데이터 Map
     */
    private JSONObject responesReportMap;

    /**
     * 최근 혈압 데이터 유무
     */
    private boolean isNewBpData = true;

    /**
     * 최근 체중 데이터 유무
     */
    private boolean isNewWsData = true;

    private String strWeight;

    /**
     * 최근 혈당 데이터 유무
     */

    private boolean isNewGlucoseData = true;

    private BluetoothAdapter bluetoothAdapter;

    private BlueToothBLEService bleService;

    private Set<BluetoothDevice> pairedDevices;

    private String email;

    private String signature;

    private IInAppBillingService mService;

    private IabHelper mHelper;

    private static final String ITEM_REMOVE_ADS = "ads";

    private AdView adView;

    private boolean adsFlag = true;

    private static final String PUBLIC_KEY_BASE_64 =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgeGTRJtTszvEOF2s+mDeyP9NgtjgUFZydq717GWDM6Kw1nHtPi7VSzIf9l2DLY+BCzVNMmTKrYaFXfNJyOJ1zHugClBqvW0q6ljRYWpZMfewnraadxAKrSfffwz1JDYI1iSINBkWJYZrn3EOeWXvOwcynpQsmvcW22U8TgezTkq/ND7gFtyRtntFSgbhJag0Z+lZapav9WYqoykFztA7BV8a8B+SyVvnMw38Sk6Ezqd6IippPWT1X5LoqokIZUyPUwNMU1X9V/S8lArV3o4Tb6pRYFNK9Jou5IU2PGi1oswZzs/7qCXu1nNzRSVJH+o3AzSEswLAWnwSMl7Lrp7GXQIDAQAB";

    /**
     * 검색 날짜
     */
    private String searchDay = "7";

    private boolean isBackPressed = false;

    private LinearLayout llAds;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1001) {
            // Pass on the activity result to the helper for handling
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
                // not handled, so handle it ourselves (here's where you'd
                // perform any handling of activity results not related to
                // in-app
                // billing...
                super.onActivityResult(requestCode, resultCode, data);
            } else {
            }
        } else if (requestCode == ManagerConstants.ActivityResultCode.BP_INPUT_MEASURE) {
            //혈압 메인 Fragment에서 수동입력 이동 후 돌아왔을 때
            if (resultCode == Activity.RESULT_OK) {
                //수동입력 완료
                isNewBpData = false;
                isGetResult = true;
                sbpDBSync = new SearchBloodPressureDBSync();
                sbpDBSync.execute();
            } else {
                //nothing
            }
        } else if (requestCode == ManagerConstants.ActivityResultCode.BP_INPUT_MEMO) {
            if (resultCode == Activity.RESULT_OK) {
                //메모입력 완료
                sbpDBSync = new SearchBloodPressureDBSync();
                sbpDBSync.execute();
            } else {
                //nothing
            }
        } else if (requestCode == ManagerConstants.ActivityResultCode.BP_VIEW_GRAPH) {
            isNewBpData = true;
            sbpDBSync = new SearchBloodPressureDBSync();
            sbpDBSync.execute();
        } else if (requestCode == ManagerConstants.ActivityResultCode.WS_INPUT_MEASURE) {
            if (resultCode == Activity.RESULT_OK) {
                //수동입력 완료
                isNewWsData = false;
                isGetResult = true;
                swsDBSync = new SearchWeighingScaleDBSync();
                swsDBSync.execute();
            }
        } else if (requestCode == ManagerConstants.ActivityResultCode.WS_INPUT_MEMO) {
            if (resultCode == Activity.RESULT_OK) {
                //메모입력 완료
                swsDBSync = new SearchWeighingScaleDBSync();
                swsDBSync.execute();
            }
        } else if (requestCode == ManagerConstants.ActivityResultCode.WS_VIEW_GRAPH) {
            isNewWsData = true;
            swsDBSync = new SearchWeighingScaleDBSync();
            swsDBSync.execute();
        } else if (requestCode == ManagerConstants.ActivityResultCode.GLUCOSE_VIEW_GRAPH) {
            isNewGlucoseData = true;
            sGlucoseDBSync = new SearchGlucoseDBSync();
            sGlucoseDBSync.execute();
        } else if (requestCode == ManagerConstants.ActivityResultCode.GLUCOSE_INPUT_MEMO) {
            if (resultCode == Activity.RESULT_OK) {
                //메모입력 완료
                sGlucoseDBSync = new SearchGlucoseDBSync();
                sGlucoseDBSync.execute();
            }
        } else if (requestCode == ManagerConstants.ActivityResultCode.GLUCOSE_INPUT_MEASURE) {
            if (resultCode == Activity.RESULT_OK) {
                //수동입력 완료
                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(MainActivity.this))) {
                    binding.layoutGlucose.tvGlucoseUnit.setText(ManagerConstants.Unit.MGDL);
                } else {
                    binding.layoutGlucose.tvGlucoseUnit.setText(ManagerConstants.Unit.MMOL);
                }
                isNewGlucoseData = false;
                isGetResult = true;
                sGlucoseDBSync = new SearchGlucoseDBSync();
                sGlucoseDBSync.execute();
            }
        } else if (requestCode == CommonConstants.RequestCode.ACTIVITY_REQUEST_REPORT_MENU) {
            if (resultCode == RESULT_OK && data != null) {
                reportViewNum = data.getIntExtra(ManagerConstants.RequestParamName.REPORT_VIEW_NUM, reportViewNum);
                mCustomViewPager.setCurrentItem(reportViewNum);
            }
        } else if (requestCode == CommonConstants.RequestCode.ACTIVITY_REQUEST_REPORT_BP_INPUT
                || requestCode == CommonConstants.RequestCode.ACTIVITY_REQUEST_REPORT_WS_INPUT
                || requestCode == CommonConstants.RequestCode.ACTIVITY_REQUEST_REPORT_GLUCOSE_INPUT) {
            if (resultCode == RESULT_OK) {
                if ("7".equals(searchDay)) {
                    binding.layoutReport.llSevenDay.performClick();
                } else if ("30".equals(searchDay)) {
                    binding.layoutReport.llThirtyDay.performClick();
                } else if ("60".equals(searchDay)) {
                    binding.layoutReport.llSixtyDay.performClick();
                } else if ("".equals(searchDay)) {
                    binding.layoutReport.llAllDay.performClick();
                }
            }

        } else if (requestCode == ManagerConstants.ActivityResultCode.SETTING_PROFILE) {
            mainViewModel.setUseGlucose(PreferenceUtil.getUsingBloodGlucose(MainActivity.this));
            binding.setMainView(mainViewModel);
        } else if (requestCode == ManagerConstants.ActivityResultCode.LANGUAGE) {
            if (resultCode == RESULT_OK) {
                Intent intent = getIntent();
                intent.putExtra("language", "language");
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
                if (mCustomProgressDialog != null && mCustomProgressDialog.isShowing()) {
                    mCustomProgressDialog.dismiss();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConn != null) {
            unbindService(mServiceConn);
        }

        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        if (bleService != null) {
            bleService.stopBluetoothBLEService();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startBLEService();
        }
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBTDevices();

        binding.layoutSetting.tvName.setText(PreferenceUtil.getDecFirstName(MainActivity.this) + " "
                + PreferenceUtil.getDecLastName(MainActivity.this));
        if (PreferenceUtil.getLanguage(MainActivity.this).equals(ManagerConstants.Language.KOR)) {
            binding.layoutSetting.rlOnlineShop.setVisibility(View.VISIBLE);
            binding.layoutSetting.ivOnlineLine.setVisibility(View.VISIBLE);
        } else {
            binding.layoutSetting.rlOnlineShop.setVisibility(View.GONE);
            binding.layoutSetting.ivOnlineLine.setVisibility(View.GONE);

        }
        SettingDisplayListener();

        if (binding.llNaviReport.isSelected()) {
            searchReportNetworkCheck();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        context = MainActivity.this;

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager mgr = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mgr.getDefaultDisplay().getMetrics(metrics);
        /**
         * 광고
         */
        llAds = (LinearLayout) findViewById(R.id.ll_ads);
        adView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                .build();
        adView.loadAd(adRequest);
        if (PreferenceUtil.getIsPayment(MainActivity.this)) {
            llAds.setVisibility(View.GONE);
            binding.layoutSetting.rlNoAds.setVisibility(View.GONE);
            binding.layoutSetting.ivNoAds.setVisibility(View.GONE);
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) binding.llNavigation.getLayoutParams();
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            llAds.setVisibility(View.VISIBLE);
            binding.layoutSetting.rlNoAds.setVisibility(View.VISIBLE);
            binding.layoutSetting.ivNoAds.setVisibility(View.VISIBLE);
        }

        setBroadcastReceiver();

        /**
         * blood pressure service
         */
        bpService = new BloodPressureService(context);

        /**
         * glucoseService
         */
        glucoseService = new GlucoseService(context);

        /**
         * weighing scale service
         */
        wsService = new WeighingScaleService(context);

        /**
         * reportService service
         */
        reportService = new ReportService(context);

        mainViewModel = new MainViewModel();
        mainBpModel = new MainBpModel();
        mainWsModel = new MainWsModel();
        mainGlucoseModel = new MainGlucoseModel();
        mainSettingModel = new MainSettingModel();

        adapter = new MyGridViewAdpater();

        adapter.add(new ExtendData(R.drawable.ic_ex_menu_set_bluetooth,
                getResources().getString(R.string.setting_connect_device)));
        adapter.add(new ExtendData(R.drawable.ic_ex_menu_set_target,
                getResources().getString(R.string.setting_activity_goal)));
        adapter.add(new ExtendData(R.drawable.ic_ex_menu_set_alarm,
                getResources().getString(R.string.setting_activity_setting_alarm)));
        adapter.add(new ExtendData(R.drawable.ic_ex_menu_lang,
                getResources().getString(R.string.setting_activity_language)));
        adapter.add(new ExtendData(R.drawable.ic_ex_menu_set_data,
                getResources().getString(R.string.setting_data_send_recieve)));
        adapter.add(new ExtendData(R.drawable.ic_ex_menu_set_setting,
                getResources().getString(R.string.common_txt_setting)));

        binding.layoutSetting.gvBtnMain.setAdapter(adapter);

        mainViewModel.setScreenType(1);
        mainViewModel.setUseGlucose(PreferenceUtil.getUsingBloodGlucose(MainActivity.this));
        binding.llNaviBp.setSelected(true);
        binding.setMainView(mainViewModel);

        if (BPDiaryApplication.isNetworkState(context)) {
            new SyncDataAsync().execute();
        } else {
            sbpDBSync = new SearchBloodPressureDBSync();
            sbpDBSync.execute();
        }

        if ("WS".equals(getIntent().getStringExtra("checkType"))) {
            gotoNavigationWS();
        }

        if ("language".equals(getIntent().getStringExtra("language"))) {
            gotoNavigationSetting();
        }

        //결제 세팅
        Intent intent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
        intent.setPackage(getPackageName());

        bindService(intent, mServiceConn, Context.BIND_AUTO_CREATE);

        mHelper = new IabHelper(context, PUBLIC_KEY_BASE_64);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

            @Override
            public void onIabSetupFinished(IabResult result) {

                if (result.isSuccess()) {

                    //String activityName = getActivity() + "";
                    mHelper.queryInventoryAsync(mGotInventoryListener);

                } else {
                    //nothing
                }
            }
        });

        mCustomViewPager = (CustomViewPager) findViewById(R.id.vp_slide_view);
        mCustomViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                reportViewNum = position;
                int realPos = position % reportCount;
                if (realPos == 0) {
                    binding.layoutReport.tvReportTitle.setText(getResources().getString(R.string.report_menu_variation));
                } else if (realPos == 1) {
                    binding.layoutReport.tvReportTitle.setText(getResources().getString(R.string.report_menu_average_value));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //네비게이션 관련 리스너
        NavigationListener();
        //혈압 관련 리스너
        BpClickListener();
        //혈당 관련 리스너
        GlucoseClickListener();
        //체중 관련 리스너
        WsClickListener();
        //레포트 관련 리스너
        ReportClickListener();
        //셋팅화면 관련 리스너
        SettingDisplayListener();

        if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(MainActivity.this))) {
            binding.layoutGlucose.tvGlucoseUnit.setText(ManagerConstants.Unit.MGDL);
        } else {
            binding.layoutGlucose.tvGlucoseUnit.setText(ManagerConstants.Unit.MMOL);
        }
        setSettingAlarm();
    }

    /**
     * 셋팅화면 관련 리스너
     */
    private void SettingDisplayListener() {
        //셋팅 프로필
        binding.layoutSetting.rlProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(context, SettingProfileActivity.class);
                    startActivityForResult(intent, ManagerConstants.ActivityResultCode.SETTING_PROFILE);
                    overridePendingTransition(0, 0);
                }
            }
        });

        //rlProfile 안의 뷰(추후 이동)
        if (PreferenceUtil.getLoginType(MainActivity.this).equals(ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK)) {
            binding.layoutSetting.ivLoginType.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_ex_menu_sns_facebook));
        } else if (PreferenceUtil.getLoginType(MainActivity.this)
                .equals(ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE)) {
            binding.layoutSetting.ivLoginType.setBackgroundDrawable(getResources().getDrawable(R.drawable.google));
        } else {
            binding.layoutSetting.ivLoginType.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_ex_menu_sns_email));
        }
        binding.layoutSetting.tvEmail.setText(PreferenceUtil.getDecEmail(MainActivity.this));

        //셋팅 광고 제거
        binding.layoutSetting.rlNoAds.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    if (mHelper != null) {
                        mHelper.flagEndAsync();
                    }
                    AnalyticsUtil.sendEvent(MainActivity.this, "광고제거", "Event", "3_M 광고제거");
                    Buy(ITEM_REMOVE_ADS);
                }
            }
        });

        //셋팅 법적내용
        binding.layoutSetting.rlRaw.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(context, LegalContentActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });

        //셋팅 온라인샵
        binding.layoutSetting.rlOnlineShop.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(context, OnlineShopActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });

        //셋팅 버튼
        binding.layoutSetting.gvBtnMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent;
                    switch (position) {
                        case 0: //연동기기
                            intent = new Intent(context, ConnectDeviceActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            break;
                        case 1: //목표설정
                            intent = new Intent(context, SettingGoalActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            break;

                        case 2: //목표설정
                            intent = new Intent(context, AlarmActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            break;

                        case 3: //알림 설정
                            intent = new Intent(context, LanguageActivity.class);
                            startActivityForResult(intent, ManagerConstants.ActivityResultCode.LANGUAGE);
                            overridePendingTransition(0, 0);
                            break;

                        case 4: //데이터 보내기/받기
                            intent = new Intent(context, DataSendReceiveActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            break;

                        case 5: //설정
                            intent = new Intent(context, SettingInsideActivity.class);
                            startActivity(intent);
                            overridePendingTransition(0, 0);
                            break;
                    }
                }
            }
        });
    }

    /**
     * 레포트 관련 리스너
     */
    private void ReportClickListener() {

        binding.layoutReport.ivReportMenu.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(context, ReportMainActivity.class);
                    intent.putExtra(ManagerConstants.RequestParamName.REPORT_VIEW_NUM, reportViewNum);
                    startActivityForResult(intent, CommonConstants.RequestCode.ACTIVITY_REQUEST_REPORT_MENU);
                    overridePendingTransition(0, 0);
                }
            }
        });

        //
        binding.layoutReport.llReportBefore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    reportViewNum -= 1;
                    mCustomViewPager.setCurrentItem(reportViewNum);
                }
            }
        });

        binding.layoutReport.llReportNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    reportViewNum += 1;
                    mCustomViewPager.setCurrentItem(reportViewNum);
                }
            }
        });

        binding.layoutReport.llSevenDay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    AnalyticsUtil.sendEvent(MainActivity.this, "레포트", "Event", "3_레포트 7일");
                    searchDay = "7";
                    binding.layoutReport.tvLately.setVisibility(View.VISIBLE);
                    binding.layoutReport.tvSearchDay.setText(R.string.common_txt_seven_day);
                    binding.layoutReport.ivSevenDayLine.setSelected(true);
                    binding.layoutReport.ivSevenDayLine.bringToFront();
                    binding.layoutReport.ivThirtyDayLine.setSelected(false);
                    binding.layoutReport.ivSixtyDayLine.setSelected(false);
                    binding.layoutReport.ivAllLine.setSelected(false);

                    binding.layoutReport.tvSevenDay.setSelected(true);
                    binding.layoutReport.tvThiryDay.setSelected(false);
                    binding.layoutReport.tvSixtyDay.setSelected(false);
                    binding.layoutReport.tvAllDay.setSelected(false);

                    searchReportNetworkCheck();
                }
            }
        });

        binding.layoutReport.llThirtyDay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    AnalyticsUtil.sendEvent(MainActivity.this, "레포트", "Event", "3_레포트 30일");
                    searchDay = "30";
                    binding.layoutReport.tvLately.setVisibility(View.VISIBLE);
                    binding.layoutReport.tvSearchDay.setText(R.string.common_txt_thirty_day);
                    binding.layoutReport.ivSevenDayLine.setSelected(false);
                    binding.layoutReport.ivThirtyDayLine.setSelected(true);
                    binding.layoutReport.ivSixtyDayLine.setSelected(false);
                    binding.layoutReport.ivAllLine.setSelected(false);

                    binding.layoutReport.tvSevenDay.setSelected(false);
                    binding.layoutReport.tvThiryDay.setSelected(true);
                    binding.layoutReport.tvSixtyDay.setSelected(false);
                    binding.layoutReport.tvAllDay.setSelected(false);

                    searchReportNetworkCheck();
                }
            }
        });

        binding.layoutReport.llSixtyDay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    AnalyticsUtil.sendEvent(MainActivity.this, "레포트", "Event", "3_레포트 60일");
                    searchDay = "60";
                    binding.layoutReport.tvLately.setVisibility(View.VISIBLE);
                    binding.layoutReport.tvSearchDay.setText(R.string.common_txt_sixty_day);
                    binding.layoutReport.ivSevenDayLine.setSelected(false);
                    binding.layoutReport.ivThirtyDayLine.setSelected(false);
                    binding.layoutReport.ivSixtyDayLine.setSelected(true);
                    binding.layoutReport.ivAllLine.setSelected(false);

                    binding.layoutReport.tvSevenDay.setSelected(false);
                    binding.layoutReport.tvThiryDay.setSelected(false);
                    binding.layoutReport.tvSixtyDay.setSelected(true);
                    binding.layoutReport.tvAllDay.setSelected(false);

                    searchReportNetworkCheck();
                }
            }
        });

        binding.layoutReport.llAllDay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    AnalyticsUtil.sendEvent(MainActivity.this, "레포트", "Event", "3_레포트 전체");
                    searchDay = "";
                    binding.layoutReport.tvLately.setVisibility(View.GONE);
                    binding.layoutReport.tvSearchDay.setText(R.string.common_txt_all);
                    binding.layoutReport.ivSevenDayLine.setSelected(false);
                    binding.layoutReport.ivThirtyDayLine.setSelected(false);
                    binding.layoutReport.ivSixtyDayLine.setSelected(false);
                    binding.layoutReport.ivAllLine.setSelected(true);

                    binding.layoutReport.tvSevenDay.setSelected(false);
                    binding.layoutReport.tvThiryDay.setSelected(false);
                    binding.layoutReport.tvSixtyDay.setSelected(false);
                    binding.layoutReport.tvAllDay.setSelected(true);

                    searchReportNetworkCheck();
                }
            }
        });

    }

    //혈압 네비게이션 이벤트
    private void gotoNavigationBp() {

        if (!binding.llNaviBp.isSelected()) {
            binding.llNaviBp.setSelected(true);
            binding.llNaviGlucose.setSelected(false);
            binding.llNaviWeight.setSelected(false);
            binding.llNaviReport.setSelected(false);
            binding.llNaviSetting.setSelected(false);
            mainViewModel.setScreenType(1);
            binding.setMainView(mainViewModel);
            binding.layoutBp.setMainBp(mainBpModel);

            sbpDBSync = new SearchBloodPressureDBSync();
            sbpDBSync.execute();

            checkBTDevices();
        } else {

        }

    }

    //혈당 네비게이션 이벤트
    private void gotoNavigationGlucose() {
        if (!binding.llNaviGlucose.isSelected()) {
            binding.llNaviBp.setSelected(false);
            binding.llNaviGlucose.setSelected(true);
            binding.llNaviWeight.setSelected(false);
            binding.llNaviReport.setSelected(false);
            binding.llNaviSetting.setSelected(false);
            mainViewModel.setScreenType(2);
            binding.setMainView(mainViewModel);

            sGlucoseDBSync = new SearchGlucoseDBSync();
            sGlucoseDBSync.execute();
        } else {

        }

    }

    //체중 네비게이션 이벤트
    private void gotoNavigationWS() {
        if (!binding.llNaviWeight.isSelected()) {
            binding.llNaviBp.setSelected(false);
            binding.llNaviGlucose.setSelected(false);
            binding.llNaviWeight.setSelected(true);
            binding.llNaviReport.setSelected(false);
            binding.llNaviSetting.setSelected(false);
            mainViewModel.setScreenType(3);
            binding.setMainView(mainViewModel);
            binding.layoutWs.setMainWs(mainWsModel);

            strWeight = null;

            swsDBSync = new SearchWeighingScaleDBSync();
            swsDBSync.execute();
        } else {

        }

    }

    //레포트 네비게이션 이벤트
    private void gotoNavigationReport() {

        if (!binding.llNaviReport.isSelected()) {
            binding.llNaviBp.setSelected(false);
            binding.llNaviGlucose.setSelected(false);
            binding.llNaviWeight.setSelected(false);
            binding.llNaviReport.setSelected(true);
            binding.llNaviSetting.setSelected(false);

            binding.layoutReport.ivSevenDayLine.setSelected(false);
            binding.layoutReport.ivThirtyDayLine.setSelected(true);
            binding.layoutReport.ivSixtyDayLine.setSelected(false);
            binding.layoutReport.ivAllLine.setSelected(false);

            binding.layoutReport.tvSevenDay.setSelected(false);
            binding.layoutReport.tvThiryDay.setSelected(true);
            binding.layoutReport.tvSixtyDay.setSelected(false);
            binding.layoutReport.tvAllDay.setSelected(false);

            //분포도는 한국어만 지원
            if (ManagerConstants.Language.KOR.equals(PreferenceUtil.getLanguage(context))) {
                reportCount = 2;
            } else {
                reportCount = 2;
            }

            searchDay = "30";
            binding.layoutReport.tvSearchDay.setText(R.string.common_txt_thirty_day);
            searchReportNetworkCheck();

            mainViewModel.setScreenType(4);
            binding.setMainView(mainViewModel);
        }
    }

    private void searchReportNetworkCheck() {
        if (BPDiaryApplication.isNetworkState(context)) {
            new SearchReport().execute(searchDay);
        } else {
            pageAdapter = new ReportPageAdapter();
            mCustomViewPager.setAdapter(pageAdapter);
            mCustomViewPager.setCurrentItem(reportViewNum);
        }
    }

    //확장메뉴 네비게이션 이벤트
    private void gotoNavigationSetting() {

        if (!binding.llNaviSetting.isSelected()) {
            binding.llNaviBp.setSelected(false);
            binding.llNaviGlucose.setSelected(false);
            binding.llNaviWeight.setSelected(false);
            binding.llNaviReport.setSelected(false);
            binding.llNaviSetting.setSelected(true);
            mainViewModel.setScreenType(5);
            binding.setMainView(mainViewModel);
            binding.layoutSetting.setMainSetting(mainSettingModel);
        } else {

        }

    }

    /**
     * 네비게이션 관련 리스너
     */
    private void NavigationListener() {
        //네비게이션 혈압
        binding.llNaviBp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    gotoNavigationBp();
                }
            }
        });

        //네비게이션 혈당
        binding.llNaviGlucose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    gotoNavigationGlucose();
                }
            }
        });

        //네비게이션 체중
        binding.llNaviWeight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    gotoNavigationWS();
                }
            }
        });

        //네비게이션 레포트
        binding.llNaviReport.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    AnalyticsUtil.sendScene(MainActivity.this, "3_레포트 메뉴");
                    gotoNavigationReport();
                }
            }
        });

        //네비게이션 세팅
        binding.llNaviSetting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    AnalyticsUtil.sendScene(MainActivity.this, "3_M 메인");
                    gotoNavigationSetting();
                }
            }
        });
    }

    //블루투스 장비 페어링 체크
    public void checkBTDevices() {
        if (bluetoothAdapter != null) {
            int cnt = 0;
            pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_BP_UA_651_BLE)) {
                        mainBpModel.setBtConnect(false);
                        mainBpModel.set651BtConnect(true);
                        cnt++;
                    } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_BP_UA_767PBT_C)) {
                        mainBpModel.setBtConnect(true);
                        mainBpModel.set651BtConnect(false);
                        cnt++;
                    } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.AnD_UA_851PBT_C)) {
                        mainBpModel.setBtConnect(true);
                        mainBpModel.set651BtConnect(false);
                        cnt++;
                    } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.HEM_7081_IT)) {
                        mainBpModel.setBtConnect(true);
                        mainBpModel.set651BtConnect(false);
                        cnt++;
                    } else if (device.getName().startsWith(ManagerConstants.BloodPressureDevice.PORA_D40B)) {
                        mainBpModel.setBtConnect(true);
                        mainBpModel.set651BtConnect(false);
                        cnt++;
                    }
                    if (cnt == 0) {
                        mainBpModel.setBtConnect(false);
                        mainBpModel.set651BtConnect(false);
                    }
                }
            } else {
                mainBpModel.setBtConnect(false);
                mainBpModel.set651BtConnect(false);
            }
            binding.layoutBp.setMainBp(mainBpModel);

            if (cnt > 1) {
                PairingListCustomDialog dialog = new PairingListCustomDialog(context,
                        pairedDevices,
                        new PairingListInterface() {

                            @Override
                            public void
                            onConfirm(final String deviceName) {
                                Handler mHandler = new Handler();
                                mHandler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (deviceName.length() > 14) {
                                            if (ManagerConstants.BloodPressureDevice.AnD_BP_UA_651_BLE.equals(deviceName.substring(0,
                                                    13))) {
                                                startBLEService();
                                            }
                                        }
                                    }
                                }, 100);
                            }
                        });
                dialog.setCancelable(false);
                if (!((Activity) context).isFinishing()) {
                    dialog.show();
                }
            }
        }
    }

    /**
     * 체중 관련 리스너
     */
    private void WsClickListener() {
        //체중 그래프 보기
        binding.layoutWs.ivGraph.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(context, WeightGraphActivity.class);
                    startActivityForResult(intent, ManagerConstants.ActivityResultCode.WS_VIEW_GRAPH);
                    overridePendingTransition(0, 0);
                }
            }
        });

        //체중 입력
        binding.layoutWs.llInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(context, InputWeightActivity.class);
                    if (responesWsMap != null) {
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT,
                                responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                    }
                    startActivityForResult(intent, ManagerConstants.ActivityResultCode.WS_INPUT_MEASURE);
                    overridePendingTransition(0, 0);
                }
            }
        });

        //체중 메모 입력
        binding.layoutWs.llMemo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (responesWsMap != null && responesWsMap.size() > 0) {
                        Intent intent = new Intent(context, WeightMemoActivity.class);
                        intent.putExtra("memo", "memo");
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ,
                                responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT,
                                responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT,
                                responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI,
                                responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE,
                                responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_INS_DT,
                                responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                                responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE,
                                responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));

                        startActivityForResult(intent, ManagerConstants.ActivityResultCode.WS_INPUT_MEMO);
                        overridePendingTransition(0, 0);
                    }
                }
            }
        });
        //
        //        //체중 측정 안내
        //        binding.layoutWs.llMeasureInfo.setOnClickListener(new View.OnClickListener() {
        //
        //            @Override
        //            public void onClick(View v) {
        //                Intent intent = new Intent(context, WeightMeasureGuideActivity.class);
        //                startActivity(intent);
        //            }
        //        });

        //체중 기준
        binding.layoutWs.llStandard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(context, WeightStandardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });

        //체중 단위 변경
        binding.layoutWs.llWeightUnit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    int chkUnit;
                    if (ManagerConstants.Unit.KG.equals(PreferenceUtil.getWeightUnit(MainActivity.this))) {
                        chkUnit = 0;
                    } else {
                        chkUnit = 1;
                    }
                    final DefaultChoiceDialog dialog = new DefaultChoiceDialog(MainActivity.this,
                            chkUnit,
                            getString(R.string.common_txt_weight_measure_unit),
                            ManagerConstants.Unit.KG,
                            ManagerConstants.Unit.LBS,
                            new IChoiceDialog() {

                                @Override
                                public void onCancel() {
                                }

                                @Override
                                public void onConfirm(int chkItem) {
                                    if (chkItem == 0) {
                                        PreferenceUtil.setWeightUnit(MainActivity.this,
                                                ManagerConstants.Unit.KG);
                                        binding.layoutWs.tvWeightUnit.setText(ManagerConstants.Unit.KG);
                                        strWeight =
                                                ManagerUtil.oneDecimalPlaceDrop(responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                                        mainWsModel.setWeight(ManagerUtil.oneDecimalPlaceDrop(responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT)));
                                        //                                        mainWsModel.setWeight(responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                                    } else {
                                        PreferenceUtil.setWeightUnit(MainActivity.this,
                                                ManagerConstants.Unit.LBS);
                                        binding.layoutWs.tvWeightUnit.setText(ManagerConstants.Unit.LBS);
                                        mainWsModel.setWeight(ManagerUtil.kgToLbs(responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT)));
                                    }
                                    binding.layoutWs.setMainWs(mainWsModel);
                                    binding.setMainView(mainViewModel);
                                }
                            });
                    dialog.show();
                }
            }
        });

        binding.layoutWs.llMeasureInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(context, WeightMeasureGuideActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });

    }

    /**
     * 혈압 관련 리스너
     */
    private void BpClickListener() {
        //혈압 그래프 보기
        binding.layoutBp.ivGraph.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(context, BPGraphActivity.class);
                    startActivityForResult(intent, ManagerConstants.ActivityResultCode.BP_VIEW_GRAPH);
                    overridePendingTransition(0, 0);
                }
            }
        });

        //혈압 그래프 보기
        binding.layoutBp.ivBtGraph.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(context, BPGraphActivity.class);
                    startActivityForResult(intent, ManagerConstants.ActivityResultCode.BP_VIEW_GRAPH);
                    overridePendingTransition(0, 0);

                }
            }
        });
        //혈압 입력
        binding.layoutBp.llInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(context, InputBPActivity.class);
                    if (responesBpMap != null) {
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE));
                    }
                    startActivityForResult(intent, ManagerConstants.ActivityResultCode.BP_INPUT_MEASURE);
                    overridePendingTransition(0, 0);
                }

            }
        });
        //혈압 입력
        binding.layoutBp.llManualInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(context, InputBPActivity.class);

                    if (responesBpMap != null) {
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE));
                    }

                    startActivityForResult(intent, ManagerConstants.ActivityResultCode.BP_INPUT_MEASURE);
                    overridePendingTransition(0, 0);
                }
            }
        });

        //혈압 블루투스 입력
        binding.layoutBp.llBluetoothInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(context, BPMeasureActivity.class);
                    startActivityForResult(intent, ManagerConstants.ActivityResultCode.BP_INPUT_MEASURE);
                    overridePendingTransition(0, 0);
                }
            }
        });

        //혈압 메모 입력
        binding.layoutBp.llMemo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (responesBpMap != null && responesBpMap.size() > 0) {
                        Intent intent = new Intent(context, BPMemoActivity.class);
                        intent.putExtra("memo", "memo");
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_INS_DT,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE,
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));

                        startActivityForResult(intent, ManagerConstants.ActivityResultCode.BP_INPUT_MEMO);
                        overridePendingTransition(0, 0);
                    }
                }
            }
        });

        //        //혈압 측정 안내
        //        binding.layoutBp.llMeasureInfo.setOnClickListener(new View.OnClickListener() {
        //
        //            @Override
        //            public void onClick(View v) {
        //                Intent intent = new Intent(context, BPMeasureGuideActivity.class);
        //                startActivity(intent);
        //            }
        //        });

        //혈압 측정 안내
        binding.layoutBp.llBtMeasureInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(context, BPMeasureGuideActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });
        //혈압 기준
        binding.layoutBp.llStandard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(context, BPStandardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });

        binding.layoutBp.llMeasureInfo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(context, BPMeasureGuideActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });
    }

    /**
     * 혈당 관련 리스너
     */
    private void GlucoseClickListener() {
        //혈당 그래프 보기
        binding.layoutGlucose.ivGraph.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(context, GlucoseGraphActivity.class);
                    startActivityForResult(intent, ManagerConstants.ActivityResultCode.GLUCOSE_VIEW_GRAPH);
                    overridePendingTransition(0, 0);
                }
            }
        });

        //혈당 입력
        binding.layoutGlucose.llInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(context, InputGlucoseActivity.class);
                    if (responesGlucoseMap != null) {
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE,
                                responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                                responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL));
                    }
                    startActivityForResult(intent, ManagerConstants.ActivityResultCode.GLUCOSE_INPUT_MEASURE);
                    overridePendingTransition(0, 0);
                }
            }
        });

        //혈당 메모 입력
        binding.layoutGlucose.llMemo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (responesGlucoseMap != null && responesGlucoseMap.size() > 0) {
                        Intent intent = new Intent(context, GlucoseMemoActivity.class);
                        intent.putExtra("memo", "memo");
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ,
                                responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE,
                                responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                                responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                                responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_INS_DT,
                                responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                                responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN));
                        intent.putExtra(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE,
                                responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));
                        startActivityForResult(intent, ManagerConstants.ActivityResultCode.GLUCOSE_INPUT_MEMO);
                        overridePendingTransition(0, 0);
                    }
                }
            }
        });

        //혈당 기준
        binding.layoutGlucose.llStandard.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(context, GlucoseStandardActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                }
            }
        });

        //혈당 단위 변경
        binding.layoutGlucose.llGlucoseUnit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    int chkUnit;
                    if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(MainActivity.this))) {
                        chkUnit = 0;
                    } else {
                        chkUnit = 1;
                    }
                    final DefaultChoiceDialog dialog = new DefaultChoiceDialog(MainActivity.this,
                            chkUnit,
                            getString(R.string.common_txt_glucose_measure_unit),
                            ManagerConstants.Unit.MGDL,
                            ManagerConstants.Unit.MMOL,
                            new IChoiceDialog() {

                                @Override
                                public void onCancel() {
                                }

                                @Override
                                public void onConfirm(int chkItem) {

                                    if (chkItem == 0) {
                                        PreferenceUtil.setGlucoseUnit(MainActivity.this,
                                                ManagerConstants.Unit.MGDL);
                                        binding.layoutGlucose.tvGlucoseUnit.setText(ManagerConstants.Unit.MGDL);
                                        mainGlucoseModel.setGlucose(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));
                                    } else {
                                        PreferenceUtil.setGlucoseUnit(MainActivity.this,
                                                ManagerConstants.Unit.MMOL);
                                        binding.layoutGlucose.tvGlucoseUnit.setText(ManagerConstants.Unit.MMOL);
                                        mainGlucoseModel.setGlucose(ManagerUtil.mgToMmol(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE)));
                                    }
                                    binding.layoutGlucose.setMainGlucose(mainGlucoseModel);
                                    binding.setMainView(mainViewModel);

                                }
                            });
                    dialog.show();
                }
            }
        });

        //혈당 식전 식후 변경
        binding.layoutGlucose.llEat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    final int chkUnit;
                    if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                        chkUnit = 0;
                    } else {
                        chkUnit = 1;
                    }
                    final DefaultChoiceDialog dialog = new DefaultChoiceDialog(MainActivity.this,
                            chkUnit,
                            getString(R.string.dialog_glucose_check_condition),
                            getString(R.string.glucose_main_txt_meal_before),
                            getString(R.string.glucose_main_txt_meal_after),
                            new IChoiceDialog() {

                                @Override
                                public void onCancel() {
                                }

                                @Override
                                public void onConfirm(int chkItem) {
                                    if (BPDiaryApplication.isNetworkState(context)) {
                                        if (chkItem == 0) {
                                            mainGlucoseModel.setMealResult(getString(R.string.glucose_main_txt_meal_before));
                                        } else {
                                            mainGlucoseModel.setMealResult(getString(R.string.glucose_main_txt_meal_after));
                                        }
                                        binding.layoutGlucose.setMainGlucose(mainGlucoseModel);
                                        binding.setMainView(mainViewModel);

                                        if (chkUnit != chkItem) {
                                            AnalyticsUtil.sendEvent(MainActivity.this,
                                                    "혈당",
                                                    "Evnet",
                                                    "3_혈당 메인 식전/식후 변경");
                                            new ModGlucoseMeal().execute();
                                        }
                                    } else {
                                        DefaultOneButtonDialog defaultOneButtonDialog =
                                                new DefaultOneButtonDialog(context,
                                                        getString(R.string.dialog_title_alarm),
                                                        getString(R.string.report_variation_network_false_guide),
                                                        getString(R.string.common_txt_confirm),
                                                        new IDefaultOneButtonDialog() {

                                                            @Override
                                                            public void
                                                            onConfirm() {
                                                            }
                                                        });
                                        defaultOneButtonDialog.show();
                                    }
                                }
                            });
                    dialog.show();
                }
            }
        });
    }

    /**
     * 혈당 화면 표시 메서드
     */
    private void setDisplayGlucoseData(List<Map<String, String>> listResultData) {

        if (listResultData != null && listResultData.size() > 0) {
            AnalyticsUtil.sendScene(MainActivity.this, "3_혈당 메인최근");
            binding.layoutGlucose.rlIsData.setVisibility(View.VISIBLE);
            binding.layoutGlucose.rlNoData.setVisibility(View.GONE);
            try {

                responesGlucoseMap = listResultData.get(0);
                String strMessage = responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE);

                String[] date =
                        ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)),
                                ManagerUtil.ShowFormatPosition.SECOND,
                                true,
                                "/",
                                ":",
                                "yyyyMMddHHmmss",
                                responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));
                mainGlucoseModel.setDate(date[0] + "  |  " + date[1]);
                mainGlucoseModel.setEatType(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL));
                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(context))) {
                    mainGlucoseModel.setGlucose(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));
                } else if (ManagerConstants.Unit.MMOL.equals(PreferenceUtil.getGlucoseUnit(context))) {
                    mainGlucoseModel.setGlucose(ManagerUtil.mgToMmol(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE)));
                } else {
                    mainGlucoseModel.setGlucose(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));
                }

                String strType = responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE);

                if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                    mainGlucoseModel.setMealResult(getResources().getString(R.string.glucose_main_txt_meal_before));
                } else {
                    mainGlucoseModel.setMealResult(getResources().getString(R.string.glucose_main_txt_meal_after));
                }

                if (HealthcareConstants.GlucoseState.GLUCOSE_LOW.equals(strType)) {
                    //저혈당
                    binding.layoutGlucose.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_e6ea47));
                    setViewAnimation(ManagerConstants.PageType.TYPE_GLUCOSE, 1);
                    mainGlucoseModel.setGlucoseType(1);
                    mainGlucoseModel.setGlucoseResult(getResources().getString(R.string.glucose_main_txt_status_low));
                } else if (HealthcareConstants.GlucoseState.GLUCOSE_NORMAL.equals(strType)) {
                    //정상
                    binding.layoutGlucose.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                    setViewAnimation(ManagerConstants.PageType.TYPE_GLUCOSE, 2);
                    mainGlucoseModel.setGlucoseType(2);
                    mainGlucoseModel.setGlucoseResult(getResources().getString(R.string.glucose_main_txt_status_normal));
                } else if (HealthcareConstants.GlucoseState.GLUCOSE_OVER.equals(strType)) {
                    //고혈당
                    binding.layoutGlucose.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_f06515));
                    setViewAnimation(ManagerConstants.PageType.TYPE_GLUCOSE, 3);
                    mainGlucoseModel.setGlucoseType(3);
                    mainGlucoseModel.setGlucoseResult(getResources().getString(R.string.glucose_main_txt_status_over));
                } else {
                    //기본 정상
                    binding.layoutGlucose.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                    setViewAnimation(ManagerConstants.PageType.TYPE_GLUCOSE, 2);
                    binding.layoutGlucose.ivLow.setVisibility(View.GONE);
                    binding.layoutGlucose.ivNormal.setVisibility(View.VISIBLE);
                    binding.layoutGlucose.ivHigh.setVisibility(View.GONE);
                    mainGlucoseModel.setGlucoseResult(getResources().getString(R.string.glucose_main_txt_status_normal));
                }

                // 메모 수정 화면
                if (!TextUtils.isEmpty(strMessage)) {
                    setMemoSelector(binding.layoutGlucose.ivMemo, binding.layoutGlucose.tvMemo, true);
                } else {
                    setMemoSelector(binding.layoutGlucose.ivMemo, binding.layoutGlucose.tvMemo, false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            AnalyticsUtil.sendScene(MainActivity.this, "3_혈당 메인최초");
            binding.layoutGlucose.rlIsData.setVisibility(View.GONE);
            binding.layoutGlucose.rlNoData.setVisibility(View.VISIBLE);
        }

        binding.setMainView(mainViewModel);
        binding.layoutGlucose.setMainGlucose(mainGlucoseModel);
    }

    private void setMemoSelector(ImageView ivMemo, TextView tvMemo, boolean flag) {
        if (flag) {
            ivMemo.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_main_update_memo));
            tvMemo.setText(R.string.common_txt_memo_update);
            tvMemo.setTextColor(getResources().getColor(R.color.color_4b413d));
        } else {
            ivMemo.setBackgroundDrawable(getResources().getDrawable(R.drawable.selector_main_memo));
            tvMemo.setText(R.string.main_input_memo_insert);
            tvMemo.setTextColor(getResources().getColor(R.color.color_9e9694));
        }
    }

    /**
     * 혈압 화면 표시 메서드
     */
    private void setDisplayBpData(List<Map<String, String>> listResultData) {

        if (listResultData != null && listResultData.size() > 0) {
            AnalyticsUtil.sendScene(MainActivity.this, "3_혈압 메인최근");
            binding.layoutBp.rlIsData.setVisibility(View.VISIBLE);
            binding.layoutBp.rlNoData.setVisibility(View.GONE);
            //측정 Data 표시
            try {

                responesBpMap = listResultData.get(0);

                String strMessage = responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE);
                String strArm = responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM);
                String[] date =
                        ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)),
                                ManagerUtil.ShowFormatPosition.SECOND,
                                true,
                                "/",
                                ":",
                                "yyyyMMddHHmmss",
                                responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));

                mainBpModel.setDate(date[0] + "  |  " + date[1]);
                mainBpModel.setSys(responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS));
                mainBpModel.setDia(responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA));
                mainBpModel.setPulse(responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE));

                String strType = responesBpMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE);

                if (HealthcareConstants.BloodPressureState.BP_LOW.equals(strType)) {
                    //저혈압
                    binding.layoutBp.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_76adef));
                    mainBpModel.setBpType(1);
                    mainBpModel.setBpResult(getResources().getString(R.string.bp_main_txt_status_low));
                    setViewAnimation(ManagerConstants.PageType.TYPE_BP, 1);
                } else if (HealthcareConstants.BloodPressureState.BP_NORMAL.equals(strType)) {
                    //정상
                    binding.layoutBp.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_ace03b));
                    mainBpModel.setBpType(2);
                    mainBpModel.setBpResult(getResources().getString(R.string.bp_main_txt_status_normal));
                    setViewAnimation(ManagerConstants.PageType.TYPE_BP, 2);

                } else if (HealthcareConstants.BloodPressureState.BP_APPROACH.equals(strType)) {
                    //고혈압 전단계
                    binding.layoutBp.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_f4dc2e));
                    mainBpModel.setBpType(3);
                    mainBpModel.setBpResult(getResources().getString(R.string.bp_main_txt_status_approach));
                    setViewAnimation(ManagerConstants.PageType.TYPE_BP, 3);

                } else if (HealthcareConstants.BloodPressureState.BP_HIGH_ONE.equals(strType)) {
                    //1기 고혈압
                    binding.layoutBp.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_f49b40));
                    mainBpModel.setBpType(4);
                    mainBpModel.setBpResult(getResources().getString(R.string.bp_main_txt_status_high_one));
                    setViewAnimation(ManagerConstants.PageType.TYPE_BP, 4);
                } else if (HealthcareConstants.BloodPressureState.BP_HIGH_TWO.equals(strType)) {
                    //2기 고혈압
                    binding.layoutBp.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_ee4633));
                    mainBpModel.setBpType(5);
                    mainBpModel.setBpResult(getResources().getString(R.string.bp_main_txt_status_high_two));
                    setViewAnimation(ManagerConstants.PageType.TYPE_BP, 5);

                } else if (HealthcareConstants.BloodPressureState.BP_VERY_HIGH.equals(strType)) {
                    //높은 고혈압
                    binding.layoutBp.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_c8084c));
                    mainBpModel.setBpType(6);
                    mainBpModel.setBpResult(getResources().getString(R.string.bp_main_txt_status_very_high));
                    setViewAnimation(ManagerConstants.PageType.TYPE_BP, 6);

                } else {
                    //기본 정상
                    binding.layoutBp.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_ace03b));
                    mainBpModel.setBpType(2);
                    mainBpModel.setBpResult(getResources().getString(R.string.bp_main_txt_status_normal));
                    setViewAnimation(ManagerConstants.PageType.TYPE_BP, 2);
                }

                if (!TextUtils.isEmpty(strMessage)) {
                    setMemoSelector(binding.layoutBp.ivMemo, binding.layoutBp.tvMemo, true);
                } else {
                    setMemoSelector(binding.layoutBp.ivMemo, binding.layoutBp.tvMemo, false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            AnalyticsUtil.sendScene(MainActivity.this, "3_혈압 메인최초");
            binding.layoutBp.rlIsData.setVisibility(View.GONE);
            binding.layoutBp.rlNoData.setVisibility(View.VISIBLE);
        }

        binding.setMainView(mainViewModel);
        binding.layoutBp.setMainBp(mainBpModel);
    }

    /**
     * 체중 화면 표시 메서드
     */
    private void setDisplayWsData(List<Map<String, String>> listResultData) {
        if (listResultData != null && listResultData.size() > 0) {
            AnalyticsUtil.sendScene(MainActivity.this, "3_체중 메인최근");
            binding.layoutWs.llIsData.setVisibility(View.VISIBLE);
            binding.layoutWs.llNoData.setVisibility(View.GONE);
            //측정 Data 표시
            try {

                responesWsMap = listResultData.get(0);

                String strWsDbSeq = responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ);
                String strMessage = responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE);

                String[] date =
                        ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)),
                                ManagerUtil.ShowFormatPosition.SECOND,
                                true,
                                "/",
                                ":",
                                "yyyyMMddHHmmss",
                                responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));
                responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT);

                mainWsModel.setDate(date[0] + "  |  " + date[1]);

                strWeight = responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT);
                mainWsModel.setDefaultWeight(ManagerUtil.kgToLbs(strWeight));

                if (getResources().getString(R.string.weight_kg).equals(PreferenceUtil.getWeightUnit(context))) {
                    binding.layoutWs.tvWeightUnit.setText(ManagerConstants.Unit.KG);
                    strWeight =
                            ManagerUtil.oneDecimalPlaceDrop(responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                } else if (getResources().getString(R.string.weight_lbs)
                        .equals(PreferenceUtil.getWeightUnit(context))) {
                    binding.layoutWs.tvWeightUnit.setText(ManagerConstants.Unit.LBS);
                    strWeight = ManagerUtil.kgToLbs(strWeight);
                } else {
                    binding.layoutWs.tvWeightUnit.setText(ManagerConstants.Unit.KG);
                    strWeight = mainWsModel.getDefaultWeight();
                }
                mainWsModel.setWeight(strWeight);
                mainWsModel.setBmi(responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI));

                String strBmiState =
                        HealthcareUtil.getWeighingScaleBmiType(responesWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI));

                if (HealthcareConstants.WeighingScaleState.WS_LOW.equals(strBmiState)) {
                    //저체중
                    binding.layoutWs.ccvWeightResult.setCircleColor(getResources().getColor(R.color.color_e9da64));
                    mainWsModel.setWsType(1);
                    mainWsModel.setWsResult(getResources().getString(R.string.weighing_scale_main_txt_status_low));
                    setViewAnimation(ManagerConstants.PageType.TYPE_WS, 1);

                } else if (HealthcareConstants.WeighingScaleState.WS_NORMAL.equals(strBmiState)) {
                    //정상
                    binding.layoutWs.ccvWeightResult.setCircleColor(getResources().getColor(R.color.color_82d589));
                    mainWsModel.setWsType(2);
                    mainWsModel.setWsResult(getResources().getString(R.string.weighing_scale_main_txt_status_normal));
                    setViewAnimation(ManagerConstants.PageType.TYPE_WS, 2);

                } else if (HealthcareConstants.WeighingScaleState.WS_OVER_WEIGHT.equals(strBmiState)) {
                    //과체중
                    binding.layoutWs.ccvWeightResult.setCircleColor(getResources().getColor(R.color.color_6aa6e9));
                    mainWsModel.setWsType(3);
                    mainWsModel.setWsResult(getResources().getString(R.string.weighing_scale_main_txt_status_approach));
                    setViewAnimation(ManagerConstants.PageType.TYPE_WS, 3);

                } else if (HealthcareConstants.WeighingScaleState.WS_OBESITY.equals(strBmiState)) {
                    //비만
                    binding.layoutWs.ccvWeightResult.setCircleColor(getResources().getColor(R.color.color_a180de));
                    mainWsModel.setWsType(4);
                    mainWsModel.setWsResult(getResources().getString(R.string.weighing_scale_main_txt_status_obesity));
                    setViewAnimation(ManagerConstants.PageType.TYPE_WS, 4);

                } else if (HealthcareConstants.WeighingScaleState.WS_VERY_OBESITY.equals(strBmiState)) {
                    //고도비만
                    binding.layoutWs.ccvWeightResult.setCircleColor(getResources().getColor(R.color.color_ed5967));
                    mainWsModel.setWsType(5);
                    mainWsModel.setWsResult(getResources().getString(R.string.weighing_scale_main_txt_status_very_obesity));
                    setViewAnimation(ManagerConstants.PageType.TYPE_WS, 5);

                } else {
                    //기본 정상
                    binding.layoutWs.ccvWeightResult.setCircleColor(getResources().getColor(R.color.color_82d589));
                    mainWsModel.setWsType(2);
                    mainWsModel.setWsResult(getResources().getString(R.string.weighing_scale_main_txt_status_normal));
                    setViewAnimation(ManagerConstants.PageType.TYPE_WS, 2);

                }
                if (!TextUtils.isEmpty(strMessage)) {
                    setMemoSelector(binding.layoutWs.ivMemo, binding.layoutWs.tvMemo, true);
                } else {
                    setMemoSelector(binding.layoutWs.ivMemo, binding.layoutWs.tvMemo, false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            AnalyticsUtil.sendScene(MainActivity.this, "3_체중 메인최초");
            binding.layoutWs.llIsData.setVisibility(View.GONE);
            binding.layoutWs.llNoData.setVisibility(View.VISIBLE);
        }
        binding.layoutWs.setMainWs(mainWsModel);
    }

    /**
     * BP Animation 관련
     */

    private void setViewAnimation(String type, int level) {

        // animation item
        Animation shake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.item_shake);
        if (isGetResult) { //onActivityResult 받았을때만 실행
            if (type.equals(ManagerConstants.PageType.TYPE_BP)) { //혈압일때
                binding.layoutBp.llSysNPulse.startAnimation(getAnimation(100, 100));
                binding.layoutBp.llDiaNMemo.startAnimation(getAnimation(150, 100));
                switch (level) {
                    case 1:
                        binding.layoutBp.ivLow.startAnimation(getAnimation(200, 100));
                        break;

                    case 2:
                        binding.layoutBp.ivNormal.startAnimation(getAnimation(200, 100));
                        break;

                    case 3:
                        binding.layoutBp.ivApproch.startAnimation(getAnimation(200, 100));
                        break;

                    case 4:
                        binding.layoutBp.ivHighOne.startAnimation(getAnimation(200, 100));
                        break;

                    case 5:
                        binding.layoutBp.ivHighTwo.startAnimation(getAnimation(200, 100));
                        break;

                    case 6:
                        binding.layoutBp.ivVeryHigh.startAnimation(getAnimation(200, 100));
                        break;
                }

            } else if (type.equals(ManagerConstants.PageType.TYPE_GLUCOSE)) { //혈당일때
                binding.layoutGlucose.llGlucoseValue.startAnimation(getAnimation(100, 100));
                binding.layoutGlucose.llEatNMemo.startAnimation(getAnimation(200, 100));
                switch (level) {
                    case 1:
                        binding.layoutGlucose.ivLow.startAnimation(getAnimation(200, 100));
                        break;

                    case 2:
                        binding.layoutGlucose.ivNormal.startAnimation(getAnimation(200, 100));
                        break;

                    case 3:
                        binding.layoutGlucose.ivHigh.startAnimation(getAnimation(200, 100));
                        break;
                }

            } else if (type.equals(ManagerConstants.PageType.TYPE_WS)) { //체중 일때
                binding.layoutWs.llWeightInfoLeft.startAnimation(getAnimation(100, 100));
                binding.layoutWs.llWeightInfoRight.startAnimation(getAnimation(200, 100));
                switch (level) {
                    case 1:
                        binding.layoutWs.ivLow.startAnimation(getAnimation(200, 100));
                        break;
                    case 2:
                        binding.layoutWs.ivNormal.startAnimation(getAnimation(200, 100));
                        break;
                    case 3:
                        binding.layoutWs.ivApproch.startAnimation(getAnimation(200, 100));
                        break;
                    case 4:
                        binding.layoutWs.ivHighOne.startAnimation(getAnimation(200, 100));
                        break;
                    case 5:
                        binding.layoutWs.ivHighTwo.startAnimation(getAnimation(200, 100));
                        break;
                }
            }
            isGetResult = false;
        }
    }

    /**
     * 복약알람노티로 진입한 메인일 경우, 해당 노티를 취소해주고, 알림을 취소한 후 다시 셋팅
     */
    private void setSettingAlarm() {
        Intent alarmIntent = getIntent();
        String alarmFlag = alarmIntent.getStringExtra(ManagerConstants.IntentData.ALARM_FLAG);
        int notifySEQ = alarmIntent.getIntExtra(ManagerConstants.IntentData.ALARM_SEQ, 0);
        String time = alarmIntent.getStringExtra(ManagerConstants.IntentData.ALARM_TIME);

        if (notifySEQ != 0) {
            NotificationManager mNotifyManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotifyManager.cancel(notifySEQ);

            PreferenceUtil.setWholeToggleState(MainActivity.this,
                    PreferenceUtil.getWholeToggleState(MainActivity.this));
            AlarmUtils.getInstance(context).setCancelAlarm(context, notifySEQ);
            AlarmUtils.getInstance(context).setAlarmManager(context, time, alarmFlag, String.valueOf(notifySEQ), true);
        }
    }

    /**
     * setting 메뉴 어뎁터
     */
    private class MyGridViewAdpater extends BaseAdapter {

        List<ExtendData> list = new ArrayList();

        public void add(ExtendData extendData) {
            list.add(extendData);
            notifyDataSetChanged();

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_setting_grid_list,
                        parent,
                        false);

            }

            ExtendData extendData = list.get(position);

            TextView titleView = (TextView) convertView.findViewById(R.id.tv_title);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_image);

            titleView.setText(extendData.getTitle());

            imageView.setBackgroundResource(extendData.getResId());

            return convertView;
        }
    }

    private class ExtendData {

        private int resId;

        private String title;

        public ExtendData(int resId, String title) {
            this.resId = resId;
            this.title = title;
        }

        public int getResId() {
            return resId;
        }

        public void setResId(int resId) {
            this.resId = resId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public void Buy(final String item) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mHelper.launchPurchaseFlow(MainActivity.this, item, 1001, mPurchaseFinishedListener);
            }
        });
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {

        @Override
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            // mHelper가 소거되었다면 종료
            if (mHelper == null) {
                return;
            }
            // getPurchases()가 실패하였다면 종료
            if (result.isFailure()) {
                return;
            }

            //            Purchase premiumPurchase = inventory.getPurchase(ITEM_REMOVE_ADS);
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {

        @Override
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            //결제 완료 되었을때 각종 결제 정보들을 볼 수 있습니다. 이정보들을 서버에 저장해 놓아야 결제 취소시 변경된 정보를 관리 할 수 있을것 같습니다~
            if (purchase != null) {
                if (!verifyDeveloperPayload(purchase)) {
                }
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            } else {
                if (result.getResponse() == IabHelper.IABHELPER_USER_CANCELLED) {

                } else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_USER_CANCELED) {

                } else if (result.getResponse() == IabHelper.BILLING_RESPONSE_RESULT_ITEM_ALREADY_OWNED) {

                }
            }
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {

        @Override
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            SendConsumeResult(purchase, result);
        }
    };

    protected void SendConsumeResult(Purchase purchase, IabResult result) {

        if (purchase != null) {

            email = PreferenceUtil.getEncEmail(context);
            signature = purchase.getSignature();

            if (ITEM_REMOVE_ADS.equals(purchase.getSku())) {
                PreferenceUtil.setPaymentCheck(context, ManagerConstants.PaymentAdYN.PAYMENT_AD_Y);
                PreferenceUtil.setPaymentSign(context, signature);

                new CheckPaymentSync().execute(email, signature);
            }
        }
    }

    ServiceConnection mServiceConn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
        }
    };

    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be the same one that you sent
         * when initiating the purchase. WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the case where the user purchases an
         * item on one device and then uses your app on a different device, because on the other device you will not
         * have access to the random string you originally generated. So a good developer payload has these
         * characteristics: 1. If two different users purchase an item, the payload is different between them, so that
         * one user's purchase can't be replayed to another user. 2. The payload must be such that you can verify it
         * even when the app wasn't the one who initiated the purchase flow (so that items purchased by the user on one
         * device work on other devices owned by the user). Using your own server to store and verify developer payloads
         * across app installations is recommended.
         */

        return true;
    }

    private class CheckPaymentSync extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {
                // 파라미터 설정
                PaymentService paymentService = new PaymentService(context);

                data.put(ManagerConstants.RequestParamName.UUID, params[0]);
                data.put(ManagerConstants.RequestParamName.PAY_TOKEN, params[1]);

                resultJSON = paymentService.creatPayment(data);

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

                if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                            .toString())) {

                        adView.setVisibility(View.GONE);

                    } else {
                        for (int i = 0; i < 3; i++) {
                            new CheckPaymentSync().execute(email, signature);
                            Toast.makeText(context,
                                    getResources().getText(R.string.common_txt_error_comment),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                } else {
                    for (int i = 0; i < 3; i++) {
                        new CheckPaymentSync().execute(email, signature);
                        Toast.makeText(context,
                                getResources().getText(R.string.common_txt_error_comment),
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 혈당 마지막 결과 조회 Sync
     */
    private class SearchGlucoseDBSync extends AsyncTask<Void, Void, Void> {

        /**
         * 측정 결과 데이터
         */
        private List<Map<String, String>> listResultData;

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (isNewGlucoseData) {
                    //가장 최신 데이터
                    listResultData = glucoseService.searchGlucoseLastMeasureData();
                } else {
                    //가장 최근 입력 데이터
                    listResultData = glucoseService.searchGlucoseLastInputData();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            hideLodingProgress();

            setDisplayGlucoseData(listResultData);
        }
    }

    /**
     * 혈압 마지막 결과 조회 Sync
     */
    private class SearchBloodPressureDBSync extends AsyncTask<Void, Void, Void> {

        /**
         * 측정 결과 데이터
         */
        private List<Map<String, String>> listResultData;

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected Void doInBackground(Void... param) {

            try {

                if (isNewBpData) {
                    //가장 최신 데이터
                    listResultData = bpService.searchBloodPressureLastMeasureData();

                } else {
                    //가장 최근 입력 데이터
                    listResultData = bpService.searchBloodPressureLastInputData();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            hideLodingProgress();
            setDisplayBpData(listResultData);

            Intent intent = getIntent();
            EventInfoData eventInfoData = (EventInfoData) intent.getSerializableExtra(CommonConstants.EVENT_DATA);
            if (eventInfoData != null) {
                if (eventInfoData.getSeq() != null && !eventInfoData.getSeq().isEmpty() && adsFlag == true) {
                    long now = System.currentTimeMillis();

                    if (!"language".equals(getIntent().getStringExtra("language"))) {
                        if (eventInfoData.getSeq().equals(PreferenceUtil.getEventAdsOneSeq(context))) {
                            if (Long.parseLong(PreferenceUtil.getEventAdsOneTime(context)) - now < 0) {
                                PreferenceUtil.setEventAdsOne(context, "", "");
                                setEventdialog(eventInfoData);
                            }
                        } else if (eventInfoData.getSeq().equals(PreferenceUtil.getEventAdsTwoSeq(context))) {
                            if (Long.parseLong(PreferenceUtil.getEventAdsTwoTime(context)) - now < 0) {
                                PreferenceUtil.setEventAdsTwo(context, "", "");
                                setEventdialog(eventInfoData);
                            }
                        } else if (eventInfoData.getSeq().equals(PreferenceUtil.getEventAdsThreeSeq(context))) {
                            if (Long.parseLong(PreferenceUtil.getEventAdsThreeTime(context)) - now < 0) {
                                PreferenceUtil.setEventAdsThree(context, "", "");
                                setEventdialog(eventInfoData);
                            }
                        } else if (eventInfoData.getSeq().equals(PreferenceUtil.getEventAdsFourSeq(context))) {
                            if (Long.parseLong(PreferenceUtil.getEventAdsFourTime(context)) - now < 0) {
                                PreferenceUtil.setEventAdsFour(context, "", "");
                                setEventdialog(eventInfoData);
                            }
                        } else if (eventInfoData.getSeq().equals(PreferenceUtil.getEventAdsFiveSeq(context))) {
                            if (Long.parseLong(PreferenceUtil.getEventAdsFiveTime(context)) - now < 0) {
                                PreferenceUtil.setEventAdsFive(context, "", "");
                                setEventdialog(eventInfoData);
                            }
                        } else {
                            setEventdialog(eventInfoData);
                        }
                    }
                }
            }
        }
    }

    /**
     * 체중계 마지막 결과 조회 Sync
     */
    private class SearchWeighingScaleDBSync extends AsyncTask<Void, Void, List<Map<String, String>>> {

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected List<Map<String, String>> doInBackground(Void... param) {
            List<Map<String, String>> listResultData = new ArrayList<>();
            try {
                if (isNewWsData) {
                    //가장 최신 데이터
                    listResultData = wsService.searchWeighinhScaleLastMeasureData();
                } else {
                    //가장 최근 입력 데이터
                    listResultData = wsService.searchWeighinhScaleLastInputData();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return listResultData;
        }

        @Override
        protected void onPostExecute(List<Map<String, String>> listResultData) {
            hideLodingProgress();

            setDisplayWsData(listResultData);
        }
    }

    /**
     * 서버 전송안된 데이터 다 보내기..
     */
    private class SyncDataAsync extends AsyncTask<Void, Integer, Void> {

        private final int sync_type_bp = 0;

        private final int sync_type_ws = 1;

        private final int sync_type_glucose = 2;

        @Override
        protected void onPreExecute() {
            mConnectionProgressDialog = new ProgressDialog(context);
            mConnectionProgressDialog.setCancelable(false);
            mConnectionProgressDialog.setCanceledOnTouchOutside(false);

            showLodingProgress();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            hideLodingProgress();

            if (!mConnectionProgressDialog.isShowing()) {
                mConnectionProgressDialog.show();
            }

            int nSyncType = values[0];

            if (nSyncType == sync_type_bp) {
                int nCurrentCnt = values[1];
                int nTotalCnt = values[2];
                mConnectionProgressDialog.setMessage(getString(R.string.data_sync_dialog) + nCurrentCnt
                        + "/"
                        + nTotalCnt);

            } else if (nSyncType == sync_type_ws) {
                int nCurrentCnt = values[1];
                int nTotalCnt = values[2];
                mConnectionProgressDialog.setMessage(getString(R.string.data_sync_dialog) + nCurrentCnt
                        + "/"
                        + nTotalCnt);
            } else if (nSyncType == sync_type_glucose) {
                int nCurrentCnt = values[1];
                int nTotalCnt = values[2];
                mConnectionProgressDialog.setMessage(getString(R.string.data_sync_dialog) + nCurrentCnt
                        + "/"
                        + nTotalCnt);
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (!"".equals(PreferenceUtil.getEncEmail(context))) {

                //혈압 데이터 전송
                Map<Object, Object> bpData = new HashMap<Object, Object>();
                JSONObject resultBPJSON = new JSONObject();

                //체중 데이터 전송
                Map<Object, Object> weightData = new HashMap<Object, Object>();
                JSONObject resultWSJSON = new JSONObject();

                //혈당 데이터 전송
                Map<Object, Object> glucoseData = new HashMap<Object, Object>();
                JSONObject resultGlucoseJSON = new JSONObject();

                try {

                    //혈압 데이터 동기화
                    bpData.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(context));

                    int nBpDataCount = bpService.searchTotalCount();

                    if (nBpDataCount < 1) {
                        //DB 데이터 없을 시 Server 데이터 조회

                        resultBPJSON = bpService.syncMeasureData(bpData);

                        if (resultBPJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                            if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultBPJSON.getString(ManagerConstants.ResponseParamName.RESULT)
                                    .toString())) {

                                if (resultBPJSON.has(ManagerConstants.ResponseParamName.BP_LIST)) {

                                    JSONArray resultBPJSONArray =
                                            new JSONArray(resultBPJSON.getString(ManagerConstants.ResponseParamName.BP_LIST));

                                    if (resultBPJSONArray.length() > 0) {

                                        JSONObject itemJSON;
                                        Map<String, String> requestMap;

                                        for (int i = 0, size = resultBPJSONArray.length(); i < size; i++) {

                                            publishProgress(sync_type_bp, i + 1, size);

                                            itemJSON = resultBPJSONArray.getJSONObject(i);

                                            //DB에 넣을 Data 작성
                                            requestMap = new HashMap<String, String>();
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS,
                                                    itemJSON.getString(ManagerConstants.ResponseParamName.BP_SYS));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA,
                                                    itemJSON.getString(ManagerConstants.ResponseParamName.BP_DIA));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN,
                                                    itemJSON.getString(ManagerConstants.ResponseParamName.BP_MEAN));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE,
                                                    itemJSON.getString(ManagerConstants.ResponseParamName.BP_PULSE));
                                            String strTypeValue =
                                                    HealthcareUtil.getBloodPressureType(MainActivity.this,
                                                            itemJSON.getString(ManagerConstants.ResponseParamName.BP_SYS),
                                                            itemJSON.getString(ManagerConstants.ResponseParamName.BP_DIA));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE, strTypeValue);
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_SN)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_SN));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_MODEL)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_MODEL));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_COMPANY)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_COMPANY));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.BP_MESSAGE)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.BP_MESSAGE));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                                    itemJSON.getString(ManagerConstants.ResponseParamName.RECORD_DT));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_INS_DT,
                                                    itemJSON.getString(ManagerConstants.ResponseParamName.INS_DT));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                                                    ManagerConstants.ServerSyncYN.SERVER_SYNC_Y);

                                            //DB 저장
                                            int nRow = bpService.createBloodPressureData(requestMap);

                                        }
                                    }
                                }
                            }
                        }

                    } else {

                        //DB에서 Server 미전송 혈압 데이터 목록 조회
                        List<Map<String, String>> resultBPDataList = bpService.searchBloodPressureNotSyncDataList();

                        if (resultBPDataList.size() > 0) {

                            String strDbSeq = null;

                            Map<String, String> resultMap;

                            for (int i = 0, size = resultBPDataList.size(); i < size; i++) {

                                publishProgress(sync_type_bp, i + 1, size);

                                resultMap = resultBPDataList.get(i);

                                strDbSeq = String.valueOf(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ));

                                //Server 전송
                                bpData.put(ManagerConstants.RequestParamName.INS_DT,
                                        resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                                bpData.put(ManagerConstants.RequestParamName.BP_SYS,
                                        resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS));
                                bpData.put(ManagerConstants.RequestParamName.BP_DIA,
                                        resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA));
                                bpData.put(ManagerConstants.RequestParamName.BP_MEAN,
                                        resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN));
                                bpData.put(ManagerConstants.RequestParamName.BP_PULSE,
                                        resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE));
                                bpData.put(ManagerConstants.RequestParamName.BP_TYPE,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE));
                                bpData.put(ManagerConstants.RequestParamName.SENSOR_COMPANY,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
                                bpData.put(ManagerConstants.RequestParamName.SENSOR_MODEL,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL));
                                bpData.put(ManagerConstants.RequestParamName.SENSOR_SN,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID));
                                bpData.put(ManagerConstants.RequestParamName.MESSAGE,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));
                                bpData.put(ManagerConstants.RequestParamName.RECORD_DT,
                                        ManagerUtil.convertDateFormatToServer(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT)));

                                resultBPJSON = bpService.sendBloodPressureData(bpData);

                                if (resultBPJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultBPJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                            .toString())) {
                                        //Server 전송 완료

                                        //DB에 Server 전송 Update
                                        bpService.updateSendToServerYN(strDbSeq);

                                    }
                                }
                            }
                        }
                    }

                    //체중 데이터 동기화
                    weightData.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(context));

                    int nWSDataCount = wsService.searchTotalCount();

                    if (nWSDataCount < 1) {
                        //DB 데이터 없을 시 Server 데이터 조회

                        resultWSJSON = wsService.syncMeasureData(weightData);

                        if (resultWSJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                            if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultWSJSON.getString(ManagerConstants.ResponseParamName.RESULT)
                                    .toString())) {

                                if (resultWSJSON.has(ManagerConstants.ResponseParamName.WS_LIST)) {

                                    JSONArray resultWSJSONArray =
                                            new JSONArray(resultWSJSON.getString(ManagerConstants.ResponseParamName.WS_LIST));

                                    if (resultWSJSONArray.length() > 0) {

                                        JSONObject itemJSON;
                                        Map<String, String> requestMap;

                                        for (int i = 0, size = resultWSJSONArray.length(); i < size; i++) {

                                            publishProgress(sync_type_ws, i + 1, size);

                                            itemJSON = resultWSJSONArray.getJSONObject(i);

                                            //DB에 넣을 Data 작성
                                            requestMap = new HashMap<String, String>();
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT,
                                                    itemJSON.getString(ManagerConstants.ResponseParamName.WS_WEIGHT));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.WS_HEIGHT)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.WS_HEIGHT));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.WS_BMI)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.WS_BMI));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.WS_BMI_TYPE)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.WS_BMI_TYPE));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_SN)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_SN));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_MODEL)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_MODEL));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_COMPANY)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_COMPANY));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.WS_MESSAGE)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.WS_MESSAGE));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                                    itemJSON.getString(ManagerConstants.ResponseParamName.RECORD_DT));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_INS_DT,
                                                    itemJSON.getString(ManagerConstants.ResponseParamName.INS_DT));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                                                    ManagerConstants.ServerSyncYN.SERVER_SYNC_Y);

                                            //DB 저장
                                            int nRow = wsService.createWeighingScaleData(requestMap);

                                        }
                                    }
                                }
                            }
                        }

                    } else {

                        //DB에서 Server 미전송 체중 데이터 목록 조회
                        List<Map<String, String>> resultWSDataList = wsService.searchWeighinhScaleNotSyncDataList();

                        if (resultWSDataList.size() > 0) {

                            String strWsDbSeq = null;

                            Map<String, String> resultMap;

                            for (int i = 0, size = resultWSDataList.size(); i < size; i++) {

                                publishProgress(sync_type_ws, i + 1, size);

                                resultMap = resultWSDataList.get(i);

                                strWsDbSeq =
                                        String.valueOf(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ));

                                //Server 전송
                                weightData.put(ManagerConstants.RequestParamName.INS_DT,
                                        resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                                weightData.put(ManagerConstants.RequestParamName.WS_WEIGHT,
                                        resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                                weightData.put(ManagerConstants.RequestParamName.HEIGHT,
                                        resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT));
                                weightData.put(ManagerConstants.RequestParamName.WS_BMI,
                                        resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI));
                                weightData.put(ManagerConstants.RequestParamName.WS_BMI_TYPE,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE));
                                weightData.put(ManagerConstants.RequestParamName.SENSOR_COMPANY,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
                                weightData.put(ManagerConstants.RequestParamName.SENSOR_MODEL,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL));
                                weightData.put(ManagerConstants.RequestParamName.SENSOR_SN,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID));
                                weightData.put(ManagerConstants.RequestParamName.MESSAGE,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));
                                weightData.put(ManagerConstants.RequestParamName.RECORD_DT,
                                        ManagerUtil.convertDateFormatToServer(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT)));

                                resultWSJSON = wsService.sendWeighingScaleData(weightData);

                                if (resultWSJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultWSJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                            .toString())) {
                                        //Server 전송 완료

                                        //DB에 Server 전송 Update
                                        wsService.updateSendToServerYN(strWsDbSeq);

                                    }
                                }
                            }
                        }
                    }

                    //혈당 데이터 동기화
                    glucoseData.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(context));

                    int nGlucoseDataCount = glucoseService.searchTotalCount();

                    if (nGlucoseDataCount < 1) {
                        //DB 데이터 없을 시 Server 데이터 조회

                        resultGlucoseJSON = glucoseService.syncMeasureData(glucoseData);

                        if (resultGlucoseJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                            if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultGlucoseJSON.getString(ManagerConstants.ResponseParamName.RESULT)
                                    .toString())) {

                                if (resultGlucoseJSON.has(ManagerConstants.ResponseParamName.GLUCOSE_LIST)) {

                                    JSONArray resultGlucoseJSONArray =
                                            new JSONArray(resultGlucoseJSON.getString(ManagerConstants.ResponseParamName.GLUCOSE_LIST));

                                    if (resultGlucoseJSONArray.length() > 0) {

                                        JSONObject itemJSON;
                                        Map<String, String> requestMap;

                                        for (int i = 0, size = resultGlucoseJSONArray.length(); i < size; i++) {

                                            publishProgress(sync_type_glucose, i + 1, size);

                                            itemJSON = resultGlucoseJSONArray.getJSONObject(i);

                                            //DB에 넣을 Data 작성
                                            requestMap = new HashMap<String, String>();
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE,
                                                    itemJSON.getString(ManagerConstants.ResponseParamName.GLUCOSE));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.GLUCOSE)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.GLUCOSE_MEAL));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.GLUCOSE_TYPE)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.GLUCOSE_TYPE));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.GLUCOSE_MESSAGE)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.GLUCOSE_MESSAGE));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_SN)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_SN));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_MODEL)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_MODEL));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_COMPANY)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.SENSOR_COMPANY));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE,
                                                    "null".equals(itemJSON.getString(ManagerConstants.ResponseParamName.GLUCOSE_MESSAGE)) ? ""
                                                            : itemJSON.getString(ManagerConstants.ResponseParamName.GLUCOSE_MESSAGE));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                                                    itemJSON.getString(ManagerConstants.ResponseParamName.RECORD_DT));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_INS_DT,
                                                    itemJSON.getString(ManagerConstants.ResponseParamName.INS_DT));
                                            requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                                                    ManagerConstants.ServerSyncYN.SERVER_SYNC_Y);

                                            //DB 저장
                                            int nRow = glucoseService.createGlucoseData(requestMap);

                                        }
                                    }
                                }
                            }
                        }

                    } else {

                        //DB에서 Server 미전송 체중 데이터 목록 조회
                        List<Map<String, String>> resultGlucoseDataList = glucoseService.searchGlucoseNotSyncDataList();

                        if (resultGlucoseDataList.size() > 0) {

                            String strWsDbSeq = null;

                            Map<String, String> resultMap;

                            for (int i = 0, size = resultGlucoseDataList.size(); i < size; i++) {

                                publishProgress(sync_type_glucose, i + 1, size);

                                resultMap = resultGlucoseDataList.get(i);

                                strWsDbSeq =
                                        String.valueOf(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ));

                                //Server 전송
                                weightData.put(ManagerConstants.RequestParamName.INS_DT,
                                        resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                                weightData.put(ManagerConstants.RequestParamName.GLUCOSE,
                                        resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));
                                weightData.put(ManagerConstants.RequestParamName.GLUCOSE_MEAL,
                                        resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL));
                                weightData.put(ManagerConstants.RequestParamName.GLUCOSE_TYPE,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE));
                                weightData.put(ManagerConstants.RequestParamName.SENSOR_COMPANY,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
                                weightData.put(ManagerConstants.RequestParamName.SENSOR_MODEL,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL));
                                weightData.put(ManagerConstants.RequestParamName.SENSOR_SN,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID));
                                weightData.put(ManagerConstants.RequestParamName.MESSAGE,
                                        "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE)) ? ""
                                                : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));
                                weightData.put(ManagerConstants.RequestParamName.RECORD_DT,
                                        ManagerUtil.convertDateFormatToServer(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT)));

                                resultWSJSON = wsService.sendWeighingScaleData(weightData);

                                if (resultWSJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultWSJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                            .toString())) {
                                        //Server 전송 완료

                                        //DB에 Server 전송 Update
                                        glucoseService.updateSendToServerYN(strWsDbSeq);

                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mConnectionProgressDialog != null && mConnectionProgressDialog.isShowing()) {
                try {
                    mConnectionProgressDialog.dismiss();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }

            hideLodingProgress();

            if (sbpDBSync != null) {
                sbpDBSync.cancel(true);
            }
            sbpDBSync = new SearchBloodPressureDBSync();
            sbpDBSync.execute();

        }
    }

    /**
     * 혈당 식전 식후 수정
     */
    private class ModGlucoseMeal extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();
            String strMeal = "";
            try {

                //DB 메세지 업데이트
                if (getString(R.string.glucose_main_txt_meal_before).equals(mainGlucoseModel.getMealResult())) {
                    strMeal = ManagerConstants.EatType.GLUCOSE_BEFORE;
                } else {
                    strMeal = ManagerConstants.EatType.GLUCOSE_AFTER;
                }
                String strTypeValue = HealthcareUtil.getGlucoseType(MainActivity.this,
                        responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE),
                        strMeal);
                int nRow =
                        glucoseService.updateMeal(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ),
                                strMeal,
                                strTypeValue);

                if (nRow > 0) {

                    //Server 전송
                    data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(MainActivity.this));
                    data.put(ManagerConstants.RequestParamName.INS_DT,
                            responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                    data.put(ManagerConstants.RequestParamName.GLUCOSE,
                            responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));
                    data.put(ManagerConstants.RequestParamName.GLUCOSE_MEAL, strMeal);
                    data.put(ManagerConstants.RequestParamName.GLUCOSE_TYPE,
                            responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE));
                    data.put(ManagerConstants.RequestParamName.RECORD_DT,
                            responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));

                    resultJSON = glucoseService.modifyMeasureData(data);

                    if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                .toString())) {

                            //DB에 Server 전송 Update
                            responesGlucoseMap.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL, strMeal);
                            responesGlucoseMap.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE, strTypeValue);
                            glucoseService.updateSendToServerYN(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ));
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {
            hideLodingProgress();
            try {
                if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {
                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                            .toString())) {
                        if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL))) {
                            mainGlucoseModel.setMealResult(getString(R.string.glucose_main_txt_meal_before));
                        } else {
                            mainGlucoseModel.setMealResult(getString(R.string.glucose_main_txt_meal_after));
                        }

                        if (HealthcareConstants.GlucoseState.GLUCOSE_LOW.equals(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE))) {
                            //저혈당
                            binding.layoutGlucose.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_e6ea47));
                            setViewAnimation(ManagerConstants.PageType.TYPE_GLUCOSE, 1);
                            mainGlucoseModel.setGlucoseType(1);
                            mainGlucoseModel.setGlucoseResult(getResources().getString(R.string.glucose_main_txt_status_low));
                        } else if (HealthcareConstants.GlucoseState.GLUCOSE_NORMAL.equals(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE))) {
                            //정상
                            binding.layoutGlucose.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                            setViewAnimation(ManagerConstants.PageType.TYPE_GLUCOSE, 2);
                            mainGlucoseModel.setGlucoseType(2);
                            mainGlucoseModel.setGlucoseResult(getResources().getString(R.string.glucose_main_txt_status_normal));
                        } else if (HealthcareConstants.GlucoseState.GLUCOSE_OVER.equals(responesGlucoseMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE))) {
                            //고혈당
                            binding.layoutGlucose.ccvMeasure.setCircleColor(getResources().getColor(R.color.color_f06515));
                            setViewAnimation(ManagerConstants.PageType.TYPE_GLUCOSE, 3);
                            mainGlucoseModel.setGlucoseType(3);
                            mainGlucoseModel.setGlucoseResult(getResources().getString(R.string.glucose_main_txt_status_over));
                        }
                        binding.layoutGlucose.setMainGlucose(mainGlucoseModel);
                    } else {
                    }
                } else {
                }

            } catch (Exception e) {
                e.printStackTrace();

                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }

    /**
     * 레포트 데이터
     */
    private class SearchReport extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected JSONObject doInBackground(String... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();
            String strGlugoseUnit = "";
            String strWeightnit = "";
            if (ManagerConstants.Unit.MMOL.equals(PreferenceUtil.getGlucoseUnit(MainActivity.this))) {
                strGlugoseUnit = "mmol";
            } else {
                strGlugoseUnit = ManagerConstants.Unit.MGDL;
            }
            if (ManagerConstants.Unit.LBS.equals(PreferenceUtil.getWeightUnit(MainActivity.this))) {
                strWeightnit = "lb";
            } else {
                strWeightnit = ManagerConstants.Unit.KG;
            }
            try {

                //Server 전송
                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(context));
                data.put(ManagerConstants.RequestParamName.PERIOD, params[0]);
                data.put(ManagerConstants.RequestParamName.END_DT, DateUtil.getDateNow("yyyyMMdd"));
                data.put(ManagerConstants.RequestParamName.DAYS_YN, "N");
                data.put(ManagerConstants.RequestParamName.GRADE_YN, "N");
                data.put(ManagerConstants.RequestParamName.DIST_YN, "N");
                if (PreferenceUtil.getUsingBloodGlucose(context)) {
                    data.put(ManagerConstants.RequestParamName.BG_YN,
                            ManagerConstants.UsingBloodGlucoseYN.USING_BLOODGLUCOSE_Y);
                } else {
                    data.put(ManagerConstants.RequestParamName.BG_YN,
                            ManagerConstants.UsingBloodGlucoseYN.USING_BLOODGLUCOSE_N);
                }

                data.put(ManagerConstants.RequestParamName.WEIGHT_UNIT, strWeightnit);
                data.put(ManagerConstants.RequestParamName.GLUCOSE_UNIT, strGlugoseUnit);

                resultJSON = reportService.searchReport(data);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {
            hideLodingProgress();

            try {

                if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                            .toString())) {
                        StringBuilder builder = new StringBuilder("(");

                        if (resultJSON.has(ManagerConstants.ResponseParamName.START_DT)) {
                            if (ManagerUtil.mapDataNullCheck(resultJSON.get(ManagerConstants.ResponseParamName.START_DT))) {
                                String[] arrDate = null;
                                if (ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)) == ManagerUtil.Localization.TYPE_01) {
                                    arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_01,
                                            ManagerUtil.ShowFormatPosition.MINUTE,
                                            true,
                                            "/",
                                            null,
                                            "yyyyMMdd",
                                            resultJSON.getString(ManagerConstants.ResponseParamName.START_DT)
                                                    .replace(".", ""));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    builder.append(arrSplitDate[0] + "/"
                                            + arrSplitDate[1]
                                            + "/"
                                            + arrSplitDate[2]
                                            + " ~ ");
                                } else if (ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)) == ManagerUtil.Localization.TYPE_02) {
                                    String aaa = resultJSON.getString(ManagerConstants.ResponseParamName.START_DT)
                                            .replace(".", "");
                                    arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_02,
                                            ManagerUtil.ShowFormatPosition.MINUTE,
                                            true,
                                            "/",
                                            null,
                                            "yyyyMMdd",
                                            resultJSON.getString(ManagerConstants.ResponseParamName.START_DT)
                                                    .replace(".", ""));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    builder.append(arrSplitDate[0] + "/"
                                            + arrSplitDate[1]
                                            + "/"
                                            + arrSplitDate[2]
                                            + " ~ ");
                                } else {
                                    arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_03,
                                            ManagerUtil.ShowFormatPosition.MINUTE,
                                            true,
                                            "/",
                                            null,
                                            "yyyyMMdd",
                                            resultJSON.getString(ManagerConstants.ResponseParamName.START_DT)
                                                    .replace(".", ""));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    builder.append(arrSplitDate[1] + "/"
                                            + arrSplitDate[0]
                                            + "/"
                                            + arrSplitDate[2]
                                            + " ~ ");
                                }
                            }
                        }
                        if (resultJSON.has(ManagerConstants.ResponseParamName.END_DT)) {
                            if (ManagerUtil.mapDataNullCheck(resultJSON.get(ManagerConstants.ResponseParamName.END_DT))) {
                                String[] arrDate = null;
                                if (ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)) == ManagerUtil.Localization.TYPE_01) {
                                    arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_01,
                                            ManagerUtil.ShowFormatPosition.MINUTE,
                                            true,
                                            "/",
                                            null,
                                            "yyyyMMdd",
                                            resultJSON.getString(ManagerConstants.ResponseParamName.END_DT)
                                                    .replace(".", ""));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    builder.append(arrSplitDate[0] + "/" + arrSplitDate[1] + "/" + arrSplitDate[2]);
                                } else if (ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)) == ManagerUtil.Localization.TYPE_02) {
                                    arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_02,
                                            ManagerUtil.ShowFormatPosition.MINUTE,
                                            true,
                                            "/",
                                            null,
                                            "yyyyMMdd",
                                            resultJSON.getString(ManagerConstants.ResponseParamName.END_DT)
                                                    .replace(".", ""));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    builder.append(arrSplitDate[0] + "/" + arrSplitDate[1] + "/" + arrSplitDate[2]);
                                } else {
                                    arrDate = ManagerUtil.getDateCharacter(ManagerUtil.Localization.TYPE_03,
                                            ManagerUtil.ShowFormatPosition.MINUTE,
                                            true,
                                            "/",
                                            null,
                                            "yyyyMMdd",
                                            resultJSON.getString(ManagerConstants.ResponseParamName.END_DT)
                                                    .replace(".", ""));
                                    String[] arrSplitDate = arrDate[0].split("/");
                                    builder.append(arrSplitDate[1] + "/" + arrSplitDate[0] + "/" + arrSplitDate[2]);
                                }
                            }
                        }
                        builder.append(")");
                        binding.layoutReport.tvStartEndDay.setText(builder);
                        responesReportMap = resultJSON;
                        pageAdapter = new ReportPageAdapter();
                        mCustomViewPager.setAdapter(pageAdapter);
                        mCustomViewPager.setCurrentItem(reportViewNum);
                    } else {
                        if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_I.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                .toString())) {
                            DefaultOneButtonDialog defaultOneButtonDialog =
                                    new DefaultOneButtonDialog(context,
                                            "",
                                            getString(R.string.common_required_value_error_comment),
                                            getString(R.string.common_txt_confirm),
                                            new IDefaultOneButtonDialog() {

                                                @Override
                                                public void
                                                onConfirm() {
                                                }
                                            });
                            defaultOneButtonDialog.show();
                        } else if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_E.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                .toString())) {
                            DefaultOneButtonDialog defaultOneButtonDialog =
                                    new DefaultOneButtonDialog(context,
                                            "",
                                            getString(R.string.common_error_comment),
                                            getString(R.string.common_txt_confirm),
                                            new IDefaultOneButtonDialog() {

                                                @Override
                                                public void
                                                onConfirm() {
                                                }
                                            });
                            defaultOneButtonDialog.show();
                        } else if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_N.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                .toString())) {
                            DefaultDialog defaultDialog = new DefaultDialog(context,
                                    "",
                                    getString(R.string.search_fail_common_no_account),
                                    getString(R.string.common_txt_cancel),
                                    getString(R.string.common_sign_up),
                                    new IDefaultDialog() {

                                        @Override
                                        public void onCancel() {
                                        }

                                        @Override
                                        public void onConfirm() {
                                            Intent intent =
                                                    new Intent(context,
                                                            SignUpActivity.class);
                                            startActivity(intent);
                                            overridePendingTransition(0, 0);
                                        }
                                    });
                            defaultDialog.show();
                        }

                        //다이얼로그
                    }
                } else {
                    //다이얼로그
                    DefaultOneButtonDialog defaultOneButtonDialog =
                            new DefaultOneButtonDialog(context,
                                    "",
                                    getString(R.string.common_error_comment),
                                    getString(R.string.common_txt_confirm),
                                    new IDefaultOneButtonDialog() {

                                        @Override
                                        public void
                                        onConfirm() {
                                        }
                                    });
                    defaultOneButtonDialog.show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class ReportPageAdapter extends PagerAdapter {

        public ReportPageAdapter() {
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            int realPos = position % reportCount;
            View v = null;

            if (realPos == 0) {
                v = new ReportVarView(context, MainActivity.this, responesReportMap, searchDay);
            } else if (realPos == 1) {
                v = new ReportAvgView(context, MainActivity.this, responesReportMap, searchDay);
            }

            if (v != null) {
                ((ViewPager) container).addView(v, 0);
            }

            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public void startBLEService() {
        //        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (bluetoothAdapter == null) {
            return;
        }
        if (bleService == null) {
            bleService = new BlueToothBLEService(context);
        }
        int UA651Count = 0;
        int MISCALECount = 0;
        String UA651Address = "";
        String MISCALEAddress = "";
        pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device != null && device.getName() != null && device.getAddress() != null) {
                String deviceName = device.getName();
                if (deviceName.startsWith("A&D_UA-651BLE")) {
                    UA651Count++;
                    UA651Address = device.getAddress();
                    //                    bleService.serviceManager("A&D_UA-651BLE", device.getAddress(), bleDeviceInterface, "start");
                } else if ("MI_SCALE".equals(deviceName)) {
                    MISCALECount++;
                    MISCALEAddress = device.getAddress();
                    //                    bleService.serviceManager("MI_SCALE", device.getAddress(), bleDeviceInterface, "start");
                }
            }
        }
        if (UA651Count == 1) {
            bleService.serviceBPManager("A&D_UA-651BLE", UA651Address, new BlueToothBPBLEDeviceInterface() {

                @Override
                public ReturnCode onSuccess(String measurDeviceType, ReturnCode rc) {
                    if (BleConstants.MEASUR_DEVICE_TYPE_BLOOD_PRESSURE.equals(measurDeviceType)) {
                        if (!"BLUETOOTH_CONNECT_SUCCESS".equals(rc.toString())) {
                            Toast.makeText(context,
                                    getResources().getString(R.string.device_connect_ble_bp),
                                    Toast.LENGTH_SHORT)
                                    .show();
                            //                            if (currentFragment == bpFragment && currentFragment != myDeviceSelectFragment) {
                            //                                (final Context context, BluetoothManager bluetoothManager(BloodPressureMainFragment) currentFragment).setProgressShow();
                            //                                timerUtil.start();
                            //                            }
                        }
                    }
                    return rc;
                }

                @Override
                public void onSuccess(int cnt, String measureType, String[] data) {
                    if ("BloodPressure".equals(measureType)) {
                        if (cnt <= 1) {
                            Toast.makeText(context,
                                    getResources().getString(R.string.saved_bp_data_01),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        } else if (cnt > 1) {
                            Toast.makeText(context,
                                    getResources().getString(R.string.saved_bp_data_02) + " " + cnt + " " + getResources().getString(R.string.saved_bp_data_03),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }
                }

                @Override
                public void onFail(String measurDeviceType, String errorCode) {
                    Log.d("mspark", "에러코드 : " + errorCode);
                }
            }, "start");
        } else if (UA651Count > 1) {
            //연결된 UA651 하나 이상 연결되어 있음
        } else {
            //연결된 UA651 없음
        }
        if (MISCALECount == 1) {
            binding.layoutWs.llMeasureInfo.setVisibility(View.VISIBLE);
            bleService.serviceWSManager("MI_SCALE", MISCALEAddress, new BlueToothWSBLEDeviceInterface() {

                @Override
                public ReturnCode onSuccess(String measurDeviceType, ReturnCode rc) {
                    if (BleConstants.MEASUR_DEVICE_TYPE_WEIGHT_SCALE.equals(measurDeviceType)) {
                        if (!"BLUETOOTH_CONNECT_SUCCESS".equals(rc.toString())) {
                            Toast.makeText(context,
                                    getResources().getString(R.string.device_connect_ble_ws),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                    return null;
                }

                @Override
                public void onSuccess(int cnt, String measureType, String weight) {
                    if ("Weight".equals(measureType)) {
                        if (cnt <= 1) {
                            Toast.makeText(context,
                                    getResources().getString(R.string.saved_ws_data),
                                    Toast.LENGTH_SHORT)
                                    .show();
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("checkType", "WS");
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(intent);
                            ((Activity) context).finish();
                        }
                    }
                }

                @Override
                public void onFail(String measurDeviceType, String errorCode) {
                    Log.i("1234", "1234 fail");
                }

            }, "start");
        } else if (MISCALECount > 1) {
            //연결된 MI_SCALE 하나 이상 연결되어 있음
        } else {
            //연결된 MI_SCALE 없음
            binding.layoutWs.llMeasureInfo.setVisibility(View.INVISIBLE);
        }
    }

    public Set<BluetoothDevice> getPairedDevices() {
        return pairedDevices;
    }

    public Animation getAnimation(int time, int width) {
        TranslateAnimation translateAnimation = new TranslateAnimation(width, 0, 0, 0);
        translateAnimation.setStartOffset(time);
        translateAnimation.setInterpolator(new BounceInterpolator());

        translateAnimation.setDuration(700);
        return translateAnimation;

    }

    public void setEventdialog(final EventInfoData eventInfoData) {
        AnalyticsUtil.sendScene(MainActivity.this, "3_이벤트팝업");
        DefaultAdsDialog da = new DefaultAdsDialog(context,
                eventInfoData.getSeq(),
                eventInfoData.getSnooze(),
                eventInfoData.getType(),
                eventInfoData.getUrl(),
                new IDefaultAdsDialog() {

                    @Override
                    public void onConfirm(String strSeq, String strViewTerm) {
                        long now = System.currentTimeMillis();
                        Date date = new Date(now);
                        date.setDate(date.getDate()
                                + Integer.parseInt(eventInfoData.getSnooze()));
                        SimpleDateFormat dateFormat =
                                new SimpleDateFormat("yyyyMMddHHmmss",
                                        java.util.Locale.getDefault());
                        String strDate = dateFormat.format(date);
                        long snoozTime = 0;
                        if ("0".equals(eventInfoData.getSnooze())) {
                            snoozTime =
                                    DateUtil.getMilliSecondDate(eventInfoData.getEndDt());
                        } else {
                            snoozTime = DateUtil.getMilliSecondDate(strDate);
                        }

                        addEventAds(strSeq, snoozTime + "");
                    }

                    @Override
                    public void onClick(String strType) {
                        if (strType.equals("MB")) {
                            gotoNavigationBp();
                        } else if (strType.equals("MW")) {
                            gotoNavigationWS();
                            //                    ((MainActivity) context).onFragmentChange(ManagerConstants.MainMenuCode.MAIN_WS_MAIN);
                        } else if (strType.equals("MG")) {
                            if (PreferenceUtil.getUsingBloodGlucose(MainActivity.this)) {
                                gotoNavigationGlucose();
                            } else {
                                Intent intent = new Intent(MainActivity.this, SettingProfileActivity.class);
                                startActivity(intent);
                            }
                            //                    ((MainActivity) context).onFragmentChange(ManagerConstants.MainMenuCode.MAIN_WS_MAIN);
                        } else if (strType.equals("P")) {
                            Intent intent = new Intent(MainActivity.this, SettingProfileActivity.class);
                            startActivity(intent);
                            //                    ((MainActivity) context).onFragmentChange(ManagerConstants.MainMenuCode.MAIN_PROFILE);
                        } else if (strType.equals("N")) {
                            Intent intent = new Intent(MainActivity.this,
                                    NoticeActivity.class);
                            startActivity(intent);
                            //                    ((MainActivity) context).onFragmentChange(ManagerConstants.MainMenuCode.MAIN_NOTICE);
                        } else if (strType.equals("ST")) {
                            Intent intent = new Intent(MainActivity.this,
                                    OnlineShopActivity.class);
                            startActivity(intent);
                            //                    ((MainActivity) context).onFragmentChange(ManagerConstants.MainMenuCode.MAIN_MARKET);
                        } else if (strType.equals("WB")) {
                            Intent intent =
                                    new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(eventInfoData.getUrl()));
                            startActivity(intent);
                        } else if (strType.equals("W")) {
                            Intent intent =
                                    new Intent(Intent.ACTION_VIEW,
                                            Uri.parse(eventInfoData.getUrl()));
                            startActivity(intent);
                        } else if (strType.equals("M")) {
                            Intent intent =
                                    new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("https://play.google.com/store/apps/details?id=kr.co.openit.bpdiary"));
                            startActivity(intent);
                        } else if (strType.equals("C")) {
                        }
                        overridePendingTransition(0, 0);
                    }
                });
        da.show();
        adsFlag = false;
    }

    public void addEventAds(String strSeq, String strSnooze) {
        if (PreferenceUtil.getEventAdsOneSeq(context).isEmpty()) {
            PreferenceUtil.setEventAdsOne(context, strSeq, strSnooze);
        } else if (PreferenceUtil.getEventAdsTwoSeq(context).isEmpty()) {
            PreferenceUtil.setEventAdsTwo(context, strSeq, strSnooze);
        } else if (PreferenceUtil.getEventAdsThreeSeq(context).isEmpty()) {
            PreferenceUtil.setEventAdsThree(context, strSeq, strSnooze);
        } else if (PreferenceUtil.getEventAdsFourSeq(context).isEmpty()) {
            PreferenceUtil.setEventAdsFour(context, strSeq, strSnooze);
        } else if (PreferenceUtil.getEventAdsFiveSeq(context).isEmpty()) {
            PreferenceUtil.setEventAdsFive(context, strSeq, strSnooze);
        }
    }

    @Override
    public void onBackPressed() {

        if (mainViewModel.getScreenType() == 1) {
            if (!isBackPressed) {
                isBackPressed = true;
                Toast.makeText(getApplicationContext(), getString(R.string.common_txt_exit_comment), Toast.LENGTH_SHORT)
                        .show();
                new CountDownTimer(1000, 1000) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        isBackPressed = false;
                    }
                }.start();
            } else {
                finish();
            }
        } else {
            gotoNavigationBp();
        }
    }

    public void setBroadcastReceiver() {
        IntentFilter contentReceiverFilter = new IntentFilter();
        contentReceiverFilter.addAction("kr.co.openit.bpdiary.report");
        registerReceiver(mBroadcastReceiver, contentReceiverFilter);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                searchReportNetworkCheck();
            }
        }
    };

    public boolean isNewWsData() {
        return isNewWsData;
    }

}
