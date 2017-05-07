package kr.co.openit.bpdiary.activity.bp;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;

public class DirectInputBPActivity extends NonMeasureActivity {

    private EditText etBP;

    private Button btnBP;

    private ImageView ivCancel;

    private TextView tvTitle;

    private final static int DEFULAT_MAX_VALUE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_input_bp);

        setLayout();

        Intent intent = getIntent();
        int type = intent.getIntExtra(InputBPActivity.BP_TYPE, 0);

        setData(type);

    }

    private void setLayout() {

        etBP = (EditText) findViewById(R.id.et_bp_input);

        tvTitle = (TextView) findViewById(R.id.tv_bp_title);

        btnBP = (Button) findViewById(R.id.btn_bp);

        btnBP.setEnabled(false);

        ivCancel = (ImageView) findViewById(R.id.iv_cancel);

        etBP.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        btnBP.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {

                    String bp = etBP.getText().toString();
                    if (Integer.parseInt(bp) > DEFULAT_MAX_VALUE) {
                        bp = "300";
                        etBP.setText(bp);
                    }

                    if (!TextUtils.isEmpty(bp)) {
                        Log.d(TAG, "input bp: " + bp);
                        Intent intent = new Intent();
                        intent.putExtra("bp", bp);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(DirectInputBPActivity.this, "값을 입력하세요", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        etBP.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    btnBP.setEnabled(false);
                } else {
                    btnBP.setEnabled(true);
                    btnBP.setClickable(true);
                }
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

    /**
     * 수축기, 이완기, 맥박에 따른 화면 변화
     *
     * @param type
     */

    private void setData(int type) {
        switch (type) {
            case InputBPActivity.BP_SYSTOLE:
                AnalyticsUtil.sendScene(DirectInputBPActivity.this, "3_혈압 수축기 직접입력");

                tvTitle.setText(R.string.bp_sys_direct_input);
                etBP.setHint(R.string.bp_sys_request_input_value);
                break;
            case InputBPActivity.BP_DIASTOLE:
                AnalyticsUtil.sendScene(DirectInputBPActivity.this, "3_혈압 이완기 직접입력");

                tvTitle.setText(R.string.bp_dia_direct_input);
                etBP.setHint(R.string.bp_dia_request_input_value);
                break;
            case InputBPActivity.BP_PULSE:
                AnalyticsUtil.sendScene(DirectInputBPActivity.this, "3_혈압 맥박 직접입력");

                tvTitle.setText(R.string.bp_pulse_direct_input);
                etBP.setHint(R.string.bp_pulse_request_input_value);
                break;
        }
    }
}
