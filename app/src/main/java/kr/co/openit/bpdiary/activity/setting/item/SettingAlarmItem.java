package kr.co.openit.bpdiary.activity.setting.item;

/**
 * Created by hwangem on 2017-01-12.
 */

/**
 * 복약/측정 알람 데이터 모델
 */
public class SettingAlarmItem {

    private boolean status; // 토글버튼 ON/OFF

    private String seq; // 알림 아이디

    private String type; // 복약/측정 구분 플래그

    private String time; // 알림 시간(시간+분)

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
