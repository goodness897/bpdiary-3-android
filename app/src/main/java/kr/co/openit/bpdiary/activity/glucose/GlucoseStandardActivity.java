package kr.co.openit.bpdiary.activity.glucose;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

import static kr.co.openit.bpdiary.utils.PhoneUtil.convertDpToPixel;


public class GlucoseStandardActivity extends BaseActivity {

    /**
     * 목표 설정 Button
     */
    private Button btnSetGoal;

    /**
     * 닫기 Button
     */
    private Button btnClose;

    /**
     * 혈당 목표설정 리퀘스트 코드
     */

    private static final int REQUEST_GLUCOSE_SET_GOAL = 100;

    /**
     * 식전 저혈당 Layout
     */
    private RelativeLayout rlBeforeUnder;

    /**
     * 식전 저혈당 View
     */
    private View rlBeforeNormal;

    /**
     * 식전 저혈당 Layout Width
     */
    private float underBeforeLayoutWidth;

    /**
     * 식전 정상 Layout Width 초기값
     */
    private float normalBeforeLayoutWidth = 100;

    /**
     * 식전 저혈당 Layout Width
     */
    private static final double UNDER_BEFORE_LAYOUT_WIDTH = 123;

    /**
     * 식전 최소~최대 범위 TextView
     */
    private TextView tvBeforeMeal;

    /**
     * 식후 최소~최대 범위 TextView
     */
    private TextView tvAfterMeal;

    /**
     * 식후 저혈당 Layout
     */

    private RelativeLayout rlAfterUnder;

    /**
     * 식후 정상 View
     */
    private View rlAfterNormal;

    /**
     * 식후 저혈당 Layout Width
     */
    private float underAfterLayoutWidth;

    /**
     * 식후 정상 Layout Width
     */
    private float normalAfterLayoutWidth = 100;

    /**
     * 식후 저혈당 Layout Width
     */
    private static final double UNDER_AFTER_LAYOUT_WIDTH = 184.8;

    /**
     * 식전/식후 공통 Height
     */

    private float commonHeight = 20;

    /**
     * 식전 목표설정 혈당 최저값
     */
    private int inputMinGlucoseBeforeMeal;

    /**
     * 식전 목표설정 혈당 최대값
     */
    private int inputMaxGlucoseBeforeMeal;

    /**
     * 식후 목표설정 혈당 최저값
     */
    private int inputMinGlucoseAfterMeal;

    /**
     * 식후 목표설정 혈당 최대값
     */
    private int inputMaxGlucoseAfterMeal;

    /**
     * 식전 텍스트 표시 Layout
     */
    private RelativeLayout rlBeforeTextUnder;

    /**
     * 식후 텍스트 표시 Layout
     */
    private RelativeLayout rlAfterTextUnder;

    /**
     * 식전 혈당 목표설정 최소값(mmol/L)
     */
    private double inputMmolMinGlucoseBeforeMeal;

    /**
     * 식전 혈당 목표설정 최대값(mmol/L)
     */
    private double inputMmolMaxGlucoseBeforeMeal;

    /**
     * 식후 혈당 목표설정 최소값(mmol/L)
     */
    private double inputMmolMinGlucoseAfterMeal;

    /**
     * 식후 혈당 목표설정 최대값(mmol/L)
     */
    private double inputMmolMaxGlucoseAfterMeal;

    /**
     * 단위 mg 일때 식전 정상 최소값
     */
    private static final int GLUCOSE_MG_BEFORE_MEAL_MIN = 80;

    /**
     * 단위 mmol 일때 식전 정상 최소값
     */
    private static final double GLUCOSE_MMOL_BEFORE_MEAL_MIN = 4.4;

    /**
     * 단위 mg 일때 식후 정상 최소값
     */
    private static final int GLUCOSE_MG_AFTER_MEAL_MIN = 120;

    /**
     * 단위 mmol 일때 식후 정상 최소값
     */
    private static final double GLUCOSE_MMOL_AFTER_MEAL_MIN = 6.6;

