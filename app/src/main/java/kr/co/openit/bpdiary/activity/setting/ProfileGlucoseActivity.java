package kr.co.openit.bpdiary.activity.setting;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.customview.CustomSwitch;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.services.ProfileService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;


public class ProfileGlucoseActivity extends NonMeasureActivity {

    /**
     * 혈당관리 변경 스위치
     */
//    private SwitchCompat switchManageGlucose;

    private LinearLayout llEmptyView;

    private CustomSwitch switchManageGlucose;
    /**
     * 저장 버튼
     */
    private Button btnSave;

    private String bgYn = "N";

    private ProfileService profileService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_glucose);

        initToolbar(getString(R.string.setting_manage_blood_glucose));

        AnalyticsUtil.sendScene(ProfileGlucoseActivity.this, "3_M 프로필 당뇨관리");

        profileService = new ProfileService(ProfileGlucoseActivity.this);

        setLayout();

        if (PreferenceUtil.getUsingBloodGlucose(ProfileGlucoseActivity.this)) {
            switchManageGlucose.setChecked(true);
            bgYn = "Y";
        } else {
            switchManageGlucose.setChecked(false);
            bgYn = "N";
        }
    }

    private void setLayout() {

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        switchManageGlucose = (CustomSwitch)findViewById(R.id.manage_glucose_switch);

        btnSave = (Button)findViewById(R.id.btn_save);

        switchManageGlucose.addSwitchObserver(new CustomSwitch.CustomSwitchObserver() {
            @Override
            public void onCheckStateChange(CustomSwitch switchView, boolean isChecked) {
                if (isChecked) {
                    bgYn = "Y";
                } else {
                    bgYn = "N";
                }
            }
        });

//        switchManageGlucose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    bgYn = "Y";
//                } else {
//                    bgYn = "N";
//                }
//            }
//        });

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (BPDiaryApplication.isNetworkState(ProfileGlucoseActivity.this)) {
                        ModifyUserInfoAsync modSync = new ModifyUserInfoAsync();
                        modSync.execute();
                    } else {
                        //다이얼로그 호출 후 앱 종료
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(ProfileGlucoseActivity.this,
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
     * 사용자 정보 수정
     */
    private class ModifyUserInfoAsync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {
                data.put(ManagerConstants.RequestParamName.UUID,
                         PreferenceUtil.getEncEmail(ProfileGlucoseActivity.this));
                data.put(ManagerConstants.RequestParamName.BG_YN, bgYn);

                resultJSON = profileService.updateMember(data);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {
            hideLodingProgress();

            try {

                if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                     .toString())) {
                        //                        PreferenceUtil.setHeight(ProfileGlucoseActivity.this, bgYn);
                        if ("Y".equals(bgYn)) {
                            PreferenceUtil.setUsingBloodGlucose(ProfileGlucoseActivity.this, true);
                        } else {
                            PreferenceUtil.setUsingBloodGlucose(ProfileGlucoseActivity.this, false);
                        }
                        Toast.makeText(ProfileGlucoseActivity.this,
                                       getResources().getString(R.string.profile_mod_finish),
                                       Toast.LENGTH_SHORT)
                             .show();

                        setResult(RESULT_OK);
                        finish();

                    }
                } else {
                    //TODO 에러 팝업
                }

            } catch (Resources.NotFoundException e) {
                //LogUtil.e(context, e.getLocalizedMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                //LogUtil.e(context, e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }
}
