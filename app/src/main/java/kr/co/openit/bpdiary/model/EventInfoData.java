package kr.co.openit.bpdiary.model;

import java.io.Serializable;

/**
 * Created by srpark on 2017-01-04.
 */

public class EventInfoData implements Serializable {

    private String seq;

    private String snooze;

    private String type;

    private String url;

    private String endDt;

    public EventInfoData() {

    }

    public EventInfoData(String seq, String snooze, String type, String url, String endDt) {
        this.seq = seq;
        this.snooze = snooze;
        this.type = type;
        this.url = url;
        this.endDt = endDt;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public String getSnooze() {
        return snooze;
    }

    public void setSnooze(String snooze) {
        this.snooze = snooze;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getEndDt() {
        return endDt;
    }

    public void setEndDt(String endDt) {
        this.endDt = endDt;
    }
}
