package kr.co.openit.bpdiary.activity.bp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.SessionInsertRequest;
import com.samsung.android.sdk.healthdata.HealthConnectionErrorResult;
import com.samsung.android.sdk.healthdata.HealthConstants;
import com.samsung.android.sdk.healthdata.HealthData;
import com.samsung.android.sdk.healthdata.HealthDataResolver;
import com.samsung.android.sdk.healthdata.HealthDataService;
import com.samsung.android.sdk.healthdata.HealthDataStore;
import com.samsung.android.sdk.healthdata.HealthDeviceManager;
import com.samsung.android.sdk.healthdata.HealthPermissionManager;
import com.samsung.android.sdk.healthdata.HealthResultHolder;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.customview.WheelNumberPicker;
import kr.co.openit.bpdiary.customview.WheelPicker;
import kr.co.openit.bpdiary.customview.WheelPickerOnResizeListener;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.services.BloodPressureService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.HealthcareUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class InputBPActivity extends NonMeasureActivity implements WheelPickerOnResizeListener {

    private LinearLayout llEmptyView;

    /**
     * 수축기 혈압 입력 요청코드
     */
    private static final int REQUEST_INPUT_BP_SYSTOLE = 100;

    /**
     * 이완기 혈압 입력 요청코드
     */
    private static final int REQUEST_INPUT_BP_DIASTOLE = 200;

    /**
     * 맥박 입력 요청코드
     */
    private static final int REQUEST_INPUT_BP_PULSE = 300;

    /**
     * 메모 입력 요청코드
     */
    private static final int REQUEST_INPUT_MEMO = 2000;

    /**
     * 혈압 타입
     */
    public static final String BP_TYPE = "bp_type";

    /**
     * 수축기
     */
    public static final int BP_SYSTOLE = 1;

    /**
     * 이완기
     */
    public static final int BP_DIASTOLE = 2;

    /**
     * 맥박
     */
    public static final int BP_PULSE = 3;

    /**
     * 수축기 NumberPicker
     */
    private WheelNumberPicker npSystole;

    /**
     * 이완기 NumberPicker
     */
    private WheelNumberPicker npDiastole;

    /**
     * 맥박 NumberPicker
     */
    private WheelNumberPicker npPulse;

    /**
     * 메모 추가 Layout
     */
    private LinearLayout llAddMemo;

    /**
     * 메모 있을 시 나타나는(메모 수정) Layout
     */
    private LinearLayout llUpdateMemo;

    /**
     * 메모 내용 TextView
     */
    private TextView tvMemoContent;

    /**
     * 저장 Button
     */

    private Button btnSave;

    /**
     * 왼팔 TextView
     */
    private TextView rbLeft;

    /**
     * 오른팔 TextView
     */
    private TextView rbRight;

    /**
     * 데이터 DB 저장용 Map
     */
    private Map<String, String> requestMap;

    /**
     * BloodPressureService
     */
    private BloodPressureService bpService;

    /**
     * 혈압 측정 Data를 DB에 저장 및 Server로 전송하는 AsyncTask
     */
    private CreateBloodPressureAsync cbpAsync;

    /**
     * Google Fit
     */
    private boolean authInProgress = false;

    private GoogleApiClient mClient = null;

    private static final String AUTH_PENDING = "auth_state_pending";

    public static final String SESSION_NAME_BLOOD_PRESSURE = "Measure Blood Pressure";

    /**
     * S Health
     */
    private HealthDataStore mStore;

    private HealthPermissionManager.PermissionKey mPermissionKey;

    private String sys = "120";

    private String dia = "90";

    private String pulse = "80";

    private String memo = "";

    private String armType = "";

    private ImageView ivCenterLine;

    private String validationMessage = "";

    private boolean isFirst;

    private static final int MOVE_PICKER_SYS = 10;

    private static final int MOVE_PICKER_DIA = 8;

    private static final int MOVE_PICKER_PULSE = 8;


    @Override
    protected void onDestroy() {
        if (mClient.isConnected()) {
            mClient.disconnect();
        }

        if (mStore != null) {
            mStore.disconnectService();
        }

        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_bp);

        AnalyticsUtil.sendScene(InputBPActivity.this, "3_혈압 입력");

        //        setStatusBarColor(this, getResources().getColor(R.color.color_efebe8));

        initToolbar(getString(R.string.bp_input_title));

        context = InputBPActivity.this;

        /**
         * blood pressure service
         */
        bpService = new BloodPressureService(InputBPActivity.this);

        /**
         * Google Fit
         */
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        buildFitnessClient();

        /**
         * S 헬스
         */
        mPermissionKey = new HealthPermissionManager.PermissionKey(HealthConstants.BloodPressure.HEALTH_DATA_TYPE,
                                                                   HealthPermissionManager.PermissionType.WRITE);

        HealthDataService healthDataService = new HealthDataService();

        try {
            healthDataService.initialize(InputBPActivity.this);
        } catch (Exception e) {
            // Handles exception
        }

        mStore = new HealthDataStore(InputBPActivity.this, mConnectionListener);

        LinearLayout layout = (LinearLayout)findViewById(R.id.ll_input_bp);

        setLayout(layout);

        isFirst = true;


    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    @Override
    public void setLayout(LinearLayout layout) {

        super.setLayout(layout);

        npSystole = (WheelNumberPicker)findViewById(R.id.picker_systole);
        npDiastole = (WheelNumberPicker)findViewById(R.id.picker_diastole);
        npPulse = (WheelNumberPicker)findViewById(R.id.picker_pulse);
        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        llAddMemo = (LinearLayout)findViewById(R.id.ll_add_memo);
        llUpdateMemo = (LinearLayout)findViewById(R.id.ll_update_memo);
        tvMemoContent = (TextView)findViewById(R.id.tv_memo_content);
        btnSave = (Button)findViewById(R.id.btn_save);
        rbLeft = (TextView)findViewById(R.id.rb_left);
        rbRight = (TextView)findViewById(R.id.rb_right);
        ivCenterLine = (ImageView)findViewById(R.id.iv_center_line);

        rbLeft.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    if (rbLeft.isSelected()) {
                        rbLeft.setSelected(false);
                        rbRight.setSelected(false);
                        ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_d7d5d6));
                    } else {
                        rbLeft.setSelected(true);
                        rbRight.setSelected(false);
                        ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_5f5b5d));
                    }
                }
            }
        });

        rbRight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (rbRight.isSelected()) {
                        rbLeft.setSelected(false);
                        rbRight.setSelected(false);
                        ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_d7d5d6));

                    } else {
                        rbLeft.setSelected(false);
                        rbRight.setSelected(true);
                        ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_5f5b5d));
                    }
                }
            }
        });

        npSystole.setOnWheelClickListener(new WheelPicker.OnWheelClickListener() {

            @Override
            public void onWheelSelected() {

                Intent intent = new Intent(InputBPActivity.this, DirectInputBPActivity.class);
                intent.putExtra(BP_TYPE, BP_SYSTOLE);
                startActivityForResult(intent, REQUEST_INPUT_BP_SYSTOLE);
            }
        });

        npSystole.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                sys = String.valueOf(number);
            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                sys = String.valueOf(number);
            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

        npDiastole.setOnWheelClickListener(new WheelPicker.OnWheelClickListener() {

            @Override
            public void onWheelSelected() {
                Intent intent = new Intent(InputBPActivity.this, DirectInputBPActivity.class);
                intent.putExtra(BP_TYPE, BP_DIASTOLE);
                startActivityForResult(intent, REQUEST_INPUT_BP_DIASTOLE);

            }
        });
        npDiastole.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                dia = String.valueOf(number);
            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                dia = String.valueOf(number);

            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

        npPulse.setOnWheelClickListener(new WheelPicker.OnWheelClickListener() {

            @Override
            public void onWheelSelected() {
                Intent intent = new Intent(InputBPActivity.this, DirectInputBPActivity.class);
                intent.putExtra(BP_TYPE, BP_PULSE);
                startActivityForResult(intent, REQUEST_INPUT_BP_PULSE);

            }
        });
        npPulse.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                pulse = String.valueOf(number);
            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                pulse = String.valueOf(number);

            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

        llAddMemo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(InputBPActivity.this, BPMemoActivity.class);
                    startActivityForResult(intent, REQUEST_INPUT_MEMO);
                }

            }
        });
        llUpdateMemo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    String memo = tvMemoContent.getText().toString();
                    Intent intent = new Intent(InputBPActivity.this, BPMemoActivity.class);
                    intent.putExtra("update", memo);
                    startActivityForResult(intent, REQUEST_INPUT_MEMO);
                }

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (rbLeft.isSelected()) {
                        armType = ManagerConstants.ArmType.BP_ARM_LEFT;
                    } else if (rbRight.isSelected()) {
                        armType = ManagerConstants.ArmType.BP_ARM_RIGHT;
                    } else {
                        armType = ManagerConstants.ArmType.BP_ARM_NOTHING;
                    }
                    if (compareDate()) {
                        makeBPManualData();
                    } else {
                        DefaultDialog deleteDialog = new DefaultDialog(InputBPActivity.this,
                                getString(R.string.common_txt_noti),
                                validationMessage,
                                getString(R.string.common_txt_cancel),
                                getString(R.string.common_txt_confirm),
                                new IDefaultDialog() {

                                    @Override
                                    public void onConfirm() {
                                    }

                                    @Override
                                    public void onCancel() {
                                        //nothing
                                    }
                                });

                        deleteDialog.show();
                    }
                }
            }
        });

        Intent intent = getIntent();

        if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS))) {
            sys = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS);
