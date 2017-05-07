package kr.co.openit.bpdiary.utils;

import android.content.Context;

import kr.co.openit.bpdiary.common.constants.HealthcareConstants;
import kr.co.openit.bpdiary.common.constants.HealthcareConstants.BloodPressureState;
import kr.co.openit.bpdiary.common.constants.HealthcareConstants.WeighingScaleState;
import kr.co.openit.bpdiary.common.constants.ManagerConstants;

/**
 * ManagerUtil
 */
public final class HealthcareUtil {

    /**
     * debugging
     */
    private static final String TAG = HealthcareUtil.class.getSimpleName();

    /**
     * 혈압 상태
     *
     * @param strSys
     * @param strDia
     * @return
     */
    public static String getBloodPressureType(Context context, String strSys, String strDia) {

        Double dSys = Double.parseDouble(strSys);
        Double dDia = Double.parseDouble(strDia);

        int preMinSys = PreferenceUtil.getBPMinSystole(context);
        int preMaxSys = PreferenceUtil.getBPMaxSystole(context);
        int preMinDia = PreferenceUtil.getBPMinDiastole(context);
        int preMaxDia = PreferenceUtil.getBPMaxDiastole(context);

        String strType = "";

        if ((dSys >= preMinSys && dSys <= preMaxSys) && (dDia >= preMinDia && dDia <= preMaxDia)) {
            //정상
            strType = BloodPressureState.BP_NORMAL;
        } else if (dSys >= 180 || dDia >= 110) {
            //높은 고혈압
            strType = BloodPressureState.BP_VERY_HIGH;
        } else if ((dSys >= 160 && dSys <= 179)) {
            if (dDia >= 100 && dDia <= 109) {
                //2기 고혈압
                strType = BloodPressureState.BP_HIGH_TWO;
            } else {
                strType = BloodPressureState.BP_HIGH_TWO;
            }
        } else if ((dSys >= 140 && dSys <= 159)) {
            if (dDia >= 100 && dDia <= 109) {
                //2기 고혈압
                strType = BloodPressureState.BP_HIGH_TWO;
            } else if (dDia >= 90 && dDia <= 99) {
                //1기 고혈압
                strType = BloodPressureState.BP_HIGH_ONE;
            } else {
                strType = BloodPressureState.BP_HIGH_ONE;
            }
        } else if (dSys >= 120 && dSys <= 139) {
            if (dDia >= 100 && dDia <= 109) {
                //2기 고혈압
                strType = BloodPressureState.BP_HIGH_TWO;
            } else if (dDia >= 90 && dDia <= 99) {
                //1기 고혈압
                strType = BloodPressureState.BP_HIGH_ONE;
            } else if (dDia >= 80 && dDia <= 89) {
                //고혈압 전단계
                strType = BloodPressureState.BP_APPROACH;
            } else {
                strType = BloodPressureState.BP_APPROACH;
            }
        } else if (dSys >= 90 && dSys <= 119) {
            if (dDia >= 100 && dDia <= 109) {
                //2기 고혈압
                strType = BloodPressureState.BP_HIGH_TWO;
            } else if (dDia >= 90 && dDia <= 99) {
                //1기 고혈압
                strType = BloodPressureState.BP_HIGH_ONE;
            } else if (dDia >= 80 && dDia <= 89) {
                //고혈압 전단계
                strType = BloodPressureState.BP_APPROACH;
            } else if (dDia >= 60 && dDia <= 79) {
                //정상
                strType = BloodPressureState.BP_NORMAL;
            } else {
                strType = BloodPressureState.BP_NORMAL;
            }
        } else {
            if (dDia >= 100 && dDia <= 109) {
                //2기 고혈압
                strType = BloodPressureState.BP_HIGH_TWO;
            } else if (dDia >= 90 && dDia <= 99) {
                //1기 고혈압
                strType = BloodPressureState.BP_HIGH_ONE;
            } else if (dDia >= 80 && dDia <= 89) {
                //고혈압 전단계
                strType = BloodPressureState.BP_APPROACH;
            } else if (dDia >= 60 && dDia <= 79) {
                //정상
                strType = BloodPressureState.BP_NORMAL;
            } else {
                //저혈압
                strType = BloodPressureState.BP_LOW;
            }
        }

        return strType;
    }

    /**
     * 체중 상태
     *
     * @param strBmi
     * @return
     */
    public static String getWeighingScaleBmiType(String strBmi) {
        double dBmi = 0.0;
        if (!strBmi.isEmpty()) {
            dBmi = Double.parseDouble(strBmi);
        }

        String strBmiType = "";

        if (dBmi < 18.5) {
            //저혈압
            strBmiType = WeighingScaleState.WS_LOW;
        } else if (dBmi >= 18.5 && dBmi < 23) {
            //정상
            strBmiType = WeighingScaleState.WS_NORMAL;
        } else if (dBmi >= 23 && dBmi < 25) {
            //과체중
            strBmiType = WeighingScaleState.WS_OVER_WEIGHT;
        } else if (dBmi >= 25 && dBmi < 30) {
            //비만
            strBmiType = WeighingScaleState.WS_OBESITY;
        } else if (dBmi >= 30) {
            //고도비만
            strBmiType = WeighingScaleState.WS_VERY_OBESITY;
        } else {
            //기본 정상
            strBmiType = WeighingScaleState.WS_NORMAL;
        }

        return strBmiType;
    }

    /**
     * Glucose 상태
     *
     * @return
     */
    public static String getGlucoseType(Context context, String strGlucose, String strMealType) {
        double dGlucose = 0.0;
        if (!strGlucose.isEmpty()) {
            dGlucose = Double.parseDouble(strGlucose);
        }

        String strGlucoseType = "";

        int minBefore = PreferenceUtil.getGlucoseMinBeforeMeal(context);
        int maxBefore = PreferenceUtil.getGlucoseMaxBeforeMeal(context);
        int minAfter = PreferenceUtil.getGlucoseMinAfterMeal(context);
        int maxAfter = PreferenceUtil.getGlucoseMaxAfterMeal(context);

        if (ManagerConstants.EatType.GLUCOSE_BEFORE.equals(strMealType)) {
            if (dGlucose < minBefore) {
                //저혈당
                strGlucoseType = HealthcareConstants.GlucoseState.GLUCOSE_LOW;
            } else if (dGlucose >= minBefore && dGlucose <= maxBefore) {
                //정상
                strGlucoseType = HealthcareConstants.GlucoseState.GLUCOSE_NORMAL;
            } else if (dGlucose > maxBefore) {
                //고혈당
                strGlucoseType = HealthcareConstants.GlucoseState.GLUCOSE_OVER;
            }

        } else {
            if (dGlucose < minAfter) {
                //저혈당
                strGlucoseType = HealthcareConstants.GlucoseState.GLUCOSE_LOW;
            } else if (dGlucose >= minAfter && dGlucose <= maxAfter) {
                //정상
                strGlucoseType = HealthcareConstants.GlucoseState.GLUCOSE_NORMAL;
            } else if (dGlucose > maxAfter) {
                //고혈당
                strGlucoseType = HealthcareConstants.GlucoseState.GLUCOSE_OVER;
            }
        }

        return strGlucoseType;
    }
}
