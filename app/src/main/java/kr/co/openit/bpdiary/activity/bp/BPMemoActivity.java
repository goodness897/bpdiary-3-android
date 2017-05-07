package kr.co.openit.bpdiary.activity.bp;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.common.NonMeasureActivity;
import kr.co.openit.bpdiary.activity.intro.LoginLogicActivity;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.databinding.ActivityBpMemoBinding;
import kr.co.openit.bpdiary.dialog.DefaultDialog;
import kr.co.openit.bpdiary.dialog.DefaultOneButtonDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultDialog;
import kr.co.openit.bpdiary.interfaces.IDefaultOneButtonDialog;
import kr.co.openit.bpdiary.model.BpMemoModel;
import kr.co.openit.bpdiary.services.BloodPressureService;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;

/**
 * Created by Hwang on 2016-12-30.
 */

public class BPMemoActivity extends NonMeasureActivity {

    private ActivityBpMemoBinding binding;

    private BpMemoModel bpMemoModel;

    /**
     * 혈압 데이터 Map
     */
    private Map<String, String> responeMap;

    /**
     * BloodPressureService
     */
    private BloodPressureService bpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        AnalyticsUtil.sendScene(BPMemoActivity.this, "3_혈압 메모입력");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_bp_memo);
        context = BPMemoActivity.this;
        bpMemoModel = new BpMemoModel();

        /**
         * blood pressure service
         */
        bpService = new BloodPressureService(BPMemoActivity.this);

        String[] date =
                      ManagerUtil.getDateCharacter(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)),
                                                   ManagerUtil.ShowFormatPosition.SECOND,
                                                   true,
                                                   "/",
                                                   ":",
                                                   "yyyyMMddHHmmss",
                                                   getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));

        bpMemoModel.setDate(date[0] + "  |  " + date[1]);
        bpMemoModel.setMemo(getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE));
        bpMemoModel.setArmType(getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM));

        //update일때 처리방법 질문
        //        if (!TextUtils.isEmpty(getIntent().getStringExtra("memo")) || !TextUtils.isEmpty(getIntent().getStringExtra("update"))) {
        if (!TextUtils.isEmpty(getIntent().getStringExtra("memo"))) {
            bpMemoModel.setIsCreate(false);
        } else {
            bpMemoModel.setIsCreate(true);
        }

        if (!TextUtils.isEmpty(bpMemoModel.getMemo())) {
            bpMemoModel.setViewDelete(true);
        } else {
            bpMemoModel.setViewDelete(false);
        }

        if (!TextUtils.isEmpty(getIntent().getStringExtra("update"))) {
            bpMemoModel.setMemo(getIntent().getStringExtra("update"));
            bpMemoModel.setViewDelete(false);
        }

        initToolbar(getString(R.string.main_input_memo));

        binding.setBpMemo(bpMemoModel);

        if (ManagerConstants.ArmType.BP_ARM_LEFT.equals(bpMemoModel.getArmType())) {
            binding.tvLeftArm.setSelected(true);
        } else if (ManagerConstants.ArmType.BP_ARM_RIGHT.equals(bpMemoModel.getArmType())) {
            binding.tvRightArm.setSelected(true);
        } else {
            binding.tvLeftArm.setSelected(false);
            binding.tvRightArm.setSelected(false);
        }

        binding.toolbar.llSave.setVisibility(View.VISIBLE);
        binding.toolbar.ivSave.setEnabled(false);
        binding.toolbar.tvSave.setEnabled(false);
        binding.toolbar.llSave.setClickable(false);

        binding.ivDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    if (BPDiaryApplication.isNetworkState(BPMemoActivity.this)) {
                        final DefaultDialog dialog = new DefaultDialog(BPMemoActivity.this,
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
                                        bpMemoModel.setMemo("");
                                        bpMemoModel.setArmType("");
                                        makeBPManualData();
                                    }
                                });
                        dialog.show();
                    } else {
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(BPMemoActivity.this,
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

        binding.tvLeftArm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    if (binding.tvLeftArm.isSelected()) {
                        binding.tvLeftArm.setSelected(false);
                        binding.tvRightArm.setSelected(false);
                        binding.toolbar.ivSave.setEnabled(true);
                        binding.toolbar.tvSave.setEnabled(true);
                        binding.toolbar.llSave.setClickable(true);
                    } else {
                        binding.tvLeftArm.setSelected(true);
                        binding.tvRightArm.setSelected(false);
                        binding.toolbar.ivSave.setEnabled(true);
                        binding.toolbar.tvSave.setEnabled(true);
                        binding.toolbar.llSave.setClickable(true);
                    }
                }
            }
        });

        binding.tvRightArm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    if (binding.tvRightArm.isSelected()) {
                        binding.tvLeftArm.setSelected(false);
                        binding.tvRightArm.setSelected(false);
                        binding.toolbar.ivSave.setEnabled(true);
                        binding.toolbar.tvSave.setEnabled(true);
                        binding.toolbar.llSave.setClickable(true);
                    } else {
                        binding.tvLeftArm.setSelected(false);
                        binding.tvRightArm.setSelected(true);
                        binding.toolbar.ivSave.setEnabled(true);
                        binding.toolbar.tvSave.setEnabled(true);
                        binding.toolbar.llSave.setClickable(true);
                    }
                }
            }
        });

        binding.toolbar.llSave.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (!ManagerUtil.isClicking()) {

                    if (BPDiaryApplication.isNetworkState(context)) {
                        if (binding.toolbar.ivSave.isEnabled()) {
                            String bpMemo = binding.etMemoContent.getText().toString();
                            if (binding.tvLeftArm.isSelected()) {
                                bpMemoModel.setArmType(ManagerConstants.ArmType.BP_ARM_LEFT);
                            } else if (binding.tvRightArm.isSelected()) {
                                bpMemoModel.setArmType(ManagerConstants.ArmType.BP_ARM_RIGHT);
                            } else {
                                bpMemoModel.setArmType(ManagerConstants.ArmType.BP_ARM_NOTHING);
                            }
                            if (!binding.getBpMemo().getIsCreate()) {
                                bpMemoModel.setMemo(bpMemo);
                                makeBPManualData();
                            } else {
                                if (!TextUtils.isEmpty(bpMemo)) {
                                    Intent intent = new Intent();
                                    intent.putExtra("bpMemo", bpMemo);
                                    setResult(RESULT_OK, intent);
                                    finish();
                                } else {
                                    finish();
                                }
                            }
                        }

                    } else {
                        DefaultOneButtonDialog defaultOneButtonDialog =
                                new DefaultOneButtonDialog(BPMemoActivity.this,
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

        TextWatcher memoWatcher = new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (bpMemoModel.getIsCreate()) {
                    binding.toolbar.ivSave.setEnabled(true);
                    binding.toolbar.tvSave.setEnabled(true);
                    binding.toolbar.llSave.setClickable(true);
                } else {
                    binding.toolbar.ivSave.setEnabled(false);
                    binding.toolbar.tvSave.setEnabled(false);
                    binding.toolbar.llSave.setClickable(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!binding.etMemoContent.getText().toString().equals(bpMemoModel.getMemo())) {
                    binding.toolbar.ivSave.setEnabled(true);
                    binding.toolbar.tvSave.setEnabled(true);
                    binding.toolbar.llSave.setClickable(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.length() > 0 && binding.etMemoContent.getText().toString().length() > 0) {
                    if (bpMemoModel.getIsCreate()) {
                        binding.toolbar.ivSave.setEnabled(true);
                        binding.toolbar.tvSave.setEnabled(true);
                        binding.toolbar.llSave.setClickable(true);
                    }
                } else {
                    if (bpMemoModel.getIsCreate()) {
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
     * 혈압 Data 생성
     */
    private void makeBPManualData() {
        responeMap = new HashMap<String, String>();
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_ARM, bpMemoModel.getArmType());
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_INS_DT,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN,
                       getIntent().getStringExtra(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN));
        responeMap.put(ManagerConstants.DataBase.COLUMN_NAME_MESSAGE, bpMemoModel.getMemo());

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
                int nRow = bpService.updateMessage(responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ),
                                                   bpMemoModel.getMemo(),
                                                   bpMemoModel.getArmType());

                if (nRow > 0) {

                    //Server 전송
                    data.put(ManagerConstants.RequestParamName.UUID, PreferenceUtil.getEncEmail(BPMemoActivity.this));
                    data.put(ManagerConstants.RequestParamName.INS_DT,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_INS_DT));
                    data.put(ManagerConstants.RequestParamName.BP_SYS,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SYS));
                    data.put(ManagerConstants.RequestParamName.BP_DIA,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_DIA));
                    data.put(ManagerConstants.RequestParamName.BP_MEAN,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_MEAN));
                    data.put(ManagerConstants.RequestParamName.BP_PULSE,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_PULSE));
                    data.put(ManagerConstants.RequestParamName.BP_TYPE,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_TYPE));
                    data.put(ManagerConstants.RequestParamName.BP_ARM, bpMemoModel.getArmType());
                    data.put(ManagerConstants.RequestParamName.MESSAGE, bpMemoModel.getMemo());
                    data.put(ManagerConstants.RequestParamName.RECORD_DT,
                             responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_MEASURE_DT));

                    resultJSON = bpService.modifyMeasureData(data);

                    if (resultJSON.has(ManagerConstants.ResponseParamName.RESULT)) {

                        if (ManagerConstants.ResponseResult.RESULT_TRUE.equals(resultJSON.get(ManagerConstants.ResponseParamName.RESULT)
                                                                                         .toString())) {
                            //Server 전송 완료

                            if (ManagerConstants.ServerSyncYN.SERVER_SYNC_N.equals(responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_SEND_TO_SERVER_YN)
                                                                                             .toString())) {
                                //DB에 Server 전송 Update
                                bpService.updateSendToServerYN(responeMap.get(ManagerConstants.DataBase.COLUMN_NAME_BP_SEQ));
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
            intent.putExtra("memo", bpMemoModel.getMemo());
            intent.putExtra("arm", bpMemoModel.getArmType());
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
