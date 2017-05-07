package kr.co.openit.bpdiary.activity.report;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.activity.intro.PasswordSearchActivity;
import kr.co.openit.bpdiary.activity.intro.SendSuccessActivity;
import kr.co.openit.bpdiary.activity.intro.SignUpActivity;
import kr.co.openit.bpdiary.activity.main.MainActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.services.ReportService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.DateUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Created by srpark on 2017-02-06.
 */

public class ReportShareActivity extends NonMeasureActivity {

    private static final int RESULT_FIND_PASSWORD_SUCCESS = 1000;

    private EditText etEmail;

    private Button btnSend;

    private TextView tvTitle;

    private ImageView ivTitle;

    private TextView tvContent;

    private LinearLayout llBack;

    private boolean isEnable = false;

    private String searchDay;

    /**
     * ReportService
     */
    private ReportService reportService;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_password_or_shard_report);

        AnalyticsUtil.sendScene(ReportShareActivity.this, "3_레포트 PDF공유");

        context = ReportShareActivity.this;
        reportService = new ReportService(context);

        searchDay = getIntent().getStringExtra("searchDay");

        tvTitle = (TextView)findViewById(R.id.tv_title);
        ivTitle = (ImageView)findViewById(R.id.iv_title);
        tvContent = (TextView)findViewById(R.id.tv_content);
        etEmail = (EditText)findViewById(R.id.et_email);
        btnSend = (Button)findViewById(R.id.btn_send);
        llBack = (LinearLayout)findViewById(R.id.ll_back);

        tvTitle.setText(R.string.report_share);
        ivTitle.setBackgroundResource(R.drawable.ic_start_screen_share);
        tvContent.setText(R.string.report_share_content);
        btnSend.setText(R.string.report_share_send);
        btnSend.setEnabled(false);

        etEmail.setText(PreferenceUtil.getDecEmail(ReportShareActivity.this));
        btnSend.setEnabled(true);
        isEnable = true;

        llBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    finish();
                }
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    if (isEnable) {
                        if (BPDiaryApplication.isNetworkState(ReportShareActivity.this)) {
                            new SendReport().execute();
                        } else {
                            //다이얼로그 호출 후 앱 종료
                            DefaultOneButtonDialog defaultOneButtonDialog =
                                    new DefaultOneButtonDialog(ReportShareActivity.this,
                                            "",
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
                }
            }
        });

        TextWatcher firstNameWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (ManagerUtil.isEmail(etEmail.getText().toString())) {
                        btnSend.setEnabled(true);
                        isEnable = true;
                    } else {
                        btnSend.setEnabled(false);
                        isEnable = false;
                    }
                } else {
                    btnSend.setEnabled(false);
                    isEnable = false;
                }
            }
        };
        etEmail.addTextChangedListener(firstNameWatcher);
    }

    /**
     * 레포트 보내기
     */
    private class SendReport extends AsyncTask<Void, Void, JSONObject> {

        String strEmail = "";

        @Override
        protected void onPreExecute() {
            showLodingProgress();

            strEmail = etEmail.getText().toString();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();
            String strGlugoseUnit = "";
            String strWeightnit = "";
            String strHeightnit = "";
            if (ManagerConstants.Unit.MMOL.equals(PreferenceUtil.getGlucoseUnit(context))) {
                strGlugoseUnit = "mmol";
            } else {
                strGlugoseUnit = ManagerConstants.Unit.MGDL;
            }
            if (ManagerConstants.Unit.LBS.equals(PreferenceUtil.getWeightUnit(context))) {
                strWeightnit = "lb";
            } else {
                strWeightnit = ManagerConstants.Unit.KG;
            }
            if (ManagerConstants.Unit.FT_IN.equals(PreferenceUtil.getWeightUnit(context))) {
                strHeightnit = ManagerConstants.Unit.FT_IN;
            } else {
                strHeightnit = ManagerConstants.Unit.CM;
            }

            try {
                data.put(ManagerConstants.RequestParamName.EMAIL, strEmail);
                data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(context));
                data.put(ManagerConstants.RequestParamName.PERIOD, searchDay);
                data.put(ManagerConstants.RequestParamName.END_DT, DateUtil.getDateNow("yyyyMMdd"));
                data.put(ManagerConstants.RequestParamName.DAYS_YN, "N");
                data.put(ManagerConstants.RequestParamName.GRADE_YN, "N");
                data.put(ManagerConstants.RequestParamName.DIST_YN, "N");
                if (PreferenceUtil.getUsingBloodGlucose(context)) {
                    data.put(ManagerConstants.RequestParamName.BG_YN,
                            ManagerConstants.UsingBloodGlucoseYN.USING_BLOODGLUCOSE_Y);
                } else {
                    data.put(ManagerConstants.RequestParamName.BG_YN,
                            ManagerConstants.UsingBloodGlucoseYN.USING_BLOODGLUCOSE_N);
                }
                data.put(ManagerConstants.RequestParamName.HEIGHT_UNIT, strHeightnit);
                data.put(ManagerConstants.RequestParamName.WEIGHT_UNIT, strWeightnit);
                data.put(ManagerConstants.RequestParamName.GLUCOSE_UNIT, strGlugoseUnit);

                //TODO Profile 이미지
                resultJSON = reportService.shareReport(data);

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
                            Intent intent = new Intent(ReportShareActivity.this, ReportSendSuccessActivity.class);
                            intent.putExtra(ManagerConstants.SharedPreferencesName.SHARED_EMAIL, strEmail);
                            startActivityForResult(intent, RESULT_FIND_PASSWORD_SUCCESS);

                        } else {
                            if (ManagerConstants.ResponseResult.RESULT_FAIL_REASON_I.equals(resultJSON.get(ManagerConstants.ResponseParamName.REASON)
                                    .toString())) {
                                DefaultOneButtonDialog defaultOneButtonDialog =
                                        new DefaultOneButtonDialog(context,
                                                getString(R.string.report_share_send_fail),
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
                                        new DefaultOneButtonDialog(ReportShareActivity.this,
                                                "",
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
                    } else {
                        //TODO 에러 팝업
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(ReportShareActivity.this,
                                        "",
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FIND_PASSWORD_SUCCESS) {
            finish();
        }
    }
}
