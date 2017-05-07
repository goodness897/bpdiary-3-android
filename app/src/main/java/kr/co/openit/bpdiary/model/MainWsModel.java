package kr.co.openit.bpdiary.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import kr.co.openit.bpdiary.BR;

/**
 * Created by srpark on 2017-01-11.
 */

public class MainWsModel extends BaseObservable {
    /**
     * 체중 값
     */
    private String weight = "";

    /**
     * 체중 값 kg
     */
    private String defaultWeight = "";
    /**
     * BMI 값
     */
    private String bmi = "";

    /**
     * 정상, 비만 등등 결과값
     */
    private String wsResult = "";

    /**
     * 날짜
     */
    private String date = "";

    /**
     * 정상, 비반 등등 타입 값 (1:저체중, 2:정상, 3:과체중, 4.비만, 5:고도비만)
     */
    private int wsType = 1;

    @Bindable
    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
        update();
    }

    @Bindable
    public String getBmi() {
        return bmi;
    }

    public void setBmi(String bmi) {
        this.bmi = bmi;
        update();
    }

    @Bindable
    public String getWsResult() {
        return wsResult;
    }

    public void setWsResult(String wsResult) {
        this.wsResult = wsResult;
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
    public int getWsType() {
        return wsType;
    }

    public void setWsType(int wsType) {
        this.wsType = wsType;
        update();
    }

    public String getDefaultWeight() {
        return defaultWeight;
    }

    public void setDefaultWeight(String defaultWeight) {
        this.defaultWeight = defaultWeight;
    }

    private void update() {
        notifyPropertyChanged(BR.mainWs);
    }

}