    /**
     * 단위 mg 일때 화면 그리는 비율
     */
    private static final double GLUCOSE_MG_MEAL_RATE = 1.54;

    /**
     * 단위 mmol 일때 화면 그리는 비율
     */
    private static final double GLUCOSE_MMOL_MEAL_RATE = 27.75;

    /**
     * 레이아웃 커서 레이아웃 마진값
     */
    private static final int CURSOR_DIFFERENCE_MARGIN = 5;

    /**
     * 식전/식후 타입
     */

    private static int TYPE_BEFORE = 0;

    /**
     * 식전/식후 타입
     */
    private static int TYPE_AFTER = 1;

    private TextView tvBeforeMin;

    private TextView tvBeforeMax;

    private TextView tvAfterMin;

    private TextView tvAfterMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glucose_standard);
        setLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();
        initGraph();
    }

    /**
     * 레이아웃 세팅
     */

    private void setLayout() {

        rlBeforeNormal = (View)findViewById(R.id.rl_before_normal);

        rlBeforeUnder = (RelativeLayout)findViewById(R.id.rl_before_under);

        rlBeforeTextUnder = (RelativeLayout)findViewById(R.id.rl_under_meal);

        rlAfterTextUnder = (RelativeLayout)findViewById(R.id.rl_after_meal);

        rlAfterNormal = (View)findViewById(R.id.rl_after_normal);

        rlAfterUnder = (RelativeLayout)findViewById(R.id.rl_after_under);

        tvBeforeMeal = (TextView)findViewById(R.id.tv_before_meal);

        tvAfterMeal = (TextView)findViewById(R.id.tv_after_meal);

        tvBeforeMin = (TextView)findViewById(R.id.tv_under_meal_min);

        tvBeforeMax = (TextView)findViewById(R.id.tv_under_meal_max);

        tvAfterMin = (TextView)findViewById(R.id.tv_after_meal_min);

        tvAfterMax = (TextView)findViewById(R.id.tv_after_meal_max);

        btnSetGoal = (Button)findViewById(R.id.btn_setting_goal);

        btnClose = (Button)findViewById(R.id.btn_close);



        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    finish();
                }
            }
        });

        btnSetGoal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(GlucoseStandardActivity.this, GlucoseSetGoalActivity.class);
                    startActivityForResult(intent, REQUEST_GLUCOSE_SET_GOAL);
                }
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    finish();
                }
            }
        });

        initGraph();

        setTextLabel();

    }

    private void setTextLabel() {
        if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseStandardActivity.this))) {
            tvBeforeMin.setText("1");
            tvBeforeMax.setText("");
            tvAfterMin.setText("1");
            tvAfterMax.setText("");

        } else {
            tvBeforeMin.setText("0.1");
            tvBeforeMax.setText("");
            tvAfterMin.setText("0.1");
            tvAfterMax.setText("");
        }
    }

    /**
     * 식전 식후 뷰 그리기
     *
     * @param type 식전, 식후
     * @param minValue 목표설정 최소값
     * @param maxValue 목표설정 최대값
     * @param rate 목표설정 비율(단위에 따른 비율이 다름)
     */

    private void drawGlucoseChart(int type, double minValue, double maxValue, double rate) {

        double maxGlucose = 0;
        double minGlucose = 0;
        double unitMinValue = 0;

        switch (type) {
            case 0: //식전
                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseStandardActivity.this))) {
                    maxGlucose = (int)maxValue;
                    minGlucose = (int)minValue;
                    unitMinValue = GLUCOSE_MG_BEFORE_MEAL_MIN;

                } else {
                    maxGlucose = maxValue;
                    minGlucose = minValue;
                    unitMinValue = GLUCOSE_MMOL_BEFORE_MEAL_MIN;
                }
                normalBeforeLayoutWidth = (float)((maxGlucose - minGlucose) * rate);
                if (minGlucose <= unitMinValue) {
                    underBeforeLayoutWidth = (float)(UNDER_BEFORE_LAYOUT_WIDTH - ((unitMinValue - minGlucose) * rate));
                } else if (minGlucose > unitMinValue) {
                    underBeforeLayoutWidth = (float)(UNDER_BEFORE_LAYOUT_WIDTH + ((minGlucose - unitMinValue) * rate));
                } else {
                    underBeforeLayoutWidth = (int)UNDER_BEFORE_LAYOUT_WIDTH;
                }
                break;
            case 1: // 식후
                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseStandardActivity.this))) {
                    maxGlucose = (int)maxValue;
                    minGlucose = (int)minValue;
                    unitMinValue = GLUCOSE_MG_AFTER_MEAL_MIN;

                } else {
                    maxGlucose = maxValue;
                    minGlucose = minValue;
                    unitMinValue = GLUCOSE_MMOL_AFTER_MEAL_MIN;
                }
                normalAfterLayoutWidth = (float)((maxGlucose - minGlucose) * rate);

                if (minGlucose <= unitMinValue) {
                    underAfterLayoutWidth = (float)(UNDER_AFTER_LAYOUT_WIDTH - ((unitMinValue - minGlucose) * rate));
                } else if (minGlucose > unitMinValue) {
                    underAfterLayoutWidth = (float)(UNDER_AFTER_LAYOUT_WIDTH + ((minGlucose - unitMinValue) * rate));
                } else {
                    underAfterLayoutWidth = (int)UNDER_AFTER_LAYOUT_WIDTH;
                }
                break;
        }

    }

    /**
     * 식전 정상 뷰 설정
     */
    private void setBeforeMealNormalView() {

        ViewGroup.LayoutParams rlNormalLayoutParams = rlBeforeNormal.getLayoutParams();
        ViewGroup.LayoutParams rlUnderLayoutParams = rlBeforeUnder.getLayoutParams();
        ViewGroup.LayoutParams tvMeal = tvBeforeMeal.getLayoutParams();
        ViewGroup.LayoutParams rlTextUnderLayoutParams = rlBeforeTextUnder.getLayoutParams();

        if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseStandardActivity.this))) {
            drawGlucoseChart(TYPE_BEFORE, inputMinGlucoseBeforeMeal, inputMaxGlucoseBeforeMeal, GLUCOSE_MG_MEAL_RATE);

        } else {
            drawGlucoseChart(TYPE_BEFORE,
                             inputMmolMinGlucoseBeforeMeal,
                             inputMmolMaxGlucoseBeforeMeal,
                             GLUCOSE_MMOL_MEAL_RATE);
        }

        //        normalBeforeLayoutWidth = (float)((inputMaxGlucoseBeforeMeal - inputMinGlucoseBeforeMeal) * 1.54);
        //
        //        if (inputMinGlucoseBeforeMeal <= 80) {
        //            underBeforeLayoutWidth = (float)(UNDER_BEFORE_LAYOUT_WIDTH - ((80 - inputMinGlucoseBeforeMeal) * 1.54));
        //
        //        } else if (inputMinGlucoseBeforeMeal > 80) {
        //            underBeforeLayoutWidth = (float)(UNDER_BEFORE_LAYOUT_WIDTH + ((inputMinGlucoseBeforeMeal - 80) * 1.54));
        //
        //        } else {
        //            underBeforeLayoutWidth = (int)UNDER_BEFORE_LAYOUT_WIDTH;
        //        }

        rlNormalLayoutParams.width = convertDpToPixel(normalBeforeLayoutWidth);
        rlNormalLayoutParams.height = convertDpToPixel(commonHeight);

        rlUnderLayoutParams.width = convertDpToPixel(underBeforeLayoutWidth);
        rlUnderLayoutParams.height = convertDpToPixel(commonHeight);

        //텍스트 중앙정렬을 위한 Margin 값
        if (normalBeforeLayoutWidth <= 40) {
            tvMeal.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            tvMeal.height = convertDpToPixel(commonHeight);
            if (inputMinGlucoseBeforeMeal >= 100 && inputMaxGlucoseBeforeMeal >= 100) {
                rlTextUnderLayoutParams.width = convertDpToPixel(underBeforeLayoutWidth - 10);
                rlTextUnderLayoutParams.height = convertDpToPixel(commonHeight);
            } else if (inputMinGlucoseBeforeMeal < 100 && inputMaxGlucoseBeforeMeal >= 100) {
                rlTextUnderLayoutParams.width = convertDpToPixel(underBeforeLayoutWidth - 7);
                rlTextUnderLayoutParams.height = convertDpToPixel(commonHeight);
            } else {
                rlTextUnderLayoutParams.width = convertDpToPixel(underBeforeLayoutWidth - 3);
                rlTextUnderLayoutParams.height = convertDpToPixel(commonHeight);
            }
        } else {
            tvMeal.width = convertDpToPixel(normalBeforeLayoutWidth);
            tvMeal.height = convertDpToPixel(commonHeight);
            rlTextUnderLayoutParams.width = convertDpToPixel(underBeforeLayoutWidth);
            rlTextUnderLayoutParams.height = convertDpToPixel(commonHeight);
        }

        rlBeforeNormal.setLayoutParams(rlNormalLayoutParams);
        rlBeforeUnder.setLayoutParams(rlUnderLayoutParams);
        tvBeforeMeal.setLayoutParams(tvMeal);
        rlBeforeTextUnder.setLayoutParams(rlTextUnderLayoutParams);

    }

    /**
     * 식후 정상 뷰 설정
     */
    private void setAfterMealNormalView() {

        ViewGroup.LayoutParams rlNormalLayoutParams = rlAfterNormal.getLayoutParams();
        ViewGroup.LayoutParams rlUnderLayoutParams = rlAfterUnder.getLayoutParams();
        ViewGroup.LayoutParams tvMeal = tvAfterMeal.getLayoutParams();
        ViewGroup.LayoutParams rlTextUnderLayoutParams = rlAfterTextUnder.getLayoutParams();

        if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseStandardActivity.this))) { //단위 mg/dL 일때
            drawGlucoseChart(TYPE_AFTER, inputMinGlucoseAfterMeal, inputMaxGlucoseAfterMeal, GLUCOSE_MG_MEAL_RATE);
        } else { //단위 mmol 일때
            drawGlucoseChart(TYPE_AFTER,
                             inputMmolMinGlucoseAfterMeal,
                             inputMmolMaxGlucoseAfterMeal,
                             GLUCOSE_MMOL_MEAL_RATE);
        }

        //        normalAfterLayoutWidth = (float)((inputMaxGlucoseAfterMeal - inputMinGlucoseAfterMeal) * 3.08);
        //
        //        if (inputMinGlucoseAfterMeal <= 120) {
        //            underAfterLayoutWidth = (float)(UNDER_AFTER_LAYOUT_WIDTH - ((120 - inputMinGlucoseAfterMeal) * 3.08));
        //
        //        } else if (inputMinGlucoseAfterMeal > 120) {
        //            underAfterLayoutWidth = (float)(UNDER_AFTER_LAYOUT_WIDTH + ((inputMinGlucoseAfterMeal - 120) * 3.08));
        //
        //        } else {
        //            underAfterLayoutWidth = (int)UNDER_AFTER_LAYOUT_WIDTH;
        //        }

        rlNormalLayoutParams.width = convertDpToPixel(normalAfterLayoutWidth);
        rlNormalLayoutParams.height = convertDpToPixel(commonHeight);

        rlUnderLayoutParams.width = convertDpToPixel(underAfterLayoutWidth);
        rlUnderLayoutParams.height = convertDpToPixel(commonHeight);

        //텍스트 중앙정렬을 위한 Margin 값
        if (normalAfterLayoutWidth <= 40) {
            tvMeal.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            tvMeal.height = ViewGroup.LayoutParams.MATCH_PARENT;


            if (inputMinGlucoseAfterMeal >= 100 && inputMaxGlucoseAfterMeal >= 100) {
                rlTextUnderLayoutParams.width = convertDpToPixel(underAfterLayoutWidth - 10);
                rlTextUnderLayoutParams.height = convertDpToPixel(40);
            } else if (inputMinGlucoseAfterMeal < 100 && inputMaxGlucoseAfterMeal >= 100) {
                rlTextUnderLayoutParams.width = convertDpToPixel(underAfterLayoutWidth - 7);
                rlTextUnderLayoutParams.height = convertDpToPixel(40);
            } else {
                rlTextUnderLayoutParams.width = convertDpToPixel(underAfterLayoutWidth - 3);
                rlTextUnderLayoutParams.height = convertDpToPixel(40);
            }
        } else {
            tvMeal.width = convertDpToPixel(normalAfterLayoutWidth);
            tvMeal.height = ViewGroup.LayoutParams.MATCH_PARENT;

            rlTextUnderLayoutParams.width = convertDpToPixel(underAfterLayoutWidth);
            rlTextUnderLayoutParams.height = convertDpToPixel(40);
        }

        rlAfterNormal.setLayoutParams(rlNormalLayoutParams);
        rlAfterUnder.setLayoutParams(rlUnderLayoutParams);
        tvAfterMeal.setLayoutParams(tvMeal);
        rlAfterTextUnder.setLayoutParams(rlTextUnderLayoutParams);

    }

    /**
     * Preference 에서 받아온 값을 이용한 뷰 설정
     */

    private void initGraph() {

        inputMinGlucoseBeforeMeal = PreferenceUtil.getGlucoseMinBeforeMeal(GlucoseStandardActivity.this);
        inputMaxGlucoseBeforeMeal = PreferenceUtil.getGlucoseMaxBeforeMeal(GlucoseStandardActivity.this);
        inputMinGlucoseAfterMeal = PreferenceUtil.getGlucoseMinAfterMeal(GlucoseStandardActivity.this);
        inputMaxGlucoseAfterMeal = PreferenceUtil.getGlucoseMaxAfterMeal(GlucoseStandardActivity.this);

        inputMmolMinGlucoseBeforeMeal =
                                      Double.parseDouble(ManagerUtil.mgToMmol(String.valueOf(inputMinGlucoseBeforeMeal)));
        inputMmolMaxGlucoseBeforeMeal =
                                      Double.parseDouble(ManagerUtil.mgToMmol(String.valueOf(inputMaxGlucoseBeforeMeal)));
        inputMmolMinGlucoseAfterMeal =
                                     Double.parseDouble(ManagerUtil.mgToMmol(String.valueOf(inputMinGlucoseAfterMeal)));
        inputMmolMaxGlucoseAfterMeal =
                                     Double.parseDouble(ManagerUtil.mgToMmol(String.valueOf(inputMaxGlucoseAfterMeal)));

        if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseStandardActivity.this))) {

            tvBeforeMeal.setText(String.valueOf(inputMinGlucoseBeforeMeal) + " ~ "
                                 + String.valueOf(inputMaxGlucoseBeforeMeal));
            tvAfterMeal.setText(String.valueOf(inputMinGlucoseAfterMeal) + " ~ "
                                + String.valueOf(inputMaxGlucoseAfterMeal));

        } else {

            tvBeforeMeal.setText(String.valueOf(inputMmolMinGlucoseBeforeMeal) + " ~ "
                                 + String.valueOf(inputMmolMaxGlucoseBeforeMeal));
            tvAfterMeal.setText(String.valueOf(inputMmolMinGlucoseAfterMeal) + " ~ "
                                + String.valueOf(inputMmolMaxGlucoseAfterMeal));

        }

        setBeforeMealNormalView();
        setAfterMealNormalView();
    }
}
