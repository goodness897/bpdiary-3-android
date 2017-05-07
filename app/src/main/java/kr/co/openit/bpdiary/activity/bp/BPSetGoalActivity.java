package kr.co.openit.bpdiary.activity.bp;

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
import kr.co.openit.bpdiary.services.BloodPressureService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

import static kr.co.openit.bpdiary.utils.PhoneUtil.convertDpToPixel;

public class BPSetGoalActivity extends BaseActivity {

    private LinearLayout llEmptyView;

    /**
     * 사용자 UserName
     */
    private TextView tvUserName;

    /**
     * 수축기 조절 SeekBar
     */

    private RangeSeekBar rsbSystole;

    /**
     * 이완기 조절 SeekBar
     */
    private RangeSeekBar rsbDiastole;

    /**
     * 초기화 버튼
     */
    private Button btnInit;

    /**
     * 적용 버튼
     */

    private Button btnApply;

    /**
     * 정상 범위 Layout
     */

    private RelativeLayout rlNormal;

    /**
     * 저혈압 범위 Layout
     */

    private RelativeLayout rlUnder;

    /**
     * 이완기 커서 조절을 위한 Layout
     */
    private LinearLayout llUpMin;

    /**
     * 이완기 커서 조절을 위한 Layout
     */
    private LinearLayout llUpMax;

    /**
     * 수축기 커서 조절을 위한 Layout
     */
    private LinearLayout llRightMin;

    /**
     * 수축기 커서 조절을 위한 Layout
     */

    private LinearLayout llRightMax;

    /**
     * 이완기 최대값 커서 Layout height
     */

    private float layoutUpTriangleMaxHeight = 5;

    /**
     * 이완기 최대값 커서 Layout width
     */

    private float layoutUpTriangleMaxWidth;

    /**
     * 이완기 최소값 커서 Layout height
     */

    private float layoutUpTriangleMinHeight = 5;

    /**
     * 이완기 최소값 커서 Layout width
     */
    private float layoutUpTriangleMinWidth;

    /**
     * 수축기 최대값 커서 Layout height
     */

    private float layoutRightTriangleMaxHeight;

    /**
     * 수축기 최대값 커서 Layout width
     */

    private float layoutRightTriangleMaxWidth = 5;

    /**
     * 수축기 최소값 커서 Layout height
     */

    private float layoutRightTriangleMinHeight;

    /**
     * 수축기 최소값 커서 Layout width
     */
    private float layoutRightTriangleMinWidth = 5;

    /**
     * 정상범위 Layout 안의 View
     */

    private View viewNormal;

    /**
     * 정상 Layout height
     */
    private float layoutHeight;

    /**
     * 정상 Layout width
     */

    private float layoutWidth;

    /**
     * 정상 Layout 안의 View height
     */
    private float viewHeight;

    /**
     * 정상 Layout 안의 View width
     */

    private float viewWidth;

    /**
     * 이완기 입력 최소값
     */

    private int inputMinDiastole = 60;

    /**
     * 이완기 입력 최대값
     */

    private int inputMaxDiastole = 79;

    /**
     * 수축기 입력 최소값
     */

    private int inputMinSystole = 90;

    /**
     * 수축기 입력 최대값
     */

    private int inputMaxSystole = 119;

    /**
     * 정상 Layout 초기 Width
     */

    private static final int NORMAL_MAX_WIDTH = 72;

    /**
     * 정상 Layout 초기 height
     */

    private static final int NORMAL_MAX_HEIGHT = 90;

    /**
     * 정상 View 초기 Width
     */
    private static final int NORMAL_MIN_WIDTH = 48;

    /**
     * 정상 View 초기 height
     */
    private static final int NORMAL_MIN_HEIGHT = 60;

    /**
     * 정상 이완기 최대값
     */
    private static final int NORMAL_MAX_DIASTOLE = 80;

    /**
     * 정상 이완기 최소값
     */

