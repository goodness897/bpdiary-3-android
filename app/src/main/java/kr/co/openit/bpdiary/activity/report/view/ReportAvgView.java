package kr.co.openit.bpdiary.activity.report.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.openit.bpdiary.BPDiaryApplication;
import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.bp.InputBPActivity;
import kr.co.openit.bpdiary.activity.glucose.InputGlucoseActivity;
import kr.co.openit.bpdiary.activity.main.MainActivity;
import kr.co.openit.bpdiary.activity.report.ReportShareActivity;
import kr.co.openit.bpdiary.activity.weight.InputWeightActivity;
import kr.co.openit.bpdiary.common.constants.CommonConstants;
import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;
import kr.co.openit.bpdiary.databinding.ViewReportAverageBinding;
import kr.co.openit.bpdiary.utils.AnalyticsUtil;
import kr.co.openit.bpdiary.utils.HealthcareUtil;
import kr.co.openit.bpdiary.utils.ManagerUtil;
import kr.co.openit.bpdiary.utils.PreferenceUtil;


/**
 * Created by Huiseop on 2016-09-22.
 */

public class ReportAvgView extends LinearLayout implements View.OnClickListener {

    private ViewReportAverageBinding binding;

    private JSONObject mListData;

    private Context context;

    private MainActivity activity;

    private String searchDay;

