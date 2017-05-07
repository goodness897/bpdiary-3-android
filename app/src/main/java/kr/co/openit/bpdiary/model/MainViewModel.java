package kr.co.openit.bpdiary.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import kr.co.openit.bpdiary.BR;


/**
 * Created by srpark on 2016-12-16.
 */

public class MainViewModel extends BaseObservable {

    private int screenType = 1;

    private boolean useGlucose = false;

    @Bindable
    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
        update();
    }

    @Bindable
    public boolean isUseGlucose() {
        return useGlucose;
    }

    public void setUseGlucose(boolean useGlucose) {
        this.useGlucose = useGlucose;
        update();
    }

    private void update() {
        notifyPropertyChanged(BR.mainView);
    }

}
