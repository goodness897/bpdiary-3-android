package kr.co.openit.bpdiary.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import kr.co.openit.bpdiary.BR;


/**
 * Created by srpark on 2016-12-16.
 */

public class MainBpModel extends BaseObservable {

    private boolean btConnect = false;

    private boolean bleConnect = false;

    private String sys = "";

    private String dia = "";

    private String pulse = "";

    private String date = "";

    private String bpResult = "";

    private int bpType = 1;

    @Bindable
    public boolean isBtConnect() {
        return btConnect;
    }

    public void setBtConnect(boolean btConnect) {
        this.btConnect = btConnect;
        update();
    }

    @Bindable
    public boolean isBleConnect() {
        return bleConnect;
    }

    public void set651BtConnect(boolean bleConnect) {
        this.bleConnect = bleConnect;
        update();
    }

    @Bindable
    public String getBpResult() {
        return bpResult;
    }

    public void setBpResult(String bpResult) {
        this.bpResult = bpResult;
        update();
    }

    @Bindable
    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
        update();
    }

    @Bindable
    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
        update();
    }

    @Bindable
    public String getPulse() {
        return pulse;
    }

    public void setPulse(String pulse) {
        this.pulse = pulse;
        update();
    }

    @Bindable
    public int getBpType() {
        return bpType;
    }

    public void setBpType(int bpType) {
        this.bpType = bpType;
        update();
    }

    @Bindable
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        update();
    }

    private void update() {
        notifyPropertyChanged(BR.mainBp);
    }

}
