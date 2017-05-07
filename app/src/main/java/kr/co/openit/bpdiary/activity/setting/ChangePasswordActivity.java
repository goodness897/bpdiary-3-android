package kr.co.openit.bpdiary.activity.setting;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
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
import kr.co.openit.bpdiary.services.IntroService;
import kr.co.openit.bpdiary.utils.AesssUtil;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class ChangePasswordActivity extends NonMeasureActivity {

    private LinearLayout llEmptyView;

    private Button btnSave;

    private EditText editPassword;

    private EditText editPasswordCheck;

    private IntroService introService;

    private String strPassword = "";

    private String validationMessage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change_password);
        initToolbar(getString(R.string.setting_activity_password));

        AnalyticsUtil.sendScene(ChangePasswordActivity.this, "3_M 프로필 비밀번호");

        context = ChangePasswordActivity.this;

        introService = new IntroService(ChangePasswordActivity.this);

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        editPassword = (EditText)findViewById(R.id.et_password);
        editPasswordCheck = (EditText)findViewById(R.id.et_password_check);
        btnSave = (Button)findViewById(R.id.btn_change_pass_save);

        editPassword.addTextChangedListener(textWatcher);
        editPasswordCheck.addTextChangedListener(checkTextWatcher);

        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    if (checkValidation()) {
                        strPassword = editPassword.getText().toString();
                        if (BPDiaryApplication.isNetworkState(ChangePasswordActivity.this)) {
                            ModifyPasswordAsync modifyPasswordAsync = new ModifyPasswordAsync();
                            modifyPasswordAsync.execute();
                        } else {
                            //다이얼로그 호출 후 앱 종료
                            DefaultOneButtonDialog defaultOneButtonDialog =
                                    new DefaultOneButtonDialog(ChangePasswordActivity.this,
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

                    } else {
                        if (validationMessage != null) {
                            DefaultOneButtonDialog defaultOneButtonDialog =
                                    new DefaultOneButtonDialog(context,
                                            "",
                                            validationMessage,
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
            }
        });

    }

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkValidation();

        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private TextWatcher checkTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            checkValidation();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 입력값 유효성 검사
     */
    private boolean checkValidation() {

        boolean isValidation = true;

        try {
            if (editPassword.getText().toString().isEmpty()) {
                isValidation = false;
                validationMessage = getResources().getString(R.string.profile_not_input_password);
            } else {
                //공백이 아니면
                if (!ManagerUtil.passwordValidate(editPassword.getText().toString())) {
                    isValidation = false;
                    validationMessage = getResources().getString(R.string.profile_input_val_password);
                } else {
                    //패스워드 형식이 맞으면
                    if (editPasswordCheck.getText().toString().isEmpty()) {
                        isValidation = false;
                        validationMessage = getResources().getString(R.string.profile_not_input_password);
                    } else {
                        //공백이 아니면
                        if (!ManagerUtil.passwordValidate(editPasswordCheck.getText().toString())) {
                            isValidation = false;
                            validationMessage = getResources().getString(R.string.profile_input_val_password);
                        } else {
                            //패스워드 확인 형식이 맞으면
                            if (!editPassword.getText().toString().equals(editPasswordCheck.getText().toString())) {
                                isValidation = false;
                                validationMessage =
                                                  getResources().getString(R.string.profile_input_not_concurrence_password);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            isValidation = false;
            validationMessage = getResources().getString(R.string.profile_not_input_val_data);
            e.printStackTrace();
        }

        if (isValidation) {
            btnSave.setEnabled(true);
        } else {
            btnSave.setEnabled(false);
        }

        return isValidation;
    }

    /**
     * 사용자 정보 수정
     */
    private class ModifyPasswordAsync extends AsyncTask<Void, Void, JSONObject> {

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
                         PreferenceUtil.getEncEmail(ChangePasswordActivity.this));
                data.put(ManagerConstants.RequestParamName.PASSWORD, AesssUtil.encrypt(strPassword));

                resultJSON = introService.changePassword(data);
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
                        try {
                            PreferenceUtil.setEncPassword(ChangePasswordActivity.this, AesssUtil.encrypt(strPassword));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(ChangePasswordActivity.this,
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
