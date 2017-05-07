package kr.co.openit.bpdiary.activity.setting;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class ProfileTallActivity extends NonMeasureActivity {

    private LinearLayout llEmptyView;

    /**
     * 단위 cm 일때 보여주는 Layout
     */
    private LinearLayout llCMLayout;

    /**
     * 단위 Inch 일때 보여주는 Layout
     */
    private LinearLayout llInchLayout;

    /**
     * 저장 버튼
     */
    private Button btnSave;

    /**
     * 단위 cm 일때 입력 EditText
     */
    private EditText etCmView;

    /**
     * 단위 ft.in 일때 Feet 입력 EditText
     */

    private EditText etFeetView;

    /**
     * 단위 ft.in 일때 Inch 입력 EditText
     */
    private EditText etInchView;

    /**
     * 단위 변경 Spinner
     */

    private Spinner spHeightUnit;

    private String height;

    private String heightUnit;

    private String strFeetInch;

    private ProfileService profileService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_tall);

        initToolbar(getString(R.string.setting_activity_height));

        AnalyticsUtil.sendScene(ProfileTallActivity.this, "3_M 프로필 키");

        setLayout();

        profileService = new ProfileService(ProfileTallActivity.this);

        heightUnit = PreferenceUtil.getHeightUnit(ProfileTallActivity.this);
        // Height 단위에 따른 Spinner 셋팅
        if (getString(R.string.tall_cm).equals(heightUnit)) {
            spHeightUnit.setSelection(0);
        } else {
            spHeightUnit.setSelection(1);
        }

        height = PreferenceUtil.getHeight(ProfileTallActivity.this);
        Log.d(TAG, "height : " + PreferenceUtil.getHeight(ProfileTallActivity.this));

        //height 값 세팅
        if (!TextUtils.isEmpty(height)) {
            etCmView.setText(height);
            //            strFeetInch = ManagerUtil.inchToFeetInch(ManagerUtil.cmToInch(height));
            //
            //            String[] strArrayFeetInch = strFeetInch.split("\\.");
            //            etFeetView.setText(strArrayFeetInch[0]);
            //            etInchView.setText(strArrayFeetInch[1]);
        }

        spHeightUnit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //cm 일때
                if (position == 0) {
                    // Preference 단위 저장
                    heightUnit = ManagerConstants.Unit.CM;
                    String strFeet = etFeetView.getText().toString();
                    String strInch = etInchView.getText().toString();
                    StringBuilder sb = new StringBuilder();
                    if (!TextUtils.isEmpty(strFeet) && !TextUtils.isEmpty(strInch)) {

                        String strInchToCm = ManagerUtil.inchToCm(ManagerUtil.feetInchToInch(sb.append(strFeet)
                                .append(".")
                                .append(strInch)
                                .toString()));
                        etCmView.setText(strInchToCm);
                    }
                    llCMLayout.setVisibility(View.VISIBLE);
                    llInchLayout.setVisibility(View.GONE);
                    //ft.in 일때
                } else if (position == 1) {
                    // Preference 단위 저장
                    heightUnit = ManagerConstants.Unit.FT_IN;
                    String strCm = etCmView.getText().toString();
                    if (!TextUtils.isEmpty(strCm)) {
                        String strFeetInch = ManagerUtil.inchToFeetInch(ManagerUtil.cmToInch(strCm));
                        String[] strArrayFeetInch = strFeetInch.split("\\.");
                        etFeetView.setText(strArrayFeetInch[0]);
                        etInchView.setText(strArrayFeetInch[1]);

                    }

                    llCMLayout.setVisibility(View.GONE);
                    llInchLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // 저장 버튼 클릭
        btnSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    if (BPDiaryApplication.isNetworkState(ProfileTallActivity.this)) {
                        if (getString(R.string.tall_cm).equals(heightUnit)) {
                            height = etCmView.getText().toString();
                        } else {
                            StringBuilder sb = new StringBuilder();
                            height = ManagerUtil.inchToCm(ManagerUtil.feetInchToInch(sb.append(etFeetView.getText().toString())
                                    .append(".")
                                    .append(etInchView.getText().toString())
                                    .toString()));
                        }
                        ModifyUserInfoAsync modSync = new ModifyUserInfoAsync();
                        modSync.execute();
                    } else {
                        //다이얼로그 호출 후 앱 종료
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(ProfileTallActivity.this,
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
     * 레이아웃 세팅
     */

    private void setLayout() {

        btnSave = (Button) findViewById(R.id.btn_save);
        llEmptyView = (LinearLayout) findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        llCMLayout = (LinearLayout) findViewById(R.id.ll_cm);
        llInchLayout = (LinearLayout) findViewById(R.id.ll_inch);
        etCmView = (EditText) findViewById(R.id.et_cm);
        etCmView.addTextChangedListener(cmTextWatcher);
        etFeetView = (EditText) findViewById(R.id.et_feet);
        etFeetView.addTextChangedListener(feetTextWatcher);
        etInchView = (EditText) findViewById(R.id.et_inch);
        etInchView.addTextChangedListener(inchTextWatcher);
        spHeightUnit = (Spinner) findViewById(R.id.spinner);

        etCmView.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(4, 1)});

    }

    private TextWatcher cmTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (str.length() > 0 && etCmView.getText().toString().length() > 0) {

                btnSave.setEnabled(true);

            } else {

                btnSave.setEnabled(false);
            }

        }
    };

    private TextWatcher feetTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (str.length() > 0 && etInchView.getText().toString().length() > 0) {

                btnSave.setEnabled(true);
            } else {

                btnSave.setEnabled(false);
            }

        }
    };

    private TextWatcher inchTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String str = s.toString();
            if (str.length() > 0 && etFeetView.getText().toString().length() > 0) {

                btnSave.setEnabled(true);
            } else {

                btnSave.setEnabled(false);
            }

        }
    };

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1)
                    + "}+((\\.[0-9]{0,"
                    + (digitsAfterZero - 1)
                    + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
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
                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(ProfileTallActivity.this));
                data.put(ManagerConstants.RequestParamName.HEIGHT, height);
                data.put(ManagerConstants.RequestParamName.HEIGHT_UNIT, heightUnit);

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

                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT).toString())) {
                            PreferenceUtil.setHeight(ProfileTallActivity.this, height);
                            if(ManagerConstants.Unit.CM.equals(heightUnit)) {
                                PreferenceUtil.setHeightUnit(ProfileTallActivity.this, ManagerConstants.Unit.CM);
                            } else {
                                PreferenceUtil.setHeightUnit(ProfileTallActivity.this, ManagerConstants.Unit.FT_IN);
                            }
                            Toast.makeText(ProfileTallActivity.this,
                                    getResources().getString(R.string.profile_mod_finish),
                                    Toast.LENGTH_SHORT).show();

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
