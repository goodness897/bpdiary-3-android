package kr.co.openit.bpdiary.activity.glucose;

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
import kr.co.openit.bpdiary.databinding.ActivityGlucoseMemoBinding;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.model.GlucoseMemoModel;
import kr.co.openit.bpdiary.services.GlucoseService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Created by Hwang on 2016-12-29.
 */

public class GlucoseMemoActivity extends NonMeasureActivity {

    private ActivityGlucoseMemoBinding binding;

    private GlucoseMemoModel glucoseMemoModel;

    /**
     * 혈압 데이터 Map
     */
    private Map<String, String> responeMap;

    /**
     * GlucoseService
     */
    private GlucoseService glucoseService;

    private EditText etContent;

    private TextView tvTimeDate;

    private ImageView deleteImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AnalyticsUtil.sendScene(GlucoseMemoActivity.this, "3_혈당 메모 입력");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_glucose_memo);
        context = GlucoseMemoActivity.this;
        glucoseMemoModel = new GlucoseMemoModel();

        glucoseService = new GlucoseService(GlucoseMemoActivity.this);

        String[] date =
                      ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)),
                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                   true,
                                                   "/",
                                                   ":",
                                                   "yyyyMMddHHmmss",
                                                   getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));

        glucoseMemoModel.setDate(date[0] + "  |  " + date[1]);
        glucoseMemoModel.setMemo(getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));

        if (!TextUtils.isEmpty(getIntent().getStringExtra("memo"))) {
            glucoseMemoModel.setIsCreate(false);
        } else {
            glucoseMemoModel.setIsCreate(true);
        }

        if (!TextUtils.isEmpty(glucoseMemoModel.getMemo())) {
            glucoseMemoModel.setViewDelete(true);
        } else {
            glucoseMemoModel.setViewDelete(false);
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("update"))) {
            glucoseMemoModel.setMemo(getIntent().getStringExtra("update"));
            glucoseMemoModel.setViewDelete(false);
        }

        initToolbar(getString(R.string.main_input_memo));

        binding.setGlucoseMemo(glucoseMemoModel);

        binding.toolbar.llSave.setVisibility(View.VISIBLE);
        binding.toolbar.ivSave.setEnabled(false);
        binding.toolbar.tvSave.setEnabled(false);
        binding.toolbar.llSave.setClickable(false);

        binding.ivDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    if (BPDiaryApplication.isNetworkState(GlucoseMemoActivity.this)) {
                        final DefaultDialog dialog = new DefaultDialog(GlucoseMemoActivity.this,
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
                                        glucoseMemoModel.setMemo("");
                                        makeGlucoseManualData();
                                    }
                                });
                        dialog.show();
                    } else {
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(GlucoseMemoActivity.this,
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
                            if (!ManagerUtil.isClicking()) {

                                if (BPDiaryApplication.isNetworkState(context)) {
                                    String glucoseMemo = binding.etMemoContent.getText().toString();
                                    if (!binding.getGlucoseMemo().getIsCreate()) {
                                        glucoseMemoModel.setMemo(glucoseMemo);
                                        makeGlucoseManualData();
                                    } else {
                                        if (!TextUtils.isEmpty(glucoseMemo)) {
                                            Intent intent = new Intent();
                                            intent.putExtra("glucoseMemo", glucoseMemo);
                                            setResult(RESULT_OK, intent);
                                            finish();
                                        } else {
                                            finish();
                                        }
                                    }
                                } else {
                                    DefaultOneButtonDialog defaultOneButtonDialog =
                                            new DefaultOneButtonDialog(GlucoseMemoActivity.this,
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

                } else {
                    if (glucoseMemoModel.getIsCreate()) {
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
    private void makeGlucoseManualData() {
        responeMap = new HashMap<String, String>();
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_INS_DT,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE, glucoseMemoModel.getMemo());

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
                int nRow =
                         glucoseService.updateMessage(responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ),
                                                      glucoseMemoModel.getMemo());

                if (nRow > 0) {

                    //Server 전송
                    data.put(ManagerConstants.RequestParamName.UUID,
                             PreferenceUtil.getEncEmail(GlucoseMemoActivity.this));
                    data.put(ManagerConstants.RequestParamName.INS_DT,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                    data.put(ManagerConstants.RequestParamName.GLUCOSE,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE));
                    data.put(ManagerConstants.RequestParamName.GLUCOSE_MEAL,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_MEAL));
                    data.put(ManagerConstants.RequestParamName.GLUCOSE_TYPE,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_TYPE));
                    data.put(ManagerConstants.RequestParamName.MESSAGE, glucoseMemoModel.getMemo());
                    data.put(ManagerConstants.RequestParamName.RECORD_DT,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));

                    resultJSON = glucoseService.modifyMeasureData(data);

                    if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                         .toString())) {
                            //Server 전송 완료

                            if (ManagerConstants.ServerSyncYN.SERVER_SYNC_N.equals(responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)
                                                                                             .toString())) {
                                //DB에 Server 전송 Update
                                glucoseService.updateSendToServerYN(responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_GLUCOSE_SEQ));
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
            intent.putExtra("memo", glucoseMemoModel.getMemo());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
