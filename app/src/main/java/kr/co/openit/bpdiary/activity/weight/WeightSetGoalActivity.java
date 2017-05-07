package kr.co.openit.bpdiary.activity.weight;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import kr.co.openit.bpdiary.services.WeighingScaleService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class WeightSetGoalActivity extends BaseActivity {

    private LinearLayout llEmptyView;

    /**
     * 사용자 UserName
     */
    private TextView tvUserName;

    /**
     * Weight조절 SeekBar
     */
    private RangeSeekBar rsbWeight;

    /**
     * 초기화 버튼
     */
    private Button btnInit;

    /**
     * 적용 버튼
     */
    private Button btnApply;

    /**
     * 입력 Weight 값
     */
    private int inputWeight;

    private Context context;

    /**
     * 작업 프로그래스
     */
    private CustomProgressDialog mConnectionProgressDialog = null;

    /**
     * weighing scale service
     */
    private WeighingScaleService wsService;

    private TextView tvWeightUnit;

    private ModWeightGoalSync modWeightGoalSync;

    private static final String DEFAULT_WEIGHT = "60";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_set_goal);

        AnalyticsUtil.sendScene(WeightSetGoalActivity.this, "3_체중 목표설정");

        context = WeightSetGoalActivity.this;
        wsService = new WeighingScaleService(context);

        mConnectionProgressDialog = new CustomProgressDialog(context);
        mConnectionProgressDialog.setCancelable(false);
        mConnectionProgressDialog.setCanceledOnTouchOutside(false);

        initToolbar(getString(R.string.weight_set_goal));

        setLayout();

        String userName = PreferenceUtil.getDecFirstName(context) + " "+ PreferenceUtil.getDecLastName(context) + " ";
        setGoalGuildeText(userName);
    }

    /**
     * 레이아웃 세팅
     */

    private void setLayout() {

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        rsbWeight = (RangeSeekBar)findViewById(R.id.rsb_weight);
        tvUserName = (TextView)findViewById(R.id.tv_user_name);
        btnInit = (Button)findViewById(R.id.btn_init);
        btnApply = (Button)findViewById(R.id.btn_apply);
        tvWeightUnit = (TextView)findViewById(R.id.tv_weight_unit);

        rsbWeight.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Number minValue, Number maxValue) {
                Log.d(TAG, "minValue : " + minValue);
                Log.d(TAG, "maxValue : " + maxValue);
                inputWeight = (int)maxValue;
            }
        });

        //초기화버튼 클릭
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

                    if (BPDiaryApplication.isNetworkState(WeightSetGoalActivity.this)) {
                        modWeightGoalSync = new ModWeightGoalSync();
                        modWeightGoalSync.execute();
                    } else {
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(WeightSetGoalActivity.this,
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

        initRangeSeekBar();
    }

    /**
     * SeekBar 초기값 세팅
     */

    private void initRangeSeekBar() {

        if (getResources().getString(R.string.weight_lbs).equals(PreferenceUtil.getWeightUnit(context))) {
            tvWeightUnit.setText("(" + getResources().getString(R.string.weight_lbs) + ")");
            rsbWeight.setRangeValues(40, 440);
            String kgToLbs = ManagerUtil.kgToLbs(String.valueOf(PreferenceUtil.getWeight(context)));
            inputWeight = (int)Math.round(Double.parseDouble(kgToLbs));
            rsbWeight.setSelectedMaxValue(inputWeight);
        } else {
            tvWeightUnit.setText("(" + getResources().getString(R.string.weight_kg) + ")");
            String kg = PreferenceUtil.getWeight(context);
            inputWeight = (int)Math.round(Double.parseDouble(kg));
            rsbWeight.setSelectedMaxValue(inputWeight);
        }
    }

    /**
     * 초기화 버튼 클릭 시 이벤트
     */

    private void setDefaultValue() {

        inputWeight = Integer.parseInt(DEFAULT_WEIGHT);

        // lbs 단위 일때
        if (getResources().getString(R.string.weight_lbs).equals(PreferenceUtil.getWeightUnit(context))) {
            rsbWeight.setRangeValues(40, 440);
            String kgToLbs = ManagerUtil.kgToLbs(String.valueOf(inputWeight));
            rsbWeight.setSelectedMaxValue(Math.round(Double.parseDouble(kgToLbs)));
            inputWeight = (int)Math.round(Double.parseDouble(kgToLbs));
        } else { //kg 단위 일때
            String kg = DEFAULT_WEIGHT;
            rsbWeight.setSelectedMaxValue(Math.round(Double.parseDouble(kg)));
            inputWeight = Integer.parseInt(DEFAULT_WEIGHT);
        }
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
     * 체중 목표 변경 전송 Sync
     */
    private class ModWeightGoalSync extends AsyncTask<Void, Void, JSONObject> {

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
                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(context));
                if (getResources().getString(R.string.weight_lbs).equals(PreferenceUtil.getWeightUnit(context))) {
                    data.put(ManagerConstants.RequestParamName.WS_WEIGHT,
                             ManagerUtil.lbsToKg(String.valueOf(inputWeight)));

                } else {
                    data.put(ManagerConstants.RequestParamName.WS_WEIGHT, String.valueOf(inputWeight));
                }

                resultJSON = wsService.modifyWSGoal(data);

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
                        if (getResources().getString(R.string.weight_lbs)
                                          .equals(PreferenceUtil.getWeightUnit(context))) {
                            PreferenceUtil.setWeightGoal(context, ManagerUtil.lbsToKg(String.valueOf(inputWeight)));

                        } else {
                            PreferenceUtil.setWeightGoal(context, String.valueOf(inputWeight));
                        }
                        finish();
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

                        }
                    }
                } else {
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
                finish();
            }
        }
    }
}
