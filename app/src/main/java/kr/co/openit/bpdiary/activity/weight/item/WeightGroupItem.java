package kr.co.openit.bpdiary.activity.weight.item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goodn on 2016-12-24.
 */

public class WeightGroupItem {

    private String title;

    private String strWeight;

    private String strBmi;

    private String strDate;

    private String strTime;

    private List<WeightChildItem> childItems = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStrWeight() {
        return strWeight;
    }

    public void setStrWeight(String strWeight) {
        this.strWeight = strWeight;
    }

    public String getStrBmi() {
        return strBmi;
    }

    public void setStrBmi(String strBmi) {
        this.strBmi = strBmi;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }

    public List<WeightChildItem> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<WeightChildItem> childItems) {
        this.childItems = childItems;
    }
}