package kr.co.openit.bpdiary.activity.setting;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.CustomProgressDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.services.ProfileService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class ProfileLeaveActivity extends BaseActivity {

    private CustomProgressDialog mProgress;

    private ProfileService profileService;

    private TextView tvContent;

    private TextView tvLeaveTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave);
        tvContent = (TextView)findViewById(R.id.tv_leave_content);
        tvLeaveTitle = (TextView)findViewById(R.id.tv_leave_title);

        SpannableString content = new SpannableString(getString(R.string.setting_check_leave));
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        tvLeaveTitle.setText(content);


        AnalyticsUtil.sendScene(ProfileLeaveActivity.this, "3_M 프로필 탈퇴팝업");

        /**
         * profile service
         */
        profileService = new ProfileService(ProfileLeaveActivity.this);

        context = ProfileLeaveActivity.this;

//        if(ManagerConstants.Language.KOR.equals(PreferenceUtil.getLanguage(context))) {
//            SpannableStringBuilder sp = new SpannableStringBuilder(tvContent.getText().toString());
//            sp.setSpan(new AbsoluteSizeSpan(33), 27, 40, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//            tvContent.setText(sp);
//        }


        Button btnCancel = (Button)findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    finish();
                }
            }
        });

        Button btnLeave = (Button)findViewById(R.id.btn_leave);
        btnLeave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (BPDiaryApplication.isNetworkState(ProfileLeaveActivity.this)) {
                        LeaveMemberAsync leaveMemberAsync = new LeaveMemberAsync();
                        leaveMemberAsync.execute();
                    } else {
                        //다이얼로그 호출 후 앱 종료
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(ProfileLeaveActivity.this,
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
     * 서비스 해지 Async
     */
    private class LeaveMemberAsync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            if (mProgress == null) {
                mProgress = new CustomProgressDialog(ProfileLeaveActivity.this);
                mProgress.setCancelable(false);
            }
            mProgress.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {

                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(ProfileLeaveActivity.this));
                resultJSON = profileService.leaveMember(data);

                if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                            .toString())) {

                        profileService.deleteLeaveMember();
                        PreferenceUtil.removeAllPreferences(ProfileLeaveActivity.this);

                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {

            if (mProgress != null && mProgress.isShowing()) {
                mProgress.hide();
            }

            try {

                if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                            .toString())) {
                        Toast.makeText(ProfileLeaveActivity.this, R.string.profile_out_toast, Toast.LENGTH_SHORT)
                                .show();

                        //앱 종료
                        ActivityCompat.finishAffinity(ProfileLeaveActivity.this);
                        System.runFinalization();
                        System.exit(0);
//                        ManagerUtil.finishedAllActivity();
//                        moveTaskToBack(true);
//                        android.os.Process.killProcess(android.os.Process.myPid());
//                        System.exit(1);
                    }
                }

            } catch (Exception e) {
                //LogUtil.e(context, e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }
}