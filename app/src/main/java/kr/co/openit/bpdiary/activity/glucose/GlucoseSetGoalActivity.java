package kr.co.openit.bpdiary.activity.glucose;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.customview.RangeSeekBar;
import kr.co.openit.bpdiary.dialog.CustomProgressDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.services.GlucoseService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

import static kr.co.openit.bpdiary.utils.PhoneUtil.convertDpToPixel;

public class GlucoseSetGoalActivity extends BaseActivity {

    private LinearLayout llEmptyView;

    /**
     * User 이름
     */

    private TextView tvUserName;

    /**
     * 초기화 버튼
     */
    private Button btnInit;

    /**
     * 적용 버튼
     */
    private Button btnApply;

    /**
     * 식전 RangeSeekBar(최소 60 최대 150)
     */
    private RangeSeekBar rsbBeforeMeal;

    /**
     * 식후 RangeSeekBar(최소 100 최대 200)
     */
    private RangeSeekBar rsbAfterMeal;

    /**
     * 식전 저혈당 레이아웃
     */
    private RelativeLayout rlBeforeUnder;

    /**
     * 식전 정상 레이아웃
     */
    private RelativeLayout rlBeforeNormal;

    /**
     * 식전 커서 설정을 위한 레이아웃
     */
    private RelativeLayout rlBeforeGoal;

    /**
     * 식전 커서 설정을 위한 레이아웃
     */
    private RelativeLayout rlBeforeUnderGoal;

    /**
     * 식전 저혈당 레이아웃 Width
     */
    private float underBeforeLayoutWidth;

    /**
     * 식전 정상 레이아웃 Width
     */
    private float normalBeforeLayoutWidth = 100;

    /**
     * 식전 저혈당 Width(기준)
     */
    private static final double UNDER_BEFORE_LAYOUT_WIDTH = 123;

    /**
     * 식전 커서 설정을 위한 레이아웃 Width
     */
    private float goalBeforeUnderLayoutWidth;

    /**
     * 식후 저혈당 레이아웃
     */
    private RelativeLayout rlAfterUnder;

    /**
     * 식후 정상 레이아웃
     */
    private RelativeLayout rlAfterNormal;

    /**
     * 식후 커서 설정을 위한 레이아웃
     */
    private RelativeLayout rlAfterGoal;

    /**
     * 식후 커서 설정을 위한 레이아웃
     */
    private RelativeLayout rlAfterUnderGoal;

    /**
     * 식후 저혈당 레이아웃 Width
     */
    private float underAfterLayoutWidth;

    /**
     * 식후 정상 레이아웃 Width
     */
    private float normalAfterLayoutWidth = 100;

    /**
     * 식후 저혈당 레이아웃 Width(기준)
     */
    private static final double UNDER_AFTER_LAYOUT_WIDTH = 184.8;

    /**
     * 식후 커서 설정을 위한 레이아웃 Width
     */
    private float goalAfterUnderLayoutWidth;

    /**
     * 공통 Height
     */
    private static final float COMMON_HEIGHT = 38;

    /**
     * 식전 혈당 목표설정 최소값(mg/dL)
     */
    private int inputMinGlucoseBeforeMeal;

    /**
     * 식전 혈당 목표설정 최대값(mg/dL)
     */
    private int inputMaxGlucoseBeforeMeal;

    /**
     * 식후 혈당 목표설정 최소값(mg/dL)
     */
    private int inputMinGlucoseAfterMeal;

    /**
     * 식후 혈당 목표설정 최대값(mg/dL)
     */
    private int inputMaxGlucoseAfterMeal;

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
     * 식전 단위 텍스트뷰
     */
    private TextView tvBeforeUnit;

    /**
     * 식후 단위 텍스트뷰
     */
    private TextView tvAfterUnit;

    /**
     * 식전 정상 최소값
     */

    private TextView tvBeforeNormalMin;

    /**
     * 식전 정상 최대값
     */
    private TextView tvBeforeNormalMax;

    /**
     * 식후 정상 최소값
     */
    private TextView tvAfterNormalMin;

    /**
     * 식후 정상 최대값
     */
    private TextView tvAfterNormalMax;

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

    private static final int TYPE_BEFORE = 0;

    /**
     * 식전/식후 타입
     */
    private static final int TYPE_AFTER = 1;

