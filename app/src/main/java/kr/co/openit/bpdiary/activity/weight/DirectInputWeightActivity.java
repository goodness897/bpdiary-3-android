package kr.co.openit.bpdiary.activity.weight;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * Created by Hwang on 2017-01-02.
 */

public class DirectInputWeightActivity extends BaseActivity {

    private static final int DEFULAT_MAX_KG_VALUE = 250;

    private static final int DEFULAT_MAX_LBS_VALUE = 551;

    private EditText etWeight;

    private Button btnWeight;

    private LinearLayout llCancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_input_weight);

        AnalyticsUtil.sendScene(DirectInputWeightActivity.this, "3_체중 직접 입력");

        etWeight = (EditText)findViewById(R.id.et_weight_input);

        etWeight.requestFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        btnWeight = (Button)findViewById(R.id.btn_weight);
        btnWeight.setEnabled(false);

        btnWeight = (Button)findViewById(R.id.btn_weight);

        btnWeight.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    String weight = etWeight.getText().toString();
                    if (!weight.equals(".") && weight.charAt(0) != '.' && !weight.endsWith(".")) {
                        if (getIntent().getStringExtra("weightUnit").equals("kg")) {
                            if (Double.parseDouble(weight) > DEFULAT_MAX_KG_VALUE) {
                                weight = "250";
                                etWeight.setText(weight);
                            }
                        } else {
                            if (Double.parseDouble(weight) > DEFULAT_MAX_LBS_VALUE) {
                                weight = "551";
                                etWeight.setText(weight);
                            }
                        }
                        if (!TextUtils.isEmpty(weight)) {
                            Log.d(TAG, "input weight : " + weight);
                            Intent intent = new Intent();
                            intent.putExtra("weight", weight);
                            setResult(RESULT_OK, intent);
                            finish();
                        }

                    } else {
                        Toast.makeText(DirectInputWeightActivity.this,
                                       R.string.profile_not_input_val_data,
                                       Toast.LENGTH_SHORT)
                             .show();
                    }
                }
            }
        });

        etWeight.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0) {
                    btnWeight.setEnabled(false);
                } else {
                    btnWeight.setEnabled(true);
                    btnWeight.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        llCancelBtn = (LinearLayout)findViewById(R.id.ll_cancel_btn);
        llCancelBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    finish();
                }
            }
        });
    }
}
