package kr.co.openit.bpdiary.activity.setting;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVWriter;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.BaseActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.dialog.CustomProgressDialog;
import kr.co.openit.bpdiary.services.BloodPressureService;
import kr.co.openit.bpdiary.services.CsvBeanBP;
import kr.co.openit.bpdiary.services.CsvBeanGlucose;
import kr.co.openit.bpdiary.services.CsvBeanWS;
import kr.co.openit.bpdiary.services.GlucoseService;
import kr.co.openit.bpdiary.services.WeighingScaleService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PermissionUtils;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

public class DataSendActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout llEmptyView;

    /**
     * 혈압 내보내기 Layout
     */
    private LinearLayout llBPExport;

    /**
     * 체중 내보내기 Layout
     */
    private LinearLayout llWeightExport;

    /**
     * 혈당 내보내기 Layout
     */
    private LinearLayout llGlucoseExport;

    /**
     * 경로 텍스트뷰
     */
    private TextView tvExplain;

    /**
     * ProgressDialog
     */
    private CustomProgressDialog mProgress;

    private LinearLayout llAds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_send);

        initToolbar(getString(R.string.setting_data_send));

        AnalyticsUtil.sendScene(DataSendActivity.this, "3_M 데이터 보내기");

        /**
         * 광고
         */
        llAds = (LinearLayout)findViewById(R.id.ll_ads);
        AdView adView = (AdView)findViewById(R.id.ad_view);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                                                     .addTestDevice("DB3280FEC2620B2BF06C4FCB439097B5")
                                                     .build();
        adView.loadAd(adRequest);

        if (PreferenceUtil.getIsPayment(DataSendActivity.this)) {
            llAds.setVisibility(View.GONE);
        } else {
            llAds.setVisibility(View.VISIBLE);
        }

        setLayout();

    }

    private void setLayout() {

        llEmptyView = (LinearLayout)findViewById(R.id.ll_empty);
        llEmptyView.setVisibility(View.VISIBLE);
        llBPExport = (LinearLayout)findViewById(R.id.ll_bp_export);
        llWeightExport = (LinearLayout)findViewById(R.id.ll_ws_export);
        llGlucoseExport = (LinearLayout)findViewById(R.id.ll_glucose_export);
        tvExplain = (TextView)findViewById(R.id.tv_explain);

        llBPExport.setOnClickListener(this);
        llWeightExport.setOnClickListener(this);
        llGlucoseExport.setOnClickListener(this);

        tvExplain.setText(String.format(getResources().getString(R.string.csv_file_path_guide),
                                        ManagerConstants.AppConfig.STORAGE_PATH));
    }

    @Override
    public void onClick(View v) {
        if (!ManagerUtil.isClicking()) {

            switch (v.getId()) {
                case R.id.ll_bp_export:
                    checkPermission(v.getId());
                    break;
                case R.id.ll_ws_export:
                    checkPermission(v.getId());
                    break;
                case R.id.ll_glucose_export:
                    checkPermission(v.getId());
                    break;

            }
        }
    }

    /**
     * 권한 확인
     */
    private void checkPermission(int id) {

        if (PermissionUtils.checkPermissionStorage(DataSendActivity.this)) {

            if (id == R.id.ll_bp_export) {
                new ExportBPDataAsync().execute();
            } else if (id == R.id.ll_ws_export) {
                new ExportWSDataAsync().execute();
            } else if (id == R.id.ll_glucose_export) {
                new ExportGlucoseDataAsync().execute();
            }
        } else {

            /*
             * ActivityCompat.requestPermissions(getActivity(), new
             * String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
             * PermissionRequestCode.PERMISSION_REQUEST_CHECK_CSV_OUTPUT);
             */
        }
    }

    /**
     * Toast Masseage
     */
    private void showMessage(int result) {
        switch (result) {
            case 0:
                Toast.makeText(DataSendActivity.this, getString(R.string.csv_export_no_data), Toast.LENGTH_SHORT)
                     .show();
                break;
            case 1:
                Toast.makeText(DataSendActivity.this, getString(R.string.csv_output_finish), Toast.LENGTH_SHORT).show();
                break;

        }
    }

    /**
     * 혈압 데이터 내보내기 Async
     */
    private class ExportBPDataAsync extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {

            if (mProgress == null) {
                mProgress = new CustomProgressDialog(DataSendActivity.this);
                mProgress.setCancelable(false);
                mProgress.setCanceledOnTouchOutside(false);
            }
            mProgress.show();

        }

        @Override
        protected Integer doInBackground(Void... params) {

            BloodPressureService bpService = new BloodPressureService(DataSendActivity.this);

            try {

                //DB에서 혈압 데이터 목록 조회
                List<Map<String, String>> resultBPDataList = bpService.searchBloodPressureDataList();

                if (resultBPDataList.size() > 0) {

                    File file = new File(ManagerConstants.AppConfig.STORAGE_PATH);

                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    Map<String, String> resultMap;

                    List<CsvBeanBP> data = new ArrayList<CsvBeanBP>();

                    CsvBeanBP bpDataBean = new CsvBeanBP();

                    bpDataBean.setSys("Systolic");
                    bpDataBean.setDia("Diastolic");
                    bpDataBean.setPulse("Pulse");
                    bpDataBean.setMessage("Comment");
                    bpDataBean.setRecordDt("Date");

                    data.add(bpDataBean);

                    for (int i = 0, size = resultBPDataList.size(); i < size; i++) {

                        resultMap = resultBPDataList.get(i);

                        //Server 전송
                        bpDataBean = new CsvBeanBP();

                        bpDataBean.setSys(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS));
                        bpDataBean.setDia(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA));
                        bpDataBean.setPulse(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE));
                        bpDataBean.setMessage(null == resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE)
                                              || "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE)) ? ""
                                                                                                                             : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));

                        bpDataBean.setRecordDt(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));

                        data.add(bpDataBean);

                    }

                    String strCharsetName = "UTF-8";

                    if (PreferenceUtil.getLanguage(DataSendActivity.this).equals(ManagerConstants.Language.KOR)) {
                        strCharsetName = "EUC-KR";
                    }

                    CSVWriter writer = new CSVWriter(
                                                     new OutputStreamWriter(new FileOutputStream(ManagerConstants.AppConfig.STORAGE_PATH
                                                                                                 + "/BPDiary_BP_"
                                                                                                 + ManagerUtil.getCurrentDateTime()
                                                                                                 + ".csv"),
                                                                            strCharsetName),
                                                     CSVWriter.DEFAULT_SEPARATOR);

                    // loop
                    for (CsvBeanBP dataBean : data) {
                        // wirteNext에는 인자로 String배열을 넘김
                        // 그러기 위해서는 AddressBean.csvString()에 만들어 놓은 문자열을
                        // 구분자 ','로 split으로 String배열을 구해 witeNext()에 넘김
                        writer.writeNext(dataBean.csvString().split(","));
                    }
                    // 화일 닫기
                    writer.flush();
                    writer.close();
                    return 1;
                } else {
                    return 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.hide();
            }
            if (result != null) {
                showMessage(result);
            }
        }
    }

    /**
     * 체중 데이터 내보내기 Async
     */
    private class ExportWSDataAsync extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {

            if (mProgress == null) {
                mProgress = new CustomProgressDialog(DataSendActivity.this);
                mProgress.setCancelable(false);
                mProgress.setCanceledOnTouchOutside(false);
            }
            mProgress.show();

        }

        @Override
        protected Integer doInBackground(Void... params) {

            WeighingScaleService wsService = new WeighingScaleService(DataSendActivity.this);

            try {

                //DB에서 체중 데이터 목록 조회
                List<Map<String, String>> resultWSDataList = wsService.searchWeighinhScaleDataList();

                if (resultWSDataList.size() > 0) {

                    File file = new File(ManagerConstants.AppConfig.STORAGE_PATH);

                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    Map<String, String> resultMap;

                    List<CsvBeanWS> data = new ArrayList<CsvBeanWS>();

                    CsvBeanWS wsDataBean = new CsvBeanWS();

                    wsDataBean.setWeight("Weight");
                    wsDataBean.setMessage("Comment");
                    wsDataBean.setRecordDt("Date");

                    data.add(wsDataBean);

                    for (int i = 0, size = resultWSDataList.size(); i < size; i++) {

                        resultMap = resultWSDataList.get(i);

                        //Server 전송
                        wsDataBean = new CsvBeanWS();

                        wsDataBean.setWeight(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));

                        wsDataBean.setMessage(null == resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE)
                                              || "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE)) ? ""
                                                                                                                             : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));

                        wsDataBean.setRecordDt(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));

                        data.add(wsDataBean);

                    }

                    //파일 생성
                    //csv write생성
                    //','를 구분자로해서 출력하도록 설정
                    //                    CSVWriter writer = new CSVWriter(new FileWriter(AppConfig.STORAGE_PATH + "/BPDiary_Weight_"
                    //                                                                    + ManagerUtil.getCurrentDateTime()
                    //                                                                    + ".csv"), CSVWriter.DEFAULT_SEPARATOR);

                    String strCharsetName = "UTF-8";

                    if (PreferenceUtil.getLanguage(DataSendActivity.this).equals(ManagerConstants.Language.KOR)) {
                        strCharsetName = "EUC-KR";
                    }

                    CSVWriter writer = new CSVWriter(
                                                     new OutputStreamWriter(new FileOutputStream(ManagerConstants.AppConfig.STORAGE_PATH
                                                                                                 + "/BPDiary_Weight_"
                                                                                                 + ManagerUtil.getCurrentDateTime()
                                                                                                 + ".csv"),
                                                                            strCharsetName),
                                                     CSVWriter.DEFAULT_SEPARATOR);

                    // loop
                    for (CsvBeanWS dataBean : data) {
                        // wirteNext에는 인자로 String배열을 넘김
                        // 그러기 위해서는 AddressBean.csvString()에 만들어 놓은 문자열을
                        // 구분자 ','로 split으로 String배열을 구해 witeNext()에 넘김
                        writer.writeNext(dataBean.csvString().split(","));
                    }

                    // 화일 닫기
                    writer.flush();
                    writer.close();
                    return 1;
                } else {
                    return 0;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.hide();
            }
            if (result != null) {
                showMessage(result);
            }
        }
    }

    /**
     * 혈당 데이터 내보내기 Async
     */
    private class ExportGlucoseDataAsync extends AsyncTask<Void, Void, Integer> {

        @Override
        protected void onPreExecute() {

            if (mProgress == null) {
                mProgress = new CustomProgressDialog(DataSendActivity.this);
                mProgress.setCancelable(false);
                mProgress.setCanceledOnTouchOutside(false);
            }
            mProgress.show();

        }

        @Override
        protected Integer doInBackground(Void... params) {

            GlucoseService glucoseService = new GlucoseService(DataSendActivity.this);

            try {

                //DB에서 혈당 데이터 목록 조회
                List<Map<String, String>> resultGlucoseDataList = glucoseService.searchGlucoseDataList();

                if (resultGlucoseDataList.size() > 0) {

                    File file = new File(ManagerConstants.AppConfig.STORAGE_PATH);

                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    Map<String, String> resultMap;

                    List<CsvBeanGlucose> data = new ArrayList<>();

                    CsvBeanGlucose glucoseDataBean = new CsvBeanGlucose();

                    glucoseDataBean.setGlucose("Glucose");
                    glucoseDataBean.setMealType("MealType");
                    glucoseDataBean.setMessage("Comment");
                    glucoseDataBean.setRecordDt("Date");

                    data.add(glucoseDataBean);

                    for (int i = 0, size = resultGlucoseDataList.size(); i < size; i++) {

                        resultMap = resultGlucoseDataList.get(i);

                        //Server 전송
                        glucoseDataBean = new CsvBeanGlucose();
                        glucoseDataBean.setGlucose(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));
                        glucoseDataBean.setMealType(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL));
                        glucoseDataBean.setMessage(null == resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE)
                                                   || "null".equals(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE)) ? ""
                                                                                                                                  : resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));
                        glucoseDataBean.setRecordDt(resultMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));
                        data.add(glucoseDataBean);

                    }

                    String strCharsetName = "UTF-8";

                    if (PreferenceUtil.getLanguage(DataSendActivity.this).equals(ManagerConstants.Language.KOR)) {
                        strCharsetName = "EUC-KR";
                    }

                    CSVWriter writer = new CSVWriter(
                                                     new OutputStreamWriter(new FileOutputStream(ManagerConstants.AppConfig.STORAGE_PATH
                                                                                                 + "/BPDiary_Glucose_"
                                                                                                 + ManagerUtil.getCurrentDateTime()
                                                                                                 + ".csv"),
                                                                            strCharsetName),
                                                     CSVWriter.DEFAULT_SEPARATOR);

                    // loop
                    for (CsvBeanGlucose dataBean : data) {
                        // wirteNext에는 인자로 String배열을 넘김
                        // 그러기 위해서는 AddressBean.csvString()에 만들어 놓은 문자열을
                        // 구분자 ','로 split으로 String배열을 구해 witeNext()에 넘김
                        writer.writeNext(dataBean.csvString().split(","));
                    }
                    // 화일 닫기
                    writer.flush();
                    writer.close();
                    return 1;
                } else {
                    return 0;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (mProgress != null && mProgress.isShowing()) {
                mProgress.hide();
            }
            if (result != null) {
                showMessage(result);
            }
        }
    }
}
