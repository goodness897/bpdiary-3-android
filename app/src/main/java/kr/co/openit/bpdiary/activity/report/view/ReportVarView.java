package kr.co.openit.bpdiary.activity.report.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.bp.InputBPActivity;
import kr.co.openit.bpdiary.activity.glucose.InputGlucoseActivity;
import kr.co.openit.bpdiary.activity.main.MainActivity;
import kr.co.openit.bpdiary.activity.report.ReportShareActivity;
import kr.co.openit.bpdiary.activity.weight.InputWeightActivity;
import kr.co.openit.bpdiary.common.constants.CommonConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.customview.GraphVarOneStick;
import kr.co.openit.bpdiary.databinding.ViewReportVariationBinding;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;


/**
 * Created by Huiseop on 2016-09-22.
 */

public class ReportVarView extends LinearLayout implements View.OnClickListener {

    private ViewReportVariationBinding binding;

    private JSONObject mListData;

    private Context context;

    private GraphVarOneStick.CustomDate customDateType;

    private MainActivity activity;

    private String searchDay;

    public ReportVarView(Context context, MainActivity activity, JSONObject mListData, String searchDay) {
        super(context);
        this.mListData = mListData;
        this.context = context;
        this.activity = activity;
        this.searchDay = searchDay;

        customDateType = GraphVarOneStick.CustomDate.MONTH;

        setView();

        binding.llReportShareBtn.setOnClickListener(this);
        binding.btnBpInput.setOnClickListener(this);
        binding.btnWsInput.setOnClickListener(this);
        binding.btnAfterGlucoseInput.setOnClickListener(this);
        binding.btnBeforeGlucoseInput.setOnClickListener(this);
        binding.btnBgInput.setOnClickListener(this);
        binding.llNetworkTryAgain.setOnClickListener(this);
    }

