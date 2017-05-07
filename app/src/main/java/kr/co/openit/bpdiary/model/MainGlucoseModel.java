package kr.co.openit.bpdiary.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import kr.co.openit.bpdiary.BR;


/**
 * Created by srpark on 2016-12-16.
 */

public class MainGlucoseModel extends BaseObservable {

    private String glucose = "";

    private int glucoseType = 1;

    private String eatType = "B";

    private String glucoseResult;

    private String mealResult;

    private String date = "";

    @Bindable
    public String getGlucose() {
        return glucose;
    }

    public void setGlucose(String glucose) {
        this.glucose = glucose;
        update();
    }

    @Bindable
    public String getMealResult() {
        return mealResult;
    }

    public void setMealResult(String mealResult) {
        this.mealResult = mealResult;
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
    public int getGlucoseType() {
        return glucoseType;
    }

    public void setGlucoseType(int glucoseType) {
        this.glucoseType = glucoseType;
        update();
    }

    @Bindable
    public String getGlucoseResult() {
        return glucoseResult;
    }

    public void setGlucoseResult(String glucoseResult) {
        this.glucoseResult = glucoseResult;
        update();
    }

    @Bindable
    public String getEatType() {
        return eatType;
    }

    public void setEatType(String eatType) {
        this.eatType = eatType;
        update();
    }

    private void update() {
        notifyPropertyChanged(BR.mainGlucose);
    }

}
