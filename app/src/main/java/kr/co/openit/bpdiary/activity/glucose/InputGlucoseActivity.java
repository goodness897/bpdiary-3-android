package kr.co.openit.bpdiary.activity.glucose;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.customview.WheelNumberPicker;
import kr.co.openit.bpdiary.customview.WheelPicker;
import kr.co.openit.bpdiary.customview.WheelPickerOnResizeListener;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.services.GlucoseService;
import kr.co.openit.bpdiary.utils.HealthcareUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class InputGlucoseActivity extends NonMeasureActivity
                                  implements View.OnClickListener, WheelPickerOnResizeListener {

    private LinearLayout llEmptyView;

    /**
     * 혈당 입력 요청코드
     */
    private static final int REQUEST_INPUT_GLUCOSE_MGDL = 1000;

    private static final int REQUEST_INPUT_GLUCOSE_MMOL = 3000;

    /**
     * 메모 입력 요청 코드
     */
    private static final int REQUEST_INPUT_MEMO = 2000;

    /**
     * mg/dL 단위 혈당 선택 피커
     */
    private WheelNumberPicker glucoseMgPicker;

    /**
     * mmol/L 단위 혈당 숫자 피커
     */
    private WheelNumberPicker glucoseMmolNumberPicker;

    /**
     * mmol/L 단위 혈당 소수점 피커
     */
    private WheelNumberPicker glucoseMmolDecimalPicker;

    /**
     * 단위 피커
     */
    private NumberPicker npUnit;

    /**
     * 메모 추가 Layout
     */
    private LinearLayout llAddMemo;

    /**
     * 메모 있을 시 나타나는(메모 수정) Layout
     */
    private LinearLayout llUpdateMemo;

    /**
     * mmol/L 단위 피커 포함한 Layout
     */
    private LinearLayout llGlucoseMmol;

    /**
     * mg/dL 단위 피커 포함한 Layout
     */
    private LinearLayout llGlucoseMg;

    /**
     * 메모 내용 보여주는 TextView
     */
    private TextView tvMemoContent;

    /**
     * 저장 버튼
     */
    private Button btnSave;

    /**
     * 식전(공복) TextView
     */
    private TextView rbBeforeMeal;

    /**
     * 식후 TextView
     */
    private TextView rbAfterMeal;

    /**
     * 혈당 mg/dL 기본값
     */
    private String glucoseMgdL = "100";

    /**
     * 혈당 mg/dL 기본값
     */
    private String glucoseMmol = "0";

    /**
     * 혈당 mmol/L 숫자 기본값
     */
    private String glucoseNumberMmolL = "5";

    /**
     * 혈당 mmol/L 소수점 기본값
     */
    private String glucoseDecimalMmolL = "6";

    /**
     * 혈당 단위
     */
    private String glucoseUnit = ManagerConstants.Unit.MGDL;

    /**
     * 메모 String
     */
    private String memo = "";

    /**
     * 식전(공복) / 식후 타입
     */
    private String eatType = "B";

    /**
     * 데이터 DB 저장용 Map
     */
    private Map<String, String> requestMap;

    /**
     * BloodPressureService
     */
    private GlucoseService glucoseService;

    private String validationMessage = "";

    /**
     * 혈압 측정 Data를 DB에 저장 및 Server로 전송하는 AsyncTask
     */
    private CreateGlucoseAsync cGlucoseAsync;

    private String[] unit = {"mg/dL", "mmol/L"};

    private ImageView ivCenterLine;

    private boolean isFirst;

    private static final int MOVE_PICKER_NUMBER = 15;

    private static final int MOVE_PICKER_DECIMAL = 5;

    private int count = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_glucose);
        initToolbar(getString(R.string.glucose_input_title));

        LinearLayout layout = (LinearLayout)findViewById(R.id.ll_input_glucose);

        setLayout(layout);

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        glucoseService = new GlucoseService(InputGlucoseActivity.this);

        glucoseUnit = PreferenceUtil.getGlucoseUnit(InputGlucoseActivity.this);

        //        if (!ManagerConstants.Unit.MGDL.equals(glucoseUnit)) {
        //            if (!TextUtils.isEmpty(getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE))) {
        //                String value =
        //                             ManagerUtil.mgToMmol(getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));
        //
        //                if (value.contains(".")) {
        //                    String[] values = value.split("\\.");
        //                    glucoseMmolNumberPicker.setDefaultNumber(Integer.parseInt(values[0]));
        //                    glucoseMmolDecimalPicker.setDefaultNumber(Integer.parseInt(values[1]));
        //                    glucoseMmol = values[0] + "." + values[1];
        //                }
        //            }
        //
        //        }

        isFirst = true;
    }

    /**
     * 레이아웃 세팅
     *
     * @param layout
     */
    @Override
    public void setLayout(LinearLayout layout) {
        super.setLayout(layout);

        Intent intent = getIntent();

        npUnit = (NumberPicker)findViewById(R.id.np_unit);

        glucoseMgPicker = (WheelNumberPicker)findViewById(R.id.picker_glucose_mg);

        glucoseMmolNumberPicker = (WheelNumberPicker)findViewById(R.id.picker_glucose_number_mmo);

        glucoseMmolDecimalPicker = (WheelNumberPicker)findViewById(R.id.picker_glucose_decimal_mmo);

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);

        llAddMemo = (LinearLayout)findViewById(R.id.ll_add_memo);

        llUpdateMemo = (LinearLayout)findViewById(R.id.ll_update_memo);

        tvMemoContent = (TextView)findViewById(R.id.tv_memo_content);

        btnSave = (Button)findViewById(R.id.btn_save);

        rbBeforeMeal = (TextView)findViewById(R.id.rb_before_meal);

        rbAfterMeal = (TextView)findViewById(R.id.rb_after_meal);

        ivCenterLine = (ImageView)findViewById(R.id.iv_center_line);

        //저장된 상태에 따라, 식전 식후 선택

        if (intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL) != null) {
            eatType = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL);
        } else if (intent.getStringExtra(ManagerConstants.RequestParamName.GLUCOSE_MEAL) != null) {
            eatType = intent.getStringExtra(ManagerConstants.RequestParamName.GLUCOSE_MEAL);
        }

        if (eatType.equals(ManagerConstants.EatType.GLUCOSE_BEFORE)) {
            rbBeforeMeal.setSelected(true);
            rbAfterMeal.setSelected(false);
            ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_5f5b5d));
        } else {
            rbBeforeMeal.setSelected(false);
            rbAfterMeal.setSelected(true);
            ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_5f5b5d));
        }

        llGlucoseMmol = (LinearLayout)findViewById(R.id.ll_glucose_mmol);

        llGlucoseMg = (LinearLayout)findViewById(R.id.ll_glucose_mg);

        // 단위 mg/dL일때 피커클릭
        glucoseMgPicker.setOnWheelClickListener(new WheelPicker.OnWheelClickListener() {

            @Override
            public void onWheelSelected() {
                Intent intent = new Intent(InputGlucoseActivity.this, DirectInputGlucoseActivity.class);
                intent.putExtra("glucoseUnit", ManagerConstants.Unit.MGDL);
                startActivityForResult(intent, REQUEST_INPUT_GLUCOSE_MGDL);
            }
        });

        // 단위 mg/dL일때 피커 Wheel Listener
        glucoseMgPicker.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                glucoseMgdL = String.valueOf(number);
            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                glucoseMgdL = String.valueOf(number);

            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

        // 단위 mmol/L 일때 숫자 클릭

        glucoseMmolNumberPicker.setOnWheelClickListener(new WheelPicker.OnWheelClickListener() {

            @Override
            public void onWheelSelected() {
                Intent intent = new Intent(InputGlucoseActivity.this, DirectInputGlucoseActivity.class);
                intent.putExtra("glucoseUnit", ManagerConstants.Unit.MMOL);
                startActivityForResult(intent, REQUEST_INPUT_GLUCOSE_MMOL);

            }
        });

        // 단위 mmol/L 일때 Wheel

        glucoseMmolNumberPicker.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                glucoseMmol = String.valueOf(number) + "."
                              + String.valueOf(glucoseMmolDecimalPicker.getCurrentNumber());
            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                glucoseMmol = String.valueOf(number) + "."
                              + String.valueOf(glucoseMmolDecimalPicker.getCurrentNumber());
            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

        // 단위 mmol/L 일때 피커 클릭

        glucoseMmolDecimalPicker.setOnWheelClickListener(new WheelPicker.OnWheelClickListener() {

            @Override
            public void onWheelSelected() {
                Intent intent = new Intent(InputGlucoseActivity.this, DirectInputGlucoseActivity.class);
                intent.putExtra("glucoseUnit", ManagerConstants.Unit.MMOL);
                startActivityForResult(intent, REQUEST_INPUT_GLUCOSE_MMOL);

            }
        });

        // 단위 mmol/L 일때 Wheel

        glucoseMmolDecimalPicker.setOnNumberSelectedListener(new WheelNumberPicker.OnNumberSelectedListener() {

            @Override
            public void onNumberSelected(WheelNumberPicker picker, int position, int number) {
                glucoseMmol = String.valueOf(glucoseMmolNumberPicker.getCurrentNumber()) + "."
                              + String.valueOf(number);

            }

            @Override
            public void onNumberCurrentScrolled(WheelNumberPicker picker, int position, int number) {
                glucoseMmol = String.valueOf(glucoseMmolNumberPicker.getCurrentNumber()) + "."
                              + String.valueOf(number);
            }

            @Override
            public void onNumberScrolledNewNumber(WheelNumberPicker picker) {

            }
        });

        initNumberPicker();

        // 저장된 단위에 따른 선택

        if (PreferenceUtil.getGlucoseUnit(InputGlucoseActivity.this).equals(ManagerConstants.Unit.MGDL)) { //단위가 mg/dL 일 경우
            npUnit.setValue(0);
            if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE))) {
                llGlucoseMg.setVisibility(View.VISIBLE);
                llGlucoseMmol.setVisibility(View.GONE);
                glucoseMgdL = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE);
                glucoseMgPicker.setDefaultNumber(Integer.parseInt(glucoseMgdL) - MOVE_PICKER_NUMBER);

            } else {
                llGlucoseMg.setVisibility(View.VISIBLE);
                llGlucoseMmol.setVisibility(View.GONE);
                glucoseMgdL = "100";
                glucoseMgPicker.setDefaultNumber(Integer.parseInt(glucoseMgdL) - MOVE_PICKER_NUMBER);

            }
        } else {
            npUnit.setValue(1);
            if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE))) { //단위가 mmol/L 일 경우
                //단위 피커 mmol/L 선택
                // Intent 로 전달온 값이 mg/dL 단위이기때문에 변환
                String glucose = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE);
                String mmolGlucose = ManagerUtil.mgToMmol(glucose);
                if (mmolGlucose.contains(".")) {
                    String[] glucoses = mmolGlucose.split("\\.");
                    if (glucoses.length > 1) {
                        llGlucoseMg.setVisibility(View.GONE);
                        llGlucoseMmol.setVisibility(View.VISIBLE);
                        glucoseNumberMmolL = glucoses[0];
                        glucoseDecimalMmolL = glucoses[1];
                        glucoseMmolNumberPicker.setDefaultNumber(Integer.parseInt(glucoseNumberMmolL)
                                                                 - MOVE_PICKER_NUMBER);
                        glucoseMmolDecimalPicker.setDefaultNumber(0);

                    }
                } else {
                    llGlucoseMg.setVisibility(View.GONE);
                    llGlucoseMmol.setVisibility(View.VISIBLE);
                    glucoseNumberMmolL = glucose;
                    glucoseDecimalMmolL = "0";
                    glucoseMmolNumberPicker.setDefaultNumber(Integer.parseInt(glucoseNumberMmolL) - MOVE_PICKER_NUMBER);
                    glucoseMmolDecimalPicker.setDefaultNumber(0);

                }
            } else {
                String glucose = "100";
                String mmolGlucose = ManagerUtil.mgToMmol(glucose);
                if (mmolGlucose.contains(".")) {
                    String[] glucoses = mmolGlucose.split("\\.");
                    if (glucoses.length > 1) {
                        llGlucoseMg.setVisibility(View.GONE);
                        llGlucoseMmol.setVisibility(View.VISIBLE);
                        glucoseNumberMmolL = glucoses[0];
                        glucoseDecimalMmolL = glucoses[1];
                        glucoseMmolNumberPicker.setDefaultNumber(Integer.parseInt(glucoseNumberMmolL)
                                                                 - MOVE_PICKER_NUMBER);
                        glucoseMmolDecimalPicker.setDefaultNumber(0);

                    }
                } else {
                    llGlucoseMg.setVisibility(View.GONE);
                    llGlucoseMmol.setVisibility(View.VISIBLE);
                    glucoseNumberMmolL = glucose;
                    glucoseDecimalMmolL = "0";
                    glucoseMmolNumberPicker.setDefaultNumber(Integer.parseInt(glucoseNumberMmolL) - MOVE_PICKER_NUMBER);
                    glucoseMmolDecimalPicker.setDefaultNumber(0);

                }
            }
        }
        rbBeforeMeal.setOnClickListener(this);
        rbAfterMeal.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        llAddMemo.setOnClickListener(this);
        llUpdateMemo.setOnClickListener(this);

        glucoseMmolNumberPicker.setOnResizeListener(this);
        glucoseMmolDecimalPicker.setOnResizeListener(this);
        glucoseMgPicker.setOnResizeListener(this);

    }

    private void moveNumberPicker() {

        Intent intent = getIntent();

        if (PreferenceUtil.getGlucoseUnit(InputGlucoseActivity.this).equals(ManagerConstants.Unit.MGDL)) { //단위가 mg/dL 일 경우
            if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE))) {
                glucoseMgdL = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE);
                glucoseMgPicker.scrollTo(Integer.parseInt(glucoseMgdL));
                isFirst = false;
            } else {
                glucoseMgdL = "100";
                glucoseMgPicker.scrollTo(Integer.parseInt(glucoseMgdL));
                isFirst = false;
            }
        } else {
            count--;
            if (count == 2)
                return;
            if (!TextUtils.isEmpty(intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE))) { //단위가 mmol/L 일 경우
                //단위 피커 mmol/L 선택
                // Intent 로 전달온 값이 mg/dL 단위이기때문에 변환
                String glucose = intent.getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE);
                String mmolGlucose = ManagerUtil.mgToMmol(glucose);
                if (mmolGlucose.contains(".")) {
                    String[] glucoses = mmolGlucose.split("\\.");
                    if (glucoses.length > 1) {
                        glucoseNumberMmolL = glucoses[0];
                        glucoseDecimalMmolL = glucoses[1];
                        glucoseMmolNumberPicker.scrollTo(Integer.parseInt(glucoseNumberMmolL));
                        glucoseMmolDecimalPicker.scrollTo(Integer.parseInt(glucoseDecimalMmolL) + 1);
                    }
                } else {
                    glucoseNumberMmolL = glucose;
                    glucoseDecimalMmolL = "0";
                    glucoseMmolNumberPicker.scrollTo(Integer.parseInt(glucoseNumberMmolL));
                    glucoseMmolDecimalPicker.scrollTo(Integer.parseInt(glucoseDecimalMmolL) + 1);
                }
            } else {
                String glucose = "100";
                String mmolGlucose = ManagerUtil.mgToMmol(glucose);
                if (mmolGlucose.contains(".")) {
                    String[] glucoses = mmolGlucose.split("\\.");
                    if (glucoses.length > 1) {
                        glucoseNumberMmolL = glucoses[0];
                        glucoseDecimalMmolL = glucoses[1];
                        glucoseMmolNumberPicker.scrollTo(Integer.parseInt(glucoseNumberMmolL));
                        glucoseMmolDecimalPicker.scrollTo(Integer.parseInt(glucoseDecimalMmolL) + 1);
                    }
                } else {
                    glucoseNumberMmolL = glucose;
                    glucoseDecimalMmolL = "0";
                    glucoseMmolNumberPicker.scrollTo(Integer.parseInt(glucoseNumberMmolL));
                    glucoseMmolDecimalPicker.scrollTo(Integer.parseInt(glucoseDecimalMmolL) + 1);
                }
            }
        }

        //        isFirst = false;

    }

    @Override
    public void onClick(View v) {
        if (!ManagerUtil.isClicking()) {

            Intent intent;

            switch (v.getId()) {

                //식전(공복) 클릭
                case R.id.rb_before_meal:
                    if (rbBeforeMeal.isSelected()) {
                        //                    rbBeforeMeal.setSelected(false);
                        //                    rbAfterMeal.setSelected(false);
                        //                    ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_d7d5d6));

                    } else {
                        rbBeforeMeal.setSelected(true);
                        rbAfterMeal.setSelected(false);
                        ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_5f5b5d));

                    }
                    break;
                //식후 클릭
                case R.id.rb_after_meal:
                    if (rbAfterMeal.isSelected()) {
                        //                    rbBeforeMeal.setSelected(false);
                        //                    rbAfterMeal.setSelected(false);
                        //                    ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_d7d5d6));

                    } else {
                        rbBeforeMeal.setSelected(false);
                        rbAfterMeal.setSelected(true);
                        ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_5f5b5d));
                    }
                    break;
                // 저장버튼 클릭
                case R.id.btn_save:
                    if (rbBeforeMeal.isSelected()) {
                        eatType = ManagerConstants.EatType.GLUCOSE_BEFORE;
                    } else if (rbAfterMeal.isSelected()) {
                        eatType = ManagerConstants.EatType.GLUCOSE_AFTER;
                    }
                    if (compareDate()) {
                        makeGlucoseManualData();
                    } else {
                        DefaultDialog deleteDialog = new DefaultDialog(InputGlucoseActivity.this,
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
                // 메모 추가 레이아웃 클릭
                case R.id.ll_add_memo:
                    intent = new Intent(InputGlucoseActivity.this, GlucoseMemoActivity.class);
                    startActivityForResult(intent, REQUEST_INPUT_MEMO);
                    break;
                //메모 수정 레이아웃 클릭
                case R.id.ll_update_memo:
                    String memo = tvMemoContent.getText().toString();
                    intent = new Intent(InputGlucoseActivity.this, GlucoseMemoActivity.class);
                    intent.putExtra("update", memo);
                    startActivityForResult(intent, REQUEST_INPUT_MEMO);
                    break;
            }
        }
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

    /**
     * 단위 피커 초기화
     */

    private void initNumberPicker() {

        npUnit.setMinValue(0);
        npUnit.setMaxValue(unit.length - 1);
        npUnit.setDisplayedValues(unit);
        setDividerColor(npUnit);
        npUnit.setWrapSelectorWheel(false);

        npUnit.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                switch (newVal) {
                    case 0: // mg/dL 선택
                        glucoseUnit = ManagerConstants.Unit.MGDL;
                        String mmolValue =
                                         ManagerUtil.mmolToMg(String.valueOf(glucoseMmolNumberPicker.getCurrentNumber()
                                                                             + "."
                                                                             + glucoseMmolDecimalPicker.getCurrentNumber()));
                        glucoseMgPicker.setDefaultNumber(Integer.parseInt(mmolValue));
                        glucoseMgdL = mmolValue;
                        llGlucoseMg.setVisibility(View.VISIBLE);
                        llGlucoseMmol.setVisibility(View.GONE);
                        break;
                    case 1: // mmol/L 선택
                        glucoseUnit = ManagerConstants.Unit.MMOL;
                        String value = ManagerUtil.mgToMmol(String.valueOf(glucoseMgPicker.getCurrentNumber()));
                        if (value.contains(".")) {
                            String[] values = value.split("\\.");
                            glucoseMmolNumberPicker.setDefaultNumber(Integer.parseInt(values[0]));
                            glucoseMmolDecimalPicker.setDefaultNumber(Integer.parseInt(values[1]));
                            glucoseMmol = values[0] + "." + values[1];
                        } else {
                            glucoseMmol = value;
                        }
                        llGlucoseMmol.setVisibility(View.VISIBLE);
                        llGlucoseMg.setVisibility(View.GONE);

                        break;

                }
            }
        });
    }

    /**
     * NumberPicker divider 삭제
     *
     * @param picker
     */

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

    /**
     * 혈압 Data 생성
     */
    private void makeGlucoseManualData() {

        String strMeasureDt = date;
        String strMeasureTime = time;
        String strGlucose = glucoseMgdL;
        if (!ManagerConstants.Unit.MGDL.equals(glucoseUnit)) {
            strGlucose = ManagerUtil.mmolToMg(glucoseMmol);
        }
        String strMealType = eatType;
        String strTypeValue = HealthcareUtil.getGlucoseType(InputGlucoseActivity.this, strGlucose, strMealType);

        //DB에 넣을 Data 작성
        requestMap = new HashMap<String, String>();
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE, strGlucose);
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE, strTypeValue);
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL, strMealType);
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID, "");
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL, "");
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER, "");
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE, memo);
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT, strMeasureDt + strMeasureTime + "00");
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_INS_DT, ManagerUtil.getCurrentDateTime());
        requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                       ManagerConstants.ServerSyncYN.SERVER_SYNC_N);

        //DB에 저장 및 Server로 전송 AsyncTask 실행
        cGlucoseAsync = new CreateGlucoseAsync();
        cGlucoseAsync.execute();

    }

    @Override
    public void onResize(int id, int xNew, int yNew, int xOld, int yOld) {
        if (isFirst)
            moveNumberPicker();
    }

    /**
     * 혈당 Data 등록 Async
     */
    private class CreateGlucoseAsync extends AsyncTask<Void, Void, JSONObject> {

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

                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(InputGlucoseActivity.this));

                //DB 저장
                int nRow = glucoseService.createGlucoseData(requestMap);

                if (nRow > 0) {

                    strDbSeq = String.valueOf(nRow);

                    //Server 전송
                    data.put(ManagerConstants.RequestParamName.INS_DT,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                    data.put(ManagerConstants.RequestParamName.GLUCOSE,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));
                    data.put(ManagerConstants.RequestParamName.GLUCOSE_MEAL,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL));
                    data.put(ManagerConstants.RequestParamName.GLUCOSE_TYPE,
                             requestMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE));
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

                    resultJSON = glucoseService.sendGlucoseData(data);

                    if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                         .toString())) {
                            //Server 전송 완료

                            //DB에 Server 전송 Update
                            glucoseService.updateSendToServerYN(strDbSeq);

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
                PreferenceUtil.setGlucoseUnit(InputGlucoseActivity.this, glucoseUnit);
                setResult(RESULT_OK);
                finish();
            } catch (Exception e) {
                e.printStackTrace();

                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_INPUT_GLUCOSE_MGDL) {
                String glucose = data.getStringExtra(GLUCOSE);
                if (glucose != null) {
                    glucoseMgdL = glucose;
                    glucoseMgPicker.setDefaultNumber(Integer.parseInt(glucose));
                }
            } else if (requestCode == REQUEST_INPUT_GLUCOSE_MMOL) {
                String glucose = data.getStringExtra(GLUCOSE);
                if (glucose != null) {
                    glucoseMmol = glucose;
                    if (glucose.contains(".")) {
                        String[] glucoses = glucose.split("\\.");
                        glucoseMmolNumberPicker.setDefaultNumber(Integer.parseInt(glucoses[0]));
                        glucoseMmolDecimalPicker.setDefaultNumber(Integer.parseInt(glucoses[1]));
                    } else {
                        glucoseMmolNumberPicker.setDefaultNumber(Integer.parseInt(glucose));
                        glucoseMmolDecimalPicker.setDefaultNumber(0);
                    }
                }
            } else if (requestCode == REQUEST_INPUT_MEMO) {
                String glucoseMemo = data.getStringExtra(GLUCOSE_MEMO);
                if (!TextUtils.isEmpty(glucoseMemo)) {
                    llAddMemo.setVisibility(View.GONE);
                    llUpdateMemo.setVisibility(View.VISIBLE);
                    tvMemoContent.setText(glucoseMemo);

                    memo = glucoseMemo;
                }
            }

        }
    }

}
