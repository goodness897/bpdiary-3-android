package kr.co.openit.bpdiary.services;

import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * 혈당 데이터 내보내기 양식
 */
public class CsvBeanGlucose {

    /**
     * 혈당값
     */
    private String strGlucose;

    /**
     * 혈당 타입(식전/식후)
     */
    private String strMealType;

    /**
     * 혈당 메모
     */
    private String strMessage;

    /**
     * 혈당 시간
     */
    private String strRecordDt;

    public String getGlucose() {
        return strGlucose;
    }

    public void setGlucose(String strGlucose) {
        this.strGlucose = strGlucose;
    }

    public String getMealType() {
        return strMealType;
    }

    public void setMealType(String strMealType) {
        this.strMealType = strMealType;
    }

    public String getMessage() {
        return strMessage;
    }

    public void setMessage(String strMessage) {
        this.strMessage = strMessage;
    }

    public String getRecordDt() {
        return strRecordDt;
    }

    public void setRecordDt(String strRecordDt) {
        this.strRecordDt = strRecordDt;
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder();
        str.append("glucose=").append(strGlucose).append(", ");
        str.append("mealtype=").append(strMealType).append(", ");
        str.append("message=").append(strMessage).append(", ");
        str.append("recordDt=").append(strRecordDt);

        return str.toString();
    }

    public String csvString() {

        StringBuilder str = new StringBuilder();
        str.append(strGlucose).append(",");
        str.append(strMealType).append(",");
        str.append(strMessage).append(",");

        if ("Date".equals(strRecordDt)) {
            str.append(strRecordDt);
        } else {
            str.append(ManagerUtil.convertDateFormatToServer(strRecordDt));
        }
        return str.toString();
    }
}
