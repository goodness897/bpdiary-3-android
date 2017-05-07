package kr.co.openit.bpdiary.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import kr.co.openit.bpdiary.BR;


/**
 * Created by srpark on 2016-12-16.
 */

public class MainReportModel extends BaseObservable {

    private boolean isData = false;

    @Bindable
    public boolean getIsData() {
        return isData;
    }

    public void setIsData(boolean isData) {
        this.isData = isData;
        update();
    }

    private void update() {
        notifyPropertyChanged(BR.mainGlucose);
    }

}
