package kr.co.openit.bpdiary.activity.intro;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.activity.main.MainActivity;
import kr.co.openit.bpdiary.common.constants.CommonConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.model.EventInfoData;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;


/**
 * Created by srpark on 2016-12-21.
 */

public class LoginActivity extends NonMeasureActivity implements
                           View.OnClickListener,
                           GoogleApiClient.ConnectionCallbacks,
                           GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 0;

    private ScrollView svEmailLogin;

    private TextView tvPasswordSearch;

    private Context context;

    private boolean signedInUser;

    private boolean mIntentInProgress;

    /**
     * Google+ 클라이언트
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Google+ Connection 정보 Result
     */
    private ConnectionResult mConnectionResult;

    /**
     * email
     */
    private String email;

    /**
     * email 입력
     */
    private EditText etInputEmail;

    /**
     * password 입력
     */
    private EditText etInputPassword;

    /**
     * 이메일에서 로그인 버튼
     */
    private Button btnInputEmailLogin;

    /**
     * validation 메세지
     */
    private String validationMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_sign_up);

        actList.add(this);
        context = LoginActivity.this;

        AnalyticsUtil.sendScene(LoginActivity.this, "3_로그인 메인");

        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
                                                            .addOnConnectionFailedListener(this)
                                                            .addApi(Plus.API/*
                                                                             * , Plus.PlusOptions.builder().build()
                                                                             */)
                                                            .addScope(Plus.SCOPE_PLUS_LOGIN)
                                                            .build();
        mIntentInProgress = false;
        callbackManager = CallbackManager.Factory.create();
        mLoginManager = LoginManager.getInstance();
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.rl_login_or_sign_up);
        setLayout(layout);
        setData();
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
    public void setLayout(RelativeLayout layout) {
        super.setLayout(layout);
        svEmailLogin = (ScrollView)findViewById(R.id.sv_email_login);
        tvPasswordSearch = (TextView)findViewById(R.id.tv_password_search);
        etInputEmail = (EditText)findViewById(R.id.et_input_email);
        etInputPassword = (EditText)findViewById(R.id.et_input_password);
        btnInputEmailLogin = (Button)findViewById(R.id.btn_input_email_login);

        btnFacebookLogin.setOnClickListener(this);
        btnGoogleLogin.setOnClickListener(this);
        btnEmailLogin.setOnClickListener(this);
        llLoginNaviBack.setOnClickListener(this);
        llNaviBack.setOnClickListener(this);
        btnInputEmailLogin.setOnClickListener(this);
    }

    /**
     * 데이터 셋팅
     */
    private void setData() {
        SpannableString styledString = new SpannableString(getResources().getString(R.string.common_password_search));
        ClickableSpan clickableSpan = new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                if (!ManagerUtil.isClicking()) {
                    Intent intent = new Intent(LoginActivity.this, PasswordSearchActivity.class); //비밀번호 찾기 화면
                    startActivity(intent);
                    overridePendingTransition(0, 0);
                    logoutGooglePlus();
                }
            }
        };
        styledString.setSpan(clickableSpan, 0, styledString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPasswordSearch.setText(styledString);
        tvPasswordSearch.setMovementMethod(LinkMovementMethod.getInstance());

        ivJoinType.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_start_screen_login));
        tvJoinMethod.setText(getResources().getString(R.string.common_login));

        LanguageSpecificTerms(context);

        etInputEmail.addTextChangedListener(new TextWatcher() {

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

        etInputPassword.addTextChangedListener(new TextWatcher() {

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
    public void onClick(View v) {
        if (!ManagerUtil.isClicking()) {

            switch (v.getId()) {
                case R.id.btn_facebook_login:
                    if (BPDiaryApplication.isNetworkState(context)) {
                        AnalyticsUtil.sendScene(LoginActivity.this, "3_로그인 페이스북");
                        if (AccessToken.getCurrentAccessToken() == null) {
                            facebookLogin();
                        } else {
                            facebookLogout();
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
                case R.id.btn_google_login:
                    if (BPDiaryApplication.isNetworkState(context)) {
                        showLodingProgress();
                        AnalyticsUtil.sendScene(LoginActivity.this, "3_로그인 구글");
                        if (!mGoogleApiClient.isConnecting()) {
                            hideLodingProgress();
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
                    svEmailLogin.setVisibility(View.VISIBLE);
                    llLoginDisplay.setVisibility(View.GONE);
                    llTerms.setVisibility(View.GONE);

                    break;
                case R.id.btn_input_email_login:
                    if (BPDiaryApplication.isNetworkState(context)) {
                        AnalyticsUtil.sendScene(LoginActivity.this, "3_로그인 이메일");
                        //로직타고  success 후 저장
                        if (checkValidation()) {
                            PreferenceUtil.setEncEmail(context, etInputEmail.getText().toString());
                            PreferenceUtil.setEncPassword(context, etInputPassword.getText().toString());
                            PreferenceUtil.setLoginType(context, ManagerConstants.LoginType.LOGIN_TYPE_EMAIL);
                            Intent intent = new Intent(context, LoginLogicActivity.class);
                            startActivityForResult(intent, CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_EMAIL);
                            logoutGooglePlus();
                            facebookLogout();
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
                case R.id.ll_login_navi_back:
                    svEmailLogin.setVisibility(View.GONE);
                    llLoginDisplay.setVisibility(View.VISIBLE);
                    llTerms.setVisibility(View.VISIBLE);
                    break;

                case R.id.ll_navi_back:
                    onBackPressed();
                    break;
            }
        }
    }

    /**
     * 페이스북 로그인
     */
    private void facebookLogin() {
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
                                                                             email = jsonObject.getString("email");
                                                                             PreferenceUtil.setEncEmail(context, email);
                                                                             PreferenceUtil.setLoginType(context,
                                                                                                         ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK);
                                                                             //로그인 서버 통신 추가
                                                                             Intent intent =
                                                                                           new Intent(context,
                                                                                                      LoginLogicActivity.class);
                                                                             startActivityForResult(intent,
                                                                                                    CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_FACEBOOK);
                                                                             overridePendingTransition(0, 0);
                                                                             logoutGooglePlus();
                                                                             facebookLogout();
                                                                         } catch (JSONException e) {
                                                                             e.printStackTrace();
                                                                         } catch (Exception e) {
                                                                             e.printStackTrace();
                                                                         }
                                                                     }
                                                                 });
                // 불러올 것들을 세팅한다.
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, email, gender");
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

    private void getGoogleProfile() {
        hideLodingProgress();
        try {
            email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            PreferenceUtil.setEncEmail(this, email);
            PreferenceUtil.setLoginType(context, ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE);
            Intent intent = new Intent(context, LoginLogicActivity.class);
            startActivityForResult(intent, CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_GOOGLE);
            overridePendingTransition(0, 0);
            logoutGooglePlus();
            facebookLogout();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_GOOGLE
            || requestCode == CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_FACEBOOK
            || requestCode == CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_EMAIL) {
            if (resultCode == RESULT_OK) {
                Log.d("srpark", "메인으로");
                Intent intent = new Intent(context, MainActivity.class);
                if (data != null) {
                    EventInfoData eventInfoData = (EventInfoData)data.getSerializableExtra(CommonConstants.EVENT_DATA);
                    intent.putExtra(CommonConstants.EVENT_DATA, eventInfoData);
                }
                startActivity(intent);
                for(int i = 0; i < actList.size(); i++){
                    actList.get(i).finish();
                }
                overridePendingTransition(0, 0);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("srpark", "로그인 실패");
                PreferenceUtil.removeAllPreferences(context);
            }
        } else if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                signedInUser = false;
                mIntentInProgress = false;
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
            } else {
                mGoogleApiClient.disconnect();
            }

        }

    }

    @Override
    public void onBackPressed() {
        if (svEmailLogin.getVisibility() == View.VISIBLE) {
            svEmailLogin.setVisibility(View.GONE);
            llTerms.setVisibility(View.VISIBLE);
            llLoginDisplay.setVisibility(View.VISIBLE);
            etInputEmail.setText("");
            etInputPassword.setText("");
        } else {
            super.onBackPressed();
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
        }

        return title;
    }

    /**
     * 입력값 유효성 검사
     */
    private boolean checkValidation() {

        boolean isValidation = true;

        try {
            if (etInputEmail.getText().toString().isEmpty()) {
                isValidation = false;
                validationMessage = getResources().getString(R.string.profile_not_input_email);
            } else {
                //공백이 아니면 이메일 형식 체크
                if (!ManagerUtil.isEmail(etInputEmail.getText().toString())) {
                    isValidation = false;
                    validationMessage = getResources().getString(R.string.profile_input_val_email);
                } else {
                    //이메일 형식이 맞으면
                    if (etInputPassword.getText().toString().isEmpty()) {
                        isValidation = false;
                        validationMessage = getResources().getString(R.string.profile_not_input_password);
                    } else {
                        if (!ManagerUtil.passwordValidate(etInputPassword.getText().toString())) {
                            isValidation = false;
                            validationMessage = getResources().getString(R.string.profile_input_val_password);
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
            btnInputEmailLogin.setEnabled(true);
        } else {
            btnInputEmailLogin.setEnabled(false);
        }

        return isValidation;
    }

}