    public ReportAvgView(Context context, MainActivity activity, JSONObject mListData, String searchDay) {
        super(context);
        this.mListData = mListData;
        this.context = context;
        this.activity = activity;
        this.searchDay = searchDay;

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
        binding = DataBindingUtil.inflate(inflater, R.layout.view_report_average, this, true);

        AnalyticsUtil.sendScene((Activity) context, "3_레포트 평균값");

        try {
            if (BPDiaryApplication.isNetworkState(context)) {
                binding.llIsNetworkFalse.setVisibility(View.GONE);
                setSearchDayText();

                boolean isPAvgListAllNull = false;
                /**
                 * 혈압 평균값
                 */
                if (mListData != null) {
                    if (mListData.has(ManagerConstants.ResponseParamName.P_AVG_LIST)) {
                        JSONArray pAvgList = mListData.getJSONArray(ManagerConstants.ResponseParamName.P_AVG_LIST);
                        if (pAvgList.length() > 0) {
                            JSONObject pAvgListResult;
                            for (int i = 0; i < pAvgList.length(); i++) {
                                pAvgListResult = pAvgList.getJSONObject(i);
                                if (i == 0) {
                                    if (ManagerUtil.mapDataNullCheck(pAvgListResult.get(ManagerConstants.ResponseParamName.SYS))) {
                                        isPAvgListAllNull = false;
                                        binding.tvMaxSys.setText(ManagerUtil.oneDecimalPlaceDrop(pAvgListResult.getString(ManagerConstants.ResponseParamName.SYS)));
                                    } else {
                                        isPAvgListAllNull = true;
                                        binding.tvMaxSys.setText("-");
                                    }
                                    if (ManagerUtil.mapDataNullCheck(pAvgListResult.get(ManagerConstants.ResponseParamName.DIA))) {
                                        isPAvgListAllNull = false;
                                        binding.tvMaxDia.setText(ManagerUtil.oneDecimalPlaceDrop(pAvgListResult.getString(ManagerConstants.ResponseParamName.DIA)));
                                    } else {
                                        binding.tvMaxDia.setText("-");
                                    }

                                } else if (i == 1) {
                                    if (ManagerUtil.mapDataNullCheck(pAvgListResult.get(ManagerConstants.ResponseParamName.SYS))) {
                                        isPAvgListAllNull = false;
                                        binding.tvMinSys.setText(ManagerUtil.oneDecimalPlaceDrop(pAvgListResult.getString(ManagerConstants.ResponseParamName.SYS)));
                                    } else {
                                        binding.tvMinSys.setText("-");
                                    }
                                    if (ManagerUtil.mapDataNullCheck(pAvgListResult.get(ManagerConstants.ResponseParamName.DIA))) {
                                        isPAvgListAllNull = false;
                                        binding.tvMinDia.setText(ManagerUtil.oneDecimalPlaceDrop(pAvgListResult.getString(ManagerConstants.ResponseParamName.DIA)));
                                    } else {
                                        binding.tvMinDia.setText("-");
                                    }
                                } else if (i == 2) {
                                    if (ManagerUtil.mapDataNullCheck(pAvgListResult.get(ManagerConstants.ResponseParamName.SYS))) {
                                        isPAvgListAllNull = false;
                                        binding.tvBpSys.setText(ManagerUtil.oneDecimalPlaceDrop(pAvgListResult.getString(ManagerConstants.ResponseParamName.SYS)));
                                    } else {
                                        binding.tvBpSys.setText("-");
                                    }
                                    if (ManagerUtil.mapDataNullCheck(pAvgListResult.get(ManagerConstants.ResponseParamName.DIA))) {
                                        isPAvgListAllNull = false;
                                        binding.tvBpDia.setText(ManagerUtil.oneDecimalPlaceDrop(pAvgListResult.getString(ManagerConstants.ResponseParamName.DIA)));
                                    } else {
                                        binding.tvBpDia.setText("-");
                                    }
                                    if (ManagerUtil.mapDataNullCheck(pAvgListResult.get(ManagerConstants.ResponseParamName.SYS))
                                            && ManagerUtil.mapDataNullCheck(pAvgListResult.get(ManagerConstants.ResponseParamName.DIA))) {
                                        isPAvgListAllNull = false;
                                        String bpType =
                                                HealthcareUtil.getBloodPressureType(context, pAvgListResult.getString(ManagerConstants.ResponseParamName.SYS),
                                                        pAvgListResult.getString(ManagerConstants.ResponseParamName.DIA));

                                        if (HealthcareConstants.BloodPressureState.BP_LOW.equals(bpType)) {
                                            //저혈압
                                            binding.ccvBpAverage.setCircleColor(getResources().getColor(R.color.color_76adef));
                                            binding.tvBpStep.setText(getResources().getString(R.string.bp_main_txt_status_low));
                                        } else if (HealthcareConstants.BloodPressureState.BP_NORMAL.equals(bpType)) {
                                            //정상
                                            binding.ccvBpAverage.setCircleColor(getResources().getColor(R.color.color_ace03b));
                                            binding.tvBpStep.setText(getResources().getString(R.string.bp_main_txt_status_normal));
                                        } else if (HealthcareConstants.BloodPressureState.BP_APPROACH.equals(bpType)) {
                                            //고혈압 전단계
                                            binding.ccvBpAverage.setCircleColor(getResources().getColor(R.color.color_f4dc2e));
                                            binding.tvBpStep.setText(getResources().getString(R.string.bp_main_txt_status_approach));
                                        } else if (HealthcareConstants.BloodPressureState.BP_HIGH_ONE.equals(bpType)) {
                                            //1기 고혈압
                                            binding.ccvBpAverage.setCircleColor(getResources().getColor(R.color.color_f49b40));
                                            binding.tvBpStep.setText(getResources().getString(R.string.bp_main_txt_status_high_one));
                                        } else if (HealthcareConstants.BloodPressureState.BP_HIGH_TWO.equals(bpType)) {
                                            //2기 고혈압
                                            binding.ccvBpAverage.setCircleColor(getResources().getColor(R.color.color_ee4633));
                                            binding.tvBpStep.setText(getResources().getString(R.string.bp_main_txt_status_high_two));
                                        } else if (HealthcareConstants.BloodPressureState.BP_VERY_HIGH.equals(bpType)) {
                                            //높은 고혈압
                                            binding.ccvBpAverage.setCircleColor(getResources().getColor(R.color.color_c8084c));
                                            binding.tvBpStep.setText(getResources().getString(R.string.bp_main_txt_status_very_high));
                                        } else {
                                            //기본 정상
                                            binding.ccvBpAverage.setCircleColor(getResources().getColor(R.color.color_ace03b));
                                            binding.tvBpStep.setText(getResources().getString(R.string.bp_main_txt_status_normal));
                                        }
                                    }
                                } else if (i == 3) {
                                    if (ManagerUtil.mapDataNullCheck(pAvgListResult.get(ManagerConstants.ResponseParamName.SYS))) {
                                        isPAvgListAllNull = false;
                                        binding.tvAmSys.setText(ManagerUtil.oneDecimalPlaceDrop(pAvgListResult.getString(ManagerConstants.ResponseParamName.SYS)));
                                    } else {
                                        binding.tvAmSys.setText("-");
                                    }
                                    if (ManagerUtil.mapDataNullCheck(pAvgListResult.get(ManagerConstants.ResponseParamName.DIA))) {
                                        isPAvgListAllNull = false;
                                        binding.tvAmDia.setText(ManagerUtil.oneDecimalPlaceDrop(pAvgListResult.getString(ManagerConstants.ResponseParamName.DIA)));
                                    } else {
                                        binding.tvAmDia.setText("-");
                                    }
                                } else if (i == 4) {
                                    if (ManagerUtil.mapDataNullCheck(pAvgListResult.get(ManagerConstants.ResponseParamName.SYS))) {
                                        isPAvgListAllNull = false;
                                        binding.tvPmSys.setText(ManagerUtil.oneDecimalPlaceDrop(pAvgListResult.getString(ManagerConstants.ResponseParamName.SYS)));
                                    } else {
                                        binding.tvPmSys.setText("-");
                                    }
                                    if (ManagerUtil.mapDataNullCheck(pAvgListResult.get(ManagerConstants.ResponseParamName.DIA))) {
                                        isPAvgListAllNull = false;
                                        binding.tvPmDia.setText(ManagerUtil.oneDecimalPlaceDrop(pAvgListResult.getString(ManagerConstants.ResponseParamName.DIA)));
                                    } else {
                                        binding.tvPmDia.setText("-");
                                    }
                                }
                            }
                        } else {
                            isPAvgListAllNull = true;
                        }
                    } else {
                        isPAvgListAllNull = true;
                    }
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
                 * 체중 평균값
                 */
                if (mListData != null) {
                    if (mListData.has(ManagerConstants.ResponseParamName.W_AVG_LIST)) {
                        JSONArray wAvgList = mListData.getJSONArray(ManagerConstants.ResponseParamName.W_AVG_LIST);
                        if (wAvgList.length() > 0) {
                            if (ManagerConstants.Unit.KG.equals(PreferenceUtil.getWeightUnit(context))) {
                                binding.tvWeightUnit.setText(R.string.weight_kg);
                                binding.tvTableWeightUnit.setText(R.string.weight_kg_unit);
                            } else {
                                binding.tvWeightUnit.setText(R.string.weight_lbs);
                                binding.tvTableWeightUnit.setText(R.string.weight_lbs_unit);
                            }
                            JSONObject wAvgListResult;
                            for (int i = 0; i < wAvgList.length(); i++) {
                                wAvgListResult = wAvgList.getJSONObject(i);
                                if (i == 0) {
                                    if (ManagerUtil.mapDataNullCheck(wAvgListResult.get(ManagerConstants.ResponseParamName.WS_WEIGHT))) {
                                        isWAvgListAllNull = false;
                                        binding.tvMaxWeight.setText(ManagerUtil.oneDecimalPlaceDrop(wAvgListResult.getString(ManagerConstants.ResponseParamName.WS_WEIGHT)));
                                    } else {
                                        isWAvgListAllNull = true;
                                        binding.tvMaxWeight.setText("-");
                                    }
                                    if (ManagerUtil.mapDataNullCheck(wAvgListResult.get(ManagerConstants.ResponseParamName.WS_BMI))) {
                                        isWAvgListAllNull = false;
                                        binding.tvMaxBmi.setText(ManagerUtil.oneDecimalPlaceDrop(wAvgListResult.getString(ManagerConstants.ResponseParamName.WS_BMI)));
                                    } else {
                                        binding.tvMaxBmi.setText("-");
                                    }

                                } else if (i == 1) {
                                    if (ManagerUtil.mapDataNullCheck(wAvgListResult.get(ManagerConstants.ResponseParamName.WS_WEIGHT))) {
                                        isWAvgListAllNull = false;
                                        binding.tvMinWeight.setText(ManagerUtil.oneDecimalPlaceDrop(wAvgListResult.getString(ManagerConstants.ResponseParamName.WS_WEIGHT)));
                                    } else {
                                        binding.tvMinWeight.setText("-");
                                    }
                                    if (ManagerUtil.mapDataNullCheck(wAvgListResult.get(ManagerConstants.ResponseParamName.WS_BMI))) {
                                        isWAvgListAllNull = false;
                                        binding.tvMinBmi.setText(ManagerUtil.oneDecimalPlaceDrop(wAvgListResult.getString(ManagerConstants.ResponseParamName.WS_BMI)));
                                    } else {
                                        binding.tvMinBmi.setText("-");
                                    }
                                } else if (i == 2) {
                                    if (ManagerUtil.mapDataNullCheck(wAvgListResult.get(ManagerConstants.ResponseParamName.WS_WEIGHT))) {
                                        isWAvgListAllNull = false;
                                        binding.tvWsWeight.setText(ManagerUtil.oneDecimalPlaceDrop(wAvgListResult.getString(ManagerConstants.ResponseParamName.WS_WEIGHT)));
                                    } else {
                                        binding.tvWsWeight.setText("-");
                                    }
                                    if (ManagerUtil.mapDataNullCheck(wAvgListResult.get(ManagerConstants.ResponseParamName.WS_BMI))) {
                                        isWAvgListAllNull = false;
                                        binding.tvWsBmi.setText(ManagerUtil.oneDecimalPlaceDrop(wAvgListResult.getString(ManagerConstants.ResponseParamName.WS_BMI)));

                                        String strBmiState =
                                                HealthcareUtil.getWeighingScaleBmiType(wAvgListResult.getString(ManagerConstants.ResponseParamName.WS_BMI));

                                        if (HealthcareConstants.WeighingScaleState.WS_LOW.equals(strBmiState)) {
                                            //저체중
                                            binding.ccvWeightAverage.setCircleColor(getResources().getColor(R.color.color_e9da64));
                                            binding.tvWeightStep.setText(getResources().getString(R.string.weighing_scale_main_txt_status_low));
                                        } else if (HealthcareConstants.WeighingScaleState.WS_NORMAL.equals(strBmiState)) {
                                            //정상
                                            binding.ccvWeightAverage.setCircleColor(getResources().getColor(R.color.color_82d589));
                                            binding.tvWeightStep.setText(getResources().getString(R.string.weighing_scale_main_txt_status_normal));
                                        } else if (HealthcareConstants.WeighingScaleState.WS_OVER_WEIGHT.equals(strBmiState)) {
                                            //과체중
                                            binding.ccvWeightAverage.setCircleColor(getResources().getColor(R.color.color_6aa6e9));
                                            binding.tvWeightStep.setText(getResources().getString(R.string.weighing_scale_main_txt_status_approach));
                                        } else if (HealthcareConstants.WeighingScaleState.WS_OBESITY.equals(strBmiState)) {
                                            //비만
                                            binding.ccvWeightAverage.setCircleColor(getResources().getColor(R.color.color_a180de));
                                            binding.tvWeightStep.setText(getResources().getString(R.string.weighing_scale_main_txt_status_obesity));
                                        } else if (HealthcareConstants.WeighingScaleState.WS_VERY_OBESITY.equals(strBmiState)) {
                                            //고도비만
                                            binding.ccvWeightAverage.setCircleColor(getResources().getColor(R.color.color_ed5967));
                                            binding.tvWeightStep.setText(getResources().getString(R.string.weighing_scale_main_txt_status_very_obesity));
                                        } else {
                                            //기본 정상
                                            binding.ccvWeightAverage.setCircleColor(getResources().getColor(R.color.color_82d589));
                                            binding.tvWeightStep.setText(getResources().getString(R.string.weighing_scale_main_txt_status_normal));
                                        }
                                    } else {
                                        binding.tvWsBmi.setText("-");
                                    }
                                }
                            }
                        } else {
                            isWAvgListAllNull = true;
                        }
                    } else {
                        isWAvgListAllNull = true;
                    }
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
                 * 혈당 평균값
                 */
                if (mListData != null) {
                    if (mListData.has(ManagerConstants.ResponseParamName.G_AVG_LIST)) {
                        if (ManagerUtil.mapDataNullCheck(mListData.get(ManagerConstants.ResponseParamName.G_AVG_LIST))) {
                            JSONArray gAvgList = mListData.getJSONArray(ManagerConstants.ResponseParamName.G_AVG_LIST);
                            if (gAvgList.length() > 0) {
                                JSONObject gAvgListResult;
                                for (int i = 0; i < gAvgList.length(); i++) {
                                    gAvgListResult = gAvgList.getJSONObject(i);
                                    if (i == 0) {
                                        if (ManagerUtil.mapDataNullCheck(gAvgListResult.get(ManagerConstants.ResponseParamName.B_GLUCOSE))) {
                                            isGbData = true;
                                            binding.tvMaxBeforeGlucose.setText(ManagerUtil.oneDecimalPlaceDrop(gAvgListResult.getString(ManagerConstants.ResponseParamName.B_GLUCOSE)));
                                            binding.tvMaxSingleBeforeGlucose.setText(ManagerUtil.oneDecimalPlaceDrop(gAvgListResult.getString(ManagerConstants.ResponseParamName.B_GLUCOSE)));
                                        } else {
                                            isGbData = false;
                                            binding.tvMaxBeforeGlucose.setText("-");
                                            binding.tvMaxSingleBeforeGlucose.setText("-");
                                        }
                                        if (ManagerUtil.mapDataNullCheck(gAvgListResult.get(ManagerConstants.ResponseParamName.A_GLUCOSE))) {
                                            isGaData = true;
                                            binding.tvMaxAfterGlucose.setText(ManagerUtil.oneDecimalPlaceDrop(gAvgListResult.getString(ManagerConstants.ResponseParamName.A_GLUCOSE)));
                                            binding.tvMaxSingleAfterGlucose.setText(ManagerUtil.oneDecimalPlaceDrop(gAvgListResult.getString(ManagerConstants.ResponseParamName.A_GLUCOSE)));
                                        } else {
                                            isGaData = false;
                                            binding.tvMaxAfterGlucose.setText("-");
                                            binding.tvMaxSingleAfterGlucose.setText("-");
                                        }
                                    } else if (i == 1) {
                                        if (ManagerUtil.mapDataNullCheck(gAvgListResult.get(ManagerConstants.ResponseParamName.B_GLUCOSE))) {
                                            isGbData = true;
                                            binding.tvMinBeforeGlucose.setText(ManagerUtil.oneDecimalPlaceDrop(gAvgListResult.getString(ManagerConstants.ResponseParamName.B_GLUCOSE)));
                                            binding.tvMinSingleBeforeGlucose.setText(ManagerUtil.oneDecimalPlaceDrop(gAvgListResult.getString(ManagerConstants.ResponseParamName.B_GLUCOSE)));
                                        } else {
                                            binding.tvMinBeforeGlucose.setText("-");
                                            binding.tvMinSingleBeforeGlucose.setText("-");
                                        }
                                        if (ManagerUtil.mapDataNullCheck(gAvgListResult.get(ManagerConstants.ResponseParamName.A_GLUCOSE))) {
                                            isGaData = true;
                                            binding.tvMinAfterGlucose.setText(ManagerUtil.oneDecimalPlaceDrop(gAvgListResult.getString(ManagerConstants.ResponseParamName.A_GLUCOSE)));
                                            binding.tvMinSingleAfterGlucose.setText(ManagerUtil.oneDecimalPlaceDrop(gAvgListResult.getString(ManagerConstants.ResponseParamName.A_GLUCOSE)));
                                        } else {
                                            binding.tvMinAfterGlucose.setText("-");
                                            binding.tvMinSingleAfterGlucose.setText("-");
                                        }
                                    } else if (i == 2) {
                                        String strType;
                                        if (ManagerUtil.mapDataNullCheck(gAvgListResult.get(ManagerConstants.ResponseParamName.B_GLUCOSE))) {
                                            isGbData = true;
                                            binding.tvBgBeforeGlucose.setText(ManagerUtil.oneDecimalPlaceDrop(gAvgListResult.getString(ManagerConstants.ResponseParamName.B_GLUCOSE)));
                                            binding.tvBgSingleBeforeGlucose.setText(ManagerUtil.oneDecimalPlaceDrop(gAvgListResult.getString(ManagerConstants.ResponseParamName.B_GLUCOSE)));
                                            if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(context))) {
                                                strType =
                                                        HealthcareUtil.getGlucoseType(context, gAvgListResult.getString(ManagerConstants.ResponseParamName.B_GLUCOSE),
                                                                ManagerConstants.EatType.GLUCOSE_BEFORE);
                                                binding.tvBeforeGlucoseUnit.setText(R.string.glucose_graph_mg_dl);
                                                binding.tvSingleBeforeGlucoseUnit.setText(R.string.glucose_graph_mg_dl);
                                                binding.tvTableBeforeGlucoseUnit.setText(R.string.glucose_before_meal_mg_dl_unit);
                                                binding.tvSingleTableBeforeGlucoseUnit.setText(R.string.glucose_before_meal_mg_dl_unit);
                                            } else {
                                                strType =
                                                        HealthcareUtil.getGlucoseType(context, ManagerUtil.mmolToMg(gAvgListResult.getString(ManagerConstants.ResponseParamName.B_GLUCOSE)),
                                                                ManagerConstants.EatType.GLUCOSE_BEFORE);
                                                binding.tvBeforeGlucoseUnit.setText(R.string.glucose_graph_mmol);
                                                binding.tvSingleBeforeGlucoseUnit.setText(R.string.glucose_graph_mmol);
                                                binding.tvTableBeforeGlucoseUnit.setText(R.string.glucose_before_meal_mmol_unit);
                                                binding.tvSingleTableBeforeGlucoseUnit.setText(R.string.glucose_before_meal_mmol_unit);
                                            }
                                            if (HealthcareConstants.GlucoseState.GLUCOSE_LOW.equals(strType)) {
                                                //저혈당
                                                binding.ccvBeforeGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_e6ea47));
                                                binding.ccvSingleBeforeGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_e6ea47));
                                                binding.tvBeforeGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_low));
                                                binding.tvSingleBeforeGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_low));
                                            } else if (HealthcareConstants.GlucoseState.GLUCOSE_NORMAL.equals(strType)) {
                                                //정상
                                                binding.ccvBeforeGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                                                binding.ccvSingleBeforeGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                                                binding.tvBeforeGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_normal));
                                                binding.tvSingleBeforeGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_normal));
                                            } else if (HealthcareConstants.GlucoseState.GLUCOSE_OVER.equals(strType)) {
                                                //고혈당
                                                binding.ccvBeforeGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_f06515));
                                                binding.ccvSingleBeforeGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_f06515));
                                                binding.tvBeforeGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_over));
                                                binding.tvSingleBeforeGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_over));
                                            } else {
                                                //기본 정상
                                                binding.ccvBeforeGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                                                binding.ccvSingleBeforeGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                                                binding.tvBeforeGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_normal));
                                                binding.tvSingleBeforeGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_normal));
                                            }
                                        } else {
                                            binding.tvBgBeforeGlucose.setText("-");
                                            binding.tvBgSingleBeforeGlucose.setText("-");
                                        }
                                        if (ManagerUtil.mapDataNullCheck(gAvgListResult.get(ManagerConstants.ResponseParamName.A_GLUCOSE))) {
                                            isGaData = true;
                                            binding.tvBgAfterGlucose.setText(ManagerUtil.oneDecimalPlaceDrop(gAvgListResult.getString(ManagerConstants.ResponseParamName.A_GLUCOSE)));
                                            binding.tvBgSingleAfterGlucose.setText(ManagerUtil.oneDecimalPlaceDrop(gAvgListResult.getString(ManagerConstants.ResponseParamName.A_GLUCOSE)));
                                            if (ManagerConstants.Unit.MGDL.equals(PreferenceUtil.getGlucoseUnit(context))) {
                                                strType =
                                                        HealthcareUtil.getGlucoseType(context, gAvgListResult.getString(ManagerConstants.ResponseParamName.A_GLUCOSE),
                                                                ManagerConstants.EatType.GLUCOSE_AFTER);
                                                binding.tvAfterGlucoseUnit.setText(R.string.glucose_graph_mg_dl);
                                                binding.tvSingleAfterGlucoseUnit.setText(R.string.glucose_graph_mg_dl);
                                                binding.tvTableAfterGlucoseUnit.setText(R.string.glucose_after_meal_mg_dl_unit);
                                                binding.tvSingleTableAfterGlucoseUnit.setText(R.string.glucose_after_meal_mg_dl_unit);
                                            } else {
                                                strType =
                                                        HealthcareUtil.getGlucoseType(context, ManagerUtil.mmolToMg(gAvgListResult.getString(ManagerConstants.ResponseParamName.A_GLUCOSE)),
                                                                ManagerConstants.EatType.GLUCOSE_AFTER);
                                                binding.tvAfterGlucoseUnit.setText(R.string.glucose_graph_mmol);
                                                binding.tvSingleAfterGlucoseUnit.setText(R.string.glucose_graph_mmol);
                                                binding.tvTableAfterGlucoseUnit.setText(R.string.glucose_after_meal_mmol_unit);
                                                binding.tvSingleTableAfterGlucoseUnit.setText(R.string.glucose_after_meal_mmol_unit);
                                            }
                                            if (HealthcareConstants.GlucoseState.GLUCOSE_LOW.equals(strType)) {
                                                //저혈당
                                                binding.ccvAfterGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_e6ea47));
                                                binding.ccvSingleAfterGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_e6ea47));
                                                binding.tvAfterGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_low));
                                                binding.tvSingleAfterGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_low));
                                            } else if (HealthcareConstants.GlucoseState.GLUCOSE_NORMAL.equals(strType)) {
                                                //정상
                                                binding.ccvAfterGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                                                binding.ccvSingleAfterGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                                                binding.tvAfterGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_normal));
                                                binding.tvSingleAfterGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_normal));
                                            } else if (HealthcareConstants.GlucoseState.GLUCOSE_OVER.equals(strType)) {
                                                //고혈당
                                                binding.ccvAfterGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_f06515));
                                                binding.ccvSingleAfterGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_f06515));
                                                binding.tvAfterGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_over));
                                                binding.tvSingleAfterGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_over));
                                            } else {
                                                //기본 정상
                                                binding.ccvAfterGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                                                binding.ccvSingleAfterGlucoseAverage.setCircleColor(getResources().getColor(R.color.color_2bccb8));
                                                binding.tvAfterGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_normal));
                                                binding.tvSingleAfterGlucoseStep.setText(getResources().getString(R.string.glucose_main_txt_status_normal));
                                            }
                                        } else {
                                            binding.tvBgAfterGlucose.setText("-");
                                        }
                                    }
                                }

                            } else {
                                isGbData = false;
                                isGaData = false;
                            }
                        } else {
                            isGbData = false;
                            isGaData = false;
                        }
                    } else {
                        isGbData = false;
                        isGaData = false;
                    }
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

                if (isPAvgListAllNull && isWAvgListAllNull && !isGbData && !isGaData) {
                    binding.llReportShareBtn.setVisibility(View.GONE);
                } else {
                    binding.llReportShareBtn.setVisibility(View.VISIBLE);
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 7일, 30일, 60일, 전체에 관한 Text 세팅
     *
     * @throws JSONException
     */
    private void setSearchDayText() throws JSONException {

        if ("7".equals(searchDay)) {
            binding.tvBpAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_seven_day),
                    getResources().getString(R.string.common_txt_avg))
                    + " :");
            binding.tvWsAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_seven_day),
                    getResources().getString(R.string.common_txt_avg))
                    + " :");
            binding.tvBeforeGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_seven_day),
                    getResources().getString(R.string.common_txt_avg)));
            binding.tvAfterGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_seven_day),
                    getResources().getString(R.string.common_txt_avg)));
            binding.tvSingleBeforeGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_seven_day),
                    getResources().getString(R.string.common_txt_avg)));
            binding.tvSingleAfterGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_seven_day),
                    getResources().getString(R.string.common_txt_avg)));

        } else if ("30".equals(searchDay)) {

            binding.tvBpAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_thirty_day),
                    getResources().getString(R.string.common_txt_avg))
                    + " :");
            binding.tvWsAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_thirty_day),
                    getResources().getString(R.string.common_txt_avg))
                    + " :");
            binding.tvBeforeGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_thirty_day),
                    getResources().getString(R.string.common_txt_avg)));
            binding.tvAfterGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_thirty_day),
                    getResources().getString(R.string.common_txt_avg)));
            binding.tvSingleBeforeGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_thirty_day),
                    getResources().getString(R.string.common_txt_avg)));
            binding.tvSingleAfterGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_thirty_day),
                    getResources().getString(R.string.common_txt_avg)));

        } else if ("60".equals(searchDay)) {

            binding.tvBpAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_sixty_day),
                    getResources().getString(R.string.common_txt_avg))
                    + " :");
            binding.tvWsAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_sixty_day),
                    getResources().getString(R.string.common_txt_avg))
                    + " :");
            binding.tvBeforeGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_sixty_day),
                    getResources().getString(R.string.common_txt_avg)));
            binding.tvAfterGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_sixty_day),
                    getResources().getString(R.string.common_txt_avg)));
            binding.tvSingleBeforeGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_sixty_day),
                    getResources().getString(R.string.common_txt_avg)));
            binding.tvSingleAfterGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_sixty_day),
                    getResources().getString(R.string.common_txt_avg)));

        } else if ("".equals(searchDay)) {
            binding.tvBpAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_all),
                    getResources().getString(R.string.common_txt_avg))
                    + " :");
            binding.tvWsAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_all),
                    getResources().getString(R.string.common_txt_avg))
                    + " :");
            binding.tvBeforeGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_all),
                    getResources().getString(R.string.common_txt_avg)));
            binding.tvAfterGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_all),
                    getResources().getString(R.string.common_txt_avg)));
            binding.tvSingleBeforeGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_all),
                    getResources().getString(R.string.common_txt_avg)));
            binding.tvSingleAfterGlucoseAverageDay.setText(String.format(getResources().getString(R.string.common_txt_avg_day),
                    getResources().getString(R.string.common_txt_all),
                    getResources().getString(R.string.common_txt_avg)));
        }
    }

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
                //식전 입력화면 이동
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
