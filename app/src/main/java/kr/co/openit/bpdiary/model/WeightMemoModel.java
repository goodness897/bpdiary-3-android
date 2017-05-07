package kr.co.openit.bpdiary.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import kr.co.openit.bpdiary.BR;


/**
 * Created by srpark on 2016-12-16.
 */

public class WeightMemoModel extends BaseObservable {
    private boolean isCreate = true;

    private boolean viewDelete = true;

    private String memo = "";

    private String date = "";

    private String weightResult = "";

    private int weightType = 1;

    @Bindable
    public String getWeightResult() {
        return weightResult;
    }

    public void setWeightResult(String weightResult) {
        this.weightResult = weightResult;
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
    public int getWeightType() {
        return weightType;
    }

    public void setWeightType(int weightType) {
        this.weightType = weightType;
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
