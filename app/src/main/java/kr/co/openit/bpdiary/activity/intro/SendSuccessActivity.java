package kr.co.openit.bpdiary.activity.intro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;


/**
 * Created by Hwang on 2016-12-27.
 */

public class SendSuccessActivity extends BaseActivity {

    private Button btnOk;

    private TextView tvEmail;

    private TextView tvContentOne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_send_success);

        AnalyticsUtil.sendScene(SendSuccessActivity.this, "3_로그인 비밀번호 발송");

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.rl_send_success);

        Intent intent = getIntent();

        btnOk = (Button) findViewById(R.id.btn_ok);
        tvEmail = (TextView) findViewById(R.id.tv_email);
        tvContentOne = (TextView)findViewById(R.id.tv_content_one);

        tvEmail.setText(intent.getStringExtra(ManagerConstants.SharedPreferencesName.SHARED_EMAIL));
        tvContentOne.setText(R.string.send_content_one);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    finish();
                }
            }
        });

        setLayout(layout);
        LanguageSpecificTerms(SendSuccessActivity.this);
    }
}