    protected void setView() {
        LayoutInflater inflater = LayoutInflater.from(context);
        binding = DataBindingUtil.inflate(inflater, R.layout.view_report_variation, this, true);

        AnalyticsUtil.sendScene((Activity)context, "3_레포트 변화량");

        /**
         * 변화량 혈압 Sys 측정 결과 데이터(그래프)
         */
        final List<Map<String, String>> varSysListResultGraphData = new ArrayList<Map<String, String>>();
        final List<Map<String, String>> varDiaListResultGraphData = new ArrayList<Map<String, String>>();
        final List<Map<String, String>> varWeightListResultGraphData = new ArrayList<Map<String, String>>();
        final List<Map<String, String>> varBmiListResultGraphData = new ArrayList<Map<String, String>>();
        final List<Map<String, String>> varBgGlucoseListResultGraphData = new ArrayList<Map<String, String>>();
        final List<Map<String, String>> varBaGlucoseListResultGraphData = new ArrayList<Map<String, String>>();
        String beforeSys = "";
        String beforeDia = "";
        String beforeRecordDt = "";

        String afterSys = "";
        String afterDia = "";
        String afterRecordDt = "";

        String compareSys = "";
        String compareDia = "";
        String compareSysStr = "";
        String compareDiaStr = "";

        String goalSys = "";
        String goalDia = "";

        String beforeWeight = "";
        String beforeBmi = "";

        String afterWeight = "";
        String afterBmi = "";

        String compareWeight = "";
        String compareBmi = "";
        String compareWeightStr = "";

        String goalWeight = "";

        /**
         * 식전 혈당
         */
        String beforeGbGlucose = "";

        String afterGbGlucose = "";

        String compareGbGlucose = "";
        String compareGbGlucoseStr = "";

        String goalGbGlucose = "";

        /**
         * 식후 혈당
         */
        String beforeGaGlucose = "";

        String afterGaGlucose = "";

        String compareGaGlucose = "";
        String compareGaGlucoseStr = "";

        String goalGaGlucose = "";

        JSONObject result;

        try {
            if (BPDiaryApplication.isNetworkState(context)) {
                binding.llIsNetworkFalse.setVisibility(View.GONE);

                boolean isPAvgListAllNull = false;

                /**
                 * 혈압
                 */
                if (mListData.has(ManagerConstants.ResponseParamName.P_VAR)) {
                    result = mListData.getJSONObject(ManagerConstants.ResponseParamName.P_VAR);
                    if (result.has(ManagerConstants.ResponseParamName.VAR_LIST)) {
                        JSONArray varList = result.getJSONArray(ManagerConstants.ResponseParamName.VAR_LIST);
                        if (varList.length() > 0) {
                            isPAvgListAllNull = false;
                            JSONObject varListResult;
                            for (int i = 0; i < varList.length(); i++) {
                                Map<String, String> sysData = new HashMap<String, String>();
                                Map<String, String> diaData = new HashMap<String, String>();
                                varListResult = varList.getJSONObject(i);
                                if (i == 0) {
                                    if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.SYS))) {
                                        beforeSys = varListResult.getString(ManagerConstants.ResponseParamName.SYS);
                                        sysData.put(ManagerConstants.ResponseParamName.SYS, String.valueOf(beforeSys));
                                    } else {
                                    }
                                    if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.DIA))) {
                                        beforeDia = varListResult.getString(ManagerConstants.ResponseParamName.DIA);
                                        diaData.put(ManagerConstants.ResponseParamName.DIA, String.valueOf(beforeDia));
                                    }
                                    if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.RECORD_DT))) {
                                        beforeRecordDt =
                                                       varListResult.getString(ManagerConstants.ResponseParamName.RECORD_DT);
                                        sysData.put(ManagerConstants.ResponseParamName.RECORD_DT,
                                                    beforeRecordDt.substring(0, 8));
                                        diaData.put(ManagerConstants.ResponseParamName.RECORD_DT,
                                                    beforeRecordDt.substring(0, 8));
                                    }

                                } else if (i == 1) {
                                    if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.SYS))) {
                                        afterSys = varListResult.getString(ManagerConstants.ResponseParamName.SYS);
                                        sysData.put(ManagerConstants.ResponseParamName.SYS, String.valueOf(afterSys));
                                    }
                                    if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.DIA))) {
                                        afterDia = varListResult.getString(ManagerConstants.ResponseParamName.DIA);
                                        diaData.put(ManagerConstants.ResponseParamName.DIA, String.valueOf(afterDia));
                                    }
                                    if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.RECORD_DT))) {
                                        afterRecordDt =
                                                      varListResult.getString(ManagerConstants.ResponseParamName.RECORD_DT);
                                        sysData.put(ManagerConstants.ResponseParamName.RECORD_DT,
                                                    afterRecordDt.substring(0, 8));
                                        diaData.put(ManagerConstants.ResponseParamName.RECORD_DT,
                                                    afterRecordDt.substring(0, 8));
                                    }
                                }
                                varSysListResultGraphData.add(sysData);
                                varDiaListResultGraphData.add(diaData);
                            }
                            setVarSysGrapth(varSysListResultGraphData,
                                            Integer.parseInt(beforeSys),
                                            Integer.parseInt(afterSys));
                            setVarDiaGrapth(varDiaListResultGraphData,
                                            Integer.parseInt(beforeDia),
                                            Integer.parseInt(afterDia));

                        } else {
                            isPAvgListAllNull = true;
                        }
                    } else {
                        isPAvgListAllNull = true;
                    }

                    JSONArray goalList = result.getJSONArray(ManagerConstants.ResponseParamName.GOAL_LIST);
                    if (goalList.length() > 0) {
                        isPAvgListAllNull = false;
                        JSONObject goalListResult;
                        for (int i = 0; i < goalList.length(); i++) {
                            goalListResult = goalList.getJSONObject(i);
                            if (i == 0) {
                                if (ManagerUtil.mapDataNullCheck(goalListResult.get(ManagerConstants.ResponseParamName.SYS))) {
                                    compareSys = goalListResult.getString(ManagerConstants.ResponseParamName.SYS);
                                    if (0 < Integer.parseInt(compareSys)) {
                                        binding.ivCompareSys.setBackgroundResource(R.drawable.img_report_arrow_up);
                                        compareSysStr = getResources().getString(R.string.report_report_variation_up);
                                    } else if (Integer.parseInt(compareSys) < 0) {
                                        binding.ivCompareSys.setBackgroundResource(R.drawable.img_report_arrow_down);
                                        compareSys = compareSys.substring(1, compareSys.length());
                                        compareSysStr = getResources().getString(R.string.report_report_variation_down);
                                    } else {
                                        binding.ivCompareSys.setBackgroundResource(R.drawable.img_report_arrow_non);
                                        compareSysStr = getResources().getString(R.string.report_report_variation_non);
                                    }

                                }
                                if (ManagerUtil.mapDataNullCheck(goalListResult.get(ManagerConstants.ResponseParamName.DIA))) {
                                    compareDia = goalListResult.getString(ManagerConstants.ResponseParamName.DIA);
                                    if (0 < Integer.parseInt(compareDia)) {
                                        binding.ivCompareDia.setBackgroundResource(R.drawable.img_report_arrow_up);
                                        compareDiaStr = getResources().getString(R.string.report_report_variation_up);
                                    } else if (Integer.parseInt(compareDia) < 0) {
                                        binding.ivCompareDia.setBackgroundResource(R.drawable.img_report_arrow_down);
                                        compareDia = compareDia.substring(1, compareDia.length());
                                        compareDiaStr = getResources().getString(R.string.report_report_variation_down);
                                    } else {
                                        binding.ivCompareDia.setBackgroundResource(R.drawable.img_report_arrow_non);
                                        compareDiaStr = getResources().getString(R.string.report_report_variation_non);
                                    }
                                }
                            } else if (i == 1) {
                                if (ManagerUtil.mapDataNullCheck(goalListResult.get(ManagerConstants.ResponseParamName.SYS))) {
                                    goalSys =
                                            String.valueOf(Math.abs(goalListResult.getInt(ManagerConstants.ResponseParamName.SYS)));
                                }
                                if (ManagerUtil.mapDataNullCheck(goalListResult.get(ManagerConstants.ResponseParamName.DIA))) {
                                    goalDia =
                                            String.valueOf(Math.abs(goalListResult.getInt(ManagerConstants.ResponseParamName.DIA)));
                                }
                            }
                        }
                    } else {
                        isPAvgListAllNull = true;
                    }

                } else {
                    isPAvgListAllNull = true;
                }

                if (isPAvgListAllNull) {
                    binding.llBpReportNonData.setVisibility(View.VISIBLE);
                    binding.llBpReportData.setVisibility(View.GONE);
                } else {
                    binding.llBpReportNonData.setVisibility(View.GONE);
                    binding.llBpReportData.setVisibility(View.VISIBLE);
                }

                boolean isWAvgListAllNull = false;
                /**
                 * 체중
                 */
                if (mListData.has(ManagerConstants.ResponseParamName.W_VAR)) {
                    result = mListData.getJSONObject(ManagerConstants.ResponseParamName.W_VAR);
                    if (result.has(ManagerConstants.ResponseParamName.VAR_LIST)) {
                        JSONArray varList = result.getJSONArray(ManagerConstants.ResponseParamName.VAR_LIST);
                        if (varList.length() > 0) {
                            if (ManagerConstants.Unit.KG.equals(PreferenceUtil.getWeightUnit(context))) {
                                binding.tvWeightUnit.setText(R.string.weight_kg);
                            } else {
                                binding.tvWeightUnit.setText(R.string.weight_lbs);
                            }
                            isWAvgListAllNull = false;
                            JSONObject varListResult;
                            for (int i = 0; i < varList.length(); i++) {
                                Map<String, String> weightData = new HashMap<String, String>();
                                Map<String, String> bmiData = new HashMap<String, String>();
                                varListResult = varList.getJSONObject(i);
                                if (i == 0) {
                                    if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.WS_WEIGHT))) {
                                        beforeWeight =
                                                     varListResult.getString(ManagerConstants.ResponseParamName.WS_WEIGHT);
                                        weightData.put(ManagerConstants.ResponseParamName.WS_WEIGHT,
                                                       String.valueOf(beforeWeight));
                                    } else {
                                    }
                                    if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.WS_BMI))) {
                                        beforeBmi = varListResult.getString(ManagerConstants.ResponseParamName.WS_BMI);
                                        bmiData.put(ManagerConstants.ResponseParamName.WS_BMI,
                                                    String.valueOf(beforeBmi));
                                    }
                                    if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.RECORD_DT))) {
                                        beforeRecordDt =
                                                       varListResult.getString(ManagerConstants.ResponseParamName.RECORD_DT);
                                        weightData.put(ManagerConstants.ResponseParamName.RECORD_DT,
                                                       beforeRecordDt.substring(0, 8));
                                        bmiData.put(ManagerConstants.ResponseParamName.RECORD_DT,
                                                    beforeRecordDt.substring(0, 8));
                                    }

                                } else if (i == 1) {
                                    if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.WS_WEIGHT))) {
                                        afterWeight =
                                                    varListResult.getString(ManagerConstants.ResponseParamName.WS_WEIGHT);
                                        weightData.put(ManagerConstants.ResponseParamName.WS_WEIGHT,
                                                       String.valueOf(afterWeight));
                                    }
                                    if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.WS_BMI))) {
                                        afterBmi = varListResult.getString(ManagerConstants.ResponseParamName.WS_BMI);
                                        bmiData.put(ManagerConstants.ResponseParamName.WS_BMI,
                                                    String.valueOf(afterBmi));
                                    }
                                    if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.RECORD_DT))) {
                                        afterRecordDt =
                                                      varListResult.getString(ManagerConstants.ResponseParamName.RECORD_DT);
                                        weightData.put(ManagerConstants.ResponseParamName.RECORD_DT,
                                                       afterRecordDt.substring(0, 8));
                                        bmiData.put(ManagerConstants.ResponseParamName.RECORD_DT,
                                                    afterRecordDt.substring(0, 8));
                                    }
                                }
                                varWeightListResultGraphData.add(weightData);
                                varBmiListResultGraphData.add(bmiData);
                            }
                            setVarWeightGrapth(varWeightListResultGraphData,
                                               Float.parseFloat(beforeWeight),
                                               Float.parseFloat(afterWeight));
                            setVarBmiGrapth(varBmiListResultGraphData,
                                            Float.parseFloat(beforeBmi),
                                            Float.parseFloat(afterBmi));

                        } else {
                            isWAvgListAllNull = true;
                        }
                    } else {
                        isWAvgListAllNull = true;
                    }

                    JSONArray goalList = result.getJSONArray(ManagerConstants.ResponseParamName.GOAL_LIST);
                    if (goalList.length() > 0) {
                        isWAvgListAllNull = false;
                        JSONObject goalListResult;
                        for (int i = 0; i < goalList.length(); i++) {
                            goalListResult = goalList.getJSONObject(i);
                            if (i == 0) {
                                if (ManagerUtil.mapDataNullCheck(goalListResult.get(ManagerConstants.ResponseParamName.WS_WEIGHT))) {
                                    compareWeight =
                                                  goalListResult.getString(ManagerConstants.ResponseParamName.WS_WEIGHT);
                                    if (0 < Float.parseFloat(compareWeight)) {
                                        binding.ivCompareWeight.setBackgroundResource(R.drawable.img_report_arrow_up);
                                        compareWeightStr =
                                                         getResources().getString(R.string.report_report_variation_up);
                                    } else if (Float.parseFloat(compareWeight) < 0) {
                                        binding.ivCompareWeight.setBackgroundResource(R.drawable.img_report_arrow_down);
                                        compareWeight = compareWeight.substring(1, compareWeight.length());
                                        compareWeightStr =
                                                         getResources().getString(R.string.report_report_variation_down);
                                    } else {
                                        binding.ivCompareWeight.setBackgroundResource(R.drawable.img_report_arrow_non);
                                        compareWeightStr =
                                                         getResources().getString(R.string.report_report_variation_non);
                                    }

                                }
                                if (ManagerUtil.mapDataNullCheck(goalListResult.get(ManagerConstants.ResponseParamName.WS_BMI))) {
                                    compareBmi = goalListResult.getString(ManagerConstants.ResponseParamName.WS_BMI);
                                    if (0 < Float.parseFloat(compareBmi)) {
                                        binding.ivCompareBmi.setBackgroundResource(R.drawable.img_report_arrow_up);
                                    } else if (Float.parseFloat(compareBmi) < 0) {
                                        binding.ivCompareBmi.setBackgroundResource(R.drawable.img_report_arrow_down);
                                        compareBmi = compareBmi.substring(1, compareBmi.length());
                                    } else {
                                        binding.ivCompareBmi.setBackgroundResource(R.drawable.img_report_arrow_non);
                                    }
                                }
                            } else if (i == 1) {
                                if (ManagerUtil.mapDataNullCheck(goalListResult.get(ManagerConstants.ResponseParamName.WS_WEIGHT))) {
                                    goalWeight =
                                               String.valueOf(Math.abs(goalListResult.getInt(ManagerConstants.ResponseParamName.WS_WEIGHT)));
                                }
                            }
                        }
                    } else {
                        isWAvgListAllNull = true;
                    }

                } else {
                    isWAvgListAllNull = true;
                }

                if (isWAvgListAllNull) {
                    binding.llWsReportNonData.setVisibility(View.VISIBLE);
                    binding.llWsReportData.setVisibility(View.GONE);
                } else {
                    binding.llWsReportNonData.setVisibility(View.GONE);
                    binding.llWsReportData.setVisibility(View.VISIBLE);
                }

                boolean isGbData = false;
                boolean isGaData = false;
                /**
                 * 식전 혈당
                 */
                if (mListData.has(ManagerConstants.ResponseParamName.GB_VAR)) {
                    if (ManagerUtil.mapDataNullCheck(mListData.get(ManagerConstants.ResponseParamName.GB_VAR))) {
                        result = mListData.getJSONObject(ManagerConstants.ResponseParamName.GB_VAR);
                        if (result.has(ManagerConstants.ResponseParamName.VAR_LIST)) {
                            JSONArray varList = result.getJSONArray(ManagerConstants.ResponseParamName.VAR_LIST);
                            if (varList.length() > 0) {
                                isGbData = true;
                                JSONObject varListResult;
                                for (int i = 0; i < varList.length(); i++) {
                                    Map<String, String> gbGlucoseData = new HashMap<String, String>();
                                    varListResult = varList.getJSONObject(i);
                                    if (i == 0) {
                                        if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.GLUCOSE))) {
                                            beforeGbGlucose =
                                                            varListResult.getString(ManagerConstants.ResponseParamName.GLUCOSE);
                                            gbGlucoseData.put(ManagerConstants.ResponseParamName.GLUCOSE,
                                                              String.valueOf(beforeGbGlucose));
                                        }
                                        if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.RECORD_DT))) {
                                            beforeRecordDt =
                                                           varListResult.getString(ManagerConstants.ResponseParamName.RECORD_DT);
                                            gbGlucoseData.put(ManagerConstants.ResponseParamName.RECORD_DT,
                                                              beforeRecordDt.substring(0, 8));
                                        }

                                    } else if (i == 1) {
                                        if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.GLUCOSE))) {
                                            afterGbGlucose =
                                                           varListResult.getString(ManagerConstants.ResponseParamName.GLUCOSE);
                                            gbGlucoseData.put(ManagerConstants.ResponseParamName.GLUCOSE,
                                                              String.valueOf(afterGbGlucose));
                                        }
                                        if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.RECORD_DT))) {
                                            afterRecordDt =
                                                          varListResult.getString(ManagerConstants.ResponseParamName.RECORD_DT);
                                            gbGlucoseData.put(ManagerConstants.ResponseParamName.RECORD_DT,
                                                              afterRecordDt.substring(0, 8));
                                        }
                                    }
                                    varBgGlucoseListResultGraphData.add(gbGlucoseData);
                                }
                                setVarGbGrapth(binding.llBgBeforeGraph,
                                               varBgGlucoseListResultGraphData,
                                               Float.parseFloat(beforeGbGlucose),
                                               Float.parseFloat(afterGbGlucose));
                                setVarGbGrapth(binding.llBgGbGraph,
                                               varBgGlucoseListResultGraphData,
                                               Float.parseFloat(beforeGbGlucose),
                                               Float.parseFloat(afterGbGlucose));

                            } else {
                                isGbData = false;
                            }
                        } else {
                            isGbData = false;
                        }

                        JSONArray goalList = result.getJSONArray(ManagerConstants.ResponseParamName.GOAL_LIST);
                        if (goalList.length() > 0) {
                            isGbData = true;
                            JSONObject goalListResult;
                            for (int i = 0; i < goalList.length(); i++) {
                                goalListResult = goalList.getJSONObject(i);
                                if (i == 0) {
                                    if (ManagerUtil.mapDataNullCheck(goalListResult.get(ManagerConstants.ResponseParamName.GLUCOSE))) {
                                        compareGbGlucose = goalListResult.getString(ManagerConstants.ResponseParamName.GLUCOSE);
                                        if (0 < Float.parseFloat(compareGbGlucose)) {
                                            binding.ivCompareBeforeGlucose.setBackgroundResource(R.drawable.img_report_arrow_up);
                                            binding.ivCompareBefore.setBackgroundResource(R.drawable.img_report_arrow_up);
                                            compareGbGlucoseStr = getResources().getString(R.string.report_report_variation_up);
                                        } else if (Float.parseFloat(compareGbGlucose) < 0) {
                                            binding.ivCompareBeforeGlucose.setBackgroundResource(R.drawable.img_report_arrow_down);
                                            binding.ivCompareBefore.setBackgroundResource(R.drawable.img_report_arrow_down);
                                            compareGbGlucose = compareGbGlucose.substring(1, compareGbGlucose.length());
                                            compareGbGlucoseStr = getResources().getString(R.string.report_report_variation_down);
                                        } else {
                                            binding.ivCompareBeforeGlucose.setBackgroundResource(R.drawable.img_report_arrow_non);
                                            binding.ivCompareBefore.setBackgroundResource(R.drawable.img_report_arrow_non);
                                            compareGbGlucoseStr = getResources().getString(R.string.report_report_variation_non);
                                        }
                                    }
                                } else if (i == 1) {
                                    if (ManagerUtil.mapDataNullCheck(goalListResult.get(ManagerConstants.ResponseParamName.GLUCOSE))) {
                                        goalGbGlucose = String.valueOf(Math.abs(goalListResult.getInt(ManagerConstants.ResponseParamName.GLUCOSE)));
                                    }
                                }
                            }
                        } else {
                            isGbData = false;
                        }
                    } else {
                        isGbData = false;
                    }
                } else {
                    isGbData = false;
                }

                /**
                 * 식후 혈당
                 */
                if (mListData.has(ManagerConstants.ResponseParamName.GA_VAR)) {
                    if (ManagerUtil.mapDataNullCheck(mListData.get(ManagerConstants.ResponseParamName.GA_VAR))) {
                        result = mListData.getJSONObject(ManagerConstants.ResponseParamName.GA_VAR);
                        if (result.has(ManagerConstants.ResponseParamName.VAR_LIST)) {
                            JSONArray varList = result.getJSONArray(ManagerConstants.ResponseParamName.VAR_LIST);
                            if (varList.length() > 0) {
                                isGaData = true;
                                JSONObject varListResult;
                                for (int i = 0; i < varList.length(); i++) {
                                    Map<String, String> gaGlucoseData = new HashMap<String, String>();
                                    varListResult = varList.getJSONObject(i);
                                    if (i == 0) {
                                        if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.GLUCOSE))) {
                                            beforeGaGlucose =
                                                            varListResult.getString(ManagerConstants.ResponseParamName.GLUCOSE);
                                            gaGlucoseData.put(ManagerConstants.ResponseParamName.GLUCOSE,
                                                              String.valueOf(beforeGaGlucose));
                                        }
                                        if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.RECORD_DT))) {
                                            beforeRecordDt =
                                                           varListResult.getString(ManagerConstants.ResponseParamName.RECORD_DT);
                                            gaGlucoseData.put(ManagerConstants.ResponseParamName.RECORD_DT,
                                                              beforeRecordDt.substring(0, 8));
                                        }

                                    } else if (i == 1) {
                                        if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.GLUCOSE))) {
                                            afterGaGlucose =
                                                           varListResult.getString(ManagerConstants.ResponseParamName.GLUCOSE);
                                            gaGlucoseData.put(ManagerConstants.ResponseParamName.GLUCOSE,
                                                              String.valueOf(afterGaGlucose));
                                        }
                                        if (ManagerUtil.mapDataNullCheck(varListResult.get(ManagerConstants.ResponseParamName.RECORD_DT))) {
                                            afterRecordDt =
                                                          varListResult.getString(ManagerConstants.ResponseParamName.RECORD_DT);
                                            gaGlucoseData.put(ManagerConstants.ResponseParamName.RECORD_DT,
                                                              afterRecordDt.substring(0, 8));
                                        }
                                    }
                                    varBaGlucoseListResultGraphData.add(gaGlucoseData);
                                }
                                setVarGaGrapth(binding.llBgAfterGraph,
                                               varBaGlucoseListResultGraphData,
                                               Float.parseFloat(beforeGaGlucose),
                                               Float.parseFloat(afterGaGlucose));
                                setVarGaGrapth(binding.llBgGaGraph,
                                               varBaGlucoseListResultGraphData,
                                               Float.parseFloat(beforeGaGlucose),
                                               Float.parseFloat(afterGaGlucose));

                            } else {
                                isGaData = false;
                            }
                        } else {
                            isGaData = false;
                        }

                        JSONArray goalList = result.getJSONArray(ManagerConstants.ResponseParamName.GOAL_LIST);
                        if (goalList.length() > 0) {
                            isGaData = true;
                            JSONObject goalListResult;
                            for (int i = 0; i < goalList.length(); i++) {
                                goalListResult = goalList.getJSONObject(i);
                                if (i == 0) {
                                    if (ManagerUtil.mapDataNullCheck(goalListResult.get(ManagerConstants.ResponseParamName.GLUCOSE))) {
                                        compareGaGlucose =
                                                         goalListResult.getString(ManagerConstants.ResponseParamName.GLUCOSE);
                                        if (0 < Float.parseFloat(compareGaGlucose)) {
                                            binding.ivCompareAfterGlucose.setBackgroundResource(R.drawable.img_report_arrow_up);
                                            binding.ivCompareAfter.setBackgroundResource(R.drawable.img_report_arrow_up);
                                            compareGaGlucoseStr =
                                                                getResources().getString(R.string.report_report_variation_up);
                                        } else if (Float.parseFloat(compareGaGlucose) < 0) {
                                            binding.ivCompareAfterGlucose.setBackgroundResource(R.drawable.img_report_arrow_down);
                                            binding.ivCompareAfter.setBackgroundResource(R.drawable.img_report_arrow_down);
                                            compareGaGlucose = compareGaGlucose.substring(1, compareGaGlucose.length());
                                            compareGaGlucoseStr =
                                                                getResources().getString(R.string.report_report_variation_down);
                                        } else {
                                            binding.ivCompareAfterGlucose.setBackgroundResource(R.drawable.img_report_arrow_non);
                                            binding.ivCompareAfter.setBackgroundResource(R.drawable.img_report_arrow_non);
                                            compareGaGlucoseStr =
                                                                getResources().getString(R.string.report_report_variation_non);
                                        }

                                    }
                                } else if (i == 1) {
                                    if (ManagerUtil.mapDataNullCheck(goalListResult.get(ManagerConstants.ResponseParamName.GLUCOSE))) {
                                        goalGaGlucose = String.valueOf(Math.abs(goalListResult.getInt(ManagerConstants.ResponseParamName.GLUCOSE)));
                                    }
                                }
                            }
                        } else {
                            isGaData = false;
                        }
                    } else {
                        isGaData = false;
                    }
                } else {
                    isGaData = false;
                }

                if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(context))) {
                    binding.tvAfterGlucoseUnit.setText(R.string.glucose_graph_mg_dl);
                    binding.tvBeforeGlucoseUnit.setText(R.string.glucose_graph_mg_dl);
                    binding.tvSingleBeforeGlucoseUnit.setText(R.string.glucose_graph_mg_dl);
                    binding.tvSingleAfterGlucoseUnit.setText(R.string.glucose_graph_mg_dl);

                } else {
                    binding.tvAfterGlucoseUnit.setText(R.string.glucose_graph_mmol);
                    binding.tvBeforeGlucoseUnit.setText(R.string.glucose_graph_mmol);
                    binding.tvSingleBeforeGlucoseUnit.setText(R.string.glucose_graph_mmol);
                    binding.tvSingleAfterGlucoseUnit.setText(R.string.glucose_graph_mmol);

                }

                //식전 식후 둘다 있을때
                if (isGbData && isGaData) {
                    binding.llBgReportData.setVisibility(View.VISIBLE);
                    binding.llBgGbReportData.setVisibility(View.GONE);
                    binding.llBgGaReportData.setVisibility(View.GONE);
                    binding.llBgReportNonData.setVisibility(View.GONE);
                } else if (isGbData || isGaData) { //식전이나 식후가 있을때 (둘다 있지는 않음)
                    if (isGbData) {
                        binding.llBgReportData.setVisibility(View.GONE);
                        binding.llBgGbReportData.setVisibility(View.VISIBLE);
                        binding.llBgGaReportData.setVisibility(View.GONE);
                        binding.llBgReportNonData.setVisibility(View.GONE);
                    } else {
                        binding.llBgReportData.setVisibility(View.GONE);
                        binding.llBgGbReportData.setVisibility(View.GONE);
                        binding.llBgGaReportData.setVisibility(View.VISIBLE);
                        binding.llBgReportNonData.setVisibility(View.GONE);
                    }
                } else { //둘다 없을때
                    if (PreferenceUtil.getUsingBloodGlucose(context)) {
                        binding.llBgReportData.setVisibility(View.GONE);
                        binding.llBgGbReportData.setVisibility(View.GONE);
                        binding.llBgGaReportData.setVisibility(View.GONE);
                        binding.llBgReportNonData.setVisibility(View.VISIBLE);
                    } else {
                        binding.llBgReportData.setVisibility(View.GONE);
                        binding.llBgGbReportData.setVisibility(View.GONE);
                        binding.llBgGaReportData.setVisibility(View.GONE);
                        binding.llBgReportNonData.setVisibility(View.GONE);
                    }
                }

                if (PreferenceUtil.getUsingBloodGlucose(context)) {
                    if (isPAvgListAllNull && isWAvgListAllNull && !isGbData && !isGaData) {
                        binding.llReportShareBtn.setVisibility(View.GONE);
                    } else {
                        binding.llReportShareBtn.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (isPAvgListAllNull && isWAvgListAllNull) {
                        binding.llReportShareBtn.setVisibility(View.GONE);
                    } else {
                        binding.llReportShareBtn.setVisibility(View.VISIBLE);
                    }
                }
            } else {
                binding.llIsNetworkFalse.setVisibility(View.VISIBLE);
                binding.llReportShareBtn.setVisibility(View.GONE);
                binding.llBpReportNonData.setVisibility(View.GONE);
                binding.llBpReportData.setVisibility(View.GONE);
                binding.llWsReportNonData.setVisibility(View.GONE);
                binding.llWsReportData.setVisibility(View.GONE);
                binding.llBgReportData.setVisibility(View.GONE);
                binding.llBgGbReportData.setVisibility(View.GONE);
                binding.llBgGaReportData.setVisibility(View.GONE);
                binding.llBgReportNonData.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        String sysResultText = String.format(getResources().getString(R.string.report_report_variation_bp_sys_result),
                                             "0".equals(compareSys) ? "" : compareSys,
                                             compareSysStr,
                                             goalSys);

        String diaResultText = String.format(getResources().getString(R.string.report_report_variation_bp_dia_result),
                                             "0".equals(compareDia) ? "" : compareDia,
                                             compareDiaStr,
                                             goalDia);
        String weightResultText =
                                String.format(getResources().getString(R.string.report_report_variation_ws_weight_result),
                                              "0".equals(compareWeight) ? "" : compareWeight,
                                              compareWeightStr,
                                              goalWeight);
        String beforeResultText =
                                String.format(getResources().getString(R.string.report_report_variation_bg_before_result),
                                              "0".equals(compareGbGlucose) ? "" : compareGbGlucose,
                                              compareGbGlucoseStr,
                                              goalGbGlucose);
        String afterResultText =
                               String.format(getResources().getString(R.string.report_report_variation_bg_after_result),
                                             "0".equals(compareGaGlucose) ? "" : compareGaGlucose,
                                             compareGaGlucoseStr,
                                             goalGaGlucose);

        binding.tvCompareSys.setText(compareSys);
        binding.tvCompareDia.setText(compareDia);
        binding.tvSysResult.setText(Html.fromHtml(sysResultText));
        binding.tvDiaResult.setText(Html.fromHtml(diaResultText));

        binding.tvCompareWeight.setText(compareWeight);
        binding.tvCompareBmi.setText(compareBmi);
        binding.tvWeightResult.setText(Html.fromHtml(weightResultText));

        binding.tvCompareBefore.setText(compareGbGlucose);
        binding.tvCompareBeforeGlucose.setText(compareGbGlucose);
        binding.tvBeforeResult.setText(Html.fromHtml(beforeResultText));
        binding.tvBeforeGlucoseResult.setText(Html.fromHtml(beforeResultText));

        binding.tvCompareAfter.setText(compareGaGlucose);
        binding.tvCompareAfterGlucose.setText(compareGaGlucose);
        binding.tvAfterResult.setText(Html.fromHtml(afterResultText));
        binding.tvAfterGlucoseResult.setText(Html.fromHtml(afterResultText));
    }

    private void setVarSysGrapth(final List<Map<String, String>> varSysListResultGraphData,
                                 int beforeSys,
                                 int afterSys) {
        final GraphVarOneStick graphUtil = new GraphVarOneStick(context, changeUI);
        int tempMaxSys = 0;
        if (beforeSys > afterSys) {
            tempMaxSys = beforeSys;
        } else {
            tempMaxSys = afterSys;
        }

        final int finalMaxSys = tempMaxSys;
        binding.llBpSysGraph.post(new Runnable() {

            @Override
            public void run() {
                binding.llBpSysGraph.removeAllViews();
                binding.llBpSysGraph.addView(graphUtil);
                graphUtil.setStayDataX(true);
                graphUtil.setCustomDataX(true);
                graphUtil.setBottomWidely(false);
                graphUtil.setCustomDataX(customDateType);
                graphUtil.setLocalizationTYPE(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)));
                graphUtil.setInitialize(GraphVarOneStick.LINE,
                                        7,
                                        finalMaxSys + 40,
                                        0,
                                        5,
                                        0,
                                        0,
                                        binding.llBpSysGraph.getWidth(),
                                        binding.llBpSysGraph.getHeight());

                //                    graphUtil.addRange(90, 120, Color.parseColor("#ffefed"));
                //                    graphUtil.addRange(60, 80, Color.parseColor("#ebf5f7"));

                graphUtil.addObjectList(varSysListResultGraphData,
                                        ManagerConstants.ResponseParamName.SYS,
                                        ManagerConstants.ResponseParamName.SYS,
                                        ManagerConstants.ResponseParamName.RECORD_DT,
                                        Color.parseColor("#2e89e9"),
                                        R.drawable.bp_graph_double_dot_blue,
                                        Color.parseColor("#3d007dee"));

                graphUtil.addObjectList(varSysListResultGraphData,
                                        ManagerConstants.ResponseParamName.SYS,
                                        ManagerConstants.ResponseParamName.SYS,
                                        ManagerConstants.ResponseParamName.RECORD_DT,
                                        Color.parseColor("#2e89e9"),
                                        R.drawable.bp_graph_double_dot_blue,
                                        Color.parseColor("#3d007dee"));
            }
        });
    }

    private void setVarDiaGrapth(final List<Map<String, String>> varDiaListResultGraphData,
                                 int beforeDia,
                                 int afterDia) {
        final GraphVarOneStick graphUtil = new GraphVarOneStick(context, changeUI);
        int tempMaxDia = 0;
        if (beforeDia > afterDia) {
            tempMaxDia = beforeDia;
        } else {
            tempMaxDia = afterDia;
        }

        final int finalMaxDia = tempMaxDia;
        binding.llBpDiaGraph.post(new Runnable() {

            @Override
            public void run() {
                binding.llBpDiaGraph.removeAllViews();
                binding.llBpDiaGraph.addView(graphUtil);
                graphUtil.setStayDataX(true);
                graphUtil.setCustomDataX(true);
                graphUtil.setBottomWidely(false);
                graphUtil.setCustomDataX(customDateType);
                graphUtil.setLocalizationTYPE(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)));
                graphUtil.setInitialize(GraphVarOneStick.LINE,
                                        7,
                                        finalMaxDia + 40,
                                        0,
                                        5,
                                        0,
                                        0,
                                        binding.llBpDiaGraph.getWidth(),
                                        binding.llBpDiaGraph.getHeight());

                //                    graphUtil.addRange(90, 120, Color.parseColor("#ffefed"));
                //                    graphUtil.addRange(60, 80, Color.parseColor("#ebf5f7"));

                graphUtil.addObjectList(varDiaListResultGraphData,
                                        ManagerConstants.ResponseParamName.DIA,
                                        ManagerConstants.ResponseParamName.DIA,
                                        ManagerConstants.ResponseParamName.RECORD_DT,
                                        Color.parseColor("#f46a2b"),
                                        R.drawable.bp_graph_double_dot_red,
                                        Color.parseColor("#3df3692b"));

                graphUtil.addObjectList(varDiaListResultGraphData,
                                        ManagerConstants.ResponseParamName.DIA,
                                        ManagerConstants.ResponseParamName.DIA,
                                        ManagerConstants.ResponseParamName.RECORD_DT,
                                        Color.parseColor("#f46a2b"),
                                        R.drawable.bp_graph_double_dot_red,
                                        Color.parseColor("#3df3692b"));
            }
        });
    }

    /**
     * 체중 그래프
     *
     * @param varWeightListResultGraphData
     * @param beforeWeight
     * @param afterWeight
     */
    private void setVarWeightGrapth(final List<Map<String, String>> varWeightListResultGraphData,
                                    float beforeWeight,
                                    float afterWeight) {
        final GraphVarOneStick graphUtil = new GraphVarOneStick(context, changeUI);
        float tempMaxWeight = 0;
        if (beforeWeight > afterWeight) {
            tempMaxWeight = beforeWeight;
        } else {
            tempMaxWeight = afterWeight;
        }

        final float finalMaxWeight = tempMaxWeight;
        binding.llWsWeightGraph.post(new Runnable() {

            @Override
            public void run() {
                binding.llWsWeightGraph.removeAllViews();
                binding.llWsWeightGraph.addView(graphUtil);
                graphUtil.setStayDataX(true);
                graphUtil.setCustomDataX(true);
                graphUtil.setBottomWidely(false);
                graphUtil.setCustomDataX(customDateType);
                graphUtil.setLocalizationTYPE(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)));
                graphUtil.setInitialize(GraphVarOneStick.LINE,
                                        7,
                                        (int)finalMaxWeight + 40,
                                        0,
                                        5,
                                        0,
                                        0,
                                        binding.llWsWeightGraph.getWidth(),
                                        binding.llWsWeightGraph.getHeight());

                //                    graphUtil.addRange(90, 120, Color.parseColor("#ffefed"));
                //                    graphUtil.addRange(60, 80, Color.parseColor("#ebf5f7"));

                graphUtil.addObjectList(varWeightListResultGraphData,
                                        ManagerConstants.ResponseParamName.WS_WEIGHT,
                                        ManagerConstants.ResponseParamName.WS_WEIGHT,
                                        ManagerConstants.ResponseParamName.RECORD_DT,
                                        Color.parseColor("#00acc8"),
                                        R.drawable.weight_graph_double_dot_blue,
                                        Color.parseColor("#3d00acc8"));

                graphUtil.addObjectList(varWeightListResultGraphData,
                                        ManagerConstants.ResponseParamName.WS_WEIGHT,
                                        ManagerConstants.ResponseParamName.WS_WEIGHT,
                                        ManagerConstants.ResponseParamName.RECORD_DT,
                                        Color.parseColor("#00acc8"),
                                        R.drawable.weight_graph_double_dot_blue,
                                        Color.parseColor("#3d00acc8"));
            }
        });
    }

    /**
     * Bmi 그래프
     *
     * @param varBmiListResultGraphData
     * @param beforeBmi
     * @param afterBmi
     */
    private void setVarBmiGrapth(final List<Map<String, String>> varBmiListResultGraphData,
                                 float beforeBmi,
                                 float afterBmi) {
        final GraphVarOneStick graphUtil = new GraphVarOneStick(context, changeUI);
        float tempMaxDia = 0;
        if (beforeBmi > afterBmi) {
            tempMaxDia = beforeBmi;
        } else {
            tempMaxDia = afterBmi;
        }

        final float finalMaxDia = tempMaxDia;
        binding.llWsBmiGraph.post(new Runnable() {

            @Override
            public void run() {
                binding.llWsBmiGraph.removeAllViews();
                binding.llWsBmiGraph.addView(graphUtil);
                graphUtil.setStayDataX(true);
                graphUtil.setCustomDataX(true);
                graphUtil.setBottomWidely(false);
                graphUtil.setCustomDataX(customDateType);
                graphUtil.setLocalizationTYPE(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)));
                graphUtil.setInitialize(GraphVarOneStick.LINE,
                                        7,
                                        (int)finalMaxDia + 40,
                                        0,
                                        5,
                                        0,
                                        0,
                                        binding.llWsBmiGraph.getWidth(),
                                        binding.llWsBmiGraph.getHeight());

                //                    graphUtil.addRange(90, 120, Color.parseColor("#ffefed"));
                //                    graphUtil.addRange(60, 80, Color.parseColor("#ebf5f7"));

                graphUtil.addObjectList(varBmiListResultGraphData,
                                        ManagerConstants.ResponseParamName.WS_BMI,
                                        ManagerConstants.ResponseParamName.WS_BMI,
                                        ManagerConstants.ResponseParamName.RECORD_DT,
                                        Color.parseColor("#9e71e0"),
                                        R.drawable.weight_graph_double_dot_purple,
                                        Color.parseColor("#3d9d71e0"));

                graphUtil.addObjectList(varBmiListResultGraphData,
                                        ManagerConstants.ResponseParamName.WS_BMI,
                                        ManagerConstants.ResponseParamName.WS_BMI,
                                        ManagerConstants.ResponseParamName.RECORD_DT,
                                        Color.parseColor("#9e71e0"),
                                        R.drawable.weight_graph_double_dot_purple,
                                        Color.parseColor("#3d9d71e0"));
            }
        });
    }

    /**
     * 식전 혈당 그래프
     *
     * @param varBgGlucoseListResultGraphData
     * @param beforeGbGlucose
     * @param afterGbGlucose
     */
    private void setVarGbGrapth(final LinearLayout layout,
                                final List<Map<String, String>> varBgGlucoseListResultGraphData,
                                float beforeGbGlucose,
                                float afterGbGlucose) {
        final GraphVarOneStick graphUtil = new GraphVarOneStick(context, changeUI);
        float tempMaxDia = 0;
        if (beforeGbGlucose > afterGbGlucose) {
            tempMaxDia = beforeGbGlucose;
        } else {
            tempMaxDia = afterGbGlucose;
        }

        final float finalMaxDia = tempMaxDia;
        layout.post(new Runnable() {

            @Override
            public void run() {
                layout.removeAllViews();
                layout.addView(graphUtil);
                graphUtil.setStayDataX(true);
                graphUtil.setCustomDataX(true);
                graphUtil.setBottomWidely(false);
                graphUtil.setCustomDataX(customDateType);
                graphUtil.setLocalizationTYPE(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)));
                graphUtil.setInitialize(GraphVarOneStick.LINE,
                                        7,
                                        (int)finalMaxDia + 40,
                                        0,
                                        5,
                                        0,
                                        0,
                                        layout.getWidth(),
                                        layout.getHeight());

                //                    graphUtil.addRange(90, 120, Color.parseColor("#ffefed"));
                //                    graphUtil.addRange(60, 80, Color.parseColor("#ebf5f7"));

                graphUtil.addObjectList(varBgGlucoseListResultGraphData,
                                        ManagerConstants.ResponseParamName.GLUCOSE,
                                        ManagerConstants.ResponseParamName.GLUCOSE,
                                        ManagerConstants.ResponseParamName.RECORD_DT,
                                        Color.parseColor("#7cb3f0"),
                                        R.drawable.blood_suger_grap_double_dot_blue,
                                        Color.parseColor("#3d7bb3f0"));

                graphUtil.addObjectList(varBgGlucoseListResultGraphData,
                                        ManagerConstants.ResponseParamName.GLUCOSE,
                                        ManagerConstants.ResponseParamName.GLUCOSE,
                                        ManagerConstants.ResponseParamName.RECORD_DT,
                                        Color.parseColor("#7cb3f0"),
                                        R.drawable.blood_suger_grap_double_dot_blue,
                                        Color.parseColor("#3d7bb3f0"));
            }
        });
    }

    /**
     * 식후 혈당 그래프
     *
     * @param layout
     * @param varBaGlucoseListResultGraphData
     * @param beforeGaGlucose
     * @param afterGaGlucose
     */
    private void setVarGaGrapth(final LinearLayout layout,
                                final List<Map<String, String>> varBaGlucoseListResultGraphData,
                                float beforeGaGlucose,
                                float afterGaGlucose) {
        final GraphVarOneStick graphUtil = new GraphVarOneStick(context, changeUI);
        float tempMaxDia = 0;
        if (beforeGaGlucose > afterGaGlucose) {
            tempMaxDia = beforeGaGlucose;
        } else {
            tempMaxDia = afterGaGlucose;
        }

        final float finalMaxDia = tempMaxDia;
        layout.post(new Runnable() {

            @Override
            public void run() {
                layout.removeAllViews();
                layout.addView(graphUtil);
                graphUtil.setStayDataX(true);
                graphUtil.setCustomDataX(true);
                graphUtil.setBottomWidely(false);
                graphUtil.setCustomDataX(customDateType);
                graphUtil.setLocalizationTYPE(ManagerUtil.getLocalizationType(PreferenceUtil.getLanguage(context)));
                graphUtil.setInitialize(GraphVarOneStick.LINE,
                                        7,
                                        (int)finalMaxDia + 40,
                                        0,
                                        5,
                                        0,
                                        0,
                                        layout.getWidth(),
                                        layout.getHeight());

                //                    graphUtil.addRange(90, 120, Color.parseColor("#ffefed"));
                //                    graphUtil.addRange(60, 80, Color.parseColor("#ebf5f7"));

                graphUtil.addObjectList(varBaGlucoseListResultGraphData,
                                        ManagerConstants.ResponseParamName.GLUCOSE,
                                        ManagerConstants.ResponseParamName.GLUCOSE,
                                        ManagerConstants.ResponseParamName.RECORD_DT,
                                        Color.parseColor("#f0a115"),
                                        R.drawable.blood_suger_grap_double_dot_orange,
                                        Color.parseColor("#3df0a115"));

                graphUtil.addObjectList(varBaGlucoseListResultGraphData,
                                        ManagerConstants.ResponseParamName.GLUCOSE,
                                        ManagerConstants.ResponseParamName.GLUCOSE,
                                        ManagerConstants.ResponseParamName.RECORD_DT,
                                        Color.parseColor("#f0a115"),
                                        R.drawable.blood_suger_grap_double_dot_orange,
                                        Color.parseColor("#3df0a115"));
            }
        });
    }

    /**
     * 그래픽 컨트롤 변경하는 핸들러
     */
    private final Handler changeUI = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            int index = -1;

            switch (msg.what) {

                case GraphVarOneStick.MESSAGE_WHAT_RESULT_TWO:

                    // 현재 인덱스를 저장

                    index = msg.getData().getInt(ManagerConstants.Graph.INDEX);

                    break;

            }
        }
    };

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_bp_input:
                //혈압 입력화면 이동
                intent = new Intent(context, InputBPActivity.class);
                activity.startActivityForResult(intent, CommonConstants.RequestCode.ACTIVITY_REQUEST_REPORT_BP_INPUT);
                break;
            case R.id.btn_ws_input:
                //체중 입력화면 이동
                intent = new Intent(context, InputWeightActivity.class);
                activity.startActivityForResult(intent, CommonConstants.RequestCode.ACTIVITY_REQUEST_REPORT_WS_INPUT);
                break;

            case R.id.btn_after_glucose_input:
                //식후 입력화면 이동
                intent = new Intent(context, InputGlucoseActivity.class);
                intent.putExtra(ManagerConstants.RequestParamName.GLUCOSE_MEAL, ManagerConstants.EatType.GLUCOSE_AFTER);
                activity.startActivityForResult(intent,
                                                CommonConstants.RequestCode.ACTIVITY_REQUEST_REPORT_GLUCOSE_INPUT);
                break;

            case R.id.ll_report_share_btn:
                //레포트 공유화면 이동
                intent = new Intent(context, ReportShareActivity.class);
                intent.putExtra("searchDay", searchDay);
                activity.startActivity(intent);
                break;

            case R.id.btn_before_glucose_input:
                intent = new Intent(context, InputGlucoseActivity.class);
                intent.putExtra(ManagerConstants.RequestParamName.GLUCOSE_MEAL, ManagerConstants.EatType.GLUCOSE_BEFORE);
                activity.startActivityForResult(intent,
                        CommonConstants.RequestCode.ACTIVITY_REQUEST_REPORT_GLUCOSE_INPUT);
                break;
            case R.id.btn_bg_input:
                //혈당 입력화면 이동 (defult가 식전)
                intent = new Intent(context, InputGlucoseActivity.class);
                activity.startActivityForResult(intent,
                                                CommonConstants.RequestCode.ACTIVITY_REQUEST_REPORT_GLUCOSE_INPUT);
                break;
            case R.id.ll_network_try_again:
                context.sendBroadcast(new Intent("kr.co.openit.bpdiary.report"));
                break;
        }
    }
}
