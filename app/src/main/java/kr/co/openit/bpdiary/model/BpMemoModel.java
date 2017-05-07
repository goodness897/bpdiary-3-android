package kr.co.openit.bpdiary.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import kr.co.openit.bpdiary.BR;


/**
 * Created by srpark on 2016-12-16.
 */

public class BpMemoModel extends BaseObservable {
    private boolean isCreate = true;

    private boolean viewDelete = true;

    private String memo = "";

    private String date = "";

    private String bpResult = "";

    private int bpType = 1;

    private String armType = "";

    @Bindable
    public String getBpResult() {
        return bpResult;
    }

    public void setBpResult(String bpResult) {
        this.bpResult = bpResult;
        update();
    }

    @Bindable
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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

    @Bindable
    public String getArmType() {
        return armType;
    }

    public void setArmType(String armType) {
        this.armType = armType;
        update();
    }

    @Bindable
    public boolean getIsCreate() {
        return isCreate;
    }

    public void setIsCreate(boolean isCreate) {
        this.isCreate = isCreate;
        update();
    }

    @Bindable
    public boolean isViewDelete() {
        return viewDelete;
    }

    public void setViewDelete(boolean viewDelete) {
        this.viewDelete = viewDelete;
        update();
    }

    private void update() {
        notifyPropertyChanged(BR.mainBp);
    }

}
