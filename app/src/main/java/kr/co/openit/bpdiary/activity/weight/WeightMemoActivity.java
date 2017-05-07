package kr.co.openit.bpdiary.activity.weight;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.databinding.ActivityWeightMemoBinding;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.model.WeightMemoModel;
import kr.co.openit.bpdiary.services.WeighingScaleService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Created by Hwang on 2016-12-29.
 */

public class WeightMemoActivity extends NonMeasureActivity {

    private ActivityWeightMemoBinding binding;

    private WeightMemoModel weightMemoModel;

    /**
     * 혈압 데이터 Map
     */
    private Map<String, String> responeMap;

    /**
     * GlucoseService
     */
    private WeighingScaleService weightService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnalyticsUtil.sendScene(WeightMemoActivity.this, "3_체중 메모입력");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_weight_memo);
        context = WeightMemoActivity.this;
        weightMemoModel = new WeightMemoModel();

        weightService = new WeighingScaleService(WeightMemoActivity.this);

        String[] date =
                      ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)),
                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                   true,
                                                   "/",
                                                   ":",
                                                   "yyyyMMddHHmmss",
                                                   getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));

        weightMemoModel.setDate(date[0] + "  |  " + date[1]);
        weightMemoModel.setMemo(getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));

        if (!TextUtils.isEmpty(getIntent().getStringExtra("memo"))) {
            weightMemoModel.setIsCreate(false);
        } else {
            weightMemoModel.setIsCreate(true);
        }

        if (!TextUtils.isEmpty(weightMemoModel.getMemo())) {
            weightMemoModel.setViewDelete(true);
        } else {
            weightMemoModel.setViewDelete(false);
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("update"))) {
            weightMemoModel.setMemo(getIntent().getStringExtra("update"));
            weightMemoModel.setViewDelete(false);
        }

        initToolbar(getString(R.string.main_input_memo));

        binding.setWeightMemo(weightMemoModel);

        binding.toolbar.llSave.setVisibility(View.VISIBLE);
        binding.toolbar.ivSave.setEnabled(false);
        binding.toolbar.tvSave.setEnabled(false);
        binding.toolbar.llSave.setClickable(false);

        binding.ivDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    if (BPDiaryApplication.isNetworkState(WeightMemoActivity.this)) {
                        final DefaultDialog dialog = new DefaultDialog(WeightMemoActivity.this,
                                getResources().getString(R.string.common_txt_noti),
                                getResources().getString(R.string.common_dialog_txt_content),
                                getResources().getString(R.string.common_txt_cancel),
                                getResources().getString(R.string.common_txt_confirm),
                                new IDefaultDialog() {

                                    @Override
                                    public void onCancel() {
                                    }

                                    @Override
                                    public void onConfirm() {
                                        weightMemoModel.setMemo("");
                                        makeWeightManualData();
                                    }
                                });
                        dialog.show();
                    } else {
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(WeightMemoActivity.this,
                                        getString(R.string.common_txt_noti),
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
        });

        binding.etMemoContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    binding.etMemoContent.setSelection(binding.etMemoContent.getText().length());
                }
            }
        });

        TextWatcher memoWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    binding.toolbar.ivSave.setEnabled(true);
                    binding.toolbar.tvSave.setEnabled(true);
                    binding.toolbar.llSave.setClickable(true);
                    binding.toolbar.llSave.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            if (BPDiaryApplication.isNetworkState(context)) {
                                String glucoseMemo = binding.etMemoContent.getText().toString();
                                if (!binding.getWeightMemo().getIsCreate()) {
                                    weightMemoModel.setMemo(glucoseMemo);
                                    makeWeightManualData();
                                } else {
                                    if (!TextUtils.isEmpty(glucoseMemo)) {
                                        Intent intent = new Intent();
                                        intent.putExtra("wsMemo", glucoseMemo);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    } else {
                                        finish();
                                    }
                                }
                            } else {
                                DefaultOneButtonDialog defaultOneButtonDialog =
                                                                              new DefaultOneButtonDialog(WeightMemoActivity.this,
                                                                                                         getString(R.string.common_txt_noti),
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
                    });

                } else {
                    if (weightMemoModel.getIsCreate()) {
                        binding.toolbar.ivSave.setEnabled(false);
                        binding.toolbar.tvSave.setEnabled(false);
                        binding.toolbar.llSave.setClickable(false);
                    } else {
                        binding.toolbar.ivSave.setEnabled(true);
                        binding.toolbar.tvSave.setEnabled(true);
                        binding.toolbar.llSave.setClickable(true);
                    }
                }
            }
        };
        binding.etMemoContent.addTextChangedListener(memoWatcher);
    }

    /**
     * 혈당 Data 생성
     */
    private void makeWeightManualData() {
        responeMap = new HashMap<String, String>();

        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_INS_DT,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE, weightMemoModel.getMemo());

        new CreateMessageSync().execute();
    }

    /**
     * 메모 전송 Sync
     */
    private class CreateMessageSync extends AsyncTask<Void, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            showLodingProgress();
        }

        @Override
        protected JSONObject doInBackground(Void... params) {

            Map<Object, Object> data = new HashMap<Object, Object>();
            JSONObject resultJSON = new JSONObject();

            try {

                //DB 메세지 업데이트
                int nRow = weightService.updateMessage(responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_SEQ),
                                                       weightMemoModel.getMemo());

                if (nRow > 0) {

                    //Server 전송
                    data.put(ManagerConstants.RequestParamName.UUID,
                             PreferenceUtil.getEncEmail(WeightMemoActivity.this));
                    data.put(ManagerConstants.RequestParamName.INS_DT,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                    data.put(ManagerConstants.RequestParamName.WS_WEIGHT,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_WEIGHT));
                    data.put(ManagerConstants.RequestParamName.HEIGHT,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_HEIGHT));
                    data.put(ManagerConstants.RequestParamName.WS_BMI,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI));
                    data.put(ManagerConstants.RequestParamName.WS_BMI_TYPE,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_WS_BMI_TYPE));
                    data.put(ManagerConstants.RequestParamName.MESSAGE, weightMemoModel.getMemo());
                    data.put(ManagerConstants.RequestParamName.RECORD_DT,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));

                    resultJSON = weightService.modifyMeasureData(data);

                    if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                         .toString())) {
                            //Server 전송 완료

                            if (ManagerConstants.ServerSyncYN.SERVER_SYNC_N.equals(responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)
                                                                                             .toString())) {
                                //DB에 Server 전송 Update
                                weightService.updateSendToServerYN(responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ));
                            }
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
            hideLodingProgress();

            Intent intent = new Intent();
            intent.putExtra("position", getIntent().getStringExtra("position"));
            intent.putExtra("memo", weightMemoModel.getMemo());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
