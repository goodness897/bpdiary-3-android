package kr.co.openit.bpdiary.activity.glucose;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.activity.weight.DirectInputWeightActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class DirectInputGlucoseActivity extends BaseActivity {

    /**
     * 혈당 직접 입력 EditText
     */
    private EditText etGlucose;

    /**
     * 입력 Button
     */
    private Button btnGlucose;

    /**
     * 우상단 취소 버튼
     */
    private ImageView ivCancel;

    private String glucoseUnit;

    private final static int DEFAULT_MAX_MGDL_VALUE = 999;

    private final static int DEFAULT_MAX_MMOL_VALUE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_input_glucose);

        setLayout();

        etGlucose.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        glucoseUnit = getIntent().getStringExtra("glucoseUnit");

        // glucoseUnit 에 따른 소수점 허용 / 비허용 여부 판단
        if (glucoseUnit.equals(ManagerConstants.Unit.MGDL)) {
            etGlucose.setInputType(InputType.TYPE_CLASS_NUMBER);
        } else {
            etGlucose.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL
                                   | InputType.TYPE_NUMBER_FLAG_SIGNED);
        }

        btnGlucose.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    String glucose = etGlucose.getText().toString();
                    if (!glucose.equals(".") && glucose.charAt(0) != '.' && !glucose.endsWith(".")) {
                        if (glucoseUnit.equals(ManagerConstants.Unit.MGDL)) {
                            if (Integer.parseInt(glucose) > DEFAULT_MAX_MGDL_VALUE) {
                                glucose = "999";
                                etGlucose.setText(glucose);
                            }

                        } else {
                            if (Double.parseDouble(glucose) > DEFAULT_MAX_MMOL_VALUE) {
                                glucose = "300";
                                etGlucose.setText(glucose);
                            }
                        }
                        if (!TextUtils.isEmpty(glucose)) {
                            Log.d(TAG, "input glucose : " + glucose);
                            Intent intent = new Intent();
                            intent.putExtra(GLUCOSE, glucose);
                            setResult(RESULT_OK, intent);
                            finish();
                        }
                    } else {
                        Toast.makeText(DirectInputGlucoseActivity.this,
                                       R.string.profile_not_input_val_data,
                                       Toast.LENGTH_SHORT)
                             .show();
                    }
                }
            }
        });

        etGlucose.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    btnGlucose.setEnabled(false);
                } else {
                    btnGlucose.setEnabled(true);
                    btnGlucose.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ivCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {
                    finish();
                }
            }
        });
    }

    private void setLayout() {

        etGlucose = (EditText)findViewById(R.id.et_glucose_input);
        btnGlucose = (Button)findViewById(R.id.btn_glucose);
        btnGlucose.setEnabled(false);
        ivCancel = (ImageView)findViewById(R.id.iv_cancel);

    }
}
