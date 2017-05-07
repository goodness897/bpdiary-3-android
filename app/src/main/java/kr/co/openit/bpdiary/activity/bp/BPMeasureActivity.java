package kr.co.openit.bpdiary.activity.bp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.MeasureActivity;
import kr.co.openit.bpdiary.common.constants.HealthcareConstants.BloodPressureState;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.DataBase;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.RequestParamName;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.ResponseParamName;
import kr.co.openit.bpdiary.common.constants.ManagerConstants.ResponseResult;
import kr.co.openit.bpdiary.common.constants.Nomenclature;
import kr.co.openit.bpdiary.common.vo.ResultData;
import kr.co.openit.bpdiary.dialog.bp.MeasureCustomDialog;
import kr.co.openit.bpdiary.interfaces.bp.MeasureInterface;
import kr.co.openit.bpdiary.services.BloodPressureService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.HealthcareUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.ParcelableMap;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * 혈압계 연동 측정 Activity
 */
public class BPMeasureActivity extends MeasureActivity {

    private final int GFIT_SHEALTH_RESULT_CODE = 9999;

    /**
     * 데이터 DB 저장용 Map
     */
    private Map<String, String> requestMap;

    private LinearLayout llEmptyView;

    /**
     * 안내 문구(상단)
     */
    private TextView txtGuideTop;

    /**
     * 안내 문구
     */
    private TextView txtNoti;

    /**
     * 측정중 화면 표시 문구
     */
    private TextView txtMeasuring;

    /**
     * 프로그래스바
     */
    private ProgressBar pbMeasuring;

    /**
     * 측정안내 버튼
     */
    private Button btnGuide;

    /**
     * 취소 버튼
     */
    private Button btnCancel;

    private ImageView ivBpGraph;

    /**
     * BloodPressureService
     */
    private BloodPressureService bpService;

    /**
     * 혈압 측정 Data를 DB에 저장 및 Server로 전송하는 AsyncTask
     */
    private CreateBloodPressureAsync cbpAsync;