    private static final int DEFAULT_BEFORE_MEAL_MIN = 81;

    private static final int DEFAULT_BEFORE_MEAL_MAX = 120;

    private static final int DEFAULT_AFTER_MEAL_MIN = 121;

    private static final int DEFAULT_AFTER_MEAL_MAX = 160;

    /**
     * 작업 프로그래스
     */
    private CustomProgressDialog mConnectionProgressDialog = null;

    private GlucoseService glucoseService;

    private ModGoalSync modGoalSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glucose_set_goal);

        context = GlucoseSetGoalActivity.this;

        AnalyticsUtil.sendScene(GlucoseSetGoalActivity.this, "3_혈당 목표설정");

        initToolbar(getString(R.string.bp_set_goal));

        setLayout();

        mConnectionProgressDialog = new CustomProgressDialog(context);
        mConnectionProgressDialog.setCancelable(false);
        mConnectionProgressDialog.setCanceledOnTouchOutside(false);

        glucoseService = new GlucoseService(context);

        String userName = PreferenceUtil.getDecFirstName(context) + " " + PreferenceUtil.getDecLastName(context) + " ";

        setGoalGuildeText(userName);

    }

    /**
     * 정상 뷰 그리기
     */

    private void setBeforeMealNormalView() {

        ViewGroup.LayoutParams rlNormalLayoutParams = rlBeforeNormal.getLayoutParams();
        ViewGroup.LayoutParams rlUnderLayoutParams = rlBeforeUnder.getLayoutParams();
        ViewGroup.LayoutParams rlGoalLayoutParams = rlBeforeGoal.getLayoutParams();
        ViewGroup.LayoutParams rlUnderGoalLayoutParams = rlBeforeUnderGoal.getLayoutParams();

        if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseSetGoalActivity.this))) {
            drawGlucoseChart(TYPE_BEFORE, inputMinGlucoseBeforeMeal, inputMaxGlucoseBeforeMeal, GLUCOSE_MG_MEAL_RATE);

        } else {
            drawGlucoseChart(TYPE_BEFORE,
                             inputMmolMinGlucoseBeforeMeal,
                             inputMmolMaxGlucoseBeforeMeal,
                             GLUCOSE_MMOL_MEAL_RATE);
        }

        rlNormalLayoutParams.width = convertDpToPixel(normalBeforeLayoutWidth);
        rlNormalLayoutParams.height = convertDpToPixel(COMMON_HEIGHT);

        rlUnderLayoutParams.width = convertDpToPixel(underBeforeLayoutWidth);
        rlUnderLayoutParams.height = convertDpToPixel(COMMON_HEIGHT);

        rlGoalLayoutParams.width = convertDpToPixel(normalBeforeLayoutWidth);
        rlGoalLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        rlUnderGoalLayoutParams.width = convertDpToPixel(goalBeforeUnderLayoutWidth);
        rlUnderGoalLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        rlBeforeNormal.setLayoutParams(rlNormalLayoutParams);
        rlBeforeGoal.setLayoutParams(rlGoalLayoutParams);
        rlBeforeUnder.setLayoutParams(rlUnderLayoutParams);

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
                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseSetGoalActivity.this))) {
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
                    goalBeforeUnderLayoutWidth = (float)((UNDER_BEFORE_LAYOUT_WIDTH + CURSOR_DIFFERENCE_MARGIN)
                                                         - ((unitMinValue - minGlucose) * rate));
                } else if (minGlucose > unitMinValue) {
                    underBeforeLayoutWidth = (float)(UNDER_BEFORE_LAYOUT_WIDTH + ((minGlucose - unitMinValue) * rate));
                    goalBeforeUnderLayoutWidth = (float)((UNDER_BEFORE_LAYOUT_WIDTH + CURSOR_DIFFERENCE_MARGIN)
                                                         + ((minGlucose - unitMinValue) * rate));
                } else {
                    underBeforeLayoutWidth = (int)UNDER_BEFORE_LAYOUT_WIDTH;
                    goalBeforeUnderLayoutWidth = (int)(UNDER_BEFORE_LAYOUT_WIDTH + CURSOR_DIFFERENCE_MARGIN);
                }
                break;
            case 1: // 식후
                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseSetGoalActivity.this))) {
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
                    goalAfterUnderLayoutWidth = (float)((UNDER_AFTER_LAYOUT_WIDTH + CURSOR_DIFFERENCE_MARGIN)
                                                        - ((unitMinValue - minGlucose) * rate));
                } else if (minGlucose > unitMinValue) {
                    underAfterLayoutWidth = (float)(UNDER_AFTER_LAYOUT_WIDTH + ((minGlucose - unitMinValue) * rate));
                    goalAfterUnderLayoutWidth = (float)((UNDER_AFTER_LAYOUT_WIDTH + CURSOR_DIFFERENCE_MARGIN)
                                                        + ((minGlucose - unitMinValue) * rate));
                } else {
                    underAfterLayoutWidth = (int)UNDER_AFTER_LAYOUT_WIDTH;
                    goalAfterUnderLayoutWidth = (int)(UNDER_AFTER_LAYOUT_WIDTH + CURSOR_DIFFERENCE_MARGIN);
                }
                break;
        }

    }

    /**
     * 혈당 식후 정상 뷰 그리기
     */

    private void setAfterMealNormalView() {

        ViewGroup.LayoutParams rlNormalLayoutParams = rlAfterNormal.getLayoutParams();
        ViewGroup.LayoutParams rlUnderLayoutParams = rlAfterUnder.getLayoutParams();
        ViewGroup.LayoutParams rlGoalLayoutParams = rlAfterGoal.getLayoutParams();
        ViewGroup.LayoutParams rlUnderGoalLayoutParams = rlAfterUnderGoal.getLayoutParams();

        if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseSetGoalActivity.this))) { //단위 mg/dL 일때
            drawGlucoseChart(TYPE_AFTER, inputMinGlucoseAfterMeal, inputMaxGlucoseAfterMeal, GLUCOSE_MG_MEAL_RATE);
        } else { //단위 mmol 일때
            drawGlucoseChart(TYPE_AFTER,
                             inputMmolMinGlucoseAfterMeal,
                             inputMmolMaxGlucoseAfterMeal,
                             GLUCOSE_MMOL_MEAL_RATE);
        }

        rlNormalLayoutParams.width = convertDpToPixel(normalAfterLayoutWidth);
        rlNormalLayoutParams.height = convertDpToPixel(COMMON_HEIGHT);

        rlUnderLayoutParams.width = convertDpToPixel(underAfterLayoutWidth);
        rlUnderLayoutParams.height = convertDpToPixel(COMMON_HEIGHT);

        rlGoalLayoutParams.width = convertDpToPixel(normalAfterLayoutWidth);
        rlGoalLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        rlUnderGoalLayoutParams.width = convertDpToPixel(goalAfterUnderLayoutWidth);
        rlUnderGoalLayoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        rlAfterNormal.setLayoutParams(rlNormalLayoutParams);
        rlAfterGoal.setLayoutParams(rlGoalLayoutParams);
        rlAfterUnder.setLayoutParams(rlUnderLayoutParams);

    }

    /**
     * UserName에 맞게 "목표를 자유롭게 조절해보세요" 텍스트 변경
     *
     * @param userName
     */
    private void setGoalGuildeText(String userName) {
        StringBuilder sb = new StringBuilder();
        sb
          // 서버로부터 받게될 유저네임 들어가는 부분
          .append(userName).append(tvUserName.getText().toString());
        final SpannableStringBuilder sp = new SpannableStringBuilder(sb.toString());
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_1869c7)),
                   0,
                   userName.length(),
                   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvUserName.setText(sp);
    }

    /**
     * 레이아웃 셋팅
     */
    private void setLayout() {

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);

        tvUserName = (TextView)findViewById(R.id.tv_user_name);

        btnInit = (Button)findViewById(R.id.btn_init);

        btnApply = (Button)findViewById(R.id.btn_apply);

        rsbBeforeMeal = (RangeSeekBar)findViewById(R.id.rsb_before_meal);

        rsbAfterMeal = (RangeSeekBar)findViewById(R.id.rsb_after_meal);

        rlBeforeNormal = (RelativeLayout)findViewById(R.id.rl_before_normal);

        rlBeforeUnder = (RelativeLayout)findViewById(R.id.rl_before_under);

        rlBeforeGoal = (RelativeLayout)findViewById(R.id.rl_goal_triangle);

        rlBeforeUnderGoal = (RelativeLayout)findViewById(R.id.rl_under_goal);

        rlAfterNormal = (RelativeLayout)findViewById(R.id.rl_after_normal);

        rlAfterUnder = (RelativeLayout)findViewById(R.id.rl_after_under);

        rlAfterGoal = (RelativeLayout)findViewById(R.id.rl_goal_after_triangle);

        rlAfterUnderGoal = (RelativeLayout)findViewById(R.id.rl_under_after_goal);

        tvBeforeUnit = (TextView)findViewById(R.id.tv_before_unit);

        tvAfterUnit = (TextView)findViewById(R.id.tv_after_unit);

        tvBeforeNormalMin = (TextView)findViewById(R.id.tv_before_normal_min);

        tvBeforeNormalMax = (TextView)findViewById(R.id.tv_before_normal_max);

        tvAfterNormalMin = (TextView)findViewById(R.id.tv_after_normal_min);

        tvAfterNormalMax = (TextView)findViewById(R.id.tv_after_normal_max);

        //저장된 단위에 따른 단위 변화
        if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseSetGoalActivity.this))) {
            tvBeforeUnit.setText("(" + ManagerConstants.Unit.MGDL + ")");
            tvAfterUnit.setText("(" + ManagerConstants.Unit.MGDL + ")");
            tvBeforeNormalMin.setText(String.valueOf(80));
            tvBeforeNormalMax.setText(String.valueOf(120));
            tvAfterNormalMin.setText(String.valueOf(120));
            tvAfterNormalMax.setText(String.valueOf(160));
        } else {
            tvBeforeUnit.setText("(" + ManagerConstants.Unit.MMOL + ")");
            tvAfterUnit.setText("(" + ManagerConstants.Unit.MMOL + ")");
            tvBeforeNormalMin.setText(String.valueOf(4.4));
            tvBeforeNormalMax.setText(String.valueOf(6.6));
            tvAfterNormalMin.setText(String.valueOf(6.6));
            tvAfterNormalMax.setText(String.valueOf(8.8));
        }

        //초기화 버튼 클릭

        btnInit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    setDefaultValue();
                }
            }
        });
        //적용 버튼 클릭
        btnApply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (BPDiaryApplication.isNetworkState(GlucoseSetGoalActivity.this)) {

                        modGoalSync = new ModGoalSync();
                        modGoalSync.execute();

                    } else {
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                                                      new DefaultOneButtonDialog(GlucoseSetGoalActivity.this,
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
            }
        });

        initSeekBar();

        rsbBeforeMeal.setNotifyWhileDragging(true);
        rsbBeforeMeal.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Number minValue, Number maxValue) {

                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseSetGoalActivity.this))) {
                    inputMinGlucoseBeforeMeal = (int)minValue;
                    inputMaxGlucoseBeforeMeal = (int)maxValue;
                } else {
                    inputMmolMinGlucoseBeforeMeal = (double)minValue;
                    inputMmolMaxGlucoseBeforeMeal = (double)maxValue;
                }

                setBeforeMealNormalView();
            }
        });

        rsbAfterMeal.setNotifyWhileDragging(true);
        rsbAfterMeal.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Number minValue, Number maxValue) {

                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseSetGoalActivity.this))) {
                    inputMinGlucoseAfterMeal = (int)minValue;
                    inputMaxGlucoseAfterMeal = (int)maxValue;
                } else {
                    inputMmolMinGlucoseAfterMeal = (double)minValue;
                    inputMmolMaxGlucoseAfterMeal = (double)maxValue;
                }

                setAfterMealNormalView();
            }
        });

    }

    private void initSeekBar() {

        inputMinGlucoseBeforeMeal = PreferenceUtil.getGlucoseMinBeforeMeal(GlucoseSetGoalActivity.this);
        inputMaxGlucoseBeforeMeal = PreferenceUtil.getGlucoseMaxBeforeMeal(GlucoseSetGoalActivity.this);
        inputMinGlucoseAfterMeal = PreferenceUtil.getGlucoseMinAfterMeal(GlucoseSetGoalActivity.this);
        inputMaxGlucoseAfterMeal = PreferenceUtil.getGlucoseMaxAfterMeal(GlucoseSetGoalActivity.this);

        inputMmolMinGlucoseBeforeMeal =
                                      Double.parseDouble(ManagerUtil.mgToMmol(String.valueOf(inputMinGlucoseBeforeMeal)));
        inputMmolMaxGlucoseBeforeMeal =
                                      Double.parseDouble(ManagerUtil.mgToMmol(String.valueOf(inputMaxGlucoseBeforeMeal)));
        inputMmolMinGlucoseAfterMeal =
                                     Double.parseDouble(ManagerUtil.mgToMmol(String.valueOf(inputMinGlucoseAfterMeal)));
        inputMmolMaxGlucoseAfterMeal =
                                     Double.parseDouble(ManagerUtil.mgToMmol(String.valueOf(inputMaxGlucoseAfterMeal)));

        if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseSetGoalActivity.this))) {

            rsbBeforeMeal.setSelectedMinValue(inputMinGlucoseBeforeMeal);
            rsbBeforeMeal.setSelectedMaxValue(inputMaxGlucoseBeforeMeal);
            rsbAfterMeal.setSelectedMinValue(inputMinGlucoseAfterMeal);
            rsbAfterMeal.setSelectedMaxValue(inputMaxGlucoseAfterMeal);
        } else {

            rsbBeforeMeal.setRangeValues(3.3, 8.3);
            rsbAfterMeal.setRangeValues(5.6, 11.1);
            rsbBeforeMeal.setMinGap(1.1);
            rsbAfterMeal.setMinGap(1.1);
            rsbBeforeMeal.setSelectedMinValue(inputMmolMinGlucoseBeforeMeal);
            rsbBeforeMeal.setSelectedMaxValue(inputMmolMaxGlucoseBeforeMeal);
            rsbAfterMeal.setSelectedMinValue(inputMmolMinGlucoseAfterMeal);
            rsbAfterMeal.setSelectedMaxValue(inputMmolMaxGlucoseAfterMeal);
        }

        setBeforeMealNormalView();
        setAfterMealNormalView();
    }

    /**
     * 초기화 버튼 눌렀을때 SeekBar 초기 설정된 값으로 이동
     */
    private void setDefaultValue() {

        inputMinGlucoseBeforeMeal = DEFAULT_BEFORE_MEAL_MIN;
        inputMaxGlucoseBeforeMeal = DEFAULT_BEFORE_MEAL_MAX;
        inputMinGlucoseAfterMeal = DEFAULT_AFTER_MEAL_MIN;
        inputMaxGlucoseAfterMeal = DEFAULT_AFTER_MEAL_MAX;

        inputMmolMinGlucoseBeforeMeal =
                                      Double.parseDouble(ManagerUtil.mgToMmol(String.valueOf(inputMinGlucoseBeforeMeal)));
        inputMmolMaxGlucoseBeforeMeal =
                                      Double.parseDouble(ManagerUtil.mgToMmol(String.valueOf(inputMaxGlucoseBeforeMeal)));
        inputMmolMinGlucoseAfterMeal =
                                     Double.parseDouble(ManagerUtil.mgToMmol(String.valueOf(inputMinGlucoseAfterMeal)));
        inputMmolMaxGlucoseAfterMeal =
                                     Double.parseDouble(ManagerUtil.mgToMmol(String.valueOf(inputMaxGlucoseAfterMeal)));

        if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseSetGoalActivity.this))) {

            rsbBeforeMeal.setSelectedMinValue(inputMinGlucoseBeforeMeal);
            rsbBeforeMeal.setSelectedMaxValue(inputMaxGlucoseBeforeMeal);
            rsbAfterMeal.setSelectedMinValue(inputMinGlucoseAfterMeal);
            rsbAfterMeal.setSelectedMaxValue(inputMaxGlucoseAfterMeal);
        } else {

            rsbBeforeMeal.setRangeValues(3.3, 8.3);
            rsbAfterMeal.setRangeValues(5.6, 11.1);
            rsbBeforeMeal.setMinGap(1.1);
            rsbAfterMeal.setMinGap(1.1);

            rsbBeforeMeal.setSelectedMinValue(inputMmolMinGlucoseBeforeMeal);
            rsbBeforeMeal.setSelectedMaxValue(inputMmolMaxGlucoseBeforeMeal);
            rsbAfterMeal.setSelectedMinValue(inputMmolMinGlucoseAfterMeal);
            rsbAfterMeal.setSelectedMaxValue(inputMmolMaxGlucoseAfterMeal);
        }

        setBeforeMealNormalView();
        setAfterMealNormalView();
    }

    /**
     * 혈당 목표 전송 Sync
     */
    private class ModGoalSync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            if (!mConnectionProgressDialog.isShowing()) {
                mConnectionProgressDialog.show();
            }
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {
                //Server 전송
                data.put(ManagerConstants.RequestParamName.UUID,
                         PreferenceUtil.getEncEmail(GlucoseSetGoalActivity.this));
                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseSetGoalActivity.this))) {

                    data.put(ManagerConstants.RequestParamName.MEAL_B_MIN, String.valueOf(inputMinGlucoseBeforeMeal));
                    data.put(ManagerConstants.RequestParamName.MEAL_B_MAX, String.valueOf(inputMaxGlucoseBeforeMeal));
                    data.put(ManagerConstants.RequestParamName.MEAL_A_MIN, String.valueOf(inputMinGlucoseAfterMeal));
                    data.put(ManagerConstants.RequestParamName.MEAL_A_MAX, String.valueOf(inputMaxGlucoseAfterMeal));
                } else {
                    data.put(ManagerConstants.RequestParamName.MEAL_B_MIN,
                             ManagerUtil.mmolToMg(String.valueOf(inputMmolMinGlucoseBeforeMeal)));
                    data.put(ManagerConstants.RequestParamName.MEAL_B_MAX,
                             ManagerUtil.mmolToMg(String.valueOf(inputMmolMaxGlucoseBeforeMeal)));
                    data.put(ManagerConstants.RequestParamName.MEAL_A_MIN,
                             ManagerUtil.mmolToMg(String.valueOf(inputMmolMinGlucoseAfterMeal)));
                    data.put(ManagerConstants.RequestParamName.MEAL_A_MAX,
                             ManagerUtil.mmolToMg(String.valueOf(inputMmolMaxGlucoseAfterMeal)));
                }

                resultJSON = glucoseService.modifyGlucoseGoal(data);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {

            if (mConnectionProgressDialog != null && mConnectionProgressDialog.isShowing()) {
                mConnectionProgressDialog.dismiss();
            }

            try {

                if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                     .toString())) {
                        Intent intent = new Intent();

                        if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(GlucoseSetGoalActivity.this))) {

                            PreferenceUtil.setGlucoseMinBeforeMeal(GlucoseSetGoalActivity.this,
                                                                   inputMinGlucoseBeforeMeal);
                            PreferenceUtil.setGlucoseMaxBeforeMeal(GlucoseSetGoalActivity.this,
                                                                   inputMaxGlucoseBeforeMeal);
                            PreferenceUtil.setGlucoseMinAfterMeal(GlucoseSetGoalActivity.this,
                                                                  inputMinGlucoseAfterMeal);
                            PreferenceUtil.setGlucoseMaxAfterMeal(GlucoseSetGoalActivity.this,
                                                                  inputMaxGlucoseAfterMeal);

                        } else {
                            PreferenceUtil.setGlucoseMinBeforeMeal(GlucoseSetGoalActivity.this,
                                                                   Integer.parseInt(ManagerUtil.mmolToMg(String.valueOf(inputMmolMinGlucoseBeforeMeal))));
                            PreferenceUtil.setGlucoseMaxBeforeMeal(GlucoseSetGoalActivity.this,
                                                                   Integer.parseInt(ManagerUtil.mmolToMg(String.valueOf(inputMmolMaxGlucoseBeforeMeal))));
                            PreferenceUtil.setGlucoseMinAfterMeal(GlucoseSetGoalActivity.this,
                                                                  Integer.parseInt(ManagerUtil.mmolToMg(String.valueOf(inputMmolMinGlucoseAfterMeal))));
                            PreferenceUtil.setGlucoseMaxAfterMeal(GlucoseSetGoalActivity.this,
                                                                  Integer.parseInt(ManagerUtil.mmolToMg(String.valueOf(inputMmolMaxGlucoseAfterMeal))));

                        }

                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                    }
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();

                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
}
