package kr.co.openit.bpdiary.services;

import kr.co.openit.bpdiary.utils.ManagerUtil;

/**
 * 혈압 데이터 내보내기 양식
 */
public class CsvBeanBP {

    private String strSys;

    private String strDia;

    private String strPulse;

    private String strMessage;

    private String strRecordDt;

    /**
     * @return
     */
    public String getSys() {
        return strSys;
    }

    /**
     * @param strSys - the strSys to set
     */
    public void setSys(String strSys) {
        this.strSys = strSys;
    }

    /**
     * @return
     */
    public String getDia() {
        return strDia;
    }

    /**
     * @param strDia - the strDia to set
     */
    public void setDia(String strDia) {
        this.strDia = strDia;
    }

    /**
     * @return
     */
    public String getPulse() {
        return strPulse;
    }

    /**
     * @param strPulse - the strPulse to set
     */
    public void setPulse(String strPulse) {
        this.strPulse = strPulse;
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
        str.append("sys=").append(strSys).append(", ");
        str.append("dia=").append(strDia).append(", ");
        str.append("pulse=").append(strPulse).append(", ");
        str.append("message=").append(strMessage).append(", ");
        str.append("recordDt=").append(strRecordDt);

        return str.toString();
    }

    public String csvString() {

        StringBuilder str = new StringBuilder();
        str.append(strSys).append(",");
        str.append(strDia).append(",");
        str.append(strPulse).append(",");
        str.append(strMessage).append(",");

        if ("Date".equals(strRecordDt)) {
            str.append(strRecordDt);
        } else {
            str.append(ManagerUtil.convertDateFormatToServer(strRecordDt));
        }
        return str.toString();
    }
}
