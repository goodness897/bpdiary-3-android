package kr.co.openit.bpdiary.services;

import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * 체중 데이터 내보내기 양식
 */
public class CsvBeanWS {

    private String strWeight;

    private String strMessage;

    private String strRecordDt;

    /**
     * @return
     */
    public String getWeight() {
        return strWeight;
    }

    /**
     * @param strWeight - the strWeight to set
     */
    public void setWeight(String strWeight) {
        this.strWeight = strWeight;
    }

    /**
     * @return
     */
    public String getMessage() {
        return strMessage;
    }

    /**
     * @param strMessage - the strMessage to set
     */
    public void setMessage(String strMessage) {
        this.strMessage = strMessage;
    }

    /**
     * @return
     */
    public String getRecordDt() {
        return strRecordDt;
    }

    /**
     * @param strRecordDt - the strRecordDt to set
     */
    public void setRecordDt(String strRecordDt) {
        this.strRecordDt = strRecordDt;
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder();
        str.append("weight=").append(strWeight).append(", ");
        str.append("message=").append(strMessage).append(", ");
        str.append("recordDt=").append(strRecordDt);

        return str.toString();
    }

    public String csvString() {

        StringBuilder str = new StringBuilder();
        str.append(strWeight).append(",");
        str.append(strMessage).append(",");
        if ("Date".equals(strRecordDt)) {
            str.append(strRecordDt);
        } else {
            str.append(ManagerUtil.convertDateFormatToServer(strRecordDt));
        }

        return str.toString();
    }
}
