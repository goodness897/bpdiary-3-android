package kr.co.openit.bpdiary.activity.setting;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.CustomDateDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.services.ProfileService;
import kr.co.openit.bpdiary.utils.AesssUtil;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

import static kr.co.openit.bpdiary.R.string.setting_activity_birth;

public class ProfileBirthActivity extends NonMeasureActivity {

    private LinearLayout llEmptyView;

    /**
     * 생년월일 입력 버튼(생년월일 텍스트 박힘)
     */
    private Button btnInputBirth;

    /**
     * 저장 버튼
     */

    private Button btnSave;

    /**
     * 최종 저장될 생년월일
     */
    private String userBirth;

    private ProfileService profileService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birth);
        initToolbar(getString(setting_activity_birth));

        AnalyticsUtil.sendScene(ProfileBirthActivity.this, "3_M 프로필 생년월일");

        profileService = new ProfileService(ProfileBirthActivity.this);

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        btnInputBirth = (Button)findViewById(R.id.btn_birth);
        btnInputBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {
                    CustomDateDialog dateDialog = new CustomDateDialog(view.getContext(), year, (month - 1), day);
                    dateDialog.show();
                }

            }
        });

        setTextInputBirth();

        btnSave = (Button)findViewById(R.id.btn_change_pass_save);
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (BPDiaryApplication.isNetworkState(ProfileBirthActivity.this)) {
                        if (!"".equals(userBirth)) {
                            ModifyUserInfoAsync modSync = new ModifyUserInfoAsync();
                            modSync.execute();
                        } else {
                            Toast.makeText(ProfileBirthActivity.this,
                                    getResources().getString(R.string.profile_not_input_birth),
                                    Toast.LENGTH_SHORT)
                                    .show();
                        }
                    } else {
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(ProfileBirthActivity.this,
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
        btnSave.setEnabled(false);
    }

    /**
     * 생년월일 입력 버튼 초기값 세팅
     */

    private void setTextInputBirth() {
        String birth = PreferenceUtil.getDecDayOfBirth(ProfileBirthActivity.this).replace("/", "");
        String transBirth = "";
        String strMeasureDt = "";
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMdd");
        Date s = null;
        try {
            s = transFormat.parse(birth);
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            transBirth = format.format(s);
            Log.d("trans", transBirth);
            strMeasureDt = transBirth.replaceAll("/", "");

            String[] date =
                    ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(ProfileBirthActivity.this)),
                            ManagerUtil.ShowFormatPosition.SECOND,
                            true,
                            "/",
                            ":",
                            "yyyyMMddHHmmss",
                            strMeasureDt + "000000");

            transBirth = date[0];
        } catch (ParseException e) {
        }

        // transBirth
        if (!TextUtils.isEmpty(transBirth)) {
            final Calendar c = Calendar.getInstance();
            c.setTime(s);
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH) + 1;
            day = c.get(Calendar.DAY_OF_MONTH);
            btnInputBirth.setText(transBirth);
        } else {
            btnInputBirth.setText(getString(setting_activity_birth));
        }
    }

    /**
     * Dialog로부터 받는 생년월일
     *
     * @param year 년도
     * @param month 월
     * @param day 일
     */

    public void receiveMessage(int year, int month, int day) {

        this.year = year;
        this.month = month;
        this.day = day;

        userBirth = setUserBirth(year, month, day);

        String strMeasureDt = userBirth.replaceAll("/", "");
        String strMeasureTime = ManagerUtil.getCurrentTime().replaceAll("/", "");

        String[] date =
                      ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(ProfileBirthActivity.this)),
                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                   true,
                                                   "/",
                                                   ":",
                                                   "yyyyMMddHHmmss",
                                                   strMeasureDt + strMeasureTime + "00");

        btnInputBirth.setText(date[0]);
        btnInputBirth.setTextColor(Color.BLACK);
        btnSave.setEnabled(true);
    }

    /**
     * 생년월일 변환
     *
     * @param year 년
     * @param month 월
     * @param day 일
     * @return 년 월 일 합쳐진 String 값
     */

    private String setUserBirth(int year, int month, int day) {
        String strYear = String.valueOf(year);
        String strMonth = "";
        String strDay = "";

        if (month < 10) {
            strMonth = "0" + month;
        } else {
            strMonth = String.valueOf(month);
        }

        if (day < 10) {
            strDay = "0" + day;
        } else {
            strDay = String.valueOf(day);
        }
        return strYear + strMonth + strDay;
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
                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(ProfileBirthActivity.this));
                data.put(ManagerConstants.RequestParamName.DAY_OF_BIRTH, AesssUtil.encrypt(userBirth));

                //TODO Profile 이미지
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

                if(resultJSON != null) {
                    if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                .toString())) {
                            PreferenceUtil.setEncDayOfBirth(ProfileBirthActivity.this, userBirth.replaceAll("/", ""));
                            Toast.makeText(ProfileBirthActivity.this,
                                    getResources().getString(R.string.profile_mod_finish),
                                    Toast.LENGTH_SHORT)
                                    .show();

                            setResult(RESULT_OK);
                            finish();

                        }
                    } else {
                        //TODO 에러 팝업
                    }
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
