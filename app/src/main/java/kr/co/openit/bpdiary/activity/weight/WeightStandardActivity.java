package kr.co.openit.bpdiary.activity.weight;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;

public class WeightStandardActivity extends BaseActivity implements View.OnClickListener {

    private Button btnSetGoal;

    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weight_standard);

        AnalyticsUtil.sendScene(WeightStandardActivity.this, "3_체중 기준팝업");

        setLayout();

    }

    private void setLayout() {

        btnSetGoal = (Button)findViewById(R.id.btn_setting_goal);
        btnClose = (Button)findViewById(R.id.btn_close);
        btnSetGoal.setOnClickListener(this);
        btnClose.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (!ManagerUtil.isClicking()) {
            switch (v.getId()) {
                case R.id.btn_setting_goal:
                    Intent intent = new Intent(WeightStandardActivity.this, WeightSetGoalActivity.class);
                    startActivity(intent);
                    break;

                case R.id.btn_close:
                    onBackPressed();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
