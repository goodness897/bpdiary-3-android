package kr.co.openit.bpdiary.activity.weight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
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

import java.lang.reflect.Field;
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
import kr.co.openit.bpdiary.services.WeighingScaleService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.HealthcareUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class InputWeightActivity extends NonMeasureActivity
                                 implements View.OnClickListener, WheelPickerOnResizeListener {

    private static final int REQUEST_INPUT_WEIGHT_LBS = 1000;

    private static final int REQUEST_INPUT_WEIGHT_KG = 2000;

    /**
     * 메모 입력 요청코드
     */
    private static final int REQUEST_INPUT_MEMO = 3000;

    private LinearLayout llEmptyView;

    private NumberPicker npUnit;

    private LinearLayout llAddMemo;

    private TextView tvMemoContent;

    private Button btnSave;

    private LinearLayout llUpdateMemo;

    private String[] unit = {"kg", "lbs"};

    /**
     * Google Fit
     */
    private boolean authInProgress = false;

    private GoogleApiClient mClient = null;

    private static final String AUTH_PENDING = "auth_state_pending";

    public static final String SESSION_NAME_WEIGHT = "Measure Weight";

    /**
     * S Health
     */
    private HealthDataStore mStore;

    private HealthPermissionManager.PermissionKey mPermissionKey;

    private String mDate;

    private String mTime;

    private int mYear = 1970;

    private int mMonth = 1;

    private int mDay = 1;

    private int mHour = 0;

    private int mMinute = 0;

    private Map<String, String> responesWsMap;

    /**
     * 측정일
     */
    private String strMeasureDt;

    /**
     * 측정시간
     */
    private String strMeasureTime;

    /**
     * 체중 단위
     */
    private String strWeightUnitType;

    /**
     * 체중
     */
    private String strWeight;

    /**
     * 메모
     */
    private String memo = "";

    /**
     * 데이터 DB 저장용 Map
     */
    private Map<String, String> requestWsMap;

    /**
     * 체중 측정 Data를 DB에 저장 및 Server로 전송하는 AsyncTask
     */
    private CreateWeighingScaleAsync cwsAsync;

    //    private SearchWeighingScaleDBSync swsDBSync;

    /**
     * WeighingScaleService
     */
    private WeighingScaleService wsService;

    private Context context;

    private String weightUnit;

    private String validationMessage = "";

    private static final int MOVE_PICKER_NUMBER = 15;

    private static final int MOVE_PICKER_DECIMAL = 5;

    private boolean isFirst;

    private int count = 3;

    /**
     * kg 단위 숫자 피커
     */
    private WheelNumberPicker kgNumberPicker;

    /**
     * kg 단위 소수점 피커
     */
    private WheelNumberPicker kgDecimalPicker;

    /**
     * lbs 단위 숫자 피커
     */
    private WheelNumberPicker lbsNumberPicker;

    /**
     * lbs 단위 소수점 피커
     */
    private WheelNumberPicker lbsDecimalPicker;

    private LinearLayout llKg;

    private LinearLayout llLbs;

    private String weightKg;

    private String weightLbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_weight);

        AnalyticsUtil.sendScene(InputWeightActivity.this, "3_체중 입력");

        initToolbar(getResources().getString(R.string.weight_input));

        context = InputWeightActivity.this;

        wsService = new WeighingScaleService(context);

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
        mPermissionKey = new HealthPermissionManager.PermissionKey(HealthConstants.Weight.HEALTH_DATA_TYPE,
                                                                   HealthPermissionManager.PermissionType.WRITE);

        HealthDataService healthDataService = new HealthDataService();

        try {
            //Initializes the Health data service
            //healthDataService.initialize(this);
            healthDataService.initialize(context);
        } catch (Exception e) {
            // Handles exception
        }

        mStore = new HealthDataStore(context, mConnectionListener);

        LinearLayout layout = (LinearLayout)findViewById(R.id.ll_input_weight);

        setLayout(layout);

        //        swsDBSync = new SearchWeighingScaleDBSync();
        //        swsDBSync.execute();

        isFirst = true;

    }

    @Override
    public void setLayout(LinearLayout layout) {
        super.setLayout(layout);

        Intent intent = getIntent();
        kgNumberPicker = (WheelNumberPicker)findViewById(R.id.np_number_kg);
        kgDecimalPicker = (WheelNumberPicker)findViewById(R.id.np_decimal_kg);
        lbsNumberPicker = (WheelNumberPicker)findViewById(R.id.np_number_lbs);
        lbsDecimalPicker = (WheelNumberPicker)findViewById(R.id.np_decimal_lbs);

        btnSave = (Button)findViewById(R.id.btn_save);
        tvMemoContent = (TextView)findViewById(R.id.tv_memo_content);
        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        llAddMemo = (LinearLayout)findViewById(R.id.ll_add_memo);
        llUpdateMemo = (LinearLayout)findViewById(R.id.ll_update_memo);
        llUpdateMemo.setClickable(false);

        llKg = (LinearLayout)findViewById(R.id.ll_kg);

        llLbs = (LinearLayout)findViewById(R.id.ll_lbs);

        // 단위 kg일때 피커클릭
        kgNumberPicker.setOnWheelClickListener(new WheelPicker.OnWheelClickListener() {

            @Override
            public void onWheelSelected() {
                Intent intent = new Intent(InputWeightActivity.this, DirectInputWeightActivity.class);
                intent.putExtra("weightUnit", "kg");
                startActivityForResult(intent, REQUEST_INPUT_WEIGHT_KG);
            }
        });

        kgNumberPicker.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                weightKg = String.valueOf(number) + "." + String.valueOf(kgDecimalPicker.getCurrentNumber());
            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                weightKg = String.valueOf(number) + "." + String.valueOf(kgDecimalPicker.getCurrentNumber());

            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

        kgDecimalPicker.setOnWheelClickListener(new WheelPicker.OnWheelClickListener() {

            @Override
            public void onWheelSelected() {
                Intent intent = new Intent(InputWeightActivity.this, DirectInputWeightActivity.class);
                intent.putExtra("weightUnit", "kg");
                startActivityForResult(intent, REQUEST_INPUT_WEIGHT_KG);

            }
        });
        kgDecimalPicker.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                weightKg = String.valueOf(kgNumberPicker.getCurrentNumber()) + "." + String.valueOf(number);
            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                weightKg = String.valueOf(kgNumberPicker.getCurrentNumber()) + "." + String.valueOf(number);
            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

        // 단위 lbs 일때 피커 클릭

        lbsNumberPicker.setOnWheelClickListener(new WheelPicker.OnWheelClickListener() {

            @Override
            public void onWheelSelected() {
                Intent intent = new Intent(InputWeightActivity.this, DirectInputWeightActivity.class);
                intent.putExtra("weightUnit", "lbs");
                startActivityForResult(intent, REQUEST_INPUT_WEIGHT_LBS);

            }
        });

        // 단위 lbs 일때 Wheel

        lbsNumberPicker.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                weightLbs = String.valueOf(number) + "." + String.valueOf(lbsDecimalPicker.getCurrentNumber());

            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                weightLbs = String.valueOf(number) + "." + String.valueOf(lbsDecimalPicker.getCurrentNumber());
            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

        lbsDecimalPicker.setOnWheelClickListener(new WheelPicker.OnWheelClickListener() {

            @Override
            public void onWheelSelected() {
                Intent intent = new Intent(InputWeightActivity.this, DirectInputWeightActivity.class);
                intent.putExtra("weightUnit", "lbs");
                startActivityForResult(intent, REQUEST_INPUT_WEIGHT_LBS);

            }
        });

        // 단위 lbs 일때 Wheel

        lbsDecimalPicker.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                weightLbs = String.valueOf(lbsNumberPicker.getCurrentNumber()) + "." + String.valueOf(number);

            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                weightLbs = String.valueOf(lbsNumberPicker.getCurrentNumber()) + "." + String.valueOf(number);
            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

        llAddMemo.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        llUpdateMemo.setOnClickListener(this);

        initNumberPicker();

        if (PreferenceUtil.getWeightUnit(InputWeightActivity.this).equals(ManagerConstants.Unit.KG)) { //단위가 mg/dL 일 경우
            npUnit.setValue(0);
            llKg.setVisibility(View.VISIBLE);
            llLbs.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT))) {
                if ("0".equals(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT))) {
                    weightKg = "60";
                    kgNumberPicker.setDefaultNumber(Integer.parseInt(weightKg) - MOVE_PICKER_NUMBER);
                    kgDecimalPicker.setDefaultNumber(0);
                } else {
                    weightKg = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT);
                    if (weightKg.contains(".")) {
                        String[] weights = weightKg.split("\\.");
                        if (weights.length > 1) {

                            kgNumberPicker.setDefaultNumber(Integer.parseInt(weights[0]) - MOVE_PICKER_NUMBER);
                            kgDecimalPicker.setDefaultNumber(0);

                        }
                    } else {
                        kgNumberPicker.setDefaultNumber(Integer.parseInt(weightKg) - MOVE_PICKER_NUMBER);
                        kgDecimalPicker.setDefaultNumber(0);

                    }
                }
            } else {
                weightKg = "60";
                kgNumberPicker.setDefaultNumber(Integer.parseInt(weightKg) - MOVE_PICKER_NUMBER);
                kgDecimalPicker.setDefaultNumber(0);
            }

        } else {
            npUnit.setValue(1);
            llKg.setVisibility(View.GONE);
            llLbs.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT))) {
                if ("0".equals(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT))) {
                    String weight = "60";
                    weightLbs = ManagerUtil.kgToLbs(weight);
                    if (weightLbs.contains(".")) {
                        String[] weights = weight.split("\\.");
                        if (weights.length > 1) {

                            lbsNumberPicker.setDefaultNumber(Integer.parseInt(weights[0]) - MOVE_PICKER_NUMBER);
                            lbsDecimalPicker.setDefaultNumber(0);

                        }
                    } else {

                        lbsNumberPicker.setDefaultNumber(Integer.parseInt(weight) - MOVE_PICKER_NUMBER);
                        lbsDecimalPicker.setDefaultNumber(0);

                    }
                } else {
                    weightLbs =
                              ManagerUtil.kgToLbs(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                    if (weightLbs.contains(".")) {
                        String[] weights = weightLbs.split("\\.");
                        if (weights.length > 1) {

                            lbsNumberPicker.setDefaultNumber(Integer.parseInt(weights[0]) - MOVE_PICKER_NUMBER);
                            lbsDecimalPicker.setDefaultNumber(0);

                        }
                    } else {
                        lbsNumberPicker.setDefaultNumber(Integer.parseInt(weightLbs) - MOVE_PICKER_NUMBER);
                        lbsDecimalPicker.setDefaultNumber(0);
                    }
                }
            } else {
                String weight = "60";
                weightLbs = ManagerUtil.kgToLbs(weight);
                if (weightLbs.contains(".")) {
                    String[] weights = weight.split("\\.");
                    if (weights.length > 1) {

                        lbsNumberPicker.setDefaultNumber(Integer.parseInt(weights[0]) - MOVE_PICKER_NUMBER);
                        lbsDecimalPicker.setDefaultNumber(0);

                    }
                } else {

                    lbsNumberPicker.setDefaultNumber(Integer.parseInt(weight) - MOVE_PICKER_NUMBER);
                    lbsDecimalPicker.setDefaultNumber(0);

                }
            }

        }

        kgNumberPicker.setOnResizeListener(this);
        kgDecimalPicker.setOnResizeListener(this);
        lbsNumberPicker.setOnResizeListener(this);
        lbsDecimalPicker.setOnResizeListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

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
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    private void initNumberPicker() {

        npUnit = (NumberPicker)findViewById(R.id.np_unit);
        npUnit.setMinValue(0);
        npUnit.setMaxValue(unit.length - 1);
        npUnit.setDisplayedValues(unit);
        setDividerColor(npUnit);
        npUnit.setWrapSelectorWheel(false);

        npUnit.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                switch (newVal) {
                    case 0: // kg
                        weightUnit = ManagerConstants.Unit.KG;
                        String kgValue = ManagerUtil.lbsToKg(String.valueOf(lbsNumberPicker.getCurrentNumber() + "."
                                                                            + lbsDecimalPicker.getCurrentNumber()));
                        if (kgValue.contains(".")) {
                            String[] values = kgValue.split("\\.");
                            kgNumberPicker.setDefaultNumber(Integer.parseInt(values[0]));
                            kgDecimalPicker.setDefaultNumber(Integer.parseInt(values[1].substring(0, 1)));
                            weightKg = values[0] + "." + values[1];
                        } else {
                            weightKg = kgValue;
                        }
                        llKg.setVisibility(View.VISIBLE);
                        llLbs.setVisibility(View.GONE);
                        Log.d("mspark", "kg : " + weightKg);

                        break;
                    case 1: // lbs
                        weightUnit = ManagerConstants.Unit.LBS;
                        String lbsValue = ManagerUtil.kgToLbs(String.valueOf(kgNumberPicker.getCurrentNumber() + "."
                                                                             + kgDecimalPicker.getCurrentNumber()));
                        if (lbsValue.contains(".")) {
                            String[] values = lbsValue.split("\\.");
                            lbsNumberPicker.setDefaultNumber(Integer.parseInt(values[0]));
                            lbsDecimalPicker.setDefaultNumber(Integer.parseInt(values[1].substring(0, 1)));
                            weightLbs = values[0] + "." + values[1];
                        } else {
                            weightLbs = lbsValue;
                        }
                        llLbs.setVisibility(View.VISIBLE);
                        llKg.setVisibility(View.GONE);
                        Log.d("mspark", "lbs : " + weightLbs);

                        break;

                }
            }
        });
    }

    private void setDividerColor(NumberPicker picker) {
        Field[] numberPickerFields = NumberPicker.class.getDeclaredFields();
        for (Field field : numberPickerFields) {
            if (field.getName().equals("mSelectionDivider")) {
                field.setAccessible(true);
                try {
                    field.set(picker, getResources().getDrawable(R.drawable.number_picker_divider));
                } catch (IllegalArgumentException e) {
                    Log.v(TAG, "Illegal Argument Exception");
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    Log.v(TAG, "Resources NotFound");
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    Log.v(TAG, "Illegal Access Exception");
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INPUT_WEIGHT_KG) {
            if (resultCode == RESULT_OK) {
                String weight = data.getStringExtra("weight");
                if (weight != null) {
                    weightKg = weight;
                    if (weight.contains(".")) {
                        String[] weightArray = weight.split("\\.");
                            if (weightArray.length == 2) {
                                kgNumberPicker.setDefaultNumber(Integer.parseInt(weightArray[0]));
                                kgDecimalPicker.setDefaultNumber(Integer.parseInt(weightArray[1].substring(0, 1)));
                            } else {
                                kgNumberPicker.setDefaultNumber(Integer.parseInt(weightArray[0]));
                                kgDecimalPicker.setDefaultNumber(0);
                        }

                    } else {
                        kgNumberPicker.setDefaultNumber(Integer.parseInt(weight));
                        kgDecimalPicker.setDefaultNumber(0);

                    }
                }

            }
        } else if (requestCode == REQUEST_INPUT_WEIGHT_LBS) {
            if (resultCode == RESULT_OK) {
                String weight = data.getStringExtra("weight");
                Log.d(TAG, "receive weight : " + weight);
                if (weight != null) {
                    weightLbs = weight;
                    if (weight.contains(".")) {
                        String[] weightArray = weight.split("\\.");
                        if (weightArray.length == 2) {
                            lbsNumberPicker.setDefaultNumber(Integer.parseInt(weightArray[0]));
                            lbsDecimalPicker.setDefaultNumber(Integer.parseInt(weightArray[1].substring(0, 1)));

                        } else {
                            lbsNumberPicker.setDefaultNumber(Integer.parseInt(weightArray[0]));
                            lbsDecimalPicker.setDefaultNumber(0);
                        }
                    } else {
                        lbsNumberPicker.setDefaultNumber(Integer.parseInt(weight));
                        lbsDecimalPicker.setDefaultNumber(0);
                    }
                }

            }
        } else if (requestCode == ManagerConstants.ActivityResultCode.GOOGLE_FIT_REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            }
        } else if (requestCode == REQUEST_INPUT_MEMO) {
            if (resultCode == RESULT_OK) {
                String wsMemo = data.getStringExtra("wsMemo");
                if (!TextUtils.isEmpty(wsMemo)) {
                    llAddMemo.setVisibility(View.GONE);
                    llUpdateMemo.setVisibility(View.VISIBLE);
                    tvMemoContent.setText(wsMemo);

                    memo = wsMemo;
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!ManagerUtil.isClicking()) {

            Intent intent;

            switch (v.getId()) {
                case R.id.ll_add_memo:
                    intent = new Intent(InputWeightActivity.this, WeightMemoActivity.class);
                    startActivityForResult(intent, REQUEST_INPUT_MEMO);
                    break;

                case R.id.btn_save:
                    if (compareDate()) {
                        makeWSManualData();
                    } else {
                        DefaultDialog deleteDialog = new DefaultDialog(InputWeightActivity.this,
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

                    break;

                case R.id.ll_update_memo:
                    String memo = tvMemoContent.getText().toString();
                    intent = new Intent(InputWeightActivity.this, WeightMemoActivity.class);
                    intent.putExtra("update", memo);
                    startActivityForResult(intent, REQUEST_INPUT_MEMO);
                    break;
            }
        }
    }

    private void makeWSManualData() {
        String strMeasureDt = date;
        String strMeasureTime = time;

        if (llKg.getVisibility() == View.VISIBLE) {
            strWeightUnitType = getResources().getString(R.string.weight_kg);
            //            strWeight = String.format("%d", scalePicker.getKgNumberPicker().getCurrentNumber()) + "."
            //                        + String.format("%d", scalePicker.getKgDecimalPicker().getCurrentNumber());
            if (weightKg.contains(".")) {
                String[] values = weightKg.split("\\.");
                weightKg = values[0] + "." + values[1].substring(0, 1);
            }
            strWeight = weightKg;
        } else {
            strWeightUnitType = getResources().getString(R.string.weight_lbs);
            //            strWeight = String.format("%d", scalePicker.getLbsNumberPicker().getCurrentNumber()) + "."
            //                        + String.format("%d", scalePicker.getLbsDecimalPicker().getCurrentNumber());
            strWeight = ManagerUtil.lbsToKg(weightLbs);
        }

        //BMI 구하기
        String strBmiValue = "";

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        //BMI 계산을 위해서 키를 cm -> m로 변환
        double dHeight = Double.parseDouble(PreferenceUtil.getHeight(context));

        if (dHeight > 0) {
            strBmiValue = ManagerUtil.calBMI(strWeight, String.valueOf((dHeight / 100.0)));
        } else {
            dHeight = 0;
            strBmiValue = "0";
        }

        String strBmiTypeValue = HealthcareUtil.getWeighingScaleBmiType(strBmiValue);
        PreferenceUtil.setWeightUnit(InputWeightActivity.this, strWeightUnitType);

        //DB에 넣을 Data 작성
        requestWsMap = new HashMap<String, String>();
        requestWsMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT, strWeight);
        requestWsMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT, String.valueOf(dHeight));
        requestWsMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI, strBmiValue);
        requestWsMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE, strBmiTypeValue);
        requestWsMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID, "");
        requestWsMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL, "");
        requestWsMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER, "");
        requestWsMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE, memo);
        requestWsMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT, strMeasureDt + strMeasureTime + "00");
        requestWsMap.put(ManagerConstants.DataBase.COLUMN_NAME_INS_DT, ManagerUtil.getCurrentDateTime());
        requestWsMap.put(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                         ManagerConstants.ServerSyncYN.SERVER_SYNC_N);

        //서버 및 DB 전송 후 성공하면 Preference 저장
        //DB에 저장 및 Server로 전송 AsyncTask 실행
        cwsAsync = new CreateWeighingScaleAsync();
        cwsAsync.execute();
    }

    @Override
    public void onResize(int id, int xNew, int yNew, int xOld, int yOld) {

        if (isFirst)
            moveNumberPicker();

    }

    /**
     * 체중계 Data 등록 Async
     */
    private class CreateWeighingScaleAsync extends AsyncTask<Void, Void, JSONObject> {

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

                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(context));

                //DB 저장
                int nRow = wsService.createWeighingScaleData(requestWsMap);

                if (nRow > 0) {

                    strDbSeq = String.valueOf(nRow);

                    //Server 전송
                    data.put(ManagerConstants.RequestParamName.INS_DT,
                             requestWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                    data.put(ManagerConstants.RequestParamName.WS_WEIGHT,
                             requestWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                    data.put(ManagerConstants.RequestParamName.HEIGHT,
                             requestWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT));
                    data.put(ManagerConstants.RequestParamName.WS_BMI,
                             requestWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI));
                    data.put(ManagerConstants.RequestParamName.WS_BMI_TYPE,
                             requestWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE));
                    data.put(ManagerConstants.RequestParamName.SENSOR_COMPANY,
                             requestWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
                    data.put(ManagerConstants.RequestParamName.SENSOR_MODEL,
                             requestWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL));
                    data.put(ManagerConstants.RequestParamName.SENSOR_SN,
                             requestWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID));
                    data.put(ManagerConstants.RequestParamName.MESSAGE,
                             requestWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));
                    data.put(ManagerConstants.RequestParamName.RECORD_DT,
                             ManagerUtil.convertDateFormatToServer(requestWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT)));

                    resultJSON = wsService.sendWeighingScaleData(data);

                    if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                         .toString())) {
                            //Server 전송 완료

                            //DB에 Server 전송 Update
                            wsService.updateSendToServerYN(strDbSeq);

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

            PreferenceUtil.setWeightUnit(context, strWeightUnitType);

            try {
                if (PreferenceUtil.getGoogleFit(context)) {
                    //구글 피트니스
                    mClient.connect();
                } else {
                    //S 헬스
                    if (PreferenceUtil.getSHealthWS(context)) {
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
                                                   .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {

                                                       @Override
                                                       public void onConnected(Bundle bundle) {
                                                           Log.i(TAG, "Connected!!!");
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

                                                           if (PreferenceUtil.getSHealthBP(context)) {
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

                                                           Log.i(TAG, "Connection failed. Cause: " + result.toString());

                                                           if (PreferenceUtil.getSHealthWS(context)) {
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
     * 현재 시간과 비교 초과 시 return false
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
                //                LogUtil.i(context,
                //                          "There was a problem inserting the session: " + insertStatus.getStatusMessage());
                return null;
            }

            // At this point, the session has been inserted and can be read.
            //            LogUtil.i(context, "Session insert was successful!");
            // [END insert_session]

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            try {

                if (PreferenceUtil.getSHealthWS(context)) {
                    mStore.connectService();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }

            } catch (Exception e) {
                //                LogUtil.e(context, e.getLocalizedMessage());
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

        // 체중
        DataSource weightDataSource = new DataSource.Builder().setAppPackageName(this.getPackageName())
                                                              .setDataType(DataType.TYPE_WEIGHT)
                                                              .setType(DataSource.TYPE_RAW)
                                                              .build();

        // Create a data set of the run speeds to include in the session.
        DataSet weightDataSet = DataSet.create(weightDataSource);

        DataPoint weightDataPoint = weightDataSet.createDataPoint().setTimestamp(endTime, TimeUnit.MILLISECONDS);

        if (getResources().getString(R.string.weight_lbs).equals(strWeightUnitType)) {
            strWeight = ManagerUtil.lbsToKg(strWeight);
        }
        weightDataPoint.getValue(com.google.android.gms.fitness.data.Field.FIELD_WEIGHT)
                       .setFloat(Float.valueOf(strWeight));
        weightDataSet.add(weightDataPoint);

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
        measureDataPoint.getValue(com.google.android.gms.fitness.data.Field.FIELD_ACTIVITY)
                        .setActivity(FitnessActivities.OTHER);
        activitySegments.add(measureDataPoint);

        // [START build_insert_session_request]
        // Create a session with metadata about the activity.
        Session session = new Session.Builder().setName(SESSION_NAME_WEIGHT)
                                               .setDescription(SESSION_NAME_WEIGHT)
                                               .setIdentifier("Unique IdentifierHere")
                                               .setActivity(FitnessActivities.OTHER)
                                               .setStartTime(startTime, TimeUnit.MILLISECONDS)
                                               .setEndTime(endTime, TimeUnit.MILLISECONDS)
                                               .build();

        // Build a session insert request
        SessionInsertRequest insertRequest = new SessionInsertRequest.Builder().setSession(session)
                                                                               .addDataSet(weightDataSet)
                                                                               .addDataSet(activitySegments)
                                                                               .build();
        // [END build_insert_session_request]
        // [END build_insert_session_request_with_activity_segments]

        return insertRequest;
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
            error.resolve(InputWeightActivity.this);

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
        data.putFloat(HealthConstants.Weight.WEIGHT,
                      Float.parseFloat(requestWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT)));
        data.putFloat(HealthConstants.Weight.HEIGHT,
                      Float.parseFloat(requestWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT)));
        data.putString(HealthConstants.Weight.COMMENT, requestWsMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));

        HealthDataResolver.InsertRequest request =
                                                 new HealthDataResolver.InsertRequest.Builder().setDataType(HealthConstants.Weight.HEALTH_DATA_TYPE)
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
                                                                                                           // Check the result
                                                                                                           setResult(RESULT_OK);
                                                                                                           finish();
                                                                                                       }
                                                                                                   };

    private void moveNumberPicker() {

        Intent intent = getIntent();

        if (getResources().getString(R.string.weight_kg).equals(PreferenceUtil.getWeightUnit(context))) {
            count--;
            if (count == 0) {
                isFirst = false;
                return;
            }
            if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT))) {
                if ("0".equals(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT))) {
                    weightKg = "60";
                    kgNumberPicker.scrollTo(Integer.parseInt(weightKg));
                    kgDecimalPicker.scrollTo(1);
                } else {
                    weightKg = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT);
                    String[] weight = weightKg.split("\\.");
                    if (weight.length > 1) {
                        kgNumberPicker.scrollTo(Integer.parseInt(weight[0]));
                        kgDecimalPicker.scrollTo(Integer.parseInt(weight[1].substring(0, 1)) + 1);
                    } else {
                        kgNumberPicker.scrollTo(Integer.parseInt(weight[0]));
                        kgDecimalPicker.scrollTo(1);
                    }
                }

            } else {
                weightKg = "60";
                kgNumberPicker.scrollTo(Integer.parseInt(weightKg));
                kgDecimalPicker.scrollTo(1);
            }
        } else if (getResources().getString(R.string.weight_lbs).equals(PreferenceUtil.getWeightUnit(context))) {
            count--;
            if (count == 0) {
                isFirst = false;
                return;
            }
            if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT))) { //단위가 kg 일 경우
                //단위 피커 mmol/L 선택
                // Intent 로 전달온 값이 mg/dL 단위이기때문에 변환
                if ("0".equals(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT))) {
                    String weight = "60";
                    weightLbs = ManagerUtil.kgToLbs(weight);
                    if (weightLbs.contains(".")) {
                        String[] weights = weightLbs.split("\\.");
                        if (weights.length > 1) {

                            lbsNumberPicker.scrollTo(Integer.parseInt(weights[0]));
                            lbsDecimalPicker.scrollTo(Integer.parseInt(weights[1]) + 1);
                        }
                    } else {

                        lbsNumberPicker.scrollTo(Integer.parseInt(weightLbs));
                        lbsDecimalPicker.scrollTo(1);
                    }
                } else {
                    String weight = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT);
                    weightLbs = ManagerUtil.kgToLbs(weight);
                    if (weightLbs.contains(".")) {
                        String[] weights = weightLbs.split("\\.");
                        if (weights.length > 1) {
                            lbsNumberPicker.scrollTo(Integer.parseInt(weights[0]));
                            lbsDecimalPicker.scrollTo(Integer.parseInt(weights[1]) + 1);
                        }
                    } else {

                        lbsNumberPicker.scrollTo(Integer.parseInt(weight));
                        lbsDecimalPicker.scrollTo(1);
                    }
                }

                //        isFirst = false;

            } else {
                String weight = "60";
                weightLbs = ManagerUtil.kgToLbs(weight);
                if (weightLbs.contains(".")) {
                    String[] weights = weightLbs.split("\\.");
                    if (weights.length > 1) {

                        lbsNumberPicker.scrollTo(Integer.parseInt(weights[0]));
                        lbsDecimalPicker.scrollTo(Integer.parseInt(weights[1]) + 1);
                    }
                } else {

                    lbsNumberPicker.scrollTo(Integer.parseInt(weightLbs));
                    lbsDecimalPicker.scrollTo(1);
                }
            }

        }

    }
}
