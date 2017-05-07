package kr.co.openit.bpdiary.activity.weight.view;

import android.content.Context;
import android.widget.FrameLayout;
import android.widget.TextView;

import kr.co.openit.bpdiary.R;
import kr.co.openit.bpdiary.activity.weight.item.WeightChildItem;

/**
 * Created by goodn on 2016-12-24.
 */

public class WeightChildView extends FrameLayout {

    private TextView contentView;
    private WeightChildItem item;

    public WeightChildView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_noti_child, this);
        contentView = (TextView)findViewById(R.id.tv_child_content);
    }
    public void setChildItem(WeightChildItem item){
        this.item = item;
        contentView.setText(item.getChildContent());

    }
}
