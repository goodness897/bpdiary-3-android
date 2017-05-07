package kr.co.openit.bpdiary.activity.bp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

import static kr.co.openit.bpdiary.utils.PhoneUtil.convertDpToPixel;

public class BPStandardActivity extends BaseActivity {

    /**
     * 혈압목표설정 리턴 데이터를 위한 리퀘스트 코드
     */
    private static final int REQUEST_BP_SET_GOAL = 1000;

    /**
     * 정상 뷰
     */
    private View viewNormal;

    /**
     * 저혈압 뷰
     */
    private View viewUnder;

    /**
     * 목표 수축기 TextView
     */
    private TextView tvGoalSys;

    /**
     * 목표 이완기 TextView
     */

    private TextView tvGoalDia;

    /**
     * 유동적으로 변할 정상뷰 height
     */

    private float height;

    /**
     * 유동적으로 변할 정상뷰 width
     */
    private float width;

    /**
     * 유동적으로 변할 저혈압 height
     */

    private float underHeight;

    /**
     * 유동적으로 변할 저혈압 width
     */
    private float underWidth;

    /**
     * 목표설정으로 입력받은 이완기 최소값
     */

    private int inputMinDiastole;

    /**
     * 목표설정으로 입력받은 이완기 최대값
     */

    private int inputMaxDiastole;

    /**
     * 목표설정으로 입력받은 수축기 최소값
     */

    private int inputMinSystole;

    /**
     * 목표설정으로 입력받은 수축기 최대값
     */

    private int inputMaxSystole;

    /**
     * 정상상태뷰 최초 width 값
     */

    private static final int NORMAL_WIDTH = 72;

    /**
     * 정상상태뷰 최초 height 값
     */

    private static final int NORMAL_HEIGHT = 90;

    /**
     * 저혈당뷰 최초 width 값
     */

    private static final int UNDER_WIDTH = 48;

    /**
     * 저혈당뷰 최초 height 값
     */

    private static final int UNDER_HEIGHT = 60;

    /**
     * 정상상태 이완기 최대값
     */

    private static final int NORMAL_DIASTOLE = 80;

    /**
     * 정상상태 수축기 최대값
     */

    private static final int NORMAL_SYSTOLE = 120;

    /**
     * 목표설정 페이지 이동 버튼
     */
    private Button btnSetGoal;

    /**
     * 닫기 버튼
     */
    private Button btnClose;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bp_standard);

        AnalyticsUtil.sendScene(BPStandardActivity.this, "3_혈압 기준팝업");

        setLayout();

    }

    @Override
    protected void onResume() {
        super.onResume();

        inputMinDiastole = PreferenceUtil.getBPMinDiastole(BPStandardActivity.this);
        inputMaxDiastole = PreferenceUtil.getBPMaxDiastole(BPStandardActivity.this);
        inputMinSystole = PreferenceUtil.getBPMinSystole(BPStandardActivity.this);
        inputMaxSystole = PreferenceUtil.getBPMaxSystole(BPStandardActivity.this);
        setNormalView();
    }

    /**
     * 정상 범위 뷰 설정
     */
    private void setNormalView() {

        tvGoalSys.setText(" "+PreferenceUtil.getBPMinSystole(BPStandardActivity.this) + "~"
                + PreferenceUtil.getBPMaxSystole(BPStandardActivity.this));
        tvGoalDia.setText(" "+PreferenceUtil.getBPMinDiastole(BPStandardActivity.this) + "~"
                + PreferenceUtil.getBPMaxDiastole(BPStandardActivity.this));

        ViewGroup.LayoutParams layoutParams = viewNormal.getLayoutParams();
        ViewGroup.LayoutParams underParams = viewUnder.getLayoutParams();

        if (inputMaxDiastole > NORMAL_DIASTOLE) {
            width = (float) (NORMAL_WIDTH + ((inputMaxDiastole - NORMAL_DIASTOLE) * 2.4));
        } else {
            width = (float) (NORMAL_WIDTH - ((NORMAL_DIASTOLE - inputMaxDiastole) * 1.2));
        }

        if (inputMaxSystole > NORMAL_SYSTOLE) {
            height = (float) (NORMAL_HEIGHT + ((inputMaxSystole - NORMAL_SYSTOLE) * 1.5));
        } else {
            height = (float) (NORMAL_HEIGHT - ((NORMAL_SYSTOLE - inputMaxSystole) * 1.0));
        }

        if (inputMinDiastole < 60) {
            underWidth = (float) (UNDER_WIDTH - ((60 - inputMinDiastole) * 1.2));
        } else if (inputMinDiastole == 60) {
            underWidth = (float) UNDER_WIDTH;
        }

        if (inputMinSystole < 90) {
            underHeight = (float) (UNDER_HEIGHT - ((90 - inputMinSystole) * 1.0));
        } else if (inputMinSystole == 90) {
            underHeight = (float) UNDER_HEIGHT;
        }

        layoutParams.width = convertDpToPixel(width);
        layoutParams.height = convertDpToPixel(height);

        underParams.width = convertDpToPixel(underWidth);
        underParams.height = convertDpToPixel(underHeight);

        viewNormal.setLayoutParams(layoutParams);
        viewUnder.setLayoutParams(underParams);
    }

    /**
     * 레이아웃 설정
     */

    private void setLayout() {

        btnSetGoal = (Button) findViewById(R.id.btn_setting_goal);
        btnClose = (Button) findViewById(R.id.btn_close);
        tvGoalSys = (TextView) findViewById(R.id.tv_goal_sys);
        tvGoalDia = (TextView) findViewById(R.id.tv_goal_dia);
        viewNormal = findViewById(R.id.view_normal);
        viewUnder = findViewById(R.id.view_under);
        btnSetGoal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(BPStandardActivity.this, BPSetGoalActivity.class);
                    startActivityForResult(intent, REQUEST_BP_SET_GOAL);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_BP_SET_GOAL) {
                if (data != null) {
                    int diastole = data.getIntExtra("diastole", 0);
                    if (diastole != 0) {
                        inputMaxDiastole = diastole;
                    }
                    int systole = data.getIntExtra("systole", 0);
                    if (systole != 0) {
                        inputMaxSystole = systole;
                    }
                    setNormalView();
                }
            }
        }
    }

}
