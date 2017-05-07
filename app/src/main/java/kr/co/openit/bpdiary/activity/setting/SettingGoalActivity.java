package kr.co.openit.bpdiary.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.bp.BPSetGoalActivity;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.activity.glucose.GlucoseSetGoalActivity;
import kr.co.openit.bpdiary.activity.weight.WeightSetGoalActivity;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class SettingGoalActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout llEmptyView;

    /**
     * 혈압 목표 설정
     */
    private RelativeLayout rlBP;

    /**
     * 혈당 목표 설정
     */
    private RelativeLayout rlGlucose;

    /**
     * 몸무게 목표 설정
     */
    private RelativeLayout rlWs;

    private LinearLayout llAds;

    private ImageView ivLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_goal);
        initToolbar(getString(R.string.setting_activity_goal));

        AnalyticsUtil.sendScene(SettingGoalActivity.this, "3_M 알림 측정");

        context = SettingGoalActivity.this;

        /**
         * 광고
         */
        llAds = (LinearLayout) findViewById(R.id.ll_ads);
        AdView adView = (AdView) findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                                     .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                                                     .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(SettingGoalActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        setLayout();

        if (!PreferenceUtil.getUsingBloodGlucose(context)) {
            rlGlucose.setVisibility(View.GONE);
            ivLine.setVisibility(View.GONE);
        } else {
            rlGlucose.setVisibility(View.VISIBLE);
            ivLine.setVisibility(View.VISIBLE);

        }
    }

    /**
     * 레이아웃 세팅
     */
    private void setLayout() {
        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        rlBP = (RelativeLayout)findViewById(R.id.rl_set_goal_bp);
        rlGlucose = (RelativeLayout)findViewById(R.id.rl_set_goal_glucose);
        rlWs = (RelativeLayout)findViewById(R.id.rl_set_goal_ws);
        ivLine = (ImageView)findViewById(R.id.iv_line3);

        rlBP.setOnClickListener(this);
        rlGlucose.setOnClickListener(this);
        rlWs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!ManagerUtil.isClicking()) {

            Intent intent;
            switch (v.getId()) {
                case R.id.rl_set_goal_bp:
                    intent = new Intent(SettingGoalActivity.this, BPSetGoalActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rl_set_goal_glucose:
                    intent = new Intent(SettingGoalActivity.this, GlucoseSetGoalActivity.class);
                    startActivity(intent);
                    break;
                case R.id.rl_set_goal_ws:
                    intent = new Intent(SettingGoalActivity.this, WeightSetGoalActivity.class);
                    startActivity(intent);
                    break;

            }
        }
    }
}
