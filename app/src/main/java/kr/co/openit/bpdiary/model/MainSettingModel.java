package kr.co.openit.bpdiary.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import kr.co.openit.bpdiary.BR;


/**
 * Created by srpark on 2016-12-16.
 */

public class MainSettingModel extends BaseObservable {

    private int screenType = 1;

    @Bindable
    public int getScreenType() {
        return screenType;
    }

    public void setScreenType(int screenType) {
        this.screenType = screenType;
        update();
    }

    private void update() {
        notifyPropertyChanged(BR.mainSetting);
    }

}
