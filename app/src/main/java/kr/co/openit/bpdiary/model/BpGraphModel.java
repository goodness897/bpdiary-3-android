package kr.co.openit.bpdiary.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import kr.co.openit.bpdiary.BR;


/**
 * Created by srpark on 2016-12-16.
 */

public class BpGraphModel extends BaseObservable {

    private boolean screenType = true;

    private int periodType = 0;

    private String memo = "";

    @Bindable
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
        update();
    }

    @Bindable
    public boolean isScreenType() {
        return screenType;
    }

    public void setScreenType(boolean screenType) {
        this.screenType = screenType;
        update();
    }

    @Bindable
    public int getPeriodType() {
        return periodType;
    }

    public void setPeriodType(int periodType) {
        this.periodType = periodType;
        update();
    }

    private void update() {
        notifyPropertyChanged(BR.bpGraph);
    }

}
