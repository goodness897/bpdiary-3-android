package kr.co.openit.bpdiary.activity.report;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.activity.glucose.GlucoseSetGoalActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * Created by srpark on 2017-02-06.
 */

public class ReportSendSuccessActivity extends Activity {

    private Button btnOk;

    private TextView tvEmail;

    private TextView tvContentOne;

    private TextView tvContentTwo;

    private LinearLayout vTerms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_success);

        RelativeLayout layout = (RelativeLayout)findViewById(R.id.rl_send_success);

        Intent intent = getIntent();

        btnOk = (Button)findViewById(R.id.btn_ok);
        tvEmail = (TextView)findViewById(R.id.tv_email);
        vTerms = (LinearLayout)layout.findViewById(R.id.v_terms);
        tvContentOne = (TextView)findViewById(R.id.tv_content_one);
        tvContentTwo = (TextView)findViewById(R.id.tv_content_two);

        tvEmail.setText(intent.getStringExtra(ManagerConstants.SharedPreferencesName.SHARED_EMAIL));
        tvContentOne.setText(R.string.report_share_send_succes);

        btnOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    finish();
                }
            }
        });
        vTerms.setVisibility(View.GONE);
        tvContentTwo.setVisibility(View.INVISIBLE);
    }
}
