package kr.co.openit.bpdiary.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class ProfileAccountActivity extends BaseActivity {

    private LinearLayout llEmptyView;

    /**
     * 로그인 타입에 따라 달라지는 이미지뷰(페북, 구글, 이메일)
     */
    private ImageView ivLoginType;

    /**
     * 유저 이메일
     */
    private TextView tvUserEmail;

    /**
     * 탈퇴하기 버튼
     */
    private Button btnLeave;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        initToolbar(getString(R.string.setting_activity_account));

        AnalyticsUtil.sendScene(ProfileAccountActivity.this, "3_M 프로필 계정");

        setLayout();

        btnLeave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    Intent intent = new Intent(ProfileAccountActivity.this, ProfileLeaveActivity.class);
                    startActivity(intent);
                }
            }
        });

        //로그인 타입에 따른 이미지
        loginTypeCheck();

    }

    private void loginTypeCheck() {
        if (PreferenceUtil.getLoginType(ProfileAccountActivity.this).equals(ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK)) {
            ivLoginType.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_ex_menu_sns_facebook));
        } else if (PreferenceUtil.getLoginType(ProfileAccountActivity.this)
                .equals(ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE)) {
            ivLoginType.setBackgroundDrawable(getResources().getDrawable(R.drawable.google));
        } else {
            ivLoginType.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_ex_menu_sns_email));
        }
    }

    private void setLayout() {

        llEmptyView = (LinearLayout) findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        ivLoginType = (ImageView) findViewById(R.id.iv_login_type);
        tvUserEmail = (TextView) findViewById(R.id.tv_account_email);
        btnLeave = (Button) findViewById(R.id.btn_leave);

        tvUserEmail.setText(PreferenceUtil.getDecEmail(ProfileAccountActivity.this));

    }
}