    private static final int NORMAL_MIN_DIASTOLE = 60;

    /**
     * 정상 수축기 최대값
     */

    private static final int NORMAL_MAX_SYSTOLE = 120;

    /**
     * 정상 수축기 최소값
     */
    private static final int NORMAL_MIN_SYSTOLE = 90;

    private BloodPressureService bpService;

    private ModGoalSync modGoalSync;

    /**
     * 정상 글자 나타내는 TextView
     */
    private TextView tvNormal;

    /**
     * Default 값(수축기, 이완기)
     */

    private static final int DEFAULT_INPUT_MIN_DIASTOLE = 90;

    private static final int DEFAULT_INPUT_MAX_DIASTOLE = 119;

    private static final int DEFAULT_INPUT_MIN_SYSTOLE = 60;

    private static final int DEFAULT_INPUT_MAX_SYSTOLE = 79;

    /**
     * 작업 프로그래스
     */
    private CustomProgressDialog mConnectionProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp_set_goal);

        context = BPSetGoalActivity.this;

        mConnectionProgressDialog = new CustomProgressDialog(context);
        mConnectionProgressDialog.setCancelable(false);
        mConnectionProgressDialog.setCanceledOnTouchOutside(false);

        initToolbar(getString(R.string.bp_goal_title));

        AnalyticsUtil.sendScene(BPSetGoalActivity.this, "3_혈압 목표설정");

        setLayout();
        String userName = PreferenceUtil.getDecFirstName(context) + " "+ PreferenceUtil.getDecLastName(context) + " ";
        setGoalGuildeText(userName);

        bpService = new BloodPressureService(BPSetGoalActivity.this);
    }

    /**
     * UserName에 맞게 "목표를 자유롭게 조절해보세요" 텍스트 변경
     *
     * @param userName
     */
    private void setGoalGuildeText(String userName) {
        StringBuilder sb = new StringBuilder();
        // 저장된 유저네임 들어가는 부분
        sb.append(userName).append(tvUserName.getText().toString());
        final SpannableStringBuilder sp = new SpannableStringBuilder(sb.toString());
        sp.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_1869c7)),
                   0,
                   userName.length(),
                   Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvUserName.setText(sp);
    }

    /**
     * 정상 상태 뷰 그려주는 부분
     */

    private void setNormalView() {

        //        rlUnder.bringToFront();

        ViewGroup.MarginLayoutParams tvNormalParams = (ViewGroup.MarginLayoutParams)tvNormal.getLayoutParams();
        ViewGroup.LayoutParams layoutParams = rlNormal.getLayoutParams();
        ViewGroup.LayoutParams viewParams = viewNormal.getLayoutParams();
        ViewGroup.LayoutParams llMinLayoutUpParams = llUpMin.getLayoutParams();
        ViewGroup.LayoutParams llMaxLayoutUpParams = llUpMax.getLayoutParams();

        ViewGroup.LayoutParams llMinLayoutRightParams = llRightMin.getLayoutParams();
        ViewGroup.LayoutParams llMaxLayoutRightParams = llRightMax.getLayoutParams();

        ViewGroup.LayoutParams rlUnderLayoutParams = rlUnder.getLayoutParams();

        //이완기 최대값

        if (inputMaxDiastole > NORMAL_MAX_DIASTOLE) {
            layoutWidth = (float)(NORMAL_MAX_WIDTH + ((inputMaxDiastole - NORMAL_MAX_DIASTOLE) * 2.4));
            layoutUpTriangleMaxWidth = (float)(80 + ((inputMaxDiastole - NORMAL_MAX_DIASTOLE) * 2.4));
        } else {
            layoutWidth = (float)(NORMAL_MAX_WIDTH - ((NORMAL_MAX_DIASTOLE - inputMaxDiastole) * 1.2));
            layoutUpTriangleMaxWidth = (float)(80 - ((inputMaxDiastole - NORMAL_MAX_DIASTOLE) * 1.2));

        }

        //이완기 최소값

        if (inputMinDiastole > NORMAL_MIN_DIASTOLE) {
            viewWidth = (float)(NORMAL_MIN_WIDTH + ((inputMinDiastole - NORMAL_MIN_DIASTOLE) * 1.2));
            layoutUpTriangleMinWidth = (float)(60 + ((inputMinDiastole - NORMAL_MIN_DIASTOLE) * 1.2));

        } else if (inputMinDiastole > NORMAL_MAX_DIASTOLE) {
            viewWidth = (float)(NORMAL_MIN_WIDTH + ((inputMinDiastole - NORMAL_MIN_DIASTOLE) * 2.4));
            layoutUpTriangleMinWidth = (float)(60 + ((inputMinDiastole - NORMAL_MIN_DIASTOLE) * 2.4));

        } else {
            viewWidth = (float)(NORMAL_MIN_WIDTH - ((NORMAL_MIN_DIASTOLE - inputMinDiastole) * 1.2));
            layoutUpTriangleMinWidth = (float)(60 - ((NORMAL_MIN_DIASTOLE - inputMinDiastole) * 1.2));

        }

        //수축기 최대값

        if (inputMaxSystole > NORMAL_MAX_SYSTOLE) {
            layoutHeight = (float)(NORMAL_MAX_HEIGHT + ((inputMaxSystole - NORMAL_MAX_SYSTOLE) * 1.5));
            layoutRightTriangleMaxHeight = (float)(95 + ((inputMaxSystole - NORMAL_MAX_SYSTOLE) * 1.5));

        } else {
            layoutHeight = (float)(NORMAL_MAX_HEIGHT - ((NORMAL_MAX_SYSTOLE - inputMaxSystole) * 1.0));
            layoutRightTriangleMaxHeight = (float)(95 - ((NORMAL_MAX_SYSTOLE - inputMaxSystole) * 1.0));

        }

        //수축기 최소값

        if (inputMinSystole > NORMAL_MIN_SYSTOLE) {
            viewHeight = (float)(NORMAL_MIN_HEIGHT + ((inputMinSystole - NORMAL_MIN_SYSTOLE) * 1.0));
            layoutRightTriangleMinHeight = (float)(65 + ((inputMinSystole - NORMAL_MIN_SYSTOLE) * 1.0));

        } else if (inputMinSystole > NORMAL_MAX_SYSTOLE) {
            viewHeight = (float)(NORMAL_MIN_HEIGHT + ((inputMinSystole - NORMAL_MIN_SYSTOLE) * 1.5));
            layoutRightTriangleMinHeight = (float)(65 + ((inputMinSystole - NORMAL_MIN_SYSTOLE) * 1.5));

        } else {
            viewHeight = (float)(NORMAL_MIN_HEIGHT - ((NORMAL_MIN_SYSTOLE - inputMinSystole) * 1.0));
            layoutRightTriangleMinHeight = (float)(65 - ((NORMAL_MIN_SYSTOLE - inputMinSystole) * 1.0));
        }

        //정상 RelativeLayout
        layoutParams.width = convertDpToPixel(layoutWidth);
        layoutParams.height = convertDpToPixel(layoutHeight);

        //정상 RelativeLayout 안의 View (저혈압쪽으로 작아질때의 값을 표현하기 위해)

        //        viewParams.width = convertDpToPixel(viewWidth);
        //        viewParams.height = convertDpToPixel(viewHeight);

        rlUnderLayoutParams.width = convertDpToPixel(viewWidth);
        rlUnderLayoutParams.height = convertDpToPixel(viewHeight);

        // 정상 Text 위치 조정
        tvNormalParams.setMargins(0, (convertDpToPixel(((layoutHeight - viewHeight) / 2)) - (59 / 2)), 0, 0);
        // 이완기 왼쪽 커서
        llMinLayoutUpParams.width = convertDpToPixel(layoutUpTriangleMinWidth);
        llMinLayoutUpParams.height = convertDpToPixel(layoutUpTriangleMinHeight);

        // 이완기 오른쪽 커서
        llMaxLayoutUpParams.width = convertDpToPixel(layoutUpTriangleMaxWidth);
        llMaxLayoutUpParams.height = convertDpToPixel(layoutUpTriangleMaxHeight);

        // 수축기 아래 커서
        llMinLayoutRightParams.width = convertDpToPixel(layoutRightTriangleMinWidth);
        llMinLayoutRightParams.height = convertDpToPixel(layoutRightTriangleMinHeight);

        // 수축기 위 커서
        llMaxLayoutRightParams.width = convertDpToPixel(layoutRightTriangleMaxWidth);
        llMaxLayoutRightParams.height = convertDpToPixel(layoutRightTriangleMaxHeight);

        rlNormal.setLayoutParams(layoutParams);
        //        viewNormal.setLayoutParams(viewParams);
        llUpMin.setLayoutParams(llMinLayoutUpParams);
        rlUnder.setLayoutParams(rlUnderLayoutParams);

        //
        //        if (inputMinDiastole >= 60 && inputMaxDiastole <= 80 && inputMinSystole >= 90 && inputMaxSystole <= 120) { //정상 범위
        //            fillCustomGradient(rlNormal, 0);
        //        } else if (inputMinDiastole >= 60 && inputMaxDiastole <= 90
        //                && inputMinSystole >= 90
        //                && inputMaxSystole <= 140) { //고혈압 전단계
        //
        //            fillCustomGradient(rlNormal, 1);
        //
        //        } else if (inputMinDiastole >= 60 && inputMaxDiastole <= 100
        //                && inputMinSystole >= 90
        //                && inputMaxSystole <= 160) { //1기 고혈압
        //            fillCustomGradient(rlNormal, 2);
        //
        //        } else if (inputMinDiastole >= 60 && inputMaxDiastole <= 110
        //                && inputMinSystole >= 90
        //                && inputMaxSystole <= 180) { //2기 고혈압
        //            fillCustomGradient(rlNormal, 3);
        //        } else if (inputMinDiastole >= 60 && inputMaxDiastole <= 120
        //                && inputMinSystole >= 90
        //                && inputMaxSystole <= 200) {
        //            fillCustomGradient(rlNormal, 4);
        //        } else if (inputMinDiastole < 60 && inputMaxDiastole > 60 && inputMinSystole < 90 && inputMaxSystole > 90) {
        //            fillCustomGradient(rlNormal, 4);
        //        } else {
        //            fillCustomGradient(rlNormal, 2);
        //        }

        //        if (inputMinSystole < NORMAL_MIN_SYSTOLE || inputMinDiastole < NORMAL_MIN_DIASTOLE) {
        //            rlNormal.bringToFront();
        //            rlUnder.bringToFront();
        //        }
    }

    /**
     * Layout 초기화
     */
    private void setLayout() {

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        tvNormal = (TextView)findViewById(R.id.tv_normal);
        tvUserName = (TextView)findViewById(R.id.tv_user_name);
        rsbSystole = (RangeSeekBar)findViewById(R.id.rsb_systole);
        rsbDiastole = (RangeSeekBar)findViewById(R.id.rsb_diastole);
        btnInit = (Button)findViewById(R.id.btn_init);
        btnApply = (Button)findViewById(R.id.btn_apply);
        rlNormal = (RelativeLayout)findViewById(R.id.rl_normal);
        rlUnder = (RelativeLayout)findViewById(R.id.rl_under);
        viewNormal = (View)findViewById(R.id.view_normal);

        llUpMin = (LinearLayout)findViewById(R.id.ll_width_min_triangle);
        llUpMax = (LinearLayout)findViewById(R.id.ll_width_max_triangle);

        llRightMin = (LinearLayout)findViewById(R.id.ll_height_min_triangle);
        llRightMax = (LinearLayout)findViewById(R.id.ll_height_max_triangle);

        initRangeSeekBar();

        rsbSystole.setNotifyWhileDragging(true);
        rsbSystole.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Number minValue, Number maxValue) {

                inputMinSystole = (int)minValue;
                inputMaxSystole = (int)maxValue;
                setNormalView();
            }
        });

        rsbDiastole.setNotifyWhileDragging(true);
        rsbDiastole.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Number minValue, Number maxValue) {
                inputMinDiastole = (int)minValue;
                inputMaxDiastole = (int)maxValue;
                setNormalView();
            }
        });

        // 초기화 버튼 클릭
        btnInit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    defaultRangeSeekBar();
                }
            }
        });

        // 적용 버튼 클릭
        btnApply.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (BPDiaryApplication.isNetworkState(BPSetGoalActivity.this)) {
                        modGoalSync = new ModGoalSync();
                        modGoalSync.execute();
                    } else {
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(BPSetGoalActivity.this,
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
    }

    /**
     * SeekBar 초기화 세팅
     */

    private void initRangeSeekBar() {

        inputMinDiastole = PreferenceUtil.getBPMinDiastole(BPSetGoalActivity.this);
        inputMaxDiastole = PreferenceUtil.getBPMaxDiastole(BPSetGoalActivity.this);
        inputMinSystole = PreferenceUtil.getBPMinSystole(BPSetGoalActivity.this);
        inputMaxSystole = PreferenceUtil.getBPMaxSystole(BPSetGoalActivity.this);

        rsbSystole.setSelectedMinValue(inputMinSystole);
        rsbSystole.setSelectedMaxValue(inputMaxSystole);
        rsbDiastole.setSelectedMinValue(inputMinDiastole);
        rsbDiastole.setSelectedMaxValue(inputMaxDiastole);
        //        fillCustomGradient(rlNormal, 0);
        setNormalView();
    }

    private void defaultRangeSeekBar() {

        inputMinDiastole = DEFAULT_INPUT_MIN_SYSTOLE;
        inputMaxDiastole = DEFAULT_INPUT_MAX_SYSTOLE;
        inputMinSystole = DEFAULT_INPUT_MIN_DIASTOLE;
        inputMaxSystole = DEFAULT_INPUT_MAX_DIASTOLE;

        rsbSystole.setSelectedMinValue(inputMinSystole);
        rsbSystole.setSelectedMaxValue(inputMaxSystole);
        rsbDiastole.setSelectedMinValue(inputMinDiastole);
        rsbDiastole.setSelectedMaxValue(inputMaxDiastole);

        setNormalView();

    }

    /**
     * 혈압 목표 전송 Sync
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
                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(BPSetGoalActivity.this));
                data.put(ManagerConstants.RequestParamName.BP_SYS_MIN, String.valueOf(inputMinSystole));
                data.put(ManagerConstants.RequestParamName.BP_SYS_MAX, String.valueOf(inputMaxSystole));
                data.put(ManagerConstants.RequestParamName.BP_DIA_MIN, String.valueOf(inputMinDiastole));
                data.put(ManagerConstants.RequestParamName.BP_DIA_MAX, String.valueOf(inputMaxDiastole));

                resultJSON = bpService.modifyBPGoal(data);

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
                        PreferenceUtil.setBPMinDiastole(context, inputMinDiastole);
                        PreferenceUtil.setBPMaxDiastole(context, inputMaxDiastole);
                        PreferenceUtil.setBPMinSystole(context, inputMinSystole);
                        PreferenceUtil.setBPMaxSystole(context, inputMaxSystole);

                        intent.putExtra("diastole", inputMaxDiastole);
                        intent.putExtra("systole", inputMaxSystole);
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
