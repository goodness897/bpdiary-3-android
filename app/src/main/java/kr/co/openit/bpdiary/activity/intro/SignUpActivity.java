package kr.co.openit.bpdiary.activity.intro;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.DefaultAudience;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.common.constants.CommonConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.services.IntroService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;



/**
 * Created by srpark on 2016-12-21.
 */

public class SignUpActivity extends NonMeasureActivity implements
                            View.OnClickListener,
                            GoogleApiClient.ConnectionCallbacks,
                            GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;

    private Context context;

    private ScrollView svEmailSignUp;

    private Button btnEmailSignUp;

    /**
     * Google+ 클라이언트
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Google+ Connection 정보 Result
     */
    private ConnectionResult mConnectionResult;

    private boolean signedInUser;

    private boolean mIntentInProgress;

    /**
     * Api Call Service
     */
    private IntroService introService;

    /**
     * Email
     */
    private String strEmail;

    /**
     * 로그인 타입
     */
    private String loginType;

    private LoginManager mLoginManager;

    private long backKeyPressedTime = 0;

    /**
     * 회원가입 이메일 입력
     */
    private EditText etSignUpEmailInput;

    /**
     * 회원가입 비밀번호 입력
     */
    private EditText etSignUpPasswordInput;

    /**
     * 회원가입 비밀번호 확인 입력
     */
    private EditText etSignUpPasswordCheckInput;

    /**
     * 툴팁 레이아웃
     */
    private LinearLayout llPasswordToolTip;

    /**
     * 확인 툴팁 레이아웃
     */
    private LinearLayout llPasswordCheckToolTip;

    /**
     * validation 메세지
     */
    private String validationMessage;

    @Override
    protected void onPause() {
        super.onPause();

        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_sign_up);

        actList.add(this);
        AnalyticsUtil.sendScene(SignUpActivity.this, "3_회원가입 메인");

        context = SignUpActivity.this;

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                                                            .addOnConnectionFailedListener(this)
                                                            .addApi(Plus.API/*
                                                                             * , Plus.PlusOptions.builder().build()
                                                                             */)
                                                            .addScope(Plus.SCOPE_PLUS_LOGIN)
                                                            .build();

        mIntentInProgress = false;
        introService = new IntroService(context);
        callbackManager = CallbackManager.Factory.create();
        mLoginManager = LoginManager.getInstance();
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.rl_login_or_sign_up);
        setLayout(layout);
        setData();
    }

    @Override
    public void setLayout(RelativeLayout layout) {
        super.setLayout(layout);

        etSignUpEmailInput = (EditText)layout.findViewById(R.id.et_sign_up_email_input);
        etSignUpPasswordInput = (EditText)layout.findViewById(R.id.et_sign_up_password_input);
        etSignUpPasswordCheckInput = (EditText)layout.findViewById(R.id.et_sign_up_password_check_input);
        svEmailSignUp = (ScrollView)findViewById(R.id.sv_email_signup);
        btnEmailSignUp = (Button)findViewById(R.id.btn_email_sign_up);
        llPasswordToolTip = (LinearLayout)findViewById(R.id.ll_password_tool_tip);
        llPasswordCheckToolTip = (LinearLayout)findViewById(R.id.ll_password_check_tool_tip);

    }

    /**
     * 데이터 셋팅
     */
    private void setData() {
        btnEmailSignUp.setOnClickListener(this);

        btnFacebookLogin.setOnClickListener(this);
        btnGoogleLogin.setOnClickListener(this);
        btnEmailLogin.setOnClickListener(this);
        btnEmailSignUp.setOnClickListener(this);
        llSignupNaviBack.setOnClickListener(this);
        llNaviBack.setOnClickListener(this);
        ivJoinType.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_start_screen_signup));
        tvJoinMethod.setText(getResources().getString(R.string.common_sign_up));

        LanguageSpecificTerms(context);

        etSignUpPasswordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    llPasswordToolTip.setVisibility(View.VISIBLE);
                } else {
                    llPasswordToolTip.setVisibility(View.GONE);
                }
            }
        });

        etSignUpEmailInput.addTextChangedListener(new TextWatcher() {

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
        });

        etSignUpPasswordInput.addTextChangedListener(new TextWatcher() {

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
        });

        etSignUpPasswordCheckInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    llPasswordCheckToolTip.setVisibility(View.VISIBLE);
                } else {
                    llPasswordCheckToolTip.setVisibility(View.GONE);
                }
            }
        });

        etSignUpPasswordCheckInput.addTextChangedListener(new TextWatcher() {

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
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logoutGooglePlus();
    }

    private void logoutGooglePlus() {
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (!ManagerUtil.isClicking()) {

            switch (v.getId()) {
                case R.id.btn_facebook_login:
                    if (BPDiaryApplication.isNetworkState(context)) {
                        AnalyticsUtil.sendScene(SignUpActivity.this, "3_회원가입 페이스북");
                        if (AccessToken.getCurrentAccessToken() == null) {
                            facebookSignUp();
                        } else {
                            facebookLogout();
                        }
                        PreferenceUtil.setLoginType(context, ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK);
                    } else {
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(context,
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
                    break;
                case R.id.btn_google_login:
                    if (BPDiaryApplication.isNetworkState(context)) {
                        AnalyticsUtil.sendScene(SignUpActivity.this, "3_회원가입 구글");
                        if (!mGoogleApiClient.isConnecting()) {
                            signedInUser = true;
                            resolveSignInError();
                        } else {
                            getGoogleProfile();
                        }
                    } else {
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(context,
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
                    break;
                case R.id.btn_email_login:
                    svEmailSignUp.setVisibility(View.VISIBLE);
                    llLoginDisplay.setVisibility(View.GONE);
                    llTerms.setVisibility(View.GONE);
                    break;

                case R.id.btn_email_sign_up:
                    if (BPDiaryApplication.isNetworkState(context)) {

                        AnalyticsUtil.sendScene(SignUpActivity.this, "3_회원가입 이메일");
                        if (checkValidation()) {
                            PreferenceUtil.setEncEmail(context, etSignUpEmailInput.getText().toString());
                            PreferenceUtil.setEncPassword(context, etSignUpPasswordInput.getText().toString());
                            PreferenceUtil.setLoginType(context, ManagerConstants.LoginType.LOGIN_TYPE_EMAIL);
                            new CheckJoinAsync().execute();
                        } else {
                            if (validationMessage != null) {
                                DefaultOneButtonDialog defaultOneButtonDialog =
                                        new DefaultOneButtonDialog(context,
                                                showValidationTitle(validationMessage),
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
                    } else {
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(context,
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

                    break;
                case R.id.ll_signup_navi_back:
                    svEmailSignUp.setVisibility(View.GONE);
                    llLoginDisplay.setVisibility(View.VISIBLE);
                    llTerms.setVisibility(View.VISIBLE);
                    break;

                case R.id.ll_navi_back:
                    onBackPressed();
                    break;
            }
        }

    }

    private void getGoogleProfile() {
        try {
            strEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);

            PreferenceUtil.setEncEmail(context, strEmail);
            PreferenceUtil.setLoginType(context, ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE);
            Log.d("srpark", "email = " + PreferenceUtil.getEncEmail(context));
            Log.d("srpark", "loginType = " + PreferenceUtil.getLoginType(context));

            new CheckJoinAsync().execute();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resolveSignInError() {
        if (mConnectionResult != null) {
            if (mConnectionResult.hasResolution()) {
                try {
                    mIntentInProgress = true;
                    mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    /**
     * 페이스북 회원가입
     */
    private void facebookSignUp() {
        mLoginManager.logInWithReadPermissions(this, Arrays.asList("email"));
        mLoginManager.setDefaultAudience(DefaultAudience.FRIENDS);
        mLoginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        mLoginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                                                                 new GraphRequest.GraphJSONObjectCallback() {

                                                                     @Override
                                                                     public void onCompleted(JSONObject jsonObject,
                                                                                             GraphResponse response) {
                                                                         try {
                                                                             strEmail = jsonObject.getString("email");
                                                                             Log.d("facebook",
                                                                                   "이메일 : " + strEmail
                                                                                               + "이름 : "
                                                                                               + jsonObject.getString("name")
                                                                                               + "성별 : "
                                                                                               + jsonObject.getString("gender"));
                                                                             PreferenceUtil.setEncEmail(context,
                                                                                                        strEmail);

                                                                             new CheckJoinAsync().execute();
                                                                             mLoginManager.logOut();
                                                                         } catch (JSONException e) {
                                                                             e.printStackTrace();
                                                                         } catch (Exception e) {
                                                                             e.printStackTrace();
                                                                         }
                                                                     }
                                                                 });
                // 불러올 것들을 세팅한다.
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("srpark", "페이스북 실패");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("srpark", "페이스북 오류 = " + error.toString());
            }
        });
    }

    /**
     * 페이스북 로그아웃
     */
    private void facebookLogout() {
        new GraphRequest(AccessToken.getCurrentAccessToken(),
                         "/me/permissions/",
                         null,
                         HttpMethod.DELETE,
                         new GraphRequest.Callback() {

                             @Override
                             public void onCompleted(GraphResponse graphResponse) {
                                 mLoginManager.logOut();
                             }
                         }).executeAsync();
    }

    @Override
    public void onBackPressed() {
        if (svEmailSignUp.getVisibility() == View.VISIBLE) {
            svEmailSignUp.setVisibility(View.GONE);
            llTerms.setVisibility(View.VISIBLE);
            llLoginDisplay.setVisibility(View.VISIBLE);
            etSignUpEmailInput.setText("");
            etSignUpPasswordInput.setText("");
            etSignUpPasswordCheckInput.setText("");
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                signedInUser = false;
                mIntentInProgress = false;
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
            } else {
                mGoogleApiClient.disconnect();
            }
        } else if (requestCode == CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_SIGN_UP) {
            if (resultCode == RESULT_CANCELED) {
                PreferenceUtil.removeAllPreferences(context);
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        getGoogleProfile();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
            if (!this.isFinishing()) {
                GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            }
            return;
        }
        if (!mIntentInProgress) {
            // store mConnectionResult
            mConnectionResult = connectionResult;
            if (signedInUser) {
                resolveSignInError();
            }
        }

    }

    /**
     * 유효성 검사에 따른 타이틀
     * 
     * @param validationMessage
     * @return 타이틀
     */
    private String showValidationTitle(String validationMessage) {
        String title = "";
        if (validationMessage.equals(getResources().getString(R.string.profile_not_input_email))) {
            title = getString(R.string.dialog_title_alarm);
        } else if (validationMessage.equals(getResources().getString(R.string.profile_input_val_email))) {
            title = getString(R.string.dialog_title_alarm);
        } else if (validationMessage.equals(getResources().getString(R.string.profile_not_input_password))) {
            title = getString(R.string.dialog_title_check_password);

        } else if (validationMessage.equals(getResources().getString(R.string.profile_input_val_password))) {
            title = getString(R.string.dialog_title_check_password);

        } else if (validationMessage.equals(getResources().getString(R.string.profile_not_input_val_data))) {
            title = getString(R.string.dialog_title_alarm);

        } else if (validationMessage.equals(getResources().getString(R.string.profile_input_not_concurrence_password))) {
            title = getString(R.string.dialog_title_check_password);

        } else if (validationMessage.equals(getString(R.string.common_required_value_error_comment))) {
            title = getString(R.string.dialog_title_alarm);

        } else if (validationMessage.equals(getString(R.string.common_error_comment))) {
            title = getString(R.string.dialog_title_alarm);

        } else if (validationMessage.equals(getString(R.string.fail_reason_content_google))) {
            title = getString(R.string.dialog_title_google_login);

        } else if (validationMessage.equals(getString(R.string.fail_reason_content_facebook))) {
            title = getString(R.string.dialog_title_facebook_login);

        } else if (validationMessage.equals(getString(R.string.fail_reason_content_email))) {
            title = getString(R.string.dialog_title_email_login);

        } else if (validationMessage.equals(getString(R.string.fail_reason_content_exist_join))) {
            title = getString(R.string.dialog_title_email_login);

        }
        return title;
    }

    /**
     * 입력값 유효성 검사
     */
    private boolean checkValidation() {

        boolean isValidation = true;

        try {
            if (etSignUpEmailInput.getText().toString().isEmpty()) {
                isValidation = false;
                validationMessage = getResources().getString(R.string.profile_not_input_email);
            } else {
                //공백이 아니면
                if (!ManagerUtil.isEmail(etSignUpEmailInput.getText().toString())) {
                    isValidation = false;
                    validationMessage = getResources().getString(R.string.profile_input_val_email);
                } else {
                    //이메일 형식이 맞으면
                    if (etSignUpPasswordInput.getText().toString().isEmpty()) {
                        isValidation = false;
                        validationMessage = getResources().getString(R.string.profile_not_input_password);
                    } else {
                        //공백이 아니면
                        if (etSignUpPasswordInput.getText().toString().length() < 6) {
                            isValidation = false;
                            validationMessage = getResources().getString(R.string.profile_input_val_password);
                            llPasswordToolTip.setVisibility(View.VISIBLE);
                        } else {
                            //패스워드 형식이 맞으면
                            llPasswordToolTip.setVisibility(View.GONE);

                            if (etSignUpPasswordCheckInput.getText().toString().isEmpty()) {
                                isValidation = false;
                                validationMessage = getResources().getString(R.string.profile_not_input_password);
                            } else {
                                //공백이 아니면
                                if (etSignUpPasswordCheckInput.getText().toString().length() < 6) {
                                    isValidation = false;
                                    validationMessage = getResources().getString(R.string.profile_input_val_password);
                                    llPasswordCheckToolTip.setVisibility(View.VISIBLE);
                                } else {
                                    //패스워드 확인 형식이 맞으면

                                    if (!etSignUpPasswordInput.getText()
                                                              .toString()
                                                              .equals(etSignUpPasswordCheckInput.getText()
                                                                                                .toString())) {
                                        isValidation = false;
                                        validationMessage =
                                                          getResources().getString(R.string.profile_input_not_concurrence_password);
                                    } else {
                                        llPasswordCheckToolTip.setVisibility(View.GONE);
                                    }
                                }
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
            btnEmailSignUp.setEnabled(true);
        } else {
            btnEmailSignUp.setEnabled(false);
        }

        return isValidation;
    }

    /**
     * 가입여부 AsyncTask
     */
    private class CheckJoinAsync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {
                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(context));

                resultJSON = introService.checkJoin(data);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {
            hideLodingProgress();
            try {
                logoutGooglePlus();
                facebookLogout();
                if(resultJSON != null) {

                    if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {
                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                .toString())) {

                            Intent intent = new Intent(context, ProfileEnterActivity.class);
                            startActivityForResult(intent, CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_SIGN_UP);
                        } else {
                            if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_I.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                    .toString())) {
                                DefaultOneButtonDialog defaultOneButtonDialog =
                                        new DefaultOneButtonDialog(context,
                                                showValidationTitle(getString(R.string.common_required_value_error_comment)),
                                                getString(R.string.common_required_value_error_comment),
                                                getString(R.string.common_txt_confirm),
                                                new IDefaultOneButtonDialog() {

                                                    @Override
                                                    public void
                                                    onConfirm() {
                                                    }
                                                });
                                defaultOneButtonDialog.show();
                            } else if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_E.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                    .toString())) {
                                DefaultOneButtonDialog defaultOneButtonDialog =
                                        new DefaultOneButtonDialog(context,
                                                showValidationTitle(getString(R.string.common_error_comment)),
                                                getString(R.string.common_error_comment),
                                                getString(R.string.common_txt_confirm),
                                                new IDefaultOneButtonDialog() {

                                                    @Override
                                                    public void
                                                    onConfirm() {
                                                    }
                                                });
                                defaultOneButtonDialog.show();
                            } else if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_J.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                    .toString())) {

                                if (resultJSON.has(ManagerConstants.ResponseParamName.LOGIN_TYPE)) {
                                    if (ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE.equals(resultJSON.get(ManagerConstants.ResponseParamName.LOGIN_TYPE)
                                            .toString())) {
                                        DefaultOneButtonDialog defaultOneButtonDialog =
                                                new DefaultOneButtonDialog(context,
                                                        showValidationTitle(getString(R.string.fail_reason_content_google)),

                                                        getString(R.string.fail_reason_content_google),
                                                        getString(R.string.common_login),
                                                        new IDefaultOneButtonDialog() {

                                                            @Override
                                                            public void
                                                            onConfirm() {
                                                                Intent intent =
                                                                        new Intent(context,
                                                                                LoginActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                        defaultOneButtonDialog.show();
                                    } else if (ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK.equals(resultJSON.get(ManagerConstants.ResponseParamName.LOGIN_TYPE)
                                            .toString())) {
                                        DefaultOneButtonDialog defaultOneButtonDialog =
                                                new DefaultOneButtonDialog(context,
                                                        showValidationTitle(getString(R.string.fail_reason_content_facebook)),

                                                        getString(R.string.fail_reason_content_facebook),
                                                        getString(R.string.common_txt_confirm),
                                                        new IDefaultOneButtonDialog() {

                                                            @Override
                                                            public void
                                                            onConfirm() {
                                                                Intent intent =
                                                                        new Intent(context,
                                                                                LoginActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                        defaultOneButtonDialog.show();
                                    } else if (ManagerConstants.LoginType.LOGIN_TYPE_EMAIL.equals(resultJSON.get(ManagerConstants.ResponseParamName.LOGIN_TYPE)
                                            .toString())) {
                                        DefaultOneButtonDialog defaultOneButtonDialog =
                                                new DefaultOneButtonDialog(context,
                                                        showValidationTitle(getString(R.string.fail_reason_content_email)),

                                                        getString(R.string.fail_reason_content_email),
                                                        getString(R.string.common_txt_confirm),
                                                        new IDefaultOneButtonDialog() {

                                                            @Override
                                                            public void
                                                            onConfirm() {
                                                                Intent intent =
                                                                        new Intent(context,
                                                                                LoginActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                        defaultOneButtonDialog.show();
                                    } else {
                                        DefaultOneButtonDialog defaultOneButtonDialog =
                                                new DefaultOneButtonDialog(context,
                                                        showValidationTitle(getString(R.string.fail_reason_content_exist_join)),

                                                        getString(R.string.fail_reason_content_exist_join),
                                                        getString(R.string.common_txt_confirm),
                                                        new IDefaultOneButtonDialog() {

                                                            @Override
                                                            public void
                                                            onConfirm() {
                                                                Intent intent =
                                                                        new Intent(context,
                                                                                LoginActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        });
                                        defaultOneButtonDialog.show();
                                    }
                                } else {
                                    //가입된 계정
                                    DefaultOneButtonDialog defaultOneButtonDialog =
                                            new DefaultOneButtonDialog(context,
                                                    showValidationTitle(getString(R.string.fail_reason_content_exist_join)),

                                                    getString(R.string.fail_reason_content_exist_join),
                                                    getString(R.string.common_txt_confirm),
                                                    new IDefaultOneButtonDialog() {

                                                        @Override
                                                        public void
                                                        onConfirm() {
                                                            Intent intent =
                                                                    new Intent(context,
                                                                            LoginActivity.class);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                    defaultOneButtonDialog.show();
                                }

                            }

                        }
                    } else {
                        //TODO 에러 팝업
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(context,
                                        showValidationTitle(getString(R.string.common_error_comment)),
                                        getString(R.string.common_error_comment),
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

            } catch (Resources.NotFoundException e) {
                //LogUtil.e(context, e.getLocalizedMessage());
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
