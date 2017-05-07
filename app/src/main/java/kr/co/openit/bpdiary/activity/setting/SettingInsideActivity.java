package kr.co.openit.bpdiary.activity.setting;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.customview.CustomSwitch;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class SettingInsideActivity extends NonMeasureActivity implements View.OnClickListener {

    private LinearLayout llEmptyView;

    /**
     * 공지사항 Layout
     */
    private RelativeLayout rlNotice;

    /**
     * 삼성 S 헬스 사용 여부 Switch
     */
//    private SwitchCompat switchSHealth;

    private CustomSwitch switchSHealth;
    /**
     * 구글 피트니스 사용 여부 Switch
     */
//    private SwitchCompat switchGoogleFit;
    private CustomSwitch switchGoogleFit;
    /**
     * S 헬스
     */
    private Set<HealthPermissionManager.PermissionKey> keySet;
    private HealthDataStore mStore;
    private HealthPermissionManager.PermissionKey mPermissionKeyBP;
    private HealthPermissionManager.PermissionKey mPermissionKeyWS;

    /**
     * Google Fit
     */
    private boolean authInProgress = false;
    private GoogleApiClient mClient = null;
    private static final String AUTH_PENDING = "auth_state_pending";

    private LinearLayout llAds;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    protected void onPause() {
        super.onPause();

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_inside);

        initToolbar(getString(R.string.setting_activity_setting));

        AnalyticsUtil.sendScene(SettingInsideActivity.this, "3_M 설정메인");

        /**
         * 광고
         */
        llAds = (LinearLayout) findViewById(R.id.ll_ads);
        AdView adView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(SettingInsideActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        /**
         * S 헬스
         */
        keySet = new HashSet<HealthPermissionManager.PermissionKey>();
        mPermissionKeyBP = new HealthPermissionManager.PermissionKey(HealthConstants.BloodPressure.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.WRITE);
        mPermissionKeyWS = new HealthPermissionManager.PermissionKey(HealthConstants.Weight.HEALTH_DATA_TYPE, HealthPermissionManager.PermissionType.WRITE);
        keySet.add(mPermissionKeyBP);
        keySet.add(mPermissionKeyWS);

        HealthDataService healthDataService = new HealthDataService();

        try {
            healthDataService.initialize(SettingInsideActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mStore = new HealthDataStore(SettingInsideActivity.this, mConnectionListener);


        /**
         * Google Fit
         */
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        buildFitnessClient();

        setLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

//        checkLanguage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ManagerConstants.ActivityResultCode.GOOGLE_FIT_REQUEST_OAUTH) {
            authInProgress = false;
            if (Activity.RESULT_OK == resultCode) {

                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                } else {
                    setGoogleFit(false);
                }
            } else {
                setGoogleFit(false);
            }
        }
    }

    private void setLayout() {

        llEmptyView = (LinearLayout) findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        rlNotice = (RelativeLayout) findViewById(R.id.rl_notice);
        switchSHealth = (CustomSwitch) findViewById(R.id.switch_s_health);
        switchGoogleFit = (CustomSwitch) findViewById(R.id.switch_google_fitness);

        switchSHealth.addSwitchObserver(new CustomSwitch.CustomSwitchObserver() {
            @Override
            public void onCheckStateChange(CustomSwitch switchView, boolean isChecked) {
                AnalyticsUtil.sendEvent(SettingInsideActivity.this, "설정", "Event", "3_M S헬스");
                boolean isSHealth = PreferenceUtil.getSHealth(SettingInsideActivity.this);

                if (isSHealth) {

                    mStore.connectService();

                    HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);

                    try {
                        pmsManager.requestPermissions(keySet).setResultListener(mPermissionListener);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    mStore.connectService();
                }
            }
        });
        switchGoogleFit.addSwitchObserver(new CustomSwitch.CustomSwitchObserver() {
            @Override
            public void onCheckStateChange(CustomSwitch switchView, boolean isChecked) {
                showLodingProgress();
                AnalyticsUtil.sendEvent(SettingInsideActivity.this, "설정", "Event", "3_M Google 피트니스");
                boolean isGoogleFit = PreferenceUtil.getGoogleFit(context);

                if (!isGoogleFit) {

                    if (PreferenceUtil.getIsGoogleFitSkip(context)) {
                        mClient.connect();
                    } else {
                        setGoogleFit(false);
                        PreferenceUtil.setIsGoogleFitSkip(SettingInsideActivity.this, true);
//                        GoogleFitDialog googleFitDialog = new GoogleFitDialog(SettingInsideActivity.this, new IDefaultDialog() {
//                            @Override
//                            public void onConfirm() {
//                                mClient.connect();
//                            }
//
//                            @Override
//                            public void onCancel() {
//                                //Skip
//
//                            }
//                        });
//
//                        googleFitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                hideLodingProgress();
//                            }
//                        });
//                        googleFitDialog.show();
                    }
                } else {
                    try {
                        PendingResult<Status> pendingResult = Fitness.ConfigApi.disableFit(mClient);
                        pendingResult.setResultCallback(new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {

                                if (status.isSuccess()) {
                                    mClient.disconnect();
                                    setGoogleFit(false);
                                } else {
                                    //nothing
                                }
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        mClient.disconnect();
                        setGoogleFit(false);
                    }
                }
            }
        });
//        switchSHealth.setOnClickListener(this);
//        switchGoogleFit.setOnClickListener(this);
        rlNotice.setOnClickListener(this);

        switchSHealth.setDefaultChecked(PreferenceUtil.getSHealth(SettingInsideActivity.this));
        switchGoogleFit.setDefaultChecked(PreferenceUtil.getGoogleFit(SettingInsideActivity.this));
    }

    @Override
    public void onClick(View v) {
        if (!ManagerUtil.isClicking()) {

            Intent intent;
            switch (v.getId()) {
//            case R.id.switch_s_health: //삼성 s 헬스 선택
//                AnalyticsUtil.sendEvent(SettingInsideActivity.this, "설정", "Event", "3_M S헬스");
//                boolean isSHealth = PreferenceUtil.getSHealth(SettingInsideActivity.this);
//
//                if (isSHealth) {
//
//                    mStore.connectService();
//
//                    HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);
//
//                    try {
//                        pmsManager.requestPermissions(keySet).setResultListener(mPermissionListener);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                } else {
//                    mStore.connectService();
//                }
//                break;
//            case R.id.switch_google_fitness: //구글 피트니스 선택
//                showLodingProgress();
//                AnalyticsUtil.sendEvent(SettingInsideActivity.this, "설정", "Event", "3_M Google 피트니스");
//                boolean isGoogleFit = PreferenceUtil.getGoogleFit(context);
//
//                if (!isGoogleFit) {
//
//                    if (PreferenceUtil.getIsGoogleFitSkip(context)) {
//                        mClient.connect();
//                    } else {
//                        GoogleFitDialog googleFitDialog = new GoogleFitDialog(SettingInsideActivity.this, new IDefaultDialog() {
//                            @Override
//                            public void onConfirm() {
//                                mClient.connect();
//                            }
//
//                            @Override
//                            public void onCancel() {
//                                //Skip
//                                setGoogleFit(false);
//                                PreferenceUtil.setIsGoogleFitSkip(SettingInsideActivity.this, true);
//                            }
//                        });
//
//                        googleFitDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                            @Override
//                            public void onCancel(DialogInterface dialog) {
//                                hideLodingProgress();
//                            }
//                        });
//                        googleFitDialog.show();
//                    }
//                } else {
//                    try {
//                        PendingResult<Status> pendingResult = Fitness.ConfigApi.disableFit(mClient);
//                        pendingResult.setResultCallback(new ResultCallback<Status>() {
//                            @Override
//                            public void onResult(Status status) {
//
//                                if (status.isSuccess()) {
//                                    mClient.disconnect();
//                                    setGoogleFit(false);
//                                } else {
//                                    //nothing
//                                }
//                            }
//                        });
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        mClient.disconnect();
//                        setGoogleFit(false);
//                    }
//                }
//                break;
                case R.id.rl_notice: //공지사항 클릭
                    intent = new Intent(SettingInsideActivity.this, NoticeActivity.class);
                    startActivity(intent);

                    break;

            }
        }
    }


    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {
        @Override
        public void onConnected() {
            // The connection is successful.
            // Acquires the required permission
            HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);

            try {
                pmsManager.requestPermissions(keySet).setResultListener(mPermissionListener);
            } catch (Exception e) {
                // Error handling
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {

            setSHealth(false);
            setSHealthBP(false);
            setSHealthWS(false);

            if(!SettingInsideActivity.this.isFinishing()) {
                AlertDialog.Builder alertDlg = new AlertDialog.Builder(SettingInsideActivity.this);
                alertDlg.setTitle(getString(R.string.common_txt_noti));
                alertDlg.setMessage(getString(R.string.common_txt_s_health_device));
                alertDlg.setPositiveButton(getString(R.string.common_txt_confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing
                    }
                });

                alertDlg.show();
            }
            // Resolve error if the connection fails
            //            error.resolve(getActivity());
        }

        @Override
        public void onDisconnected() {
            // The connection is disconnected
        }
    };
    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener = new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {
        @Override
        public void onResult(HealthPermissionManager.PermissionResult result) {

            Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = result.getResultMap();

            if (resultMap.get(mPermissionKeyBP) == Boolean.TRUE || resultMap.get(mPermissionKeyWS) == Boolean.TRUE) {

                setSHealth(true);

                if (resultMap.get(mPermissionKeyBP) == Boolean.TRUE) {
                    setSHealthBP(true);
                }

                if (resultMap.get(mPermissionKeyWS) == Boolean.TRUE) {
                    setSHealthWS(true);
                }

            } else if (resultMap.get(mPermissionKeyBP) == Boolean.FALSE && resultMap.get(mPermissionKeyWS) == Boolean.FALSE) {

                setSHealth(false);
                setSHealthBP(false);
                setSHealthWS(false);

                mStore.disconnectService();

            } else {
                // The requested permission is acquired.
            }
        }
    };


    /**
     * S 헬스 설정
     */
    private void setSHealth(boolean isSHealth) {
        //true : 연동 됨, false : 연동 안됨

        PreferenceUtil.setSHealth(SettingInsideActivity.this, isSHealth);
        switchSHealth.setChecked(isSHealth);

    }

    /**
     * S 헬스 BP 설정
     */
    private void setSHealthBP(boolean isSHealthBP) {
        //true : 연동 됨, false : 연동 안됨
        PreferenceUtil.setSHealthBP(SettingInsideActivity.this, isSHealthBP);
    }

    /**
     * S 헬스 WS 설정
     */
    private void setSHealthWS(boolean isSHealthWS) {
        //true : 연동 됨, false : 연동 안됨
        PreferenceUtil.setSHealthWS(SettingInsideActivity.this, isSHealthWS);
    }


    /**
     * 구글 피트니스 설정
     */
    private void setGoogleFit(boolean isGoogleFit) {
        //true : 연동 됨, false : 연동 안됨

        PreferenceUtil.setGoogleFit(context, isGoogleFit);
        switchGoogleFit.setChecked(isGoogleFit);

        hideLodingProgress();
    }

    private void buildFitnessClient() {
        //Create the Google API Client
        mClient = new GoogleApiClient.Builder(SettingInsideActivity.this)
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.CONFIG_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        setGoogleFit(true);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                        } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                        }
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    // Called whenever the API client fails to connect.
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        if (!result.hasResolution()) {
                            // Show the localized error dialog
                            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                    SettingInsideActivity.this,
                                    0)
                                    .show();
                            return;
                        }

                        // The failure has a resolution. Resolve it.
                        // Called typically when the app is not yet authorized, and an
                        // authorization dialog is displayed to the user.
                        if (!authInProgress) {

                            try {
                                authInProgress = true;
                                result.startResolutionForResult(SettingInsideActivity.this,
                                        ManagerConstants.ActivityResultCode.GOOGLE_FIT_REQUEST_OAUTH);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                })
                .build();
    }
}