//            npSystole.setDefaultNumber(200);
            try {
                npSystole.setDefaultNumber(Integer.parseInt(sys) - MOVE_PICKER_SYS);
            }catch (NumberFormatException e){
                npSystole.setDefaultNumber(120 - MOVE_PICKER_SYS);
            }
        } else {
            npSystole.setDefaultNumber(120 - MOVE_PICKER_SYS);

        }
        if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA))) {
            dia = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA);
            npDiastole.setDefaultNumber(Integer.parseInt(dia) - MOVE_PICKER_DIA);
        } else {
            npDiastole.setDefaultNumber(90 - MOVE_PICKER_DIA);

        }
        if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE))) {
            pulse = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE);
            npPulse.setDefaultNumber(Integer.parseInt(pulse) - MOVE_PICKER_PULSE);
        } else {
            npPulse.setDefaultNumber(80 - MOVE_PICKER_PULSE);
        }

        npSystole.setOnResizeListener(this);
        npDiastole.setOnResizeListener(this);
        npPulse.setOnResizeListener(this);

    }

    private void moveNumberPicker() {

        Intent intent = getIntent();

        if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS))) {
            sys = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS);
//            npSystole.setDefaultNumber(200);
            npSystole.scrollTo(Integer.parseInt(sys));

        } else {
            npSystole.scrollTo(120);

        }
        if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA))) {
            dia = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA);
            npDiastole.scrollTo(Integer.parseInt(dia));

        } else {
            npDiastole.scrollTo(90);

        }
        if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE))) {
            pulse = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE);
            npPulse.scrollTo(Integer.parseInt(pulse));

        } else {
            npPulse.scrollTo(80);
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == ManagerConstants.ActivityResultCode.GOOGLE_FIT_REQUEST_OAUTH) {
                authInProgress = false;
                if (resultCode == RESULT_OK) {
                    // Make sure the app is not already connected or attempting to connect
                    if (!mClient.isConnecting() && !mClient.isConnected()) {
                        mClient.connect();
                    }
                }
            } else if (requestCode == REQUEST_INPUT_BP_SYSTOLE) {
                String bp = data.getStringExtra("bp");
                if (bp != null) {
                    npSystole.setDefaultNumber(Integer.parseInt(bp));
//                    npSystole.scrollTo(Integer.parseInt(bp));
                    sys = bp;
                }

            } else if (requestCode == REQUEST_INPUT_BP_DIASTOLE) {
                String bp = data.getStringExtra("bp");
                if (bp != null) {
                    npDiastole.setDefaultNumber(Integer.parseInt(bp));
                    dia = bp;
                }
            } else if (requestCode == REQUEST_INPUT_BP_PULSE) {
                String bp = data.getStringExtra("bp");
                if (bp != null) {
                    npPulse.setDefaultNumber(Integer.parseInt(bp));
                    pulse = bp;
                }
            } else if (requestCode == REQUEST_INPUT_MEMO) {
                String bpMemo = data.getStringExtra("bpMemo");
                if (!TextUtils.isEmpty(bpMemo)) {
                    llAddMemo.setVisibility(View.GONE);
                    llUpdateMemo.setVisibility(View.VISIBLE);
                    tvMemoContent.setText(bpMemo);

                    memo = bpMemo;

                    //                    String[] dummy = bpMemo.split(" ");
                    //                    for (int i = 0; i < dummy.length; i++) {
                    //                        TextView tv = new TextView(context);
                    //                        tv.setText(dummy[i]);
                    //                        llUpdateMemo.addView(tv);
                    //                    }
                    //
                    //                    llUpdateMemo.addView(btnUpdate);

                }
            }
        }
    }

    /**
     * 혈압 Data 생성
     */
    private void makeBPManualData() {

        String strMeasureDt = date;
        String strMeasureTime = time;
        String strSysValue = sys;
        String strDiaValue = dia;
        String strPulValue = pulse;
        String strTypeValue = HealthcareUtil.getBloodPressureType(InputBPActivity.this, strSysValue, strDiaValue);
        //평균혈압 = 최저혈압 + ((최고혈압 - 최저혈압) / 3)
        String strMeanValue =
                            String.valueOf(Double.parseDouble(strDiaValue)
                                           + ((Double.parseDouble(strSysValue) - Double.parseDouble(strDiaValue)) / 3));

        //        if(rgArm.getCheckedRadioButtonId() == 2131558590) {
        //
        //        } else if(rgArm.getCheckedRadioButtonId() == 2131558591) {
        //
        //        } else {
        //
        //        }

        //DB에 넣을 Data 작성
        requestMap = new HashMap<String, String>();
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS, strSysValue);
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA, strDiaValue);
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN, strMeanValue);
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE, strPulValue);
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE, strTypeValue);
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM, armType);
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID, "");
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL, "");
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER, "");
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE, memo);
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT, strMeasureDt + strMeasureTime + "00");
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_INS_DT, ManagerUtil.getCurrentDateTime());
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                       ManagerConstants.ServerSyncYN.SERVER_SYNC_N);

        //DB에 저장 및 Server로 전송 AsyncTask 실행
        cbpAsync = new CreateBloodPressureAsync();
        cbpAsync.execute();

    }

    /**
     * 현재 시간과 비교
     * 초과 시 return false
     */

    private boolean compareDate() {

        boolean isValidation = true;

        if (Long.parseLong(date + time + "00") > Long.parseLong(ManagerUtil.getCurrentDateTime())) {
            //날짜 비교
            isValidation = false;
            validationMessage = getResources().getString(R.string.manual_input_txt_val_date);
        }

        if (Integer.parseInt(date.substring(0, 4)) < 2000) {
            //날짜  연도 비교
            isValidation = false;
            validationMessage = getResources().getString(R.string.manual_input_txt_val_date);
        }

        return isValidation;
    }

    @Override
    public void onResize(int id, int xNew, int yNew, int xOld, int yOld) {
        if(isFirst) moveNumberPicker();
    }

    /**
     * 혈압 Data 등록 Async
     */
    private class CreateBloodPressureAsync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {

                String strDbSeq = null;

                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(InputBPActivity.this));

                //DB 저장
                int nRow = bpService.createBloodPressureData(requestMap);

                if (nRow > 0) {

                    strDbSeq = String.valueOf(nRow);

                    //Server 전송
                    data.put(ManagerConstants.RequestParamName.INS_DT,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                    data.put(ManagerConstants.RequestParamName.BP_SYS,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS));
                    data.put(ManagerConstants.RequestParamName.BP_DIA,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA));
                    data.put(ManagerConstants.RequestParamName.BP_MEAN,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN));
                    data.put(ManagerConstants.RequestParamName.BP_PULSE,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE));
                    data.put(ManagerConstants.RequestParamName.BP_TYPE,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE));
                    data.put(ManagerConstants.RequestParamName.BP_ARM, armType);
                    data.put(ManagerConstants.RequestParamName.SENSOR_COMPANY,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
                    data.put(ManagerConstants.RequestParamName.SENSOR_MODEL,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL));
                    data.put(ManagerConstants.RequestParamName.SENSOR_SN,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID));
                    data.put(ManagerConstants.RequestParamName.MESSAGE,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));
                    data.put(ManagerConstants.RequestParamName.RECORD_DT,
                             ManagerUtil.convertDateFormatToServer(requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT)));

                    resultJSON = bpService.sendBloodPressureData(data);

                    if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                         .toString())) {
                            //Server 전송 완료

                            //DB에 Server 전송 Update
                            bpService.updateSendToServerYN(strDbSeq);

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

                if (PreferenceUtil.getGoogleFit(InputBPActivity.this)) {
                    //구글 피트니스
                    mClient.connect();
                } else {
                    //S 헬스
                    if (PreferenceUtil.getSHealthBP(InputBPActivity.this)) {
                        mStore.connectService();
                    } else {
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this).addApi(Fitness.SENSORS_API)
                                                   .addApi(Fitness.CONFIG_API)
                                                   .addApi(Fitness.RECORDING_API)
                                                   .addApi(Fitness.HISTORY_API)
                                                   .addApi(Fitness.SESSIONS_API)
                                                   .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                                                   .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                                                   //                .addApi(Fitness.API)
                                                   //                                                   .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                                                   //                                                   .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                                                   .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                                                       @Override
                                                       public void onConnected(Bundle bundle) {
                                                           // Now you can make calls to the Fitness APIs.  What to do?
                                                           // Play with some sessions!!

                                                           new InsertAndVerifySessionTask().execute();
                                                       }

                                                       @Override
                                                       public void onConnectionSuspended(int i) {
                                                           // If your connection to the sensor gets lost at some point,
                                                           // you'll be able to determine the reason and react to it here.
                                                           if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                                               Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                                           } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                                               Log.i(TAG,
                                                                     "Connection lost.  Reason: Service Disconnected");
                                                           }

                                                           if (PreferenceUtil.getSHealthBP(InputBPActivity.this)) {
                                                               mStore.connectService();
                                                           } else {
                                                               setResult(RESULT_OK);
                                                               finish();
                                                           }
                                                       }
                                                   })
                                                   .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {

                                                       // Called whenever the API client fails to connect.
                                                       @Override
                                                       public void onConnectionFailed(ConnectionResult result) {

                                                           if (PreferenceUtil.getSHealthBP(InputBPActivity.this)) {
                                                               mStore.connectService();
                                                           } else {
                                                               setResult(RESULT_OK);
                                                               finish();
                                                           }
                                                       }
                                                   })
                                                   .build();

    }

    /**
     * S 헬스
     */
    private final HealthDataStore.ConnectionListener mConnectionListener = new HealthDataStore.ConnectionListener() {

        @Override
        public void onConnected() {
            // The connection is successful.
            // Acquires the required permission
            HealthPermissionManager pmsManager = new HealthPermissionManager(mStore);

            try {

                // Check whether the required permission is acquired
                Set<HealthPermissionManager.PermissionKey> keySet =
                                                                  new HashSet<HealthPermissionManager.PermissionKey>();
                keySet.add(mPermissionKey);
                Map<HealthPermissionManager.PermissionKey, Boolean> resultMap = pmsManager.isPermissionAcquired(keySet);

                if (resultMap.get(mPermissionKey) == Boolean.TRUE) {
                    // The permission has been acquired already

                    insertSHealthData();

                } else {
                    // Requests permission to read the count of steps

                    pmsManager.requestPermissions(keySet).setResultListener(mPermissionListener);
                }
            } catch (Exception e) {
                // Error handling
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionFailed(HealthConnectionErrorResult error) {
            // Resolve error if the connection fails
            error.resolve(InputBPActivity.this);

            setResult(RESULT_OK);
            finish();
        }

        @Override
        public void onDisconnected() {
            // The connection is disconnected
        }
    };

    private final HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult> mPermissionListener =
                                                                                                                  new HealthResultHolder.ResultListener<HealthPermissionManager.PermissionResult>() {

                                                                                                                      @Override
                                                                                                                      public void
                                                                                                                             onResult(HealthPermissionManager.PermissionResult result) {

                                                                                                                          Map<HealthPermissionManager.PermissionKey, Boolean> resultMap =
                                                                                                                                                                                        result.getResultMap();

                                                                                                                          if (resultMap.get(mPermissionKey) == Boolean.FALSE) {
                                                                                                                              // The requested permission is not acquired

                                                                                                                              mStore.disconnectService();
                                                                                                                          } else {
                                                                                                                              // The requested permission is acquired.
                                                                                                                          }
                                                                                                                      }
                                                                                                                  };

    private class InsertAndVerifySessionTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //First, create a new session and an insertion request.
            SessionInsertRequest insertRequest = insertFitnessSession();

            // [START insert_session]
            // Then, invoke the Sessions API to insert the session and await the result,
            // which is possible here because of the AsyncTask. Always include a timeout when
            // calling await() to avoid hanging that can occur from the service being shutdown
            // because of low memory or other conditions.
            Log.i(TAG, "Inserting the session in the History API");
            com.google.android.gms.common.api.Status insertStatus = Fitness.SessionsApi
                                                                                       .insertSession(mClient,
                                                                                                      insertRequest)
                                                                                       .await(1, TimeUnit.MINUTES);

            // Before querying the session, check to see if the insertion succeeded.
            if (!insertStatus.isSuccess()) {
                Log.i(TAG, "There was a problem inserting the session: " + insertStatus.getStatusMessage());
                return null;
            }

            // At this point, the session has been inserted and can be read.
            Log.i(TAG, "Session insert was successful!");
            // [END insert_session]

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            try {

                if (PreferenceUtil.getSHealthBP(InputBPActivity.this)) {
                    mStore.connectService();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();

                setResult(RESULT_OK);
                finish();
            }
        }
    }

    private SessionInsertRequest insertFitnessSession() {

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);

        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.MINUTE, -1);
        long startTime = cal.getTimeInMillis();

        // 맥박
        DataSource pulseDataSource = new DataSource.Builder().setAppPackageName(this.getPackageName())
                                                             .setDataType(DataType.TYPE_HEART_RATE_BPM)
                                                             .setType(DataSource.TYPE_RAW)
                                                             .build();

        // Create a data set of the run speeds to include in the session.
        DataSet pulseDataSet = DataSet.create(pulseDataSource);

        DataPoint pulseDataPoint = pulseDataSet.createDataPoint().setTimestamp(endTime, TimeUnit.MILLISECONDS);

        pulseDataPoint.getValue(Field.FIELD_BPM).setFloat(Float.valueOf(pulse));
        pulseDataSet.add(pulseDataPoint);

        // [START build_insert_session_request_with_activity_segments]
        // Create a second DataSet of ActivitySegments to indicate the runner took a 10-minute walk
        // in the middle of the run.
        DataSource activitySegmentDataSource = new DataSource.Builder().setAppPackageName(this.getPackageName())
                                                                       .setDataType(DataType.TYPE_ACTIVITY_SEGMENT)
                                                                       .setType(DataSource.TYPE_RAW)
                                                                       .build();

        DataSet activitySegments = DataSet.create(activitySegmentDataSource);

        DataPoint measureDataPoint = activitySegments.createDataPoint().setTimeInterval(startTime,
                                                                                        endTime,
                                                                                        TimeUnit.MILLISECONDS);
        measureDataPoint.getValue(Field.FIELD_ACTIVITY).setActivity(FitnessActivities.OTHER);
        activitySegments.add(measureDataPoint);

        // [START build_insert_session_request]
        // Create a session with metadata about the activity.
        Session session = new Session.Builder().setName(SESSION_NAME_BLOOD_PRESSURE)
                                               .setDescription(SESSION_NAME_BLOOD_PRESSURE)
                                               .setIdentifier(getApplicationInfo().packageName)
                                               .setActivity(FitnessActivities.OTHER)
                                               .setStartTime(startTime, TimeUnit.MILLISECONDS)
                                               .setEndTime(endTime, TimeUnit.MILLISECONDS)
                                               .build();

        // Build a session insert request
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder().setSession(session)
                                                                               .addDataSet(pulseDataSet)
                                                                               .addDataSet(activitySegments)
                                                                               .build();
        // [END build_insert_session_request]
        // [END build_insert_session_request_with_activity_segments]

        return insertRequest;
    }

    private void insertSHealthData() {

        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        cal.add(Calendar.MINUTE, -1);
        long startTime = cal.getTimeInMillis();

        // 타임존
        TimeZone tz = TimeZone.getDefault();
        int offsetFromUtc = tz.getOffset(now.getTime());

        HealthDataResolver resolver = new HealthDataResolver(mStore, null);
        HealthData data = new HealthData();

        data.putLong(HealthConstants.SessionMeasurement.START_TIME, startTime);
        data.putLong(HealthConstants.SessionMeasurement.TIME_OFFSET, offsetFromUtc);
        data.putFloat(HealthConstants.BloodPressure.DIASTOLIC,
                      Float.parseFloat(requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA)));
        data.putFloat(HealthConstants.BloodPressure.MEAN,
                      Float.parseFloat(requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN)));
        data.putInt(HealthConstants.BloodPressure.PULSE,
                    Integer.parseInt(requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE)));
        data.putFloat(HealthConstants.BloodPressure.SYSTOLIC,
                      Float.parseFloat(requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS)));
        data.putString(HealthConstants.BloodPressure.COMMENT,
                       requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));

        HealthDataResolver.InsertRequest request =
                                                 new HealthDataResolver.InsertRequest.Builder().setDataType(HealthConstants.BloodPressure.HEALTH_DATA_TYPE)
                                                                                               .build();

        try {
            // register the local device with the data if it is not registered
            data.setSourceDevice(new HealthDeviceManager(mStore).getLocalDevice().getUuid());
            request.addHealthData(data);
            resolver.insert(request).setResultListener(mResultListener);
        } catch (Exception e) {
            // Error handling
            e.printStackTrace();

            setResult(RESULT_OK);
            finish();
        }
    }

    private final HealthResultHolder.ResultListener<HealthResultHolder.BaseResult> mResultListener =
                                                                                                   new HealthResultHolder.ResultListener<HealthResultHolder.BaseResult>() {

                                                                                                       @Override
                                                                                                       public void
                                                                                                              onResult(HealthResultHolder.BaseResult result) {
                                                                                                           //Check the result
                                                                                                           setResult(RESULT_OK);
                                                                                                           finish();
                                                                                                       }
                                                                                                   };
}
