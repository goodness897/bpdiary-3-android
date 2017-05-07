package kr.co.openit.bpdiary.activity.setting;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.services.ProfileService;
import kr.co.openit.bpdiary.utils.AesssUtil;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class ProfileNameActivity extends NonMeasureActivity {

    private LinearLayout llEmptyView;

    /**
     * 이름 입력 EditText
     */
    private EditText etUserFirstName;

    /**
     * 성 입력 EditText
     */
    private EditText etUserLastName;

    /**
     * 저장 버튼
     */
    private Button btnSave;

    private ProfileService profileService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_name);

        initToolbar(getString(R.string.setting_profile_name));

        AnalyticsUtil.sendScene(ProfileNameActivity.this, "3_M 프로필 이름");

        profileService = new ProfileService(ProfileNameActivity.this);

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        etUserFirstName = (EditText)findViewById(R.id.et_user_first_name);
        etUserLastName = (EditText)findViewById(R.id.et_user_last_name);
        btnSave = (Button)findViewById(R.id.btn_save);

        if (etUserFirstName.getText().length() > 0 && etUserLastName.getText().length() > 0) {
            btnSave.setEnabled(true);
        } else {
            btnSave.setEnabled(false);
        }

        etUserFirstName.setText(PreferenceUtil.getDecFirstName(ProfileNameActivity.this));
        etUserLastName.setText(PreferenceUtil.getDecLastName(ProfileNameActivity.this));
        etUserFirstName.requestFocus();

        //키보드 보이게 하는 부분

        final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        etUserFirstName.addTextChangedListener(firstNameTextWatcher);
        etUserLastName.addTextChangedListener(lastNameTextWatcher);

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    // 저장버튼 클릭 후 PreferenceUtil 저장 후 종료
                    if (BPDiaryApplication.isNetworkState(ProfileNameActivity.this)) {
                        ModifyUserInfoAsync modSync = new ModifyUserInfoAsync();
                        modSync.execute();
                    } else {
                        //키보드 사라지게 하기
                        imm.hideSoftInputFromWindow(etUserFirstName.getWindowToken(), 0);

                        //다이얼로그 호출 후 앱 종료
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(ProfileNameActivity.this,
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

    // FirstName TextWatcher
    private TextWatcher firstNameTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (str.length() > 0 && etUserLastName.getText().toString().length() > 0) {

                btnSave.setEnabled(true);

            } else {

                btnSave.setEnabled(false);
            }

        }
    };

    // LastName TextWatcher

    private TextWatcher lastNameTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (str.length() > 0 && etUserFirstName.getText().toString().length() > 0) {

                btnSave.setEnabled(true);
            } else {

                btnSave.setEnabled(false);
            }

        }
    };


    /**
     * 사용자 정보 수정
     */
    private class ModifyUserInfoAsync extends AsyncTask<Void, Void, JSONObject> {
        String strFirstName = "";
        String strLastName = "";

        @Override
        protected void onPreExecute() {
            showLodingProgress();

            strFirstName = etUserFirstName.getText().toString();
            strLastName = etUserLastName.getText().toString();

        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {
                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(ProfileNameActivity.this));
                data.put(ManagerConstants.RequestParamName.FIRST_NAME, AesssUtil.encrypt(strFirstName));
                data.put(ManagerConstants.RequestParamName.LAST_NAME, AesssUtil.encrypt(strLastName));

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

                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT).toString())) {
                        PreferenceUtil.setEncFirstName(ProfileNameActivity.this, strFirstName);
                        PreferenceUtil.setEncLastName(ProfileNameActivity.this, strLastName);
                        Toast.makeText(ProfileNameActivity.this,
                                getResources().getString(R.string.profile_mod_finish),
                                Toast.LENGTH_SHORT).show();
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
