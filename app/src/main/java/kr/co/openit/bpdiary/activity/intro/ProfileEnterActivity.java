package kr.co.openit.bpdiary.activity.intro;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.activity.main.MainActivity;
import kr.co.openit.bpdiary.common.constants.CommonConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.customview.CustomSwitch;
import kr.co.openit.bpdiary.databinding.ActivityProfileEnterBinding;
import kr.co.openit.bpdiary.dialog.CustomDateDialog;
import kr.co.openit.bpdiary.dialog.CustomProgressDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.model.EventInfoData;
import kr.co.openit.bpdiary.services.BloodPressureService;
import kr.co.openit.bpdiary.services.IntroService;
import kr.co.openit.bpdiary.utils.AesssUtil;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PhoneUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class ProfileEnterActivity extends BaseActivity implements View.OnClickListener {

    /**
     * 바인딩
     */
    private ActivityProfileEnterBinding binding;

    /**
     * 혈당 사용 여부
     */
    private boolean usingBloodGlucose;

    /**
     * 키 단위
     */
    private String heightUnit;

    /**
     * 사용자 키
     */
    private String userHeight;

    /**
     * 사용자 생년월일
     */
    private String userBirth;

    /**
     * 사용자 FirstName
     */
    private String userFirstName;

    /**
     * 사용자 LastName
     */
    private String userLastName;

    /**
     * 사용자 성별(남/녀)
     */
    private String userGender;

    /**
     * ProgressDialog
     */
    private CustomProgressDialog mProgress;

    private Context context;

    /**
     * 로그인 타입(이메일, Facebook, Google)
     */
    private String loginType;

    private LinearLayout llBackBtn;

    private kr.co.openit.bpdiary.customview.CustomSwitch swUseBloodGlucose;

    /**
     * IntroService
     */
    private IntroService introService;

    /**
     * validation 메세지
     */
    private String validationMessage;

    private BloodPressureService bpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnalyticsUtil.sendScene(ProfileEnterActivity.this, "3_회원가입 프로필 입력");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_enter);
        context = ProfileEnterActivity.this;

        swUseBloodGlucose = (kr.co.openit.bpdiary.customview.CustomSwitch)findViewById(R.id.sw_use_blood_glucose);

        introService = new IntroService(context);
        bpService = new BloodPressureService(context);

        mProgress = new CustomProgressDialog(context);
        mProgress.setCancelable(false);
        mProgress.setCanceledOnTouchOutside(false);

        llBackBtn = (LinearLayout)findViewById(R.id.ll_navi_back);
        llBackBtn.setOnClickListener(this);
        llBackBtn.setVisibility(View.VISIBLE);

        binding.btnUserBirth.setTypeface(Typeface.DEFAULT);
        binding.btnUserBirth.setOnClickListener(this);
        binding.btnComplete.setOnClickListener(this);
        binding.btnSkip.setOnClickListener(this);
        setData();

    }

    private void setData() {
        swUseBloodGlucose.setChecked(PreferenceUtil.getUsingBloodGlucose(context));
        usingBloodGlucose = PreferenceUtil.getUsingBloodGlucose(context);
        userGender = PreferenceUtil.getGender(context);
        loginType = PreferenceUtil.getLoginType(context);
        binding.spUserHeight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    //Cm
                    String strFeet = binding.etUserHeightFt.getText().toString();
                    String strInch = binding.etUserHeightIn.getText().toString();
                    StringBuilder sb = new StringBuilder();
                    if (!TextUtils.isEmpty(strFeet) && !TextUtils.isEmpty(strInch)) {

                        String strInchToCm = ManagerUtil.inchToCm(ManagerUtil.feetInchToInch(sb.append(strFeet)
                                                                                               .append(".")
                                                                                               .append(strInch)
                                                                                               .toString()));
                        binding.etUserHeightCm.setText(strInchToCm);
                    }
                    binding.llHeightUnitCm.setVisibility(View.VISIBLE);
                    binding.llHeightUnitFtIn.setVisibility(View.GONE);
                    heightUnit = ManagerConstants.Unit.CM;
                } else if (position == 1) {
                    //Ft.In
                    String strCm = binding.etUserHeightCm.getText().toString();
                    if (!TextUtils.isEmpty(strCm)) {
                        String strFeetInch = ManagerUtil.inchToFeetInch(ManagerUtil.cmToInch(strCm));
                        String[] strArrayFeetInch = strFeetInch.split("\\.");
                        binding.etUserHeightFt.setText(strArrayFeetInch[0]);
                        binding.etUserHeightIn.setText(strArrayFeetInch[1]);

                    }
                    binding.llHeightUnitCm.setVisibility(View.GONE);
                    binding.llHeightUnitFtIn.setVisibility(View.VISIBLE);
                    heightUnit = ManagerConstants.Unit.FT_IN;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        swUseBloodGlucose.addSwitchObserver(new CustomSwitch.CustomSwitchObserver() {

            @Override
            public void onCheckStateChange(CustomSwitch switchView, boolean isChecked) {
                usingBloodGlucose = isChecked;
            }
        });
        //        binding.swUseBloodGlucose.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        //
        //            @Override
        //            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        //                usingBloodGlucose = isChecked;
        //            }
        //        });

        binding.rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_user_gender_male:
                        binding.rbUserGenderMale.setSelected(true);
                        binding.rbUserGenderFemale.setSelected(false);
                        userGender = ManagerConstants.Gender.MALE;
                        binding.ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_5f5b5d));
                        break;
                    case R.id.rb_user_gender_female:
                        userGender = ManagerConstants.Gender.FEMALE;
                        binding.rbUserGenderMale.setSelected(false);
                        binding.rbUserGenderFemale.setSelected(true);
                        binding.ivCenterLine.setBackgroundColor(getResources().getColor(R.color.color_5f5b5d));
                        break;
                }

            }
        });

        binding.etUserFirstName.addTextChangedListener(new TextWatcher() {

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
        binding.etUserLastName.addTextChangedListener(new TextWatcher() {

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

        binding.btnUserBirth.addTextChangedListener(new TextWatcher() {

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

        binding.etUserHeightCm.addTextChangedListener(new TextWatcher() {

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
        binding.etUserHeightFt.addTextChangedListener(new TextWatcher() {

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
        binding.etUserHeightIn.addTextChangedListener(new TextWatcher() {

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

    public void receiveMessage(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;

        userBirth = setUserBirth(year, month, day);

        this.year = year;
        this.month = month;
        this.day = day;

        userBirth = setUserBirth(year, month, day);

        String strMeasureDt = userBirth.replaceAll("/", "");
        String strMeasureTime = ManagerUtil.getCurrentTime().replaceAll("/", "");

        String[] date =
                      ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(ProfileEnterActivity.this)),
                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                   true,
                                                   "/",
                                                   ":",
                                                   "yyyyMMddHHmmss",
                                                   strMeasureDt + strMeasureTime + "00");

        binding.btnUserBirth.setText(date[0]);
        binding.btnUserBirth.setTextColor(Color.BLACK);
    }

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

    @Override
    public void onClick(View v) {
        if (!ManagerUtil.isClicking()) {

            int id = v.getId();
            switch (id) {
                case R.id.ll_navi_back:
                    onBackPressed();
                    break;
                case R.id.btn_user_birth:
                    CustomDateDialog dateDialog = new CustomDateDialog(v.getContext(), year, (month - 1), day);
                    dateDialog.show();
                    break;
                case R.id.btn_complete:
                    if (BPDiaryApplication.isNetworkState(ProfileEnterActivity.this)) {
                        if (checkValidation()) {
                            // 회원가입 api 설정
                            CreateUserInfoAsync createUserInfoAsync = new CreateUserInfoAsync();
                            createUserInfoAsync.execute();
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
                case R.id.btn_skip:
                    if (BPDiaryApplication.isNetworkState(ProfileEnterActivity.this)) {

                        userFirstName = "Default";
                        userLastName = "Name";
                        userGender = ManagerConstants.Gender.MALE;
                        userHeight = "170";
                        heightUnit = ManagerConstants.Unit.CM;
                        userBirth = "19820101";
                        usingBloodGlucose = true;

                        CreateUserInfoAsync createUserInfoAsync = new CreateUserInfoAsync();
                        createUserInfoAsync.execute();
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
            }
        }
    }

    /**
     * 입력값 유효성 검사
     */
    private boolean checkValidation() {
        boolean isValidation = true;
        try {
            userBirth = setUserBirth(year, month, day);
            userFirstName = binding.etUserFirstName.getText().toString();
            userLastName = binding.etUserLastName.getText().toString();

            if (ManagerConstants.Unit.CM.equals(heightUnit)) {
                //cm면서
                userHeight = binding.etUserHeightCm.getText().toString();
                if (!"".equals(userHeight)) {
                    //공백이 아니고
                    if (Double.parseDouble(userHeight) < 40 || Double.parseDouble(userHeight) > 300) {
                        isValidation = false;
                        validationMessage = getResources().getString(R.string.profile_not_input_val_data);
                    } else {
                        //조건에 맞으면
                        if (userBirth == null || userBirth.isEmpty()) {
                            isValidation = false;
                            validationMessage = getResources().getString(R.string.profile_not_input_birth);
                        } else {
                            if ("".equals(userFirstName) || userFirstName.length() < 1
                                || "".equals(userLastName)
                                || userLastName.length() < 1) {

                                isValidation = false;
                                validationMessage = getResources().getString(R.string.profile_not_input_name);
                            }
                        }

                    }
                } else {
                    isValidation = false;
                    validationMessage = getResources().getString(R.string.profile_not_input_tall);
                }

            } else if (ManagerConstants.Unit.FT_IN.equals(heightUnit)) {
                if (!"".equals(binding.etUserHeightFt.getText().toString())) {
                    if (!"".equals(binding.etUserHeightIn.getText().toString())) {
                        userHeight = binding.etUserHeightFt.getText().toString() + "."
                                     + binding.etUserHeightIn.getText().toString();
                        userHeight = ManagerUtil.feetInchToInch(userHeight);
                        userHeight = ManagerUtil.inchToCm(userHeight);

                        if (Double.parseDouble(userHeight) < 40 || Double.parseDouble(userHeight) > 300) {
                            isValidation = false;
                            validationMessage = getResources().getString(R.string.profile_not_input_val_data);
                        } else {
                            //조건에 맞으면
                            if (userBirth == null || userBirth.isEmpty()) {
                                isValidation = false;
                                validationMessage = getResources().getString(R.string.profile_not_input_birth);
                            } else {
                                if ("".equals(userFirstName) || userFirstName.length() < 1
                                    || "".equals(userLastName)
                                    || userLastName.length() < 1) {

                                    isValidation = false;
                                    validationMessage = getResources().getString(R.string.profile_not_input_name);
                                }
                            }
                        }
                    } else {
                        isValidation = false;
                        validationMessage = getResources().getString(R.string.profile_not_input_tall);
                    }
                } else {
                    isValidation = false;
                    validationMessage = getResources().getString(R.string.profile_not_input_tall);
                }
            }

        } catch (Exception e) {
            isValidation = false;
            validationMessage = getResources().getString(R.string.profile_not_input_val_data);
            e.printStackTrace();
        }

        if (isValidation) {
            binding.btnComplete.setEnabled(true);
        } else {
            binding.btnComplete.setEnabled(false);
        }

        return isValidation;
    }

    /**
     * 유저 정보 입력
     */
    private class CreateUserInfoAsync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            mProgress.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {
                //유저정보 SharedPreferences 저장 세팅
                PreferenceUtil.setEncFirstName(getApplicationContext(), userFirstName);
                PreferenceUtil.setEncLastName(getApplicationContext(), userLastName);
                PreferenceUtil.setEncDayOfBirth(context, userBirth.replaceAll("/", ""));
                PreferenceUtil.setHeight(context, userHeight);
                PreferenceUtil.setHeightUnit(context, heightUnit);
                PreferenceUtil.setGender(context, userGender);
                PreferenceUtil.setUsingBloodGlucose(context, usingBloodGlucose);

                //유저정보 Server 전송
                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(context));
                data.put(ManagerConstants.RequestParamName.PASSWORD, PreferenceUtil.getEncPassword(context));
                data.put(ManagerConstants.RequestParamName.LOGIN_TYPE, PreferenceUtil.getLoginType(context));
                data.put(ManagerConstants.RequestParamName.DEVICE_COMPANY, Build.BRAND);
                data.put(ManagerConstants.RequestParamName.DEVICE_MODEL, Build.MODEL.replaceAll("", "_"));
                data.put(ManagerConstants.RequestParamName.MOBILE_OS, ManagerConstants.AppConfig.MOBILE_OS);
                data.put(ManagerConstants.RequestParamName.MOBILE_TOKEN, PreferenceUtil.getGcmToken(context));
                data.put(ManagerConstants.RequestParamName.FIRST_NAME, AesssUtil.encrypt(userFirstName));
                data.put(ManagerConstants.RequestParamName.LAST_NAME, AesssUtil.encrypt(userLastName));
                data.put(ManagerConstants.RequestParamName.GENDER, userGender);
                data.put(ManagerConstants.RequestParamName.DAY_OF_BIRTH, AesssUtil.encrypt(userBirth));
                data.put(ManagerConstants.RequestParamName.HEIGHT, userHeight);
                data.put(ManagerConstants.RequestParamName.HEIGHT_UNIT, heightUnit);
                if (usingBloodGlucose) {
                    data.put(ManagerConstants.RequestParamName.BG_YN,
                             ManagerConstants.UsingBloodGlucoseYN.USING_BLOODGLUCOSE_Y);
                } else {
                    data.put(ManagerConstants.RequestParamName.BG_YN,
                             ManagerConstants.UsingBloodGlucoseYN.USING_BLOODGLUCOSE_N);
                }
                Locale locale = getResources().getConfiguration().locale;
                data.put(ManagerConstants.RequestParamName.COUNTRY, locale.getCountry().toString());

                data.put(ManagerConstants.RequestParamName.OS_VERSION, Build.VERSION.RELEASE);
                data.put(ManagerConstants.RequestParamName.APP_VERSION_CREATE, PreferenceUtil.getVersion(context));
                data.put(ManagerConstants.RequestParamName.CARRIER,
                         PhoneUtil.getNetworkOperatorName(context).replaceAll("", "_"));

                //TODO Profile 이미지
                resultJSON = introService.createUserInfo(data);
                //                Map<String, String> fileMap = new HashMap<String, String>();
                //                resultJSON = introService.createUserInfo(requestJSON, fileMap);

            } catch (Exception e) {
                //LogUtil.e(ProfileTallActivity.this, e.getLocalizedMessage());
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {

            if (mProgress != null && mProgress.isShowing()) {
                mProgress.dismiss();
            }

            try {
                if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {
                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                     .toString())) {
                        Log.d("srpark", "로그인 성공");
                        ModGoalSync modGoalSync = new ModGoalSync();
                        modGoalSync.execute();
                    } else {
                        //TODO 알림 팝업
                        PreferenceUtil.removeAllPreferences(context);
                        if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_I.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                                                                                  .toString())) {
                            Log.d("srpark", "필수값없음");
                        } else if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_E.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                                                                                         .toString())) {
                            Log.d("srpark", "서버에러");
                        } else if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_J.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                                                                                         .toString())) {
                            //fail이면 알맞은 다이얼로그
                            if (resultJSON.has(ManagerConstants.ResponseParamName.LOGIN_TYPE)) {
                                if (ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE.equals(resultJSON.get(ManagerConstants.ResponseParamName.LOGIN_TYPE)
                                                                                                  .toString())) {
                                    DefaultOneButtonDialog defaultOneButtonDialog =
                                                                                  new DefaultOneButtonDialog(context,
                                                                                                             "",
                                                                                                             getString(R.string.fail_reason_content_google),
                                                                                                             getString(R.string.common_txt_confirm),
                                                                                                             new IDefaultOneButtonDialog() {

                                                                                                                 @Override
                                                                                                                 public void
                                                                                                                        onConfirm() {
                                                                                                                 }
                                                                                                             });
                                    defaultOneButtonDialog.show();
                                } else if (ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK.equals(resultJSON.get(ManagerConstants.ResponseParamName.LOGIN_TYPE)
                                                                                                           .toString())) {
                                    DefaultOneButtonDialog defaultOneButtonDialog =
                                                                                  new DefaultOneButtonDialog(context,
                                                                                                             "",
                                                                                                             getString(R.string.fail_reason_content_facebook),
                                                                                                             getString(R.string.common_txt_confirm),
                                                                                                             new IDefaultOneButtonDialog() {

                                                                                                                 @Override
                                                                                                                 public void
                                                                                                                        onConfirm() {
                                                                                                                 }
                                                                                                             });
                                    defaultOneButtonDialog.show();
                                } else if (ManagerConstants.LoginType.LOGIN_TYPE_EMAIL.equals(resultJSON.get(ManagerConstants.ResponseParamName.LOGIN_TYPE)
                                                                                                        .toString())) {
                                    DefaultOneButtonDialog defaultOneButtonDialog =
                                                                                  new DefaultOneButtonDialog(context,
                                                                                                             "",
                                                                                                             getString(R.string.fail_reason_content_email),
                                                                                                             getString(R.string.common_txt_confirm),
                                                                                                             new IDefaultOneButtonDialog() {

                                                                                                                 @Override
                                                                                                                 public void
                                                                                                                        onConfirm() {
                                                                                                                 }
                                                                                                             });
                                    defaultOneButtonDialog.show();
                                } else {
                                    DefaultOneButtonDialog defaultOneButtonDialog =
                                                                                  new DefaultOneButtonDialog(context,
                                                                                                             "",
                                                                                                             getString(R.string.fail_reason_content_exist_join),
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
                                //가입된 계정
                                DefaultOneButtonDialog defaultOneButtonDialog =
                                                                              new DefaultOneButtonDialog(context,
                                                                                                         "",
                                                                                                         getString(R.string.fail_reason_content_exist_join),
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
                } else {
                    //TODO 알림 팝업
                    PreferenceUtil.removeAllPreferences(context);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 혈압 목표 전송 Sync
     */
    private class ModGoalSync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            //            if (mProgress == null) {
            //                mProgress = new CustomDialogProgress(BloodPressureMemoActivity.this);
            //                mProgress.setCancelable(false);
            //            }
            //            mProgress.show();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {
                //Server 전송
                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(ProfileEnterActivity.this));
                data.put(ManagerConstants.RequestParamName.BP_SYS_MIN, "90");
                data.put(ManagerConstants.RequestParamName.BP_SYS_MAX, "119");
                data.put(ManagerConstants.RequestParamName.BP_DIA_MIN, "60");
                data.put(ManagerConstants.RequestParamName.BP_DIA_MAX, "79");
                data.put(ManagerConstants.RequestParamName.WS_WEIGHT, "60");
                data.put(ManagerConstants.ResponseParamName.BG_MEAL_B_MIN, "81");
                data.put(ManagerConstants.ResponseParamName.BG_MEAL_B_MAX, "120");
                data.put(ManagerConstants.ResponseParamName.BG_MEAL_A_MIN, "121");
                data.put(ManagerConstants.ResponseParamName.BG_MEAL_A_MAX, "160");

                resultJSON = bpService.modifyBPGoal(data);

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {

            //            if (mProgress != null && mProgress.isShowing()) {
            //                mProgress.hide();
            //            }

            try {

                if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                    if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                     .toString())) {
                        PreferenceUtil.setBPMinDiastole(context, 60);
                        PreferenceUtil.setBPMaxDiastole(context, 79);
                        PreferenceUtil.setBPMinSystole(context, 90);
                        PreferenceUtil.setBPMaxSystole(context, 119);
                        PreferenceUtil.setWeightGoal(context, "60");
                        PreferenceUtil.setGlucoseMinBeforeMeal(context, 81);
                        PreferenceUtil.setGlucoseMaxBeforeMeal(context, 120);
                        PreferenceUtil.setGlucoseMinAfterMeal(context, 121);
                        PreferenceUtil.setGlucoseMaxAfterMeal(context, 160);

                        if (loginType.equals(ManagerConstants.LoginType.LOGIN_TYPE_GOOGLE)) {
                            Intent intent = new Intent(context, LoginLogicActivity.class);
                            startActivityForResult(intent,
                                                   CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_GOOGLE);
                        } else if (loginType.equals(ManagerConstants.LoginType.LOGIN_TYPE_FACEBOOK)) {
                            Intent intent = new Intent(context, LoginLogicActivity.class);
                            startActivityForResult(intent,
                                                   CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_FACEBOOK);
                        } else {
                            Intent intent = new Intent(context, LoginLogicActivity.class);
                            startActivityForResult(intent,
                                                   CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_EMAIL);
                        }
                    } else {
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_GOOGLE
            || requestCode == CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_FACEBOOK
            || requestCode == CommonConstants.RequestCode.ACTIVITY_REQUEST_CODE_LOGIN_TYPE_EMAIL) {

            //Event 데이터가 있으면 모델을 메인으로 던짐
            if (resultCode == RESULT_OK) {
                Log.d("srpark", "메인으로");
                for (int i = 0; i < actList.size(); i++) {
                    actList.get(i).finish();
                }
                Intent intent = new Intent(context, MainActivity.class);
                if (data != null) {
                    EventInfoData eventInfoData = (EventInfoData)data.getSerializableExtra(CommonConstants.EVENT_DATA);
                    intent.putExtra(CommonConstants.EVENT_DATA, eventInfoData);
                }
                startActivity(intent);
                finish();
                overridePendingTransition(0, 0);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("srpark", "프로필에서 로그인 실패");
                onBackPressed();
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
