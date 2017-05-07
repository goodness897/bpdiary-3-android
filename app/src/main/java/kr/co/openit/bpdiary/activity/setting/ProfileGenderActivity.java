package kr.co.openit.bpdiary.activity.setting;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.services.ProfileService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class ProfileGenderActivity extends NonMeasureActivity {

    private LinearLayout llEmptyView;

    /**
     * 저장 버튼
     */
    private Button btnSave;

    /**
     * 성별 남자 라디오버튼
     */
    private RadioButton rbMan;

    /**
     * 성별 여자 라디오버튼
     */
    private RadioButton rbWoman;

    /**
     * 라디오그룹
     */
    private RadioGroup rgType;

    /**
     * 최종 저장될 성별
     */
    private String userGender;

    private ImageView ivCenterLine;

    private ProfileService profileService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_gender);

        initToolbar(getString(R.string.setting_activity_gender));

        AnalyticsUtil.sendScene(ProfileGenderActivity.this, "3_M 프로필 성별");

        profileService = new ProfileService(ProfileGenderActivity.this);

        userGender = PreferenceUtil.getGender(ProfileGenderActivity.this);

        setLayout();

        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_man:
                        userGender = ManagerConstants.Gender.MALE;
                        rbMan.setSelected(true);
                        rbWoman.setSelected(false);
                        btnSave.setEnabled(true);
                        ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_5f5b5d));
                        break;
                    case R.id.rb_woman:
                        userGender = ManagerConstants.Gender.FEMALE;
                        rbMan.setSelected(false);
                        rbWoman.setSelected(true);
                        btnSave.setEnabled(true);
                        ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_5f5b5d));
                        break;

                }

            }
        });

        btnSave.setEnabled(false);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (BPDiaryApplication.isNetworkState(ProfileGenderActivity.this)) {
                        ModifyUserInfoAsync modSync = new ModifyUserInfoAsync();
                        modSync.execute();
                    } else {
                        //다이얼로그 호출 후 앱 종료
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(ProfileGenderActivity.this,
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

        initRadioGroup();

    }

    /**
     * 레이아웃 세팅
     */

    private void setLayout() {
        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        rgType = (RadioGroup)findViewById(R.id.rg_type);
        rbMan = (RadioButton)findViewById(R.id.rb_man);
        rbWoman = (RadioButton)findViewById(R.id.rb_woman);
        btnSave = (Button)findViewById(R.id.btn_save);
        ivCenterLine = (ImageView)findViewById(R.id.iv_center_line);
    }

    /**
     * 초기 Preference 저장된 Gender 값에 따라 Clicked RadioButton 보여주기
     */

    private void initRadioGroup() {
        if (userGender.equals(ManagerConstants.Gender.MALE)) {
            //            rbMan.setSelected(true);
            rbMan.setChecked(true);
            //            rbWoman.setSelected(false);
            rbWoman.setChecked(false);
            btnSave.setEnabled(true);
        } else {
            //            rbMan.setSelected(false);
            rbMan.setChecked(false);
            //            rbWoman.setSelected(true);
            rbWoman.setChecked(true);
            btnSave.setEnabled(true);
        }
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
                         PreferenceUtil.getEncEmail(ProfileGenderActivity.this));
                if (ManagerConstants.Gender.MALE.equals(userGender)) {
                    data.put(ManagerConstants.RequestParamName.GENDER, ManagerConstants.Gender.MALE);
                } else {
                    data.put(ManagerConstants.RequestParamName.GENDER, ManagerConstants.Gender.FEMALE);
                }

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
                        if (ManagerConstants.Gender.MALE.equals(userGender)) {
                            PreferenceUtil.setGender(ProfileGenderActivity.this, ManagerConstants.Gender.MALE);
                        } else {
                            PreferenceUtil.setGender(ProfileGenderActivity.this, ManagerConstants.Gender.FEMALE);
                        }
                        Toast.makeText(ProfileGenderActivity.this,
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