    /**
     * 측정 가이드 Dialog
     */
    private MeasureCustomDialog measureCustomDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_bp_measure);

        AnalyticsUtil.sendScene(BPMeasureActivity.this, "3_혈압 자동측정");

        initToolbar(getString(R.string.bp_auto_measure));

        //        AnalyticsUtil.sendScene(BPMeasureActivity.this, "BloodPressureMeasureActivity");

        /**
         * 화면 초기 설정
         */
        setActivityLayout();

        /**
         * blood pressure service
         */
        bpService = new BloodPressureService(BPMeasureActivity.this);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == GFIT_SHEALTH_RESULT_CODE) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    protected void measureResult(Bundle measureBundle) {

        if (measureBundle.getBoolean("isSuccess")) {
            // 프로파일
            int nHealthProfile = measureBundle.getInt("healthProfile");

            // 측정 데이터 자료
            Bundle measureResult = measureBundle.getBundle("measureResult");

            // 측정 결과
            ArrayList<ParcelableMap> rList = measureResult.getParcelableArrayList("data");

            if (rList.size() > 0) {

                // 측정 결과 데이터 Map 을 가져옴
                Map<String, ResultData> map = rList.get(rList.size() - 1).getResultDataMap();

                // 기기에서 준 ResultData 값을 가져옴
                ResultData rdSysData =
                                     map.get(ManagerUtil.convertDataName(Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV_SYS));
                ResultData rdDiaData =
                                     map.get(ManagerUtil.convertDataName(Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV_DIA));
                ResultData rdMeanData =
                                      map.get(ManagerUtil.convertDataName(Nomenclature.DataAcqu.MDC_PRESS_BLD_NONINV_MEAN));
                ResultData rdPulseData =
                                       map.get(ManagerUtil.convertDataName(Nomenclature.DataAcqu.MDC_PULS_RATE_NON_INV));

                double dSys = 0;
                double dDia = 0;
                double dMean = 0;
                double dPulse = 0;

                if (rdSysData != null) {
                    dSys = Double.parseDouble(rdSysData.getValue());
                }

                if (rdDiaData != null) {
                    dDia = Double.parseDouble(rdDiaData.getValue());
                }

                if (rdMeanData != null) {
                    dMean = Double.parseDouble(rdMeanData.getValue());
                }

                if (rdPulseData != null) {
                    dPulse = Double.parseDouble(rdPulseData.getValue());
                }

                //DB에 넣을 Data 작성
                requestMap = new HashMap<String, String>();
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS, String.valueOf(dSys));
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA, String.valueOf(dDia));
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN, String.valueOf(dMean));
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE, String.valueOf(dPulse));
                //                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS,
                //                               rdSysData == null ? "0" : rdSysData.getValue());
                //                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA,
                //                               rdDiaData == null ? "0" : rdDiaData.getValue());
                //                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN,
                //                               rdMeanData == null ? "0" : rdMeanData.getValue());
                //                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE,
                //                               rdPulseData == null ? "0" : rdPulseData.getValue());

                String strType = BloodPressureState.BP_NORMAL;

                if (rdSysData != null && rdDiaData != null) {
                    strType = HealthcareUtil.getBloodPressureType(BPMeasureActivity.this, rdSysData.getValue(), rdDiaData.getValue());
                }

                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE, strType);
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_ID, measureBundle.getString("deviceId"));
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MODEL,
                               measureBundle.getString("deviceModel"));
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_DEVICE_MANUFACTURER,
                               measureBundle.getString("deviceCompany"));
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE, "");
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT, ManagerUtil.getCurrentDateTime());
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_INS_DT, ManagerUtil.getCurrentDateTime());
                requestMap.put(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN, "N");

                //측정 Data 화면 표시
                setDisplayData();

                // DB에 저장 및 Server로 전송 AsyncTask 실행
                cbpAsync = new CreateBloodPressureAsync();
                cbpAsync.execute();

            }

        } else {
            Log.e(TAG, "measure error : " + measureBundle.getString("rtnMsg"));
        }
    }

    @Override
    protected void startMeasuringAnimation() {
        super.startMeasuringAnimation();

        //        measureCustomDialog.dismiss();

        txtGuideTop.setText(getResources().getText(R.string.bp_measure_txt_guide_measuring_top));
        pbMeasuring.setVisibility(View.VISIBLE);
        ivBpGraph.setVisibility(View.VISIBLE);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)txtMeasuring.getLayoutParams();
        params.setMargins(0, 0, 0, (int)getResources().getDimension(R.dimen.measure_txt_margin_on));
        txtMeasuring.setLayoutParams(params);
        txtMeasuring.setText(getResources().getText(R.string.bp_measure_txt_display_measuring));

    }

    @Override
    protected void stopMeasuringAnimation() {
        super.stopMeasuringAnimation();

    }

    @Override
    protected void startSendingAnimation() {
        super.startSendingAnimation();

    }

    /**
     * 화면 초기 설정 메서드
     */
    private void setActivityLayout() {
        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        txtGuideTop = (TextView)findViewById(R.id.bp_measure_txt_guide_top);
        txtNoti = (TextView)findViewById(R.id.bp_measure_txt_guide_noti);

        txtMeasuring = (TextView)findViewById(R.id.bp_measure_txt_measuring);

        pbMeasuring = (ProgressBar)findViewById(R.id.bp_measure_progressbar_measuring);

        ivBpGraph = (ImageView)findViewById(R.id.iv_bp_graph);

        btnGuide = (Button)findViewById(R.id.bp_measure_btn_guide);
        btnGuide.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    /**
                     * 측정 가이드 Dialog
                     */
                    measureCustomDialog = new MeasureCustomDialog(BPMeasureActivity.this, new MeasureInterface() {
                    });
                    measureCustomDialog.show();
                }
            }
        });

        btnCancel = (Button)findViewById(R.id.bp_measure_btn_cancel);
        btnCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!ManagerUtil.isClicking()) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            }
        });
    }

    /**
     * 화면 표시 메서드
     */
    public void setDisplayData() {

        //측정 Data 화면 표시
        try {
            txtGuideTop.setText(getResources().getText(R.string.bp_measure_txt_guide_end));
            pbMeasuring.setVisibility(View.GONE);
            ivBpGraph.setVisibility(View.GONE);
            txtMeasuring.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 혈압 측정 등록 Async
     */
    private class CreateBloodPressureAsync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {

            try {
                showLodingProgress();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<>();
            JSONObject resultJSON = new JSONObject();

            try {

                String strDbSeq = null;

                data.put(RequestParamName.UUID, PreferenceUtil.getEncEmail(BPMeasureActivity.this));

                //DB 저장
                int nRow = bpService.createBloodPressureData(requestMap);

                if (nRow > 0) {

                    strDbSeq = String.valueOf(nRow);

                    //Server 전송
                    data.put(RequestParamName.INS_DT, requestMap.get(DataBase.COLUMN_NAME_INS_DT));
                    data.put(RequestParamName.BP_SYS, requestMap.get(DataBase.COLUMN_NAME_BP_SYS));
                    data.put(RequestParamName.BP_DIA, requestMap.get(DataBase.COLUMN_NAME_BP_DIA));
                    data.put(RequestParamName.BP_MEAN, requestMap.get(DataBase.COLUMN_NAME_BP_MEAN));
                    data.put(RequestParamName.BP_PULSE, requestMap.get(DataBase.COLUMN_NAME_BP_PULSE));
                    data.put(RequestParamName.BP_TYPE, requestMap.get(DataBase.COLUMN_NAME_BP_TYPE));
                    data.put(RequestParamName.SENSOR_COMPANY, requestMap.get(DataBase.COLUMN_NAME_DEVICE_MANUFACTURER));
                    data.put(RequestParamName.SENSOR_MODEL, requestMap.get(DataBase.COLUMN_NAME_DEVICE_MODEL));
                    data.put(RequestParamName.SENSOR_SN, requestMap.get(DataBase.COLUMN_NAME_DEVICE_ID));
                    data.put(RequestParamName.MESSAGE, requestMap.get(DataBase.COLUMN_NAME_MESSAGE));
                    data.put(RequestParamName.RECORD_DT,
                             ManagerUtil.convertDateFormatToServer(requestMap.get(DataBase.COLUMN_NAME_MEASURE_DT)));

                    resultJSON = bpService.sendBloodPressureData(data);

                    if (resultJSON.has(ResponseParamName.RESULT)) {

                        if (ResponseResult.RESULT_TRUE.equals(resultJSON.get(ResponseParamName.RESULT).toString())) {
                            //Server 전송 완료

                            //DB에 Server 전송 Update
                            bpService.updateSendToServerYN(strDbSeq);

                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultJSON;
        }

        @Override
        protected void onPostExecute(JSONObject resultJSON) {

            try {
                hideLodingProgress();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (PreferenceUtil.getGoogleFit(BPMeasureActivity.this)) {
                    //구글 피트니스
                    Intent intent = new Intent(BPMeasureActivity.this, BPGFitSHealthActivity.class);
                    intent.putExtra("sys", requestMap.get(DataBase.COLUMN_NAME_BP_SYS));
                    intent.putExtra("dia", requestMap.get(DataBase.COLUMN_NAME_BP_DIA));
                    intent.putExtra("mean", requestMap.get(DataBase.COLUMN_NAME_BP_MEAN));
                    intent.putExtra("pulse", requestMap.get(DataBase.COLUMN_NAME_BP_PULSE));
                    startActivityForResult(intent, GFIT_SHEALTH_RESULT_CODE);
                } else {
                    //S 헬스
                    if (PreferenceUtil.getSHealthBP(BPMeasureActivity.this)) {
                        Intent intent = new Intent(BPMeasureActivity.this, BPGFitSHealthActivity.class);
                        intent.putExtra("sys", requestMap.get(DataBase.COLUMN_NAME_BP_SYS));
                        intent.putExtra("dia", requestMap.get(DataBase.COLUMN_NAME_BP_DIA));
                        intent.putExtra("mean", requestMap.get(DataBase.COLUMN_NAME_BP_MEAN));
                        intent.putExtra("pulse", requestMap.get(DataBase.COLUMN_NAME_BP_PULSE));
                        startActivityForResult(intent, GFIT_SHEALTH_RESULT_CODE);
                    } else {
                        setResult(RESULT_OK);
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                setResult(RESULT_OK);
                finish();
            }
        }
    }
}
