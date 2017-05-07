package kr.co.openit.bpdiary.activity.setting.item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by goodn on 2016-12-24.
 */

public class NotiGroupItem {

    private String title;

    private String date;

    private List<NotiChildItem> childItems = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<NotiChildItem> getChildItems() {
        return childItems;
    }

    public void setChildItems(List<NotiChildItem> childItems) {
        this.childItems = childItems;
    }
}
